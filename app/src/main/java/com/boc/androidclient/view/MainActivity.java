package com.boc.androidclient.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.boc.androidclient.R;
import com.boc.androidclient.network.BocAccountsService;
import com.boc.androidclient.network.BocAuthorizationService;
import com.boc.androidclient.network.BocSubscriptionService;
import com.boc.androidclient.utils.Utilities;
import com.boc.client.api.ApiConfiguration;
import com.boc.client.model.AccessTokenResponse;
import com.boc.client.model.Account;
import com.boc.client.model.CreateSubscriptionResponse;
import com.boc.client.model.SubscriptionView;
import com.boc.client.model.UpdateSubscriptionResponse;
import com.testfairy.TestFairy;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements ApiConfiguration {

    private final String LOGTAG = this.getClass().getName();
    private Button btnCreateSub;
    private Button btnPatch;
    private Button btnPay;
    private Button btnAccounts;
    private ProgressBar spinner;

    private CompositeDisposable disposable = new CompositeDisposable();
    private BocSubscriptionService mSubscriptionService = new BocSubscriptionService();
    private BocAuthorizationService mAuthorizationSerivce = new BocAuthorizationService();
    private BocAccountsService mAccountsService = new BocAccountsService();
    private Utilities utils = new Utilities();


    private boolean isSubscriptionCreated = false;
    private boolean isSubscriptionUpdated = false;

    private String subscriptionId;
    private String url = "";
    private String authCode;
    private String token2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TestFairy.begin(this, "SDK-2JiBv9kE");
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.progressbar_boc);
        spinner.setVisibility(View.GONE);

        btnCreateSub = findViewById(R.id.btn_createsub);
        btnPatch = findViewById(R.id.btn_patch);
        btnPay = findViewById(R.id.btn_pay);
        btnAccounts = findViewById(R.id.btn_accounts);

        // Change flags and disable/enable buttons to make the user flow more straightforward
        if(!isSubscriptionCreated && !isSubscriptionUpdated){
            disableButton(btnPatch);
            disableButton(btnPay);
            disableButton(btnAccounts);
        }

        // Change flags and disable/enable buttons to make the user flow more straightforward
        if(getIntent().getData()!=null){
            authCode = getIntent().getData().getQueryParameter("code");
            if(authCode!=null) {
                isSubscriptionCreated = true;
                enableButton(btnPatch);
                disableButton(btnPay);
                disableButton(btnAccounts);
            }
        }


        btnCreateSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if network is available and call the API to start the subscription creation process
                if(utils.isNetworkAvailable(getApplicationContext())){
                    createSubscription();
                }
                else{
                    Toast.makeText(getApplicationContext(),
                            "No network available, please connect!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });


        btnPatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get subscription ID from application shared preferences
                String subId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("SUB_ID", "NA");
                // Check if network is available and call the API to patch the retrieved subscription ID
                if(utils.isNetworkAvailable(getApplicationContext())){
                    spinner.setVisibility(View.VISIBLE);
                    patchSubscription(authCode,subId);
                }
                else{
                    Toast.makeText(getApplicationContext(),
                            "No network available, please connect!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });


        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if network is available and start the Payments Activity
                if(utils.isNetworkAvailable(getApplicationContext())){
                    Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(),
                            "No network available, please connect!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });


        btnAccounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get subscription ID from application shared preferences
                String subId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("SUB_ID", "NA");
                // Check if network is available and call the API to get the available accounts from the retrieved subscriptionID and start the Accounts Activity
                if(utils.isNetworkAvailable(getApplicationContext())){
                    spinner.setVisibility(View.VISIBLE);
                    getAccounts(subId);
                }
                else{
                    Toast.makeText(getApplicationContext(),
                            "No network available, please connect!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     * Patch the subscription ID with the authorization code retrieved from the user's login to 1bank environment by calling the BOC APIs (RxJava call).
     * This function chains the following API calls:
     * 1. Get Access Token by calling the Authorization API. (The auth code is retrieved when the user is redirected back to the app from 1bank environment)
     * 2. Get the updated Subscription ID info after the user has given consent
     * 3. Patch the subscription ID with the unique token
     * @param code
     * @param subId
     */
    private void patchSubscription(String code, final String subId){
        disposable.add(
                mAuthorizationSerivce
                        // Async call to BOC Java SDK library
                        .getAccessToken2(code)
                        // Async call to BOC Java SDK library. Get the response from getAccessToken2 and pass it down the chain
                        .flatMap(new Function<AccessTokenResponse, SingleSource<List<SubscriptionView>>>() {
                            @Override
                            public SingleSource<List<SubscriptionView>> apply(@NonNull AccessTokenResponse accessTokenResponse) throws Exception {
                                token2 = accessTokenResponse.getAccessToken();
                                return mSubscriptionService.getSubscriptionIdInfo(subId);
                            }
                        })
                        // Async call to BOC Java SDK library. Get the response from getSubscriptionID info, as well as the accessToken, and patch the subscription
                        .flatMap(new Function<List<SubscriptionView>, SingleSource<UpdateSubscriptionResponse>>() {
                            @Override
                            public SingleSource<UpdateSubscriptionResponse> apply(List<SubscriptionView> subscriptionViewList) throws Exception {
                                return mSubscriptionService.patchSubscriptionIdInfo(subscriptionViewList,subId,token2);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UpdateSubscriptionResponse>() {
                            @Override
                            public void onSuccess(UpdateSubscriptionResponse response) {
                                Log.e(LOGTAG, " ------------- Patch Subscription success ---------------");
                                Log.e(LOGTAG,  response.toString());

                                // Change flags and disable/enable buttons to make the user flow more straightforward
                                spinner.setVisibility(View.GONE);
                                isSubscriptionUpdated = true;
                                disableButton(btnPatch);
                                enableButton(btnAccounts);
                                enableButton(btnPay);
                                // Storing user API Key in preferences
                                Toast.makeText(getApplicationContext(),
                                        "Patch Subscription successful!! ",
                                        Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onError(Throwable e) {
                                spinner.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "An error has occured. please check your connection", Toast.LENGTH_LONG).show();
                                Log.e(LOGTAG, "onError: " + e.getMessage());
                            }
                        }));
    }

    /**
     * Create a new subscription by calling the BOC APIs (RxJava call).
     * Once the Subscription ID is retrieved, open the browser so the user can log in to 1bank environment
     */
    private void createSubscription() {
        disposable.add(
                mSubscriptionService
                        // Async call to BOC Java SDK library
                        .createSubscription()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<CreateSubscriptionResponse>() {
                            @Override
                            public void onSuccess(CreateSubscriptionResponse response) {
                                Log.e(LOGTAG, " ------------- Create Subscription success ---------------");
                                Log.e(LOGTAG,  response.getSubscriptionId());
                                // Storing user API Key in preferences
                                Toast.makeText(getApplicationContext(),
                                        "SubId: " + response.getSubscriptionId(),
                                        Toast.LENGTH_LONG).show();

                                // Get subscription ID from API response and save to application shared preferences
                                subscriptionId = response.getSubscriptionId();
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("SUB_ID", response.getSubscriptionId()).apply();

                                // Open the browser and direct the user to the 1bank environment for login
                                url = "https://sandbox-apis.bankofcyprus.com/df-boc-org-sb/sb/psd2/oauth2/authorize?response_type=code&redirect_uri=https://bocandroid.com/callback&scope=UserOAuth2Security&client_id=7c7bcb8f-7930-495d-adc2-e69f1afb07da&subscriptionid="+subscriptionId;
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                startActivity(browserIntent);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(getApplicationContext(), "An error has occured. please check your connection", Toast.LENGTH_LONG).show();
                                Log.e(LOGTAG, "onError: " + e.getMessage());
                            }
                        }));
    }



    /**
     * Get available accounts for a subscription ID by calling the BOC APIs (RxJava call).
     */
    private void getAccounts(final String subId) {
        disposable.add(
                mAccountsService
                        // Async call to BOC Java SDK library
                        .getAccounts(subId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Account>>() {
                            @Override
                            public void onSuccess(List<Account> response) {
                                Log.e(LOGTAG, " ------------- Payment success ---------------");
                                Log.e(LOGTAG, response.toString());
                                spinner.setVisibility(View.GONE);

                                // Convert the API response to ArrayList<String>
                                ArrayList<String> accountIdList = new ArrayList<>();
                                for (Account a: response
                                ) {
                                    accountIdList.add(a.getAccountId());
                                }

                                // Start the Accounts Activity
                                Intent intent = new Intent(getApplicationContext(), AccountActivity.class);
                                intent.putStringArrayListExtra("list", accountIdList);
                                startActivity(intent);
                            }

                            @Override
                            public void onError(Throwable e) {
                                spinner.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "An error has occured. please check your connection", Toast.LENGTH_LONG).show();
                                Log.e(LOGTAG, "onError: " + e.getMessage());
                            }
                        }));
    }

    private void enableButton(Button btn){
        btn.setEnabled(true);
        btn.setAlpha(1.0f);
    }

    private void disableButton(Button btn){
        btn.setEnabled(false);
        btn.setAlpha(.3f);
    }





}

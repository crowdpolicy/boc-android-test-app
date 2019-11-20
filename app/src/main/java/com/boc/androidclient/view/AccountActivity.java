package com.boc.androidclient.view;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.boc.androidclient.R;
import com.boc.androidclient.network.BocAccountsService;
import com.boc.androidclient.utils.Utilities;
import com.boc.client.model.Account;
import com.boc.client.model.Statement;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;



/**
 * Display list of available accounts (retrieved from Subscription ID) after user clicks on 'Get Accounts' of Main Activity
 * Starts AccountDetailsActivity if user clicks on a specific account ID from the list
 */
public class AccountActivity extends AppCompatActivity {

    private final String LOGTAG = this.getClass().getName();
    private Utilities utils = new Utilities();

    private CompositeDisposable disposable = new CompositeDisposable();
    private BocAccountsService mAccountsService = new BocAccountsService();

    private ProgressBar spinner;

    private ListView listView;
    private TextView textView;
    private ArrayList<String> accountListString;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activity);

        listView = findViewById(R.id.listView);
        textView = findViewById(R.id.list_text);
        // Get list of account IDs from Intent of Main Activity
        accountListString = getIntent().getStringArrayListExtra("list");

        spinner = findViewById(R.id.progressbar_boc);
        spinner.setVisibility(View.GONE);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.list_item,  accountListString);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Get subscription ID from application shared preferences
                final String subId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("SUB_ID", "NA");
                final String accId=adapter.getItem(position);

                // Check if network is available and call the API to get details of an account
                if(utils.isNetworkAvailable(getApplicationContext())){
                    // Set loading spinner to active until we get response
                    spinner.setVisibility(View.VISIBLE);
                    //TODO
                    //getAccountDetails(accId, subId);

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(AccountActivity.this);
                    builder1.setMessage("Select action");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Statements",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    getAccountStatement(accId,subId, "01/01/2016", "31/12/2018", 10);
                                }
                            });

                    builder1.setNegativeButton(
                            "Details",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    getAccountDetails(accId, subId);
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
                else{
                    Toast.makeText(getApplicationContext(),
                            "No network available, please connect !",
                            Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    /**
     * BOC API call to get Account Details (RxJava call)
     */
    private void getAccountDetails(final String accId, final String subId) {
        disposable.add(
                mAccountsService
                        // Async call to BOC Java SDK library
                        .getAccountDetails(accId, subId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Account>>() {
                            @Override
                            public void onSuccess(List<Account> response) {
                                // For debugging purposes
                                Log.e(LOGTAG, response.toString());

                                // Set loading spinner to invisible since we got success
                                spinner.setVisibility(View.GONE);

                                // Start AccounDetailsActivity
                                Intent intent = new Intent(getApplicationContext(), AccountDetailsActivity.class);
                                intent.putStringArrayListExtra("details", accountResponseToArrayList(response));
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


    /**
     * BOC API call to get Account Statements (RxJava call)
     */
    private void getAccountStatement(final String accId, final String subId, final String startDate, final String endDate, final int maxCount) {
        disposable.add(
                mAccountsService
                        // Async call to BOC Java SDK library
                        .getAccountStatement(accId, subId, startDate, endDate, maxCount)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Statement>() {
                            @Override
                            public void onSuccess(Statement response) {
                                // For debugging purposes
                                Log.e(LOGTAG, response.toString());

                                // Set loading spinner to invisible since we got success
                                spinner.setVisibility(View.GONE);

                                // Convert Array of transactions to String in order to pass to next activity
                                String arrayAsString = new Gson().toJson(response.getTransaction());

                                // Start AccounDetailsActivity
                                Intent intent = new Intent(getApplicationContext(), StatementActivity.class);
                                intent.putExtra("transactionArray", arrayAsString);
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


    /**
     * Convert the API response to ArrayList<String> that can be passed to AccountDetailsActivity for display
     * @param response
     * @return
     */
    private ArrayList<String> accountResponseToArrayList(List<Account> response){
        ArrayList<String> responseArray = new ArrayList<>();
        responseArray.add(response.get(0).getBankId());
        responseArray.add(response.get(0).getAccountId());
        responseArray.add(response.get(0).getAccountName());
        responseArray.add(response.get(0).getAccountType());
        responseArray.add(response.get(0).getIBAN());
        responseArray.add(response.get(0).getCurrency());
        responseArray.add(response.get(0).getInfoTimeStamp());
        responseArray.add(response.get(0).getInterestRate().toString());
        responseArray.add(response.get(0).getMaturityDate());
        responseArray.add(response.get(0).getLastPaymentDate());
        responseArray.add(response.get(0).getNextPaymentDate());
        responseArray.add(response.get(0).getRemainingInstallments().toString());
        responseArray.add(response.get(0).getBalances().get(0).getAmount().toString());
        responseArray.add(response.get(0).getBalances().get(1).getAmount().toString());
        return responseArray;
    }




}

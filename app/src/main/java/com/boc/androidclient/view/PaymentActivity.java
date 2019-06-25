package com.boc.androidclient.view;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.boc.androidclient.R;
import com.boc.androidclient.network.BocPaymentsService;
import com.boc.androidclient.utils.Utilities;
import com.boc.client.model.Amount;
import com.boc.client.model.CreatePaymentResponse;
import com.boc.client.model.Creditor;
import com.boc.client.model.Debtor;
import com.boc.client.model.SignPaymentRequest;
import com.boc.client.model.SignPaymentResponse;
import com.boc.client.model.Status;

import java.math.BigDecimal;
import java.util.ArrayList;

import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Create a new payment request payload from user input, after user clicks 'Create payment' from Main Activity
 * Starts PaymentDetailsActivity if user clicks on 'Execute Payment'
 */
public class PaymentActivity extends AppCompatActivity {

    private final String LOGTAG = this.getClass().getName();
    private Utilities utils = new Utilities();
    private Button btnPay;

    private ProgressBar spinner;

    private CompositeDisposable disposable = new CompositeDisposable();
    private BocPaymentsService mPaymentsService = new BocPaymentsService();

    private EditText inputAmount;
    private EditText inputCurrency;
    private EditText inputDebtor;
    private EditText inputCreditor;
    private EditText inputDetails;
    private EditText inputTerminal;
    private EditText inputCurrencyRate;

    private ArrayList<String> paymentDetailsArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        spinner = findViewById(R.id.progressbar_boc);
        spinner.setVisibility(View.GONE);

        inputAmount = findViewById(R.id.edit_amount);
        inputCreditor = findViewById(R.id.edit_creditor);
        inputDebtor = findViewById(R.id.edit_debtor);
        inputCurrency = findViewById(R.id.edit_currency);
        inputDetails = findViewById(R.id.edit_details);
        inputTerminal = findViewById(R.id.edit_terminal);
        inputCurrencyRate = findViewById(R.id.edit_currency_rate);


        btnPay = findViewById(R.id.btn_execute_pay);
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get subscription ID from application shared preferences
                String subId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("SUB_ID", "NA");

                // Check if network is available and call the API to start the payment creation process
                if(utils.isNetworkAvailable(getApplicationContext())){
                    // Set loading spinner to active until we get response
                    spinner.setVisibility(View.VISIBLE);
                    payment(subId, createPaymentFromInput());
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
     * Execute a single payment by calling the BoC APIs. (RxJava call)
     * This function chains the following API calls:
     * 1. Sign Payment (create a JWS from the payment body the user has provided)
     * 2. Create Payment (pass the SignPaymentResponse as a body to createPayment call)
     * 3. Approve Payment
     */
    private void payment(final String subId, SignPaymentRequest request){
        disposable.add(
                mPaymentsService
                        // Async call to BOC Java SDK library
                        .signPayment(request)
                        // Async call to BOC Java SDK library. Get the response from signPayment and use it as input for createPayment
                        .flatMap(new Function<SignPaymentResponse, SingleSource<CreatePaymentResponse>>() {
                            @Override
                            public SingleSource<CreatePaymentResponse> apply(SignPaymentResponse signPaymentResponse) throws Exception {
                                return mPaymentsService.createPayment(signPaymentResponse,subId);
                            }
                        })
                        // Async call to BOC Java SDK library. Get the response from createPayment and use it as input for approvePayment
                        .flatMap(new Function<CreatePaymentResponse, SingleSource<Status>>() {
                            @Override
                            public SingleSource<Status> apply(CreatePaymentResponse createPaymentResponse) throws Exception {
                                // Save the payment details to an Activity variable to pass later on PaymentDetailsActivity
                                paymentDetailsArray = responseToArrayList(createPaymentResponse);
                                return mPaymentsService.authorizePayment(createPaymentResponse.getPayment().getPaymentId(),subId);
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Status>(){
                            @Override
                            public void onSuccess(Status response) {
                                Log.e(LOGTAG, " ------------- Payment success ---------------");
                                Log.e(LOGTAG,  response.toString());

                                // Set loading spinner to invisible since we got success
                                spinner.setVisibility(View.GONE);

                                // Start PaymentDetailsActivity
                                Intent intent = new Intent(getApplicationContext(), PaymentDetailsActivity.class);
                                intent.putStringArrayListExtra("details",
                                        appendStatusToPaymentDetails(paymentDetailsArray,response));
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
     * Create a SignPayment request body with mock data
     * @return
     */
    private SignPaymentRequest createDummyPayment(){

        SignPaymentRequest requestBody = new SignPaymentRequest();

        requestBody.setCreditor(new Creditor().accountId("351012345673"));
        requestBody.setDebtor(new Debtor().accountId("351012345671"));
        requestBody.setTransactionAmount(new Amount().amount(new BigDecimal(4)).currency("EUR").currencyRate(""));
        requestBody.setEndToEndId("");
        requestBody.setPaymentDetails("test sandbox ");
        requestBody.setTerminalId("string");
        requestBody.setExecutionDate("");
        requestBody.setValueDate("");

        return requestBody;
    }

    /**
     * Create a SignPayment request body from user input
     * @return
     */
    private SignPaymentRequest createPaymentFromInput(){

        SignPaymentRequest requestBody = new SignPaymentRequest();

        requestBody.setCreditor(new Creditor().accountId(inputCreditor.getText().toString()));
        requestBody.setDebtor(new Debtor().accountId(inputDebtor.getText().toString()));
        requestBody.setTransactionAmount(new Amount().amount(new BigDecimal(inputAmount.getText().toString()))
                .currency(inputCurrency.getText().toString())
                .currencyRate(inputCurrencyRate.getText().toString()));
        requestBody.setEndToEndId("");
        requestBody.setPaymentDetails(inputDetails.getText().toString());
        requestBody.setTerminalId(inputTerminal.getText().toString());
        requestBody.setExecutionDate("");
        requestBody.setValueDate("");

        return requestBody;
    }


    /**
     * Convert CreatePaymentResponse to ArrayList<String> that can be passed to PaymentDetailsActivity for display
     * @param response
     * @return
     */
    private ArrayList<String> responseToArrayList(CreatePaymentResponse response){
        ArrayList<String> responseArray = new ArrayList<>();
        responseArray.add(response.getPayment().getPaymentId());
        responseArray.add(response.getPayment().getTransactionTime());
        responseArray.add(response.getPayment().getDebtor().getAccountId());
        responseArray.add(response.getPayment().getCreditor().getAccountId());
        responseArray.add(response.getPayment().getTransactionAmount().getCurrency());
        responseArray.add(response.getPayment().getTransactionAmount().getAmount().toString());
        responseArray.add(response.getPayment().getTotalCharges());
        responseArray.add(response.getPayment().getPaymentDetails());
        responseArray.add(response.getPayment().getExecutionDate());
        responseArray.add(response.getPayment().getValueDate());

        return responseArray;
    }

    /**
     * Append the status data from ApprovePayment response to the ArrayList<String> object that we will pass to PaymentDetailsActivity
     * @param array
     * @param response
     * @return
     */
    private ArrayList<String> appendStatusToPaymentDetails(ArrayList<String> array, Status response){
        ArrayList<String> responseArray = new ArrayList<>();
        responseArray.add(response.getDescription().get(0));
        responseArray.add(response.getCode());
        responseArray.add(response.getRefNumber());
        responseArray.add(array.get(0));
        responseArray.add(array.get(1));
        responseArray.add(array.get(2));
        responseArray.add(array.get(3));
        responseArray.add(array.get(4));
        responseArray.add(array.get(5));
        responseArray.add(array.get(6));
        responseArray.add(array.get(7));
        responseArray.add(array.get(8));
        responseArray.add(array.get(9));
        return responseArray;
    }

}

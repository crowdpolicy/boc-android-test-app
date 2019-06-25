package com.boc.androidclient.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.boc.androidclient.R;

import java.util.ArrayList;

/**
 * Display the details of a successful payment in a simple Linear View
 */
public class PaymentDetailsActivity extends AppCompatActivity {

    private TextView description;
    private TextView code;
    private TextView refnumber;
    private TextView paymentid;
    private TextView transactiontime;
    private TextView debtorid;
    private TextView creditorid;
    private TextView currency;
    private TextView amount;
    private TextView charges;
    private TextView paymentdetails;
    private TextView execdate;
    private TextView valdate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        // Get the payment details from the Intent of Payment activity
        ArrayList<String> details = getIntent().getStringArrayListExtra("details");

        description = findViewById(R.id.val_description);
        code = findViewById(R.id.val_code);
        refnumber = findViewById(R.id.val_refnumber);
        paymentid = findViewById(R.id.val_paymentid);
        transactiontime = findViewById(R.id.val_transactiontime);
        debtorid = findViewById(R.id.val_debtor);
        creditorid = findViewById(R.id.val_creditor);
        currency = findViewById(R.id.val_paycurrency);
        amount = findViewById(R.id.val_amount);
        charges = findViewById(R.id.val_charges);
        paymentdetails = findViewById(R.id.val_details);
        execdate = findViewById(R.id.val_pay_execdate);
        valdate = findViewById(R.id.val_pay_valdate);

        description.setText(details.get(0));
        code.setText(details.get(1));
        refnumber.setText(details.get(2));
        paymentid.setText(details.get(3));
        transactiontime.setText(details.get(4));
        debtorid.setText(details.get(5));
        creditorid.setText(details.get(6));
        currency.setText(details.get(7));
        amount.setText(details.get(8));
        charges.setText(details.get(9));
        paymentdetails.setText(details.get(10));
        execdate.setText(details.get(11));
        valdate.setText(details.get(12));
    }

}

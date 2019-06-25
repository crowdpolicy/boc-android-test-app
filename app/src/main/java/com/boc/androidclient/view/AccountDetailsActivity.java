package com.boc.androidclient.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.boc.androidclient.R;

import java.util.ArrayList;

/**
 * Display the details of an account after call to API is successful in a simple Linear View.
 */
public class AccountDetailsActivity extends AppCompatActivity {

    private TextView bankid;
    private TextView accountid;
    private TextView account_name;
    private TextView account_type;
    private TextView iban;
    private TextView cuurency;
    private TextView infotimestamp;
    private TextView interestrate;
    private TextView maturitydate;
    private TextView lastdate;
    private TextView nextdate;
    private TextView installments;
    private TextView currbalance;
    private TextView availbalance;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        // Get the account details from the Intent of Account Activity
        ArrayList<String> details = getIntent().getStringArrayListExtra("details");

        bankid = findViewById(R.id.val_bankid);
        accountid = findViewById(R.id.val_accountid);
        account_name = findViewById(R.id.val_name);
        account_type = findViewById(R.id.val_type);
        iban = findViewById(R.id.val_iban);
        cuurency = findViewById(R.id.val_acccurrency);
        infotimestamp = findViewById(R.id.val_infotimestamp);
        interestrate = findViewById(R.id.val_interest_rate);
        maturitydate = findViewById(R.id.val_maturitydate);
        lastdate = findViewById(R.id.val_lastdate);
        nextdate = findViewById(R.id.val_nextdate);
        installments = findViewById(R.id.val_installments);
        availbalance = findViewById(R.id.val_availbalance);
        currbalance = findViewById(R.id.val_currbalance);

        bankid.setText(details.get(0));
        accountid.setText(details.get(1));
        account_name.setText(details.get(2));
        account_type.setText(details.get(3));
        iban.setText(details.get(4));
        cuurency.setText(details.get(5));
        infotimestamp.setText(details.get(6));
        interestrate.setText(details.get(7));
        maturitydate.setText(details.get(8));
        lastdate.setText(details.get(9));
        nextdate.setText(details.get(10));
        installments.setText(details.get(11));
        currbalance.setText(details.get(12));
        availbalance.setText(details.get(13));
    }

}

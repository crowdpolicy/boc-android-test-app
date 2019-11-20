package com.boc.androidclient.view;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.boc.androidclient.R;
import com.boc.androidclient.utils.Utilities;
import com.boc.client.model.Transaction;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;


/**
 * Display statements of the selected account
 */
public class StatementActivity extends AppCompatActivity {


    private final String LOGTAG = this.getClass().getName();
    private Utilities utils = new Utilities();

    private TextView transId;
    private TextView transDcind;
    private TextView transAmount;
    private TextView transCurrency;
    private TextView transDesc;
    private TextView transPostingdate;
    private TextView transValuedate;
    private TextView transType;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);

        String statementAsString = getIntent().getStringExtra("transactionArray");
        Type collectionType = new TypeToken<List<Transaction>>(){}.getType();
        List<Transaction> list = new Gson()
                .fromJson( statementAsString , collectionType);

        //TODO recyclerview, only displays the first transaction
        transId = findViewById(R.id.trans_id);
        transId.setText("TransactionID: "+ list.get(0).getId());

        transDcind = findViewById(R.id.trans_dcind);
        transDcind.setText("DcInd: "+ list.get(0).getDcInd());

        transAmount = findViewById(R.id.trans_amount);
        transAmount.setText("Amount: "+ list.get(0).getTransactionAmount().getAmount().toString());

        transCurrency = findViewById(R.id.trans_currency);
        transCurrency.setText("Currency: "+list.get(0).getTransactionAmount().getCurrency());

        transDesc = findViewById(R.id.trans_description);
        transDesc.setText("Description: "+list.get(0).getDescription());

        transPostingdate = findViewById(R.id.trans_postingdate);
        transPostingdate.setText("PostingDate: "+list.get(0).getPostingDate());

        transValuedate = findViewById(R.id.trans_valuedate);
        transValuedate.setText("ValueDate: "+list.get(0).getValueDate());

        transType = findViewById(R.id.trans_type);
        transType.setText("Type: "+list.get(0).getTransactionType());

    }

}



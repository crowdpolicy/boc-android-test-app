package com.boc.androidclient.view;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.boc.androidclient.R;
import com.boc.androidclient.utils.Utilities;


/**
 * Display statements of the selected account
 */
public class StatementActivity extends AppCompatActivity {


    private final String LOGTAG = this.getClass().getName();
    private Utilities utils = new Utilities();

    private TextView statement;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statement);

        String statementAsString = getIntent().getStringExtra("transactionArray");
        statement = findViewById(R.id.statement_list);

        statement.setText(statementAsString);

    }

}



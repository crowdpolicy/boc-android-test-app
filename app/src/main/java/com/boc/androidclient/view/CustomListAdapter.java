package com.boc.androidclient.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.boc.androidclient.R;
import com.boc.client.model.Transaction;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter<Transaction> implements View.OnClickListener{

    private ArrayList<Transaction> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
         TextView transId;
         TextView transDcind;
         TextView transAmount;
         TextView transCurrency;
         TextView transDesc;
         TextView transPostingdate;
         TextView transValuedate;
         TextView transType;
    }

    public CustomListAdapter(ArrayList<Transaction> data, Context context) {
        super(context,
                R.layout.transaction_item, data);
        this.dataSet = data;
        this.mContext=context;

    }


    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Transaction dataModel=(Transaction)object;

        switch (v.getId())
        {
//            case R.id.item_info:
//                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
//                break;
        }
    }


    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Transaction dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.transaction_item, parent, false);
            viewHolder.transId = (TextView) convertView.findViewById(R.id.trans_id);
            viewHolder.transDcind = (TextView) convertView.findViewById(R.id.trans_dcind);
            viewHolder.transAmount = (TextView) convertView.findViewById(R.id.trans_amount);
            viewHolder.transCurrency = (TextView) convertView.findViewById(R.id.trans_currency);
            viewHolder.transDesc = (TextView) convertView.findViewById(R.id.trans_description);
            viewHolder.transPostingdate = (TextView) convertView.findViewById(R.id.trans_postingdate);
            viewHolder.transValuedate = (TextView) convertView.findViewById(R.id.trans_valuedate);
            viewHolder.transType = (TextView) convertView.findViewById(R.id.trans_type);


            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }


        lastPosition = position;

        viewHolder.transId.setText("TransactionID: "+ dataModel.getId());
        viewHolder.transDcind.setText("DcInd: "+ dataModel.getDcInd());
        viewHolder.transAmount.setText("Amount: "+ dataModel.getTransactionAmount().getAmount().toString());
        viewHolder.transCurrency.setText("Currency: "+dataModel.getTransactionAmount().getCurrency());
        viewHolder.transDesc.setText("Description: "+dataModel.getDescription());
        viewHolder.transPostingdate.setText("PostingDate: "+dataModel.getPostingDate());
        viewHolder.transValuedate.setText("ValueDate: "+dataModel.getValueDate());
        viewHolder.transType.setText("Type: "+dataModel.getTransactionType());

        // Return the completed view to render on screen
        return convertView;
    }
}
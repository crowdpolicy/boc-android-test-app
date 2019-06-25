package com.boc.androidclient.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;




public class Utilities {

    private final String LOGTAG = this.getClass().getName();

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public boolean isInternetAvailable() {
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            Log.e(LOGTAG,"Network error!!!! : " + e.getStackTrace());
        }
        return false;
    }
}

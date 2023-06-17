package com.rakibofc.imageplaceholder.receiver;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class ConnectionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String actionOfIntent = intent.getAction();
        boolean isConnected = checkForInternet(context);

        if (actionOfIntent.equals(CONNECTIVITY_ACTION)) {

            // Get connection status
            String connStatus = isConnected ? "Connection restored" : "No Connection";

            // Toast the connection status
            Toast.makeText(context, connStatus, Toast.LENGTH_SHORT).show();
        }
    }

    // Self explanatory method
    public boolean checkForInternet(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}

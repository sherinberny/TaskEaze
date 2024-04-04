package com.ads.taskeaze.utils;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Check device's network connectivity and speed
 * @author emil http://stackoverflow.com/users/220710/emil
 *
 */
public class NetworkUtils {


    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public static boolean isConnected(Context context) {
        NetworkInfo info = NetworkUtils.getNetworkInfo(context);
        return (info != null && info.isConnected());
    }



    public static String getConnectionType(Context context) {

        NetworkInfo info = NetworkUtils.getNetworkInfo(context);
        if (info != null) {
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return "W";
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                return "M";
            } else {
                return "NA";
            }
        } else {
            return "NA";
        }

    }


    public static boolean checkInternetAndOpenDialog(Context context, AppCompatActivity appCompatActivity, View view) {
        if (isConnected(context)) {
            return true;
        } else {
            CommonFunc.commonDialog(context, "No internet", "Please check network settings", true,
                    appCompatActivity, view);
            return false;
        }
    }

}
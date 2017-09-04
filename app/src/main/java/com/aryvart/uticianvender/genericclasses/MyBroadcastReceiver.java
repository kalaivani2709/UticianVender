package com.aryvart.uticianvender.genericclasses;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.aryvart.uticianvender.provider.Provider_DashBoard;



/**
 * Created by android2 on 2/9/16.
 */
public class MyBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = netInfo != null && netInfo.isConnectedOrConnecting();
            context = GeneralData.context;


            if (!isConnected) {



                GeneralData.showAlertDialogNet(context, "Unable to connect with Internet. Please check your internet connection and try again");
            }

            else {

                try {
                    if(GeneralData.context instanceof Provider_DashBoard) {
                        Class c2 = Class.forName(GeneralData.context.getClass().getName().toString());
                        Intent i = new Intent(GeneralData.context, c2);
                        GeneralData.context.startActivity(i);
                        ((Activity) GeneralData.context).finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }




}
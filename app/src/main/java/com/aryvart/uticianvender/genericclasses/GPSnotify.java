package com.aryvart.uticianvender.genericclasses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by android3 on 4/6/16.
 */
public class GPSnotify extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent){} /*{
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            Toast.makeText(context, "in android.location.PROVIDERS_CHANGED",
                    Toast.LENGTH_SHORT).show();
            Intent pushIntent = new Intent(context, GPSTracker.class);
            context.startService(pushIntent);
            if (context instanceof User_Category) {
                //Log.i("KV", "Context Called........");
               // new User_Category().getCurrentLocation();
            }
          //  Log.i("KV", "Context Not Called........");
          //  new User_Category().getCurrentLocation();
        }
    }*/

}

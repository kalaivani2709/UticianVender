package com.aryvart.uticianvender.newGCM;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    String PROJECT_NUMBER = "311648921952";
    static Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_main);

        try {
            ctx = this;

            GCMClientManager pushClientManager = new GCMClientManager(this, PROJECT_NUMBER);
            pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
                @Override
                public void onSuccess(String registrationId, boolean isNewRegistration) {

                    Log.d("Registration id", registrationId);
                    //send this registrationId to your server
                }

                @Override
                public void onFailure(String ex) {
                    super.onFailure(ex);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

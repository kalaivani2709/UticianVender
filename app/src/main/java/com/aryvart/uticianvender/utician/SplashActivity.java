package com.aryvart.uticianvender.utician;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.view.View;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.admin.AdminList;
import com.aryvart.uticianvender.provider.AddHoroscope;
import com.aryvart.uticianvender.provider.ProviderAddServices;
import com.aryvart.uticianvender.provider.ProviderCompanyInfoLicense;

import com.aryvart.uticianvender.provider.ProviderCompanyInfoPay;
import com.aryvart.uticianvender.provider.Provider_ChooseService;
import com.aryvart.uticianvender.provider.Provider_Paypalid_Screen;


/**
 * Created by android3 on 9/5/16.
 */
public class SplashActivity extends Activity {
    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    public static Context context;
    public static SharedPreferences autoLoginPreference, sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        context = this;
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        autoLoginPreference = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);

        sharedPreferences = getSharedPreferences("regid", Context.MODE_PRIVATE);

        MultiDex.install(this);

      //  verifyStoragePermissions(this);

        Thread timerThread = new Thread() {
            public void run() {
                try {
                    sleep(3000);

                    try {
                        startActivity(new Intent(SplashActivity.this, HomePage.class));

                        Intent intent = null;
                        if (autoLoginPreference.getString("uname", null) != null && autoLoginPreference.getString("pwd", null) != null) {

                            if (sharedPreferences.getString("utype", null) != null) {

                                String userType = sharedPreferences.getString("utype", null);

                                Log.i("HHJ", "userType" + userType);

                                if (userType.equalsIgnoreCase("provider")) {

                                    String strServiceType = sharedPreferences.getString("serviceType", null);

                                    Log.i("HHJ", "strServiceType" + strServiceType);


                                    if (strServiceType.equalsIgnoreCase("0")) {
                                        intent = new Intent(SplashActivity.this, AddHoroscope.class);
                                    } else if (strServiceType.equalsIgnoreCase("1")) {
                                        intent = new Intent(SplashActivity.this, ProviderCompanyInfoLicense.class);
                                    } else if (strServiceType.equalsIgnoreCase("2")) {
                                        intent = new Intent(SplashActivity.this, ProviderCompanyInfoPay.class);
                                    } else if (strServiceType.equalsIgnoreCase("5")) {
                                        intent = new Intent(SplashActivity.this, ProviderAddServices.class);
                                    } else if (strServiceType.equalsIgnoreCase("6")) {
                                        intent = new Intent(SplashActivity.this, Provider_ChooseService.class);
                                    }
                                    else if (strServiceType.equalsIgnoreCase("7")) {
                                        intent = new Intent(SplashActivity.this, Provider_Paypalid_Screen.class);
                                    }else if (strServiceType.equalsIgnoreCase("8")) {
                                        intent = new Intent(SplashActivity.this, SignInActivity.class);
                                    } else if (strServiceType.equalsIgnoreCase("4")) {
                                        intent = new Intent(SplashActivity.this, SignInActivity.class);
                                    }
                                    else {
                                        intent = new Intent(SplashActivity.this, SignInActivity.class);
                                    }

                                    if (intent != null) {
                                        startActivity(intent);
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                            finishAffinity();
                                        }
                                    }
                                }  else {
                                    intent = new Intent(SplashActivity.this, AdminList.class);
                                    startActivity(intent);
                                }
                            } else {
                                intent = new Intent(SplashActivity.this, HomePage.class);
                                startActivity(intent);
                            }
                        } else {
                            intent = new Intent(SplashActivity.this, HomePage.class);
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {

                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }
}

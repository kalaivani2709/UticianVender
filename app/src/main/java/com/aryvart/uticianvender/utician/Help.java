package com.aryvart.uticianvender.utician;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.genericclasses.AlertDialogManager;
import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.aryvart.uticianvender.provider.Provider_DashBoard;


/**
 * Created by android1 on 15/7/16.
 */
public class Help extends Activity {
    AlertDialogManager alert = new AlertDialogManager();
    ProgressDialog progressDialog;

    Context context;

    TextView tvHelp;
    ProgressDialog mProgress;
    WebView mWeb;
    GeneralData gD;
    String strtype;
    int nScreenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.help_screen);
           SplashActivity. sharedPreferences = getSharedPreferences("regid", Context.MODE_PRIVATE);


            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = (int) ((float) nHeight / (float) 2.1);

            context = this;
            strtype = SplashActivity.sharedPreferences.getString("utype", null);
            Log.i("YY","strytpe" +strtype);

            gD = new GeneralData(context);
            if (!gD.isConnectingToInternet()) {
                Toast.makeText(Help.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
            }

            ImageView imvback = (ImageView) findViewById(R.id.back);
            mWeb = (WebView) findViewById(R.id.web_help);
            mWeb.getSettings().setJavaScriptEnabled(true);

            //tvHelp = (TextView) findViewById(R.id.tv_help);
            gD.callload(context, nScreenHeight);


            mWeb.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                    return super.onJsAlert(view, url, message, result);
                }
            });

            mWeb.setWebViewClient(new WebViewClient() {
                // load url
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }

                // when finish loading page
                public void onPageFinished(WebView view, String url) {
                    if(gD.alertDialog!=null) {
                        gD.alertDialog.dismiss();
                    }
                }
            });



            if (strtype.equalsIgnoreCase("Provider"))
            {
                mWeb.loadUrl(gD.common_baseurl+"mail/faqprovider.html");
            }
            else
            {
                mWeb.loadUrl(gD.common_baseurl+"mail/faq.html");
            }


          //  mWeb.loadUrl("http://aryvartdev.com/projects/utician/mail/faq.html");



            imvback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Help.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Intent i;
                        finish();

                        if (strtype.equalsIgnoreCase("Provider"))
                        {
                            i = new Intent(Help.this, Provider_DashBoard.class);
                            //  gD.screenback = 0;
                            startActivity(i);

                        }
                        else
                        {
                         //   i = new Intent(Help.this, User_Category.class);
                        }



                    }}
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            Intent i;
            finish();

            if (strtype.equalsIgnoreCase("Provider"))
            {
           i = new Intent(Help.this, Provider_DashBoard.class);
                //  gD.screenback = 0;
                startActivity(i);

            }
            else
            {
      //   i = new Intent(Help.this, User_Category.class);
            }




            super.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
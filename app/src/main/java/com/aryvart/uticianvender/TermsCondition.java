package com.aryvart.uticianvender;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.genericclasses.AlertDialogManager;
import com.aryvart.uticianvender.genericclasses.GeneralData;

/**
 * Created by android1 on 20/7/16.
 */
public class TermsCondition extends Activity {
    AlertDialogManager alert = new AlertDialogManager();
    ProgressDialog progressDialog;
    Context context;
    TextView tvHelp;
    ProgressDialog mProgress;
    WebView mWeb;
    GeneralData gD;
    int nScreenHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.termscondition);

            context = this;
            gD = new GeneralData(context);
            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = (int) ((float) nHeight / (float) 2.1);
            ImageView imvback = (ImageView) findViewById(R.id.back);
            mWeb = (WebView) findViewById(R.id.web_help);
            //tvHelp = (TextView) findViewById(R.id.tv_help);

            gD.callload(context, nScreenHeight);

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
            mWeb.loadUrl(

                    gD.common_baseurl + "mail/terms-condition.html");
            Log.i("FF","url" +gD.common_baseurl + "mail/terms-condition.html");
            imvback.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            finish();
            super.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
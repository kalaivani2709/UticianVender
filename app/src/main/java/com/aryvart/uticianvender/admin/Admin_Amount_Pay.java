package com.aryvart.uticianvender.admin;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.aryvart.uticianvender.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by android3 on 5/11/16.
 */
public class Admin_Amount_Pay extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_amount_pay);
        WebView webView = (WebView) findViewById(R.id.webView);
        String strResp = getIntent().getStringExtra("Resp");
        Log.i("SS","response" + strResp);
        JSONObject jsoBj = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.app_green));
        }
        try {
            jsoBj = new JSONObject(strResp);
            webView.loadUrl(jsoBj.getString("url"));
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);

            webView.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });

            ImageView btnClose = (ImageView) findViewById(R.id.cancel_btn);

            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(Admin_Amount_Pay.this,Admin_Share_Pay.class);
                    startActivity(i);
                    finish();

                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }




    }
}

package com.aryvart.uticianvender.provider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.genericclasses.GeneralData;

import com.aryvart.uticianvender.imageCache.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by android3 on 27/8/16.
 */
public class provider_transaction_details extends Activity{
    ImageView menu_back;
    TextView txt_pdate,txt_btime,txt_uramt,txt_Totalrate,txt_service,txt_username;
    Context context;
    ImageLoader imgLoader;
    ImageView useriamge;
    String str_jsonval;
    GeneralData gD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transaction_details);

        try {
            context = this;

            gD = new GeneralData(context);
            imgLoader = new ImageLoader(context);
            useriamge = (ImageView) findViewById(R.id.user_image);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.app_green));
            }

            txt_username = (TextView) findViewById(R.id.tv_rName);

            str_jsonval = getIntent().getStringExtra("jsonvalues");

            Log.i("II..", "jsonvalues" + str_jsonval);

            menu_back = (ImageView) findViewById(R.id.menu_back);
            menu_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(provider_transaction_details.this, ProviderAccount.class);
                    startActivity(i);
                    finish();
                }
            });
            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(context, "Unable to connect with Internet. Please check your internet connection and try again");
            }

            txt_service = (TextView) findViewById(R.id.txt_service);
            txt_Totalrate = (TextView) findViewById(R.id.txt_Totalrate);
            txt_uramt = (TextView) findViewById(R.id.txt_uramt);
            txt_btime = (TextView) findViewById(R.id.txt_btime);
            txt_pdate = (TextView) findViewById(R.id.txt_pdate);


            try {
                JSONObject jsobj = new JSONObject(str_jsonval);
                txt_service.setText(jsobj.getString("service"));
                txt_Totalrate.setText("$" +jsobj.getString("totalrate"));
                txt_uramt.setText("$" + jsobj.getString("pro_amt"));
                txt_btime.setText(jsobj.getString("bdate") + jsobj.getString("btime"));
                txt_pdate.setText(jsobj.getString("pdate") + jsobj.getString("ptime") );
                txt_username.setText(jsobj.getString("username"));
                String strPath = jsobj.getString("userimage");

                if (strPath.length() == 0) {
                    useriamge.setBackgroundResource(R.drawable.default_user_icon);
                } else {

                    String strimage = gD.common_baseurl+"upload/" +strPath;

                    useriamge.setImageBitmap(imgLoader.getBitmap(strimage));

                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    @Override
    public void onBackPressed() {
        Intent i = new Intent(provider_transaction_details.this, ProviderAccount.class);
        startActivity(i);
        finish();
    }
}

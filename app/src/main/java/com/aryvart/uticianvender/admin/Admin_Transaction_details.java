package com.aryvart.uticianvender.admin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.genericclasses.GeneralData;

import com.aryvart.uticianvender.imageCache.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.TimeZone;

/**
 * Created by android3 on 27/8/16.
 */
public class Admin_Transaction_details extends Activity {
    ImageView menu_back;
    TextView txt_pdate,txt_btime,txt_providername,txt_uramt,txt_Totalrate,txt_service,txt_username;
    Context context;
    ImageLoader imgLoader;
    ImageView useriamge,providerimage;
    String str_jsonval;
    String timezonevalue;
    GeneralData gD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.admin_transactiondetails);

            context = this;
            gD = new GeneralData(context);
            imgLoader = new ImageLoader(context);
            useriamge = (ImageView) findViewById(R.id.user_image);


            TimeZone tz = TimeZone.getDefault();
            System.out.println("TimeZone " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

            timezonevalue=tz.getID();
            Log.i("TZ", "timezone" + timezonevalue);
            txt_username = (TextView) findViewById(R.id.tv_userName);

            providerimage = (ImageView) findViewById(R.id.provider_image);


            txt_providername = (TextView) findViewById(R.id.tv_pName);


            str_jsonval = getIntent().getStringExtra("jsonvalues");

            Log.i("II..", "jsonvalues" + str_jsonval);

            menu_back = (ImageView) findViewById(R.id.menu_back);
            menu_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Admin_Transaction_details.this, Admin_HistoryPage.class);
                    startActivity(i);
                    finish();
                }
            });


            txt_service = (TextView) findViewById(R.id.txt_service);
            txt_Totalrate = (TextView) findViewById(R.id.txt_Totalrate);
            txt_uramt = (TextView) findViewById(R.id.txt_uramt);
            txt_btime = (TextView) findViewById(R.id.txt_btime);
            txt_pdate = (TextView) findViewById(R.id.txt_pdate);


            try {
                JSONObject jsobj = new JSONObject(str_jsonval);
                txt_service.setText(jsobj.getString("service"));
                txt_Totalrate.setText("$" +jsobj.getString("totalrate"));
                txt_uramt.setText("$" + jsobj.getString("admin_amt"));
                txt_btime.setText(jsobj.getString("bdate") + jsobj.getString("btime"));
                txt_pdate.setText(jsobj.getString("pdate") + jsobj.getString("ptime") );
                txt_username.setText(jsobj.getString("providername"));
                txt_providername.setText(jsobj.getString("username"));
                String strPath = jsobj.getString("providerimage");

                if (strPath.length() == 0) {
                    useriamge.setBackgroundResource(R.drawable.default_user_icon);
                } else {

                    String strimage = gD.common_baseurl+"upload/" + jsobj.getString("providerimage");

                    useriamge.setImageBitmap(imgLoader.getBitmap(strimage));

                }
                String strPathprovider = jsobj.getString("userimage");

                if (strPathprovider.length() == 0) {
                    providerimage.setBackgroundResource(R.drawable.default_user_icon);
                } else {

                    String strimages = gD.common_baseurl+"upload/" + jsobj.getString("userimage");

                    providerimage.setImageBitmap(imgLoader.getBitmap(strimages));

                }



            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onResume() {
        TimeZone tz = TimeZone.getDefault();
        System.out.println("TimeZone " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

        timezonevalue = tz.getID();
        super.onResume();

    }
    @Override
    public void onBackPressed() {

        Intent i = new Intent(Admin_Transaction_details.this, Admin_HistoryPage.class);
        startActivity(i);
        finish();

    }
}


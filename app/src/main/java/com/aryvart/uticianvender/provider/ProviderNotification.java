package com.aryvart.uticianvender.provider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.adapter.ProviderNotificationAdpater;
import com.aryvart.uticianvender.bean.ProviderNotificationBean;
import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.aryvart.uticianvender.genericclasses.MultipartUtility;
import com.aryvart.uticianvender.imageCache.ImageLoader;
import com.aryvart.uticianvender.utician.SplashActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Android2 on 7/19/2016.
 */
public class ProviderNotification extends Activity {
    ImageView imvBack;
    ProgressDialog pD;
    Context context;
    ImageLoader imgLoader;
    String str_pid;

    String timezonevalue;
    TextView emptydata;
    JSONArray providerReviewJSONArray;
    private RecyclerView recyclerView;
    private ProviderNotificationAdpater mAdapter;
    GeneralData gD;
    int nScreenHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.provider_notification);
            pD = new ProgressDialog(ProviderNotification.this);
            context = this;
            gD = new GeneralData(context);

            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = (int) ((float) nHeight / (float) 2.1);
            imgLoader = new ImageLoader(context);
            emptydata = (TextView) findViewById(R.id.emptydata);

            TimeZone tz = TimeZone.getDefault();
            System.out.println("TimeZone " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

            timezonevalue=tz.getID();
            Log.i("TZ", "timezone" + timezonevalue);

            imvBack = (ImageView) findViewById(R.id.img_back);
            imvBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    Intent i = new Intent(context, Provider_DashBoard.class);
                    //  gD.screenback = 0;
                    startActivity(i);
                }
            });

            //Registration_page.idpref = context.getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view_notification);
            SplashActivity.sharedPreferences = getSharedPreferences("regid", Context.MODE_PRIVATE);

            str_pid = SplashActivity.sharedPreferences.getString("UID", null);
            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(context, "Unable to connect with Internet. Please check your internet connection and try again");
            }
            else {
                GetProviderReviews get_Provider_details = new GetProviderReviews(str_pid, context, recyclerView);
                get_Provider_details.execute();
            }

            imvBack.setOnClickListener(new View.OnClickListener() {
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
    protected void onResume() {


        try {
            TimeZone tz = TimeZone.getDefault();
            System.out.println("TimeZone " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

            timezonevalue = tz.getID();

            if (!gD.isConnectingToInternet()) {
                Toast.makeText(ProviderNotification.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
            }


            super.onResume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class GetProviderReviews extends AsyncTask {
        String user_id = null;
        String sResponse = null;
        Context cont;
        RecyclerView recycler;

        public GetProviderReviews(String userid, Context context, RecyclerView recyclerView) {
            this.user_id = userid;
            this.cont = context;
            this.recycler = recyclerView;

        }

        @Override
        protected void onPreExecute() {
            ProviderNotification.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gD.callload(context, nScreenHeight);


                }
            });
        }

        @Override
        protected Object doInBackground(Object[] param) {
            try {
                String charset = "UTF-8";
                String requestURL = gD.common_baseurl+"notification.php";
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);

                multipart.addHeaderField("Content-Encoding", "gzip");
                multipart.addFormField("timezone", timezonevalue.trim());
                Log.i("RR", "timezonevalue" + timezonevalue);
                multipart.addFormField("providerid", SplashActivity.sharedPreferences.getString("UID", null));
                List<String> response = multipart.finish();

                StringBuilder sb = new StringBuilder();
                for (String line : response) {
                    System.out.println(line);
                    sb.append(line);
                }
                try {
                    JSONObject jsonObj = new JSONObject(sb.toString());
                    Log.i("NNN", "StrResp : " + jsonObj.toString());
                    sResponse = jsonObj.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return sResponse;
        }

        @Override
        protected void onPostExecute(Object response) {




            if(gD.alertDialog!=null) {
                gD.alertDialog.dismiss();
            }

            try {

                ArrayList beanArrayList = new ArrayList<ProviderNotificationBean>();
                Log.i("HH", "strResp : " + beanArrayList.size());
                JSONObject jsobj = new JSONObject(sResponse);
                JSONObject providersReviewJSONobject = null;
                Log.i("HH", "strResp : " + sResponse);
                if (jsobj.getString("status").equalsIgnoreCase("success")) {
                    providerReviewJSONArray = jsobj.getJSONArray("provider_details");
                    if (providerReviewJSONArray.length() > 0) {

                        for (int i = 0; i < providerReviewJSONArray.length(); i++) {
                            ProviderNotificationBean bean = new ProviderNotificationBean();
                            providersReviewJSONobject = providerReviewJSONArray.getJSONObject(i);
                            Log.i("GG", "providersServiceJSONobject : " + providersReviewJSONobject);
                            bean.setN_user_image(gD.common_baseurl+"upload/" + providersReviewJSONobject.getString("image"));
                            bean.setStr_descption(providersReviewJSONobject.getString("message"));
                            bean.setStr_time(providersReviewJSONobject.getString("send_at"));
                            bean.setStr_service(providersReviewJSONobject.getString("service"));
                            bean.setStr_type(providersReviewJSONobject.getString("type"));

                            beanArrayList.add(bean);
                        }
                    }
         else {
                        emptydata.setVisibility(View.VISIBLE);
                    }
                }

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recycler.setLayoutManager(mLayoutManager);
                recycler.setItemAnimator(new DefaultItemAnimator());
                mAdapter = new ProviderNotificationAdpater(beanArrayList, ProviderNotification.this);
                recycler.setAdapter(mAdapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(context, Provider_DashBoard.class);
        //  gD.screenback = 0;
        startActivity(i);
    }

}

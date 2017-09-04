package com.aryvart.uticianvender.provider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.bean.UserHistoryBean;
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
 * Created by android3 on 11/11/16.
 */
public class Provider_Profile_Edit  extends Activity implements View.OnClickListener {

    ImageView imvBack;
    TextView txt_Providername, txt_ParlourName, tv_lastApp_userName, time, service_type;
    ImageView providerimage, userprofile;
    ImageLoader imgLoader;
    Context context;
    LinearLayout next_appointmentLay;
    TextView no_appointment;
    ProgressDialog dialog, pd;
    GeneralData gD;
    int nScreenHeight;
    String timezonevalue;;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.provider_profileedit);
            context = this;


            gD = new GeneralData(context);
            TimeZone tz = TimeZone.getDefault();
            System.out.println("TimeZone " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

            timezonevalue=tz.getID();
            Log.i("TZ", "timezone" + timezonevalue);
            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = (int) ((float) nHeight / (float) 2.1);

            imgLoader = new ImageLoader(context);
            providerimage = (ImageView) findViewById(R.id.user_image);
          //  providerimage.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            next_appointmentLay = (LinearLayout) findViewById(R.id.next_appointmentLay);
            no_appointment = (TextView) findViewById(R.id.no_appointment);


            txt_Providername = (TextView) findViewById(R.id.tv_ProviderName);
            //Registration_page.idpref = context.getSharedPreferences("UserProfile", Context.MODE_PRIVATE);
            // Registration_page.fname = context.getSharedPreferences("FullName", Context.MODE_PRIVATE);
            SplashActivity.sharedPreferences = context.getSharedPreferences("regid", Context.MODE_PRIVATE);
            Log.i("JJJ", "id.." + SplashActivity.sharedPreferences.getString("UID", null));
            txt_Providername.setText(SplashActivity.sharedPreferences.getString("fullname", null));
            imgLoader.DisplayImage(SplashActivity.sharedPreferences.getString("user_image", null), providerimage);


            //next appointment
            tv_lastApp_userName = (TextView) findViewById(R.id.tv_lastApp_userName);
            time = (TextView) findViewById(R.id.time);
            service_type = (TextView) findViewById(R.id.service_type);
            userprofile = (ImageView) findViewById(R.id.userprofile);
            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(context, "Unable to connect with Internet. Please check your internet connection and try again");
            }
            else {
                GetProviderDetails get_Provider_details = new GetProviderDetails(SplashActivity.sharedPreferences.getString("UID", null), context);
                get_Provider_details.execute();
            }
            LinearLayout llayBrainInfo = (LinearLayout) findViewById(R.id.llayBrainInfo);
            LinearLayout llayAcc = (LinearLayout) findViewById(R.id.llayAccount);

            LinearLayout llaySuport = (LinearLayout) findViewById(R.id.llaySupport);
            LinearLayout llayPay = (LinearLayout) findViewById(R.id.llayPayInfo);
            LinearLayout llayServices = (LinearLayout) findViewById(R.id.llayServices);
            LinearLayout llayCompany = (LinearLayout) findViewById(R.id.llayCompanyInfo);
            LinearLayout llaycompanylicense = (LinearLayout) findViewById(R.id.llayCompanyInfolicense);
            LinearLayout llaypreference = (LinearLayout) findViewById(R.id.llayPreference);
            imvBack = (ImageView) findViewById(R.id.menu_back);

            llayBrainInfo.setOnClickListener(this);
            llayAcc.setOnClickListener(this);


            llaypreference.setOnClickListener(this);
            llaySuport.setOnClickListener(this);
            llayServices.setOnClickListener(this);
            llayCompany.setOnClickListener(this);
            llayPay.setOnClickListener(this);
            llaycompanylicense.setOnClickListener(this);
            imvBack.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        try {
            String strCompanyName = SplashActivity.sharedPreferences.getString("companyname", null);
            txt_ParlourName = (TextView) findViewById(R.id.tv_Parlourname);
            txt_ParlourName.setText(strCompanyName);
            TimeZone tz = TimeZone.getDefault();
            System.out.println("TimeZone " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

            timezonevalue = tz.getID();

            super.onResume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        Intent voicpage = new Intent(Provider_Profile_Edit.this, Provider_DashBoard.class);




        startActivity(voicpage);
    }
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        try {
            Intent nextScreenIntent = null;
            switch (v.getId()) {

                case R.id.llayAccount:

                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_Profile_Edit.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        nextScreenIntent = new Intent(Provider_Profile_Edit.this, ProviderAccount.class);
                        break;
                    }


                case R.id.llayPreference:

                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_Profile_Edit.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        nextScreenIntent = new Intent(Provider_Profile_Edit.this, Provider_PreferencePage.class);
                        break;
                    }

                case R.id.llaySupport:

                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_Profile_Edit.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        nextScreenIntent = new Intent(Provider_Profile_Edit.this, Provider_support.class);
                        break;
                    }
                case R.id.llayCompanyInfolicense:

                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_Profile_Edit.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        nextScreenIntent = new Intent(Provider_Profile_Edit.this, ProviderLicenseEdit.class);
                        break;
                    }
                case R.id.menu_back:

                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_Profile_Edit.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        finish();
                        Intent voicpage = new Intent(Provider_Profile_Edit.this, Provider_DashBoard.class);




                        startActivity(voicpage);
                        break;
                    }

                case R.id.llayServices:

                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_Profile_Edit.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        nextScreenIntent = new Intent(Provider_Profile_Edit.this, ProviderAddServices_edit.class);
                        break;
                    }
                case R.id.llayBrainInfo:

                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_Profile_Edit.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        nextScreenIntent = new Intent(Provider_Profile_Edit.this, ProviderCompanyPay_Edit.class);


                        break;
                    }


                case R.id.llayCompanyInfo:

                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_Profile_Edit.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        nextScreenIntent = new Intent(Provider_Profile_Edit.this, AddHoroscope_edit.class);


                        break;
                    }
                case R.id.llayPayInfo:

                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_Profile_Edit.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        nextScreenIntent = new Intent(Provider_Profile_Edit.this, Provider_paypalid_edit_screen.class);


                        break;
                    }
            }
            if (nextScreenIntent != null) {
                startActivity(nextScreenIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    class GetProviderDetails extends AsyncTask {
        String user_id = null;
        String sResponse = null;
        Context cont;
        RecyclerView recycler;

        public GetProviderDetails(String userid, Context context) {
            this.user_id = userid;
            this.cont = context;
        }

        @Override
        protected void onPreExecute() {
            gD.callload(context, nScreenHeight);



        }

        @Override
        protected Object doInBackground(Object[] param) {
            try {
                String charset = "UTF-8";
                Log.i("JJJ", "id.." + SplashActivity.sharedPreferences.getString("UID", null));
                String requestURL = gD.common_baseurl+"nextappointment.php";
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
                    Log.i("JJJ", "StrResp : " + jsonObj.toString());
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

            try {
                ArrayList beanArrayList = new ArrayList<UserHistoryBean>();
                JSONObject jsobj = new JSONObject(sResponse);
                Log.i("HH", "strResp : " + sResponse);
                if (jsobj.getString("status").equalsIgnoreCase("success")) {

                    next_appointmentLay.setVisibility(View.VISIBLE);
                    JSONArray providerServicesJSONArray = jsobj.getJSONArray("next_appointment");
                    if (providerServicesJSONArray.length() > 0) {
                        for (int i = 0; i < providerServicesJSONArray.length(); i++) {
                            JSONObject providersServiceJSONobject = providerServicesJSONArray.getJSONObject(i);
                            // imgLoader.DisplayImage(Registration_page.idpref.getString("userimage", null), providerimage);
                            service_type.setText(providersServiceJSONobject.getString("service"));
                            time.setText(providersServiceJSONobject.getString("starttime"));
                            tv_lastApp_userName.setText(providersServiceJSONobject.getString("username"));
                            imgLoader.DisplayImage(gD.common_baseurl+"upload/" + providersServiceJSONobject.getString("image_path"), userprofile);
                            no_appointment.setVisibility(View.GONE);
                        }
                    } else {
                        no_appointment.setVisibility(View.VISIBLE);
                        next_appointmentLay.setVisibility(View.GONE);
                    }
                }



                if(gD.alertDialog!=null) {
                    gD.alertDialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}


package com.aryvart.uticianvender.provider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.adapter.Provider_Support_Payment;
import com.aryvart.uticianvender.adapter.Provider_Support_Review;
import com.aryvart.uticianvender.adapter.Provider_support_Appointment;
import com.aryvart.uticianvender.bean.ProviderReviewBean;
import com.aryvart.uticianvender.bean.commonBeanSupport;
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
 * Created by android3 on 19/7/16.
 */
public class Provider_support extends Activity {

    LinearLayout rl_left;
    ImageView img_left;
    TextView txt_left;

    LinearLayout rl_right;

    String timezonevalue;
    ImageView img_right;
    TextView txt_right;

    LinearLayout llay_appointments, ll_reviews, llay_payments;
    TextView provider_name, no_review, provider_company_name, payment_empty, appointment_empty;
    ImageView btn_back;
    String provider_id;
    ;
    ImageView provider_image;
    Context context;
    ImageLoader imgLoader;
    ListView listReviews, appointment_list, payment_list;

    ProgressDialog pD;
    String strOnlineStatus;
    int nScreenHeight;

    GeneralData gD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.provider_support);

        try {
            //  pSA = new ProviderServicesAdapter();
            context = this;
            imgLoader = new ImageLoader(context);

            gD = new GeneralData(context);

            provider_image = (ImageView) findViewById(R.id.provider_image);
            provider_name = (TextView) findViewById(R.id.provider_name);

            TimeZone tz = TimeZone.getDefault();
            System.out.println("TimeZone " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

            timezonevalue=tz.getID();
            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = (int) ((float) nHeight / (float) 2.1);
            Log.i("TZ", "timezone" + timezonevalue);

            SplashActivity.sharedPreferences = context.getSharedPreferences("regid", Context.MODE_PRIVATE);
            Log.i("JJJ", "id.." + SplashActivity.sharedPreferences.getString("UID", null));
            provider_name.setText(SplashActivity.sharedPreferences.getString("fullname", null));
            imgLoader.DisplayImage(SplashActivity.sharedPreferences.getString("user_image", null), provider_image);


            payment_empty = (TextView) findViewById(R.id.payment_empty);
            appointment_empty = (TextView) findViewById(R.id.appointment_empty);

            provider_company_name = (TextView) findViewById(R.id.parlour_name);
            no_review = (TextView) findViewById(R.id.empty);
            listReviews = (ListView) findViewById(R.id.reviews_list);
            appointment_list = (ListView) findViewById(R.id.appointment_list);
            payment_list = (ListView) findViewById(R.id.payment_list);

            pD = new ProgressDialog(Provider_support.this);

            rl_left = (LinearLayout) findViewById(R.id.rl_left);
            img_left = (ImageView) findViewById(R.id.img_left);
            txt_left = (TextView) findViewById(R.id.txt_left);

            rl_right = (LinearLayout) findViewById(R.id.rl_right);
            img_right = (ImageView) findViewById(R.id.img_right);
            txt_right = (TextView) findViewById(R.id.txt_right);

            llay_appointments = (LinearLayout) findViewById(R.id.llay_appointments);
            ll_reviews = (LinearLayout) findViewById(R.id.ll_reviews);
            llay_payments = (LinearLayout) findViewById(R.id.llay_payments);


            provider_id = getIntent().getStringExtra("providerid");
            strOnlineStatus = getIntent().getStringExtra("providerstatus");


            //back button
            btn_back = (ImageView) findViewById(R.id.back);
            btn_back.setOnClickListener(new View.OnClickListener() {
                @Override

                public void onClick(View v) {

                    finish();
                }

            });


            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(context, "Unable to connect with Internet. Please check your internet connection and try again");
            }

else
            {
                GetProviderDetails get_Provider_details = new GetProviderDetails();
                get_Provider_details.execute();

            }
            appointments();



            rl_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_support.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    }
                    else {
                    if (txt_left.getText().equals("Payment")) {
                        payment();
                    /*Toast.makeText(UserProviderDetails.this, "services", Toast.LENGTH_SHORT).show();*/
                    } else if (txt_left.getText().equals("Appointments")) {
                        appointments();
                        //Toast.makeText(Provider_support.this, "Appointments", Toast.LENGTH_SHORT).show();
                    } else {
                        reviews();
                        // Toast.makeText(Provider_support.this, "reviews", Toast.LENGTH_SHORT).show();
                    }
                }}
            });
            rl_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_support.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    }
                    else {
                    if (txt_right.getText().equals("Payment")) {
                        payment();
                    /*Toast.makeText(UserProviderDetails.this, "services", Toast.LENGTH_SHORT).show();*/
                    } else if (txt_right.getText().equals("Appointments")) {
                        appointments();
                    /*Toast.makeText(UserProviderDetails.this, "gallery", Toast.LENGTH_SHORT).show();*/
                    } else {
                        reviews();
                    /*Toast.makeText(UserProviderDetails.this, "reviews", Toast.LENGTH_SHORT).show();*/
                    }
                }}
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onResume() {

        TimeZone tz = TimeZone.getDefault();
        System.out.println("TimeZone " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

        timezonevalue = tz.getID();

        super.onResume();



    }    public void reviews() {
        try {
            ll_reviews.setVisibility(View.VISIBLE);
            llay_appointments.setVisibility(View.GONE);
            llay_payments.setVisibility(View.GONE);
            img_left.setBackgroundResource(R.drawable.service);
            txt_left.setText("Payment");
            img_right.setBackgroundResource(R.drawable.album);
            txt_right.setText("Appointments");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void appointments() {
        try {
            ll_reviews.setVisibility(View.GONE);
            llay_appointments.setVisibility(View.VISIBLE);
            llay_payments.setVisibility(View.GONE);
            img_left.setBackgroundResource(R.drawable.service);
            txt_left.setText("Payment");
            img_right.setBackgroundResource(R.drawable.review);
            txt_right.setText("Reviews");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void payment() {
        try {
            ll_reviews.setVisibility(View.GONE);
            llay_appointments.setVisibility(View.GONE);
            llay_payments.setVisibility(View.VISIBLE);
            img_right.setBackgroundResource(R.drawable.review);
            txt_right.setText("Reviews");
            img_left.setBackgroundResource(R.drawable.album);
            txt_left.setText("Appointments");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //appointment task

    @Override
    public void onBackPressed() {
        finish();
    }

    class GetProviderDetails extends AsyncTask {
        String provider_i = null;
        String sResponse = null;


        public GetProviderDetails() {

        }

        @Override
        protected void onPreExecute() {
            gD.callload(context, nScreenHeight);



            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] param) {

            try {
                String charset = "UTF-8";
                String requestURL = gD.common_baseurl+"support.php";
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                multipart.addHeaderField("Content-Encoding", "gzip");

                multipart.addFormField("timezone", timezonevalue.trim());
                Log.i("RR", "timezonevalue" + timezonevalue);
                multipart.addFormField("providerid", SplashActivity.sharedPreferences.getString("UID", null));
                List<String> response = multipart.finish();

                Log.i("RR", "providerid" + SplashActivity.sharedPreferences.getString("UID", null));
                StringBuilder sb = new StringBuilder();
                for (String line : response) {
                    System.out.println(line);
                    sb.append(line);
                }
                try {
                    JSONObject jsonObj = new JSONObject(sb.toString());
                    Log.i("SSS", "StrResp : " + jsonObj.toString());
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

                ArrayList cPRB = new ArrayList<commonBeanSupport>();
                ArrayList cPRB1 = new ArrayList<commonBeanSupport>();
                ArrayList beanArrayList = new ArrayList<ProviderReviewBean>();
                JSONObject jsobj = new JSONObject(sResponse);
                Log.i("HH", "strResp : " + sResponse);

                JSONArray providerReviewsJSONArray = jsobj.getJSONArray("reviews");
                JSONArray providersAppoitmentJSONArray = jsobj.getJSONArray("appointments");
                JSONArray providerPaymentJSONArray = jsobj.getJSONArray("payment");
                if (jsobj.getString("status").equalsIgnoreCase("success")) {


                    if (providerReviewsJSONArray.length() > 0) {

                        for (int i = 0; i < providerReviewsJSONArray.length(); i++) {
                            ProviderReviewBean pRb = new ProviderReviewBean();
                            JSONObject providersReviewJSONobject = providerReviewsJSONArray.getJSONObject(i);


                            pRb.setStrphonenumbver(providersReviewJSONobject.getString("phonenumber"));
                            pRb.setStremailid(providersReviewJSONobject.getString("email"));

                            pRb.set_nickName(providersReviewJSONobject.getString("username"));
                            pRb.set_descrtiption(providersReviewJSONobject.getString("description"));
                            pRb.set_rating(providersReviewJSONobject.getString("rating"));
                            pRb.setIssueid(providersReviewJSONobject.getString("id"));
                            pRb.setUserid(providersReviewJSONobject.getString("userid"));
                            Log.e("LL1", providersReviewJSONobject.getString("username"));
                            Log.e("LL1", providersReviewJSONobject.getString("description"));


                            pRb.set_ImageName(gD.common_baseurl+"upload/" + providersReviewJSONobject.getString("image_path"));


                            beanArrayList.add(pRb);
                            Provider_Support_Review pRadapter = new Provider_Support_Review(Provider_support.this, beanArrayList);
                            listReviews.setAdapter(pRadapter);
                        }
                    } else {

                        listReviews.setEmptyView(no_review);
                    }


                    if (providersAppoitmentJSONArray.length() > 0) {

                        for (int i = 0; i < providersAppoitmentJSONArray.length(); i++) {

                            commonBeanSupport cRb = new commonBeanSupport();
                            JSONObject providerAppointmJSONobject = providersAppoitmentJSONArray.getJSONObject(i);
                            cRb.setStrUserName(providerAppointmJSONobject.getString("username"));
                            cRb.setIssueid(providerAppointmJSONobject.getString("id"));
                            cRb.setUserid(providerAppointmJSONobject.getString("userid"));


                            cRb.setStrphonenumbver(providerAppointmJSONobject.getString("phonenumber"));
                            cRb.setStremailid(providerAppointmJSONobject.getString("email"));


                            cRb.setStrdateDetails(providerAppointmJSONobject.getString("date") + "/" + providerAppointmJSONobject.getString("starttime"));
                            cRb.setStrservices(providerAppointmJSONobject.getString("service"));

                            cRb.setStrUserImage(gD.common_baseurl+"upload/" + providerAppointmJSONobject.getString("image_path"));
                            cPRB.add(cRb);
                            Provider_support_Appointment pRadapter = new Provider_support_Appointment(Provider_support.this, cPRB);
                            appointment_list.setAdapter(pRadapter);

                        }
                    } else {

                        appointment_list.setEmptyView(appointment_empty);
                    }


                    if (providerPaymentJSONArray.length() > 0) {

                        for (int i = 0; i < providerPaymentJSONArray.length(); i++) {
                            Log.i("LL", "TT" + providerPaymentJSONArray.length());
                            commonBeanSupport cRb = new commonBeanSupport();
                            JSONObject providerPaymentJSONobject = providerPaymentJSONArray.getJSONObject(i);
                            cRb.setStrUserName(providerPaymentJSONobject.getString("username"));
                            cRb.setIssueid(providerPaymentJSONobject.getString("id"));
                            cRb.setUserid(providerPaymentJSONobject.getString("userid"));
                            cRb.setStrdateDetails(providerPaymentJSONobject.getString("bdate") + "/" + providerPaymentJSONobject.getString("btime"));
                            cRb.setStrservices(providerPaymentJSONobject.getString("service"));

                            cRb.setStrphonenumbver(providerPaymentJSONobject.getString("phonenumber"));
                            cRb.setStremailid(providerPaymentJSONobject.getString("email"));


                            cRb.setStrUserImage(gD.common_baseurl+"upload/" + providerPaymentJSONobject.getString("image_path"));
                            cPRB1.add(cRb);

                            Provider_Support_Payment pRadapte = new Provider_Support_Payment(Provider_support.this, cPRB1);
                            payment_list.setAdapter(pRadapte);


                        }
                    } else {

                        payment_list.setEmptyView(payment_empty);

                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}

package com.aryvart.uticianvender.provider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aryvart.uticianvender.Interface.GetBookingId;
import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.adapter.ProviderHistoryAdapter;
import com.aryvart.uticianvender.bean.UserHistoryBean;
import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.aryvart.uticianvender.genericclasses.MultipartUtility;

import com.aryvart.uticianvender.imageCache.ImageLoader;
import com.aryvart.uticianvender.utician.SplashActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by android1 on 17/7/16.
 */
public class ProviderHistoryPage extends Activity implements GetBookingId {
    LinearLayout lay_today, lay_all, lay_yesterday, lay_previWeek;
    TextView text_head;
    ProgressDialog pd;
    RecyclerView recyclerView;
    ProgressDialog dialog;
    Context ctx;
    int nScreenHeight,ScreenHeight;

    String timezonevalue;
    String  str_des, str_rating/*id*/;
    ImageLoader imgLoader;
    ImageView image_prev_week, image_yesterday, image_today, image_all, menu_back;
    JSONArray providerServicesToday, providerServicesYesterday, providerServicesAll, providerServicesPrevWeek;
    TextView emptyText;
    GeneralData gD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.user_history);

            ctx = this;
            gD = new GeneralData(ctx);
            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;

            TimeZone tz = TimeZone.getDefault();
            System.out.println("TimeZone " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

            timezonevalue=tz.getID();
            Log.i("TZ", "timezone" + timezonevalue);
            nScreenHeight = (int) ((float) nHeight / (float) 2.4);
            ScreenHeight = (int) ((float) nHeight / (float) 1.4);
            imgLoader = new ImageLoader(ctx);
            lay_all = (LinearLayout) findViewById(R.id.lay_all);
            emptyText = (TextView) findViewById(R.id.emptyText);
            lay_yesterday = (LinearLayout) findViewById(R.id.lay_yesterday);
            lay_today = (LinearLayout) findViewById(R.id.lay_today);
            lay_previWeek = (LinearLayout) findViewById(R.id.lay_prev_week);
            text_head = (TextView) findViewById(R.id.textContent);
            menu_back = (ImageView) findViewById(R.id.menu_back);
            image_all = (ImageView) findViewById(R.id.image_all);
            image_prev_week = (ImageView) findViewById(R.id.image_prev_week);
            image_yesterday = (ImageView) findViewById(R.id.image_yesterday);
            image_today = (ImageView) findViewById(R.id.image_today);
            text_head.setText("Today");
            image_today.setImageResource(R.drawable.history_red);
            recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            menu_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(ProviderHistoryPage.this, Provider_DashBoard.class);
                    startActivity(i);
                    finish();
                }
            });

            lay_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(ProviderHistoryPage.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    }
                    else {
                    text_head.setText("All");
                    image_all.setImageResource(R.drawable.history_red);
                    image_yesterday.setImageResource(R.drawable.history_green);
                    image_today.setImageResource(R.drawable.history_green);
                    image_prev_week.setImageResource(R.drawable.history_green);
                    showView("all");

                }}
            });
            lay_yesterday.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    text_head.setText("Yesterday");
                    image_yesterday.setImageResource(R.drawable.history_red);
                    image_all.setImageResource(R.drawable.history_green);
                    image_today.setImageResource(R.drawable.history_green);
                    image_prev_week.setImageResource(R.drawable.history_green);
                    showView("yesterday");

                }
            });
            lay_today.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    text_head.setText("Today");
                    image_today.setImageResource(R.drawable.history_red);
                    image_all.setImageResource(R.drawable.history_green);
                    image_yesterday.setImageResource(R.drawable.history_green);
                    image_prev_week.setImageResource(R.drawable.history_green);
                    showView("today");

                }
            });
            lay_previWeek.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    text_head.setText("Previous Week");
                    image_prev_week.setImageResource(R.drawable.history_red);
                    image_all.setImageResource(R.drawable.history_green);
                    image_today.setImageResource(R.drawable.history_green);
                    image_yesterday.setImageResource(R.drawable.history_green);
                    showView("prevweek");
                }
            });

            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(ctx, "Unable to connect with Internet. Please check your internet connection and try again");
            }
            else
            {
                GetProviderDetails get_Provider_details = new GetProviderDetails(SplashActivity.sharedPreferences.getString("UID", null), ctx, recyclerView);
                get_Provider_details.execute();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void showView(String data) {
       /* Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();*/
        try {
            if (data == "all") {
                /*if (providerServicesAll.length() > 0) {*/
                LoadLayout(recyclerView, providerServicesAll);
                /*}*/
                if (providerServicesAll.length() > 0) {
                    emptyText.setVisibility(View.GONE);
                } else {
                    emptyText.setVisibility(View.VISIBLE);
                }
            } else if (data == "yesterday") {
               /* if (providerServicesYesterday.length() > 0) {*/
                LoadLayout(recyclerView, providerServicesYesterday);
               /* } else {
                    Toast.makeText(UserHistoryPage.this, "No items to be found !", Toast.LENGTH_SHORT).show();
                }*/
                if (providerServicesYesterday.length() > 0) {
                    emptyText.setVisibility(View.GONE);
                } else {
                    emptyText.setVisibility(View.VISIBLE);
                }

            } else if (data == "today") {
                LoadLayout(recyclerView, providerServicesToday);
                if (providerServicesToday.length() > 0) {
                    emptyText.setVisibility(View.GONE);
                } else {
                    emptyText.setVisibility(View.VISIBLE);
                }

            } else {
               /* if (providerServicesPrevWeek.length() > 0) {*/
                LoadLayout(recyclerView, providerServicesPrevWeek);
               /* } else {
                    Toast.makeText(UserHistoryPage.this, "No items to be found !", Toast.LENGTH_SHORT).show();
                }*/
                if (providerServicesPrevWeek.length() > 0) {
                    emptyText.setVisibility(View.GONE);
                } else {
                    emptyText.setVisibility(View.VISIBLE);
                }
            }
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
                Toast.makeText(ProviderHistoryPage.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
            }


            super.onResume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void LoadLayout(RecyclerView recyclerView, JSONArray providerServicesMonth) {

        String name = null, image = null, time = null, date = null;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
     /*   recyclerView.addItemDecoration(new com.aryvart.utician.adapter.DividerItemDecoration(this, LinearLayoutManager.VERTICAL));*/
        ArrayList<UserHistoryBean> beanArrayList = new ArrayList<UserHistoryBean>();
        JSONObject providersServiceJSONobject = null;
        try {
            for (int i = 0; i < providerServicesMonth.length(); i++) {

                UserHistoryBean userHistoryBean = new UserHistoryBean();
                providersServiceJSONobject = providerServicesMonth.getJSONObject(i);
                userHistoryBean.setStr_shop_name(providersServiceJSONobject.getString("username"));
                //userHistoryBean.setStr_address(providersServiceJSONobject.getString("companyaddress"));
                Log.i("mA", "Get Val1 : " + providersServiceJSONobject.toString());

                if (providersServiceJSONobject.has("toaccept")) {
                    userHistoryBean.setStr_toaccept(providersServiceJSONobject.getString("toaccept"));
                    Log.i("HH", "toaccept : " + providersServiceJSONobject.getString("toaccept"));
                } else {
                    userHistoryBean.setStr_toaccept("1");
                }
                userHistoryBean.setStr_isaccept(providersServiceJSONobject.getString("isaccept"));
                Log.i("HH", "isaccept : " + providersServiceJSONobject.getString("isaccept"));

                userHistoryBean.setStr_response(providersServiceJSONobject.getString("apptstatus"));

                userHistoryBean.setStr_jsonvalue(providersServiceJSONobject.toString());

                userHistoryBean.setStr_services(providersServiceJSONobject.getString("service"));
                userHistoryBean.setStr_date_time(providersServiceJSONobject.getString("date") + " " + providersServiceJSONobject.getString("starttime"));
                userHistoryBean.setStr_price("$" + providersServiceJSONobject.getString("rate"));
                userHistoryBean.setStr_user_img(gD.common_baseurl+"upload/" + providersServiceJSONobject.getString("image_path"));
                beanArrayList.add(userHistoryBean);

            }

            ProviderHistoryAdapter userHistoryAdpater = new ProviderHistoryAdapter(beanArrayList, ProviderHistoryPage.this, (GetBookingId) ctx);
            recyclerView.setAdapter(userHistoryAdpater);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void bookingid(String id, String strTitle) {
        try {
            LayoutInflater inflater = LayoutInflater.from(ctx);
            View dialogLayout = inflater.inflate(R.layout.provider_review_popup, null);

            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(ctx);
            alertDialogBuilder.setView(dialogLayout);
            alertDialogBuilder.setCancelable(false);


            final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            //  alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            alertDialog.show();

            LinearLayout llayAlert = (LinearLayout) dialogLayout.findViewById(R.id.llayalertDialog);



            FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ScreenHeight);
            llayAlert.setLayoutParams(lparams);
            final RatingBar ratingBar = (RatingBar) dialogLayout.findViewById(R.id.review_rating);
            final Button btn_submit_review = (Button) dialogLayout.findViewById(R.id.but_submit);
            final ImageView user_image = (ImageView) dialogLayout.findViewById(R.id.user_image);

            final TextView txt_username = (TextView) dialogLayout.findViewById(R.id.txt_username);

            final EditText edit_review = (EditText) dialogLayout.findViewById(R.id.edit_review);

            final Button btn_cancel = (Button) dialogLayout.findViewById(R.id.butcancel);
            ImageView close = (ImageView) dialogLayout.findViewById(R.id.close);

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            //  str_provider_name=txt_provider_name.getText().toString();

            //str_des=et_reviews.getText().toString();
            JSONObject jsobj = null;
            try {
                jsobj = new JSONObject(strTitle);
                txt_username.setText(jsobj.getString("username"));

                final String str_id = jsobj.getString("userid");
                final String strRow_id = jsobj.getString("id");


                Log.e("userid", str_id);
                Log.e("username", jsobj.getString("username"));

                String strPath = jsobj.getString("image_path");

                Log.i("FF", "strimage" + strPath.length());

                if (strPath.length() == 0) {
                    user_image.setBackgroundResource(R.drawable.default_user_icon);
                } else {

                    String strimage = gD.common_baseurl+"upload/" + jsobj.getString("image_path");

                    imgLoader.DisplayImage(strimage, user_image);

                    //user_image.setImageBitmap(imgLoader.getBitmap(strimage));

                }
                btn_submit_review.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        str_rating = String.valueOf(ratingBar.getRating());
                        str_des = edit_review.getText().toString();


                        Log.i("HHB", "Rating : " + ratingBar.getRating());
                        if (!str_rating.equalsIgnoreCase("0.0") && str_des.length() > 0) {


                            Log.i("HHB", "str_des : " + str_des);
                           // Toast.makeText(ctx, "str_des"+str_des, Toast.LENGTH_SHORT).show();

                            ReviewUploadTask uploadTask = new ReviewUploadTask(str_id, strRow_id, str_rating, str_des);
                           uploadTask.execute();

                        } else {
                            if (str_rating.equals("0.0")) {
                                Toast.makeText(ProviderHistoryPage.this, "Rating should not be zero !", Toast.LENGTH_SHORT).show();
                            } else if (str_des.length() == 0) {
                                Toast.makeText(ProviderHistoryPage.this, "Comment should not be empty !", Toast.LENGTH_SHORT).show();
                            }
                        }
                        // Toast.makeText(Provider_Pay.this, " "+str_rating+" "+str_id+"- "+str_des, Toast.LENGTH_SHORT).show();


                    }
                });






            } catch (JSONException e) {
                e.printStackTrace();
            }






            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    alertDialog.dismiss();



                }
            });














        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void serviceendid(String id, String strTitle) {

    }

    @Override
    public void navigatepage(String iscomplete, String paidstatus, String generate, String reached, String pse, String review, String strtype, String strresponse, String leave, String responseval) {

    }

    @Override
    public void paidstatus(String id, String strTitle, String payservices, String rate, String name, String image, String prid) {

    }


    @Override
    public void onBackPressed() {
        Intent i = new Intent(ProviderHistoryPage.this, Provider_DashBoard.class);
        startActivity(i);
        finish();
    }

    class GetProviderDetails extends AsyncTask {
        String user_id = null;
        String sResponse = null;
        Context cont;
        RecyclerView recycler;

        public GetProviderDetails(String userid, Context context, RecyclerView recyclerView) {
            this.user_id = userid;
            this.cont = context;
            this.recycler = recyclerView;

        }

        @Override
        protected void onPreExecute() {
            gD.callload(ctx, nScreenHeight);




        }

        @Override
        protected Object doInBackground(Object[] param) {
            try {
                String charset = "UTF-8";
                String requestURL = gD.common_baseurl+"providerhistory.php";
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
                    Log.i("BBB", "StrResp : " + jsonObj.toString());
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

                String name = null, image = null, time = null, date = null;
                ArrayList beanArrayList = new ArrayList<UserHistoryBean>();

                JSONObject jsobj = new JSONObject(sResponse);
                JSONObject providersServiceJSONobject = null;

                Log.i("HH", "strResp : " + sResponse);
                if (jsobj.getString("status").equalsIgnoreCase("success")) {
                    JSONArray providerServicesJSONArray = jsobj.getJSONArray("today");
                    providerServicesPrevWeek = jsobj.getJSONArray("last week");
                    providerServicesYesterday = jsobj.getJSONArray("yesterday");
                    providerServicesToday = jsobj.getJSONArray("today");
                    providerServicesAll = jsobj.getJSONArray("all");

                    if (providerServicesJSONArray.length() > 0) {
                        for (int i = 0; i < providerServicesJSONArray.length(); i++) {

                            UserHistoryBean userHistoryBean = new UserHistoryBean();
                            providersServiceJSONobject = providerServicesToday.getJSONObject(i);

                            Log.i("GG", "providersServiceJSONobject : " + providersServiceJSONobject);

                            userHistoryBean.setStr_shop_name(providersServiceJSONobject.getString("username"));
                            userHistoryBean.setStr_address(providersServiceJSONobject.getString("companyaddress"));
                            userHistoryBean.setStr_toaccept(providersServiceJSONobject.getString("iscomplete"));
                            Log.i("HH", "toaccept : " + providersServiceJSONobject.getString("toaccept"));
                            userHistoryBean.setStr_isaccept(providersServiceJSONobject.getString("isaccept"));
                            Log.i("HH", "isaccept : " + providersServiceJSONobject.getString("isaccept"));
                            userHistoryBean.setStr_response(providersServiceJSONobject.getString("apptstatus"));

                            userHistoryBean.setStr_services(providersServiceJSONobject.getString("service"));
                            userHistoryBean.setStr_date_time(providersServiceJSONobject.getString("date") + " " + providersServiceJSONobject.getString("starttime"));
                            userHistoryBean.setStr_price("$ " + providersServiceJSONobject.getString("rate"));
                            userHistoryBean.setStr_user_img(gD.common_baseurl+"upload/" + providersServiceJSONobject.getString("image_path"));

                            Log.i("mA", "Get Val : " + providersServiceJSONobject.toString());
                            userHistoryBean.setStr_jsonvalue(providersServiceJSONobject.toString());
                            beanArrayList.add(userHistoryBean);
                        }
                    } else {
                        emptyText.setVisibility(View.VISIBLE);
                    }
                }

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recycler.setLayoutManager(mLayoutManager);
                recycler.setItemAnimator(new DefaultItemAnimator());
               /* recycler.addItemDecoration(new com.aryvart.utician.adapter.DividerItemDecoration(ProviderHistoryPage.this, LinearLayoutManager.VERTICAL));*/

                ProviderHistoryAdapter userHistoryAdpater = new ProviderHistoryAdapter(beanArrayList, ProviderHistoryPage.this, (GetBookingId) ctx);
                recycler.setAdapter(userHistoryAdpater);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    //review page

    class ReviewUploadTask extends AsyncTask {

        String sResponse = null;

        String strID, strrowID, strrating, strdescription;
        AlertDialog aD;

        public ReviewUploadTask(String str_id, String strRow_id, String str_rating, String str_des) {
            strID = str_id;
            strrowID = strRow_id;
            strrating = str_rating;
            strdescription = str_des;
          //  aD = altDialog;
        }


        @Override
        protected Object doInBackground(Object[] params) {
            try {


                String charset = "UTF-8";

                String requestURL = gD.common_baseurl+"writereviewpro.php";


                // 4. separate class for multipart content image uploaded task----------- vinoth
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                multipart.addHeaderField("Content-Encoding", "gzip");
                multipart.addHeaderField("User-Agent", "rajaji");
                multipart.addHeaderField("Test-Header", "Header-Value");


                multipart.addFormField("keywords", "Java,upload,Spring");

                // 5. set the user_image key word and multipart image file value ----------- vinoth

                multipart.addFormField("description", strdescription);
                multipart.addFormField("rating", strrating);

                multipart.addFormField("userid", strID);
                multipart.addFormField("id", strrowID);
                multipart.addFormField("providerid", SplashActivity.sharedPreferences.getString("UID", null));

                Log.e("description", strdescription);
                Log.e("rating", strrating);
                Log.e("userid", strID);
                Log.e("providerid", SplashActivity.sharedPreferences.getString("UID", null));

                // 6. upload finish ----------- vinoth
                List<String> response = multipart.finish();

                System.out.println("SERVER REPLIED:");

                StringBuilder sb = new StringBuilder();
                for (String line : response) {
                    System.out.println(line);
                    // 6. get the server response for image upload ----------- vinoth
                    sb.append(line);
                }


                try {

                    JSONObject jsonObj = new JSONObject(sb.toString());

                    Log.i("GGG", "StrResp : " + jsonObj.toString());

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
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            ProviderHistoryPage.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gD.callload(ctx, nScreenHeight);



                }
            });
        }

        @Override
        protected void onPostExecute(Object sesponse) {
            try {
                if (sResponse != null) {



                    if(gD.alertDialog!=null) {
                        gD.alertDialog.dismiss();
                    }
                    JSONObject jsobj = new JSONObject(sResponse);

                    if (jsobj.getInt("code") == 2) {


                        LayoutInflater inflater = LayoutInflater.from(ctx);
                        View dialogLayout = inflater.inflate(R.layout.layout_confirmation, null);

                        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(ctx);
                        alertDialogBuilder.setView(dialogLayout);
                        alertDialogBuilder.setCancelable(false);


                        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                        //  alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        alertDialog.show();

                        LinearLayout llayAlert = (LinearLayout) dialogLayout.findViewById(R.id.llayalertDialog);
                        // TextView tv_alert_Title = (TextView) dialogLayout.findViewById(R.id.tv_alert_Title);
                        TextView tv_alert_Message = (TextView) dialogLayout.findViewById(R.id.message);

                        tv_alert_Message.setText("Review posted successfully");

                        Button btn_submit = (Button) dialogLayout.findViewById(R.id.ok);
                        Button btn_cancel = (Button) dialogLayout.findViewById(R.id.cancel);


                        FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, nScreenHeight);
                        llayAlert.setLayoutParams(lparams);
                        int count=1;
                        if(count==1)
                        {
                            btn_cancel.setVisibility(View.GONE);
                        }


                        btn_submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i=new Intent(ProviderHistoryPage.this,ProviderHistoryPage.class);
                                startActivity(i);



                                finish();
                                alertDialog.dismiss();


                            }
                        });





                    }
                } else {



                    if(gD.alertDialog!=null) {
                        gD.alertDialog.dismiss();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

}


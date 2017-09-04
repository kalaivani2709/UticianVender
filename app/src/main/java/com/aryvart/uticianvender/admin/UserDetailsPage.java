package com.aryvart.uticianvender.admin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.bean.ProviderNotificationBean;
import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.aryvart.uticianvender.genericclasses.MultipartUtility;
import com.aryvart.uticianvender.imageCache.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Androidwin on 5/16/2017.
 */
public class UserDetailsPage extends Activity {
    ImageView menu_back;
    TextView user_name,phone_num,paypalid,email_add,preference,title,btn_Delete;
    Context context;
    ImageLoader imgLoader;
    ImageView useriamge,providerimage;
    String str_jsonval;
    String timezonevalue;
    String id,type;
    GeneralData gD;
    int nScreenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.userdetailpage);

            context = this;
            gD = new GeneralData(context);
            imgLoader = new ImageLoader(context);
            useriamge = (ImageView) findViewById(R.id.user_image);


            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = (int) ((float) nHeight / (float) 2.1);

            TimeZone tz = TimeZone.getDefault();
            System.out.println("TimeZone " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

            timezonevalue=tz.getID();
            Log.i("TZ", "timezone" + timezonevalue);


            str_jsonval = getIntent().getStringExtra("jsonvalues");

            Log.i("UD", "jsonvalues" + str_jsonval);

            menu_back = (ImageView) findViewById(R.id.menu_back);
            menu_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(UserDetailsPage.this, UserProviderList.class);
                    startActivity(i);
                    finish();
                }
            });


            user_name = (TextView) findViewById(R.id.user_name);

            paypalid = (TextView) findViewById(R.id.paypalid);
            phone_num = (TextView) findViewById(R.id.phone_num);
            email_add = (TextView) findViewById(R.id.email_add);
            preference = (TextView) findViewById(R.id.preference);

            title = (TextView) findViewById(R.id.title);
            btn_Delete = (TextView) findViewById(R.id.delete);





            try {
                JSONObject jsobj = new JSONObject(str_jsonval);

                Log.i("UD","ddddd---"+jsobj.toString());

                user_name.setText(jsobj.getString("fullname"));
                paypalid.setText(jsobj.getString("paypal_id"));
                phone_num.setText( jsobj.getString("phonenumber"));
                email_add.setText(jsobj.getString("email"));

id=jsobj.getString("id");
                type=jsobj.getString("usertype");
                Log.i("UD","id"+id);
                Log.i("UD","type"+type);
                if(jsobj.getString("usertype").equalsIgnoreCase("Provider")){
                    preference.setText(jsobj.getString("propreference"));
                    title.setText("Utician Details");
                }
                                else{
                    preference.setText(jsobj.getString("preference"));
                    title.setText("User Details");
                                }



                String strPathprovider = jsobj.getString("image_path");

                if (strPathprovider.length() == 0) {
                    useriamge.setBackgroundResource(R.drawable.default_user_icon);
                } else {

                    String strimages = gD.common_baseurl+"upload/" + jsobj.getString("image_path");

                    useriamge.setImageBitmap(imgLoader.getBitmap(strimages));

                }

                btn_Delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        GetDeleteaccount getdelete = new GetDeleteaccount(context,id,type);
                        getdelete.execute();
                    }
                });


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

        Intent i = new Intent(UserDetailsPage.this, UserProviderList.class);
        startActivity(i);
        finish();

    }
    class GetDeleteaccount extends AsyncTask {
        String user_id = null;
        String usertype = null;

        Context cont;
        RecyclerView recycler;
        String sResponse = null;

        public GetDeleteaccount(Context context, String userid, String usertype) {
            this.user_id = userid;
            this.cont = context;
            this.usertype = usertype;


        }

        @Override
        protected void onPreExecute() {
            UserDetailsPage.this.runOnUiThread(new Runnable() {
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
                String requestURL = gD.common_baseurl+"user_utician_delete.php";
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);

                multipart.addHeaderField("Content-Encoding", "gzip");



                Log.i("UD","user_id"+user_id);
                Log.i("UD","usertype"+usertype);


                multipart.addFormField("id", user_id.trim());
                multipart.addFormField("type", usertype.trim());
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

                Log.i("HH", "strResp : " + sResponse);


                if (jsobj.getInt("code") == 2)

                {


                    LayoutInflater inflater = LayoutInflater.from(cont);
                    View dialogLayout = inflater.inflate(R.layout.layout_confirmation, null);

                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(cont);
                    alertDialogBuilder.setView(dialogLayout);
                    alertDialogBuilder.setCancelable(false);


                    final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                    //  alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    alertDialog.show();

                    LinearLayout llayAlert = (LinearLayout) dialogLayout.findViewById(R.id.llayalertDialog);
                    // TextView tv_alert_Title = (TextView) dialogLayout.findViewById(R.id.tv_alert_Title);
                    TextView tv_alert_Message = (TextView) dialogLayout.findViewById(R.id.message);

                    tv_alert_Message.setText( jsobj.getString("status"));

                    Button btn_submit = (Button) dialogLayout.findViewById(R.id.ok);
                    Button btn_cancel = (Button) dialogLayout.findViewById(R.id.cancel);


                    FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, nScreenHeight);
                    llayAlert.setLayoutParams(lparams);

                    int count = 1;
                    if (count == 1) {
                        btn_cancel.setVisibility(View.GONE);
                    }


                    btn_submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent NextScreenIntent = new Intent(UserDetailsPage.this, UserProviderList.class);
                            startActivity(NextScreenIntent);
                            finish();;
                            alertDialog.dismiss();


                        }
                    });


                }








            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }





}


package com.aryvart.uticianvender.utician;

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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.bean.commonBeanSupport;
import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.aryvart.uticianvender.genericclasses.MultipartUtility;
import com.aryvart.uticianvender.imageCache.ImageLoader;
import com.aryvart.uticianvender.provider.Provider_DashBoard;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android3 on 7/11/16.
 */
public class Share_Layout extends Activity {
    TextView txt_code,refer_code;
    TextView btn_invote_friends;
    GeneralData gD;
    Context context;
    ProgressDialog pD;
    LinearLayout llayback;
    int nScreenHeight;
    String strtype;
    ImageLoader imgLoader;
    ImageView user_image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_layout);
        txt_code=(TextView)findViewById(R.id.text_code);
      refer_code=(TextView)findViewById(R.id.referal_count);
        context = this;
        gD = new GeneralData(context);
        imgLoader = new ImageLoader(context);
        user_image = (ImageView) findViewById(R.id.user_image);


        SplashActivity.sharedPreferences = getSharedPreferences("regid", Context.MODE_PRIVATE);

        SplashActivity.autoLoginPreference = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);




        imgLoader.DisplayImage(SplashActivity.sharedPreferences.getString("user_image", null), user_image);


        DisplayMetrics dp = getResources().getDisplayMetrics();
        int nHeight = dp.heightPixels;
        nScreenHeight = (int) ((float) nHeight / (float) 2.1);
        strtype = SplashActivity.sharedPreferences.getString("utype", null);
        Log.i("YY","strytpe" +strtype);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.app_green));
        }

        pD = new ProgressDialog(Share_Layout.this);
        ReferalcodeDisplay referalcodeTask = new ReferalcodeDisplay(SplashActivity.sharedPreferences.getString("UID", null), context);
        referalcodeTask.execute();
        SplashActivity.sharedPreferences = getSharedPreferences("regid", Context.MODE_PRIVATE);

        llayback = (LinearLayout) findViewById(R.id.llayBack);
        llayback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Intent i;
                    finish();

                    if (strtype.equalsIgnoreCase("Provider"))
                    {
                        i = new Intent(Share_Layout.this, Provider_DashBoard.class);
                        //  gD.screenback = 0;
                        startActivity(i);

                    }
                    else
                    {

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        btn_invote_friends=(TextView)findViewById(R.id.btn_invite);

        btn_invote_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!gD.isConnectingToInternet()) {
                    Toast.makeText(Share_Layout.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                } else {
                    ReferalcodeTask referalcodeTask = new ReferalcodeTask(SplashActivity.sharedPreferences.getString("UID", null), context);
                    referalcodeTask.execute();


                }
            }
        });

    }
    class ReferalcodeTask extends AsyncTask {
        String userid = null;
        String sResponse = null;
        Context cont;
        RecyclerView recycler;

        public ReferalcodeTask(String providerid, Context context) {
            this.userid = providerid;
            this.cont = context;

        }

        @Override
        protected void onPreExecute() {
            Share_Layout.this.runOnUiThread(new Runnable() {
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
                String requestURL = gD.common_baseurl+"referallink.php";
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);

                multipart.addHeaderField("Content-Encoding", "gzip");
                multipart.addFormField("userid", userid);
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
            try {
                String name = null, image = null, time = null, date = null;
                ArrayList beanArrayList = new ArrayList<commonBeanSupport>();
                JSONObject jsobj = new JSONObject(sResponse);

                Log.i("HH", "strResp : " + sResponse);

                if (jsobj.getString("status").equalsIgnoreCase("success")) {

                    Intent intent = new Intent(Intent.ACTION_SEND);

                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_SUBJECT, "Check out this site!");

                    intent.putExtra(Intent.EXTRA_TEXT, jsobj.getString("name") + " has given you a discount off your first service (%10 discount) to claim your free gift, sign up using this link: " + jsobj.getString("shareurl"));



                    startActivity(Intent.createChooser(intent, "Share"));


                }



                if(gD.alertDialog!=null) {
                    gD.alertDialog.dismiss();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onBackPressed() {
        try {
            Intent i;
            finish();

            if (strtype.equalsIgnoreCase("Provider"))
            {
                i = new Intent(Share_Layout.this, Provider_DashBoard.class);
                //  gD.screenback = 0;
                startActivity(i);


            }
            else
            {

            }



            super.onBackPressed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//display codeig
class ReferalcodeDisplay extends AsyncTask {
    String userid = null;
    String sResponse = null;
    Context cont;
    RecyclerView recycler;

    public ReferalcodeDisplay(String providerid, Context context) {
        this.userid = providerid;
        this.cont = context;

    }

    @Override
    protected void onPreExecute() {
        Share_Layout.this.runOnUiThread(new Runnable() {
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
            String requestURL = gD.common_baseurl+"referallink.php";
            MultipartUtility multipart = new MultipartUtility(requestURL, charset);

            multipart.addHeaderField("Content-Encoding", "gzip");
            multipart.addFormField("userid", userid);
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
        try {
            String name = null, image = null, time = null, date = null;
            ArrayList beanArrayList = new ArrayList<commonBeanSupport>();
            JSONObject jsobj = new JSONObject(sResponse);

            Log.i("HH", "strResp : " + sResponse);

            if (jsobj.getString("status").equalsIgnoreCase("success")) {

               txt_code.setText(jsobj.getString("referalcode"));
                refer_code.setText(jsobj.getString("referalcount"));

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

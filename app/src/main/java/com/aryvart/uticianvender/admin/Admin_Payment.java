package com.aryvart.uticianvender.admin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.genericclasses.AlertDialogManager;
import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.aryvart.uticianvender.genericclasses.MultipartUtility;
import com.aryvart.uticianvender.utician.SplashActivity;

import org.json.JSONObject;

import java.util.List;
import java.util.TimeZone;

/**
 * Created by android3 on 22/9/16.
 */
public class Admin_Payment extends Activity {
    ImageView img_back;
    TextView ll_next;

    String timezonevalue;


    AlertDialogManager alertDialog;
    Context context;

    GeneralData gD;
    int nScreenHeight;
    String str_get_merchantId, str_get_publicKey, str_get_privateKey, str_get_merchantaccountid;

    EditText ed_merchantId, ed_publicKey, ed_privateKey, ed_merchantaccountid;


    private ProgressDialog pdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.admin_payment);
            img_back = (ImageView) findViewById(R.id.img_back);
            ll_next = (TextView) findViewById(R.id.llayNext);
            context = this;

            TimeZone tz = TimeZone.getDefault();
            System.out.println("TimeZone " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

            timezonevalue=tz.getID();
            Log.i("TZ", "timezone" + timezonevalue);
            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = (int) ((float) nHeight / (float) 2.2);
            SplashActivity.sharedPreferences = getSharedPreferences("regid", Context.MODE_PRIVATE);


            gD = new GeneralData(context);
            pdialog = new ProgressDialog(context);

            SplashActivity.autoLoginPreference = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
            alertDialog = new AlertDialogManager();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.app_green));
            }

            ed_merchantId = (EditText) findViewById(R.id.ed_merchantId);
            ed_publicKey = (EditText) findViewById(R.id.ed_publicKey);
            ed_privateKey = (EditText) findViewById(R.id.ed_privateKey);
            ed_merchantaccountid = (EditText) findViewById(R.id.ed_merchantaccountid);
            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(context, "Unable to connect with Internet. Please check your internet connection and try again");
            }else {
                AfterLoginTask aft = new AfterLoginTask();
                aft.execute();
            }

            img_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    SharedPreferences.Editor editor = SplashActivity.autoLoginPreference.edit();
                    editor.putString("uname", null);
                    editor.putString("pwd", null);
                    editor.commit();
                    finish();

                    Intent i = new Intent(context, AdminList.class);
                    //  gD.screenback = 0;
                    startActivity(i);
                }
            });
            ll_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  str_get_paypal_id=ed_paypal_id.getText().toString().trim();


                    str_get_merchantId = ed_merchantId.getText().toString().trim();
                    str_get_publicKey = ed_publicKey.getText().toString().trim();
                    str_get_privateKey = ed_privateKey.getText().toString().trim();

                    str_get_merchantaccountid = ed_merchantaccountid.getText().toString().trim();


                    //  Log.i("paypal_id", str_get_paypal_id);

                    if (str_get_merchantId.length() > 0 && str_get_publicKey.length() > 0 && str_get_privateKey.length() > 0 && str_get_merchantaccountid.length() > 0) {


                        AdminPaymentEdit APT = new AdminPaymentEdit();
                        APT.execute();


                    } else {
                        if (str_get_merchantId.length() == 0) {
                            Toast.makeText(Admin_Payment.this, "First name can't be empty!", Toast.LENGTH_SHORT).show();
                        } else if (str_get_publicKey.length() == 0) {
                            Toast.makeText(Admin_Payment.this, "Last name can't be empty!", Toast.LENGTH_SHORT).show();
                        } else if (str_get_privateKey.length() == 0) {
                            Toast.makeText(Admin_Payment.this, "Address can't be empty!", Toast.LENGTH_SHORT).show();
                        } else if (str_get_merchantaccountid.length() == 0) {
                            Toast.makeText(Admin_Payment.this, "Taxid can't be empty!", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });
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


        try {
            SharedPreferences.Editor editor = SplashActivity.autoLoginPreference.edit();
            editor.putString("uname", null);
            editor.putString("pwd", null);
            editor.commit();
            finish();
            Intent i = new Intent(context, AdminList.class);
            //  gD.screenback = 0;
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class AfterLoginTask extends AsyncTask {
        String sResponse = null;

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                String charset = "UTF-8";
                String requestURL = gD.common_baseurl+"adminbtcredientials.php";
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);


                multipart.addHeaderField("Content-Encoding", "gzip");

                multipart.addHeaderField("Accept", "application*/*");
                multipart.addHeaderField("Content-type", "application/x-www-form-urlencoded");


                List<String> response = multipart.finish();
                System.out.println("SERVER REPLIED:");
                StringBuilder sb = new StringBuilder();
                for (String line : response) {
                    System.out.println(line);
                    sb.append(line);
                }
                try {
                    JSONObject jsonObj = new JSONObject(sb.toString());
                    Log.i("SSK", "StrResp : " + jsonObj.toString());
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
            gD.callload(context, nScreenHeight);




        }

        @Override
        protected void onPostExecute(Object sesponse) {
            try {
                JSONObject jsobj = new JSONObject(sResponse);
                if (jsobj.getInt("code") == 2) {
                    JSONObject jsArProviders = jsobj.getJSONObject("admincredientials");
                    String merchantId = jsArProviders.getString("merchantId");
                    String publicKey = jsArProviders.getString("publicKey");
                    String privateKey = jsArProviders.getString("privateKey");
                    String merchantaccountid = jsArProviders.getString("merchantaccountid");


                    ed_merchantId.setText(merchantId);
                    ed_publicKey.setText(publicKey);
                    ed_privateKey.setText(privateKey);
                    ed_merchantaccountid.setText(merchantaccountid);


                }




                if(gD.alertDialog!=null) {
                    gD.alertDialog.dismiss();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


    class AdminPaymentEdit extends AsyncTask {

        String sResponse = null;


        public AdminPaymentEdit() {

        }


        @Override
        protected Object doInBackground(Object[] params) {
            try {


                String charset = "UTF-8";

                String requestURL = gD.common_baseurl+"braintree/html/adminbraintree.php";


                // 4. separate class for multipart content image uploaded task----------- vinoth
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                multipart.addHeaderField("Content-Encoding", "gzip");
                multipart.addHeaderField("Accept", "application*/*");
                multipart.addHeaderField("Content-type", "application/x-www-form-urlencoded");

                multipart.addFormField("merchantId", str_get_merchantId);
                multipart.addFormField("privateKey", str_get_privateKey);
                multipart.addFormField("publicKey", str_get_publicKey);
                multipart.addFormField("merchantaccountid", str_get_merchantaccountid);

                Log.i("KK", "merchantaccountid" + str_get_merchantaccountid);
                Log.i("KK", "publicKey" + str_get_publicKey);
                Log.i("KK", "privateKey" + str_get_privateKey);
                Log.i("KK", "merchantId" + str_get_merchantId);


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

                    Log.i("SSS", "StrResp : " + jsonObj.toString());

                    sResponse = jsonObj.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                }


            } catch (Exception e) {

                Log.i("NN", "i'm catched");
                e.printStackTrace();
            }


            return sResponse;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            gD.callload(context, nScreenHeight);



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


                        LayoutInflater inflater = LayoutInflater.from(context);
                        View dialogLayout = inflater.inflate(R.layout.layout_confirmation, null);

                        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
                        alertDialogBuilder.setView(dialogLayout);
                        alertDialogBuilder.setCancelable(false);


                        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                        //  alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        alertDialog.show();

                        LinearLayout llayAlert = (LinearLayout) dialogLayout.findViewById(R.id.llayalertDialog);
                        // TextView tv_alert_Title = (TextView) dialogLayout.findViewById(R.id.tv_alert_Title);
                        TextView tv_alert_Message = (TextView) dialogLayout.findViewById(R.id.message);

                        tv_alert_Message.setText("You successfully added your information");

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


                                Intent user_category = new Intent(Admin_Payment.this, AdminList.class);

                                startActivity(user_category);
                                finish();

                            }

                        });


                    } else if (jsobj.getInt("code") == 3) {
                        Toast.makeText(Admin_Payment.this, jsobj.getString("message"), Toast.LENGTH_SHORT).show();
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


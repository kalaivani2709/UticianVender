package com.aryvart.uticianvender.provider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
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

/**
 * Created by android3 on 10/11/16.
 */
public class Provider_paypalid_edit_screen extends Activity {
    ImageView img_back;
    TextView ll_next;
    EditText ed_paypal_id;
    String str_get_paypal_id;

    AlertDialogManager alertDialog;
    Context context;
    GeneralData gD;
    int nScreenHeight;
    TextView txt_headername;
    String strtype;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.provider_companyinfo_paypal);
            img_back=(ImageView)findViewById(R.id.img_back);
            ll_next=(TextView)findViewById(R.id.llayNext);
            context=this;
            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = (int) ((float) nHeight / (float) 2.1);
            SplashActivity.sharedPreferences = getSharedPreferences("regid", Context.MODE_PRIVATE);
            txt_headername=(TextView)findViewById(R.id.header_name);
            strtype = SplashActivity.sharedPreferences.getString("utype", null);
            Log.i("YY","strytpe" +strtype);
            Log.i("KK", "userttyep" + SplashActivity.sharedPreferences.getString("utype", null));

          if(SplashActivity.sharedPreferences.getString("utype", null).equalsIgnoreCase("User")) {
              txt_headername.setText("Client Information");
          }
            else
          {
              txt_headername.setText("Utician Information");
          }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.app_green));
            }

            gD = new GeneralData(context);
            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(context, "Unable to connect with Internet. Please check your internet connection and try again");
            }else {
                AfterLoginTask aft = new AfterLoginTask();
                aft.execute();
            }
            SplashActivity.autoLoginPreference = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
            alertDialog = new AlertDialogManager();
            ed_paypal_id=(EditText)findViewById(R.id.ed_paypal_id);
            img_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i;
                    finish();

                    if (strtype.equalsIgnoreCase("Provider"))
                    {
                        i = new Intent(Provider_paypalid_edit_screen.this, Provider_Profile_Edit.class);
                        //  gD.screenback = 0;
                        startActivity(i);

                    }
                    else
                    {
                       // i = new Intent(Provider_paypalid_edit_screen.this, User_Category.class);
                    }


                }
            });


            ed_paypal_id.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {


                    if (actionId == EditorInfo.IME_ACTION_DONE) {

                    callevent();

                        return true;
                    }
                    return false;
                }
            });









            ll_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callevent();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


public  void callevent()
{
    try {
        if (!gD.isConnectingToInternet()) {
            Toast.makeText(Provider_paypalid_edit_screen.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
        } else {

            str_get_paypal_id=ed_paypal_id.getText().toString().trim();

            Log.i("paypal_id", str_get_paypal_id);

            if (str_get_paypal_id.length() > 0) {

                if (gD.isValidEmail(str_get_paypal_id)) {

                    ProviderCompanyInfoPayTask pCT = new ProviderCompanyInfoPayTask(str_get_paypal_id);
                    pCT.execute();
                }



                else {

                    Toast.makeText(getApplicationContext(), "Enter the valid email id !", Toast.LENGTH_SHORT).show();

                }



            } else {
                if (str_get_paypal_id.length() == 0) {
                    Toast.makeText(Provider_paypalid_edit_screen.this, "Paypay ID can't be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

}
    class ProviderCompanyInfoPayTask extends AsyncTask {

        String sResponse = null;

        String str_paypal, str_social_sec, strMulimages;

        public ProviderCompanyInfoPayTask(String strPaypal) {
            str_paypal = strPaypal;
        }


        @Override
        protected Object doInBackground(Object[] params) {
            try {


                String charset = "UTF-8";

                String requestURL = gD.common_baseurl+"editpaypalinfo.php";


                // 4. separate class for multipart content image uploaded task----------- vinoth
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);

                multipart.addHeaderField("Content-Encoding", "gzip");

                // 5. set the user_image key word and multipart image file value ----------- vinoth

                // strCname, strCaddress, strNemp, strMulimages, strLicenseupload, strSsecurityNo

                Log.i("TT", "paypalid : " + str_paypal);
                multipart.addFormField("paypalid", str_paypal);


                //multipart.addFormField("providerid", SplashActivity.sharedPreferences.getString("UID", null));

                multipart.addFormField("id", SplashActivity.sharedPreferences.getString("UID", null));

                //  Log.i("HH", " strrealpath : " + strrealpath + "**********" + strrealpath.trim().length());



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
                    if (jsobj.getInt("code") == 0) {
                        gD.showAlertDialog(context, "", "Your paypal id already exists", nScreenHeight, 1);

                    }
                  else  if (jsobj.getInt("code") == 2) {
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

                        tv_alert_Message.setText("Your paypal Information changed Successfully");

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


                                finish();

                            }

                        });


                    }
                }else {


                    if(gD.alertDialog!=null) {
                        gD.alertDialog.dismiss();
                    }
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
                i = new Intent(Provider_paypalid_edit_screen.this, Provider_Profile_Edit.class);
                //  gD.screenback = 0;

                startActivity(i);

            }
            else
            {
             //   i = new Intent(Provider_paypalid_edit_screen.this, User_Category.class);
            }


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
                String requestURL = gD.common_baseurl+"paypaldata.php";
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                multipart.addHeaderField("Content-Encoding", "gzip");
                multipart.addHeaderField("Accept", "application*/*");
                multipart.addHeaderField("Content-type", "application/x-www-form-urlencoded");

                multipart.addFormField("id", SplashActivity.sharedPreferences.getString("UID", null));

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

                    String paypalid = jsobj.getString("data");

                    ed_paypal_id.setText(paypalid);



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
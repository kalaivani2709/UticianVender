package com.aryvart.uticianvender.provider;

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
import com.aryvart.uticianvender.utician.SignInActivity;
import com.aryvart.uticianvender.utician.SplashActivity;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by android3 on 4/11/16.
 */
public class Provider_Paypalid_Screen extends Activity {
    ImageView img_back;
    TextView ll_next;
    EditText ed_paypal_id;
    String str_get_paypal_id;
    private ProgressDialog pdialog;
    AlertDialogManager alertDialog;
    Context context;
    GeneralData gD;
    int nScreenHeight,nnScreenHeight;
    TextView txt_headername;
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
            nScreenHeight = (int) ((float) nHeight / (float) 1.7);
            nnScreenHeight = (int) ((float) nHeight / (float) 2.3);
            SplashActivity.sharedPreferences = getSharedPreferences("regid", Context.MODE_PRIVATE);
            txt_headername=(TextView)findViewById(R.id.header_name);
            txt_headername.setText("Utician Information");


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.app_green));
            }
            gD = new GeneralData(context);
            pdialog = new ProgressDialog(context);
            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(context, "Unable to connect with Internet. Please check your internet connection and try again");
            }
            SplashActivity.autoLoginPreference = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
            alertDialog = new AlertDialogManager();
            ed_paypal_id=(EditText)findViewById(R.id.ed_paypal_id);

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



            img_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    popupcall("Do you want to logout",0,"back",nnScreenHeight);
                   // finish();

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

public void callevent()
    {
        try {
            if (!gD.isConnectingToInternet()) {
                Toast.makeText(Provider_Paypalid_Screen.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(Provider_Paypalid_Screen.this, "Paypay ID can't be empty!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    class ProviderCompanyInfoPayTask extends AsyncTask {

        String sResponse = null;

        String str_paypal;

        public ProviderCompanyInfoPayTask(String strPaypal) {
            str_paypal = strPaypal;
        }


        @Override
        protected Object doInBackground(Object[] params) {
            try {


                String charset = "UTF-8";

                String requestURL = gD.common_baseurl+"paypalinfo.php";


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

                    if (jsobj.getInt("code") == 2) {
                        String strservicetype = jsobj.getJSONObject("registered_user").getString("services");
                        SharedPreferences.Editor editor = SplashActivity.sharedPreferences.edit();
                        editor.putString("serviceType", strservicetype);
                        editor.commit();




























                        popupcall("You have successfully registered as a Utician, please allow us some time to review your account and issue your approval.",1,"from_api", nScreenHeight);


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
        popupcall("Do you want to logout",0,"back", nnScreenHeight);

       /* SharedPreferences.Editor editor = SplashActivity.autoLoginPreference.edit();
        editor.putString("uname", null);
        editor.putString("pwd", null);
        editor.commit();
        finish();*/
    }



    public void popupcall(String s, int i, final String str_res, int size)
    {


        try {
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialogLayout = inflater.inflate(R.layout.layout_confirmation, null);

            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
            alertDialogBuilder.setView(dialogLayout);
            alertDialogBuilder.setCancelable(false);


            final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            //  alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            alertDialog.show();

            LinearLayout llayAlert = (LinearLayout) dialogLayout.findViewById(R.id.llayalertDialog);
             TextView tv_alert_Title = (TextView) dialogLayout.findViewById(R.id.add_msg);
            TextView tv_alert_Message = (TextView) dialogLayout.findViewById(R.id.message);

            tv_alert_Message.setText(s);

            Button btn_submit = (Button) dialogLayout.findViewById(R.id.ok);
            Button btn_cancel = (Button) dialogLayout.findViewById(R.id.cancel);


            FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, size);
            llayAlert.setLayoutParams(lparams);
            // int count = 1;
            if (i == 1) {
                btn_submit.setText("OK");
                tv_alert_Title.setVisibility(View.VISIBLE);
                tv_alert_Title.setText("(Review usually takes anywhere between 24-72 hours)");
                btn_cancel.setVisibility(View.GONE);
            }
            else
            {
                btn_submit.setText("YES");
                btn_cancel.setText("NO");

                tv_alert_Title.setVisibility(View.GONE);
                btn_submit.setVisibility(View.VISIBLE);
                btn_cancel.setVisibility(View.VISIBLE);
            }


            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(str_res.equalsIgnoreCase("from_api"))
                    {
                        Intent user = new Intent(Provider_Paypalid_Screen.this, SignInActivity.class);
                        startActivity(user);
                        SharedPreferences.Editor editor = SplashActivity.autoLoginPreference.edit();
                        editor.putString("uname", null);
                        editor.putString("pwd", null);
                        editor.commit();
                        finish();
                        finishAffinity();

                        alertDialog.dismiss();

                    }
                    else
                    {
                        SharedPreferences.Editor editor = SplashActivity.autoLoginPreference.edit();
                        editor.putString("uname", null);
                        editor.putString("pwd", null);
                        editor.commit();

                        Intent user_category = new Intent(Provider_Paypalid_Screen.this, SignInActivity.class);

                        startActivity(user_category);
                        finish();
                        alertDialog.dismiss();
                    }



                }

            });


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
}
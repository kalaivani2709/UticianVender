package com.aryvart.uticianvender.utician;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryvart.uticianvender.Interface.GetNotification;
import com.aryvart.uticianvender.Interface.OAuthAuthenticationListener;
import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.admin.AdminList;
import com.aryvart.uticianvender.genericclasses.AlertDialogManager;
import com.aryvart.uticianvender.genericclasses.Data;
import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.aryvart.uticianvender.newGCM.GCMClientManager;
import com.aryvart.uticianvender.provider.AddHoroscope;
import com.aryvart.uticianvender.provider.ProviderAddServices;
import com.aryvart.uticianvender.provider.ProviderCompanyInfoLicense;

import com.aryvart.uticianvender.provider.ProviderCompanyInfoPay;
import com.aryvart.uticianvender.provider.Provider_ChooseService;
import com.aryvart.uticianvender.provider.Provider_DashBoard;
import com.aryvart.uticianvender.provider.Provider_Paypalid_Screen;



import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by Android2 on 7/12/2016.
 */
public class SignInActivity extends Activity implements GetNotification, OAuthAuthenticationListener {


    EditText et_email, et_pass;
    TextView txt_settings;
    Button txt_signIn, txt_signup;
    String str_userName, str_password, str_social_id;
    ProgressDialog pD;
    public static final int DIALOG_LOADING = 1;
    Data user = new Data();
    Context context;

    Dialog dialog;
    //Facebook

    ProgressDialog progress;
    //GCm
    String PROJECT_NUMBER = "311648921952";
    String strGCMid = "";
    GeneralData gD;
    AlertDialogManager alert = new AlertDialogManager();
    //SharedPreferences.Editor editor;
    String strImagepath;


    private String strInstaUserId = "";
    int nScreenHeight,ScreenHeight;
    //facebook

    //   private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SplashActivity.autoLoginPreference = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
        //getting resistration id
        SplashActivity.sharedPreferences = getSharedPreferences("regid", Context.MODE_PRIVATE);


        context = this;
        gD = new GeneralData(context);

        if (SplashActivity.autoLoginPreference.getString("uname", null) != null && SplashActivity.autoLoginPreference.getString("pwd", null) != null) {

            Intent provide_after_log = null;

            if (SplashActivity.sharedPreferences.getString("utype", null).equalsIgnoreCase("provider")) {
                provide_after_log = new Intent(SignInActivity.this, Provider_DashBoard.class);
            } else {
                provide_after_log = new Intent(SignInActivity.this, AdminList.class);
            }

            if (provide_after_log != null) {
                startActivity(provide_after_log);
            }
        } else {

            pD = new ProgressDialog(SignInActivity.this);


            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = (int) ((float) nHeight / (float) 2.4);
            ScreenHeight = (int) ((float) nHeight / (float) 2.2);

            setContentView(R.layout.sign_in);

            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            et_email = (EditText) findViewById(R.id.edit_email);
            et_pass = (EditText) findViewById(R.id.edit_password);
            txt_signup = (Button) findViewById(R.id.txt_signup);
            txt_settings = (TextView) findViewById(R.id.txt_frgtpassword);
            txt_signIn = (Button) findViewById(R.id.txt_signin);

            Typeface tf = Typeface.createFromAsset(getAssets(),
                    "arial.ttf");


            et_email.setTypeface(tf);

            et_pass.setTypeface(tf);
            txt_signIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendLoginRequest(strGCMid);
                }
            });
            txt_settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SignInActivity.this, ForgetPassword.class));
                }
            });
            txt_signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                }
            });
            progress = new ProgressDialog(SignInActivity.this);
            progress.setMessage("please wait..");
            progress.setIndeterminate(false);
            progress.setCancelable(false);


            et_email.setText("");
            et_pass.setText("");
            pD = new ProgressDialog(SignInActivity.this);


            et_pass.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        sendLoginRequest(strGCMid);
                        return true;
                    }
                    return false;
                }
            });

            GCMClientManager pushClientManager = new GCMClientManager(this, PROJECT_NUMBER);
            pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
                @Override
                public void onSuccess(String registrationId, boolean isNewRegistration) {

                    strGCMid = registrationId;

                    Log.d("LRegID", registrationId);
                    //send this registrationId to your server
                }

                @Override
                public void onFailure(String ex) {
                    super.onFailure(ex);
                }
            });
        }

    }



    private void sendLoginRequest(final String GCMID) {
        // GCMActivity gcmAct = new GCMActivity(context, (GetNotification) context);
        // String str_gmc_id = gcmAct.getRegId();

        SignInActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!gD.isConnectingToInternet()) {
                    Toast.makeText(SignInActivity.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                }
                else {
                try {
                    if (!gD.isConnectingToInternet()) {
                        alert.showAlertDialog(
                                SignInActivity.this,
                                "Failed to connect Server . Please Check your Network Connection and try again .",
                                false);
                        return;
                    } else {
                        str_userName = et_email.getText().toString().trim();
                        str_password = et_pass.getText().toString().trim();

                        if (str_userName.equals("") && (str_password.equals(""))) {
                            Toast.makeText(getApplicationContext(), "Enter Username and Password", Toast.LENGTH_SHORT).show();
                        } else if (str_password.equals("")) {
                            Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
                        } else if (str_userName.equals("")) {
                            Toast.makeText(getApplicationContext(), "Enter UserName", Toast.LENGTH_SHORT).show();
                        } else {
                            Login obj = new Login(GCMID);
                            obj.execute("");

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }}
        });

    }

    @Override
    public void setNotificationId(final String strGCMid) {

    }

    @Override
    public void setProviderDetails(String strGCMid, String strName, String strImage, String straddress, String expertiseid, String strReferalcdoe) {

    }




    @Override
    public void getProviderId(String strProviderId, String strType) {

    }

    @Override
    public void getServicePrice(String n_price) {

    }

    @Override
    public void getServiceDuration(String n_duration) {

    }

    @Override
    public ArrayList<String> getServicName(TreeMap<Integer, String> str_serviceName) {
        return null;
    }

    @Override
    public void setProviderdet(String strGCMid, String strName, String strImage, String straddress, String strExpertise, String strReferalcode) {

    }



    @Override
    public void NotifyFilter(String strFilterVal) {

    }

    @Override
    public void setProvideraddress(String strlatitude, String strlongitude, String straddress) {

    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onFail(String error) {

    }

    @Override
    public void onReceived(final String strMessage, final String strId) {

        Log.i("SS", "i'mCalled... yahoooooooooooooooo!!!!!!!!!!!!" + strMessage);
        Log.i("SS", "Id" + strId);
        strInstaUserId = strId;

        str_social_id = strInstaUserId;
        Login obj = new Login(str_social_id, true, "instid");
        obj.execute("");

        Log.i("VV", "Id" + strInstaUserId);

    }


    class Login extends AsyncTask<String, Void, String> {

        JSONObject json;
        String str_gcmid;
        Boolean isSocial = false;
        String str_social_key;

        public Login(String str_id) {
            this.str_gcmid = str_id;

        }

        public Login(String str_id, Boolean is_Social, String str_key) {
            this.str_gcmid = str_id;
            this.isSocial = is_Social;
            this.str_social_key = str_key;

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                if (isSocial) {
                    Log.i("FF", "social : " + str_social_key);

                    Log.i("FF", "social_gcmid : " + str_gcmid);
                    json = user.loginUser_Social(str_social_key, str_gcmid);
                } else {
                    json = user.loginUser(str_userName, str_password, str_gcmid,"2");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return String.valueOf(json);
        }

        protected void onPreExecute() {
            //showDialog(DIALOG_LOADING);
           SignInActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gD.callload(context, nScreenHeight);
                }
            });


        }

        protected void onPostExecute(String result) {
            Log.i("SV", "Response Login : " + json.toString());
            if(gD.alertDialog!=null) {
                gD.alertDialog.dismiss();
            }
            try {
                if (json.getInt("code") == 0) {

                    gD.showAlertDialog(context, "",  "Invalid User", nScreenHeight, 1);


                    et_email.setText("");
                    et_pass.setText("");

                } else if (json.getInt("code") == 1) {


                    gD.showAlertDialog(context, "",   "Invalid Password", nScreenHeight, 1);

                     et_pass.setText("");
                } else if (json.getInt("code") == 4) {
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
                        // TextView tv_alert_Title = (TextView) dialogLayout.findViewById(R.id.tv_alert_Title);
                        TextView tv_alert_Message = (TextView) dialogLayout.findViewById(R.id.message);
                        tv_alert_Message.setText(json.get("message").toString());


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

                                alertDialog.dismiss();
                            }
                        });





                        //alert.showAlertDialog(SignInActivity.this, json.get("message").toString(), false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (json.getInt("code") == 2) {
                    /*Toast.makeText(SignInActivity.this, "Login Success", Toast.LENGTH_SHORT).show();*/
                    Log.i("GM", "gcmid" + str_gcmid);

                    String userType = json.getJSONObject("logged_in_user").getString("usertype");
                    String id = json.getJSONObject("logged_in_user").getString("userid");
                    String name = json.getJSONObject("logged_in_user").getString("username");
                    String ful_name = json.getJSONObject("logged_in_user").getString("fullname");
                    String comapny_name = json.getJSONObject("logged_in_user").getString("companyname");
                    String phone_num = json.getJSONObject("logged_in_user").getString("phonenumber");
                    String strUserImagePath = json.getJSONObject("logged_in_user").getString("image_path");

                    strImagepath = gD.common_baseurl+"upload/" + strUserImagePath;

                    SplashActivity.autoLoginPreference = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = SplashActivity.autoLoginPreference.edit();
                    editor.putString("uname", name);
                    editor.putString("pwd", et_pass.getText().toString().trim());
                    editor.commit();

                    SplashActivity.sharedPreferences = getSharedPreferences("regid", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editorVal = SplashActivity.sharedPreferences.edit();
                    editorVal.putString("UID", id);
                    editorVal.putString("username", name);
                    editorVal.putString("fullname", ful_name);
                    editorVal.putString("companyname", comapny_name);
                    editorVal.putString("phone_num", phone_num);
                    editorVal.putString("user_image", strImagepath);
                    editor.putString("real_path", strUserImagePath);
//                    editor.putString("phone_num", "");

                    Log.i("SE", "ID : " + id);
                    editorVal.putString("utype", userType);
                    Log.i("SE", "utype : " + userType);
                    if (userType.equalsIgnoreCase("provider")) {
                        String str_service_log = json.getJSONObject("logged_in_user").getString("services");
                        Intent provide_after_log = null;
                        editorVal.putString("serviceType", str_service_log);


                        if (str_service_log.equals("0")) {
                            provide_after_log = new Intent(SignInActivity.this, AddHoroscope.class);
                        } else if (str_service_log.equals("1")) {
                            provide_after_log = new Intent(SignInActivity.this, ProviderCompanyInfoLicense.class);
                        } else if (str_service_log.equals("2")) {
                            provide_after_log = new Intent(SignInActivity.this, ProviderCompanyInfoPay.class);
                        } else if (str_service_log.equals("5")) {
/*Toast.makeText(SignInActivity.this, json.getString("status"), Toast.LENGTH_SHORT).show();*/
                            provide_after_log = new Intent(SignInActivity.this, ProviderAddServices.class);
                        } else if (str_service_log.equals("6")) {
/*Toast.makeText(SignInActivity.this, json.getString("status"), Toast.LENGTH_SHORT).show();*/
                            provide_after_log = new Intent(SignInActivity.this, Provider_ChooseService.class);
                        } else if (str_service_log.equals("8")) {

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
tv_alert_Title.setVisibility(View.VISIBLE);
                            tv_alert_Message.setText("Your account is pending");
                            tv_alert_Title.setText("(Your account should be approved shortly)");

                            Button btn_submit = (Button) dialogLayout.findViewById(R.id.ok);
                            Button btn_cancel = (Button) dialogLayout.findViewById(R.id.cancel);


                            FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ScreenHeight);
                            llayAlert.setLayoutParams(lparams);
                            int count = 1;
                            if (count == 1) {
                                btn_cancel.setVisibility(View.GONE);
                            }


                            btn_submit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    SharedPreferences.Editor editor = SplashActivity.autoLoginPreference.edit();
                                    editor.putString("uname", null);
                                    editor.putString("pwd", null);
                                    editor.commit();
                                    et_email.setText("");
                                    et_pass.setText("");

                                    alertDialog.dismiss();



                                }

                            });

                        }
                        else if (str_service_log.equalsIgnoreCase("7")) {
                            provide_after_log = new Intent(SignInActivity.this, Provider_Paypalid_Screen.class);
                        }else if (str_service_log.equals("4")) {


                            LayoutInflater inflater = LayoutInflater.from(context);
                            View dialogLayout = inflater.inflate(R.layout.layout_confirmation, null);

                            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
                            alertDialogBuilder.setView(dialogLayout);
                            alertDialogBuilder.setCancelable(false);


                            final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                            //  alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            alertDialog.show();

                            LinearLayout llayAlert = (LinearLayout) dialogLayout.findViewById(R.id.llayalertDialog);
                            TextView tv_alert_Message = (TextView) dialogLayout.findViewById(R.id.message);

                            tv_alert_Message.setText(json.getString("status"));


                            Button btn_submit = (Button) dialogLayout.findViewById(R.id.ok);
                            Button btn_cancel = (Button) dialogLayout.findViewById(R.id.cancel);


                            FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ScreenHeight);
                            llayAlert.setLayoutParams(lparams);
                            int count = 1;
                            if (count == 1) {
                                btn_cancel.setVisibility(View.GONE);
                            }


                            btn_submit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SharedPreferences.Editor editor = SplashActivity.autoLoginPreference.edit();
                                    editor.putString("uname", null);
                                    editor.putString("pwd", null);
                                    editor.commit();

                                    et_email.setText("");
                                    et_pass.setText("");

                       alertDialog.dismiss();



                                }

                            });


                        }

                        else  {
                            provide_after_log = new Intent(SignInActivity.this, SignInActivity.class);
                        }

                        if (provide_after_log != null) {
                            /*Toast.makeText(getApplicationContext(), "Logged in as " + userType, Toast.LENGTH_SHORT).show();*/
                            provide_after_log.putExtra("userName", ful_name);
                            startActivity(provide_after_log);
                            finishAffinity();
                        }

                    } else {
                        Intent nextScreenIntent = new Intent(SignInActivity.this, AdminList.class);
                        startActivity(nextScreenIntent);
                        finish();
                    }


                    editorVal.commit();/**/
                } else if (json.getInt("code") == 2) {
                    try {

                        gD.showAlertDialog(context, "",  json.getString("status").toString(), nScreenHeight, 1);
                       // alert.showAlertDialog(SignInActivity.this, json.getString("status").toString(), false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                   /* Toast.makeText(getApplicationContext(), json.getString("status"), Toast.LENGTH_SHORT).show();*/
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


}

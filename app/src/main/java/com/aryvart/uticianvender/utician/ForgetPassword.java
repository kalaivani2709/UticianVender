package com.aryvart.uticianvender.utician;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
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
import com.aryvart.uticianvender.genericclasses.Data;
import com.aryvart.uticianvender.genericclasses.GeneralData;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Android2 on 7/13/2016.
 */
public class ForgetPassword extends Activity {
    ImageView img_send_mail,img_back;

    String str_text_email;
    EditText et_email;
    GeneralData gD;
    Context ctx;
    AlertDialogManager alert = new AlertDialogManager();

    Data user = new Data();
    int nScreenHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.forget_password);
            ctx = this;
            gD = new GeneralData(ctx);

            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = (int) ((float) nHeight / (float) 2.1);
            if (!gD.isConnectingToInternet()) {
                alert.showAlertDialog(ctx, "Unable to connect with Internet. Please check your internet connection and try again", false);
                //gD.showAlertDialogNet(context, "No Internet Connection. Do you wish to turn it on?");
            }

            img_send_mail=(ImageView)findViewById(R.id.img_send_email);
            et_email=(EditText)findViewById(R.id.edit_email);
            img_back=(ImageView)findViewById(R.id.img_back);
            img_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            img_send_mail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendEmail();
                }
            });
            et_email.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                        sendEmail();
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void sendEmail() {

        if (!gD.isConnectingToInternet()) {
            Toast.makeText(ForgetPassword.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
        }
        else {
        try {
            try {
                if (!gD.isConnectingToInternet()) {
                    alert.showAlertDialog(
                            ctx,
                            "Failed to connect Server . Please Check your Network Connection and try again .",
                            false);
                    return;
                }
                str_text_email = et_email.getText().toString().trim();

                if (str_text_email.equals("")) {
                    Toast.makeText(getApplicationContext(), "Enter your email", Toast.LENGTH_SHORT).show();

                } else if (!gD.isValidEmail(str_text_email)) {
                    Toast.makeText(getApplicationContext(), "Invalid Email Address", Toast.LENGTH_SHORT).show();
                } else {
                    ForgetPasswordTask obj = new ForgetPasswordTask();
                    obj.execute("");
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }}
    }
    class ForgetPasswordTask extends AsyncTask<String, Void, String> {

        JSONObject json;

        @Override
        protected String doInBackground(String... params) {

            json = user.forgetPassword(str_text_email);
            return String.valueOf(json);
        }

        protected void onPreExecute() {

            gD.callload(ctx, nScreenHeight);



        }

        protected void onPostExecute(String result) {

            try {
                if (json.getInt("code") == 0) {


                    gD.showAlertDialog(ctx, "",   "Invalid email. Please try again", nScreenHeight, 1);

                } else if (json.getInt("code") == 1) {

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

                    tv_alert_Message.setText("Password has been sent to your registered email successful");

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






                            Intent goToLogin = new Intent(getApplicationContext(), SignInActivity.class);
                            startActivity(goToLogin);
                            finish();



                        }

                    });






                }

            } catch (JSONException e) {
                e.printStackTrace();
            }



            if(gD.alertDialog!=null) {
                gD.alertDialog.dismiss();
            }
        }
    }
}

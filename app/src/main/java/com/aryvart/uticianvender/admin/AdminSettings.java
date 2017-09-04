package com.aryvart.uticianvender.admin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.genericclasses.AlertDialogManager;
import com.aryvart.uticianvender.genericclasses.GeneralData;

import com.aryvart.uticianvender.genericclasses.MultipartUtility;
import com.aryvart.uticianvender.imageCache.ImageLoader;
import com.aryvart.uticianvender.utician.SplashActivity;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by android3 on 22/7/16.
 */
public class AdminSettings extends Activity {

    ImageView back_image, change_password;
    LinearLayout edit_imae_lay, change_password_image,notify_image_lay,payment_image_lay, ll_change_pass, ll_edit_profile,ll_notify,ll_payment;
    EditText edit_old_pass, edit_new_pass, edit_conf_pass;
    ImageView  change_ok;
    ProgressDialog pd;

    String  str_get_old_pass, str_get_new_pass, str_get_new_confirm_pass;
    AlertDialogManager alert = new AlertDialogManager();



    ImageLoader imgLoader;
    Context context;
    GeneralData gD;
    int nScreenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.adminsettings);
            context = this;
            gD = new GeneralData(context);

            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = (int) ((float) nHeight / (float) 2.4);
            back_image = (ImageView) findViewById(R.id.back_image);

            change_password = (ImageView) findViewById(R.id.change_password);

        /*change_cancel = (ImageView) findViewById(R.id.change_cancel);*/
            change_ok = (ImageView) findViewById(R.id.change_ok);

            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(context, "Unable to connect with Internet. Please check your internet connection and try again");
            }
            edit_old_pass = (EditText) findViewById(R.id.edit_old_pass);
            edit_new_pass = (EditText) findViewById(R.id.edit_new_pass);
            edit_conf_pass = (EditText) findViewById(R.id.edit_conf_pass);


            str_get_old_pass = edit_old_pass.getText().toString();
            str_get_new_pass = edit_new_pass.getText().toString();
            str_get_new_confirm_pass = edit_conf_pass.getText().toString();

            back_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    Intent i = new Intent(AdminSettings.this, AdminList.class);
                    //  gD.screenback = 0;
                    startActivity(i);
                }
            });
            edit_conf_pass.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {

                        clickevent();


                        return true;
                    }
                    return false;
                }
            });


            change_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    clickevent();
                }

            });


            edit_imae_lay = (LinearLayout) findViewById(R.id.edit_image_lay);
            ll_edit_profile = (LinearLayout) findViewById(R.id.edit_profile_layout);
            change_password_image = (LinearLayout) findViewById(R.id.changepassword_image_lay);
            ll_change_pass = (LinearLayout) findViewById(R.id.chage_pwd_layout);
            ll_payment = (LinearLayout) findViewById(R.id.payment_layout);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(context, AdminList.class);
        //  gD.screenback = 0;
        startActivity(i);
    }

public void clickevent()
{

    try {
        str_get_new_pass = edit_new_pass.getText().toString().trim();
        str_get_new_confirm_pass = edit_conf_pass.getText().toString().trim();
        str_get_old_pass = edit_old_pass.getText().toString().trim();

        if (str_get_old_pass.length() > 0 && str_get_new_pass.length() > 0 && str_get_new_confirm_pass.length() > 0 && str_get_new_pass.length() > 5 && str_get_new_confirm_pass.length() > 5) {
            if (str_get_new_pass.equals(str_get_new_confirm_pass)) {
                ChangePasswordTask chngepasstask = new ChangePasswordTask();
                chngepasstask.execute();
            } else {
                Toast.makeText(AdminSettings.this, "New password and Confirm password doesn't match !", Toast.LENGTH_SHORT).show();
            }


        } else {
            if (str_get_old_pass.length() == 0) {
                Toast.makeText(AdminSettings.this, "Enter old password.", Toast.LENGTH_SHORT).show();
            } else if (str_get_new_pass.length() == 0) {
                Toast.makeText(AdminSettings.this, "Enter new password.", Toast.LENGTH_SHORT).show();
            } else if (str_get_new_pass.length() < 6) {
                Toast.makeText(getApplicationContext(), "Password must be in 6 to 20 characters", Toast.LENGTH_SHORT).show();
            }else if (str_get_new_confirm_pass.length() == 0) {
                Toast.makeText(AdminSettings.this, "Enter confirm password.", Toast.LENGTH_SHORT).show();
            } else if (str_get_new_confirm_pass.length() < 6) {
                Toast.makeText(getApplicationContext(), "Password must be in 6 to 20 characters", Toast.LENGTH_SHORT).show();
            }else if (str_get_old_pass.equals(str_get_new_pass)) {
                Toast.makeText(AdminSettings.this, "Old and New password should not same.", Toast.LENGTH_SHORT).show();
            } else if (!str_get_new_pass.equals(str_get_new_confirm_pass)) {
                Toast.makeText(AdminSettings.this, "Old and New password doesn't match.", Toast.LENGTH_SHORT).show();
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

}



    /*  ChangePasswordTasks*/
        class ChangePasswordTask extends AsyncTask {

            String sResponse = null;

            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    String charset = "UTF-8";
                    String requestURL = gD.common_baseurl+"changepassword.php";

                    MultipartUtility multipart = new MultipartUtility(requestURL, charset);

                    multipart.addHeaderField("Content-Encoding", "gzip");
                    multipart.addHeaderField("User-Agent", "rajaji");
                    multipart.addHeaderField("Test-Header", "Header-Value");
                    multipart.addFormField("description", "CoolPictures");
                    multipart.addFormField("keywords", "Java,upload,Spring");

                    multipart.addFormField("oldpassword", str_get_old_pass);
                    multipart.addFormField("newpassword", str_get_new_pass);
                    multipart.addFormField("id", SplashActivity.sharedPreferences.getString("UID", null));

                    // 6. upload finish ----------- vinoth
                    List<String> response = multipart.finish();
                    System.out.println("SERVER REPLIED:");
                    StringBuilder sb = new StringBuilder();
                    for (String line : response) {
                        System.out.println(line);
                        sb.append(line);
                    }
                    try {
                        JSONObject jsonObj = new JSONObject(sb.toString());
                        Log.i("SSu", "StrResp : " + jsonObj.toString());
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
                gD.callload(context, nScreenHeight);



            }

            @Override
            protected void onPostExecute(Object sesponse) {
                try {
                    if (sResponse != null) {
                        JSONObject jsobj = new JSONObject(sResponse);

                        if (jsobj.getInt("code") == 2) {
                            //SharedPreferences.Editor editor = SplashActivity.autoLoginPreference.edit();
                            //editor.putString("pwd", str_get_new_pass);
                           // editor.commit();

                            Toast.makeText(AdminSettings.this, "Your password has been change successfully", Toast.LENGTH_SHORT).show();
                            Intent NextScreenIntent = new Intent(AdminSettings.this, AdminList.class);
                            startActivity(NextScreenIntent);
                        } else if (jsobj.getInt("code") == 0) {

                            gD.showAlertDialog(context, "",jsobj.getString("status").toString(), nScreenHeight, 1);


                        } else if (jsobj.getInt("code") == 3) {
                            gD.showAlertDialog(context, "",jsobj.getString("status").toString(), nScreenHeight, 1);

                            edit_old_pass.setText("");
                            edit_new_pass.setText("");
                            edit_conf_pass.setText("");

                        }



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

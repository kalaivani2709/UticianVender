package com.aryvart.uticianvender.provider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.aryvart.uticianvender.genericclasses.MultipartUtility;
import com.aryvart.uticianvender.utician.SplashActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android3 on 30/7/16.
 */
public class Provider_PreferencePage extends Activity {
    CheckBox chk_white, chk_american, chk_asian, chk_middle_eastern, chk_latino, chk_spanish, chk_selectall;
    String strcheckval;
    TextView btn_submit;

    TextView txt;
    ArrayList<CheckBox> alcheckboxids;

    ArrayList<CheckBox> alARcheckboxids;

    ArrayList<String> alChkName;
    Context ctx;
    ProgressDialog dialog;

    CheckBox cheGLid;
    String strvalue;
    String strFrom;
    LinearLayout back;

    String strTobeCheck = "all";
    GeneralData gD;
    int nScreenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.chooseservicelayout);
            ctx = this;
            dialog = new ProgressDialog(Provider_PreferencePage.this);

            gD = new GeneralData(ctx);
            back = (LinearLayout) findViewById(R.id.llayBack);
            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = (int) ((float) nHeight / (float) 2.0);

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    finish();
                }
            });
            AfterLoginTask aft = new AfterLoginTask();
            aft.execute();
            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(ctx, "Unable to connect with Internet. Please check your internet connection and try again");
            }
            btn_submit = (TextView) findViewById(R.id.llaySubmit);
            btn_submit.setText("Submit");
            chk_white = (CheckBox) findViewById(R.id.chk_white);
            chk_american = (CheckBox) findViewById(R.id.chk_american);
            chk_asian = (CheckBox) findViewById(R.id.chk_asian);
            chk_latino = (CheckBox) findViewById(R.id.chk_latino);
            chk_spanish = (CheckBox) findViewById(R.id.chk_spanish);
            chk_selectall = (CheckBox) findViewById(R.id.chk_selectall);
        //    chk_middle_eastern = (CheckBox) findViewById(R.id.chk_middle_eastern);

            alcheckboxids = new ArrayList<CheckBox>();
            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(ctx, "Unable to connect with Internet. Please check your internet connection and try again");
            }

            alARcheckboxids = new ArrayList<CheckBox>();
            alcheckboxids.add(chk_white);
            alcheckboxids.add(chk_american);
            alcheckboxids.add(chk_asian);
            alcheckboxids.add(chk_latino);
            alcheckboxids.add(chk_spanish);
         //   alcheckboxids.add(chk_middle_eastern);

            alChkName = new ArrayList<String>();

            chk_selectall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView.isChecked()) {
                        alChkName = new ArrayList<String>();
                        strFrom = "chkall";
                        ArrayList<String> alNames = new ArrayList<String>();

                        for (int i = 0; i < alcheckboxids.size(); i++) {
                            alNames.add(getCheckedNames(alcheckboxids.get(i), "all"));

                        }
                        strcheckval = alNames.toString();


                        //  Toast.makeText(Provider_PreferencePage.this, "strselectall" + strselectall, Toast.LENGTH_SHORT).show();

                    } else {

                        alChkName = new ArrayList<String>();
                        if (!strTobeCheck.equals("only")) {
                            for (int i = 0; i < alcheckboxids.size(); i++) {
                                //   unselectAll(alcheckboxids.get(i));

                                checkAnduncheck(alcheckboxids.get(i), "unsel");
                            }
                            strcheckval = "";
                        }


                    }
                }
            });
            chk_white.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (buttonView.isChecked()) {
                        if (alChkName.size() >= alcheckboxids.size()) {
                            alChkName = new ArrayList<String>();
                        }
                        alChkName.add(checkAnduncheck(chk_white, "sel"));
                        mSelectall();
                    } else {
                        alChkName.add(checkAnduncheck(chk_white, "unsel"));
                    }
                }
            });


            chk_american.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (buttonView.isChecked()) {
                        if (alChkName.size() >= alcheckboxids.size()) {
                            alChkName = new ArrayList<String>();
                        }
                        alChkName.add(checkAnduncheck(chk_american, "sel"));
                        mSelectall();
                    } else {
                        alChkName.add(checkAnduncheck(chk_american, "unsel"));
                    }
                }
            });
            chk_latino.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (buttonView.isChecked()) {
                        if (alChkName.size() >= alcheckboxids.size()) {
                            alChkName = new ArrayList<String>();
                        }
                        alChkName.add(checkAnduncheck(chk_latino, "sel"));
                        mSelectall();
                    } else {
                        alChkName.add(checkAnduncheck(chk_latino, "unsel"));
                    }
                }
            });
            chk_spanish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                    if (buttonView.isChecked()) {
                        if (alChkName.size() >= alcheckboxids.size()) {
                            alChkName = new ArrayList<String>();
                        }
                        alChkName.add(checkAnduncheck(chk_spanish, "sel"));
                        mSelectall();
                    } else {
                        alChkName.add(checkAnduncheck(chk_spanish, "unsel"));
                    }
                }
            });
            chk_asian.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                    if (buttonView.isChecked()) {
                        if (alChkName.size() >= alcheckboxids.size()) {
                            alChkName = new ArrayList<String>();
                        }
                        alChkName.add(checkAnduncheck(chk_asian, "sel"));
                        mSelectall();
                    } else {
                        alChkName.add(checkAnduncheck(chk_asian, "unsel"));
                    }

                }
            });
           /* chk_middle_eastern.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                    if (buttonView.isChecked()) {
                        if (alChkName.size() >= alcheckboxids.size()) {
                            alChkName = new ArrayList<String>();
                        }
                        alChkName.add(checkAnduncheck(chk_middle_eastern, "sel"));
                        mSelectall();
                    } else {
                        alChkName.add(checkAnduncheck(chk_middle_eastern, "unsel"));
                    }

                }
            });*/

            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_PreferencePage.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } else {
                        if ((!chk_white.isChecked()) && (!chk_asian.isChecked()) && (!chk_spanish.isChecked()) && (!chk_latino.isChecked()) && (!chk_selectall.isChecked()) && (!chk_american.isChecked()) ) {

                            Toast.makeText(Provider_PreferencePage.this, "Please select one value", Toast.LENGTH_SHORT).show();
                        } else {


                            ArrayList<String> alIds = new ArrayList<String>();
                            for (int i = 0; i < alcheckboxids.size(); i++) {
                                if (alcheckboxids.get(i).isChecked()) {
                                    alIds.add(alcheckboxids.get(i).getTag().toString());
                                    strvalue = alIds.toString().substring(1, alIds.toString().length() - 1);
                                }
                            }

                            ProviderServiceAdd addservice = new ProviderServiceAdd(strvalue);
                            addservice.execute();
                        }

                        // Toast.makeText(Provider_PreferencePage.this, "strvalue" + strvalue , Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void unselectAll(CheckBox checkBox) {
        checkBox.setChecked(false);
    }

    private void mSelectall() {
        Log.i("DS", "chkNames : " + alChkName.size() + "********** : " + "alcheckboxids : " + alcheckboxids.size());
        if (alChkName.size() == alcheckboxids.size()) {
            chk_selectall.setChecked(true);
        }

    }

    private String checkAnduncheck(CheckBox chk_white, String strType) {

        String strChekNames = null;
        try {
            strChekNames = "";
            if (strType.equalsIgnoreCase("sel")) {
                chk_white.setChecked(true);
                strChekNames = chk_white.getText().toString();

            } else {
                chk_white.setChecked(false);

                if (chk_selectall.isChecked()) {
                    cheGLid = chk_white;
                    strFrom = "check";
                    strTobeCheck = "only";
                    chk_selectall.setChecked(false);

                } else {
                    strTobeCheck = "all";
                    chk_white.setChecked(false);
                    strChekNames = chk_white.getText().toString();
                    if (alcheckboxids.size() == 0) {
                        chk_selectall.setChecked(false);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strChekNames;

    }

    private String getCheckedNames(CheckBox checkBox, String strTye) {
        String strNames = null;
        try {
            strNames = "";
            if (strTye.equalsIgnoreCase("all")) {
                checkBox.setChecked(true);
                strNames = checkBox.getText().toString().trim();
            } else {

                for (int i = 0; i < alcheckboxids.size(); i++) {

                    if (strFrom.equalsIgnoreCase("chkall")) {
                        alcheckboxids.get(i).setChecked(false);
                    } else {
                        if (alcheckboxids.get(i) == cheGLid) {
                            checkBox.setChecked(false);
                        } else {
                            alcheckboxids.get(i).setChecked(true);
                        }
                    }
                }


                strNames = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return strNames;
    }


    //sending services

    private void callCheckboxtoCheck(String[] strp) {

        try {
            alChkName = new ArrayList<String>();
            //White, African American, Asian, Latino, Spanish
            for (int i = 0; i < strp.length; i++) {

                if (strp[i].toString().trim().equalsIgnoreCase("AmericanIndianAlaskinNative")) {
                    chk_white.setChecked(true);
                } else if (strp[i].toString().trim().equalsIgnoreCase("AsianorPacificIslander")) {
                    chk_american.setChecked(true);
                } else if (strp[i].toString().trim().equalsIgnoreCase("BlacknotofHispanicorigin")) {
                    chk_asian.setChecked(true);
                } else if (strp[i].toString().trim().equalsIgnoreCase("Hispanic")) {
                    chk_latino.setChecked(true);
                } else if (strp[i].toString().trim().equalsIgnoreCase("WhitenotofHispanicorigin")) {
                    chk_spanish.setChecked(true);
                } /*else if (strp[i].toString().trim().equalsIgnoreCase("Middle Eastern")) {
                    chk_middle_eastern.setChecked(true);
                }*/


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ProviderServiceAdd extends AsyncTask {

        String sResponse = null;
        String strval;

        public ProviderServiceAdd(String value) {
            strval = value;


        }


        @Override
        protected Object doInBackground(Object[] params) {
            try {


                String charset = "UTF-8";

                String requestURL = gD.common_baseurl+"editproviderpreferences.php";


                // 4. separate class for multipart content image uploaded task----------- vinoth
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                multipart.addHeaderField("Accept", "application*/*");
                multipart.addHeaderField("Content-type", "application/x-www-form-urlencoded");

                multipart.addHeaderField("Content-Encoding", "gzip");


                // 5. set the user_image key word and multipart image file value ----------- vinoth

                multipart.addFormField("preferences", strval);
                // multipart.addFormField("providerid", String.valueOf(2) );

                multipart.addFormField("providerid", SplashActivity.sharedPreferences.getString("UID", null));


                // Log.e("providerid", SplashActivity.sharedPreferences.getString("UID", null));
                Log.e("preference", strval);

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
            Provider_PreferencePage.this.runOnUiThread(new Runnable() {
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

                        tv_alert_Message.setText("Your preference changed successfully");

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

    class AfterLoginTask extends AsyncTask {
        String sResponse = null;

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                String charset = "UTF-8";
                String requestURL = gD.common_baseurl+"selectprovider.php";
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);

                multipart.addHeaderField("Content-Encoding", "gzip");
                multipart.addHeaderField("User-Agent", "rajaji");
                multipart.addHeaderField("Test-Header", "Header-Value");
                multipart.addFormField("keywords", "Java,upload,Spring");
                multipart.addFormField("providerid", SplashActivity.sharedPreferences.getString("UID", null));
                // multipart.addFormField("providerid", "7");
                List<String> response = multipart.finish();
                System.out.println("SERVER REPLIED:");
                StringBuilder sb = new StringBuilder();
                for (String line : response) {
                    System.out.println(line);
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
            gD.callload(ctx, nScreenHeight);



        }

        @Override
        protected void onPostExecute(Object sesponse) {
            try {
                JSONObject jsobj = new JSONObject(sResponse);
                if (jsobj.getInt("code") == 2) {
                    JSONArray jsArProviders = jsobj.getJSONArray("providers");
                    String strpreference = jsArProviders.getJSONObject(0).getString("preferences");


                    if (strpreference.contains(",")) {
                        String[] strp = strpreference.split(",");
                        callCheckboxtoCheck(strp);

                    } else {

                        String[] strp = new String[]{strpreference};
                        callCheckboxtoCheck(strp);
                    }


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



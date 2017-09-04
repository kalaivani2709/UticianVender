package com.aryvart.uticianvender.provider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aryvart.uticianvender.Interface.GetNotification;
import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.bean.ProviderBean;
import com.aryvart.uticianvender.genericclasses.GeneralData;

import com.aryvart.uticianvender.imageCache.ImageLoader;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.TreeMap;

/**
 * Created by android3 on 1/6/16.
 */
public class Provider_Generate extends Activity implements GetNotification {


    ArrayList<ProviderBean> alPB;
    Context context;

    String timezonevalue;
    ProviderBean pB;
    ListView lv;
    Button generate;
    String strname, rowid, status, strtype;

    ProgressDialog dialog;
    ImageLoader imgLoader;
    ImageView userimage;

    TextView tv_totalRateAD, rating, username, txt_time;

    ImageView back;
    String strSR;
    GeneralData gD;
    int nScreenHeight;

    LinearLayout llayServiceRN;

    int nTotalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            setContentView(R.layout.invoice_page);
            context = this;
            gD = new GeneralData(context);

            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = (int) ((float) nHeight / (float) 2.1);
            dialog = new ProgressDialog(Provider_Generate.this);

            TimeZone tz = TimeZone.getDefault();
            System.out.println("TimeZone " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

            timezonevalue=tz.getID();
            Log.i("TZ", "timezone" + timezonevalue);
            imgLoader = new ImageLoader(context);

            Provider_CompleteRaisePage.sharedPreferences = getSharedPreferences("GenerateId", Context.MODE_PRIVATE);
            // lv = (ListView) findViewById(R.id.service_list);
            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(context, "Unable to connect with Internet. Please check your internet connection and try again");
            }

            back = (ImageView) findViewById(R.id.img_back);

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent i = new Intent(Provider_Generate.this, ProviderAppointment.class);
                    startActivity(i);
                    finish();
                }
            });
            generate = (Button) findViewById(R.id.but_generate);





            llayServiceRN = (LinearLayout) findViewById(R.id.llaySR);

            try {

                strSR = getIntent().getStringExtra("sR");
                strtype = getIntent().getStringExtra("type");

                Log.i("GG", "strtype.." + strtype);
                username = (TextView) findViewById(R.id.username);

                JSONObject jsobj = new JSONObject(strSR);

                userimage = (ImageView) findViewById(R.id.provider_im);

                tv_totalRateAD = (TextView) findViewById(R.id.tv_totalRateAD);
                txt_time = (TextView) findViewById(R.id.time);

                strname = getIntent().getStringExtra("name");

                username.setText(jsobj.getString("username"));
                txt_time.setText(jsobj.getString("starttime"));
                rowid = jsobj.getString("id");
                rating = (TextView) findViewById(R.id.rating);
                rating.setText(jsobj.getString("useravgrating") + " " + "Rating");
                status = String.valueOf(1);
                String strPath = jsobj.getString("image_path");

                Log.i("FF", "strimage" + strPath.length());

                if (strPath.length() == 0) {
                    userimage.setBackgroundResource(R.drawable.default_user_icon);
                } else {

                    String strimage = gD.common_baseurl+"upload/" + jsobj.getString("image_path");

                    userimage.setImageBitmap(imgLoader.getBitmap(strimage));

                }



                alPB = new ArrayList<ProviderBean>();
                JSONArray jsAr = jsobj.getJSONArray("invoice");


                Log.i("sR", "ProviderInvoice : " + strSR + "******** jsAr.length():" + jsAr.length());
                for (int i = 0; i < jsAr.length(); i++) {

                    Log.i("sR", "values " + jsAr.getJSONObject(i).getString("rate"));


                    LinearLayout llayEachLinear = new LinearLayout(this);
                    llayEachLinear.setOrientation(LinearLayout.HORIZONTAL);
                    llayEachLinear.setWeightSum(3);
                    LinearLayout.LayoutParams llayEachParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    llayEachLinear.setLayoutParams(llayEachParams);


                    LinearLayout.LayoutParams llayleftParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                    LinearLayout llayLeft = new LinearLayout(this);
                    llayleftParams.weight = 2;
                    llayLeft.setLayoutParams(llayleftParams);

                    LinearLayout.LayoutParams llaycenterParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout llayCenter = new LinearLayout(this);
                    llaycenterParams.weight = 0.5f;
                    llayCenter.setLayoutParams(llaycenterParams);


                    LinearLayout.LayoutParams llayrightParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                    LinearLayout llayRight = new LinearLayout(this);
                    llayrightParams.weight = 0.5f;
                    llayRight.setLayoutParams(llayrightParams);

                    LinearLayout.LayoutParams viewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                    viewParams.weight = 0.03f;
                    View vv = new View(this);
                    vv.setLayoutParams(viewParams);
                    vv.setBackgroundColor(Color.parseColor("#1A000000"));


                    LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                    TextView txt_service = new TextView(this);
                    txt_service.setPadding(3, 2, 0, 0);
                    txt_service.setGravity(Gravity.CENTER_VERTICAL);
                    txt_service.setGravity(Gravity.CENTER_HORIZONTAL);
                    txt_service.setGravity(Gravity.CENTER);
                    txt_service.setLayoutParams(textParams);
                    txt_service.setTextColor(Color.parseColor("#000000"));
                    txt_service.setText(jsAr.getJSONObject(i).getString("service"));


                    TextView txt_rate = new TextView(this);
                    txt_rate.setLayoutParams(textParams);
                    txt_rate.setPadding(3, 2, 25, 0);
                    txt_rate.setTextColor(Color.parseColor("#000000"));
                    txt_rate.setGravity(Gravity.RIGHT|Gravity.CENTER);

                    txt_rate.setText("$ " + jsAr.getJSONObject(i).getString("rate"));


                    int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
                    int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
                    LinearLayout.LayoutParams imagParams = new LinearLayout.LayoutParams(width, height);
                    ImageView img_service = new ImageView(this);
                    img_service.setPadding(0, 3, 0, 3);
                    img_service.setImageResource(R.drawable.default1);


                    if (jsAr.getJSONObject(i).getString("service").equalsIgnoreCase("Barber")) {
                        img_service.setImageResource(R.drawable.default1);
                    } else if (jsAr.getJSONObject(i).getString("service").equalsIgnoreCase("Hair Stylist")) {
                        img_service.setImageResource(R.drawable.default1);
                    } else if (jsAr.getJSONObject(i).getString("service").equalsIgnoreCase("Makeup Artist")) {
                        img_service.setImageResource(R.drawable.default1);
                    } else if (jsAr.getJSONObject(i).getString("service").equalsIgnoreCase("Nail Technician")) {
                        img_service.setImageResource(R.drawable.default1);
                    }

                    img_service.setLayoutParams(imagParams);

                    llayCenter.addView(img_service);
                    llayLeft.addView(txt_service);
                    llayRight.addView(txt_rate);

                    llayEachLinear.addView(llayCenter);
                    llayEachLinear.addView(llayLeft);
                    llayEachLinear.addView(llayRight);


                    llayServiceRN.addView(llayEachLinear);
                    llayServiceRN.addView(vv);


                }
                int total_count = llayServiceRN.getChildCount();
                for (int j = 0; j < total_count; j++) {

                    View v = llayServiceRN.getChildAt(j);
                    if (v instanceof LinearLayout) {
                        LinearLayout llayEach = (LinearLayout) llayServiceRN.getChildAt(j);


                        View vv2 = llayEach.getChildAt(2);
                        if (vv2 instanceof LinearLayout) {


                            View vText = ((LinearLayout) vv2).getChildAt(0);


                            if (vText instanceof TextView) {

                                String strTotal = ((TextView) vText).getText().toString().trim();
                                nTotalAmount += Integer.parseInt(strTotal.substring(1, strTotal.length()).trim());
                                Log.i("sR", "Value 0" + nTotalAmount);

                                Log.i("sR", "vText" + strTotal);
                                tv_totalRateAD.setText("$" + nTotalAmount);
                                // int nVal = Integer.parseInt(tv_ServiceTax.getText().toString().trim().substring(1, tv_ServiceTax.getText().toString().trim().length())) + Integer.parseInt(String.valueOf(nTotalAmount));
                                // tv_GrandTotal.setText("$" + nVal);

                            }


                        }


                    }

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }


            generate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    GenerateInvoiceTask generateinvoice = new GenerateInvoiceTask(context, rowid, status);
                    generateinvoice.execute();


                }
            });
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


    }






    @Override
    public void setNotificationId(String strGCMid) {

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

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            Log.i("SR", "Exception_readStream : " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }
    //generate asynchoronus task

    @Override
    public void onBackPressed() {

        Intent i = new Intent(Provider_Generate.this, ProviderAppointment.class);
        startActivity(i);
        finish();
    }

    class GenerateInvoiceTask extends AsyncTask {

        String user_id = null;
        String sResponse = null;
        Context cont;
        String status;

        public GenerateInvoiceTask(Context context, String userid, String status) {
            this.user_id = userid;
            this.cont = context;
            this.status = status;
        }

        @Override
        protected void onPreExecute() {
            Provider_Generate.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gD.callload(context, nScreenHeight);



                }
            });
// TODO Auto-generated method stub
            super.onPreExecute();
        }


        @Override
        protected Object doInBackground(Object[] params) {
            try {


                String requestURL = gD.common_baseurl+"generateinvoice.php?id=" + user_id + "&generate=" + status;



                try {

                    URL url;
                    HttpURLConnection urlConnection = null;
                    try {
                        Log.i("SR", "Login URL : " + requestURL.trim());
                        System.out.print("strHTTP_LOGIN_URL");
                        url = new URL(requestURL.trim());

                        urlConnection = (HttpURLConnection) url.openConnection();
                        int responseCode = urlConnection.getResponseCode();

                        sResponse = readStream(urlConnection.getInputStream());

                        Log.i("SR", "APIcalls_Process : " + sResponse);


                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("SR", "Exception_Process : " + e.getMessage());
                    } finally {
                        if (urlConnection != null)
                            urlConnection.disconnect();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }


            return sResponse;
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
                        TextView tv_alert_Title = (TextView) dialogLayout.findViewById(R.id.add_msg);
                        TextView tv_alert_Message = (TextView) dialogLayout.findViewById(R.id.message);

                        tv_alert_Message.setText("Your invoice has been generated successfully");


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

                                Provider_CompleteRaisePage.sharedPreferences = getSharedPreferences("GenerateId", Context.MODE_PRIVATE);
                                String befrorepagetype = "raisepage";
                                SharedPreferences.Editor editname = Provider_CompleteRaisePage.sharedPreferences.edit();
                                editname.putInt("isGenerate", 1);
                                editname.commit();

                                if (strtype.equalsIgnoreCase("generate")) {

                                    Intent i = new Intent(Provider_Generate.this, Provider_CompleteRaisePage.class);
                                    i.putExtra("type", befrorepagetype);
                                    i.putExtra("sR", strSR);
                                    startActivity(i);
                                    finish();
                                    ;
                                } else {

                                    Intent i = new Intent(Provider_Generate.this, Provider_CompleteRaisePage.class);
                                    i.putExtra("type", befrorepagetype);
                                    i.putExtra("sR", strSR);
                                    startActivity(i);

                                    finish();
                                }

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

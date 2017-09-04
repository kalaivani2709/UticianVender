package com.aryvart.uticianvender.provider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
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

import com.aryvart.uticianvender.Interface.GetBookingId;
import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.adapter.ProviderAppointmentAdapter;
import com.aryvart.uticianvender.bean.AppointmentBean;
import com.aryvart.uticianvender.genericclasses.AlertDialogManager;
import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.aryvart.uticianvender.genericclasses.MultipartUtility;
import com.aryvart.uticianvender.utician.SplashActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Android2 on 7/17/2016.
 */
public class ProviderAppointment extends Activity implements GetBookingId {
    static SharedPreferences sharedpreferences;
    LinearLayout lay_today, lay_tomorrow, lay_month, lay_all;
    TextView text_head;
    AlertDialogManager alert = new AlertDialogManager();
    ProgressDialog pD;
    RecyclerView recyclerView;
    Context ctx;
String bookingID;
    String timezonevalue;
    JSONArray providerServicesToday, providerServicesTom, providerServicesMonth, providerServicesAll;
    TextView emptyText;
    int nScreenHeight;
    ImageView image_today, image_tomorrow, image_month, image_all, menu_back;

    private List<AppointmentBean> myListt = new ArrayList<>();
GeneralData gD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.provider_appointments);
            ctx = this;
            gD = new GeneralData(ctx);
            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = (int) ((float) nHeight / (float) 2.4);

            TimeZone tz = TimeZone.getDefault();
            System.out.println("TimeZone " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

            timezonevalue=tz.getID();
            Log.i("TZ", "timezone" + timezonevalue);
            pD = new ProgressDialog(ProviderAppointment.this);
            emptyText = (TextView) findViewById(R.id.emptyText);
            lay_today = (LinearLayout) findViewById(R.id.lay_today);
            lay_tomorrow = (LinearLayout) findViewById(R.id.lay_tomorrow);
            lay_month = (LinearLayout) findViewById(R.id.lay_month);
            lay_all = (LinearLayout) findViewById(R.id.lay_all);
            text_head = (TextView) findViewById(R.id.textContent);
            menu_back = (ImageView) findViewById(R.id.menu_back);
            image_today = (ImageView) findViewById(R.id.image_today);
            image_tomorrow = (ImageView) findViewById(R.id.image_tomorrow);
            image_month = (ImageView) findViewById(R.id.image_month);
            image_all = (ImageView) findViewById(R.id.image_all);
            text_head.setText("Today");
            SplashActivity.sharedPreferences = getSharedPreferences("regid", Context.MODE_PRIVATE);

            image_today.setImageResource(R.drawable.appointment_red);
            recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            menu_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();

                    Intent voicpage = new Intent(ProviderAppointment.this, Provider_DashBoard.class);




                    startActivity(voicpage);
                }
            });

            lay_today.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    text_head.setText("Today");
                    image_today.setImageResource(R.drawable.appointment_red);
                    image_tomorrow.setImageResource(R.drawable.appointment_green);
                    image_month.setImageResource(R.drawable.appointment_green);
                    image_all.setImageResource(R.drawable.appointment_green);
                    showView("today");

                }
            });
            lay_tomorrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    text_head.setText("Tomorrow");
                    image_today.setImageResource(R.drawable.appointment_green);
                    image_tomorrow.setImageResource(R.drawable.appointment_red);
                    image_month.setImageResource(R.drawable.appointment_green);
                    image_all.setImageResource(R.drawable.appointment_green);
                    showView("tomorrow");


                }
            });
            lay_month.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    text_head.setText("This Month");
                    image_today.setImageResource(R.drawable.appointment_green);
                    image_tomorrow.setImageResource(R.drawable.appointment_green);
                    image_month.setImageResource(R.drawable.appointment_red);
                    image_all.setImageResource(R.drawable.appointment_green);
                    showView("month");


                }
            });
            lay_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    text_head.setText("All");
                    image_today.setImageResource(R.drawable.appointment_green);
                    image_tomorrow.setImageResource(R.drawable.appointment_green);
                    image_month.setImageResource(R.drawable.appointment_green);
                    image_all.setImageResource(R.drawable.appointment_red);
                    showView("all");

                }
            });
            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(ctx, "Unable to connect with Internet. Please check your internet connection and try again");
            }
            else {


                GetProviderDetails get_Provider_details = new GetProviderDetails(SplashActivity.sharedPreferences.getString("UID", null), ctx, recyclerView);
                get_Provider_details.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showView(String data) {
       /* Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();*/
        try {
            if (data == "today") {

                JSONArray jsArToday = null;
                LoadLayout(recyclerView, providerServicesToday);
                /*}*/
                if (providerServicesToday.length() > 0) {
                    emptyText.setVisibility(View.GONE);
                } else {
                    emptyText.setVisibility(View.VISIBLE);
                }

            } else if (data == "tomorrow") {
                /*if (providerServicesTom.length() > 0) {
                    JSONArray jsArToday = null;
                    LoadLayout(recyclerView, providerServicesTom);
                } else {
                    Toast.makeText(ProviderAppointment.this, "No items to be found !", Toast.LENGTH_SHORT).show();
                }*/
                LoadLayout(recyclerView, providerServicesTom);
                if (providerServicesTom.length() > 0) {
                    emptyText.setVisibility(View.GONE);
                } else {
                    emptyText.setVisibility(View.VISIBLE);
                }

            } else if (data == "month") {
                LoadLayout(recyclerView, providerServicesMonth);
                if (providerServicesMonth.length() > 0) {
                    emptyText.setVisibility(View.GONE);
                } else {
                    emptyText.setVisibility(View.VISIBLE);
                }


            } else {
                LoadLayout(recyclerView, providerServicesAll);
                if (providerServicesAll.length() > 0) {
                    emptyText.setVisibility(View.GONE);
                } else {
                    emptyText.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void LoadLayout(RecyclerView recyclerView, JSONArray providerServicesMonth) {


        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        ArrayList beanArrayList = new ArrayList<AppointmentBean>();

        try {


            for (int i = 0; i < providerServicesMonth.length(); i++) {

                AppointmentBean flowers = new AppointmentBean();
                JSONObject providersServiceJSONobject = providerServicesMonth.getJSONObject(i);
                flowers.setStr_name(providersServiceJSONobject.getString("username"));
                flowers.setStr_miles(providersServiceJSONobject.getString("rate"));
                flowers.setacceptStatus(providersServiceJSONobject.getString("iscomplete"));
                flowers.setRow_id(providersServiceJSONobject.getString("id"));

                flowers.setStr_jsongvalues(providersServiceJSONobject.toString());
                flowers.setStr_Service(providersServiceJSONobject.getString("service"));
                flowers.setStr_rate(providersServiceJSONobject.getString("date") + " " + providersServiceJSONobject.getString("starttime"));

                flowers.setStr_iscomplete(providersServiceJSONobject.getString("iscomplete"));
                flowers.setStr_leave(providersServiceJSONobject.getString("leaveforapp"));
                flowers.setStr_paidstatus(providersServiceJSONobject.getString("paidstatus"));
                flowers.setStr_generate(providersServiceJSONobject.getString("generate"));

                flowers.setStr_reached(providersServiceJSONobject.getString("reached"));
                flowers.setStr_pse(providersServiceJSONobject.getString("pse"));

                flowers.setStr_review(providersServiceJSONobject.getString("preview"));

                flowers.setStr_response(providersServiceJSONobject.getString("apptstatus"));

                Log.i("jj", "App_Status : " + providersServiceJSONobject.getString("apptstatus"));


                flowers.setN_user_image(gD.common_baseurl+"upload/" + providersServiceJSONobject.getString("image_path"));


                beanArrayList.add(flowers);
            }
            ProviderAppointmentAdapter mAdapter = new ProviderAppointmentAdapter(beanArrayList, ProviderAppointment.this, (GetBookingId) ctx);
            recyclerView.setAdapter(mAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void bookingid(String id, String strTitle) {
        Log.i("PP", "bookingid ***" + id);

        bookingID = id;
        CancelBooking generateinvoice = new CancelBooking(ctx, bookingID);
        generateinvoice.execute();
    }

    @Override
    public void serviceendid(String id, String strTitle) {

    }
    class CancelBooking extends AsyncTask {
        String strreason = null;
        String sResponse = null;
        Context cont;
        String row_id;

        public CancelBooking(Context context, String rowid) {
            this.cont = context;
            this.row_id = rowid;


        }

        @Override
        protected void onPreExecute() {
            ProviderAppointment.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gD.callload(ctx, nScreenHeight);
                }
            });
        }

        @Override
        protected Object doInBackground(Object[] param) {
            try {

                String requestURL = gD.common_baseurl+"acceptappointment.php?id=" + row_id + "&status=" + "3";

                //   APIcalls AceptApi = new APIcalls(requestURL);
                // AceptApi.Process();


                try {

                    URL url;
                    HttpURLConnection urlConnection = null;
                    try {
                        Log.i("SR", "Login URL : " + requestURL.trim());
                        System.out.print("strHTTP_LOGIN_URL");
                        url = new URL(requestURL.trim());

                      //  urlConnection.setRequestProperty("Accept-Encoding", "gzip");
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
        protected void onPostExecute(Object response) {

            if(gD.alertDialog!=null) {
                gD.alertDialog.dismiss();
            }
            try {
                JSONObject jsobj = new JSONObject(sResponse);
                Log.i("HH", "strResp : " + sResponse);
                if (jsobj.getInt("code") == 0) {


                    LayoutInflater inflater = LayoutInflater.from(ctx);
                    View dialogLayout = inflater.inflate(R.layout.layout_confirmation, null);

                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(ctx);
                    alertDialogBuilder.setView(dialogLayout);
                    alertDialogBuilder.setCancelable(true);


                    final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                    //  alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    alertDialog.show();

                    LinearLayout llayAlert = (LinearLayout) dialogLayout.findViewById(R.id.llayalertDialog);
                    // TextView tv_alert_Title = (TextView) dialogLayout.findViewById(R.id.tv_alert_Title);
                    TextView tv_alert_Message = (TextView) dialogLayout.findViewById(R.id.message);

                    tv_alert_Message.setText("Your booking has been cancelled ");

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


                          Intent i=new Intent(ProviderAppointment.this,ProviderAppointment.class);
                            startActivity(i);
                            finish();
                            alertDialog.dismiss();
                        }
                    });


                }


                else {
                    if(gD.alertDialog!=null) {
                        gD.alertDialog.dismiss();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
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


    @Override
    public void navigatepage(String iscomplete, String paidstatus, String generate, String reached, String pse, String review, String strtype, String strresponse, String leave, String responsevalue) {


        try {
            Log.i("JJ", "responsevalue.." + responsevalue);


            //first time click appoitment view-ok

            if (responsevalue.equalsIgnoreCase("appointment")) {

                strtype = "leave";

                Intent raiseinvoicerpage = new Intent(ProviderAppointment.this, ProviderArrived.class);

                raiseinvoicerpage.putExtra("msg", strresponse);

                Log.i("JJ", "jsonvales 1.." + strresponse);
                Log.i("JJ", "strtype 1.." + strtype);
                raiseinvoicerpage.putExtra("type", strtype);
                Log.i("JJ", "responsevalue 1.." + responsevalue);
                startActivity(raiseinvoicerpage);
                finish();
            }

            //after click appointment and went out-ok

            else if (responsevalue.equalsIgnoreCase("leaveforappointment")) {

                strtype = "afterclickleave";

                Intent raiseinvoicerpage = new Intent(ProviderAppointment.this, ProviderArrived.class);

                raiseinvoicerpage.putExtra("msg", strresponse);
                raiseinvoicerpage.putExtra("type", strtype);

                Log.i("JJ", "jsonvales.." + strresponse);
                Log.i("JJ", "strtype 1.." + strtype);
                Log.i("JJ", "responsevalue 1.." + responsevalue);

                startActivity(raiseinvoicerpage);
                finish();
            }


            //after click reach first time and went out

            else if (responsevalue.equalsIgnoreCase("reached")) {

                strtype = "clickreach";

                Intent raiseinvoicerpage = new Intent(ProviderAppointment.this, ProviderArrived.class);

                raiseinvoicerpage.putExtra("msg", strresponse);
                raiseinvoicerpage.putExtra("type", strtype);

                Log.i("JJ", "jsonvales.." + strresponse);
                Log.i("JJ", "strtype 1.." + strtype);
                Log.i("JJ", "responsevalue 1.." + responsevalue);

                startActivity(raiseinvoicerpage);
                finish();
            }

            //after click reach second time and went out

            else if (responsevalue.equalsIgnoreCase("useracceptreach")) {

                strtype = "reached";

                Intent raiseinvoicerpage = new Intent(ProviderAppointment.this, ProviderArrived.class);

                raiseinvoicerpage.putExtra("msg", strresponse);
                raiseinvoicerpage.putExtra("type", strtype);

                Log.i("JJ", "jsonvales.." + strresponse);
                Log.i("JJ", "strtype 1.." + strtype);
                Log.i("JJ", "responsevalue 1.." + responsevalue);

                startActivity(raiseinvoicerpage);
                finish();
            }


            //after click start process and went out

            else if (responsevalue.equalsIgnoreCase("providerstarted")) {

                strtype = "clickstart";

                Intent raiseinvoicerpage = new Intent(ProviderAppointment.this, ProviderArrived.class);

                raiseinvoicerpage.putExtra("msg", strresponse);
                raiseinvoicerpage.putExtra("type", strtype);

                Log.i("JJ", "jsonvales.." + strresponse);
                Log.i("JJ", "strtype 1.." + strtype);
                Log.i("JJ", "responsevalue 1.." + responsevalue);

                startActivity(raiseinvoicerpage);
                finish();
            }


            //after click end start process and went out

            else if (responsevalue.equalsIgnoreCase("useracceptstarted")) {

                strtype = "endstart";

                Intent raiseinvoicerpage = new Intent(ProviderAppointment.this, ProviderArrived.class);

                raiseinvoicerpage.putExtra("msg", strresponse);
                raiseinvoicerpage.putExtra("type", strtype);

                Log.i("JJ", "jsonvales.." + strresponse);
                Log.i("JJ", "strtype 1.." + strtype);
                Log.i("JJ", "responsevalue 1.." + responsevalue);

                startActivity(raiseinvoicerpage);
                finish();
            }
            //after click start process click stop and went out

            else if (responsevalue.equalsIgnoreCase("providerended")) {

                strtype = "clickstop";

                Intent raiseinvoicerpage = new Intent(ProviderAppointment.this, Provider_CompleteRaisePage.class);

                raiseinvoicerpage.putExtra("sR", strresponse);
                raiseinvoicerpage.putExtra("type", strtype);

                Log.i("JJ", "jsonvales.." + strresponse);
                Log.i("JJ", "strtype 1.." + strtype);
                Log.i("JJ", "responsevalue 1.." + responsevalue);

                startActivity(raiseinvoicerpage);
                finish();
            }

    //after click start process click stop and went out

            else if (responsevalue.equalsIgnoreCase("useracceptended")) {

                strtype = "generate";
                Intent raiseinvoicerpage = new Intent(ProviderAppointment.this, Provider_Generate.class);


                sharedpreferences = getSharedPreferences("GenerateId", Context.MODE_PRIVATE);

                SharedPreferences.Editor editname = sharedpreferences.edit();
                editname.putInt("isGenerate", 0);

                editname.commit();

                raiseinvoicerpage.putExtra("sR", strresponse);
                raiseinvoicerpage.putExtra("type", strtype);

                Log.i("JJ", "jsonvales.." + strresponse);
                Log.i("JJ", "strtype 1.." + strtype);
                Log.i("JJ", "responsevalue 1.." + responsevalue);

                startActivity(raiseinvoicerpage);
                finish();
            }


            // raise invoice page

            else if (responsevalue.equalsIgnoreCase("invoicegenerated")) {

                strtype = "raisepage";

                Intent raiseinvoicerpage = new Intent(ProviderAppointment.this, Provider_CompleteRaisePage.class);

                raiseinvoicerpage.putExtra("sR", strresponse);
                raiseinvoicerpage.putExtra("type", strtype);

                Log.i("JJ", "jsonvales.." + strresponse);
                Log.i("JJ", "responsevalue 1.." + responsevalue);
                Log.i("JJ", "strtype 1.." + strtype);

                startActivity(raiseinvoicerpage);
                finish();
            }

            // raise invoice page

            else if (responsevalue.equalsIgnoreCase("userpaid")) {


                strtype = "raisepage";

                Intent raiseinvoicerpage = new Intent(ProviderAppointment.this, Provider_CompleteRaisePage.class);

                raiseinvoicerpage.putExtra("sR", strresponse);
                raiseinvoicerpage.putExtra("type", strtype);

                Log.i("JJ", "jsonvales.." + strresponse);
                Log.i("JJ", "responsevalue 1.." + responsevalue);
                Log.i("JJ", "strtype 1.." + strtype);

                startActivity(raiseinvoicerpage);
                finish();

            } else if (responsevalue.equalsIgnoreCase("cancelled")) {
                alert.showAlertDialog(
                        this,
                        "This booking has been already cancelled",
                        false);
            } else {

                strtype = "generate";
                Intent raiseinvoicerpage = new Intent(ProviderAppointment.this, Provider_Generate.class);


                sharedpreferences = getSharedPreferences("GenerateId", Context.MODE_PRIVATE);

                SharedPreferences.Editor editname = sharedpreferences.edit();
                editname.putInt("isGenerate", 0);

                editname.commit();

                raiseinvoicerpage.putExtra("sR", strresponse);
                raiseinvoicerpage.putExtra("type", strtype);

                Log.i("JJ", "jsonvales.." + strresponse);
                Log.i("JJ", "strtype 1.." + strtype);

                startActivity(raiseinvoicerpage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void paidstatus(String id, String strTitle, String payservices, String rate, String name, String image, String prid) {

    }

    @Override
    public void onBackPressed() {
        finish();
        Intent voicpage = new Intent(ProviderAppointment.this, Provider_DashBoard.class);




        startActivity(voicpage);
    }
    @Override
    public void onResume() {

        TimeZone tz = TimeZone.getDefault();
        System.out.println("TimeZone " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

        timezonevalue = tz.getID();

        super.onResume();



    }    class GetProviderDetails extends AsyncTask {
        String user_id = null;
        String sResponse = null;
        Context cont;
        RecyclerView recycler;

        public GetProviderDetails(String userid, Context context, RecyclerView recyclerView) {
            this.user_id = userid;
            this.cont = context;
            this.recycler = recyclerView;

        }

        @Override
        protected void onPreExecute() {
            ProviderAppointment.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    gD.callload(ctx, nScreenHeight);
                }
            });
        }

        @Override
        protected Object doInBackground(Object[] param) {
            try {
                String charset = "UTF-8";
                Log.i("PPP", "providerid : " + SplashActivity.sharedPreferences.getString("UID", null));
                String requestURL = gD.common_baseurl+"appointment.php";
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                multipart.addHeaderField("Content-Encoding", "gzip");
                multipart.addFormField("timezone", timezonevalue.trim());
                Log.i("RR", "timezonevalue" + timezonevalue);

                multipart.addFormField("providerid", SplashActivity.sharedPreferences.getString("UID", null));
                List<String> response = multipart.finish();

                StringBuilder sb = new StringBuilder();
                for (String line : response) {
                    System.out.println(line);
                    sb.append(line);
                }
                try {
                    JSONObject jsonObj = new JSONObject(sb.toString());
                    Log.i("PPP", "StrResp : " + jsonObj.toString());
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
                ArrayList beanArrayList = new ArrayList<AppointmentBean>();

                JSONObject jsobj = new JSONObject(sResponse);
                JSONObject providersServiceJSONobject = null;
                Log.i("HH", "strResp : " + sResponse);
                if (jsobj.getString("status").equalsIgnoreCase("success")) {


                    JSONArray providerServicesJSONArray = jsobj.getJSONArray("today");
                    providerServicesToday = jsobj.getJSONArray("today");
                    providerServicesTom = jsobj.getJSONArray("tomorrow");
                    providerServicesMonth = jsobj.getJSONArray("week");
                    providerServicesAll = jsobj.getJSONArray("month");
                    Log.i("GG", "providerServicesToday : " + providerServicesToday);

                    if (providerServicesToday.length() > 0) {
                        for (int i = 0; i < providerServicesJSONArray.length(); i++) {

                            AppointmentBean flowers = new AppointmentBean();
                            providersServiceJSONobject = providerServicesToday.getJSONObject(i);
                            Log.i("GG", "providersServiceJSONobject : " + providersServiceJSONobject);
                            flowers.setStr_name(providersServiceJSONobject.getString("username"));
                            flowers.setStr_miles(providersServiceJSONobject.getString("rate"));
                            flowers.setacceptStatus(providersServiceJSONobject.getString("iscomplete"));
                            flowers.setStr_jsongvalues(providersServiceJSONobject.toString());


                            flowers.setRow_id(providersServiceJSONobject.getString("id"));
                            flowers.setStr_iscomplete(providersServiceJSONobject.getString("iscomplete"));
                            flowers.setStr_leave(providersServiceJSONobject.getString("leaveforapp"));
                            flowers.setStr_paidstatus(providersServiceJSONobject.getString("paidstatus"));
                            flowers.setStr_generate(providersServiceJSONobject.getString("generate"));

                            flowers.setStr_reached(providersServiceJSONobject.getString("reached"));
                            flowers.setStr_pse(providersServiceJSONobject.getString("pse"));

                            flowers.setStr_review(providersServiceJSONobject.getString("preview"));


                            flowers.setStr_response(providersServiceJSONobject.getString("apptstatus"));


                            flowers.setStr_Service(providersServiceJSONobject.getString("service"));
                            flowers.setStr_rate(providersServiceJSONobject.getString("date") +"" +providersServiceJSONobject.getString("starttime"));
                            flowers.setN_user_image(gD.common_baseurl+"upload/" + providersServiceJSONobject.getString("image_path"));
                            beanArrayList.add(flowers);
                        }
                    } else {
                        emptyText.setVisibility(View.VISIBLE);
                    }
                }
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recycler.setLayoutManager(mLayoutManager);
                recycler.setItemAnimator(new DefaultItemAnimator());
                ProviderAppointmentAdapter mAdapter = new ProviderAppointmentAdapter(beanArrayList, ProviderAppointment.this, (GetBookingId) ctx);
                recycler.setAdapter(mAdapter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}

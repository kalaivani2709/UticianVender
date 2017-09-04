package com.aryvart.uticianvender.admin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aryvart.uticianvender.Interface.GetNotification;
import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.adapter.Provider_Account_Adpater;
import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.aryvart.uticianvender.genericclasses.MultipartUtility;
import com.aryvart.uticianvender.utician.SplashActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.TreeMap;

/**
 * Created by android3 on 26/8/16.
 */
public class Admin_HistoryPage extends Activity implements GetNotification {
    ImageView menu_back;
    LinearLayout lay_transaction, lay_invoice;
    ImageView image_transaction, image_invoice;
    TextView textContent, balance;
    ProgressDialog pD;
    JSONArray notifyPending, notifyfulltrans;
    RecyclerView recyclerView;
    TextView emptyText;
    Context ctx;
    String timezonevalue;

    private Provider_Account_Adpater mAdapter;
GeneralData gD;
    int nScreenHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.admin_history);

            SplashActivity.sharedPreferences=getSharedPreferences("regid", Context.MODE_PRIVATE);

            TimeZone tz = TimeZone.getDefault();
            System.out.println("TimeZone " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

            timezonevalue=tz.getID();
            Log.i("TZ", "timezone" + timezonevalue);

            pD = new ProgressDialog(Admin_HistoryPage.this);
            ctx = this;
            gD = new GeneralData(ctx);

            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = (int) ((float) nHeight / (float) 2.1);

            textContent = (TextView) findViewById(R.id.textContent);
            balance = (TextView) findViewById(R.id.balance);
            emptyText = (TextView) findViewById(R.id.emptyText);
            menu_back = (ImageView) findViewById(R.id.menu_back);
            image_transaction = (ImageView) findViewById(R.id.image_transaction);
            image_invoice = (ImageView) findViewById(R.id.image_invoice);
            //  image_account = (ImageView) findViewById(R.id.image_account);
            recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            lay_transaction = (LinearLayout) findViewById(R.id.lay_transaction);
            lay_invoice = (LinearLayout) findViewById(R.id.lay_invoice);
            //  lay_account = (LinearLayout) findViewById(R.id.lay_account);
            textContent.setText("Completed Transactions");
            image_transaction.setImageResource(R.drawable.acompletered);
       /* showView("transactions");*/

            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(ctx, "Unable to connect with Internet. Please check your internet connection and try again");
            }else {
                GetTranscationDetails get_Provider_details = new GetTranscationDetails(ctx, recyclerView);
                get_Provider_details.execute();
            }
            lay_transaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textContent.setText("Completed Transactions");
                    image_transaction.setImageResource(R.drawable.acompletered);
                    image_invoice.setImageResource(R.drawable.apendingreennew);
                    //  image_account.setImageResource(R.drawable.acc_summary_green);
                    showView("completed");

                }
            });
            lay_invoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    textContent.setText("Pending Transactions");
                    image_transaction.setImageResource(R.drawable.acompletegreen);
                    image_invoice.setImageResource(R.drawable.apendingrednew);
                    //  image_account.setImageResource(R.drawable.acc_summary_green);
                    showView("pending");

                }
            });

            menu_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();

                        Intent i = new Intent(ctx, AdminList.class);
                        //  gD.screenback = 0;
                        startActivity(i);

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

    private void showView(String data) {
       /* Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();*/
        try {
            if (data .equalsIgnoreCase("completed")) {
                LoadLayout(recyclerView, notifyfulltrans, data);
                if (notifyfulltrans.length() > 0) {
                    emptyText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    emptyText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }

            } else {
               /* if (providerServicesPrevWeek.length() > 0) {*/
                LoadLayout(recyclerView, notifyPending, data);
               /* } else {
                    Toast.makeText(UserHistoryPage.this, "No items to be found !", Toast.LENGTH_SHORT).show();
                }*/
                if (notifyPending.length() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyText.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    emptyText.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(ctx, AdminList.class);
        //  gD.screenback = 0;
        startActivity(i);
    }
    private void LoadLayout(RecyclerView recyclerView, JSONArray providerServicesMonth, String data) {

        String name = null, image = null, time = null, date = null;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
     /*   recyclerView.addItemDecoration(new com.aryvart.utician.adapter.DividerItemDecoration(this, LinearLayoutManager.VERTICAL));*/
        ArrayList beanArrayList = new ArrayList<AdminProviderListBean>();

        JSONObject userNotifyJSONobject = null;
        JSONObject usertransactionJSONobject = null;
        // Log.i("action", String.valueOf(providerServicesMonth));
        // Log.i("info", String.valueOf(providerServicesMonth));


        try {
            for (int i = 0; i < providerServicesMonth.length(); i++) {
                AdminProviderListBean adminProviderbean = new AdminProviderListBean();
                usertransactionJSONobject = providerServicesMonth.getJSONObject(i);
                adminProviderbean.setProvider_ID(usertransactionJSONobject.getString("userid"));
                adminProviderbean.setProvider_Name(usertransactionJSONobject.getString("providername"));
                adminProviderbean.setProvider_image(gD.common_baseurl+"upload/" + usertransactionJSONobject.getString("providerimage"));
                adminProviderbean.setCompany_name(usertransactionJSONobject.getString("pdate"));
                adminProviderbean.setStr_pro_balance("$" + usertransactionJSONobject.getString("totalrate"));
                adminProviderbean.setStr_jsonvalues(usertransactionJSONobject.toString());
                beanArrayList.add(adminProviderbean);
            }

            mAdapter = new Provider_Account_Adpater(beanArrayList, Admin_HistoryPage.this, (GetNotification) ctx);
            recyclerView.setAdapter(mAdapter);

        } catch (Exception e) {
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

        Intent i = new Intent(Admin_HistoryPage.this, Admin_Transaction_details.class);
        i.putExtra("providerid", strProviderId);
        i.putExtra("jsonvalues", strType);
        Log.i("II..", "str_providerid" + strProviderId);
        Log.i("II..", "jsonvalues" + strType);
        startActivity(i);
        finish();



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

    class GetTranscationDetails extends AsyncTask {
        String user_id = null;
        String sResponse = null;
        Context cont;
        RecyclerView recycler;

        public GetTranscationDetails(Context context, RecyclerView recyclerView) {

            this.cont = context;
            this.recycler = recyclerView;

        }

        @Override
        protected void onPreExecute() {
            Admin_HistoryPage.this.runOnUiThread(new Runnable() {
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
                String requestURL = gD.common_baseurl+"admintransactions.php";
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);

                multipart.addHeaderField("Content-Encoding", "gzip");
                multipart.addFormField("timezone", timezonevalue.trim());
                Log.i("RR", "timezonevalue" + timezonevalue);
                multipart.addHeaderField("Accept", "application*/*");

                multipart.addHeaderField("Content-type", "application/x-www-form-urlencoded");
               // multipart.addFormField("providerid", SplashActivity.sharedPreferences.getString("UID", null));
                List<String> response = multipart.finish();

                StringBuilder sb = new StringBuilder();
                for (String line : response) {
                    System.out.println(line);
                    sb.append(line);
                }
                try {
                    JSONObject jsonObj = new JSONObject(sb.toString());
                    Log.i("NNN-notify", "StrResp : " + jsonObj.toString());
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
                String name = null, image = null, time = null, date = null;
                ArrayList beanArrayList = new ArrayList<AdminProviderListBean>();
                Log.i("HH", "strResp : " + beanArrayList.size());
                JSONObject jsobj = new JSONObject(sResponse);
                JSONObject usertransactionJSONobject = null;

                Log.i("HH", "strResp : " + sResponse);

                if (jsobj.getString("status").equalsIgnoreCase("success")) {
                    JSONArray providerActionJSONArray = jsobj.getJSONArray("completed");
                    notifyPending = jsobj.getJSONArray("pending");
                     notifyfulltrans = jsobj.getJSONArray("completed");

                    balance.setText("$" + jsobj.getString("admintotal"));

                    Log.i("PP","total" + jsobj.getString("admintotal"));
                    if (providerActionJSONArray.length() > 0) {
                        for (int i = 0; i < providerActionJSONArray.length(); i++) {
                            AdminProviderListBean adminProviderbean = new AdminProviderListBean();
                            usertransactionJSONobject = notifyfulltrans.getJSONObject(i);
                            adminProviderbean.setProvider_ID(usertransactionJSONobject.getString("userid"));
                            adminProviderbean.setProvider_Name(usertransactionJSONobject.getString("providername"));
                            adminProviderbean.setProvider_image(gD.common_baseurl+"upload/" + usertransactionJSONobject.getString("providerimage"));
                            adminProviderbean.setCompany_name(usertransactionJSONobject.getString("pdate"));
                            adminProviderbean.setStr_pro_balance("$" + usertransactionJSONobject.getString("totalrate"));
                            adminProviderbean.setStr_jsonvalues(usertransactionJSONobject.toString());
                            beanArrayList.add(adminProviderbean);
                        }
                    } else {
                        emptyText.setVisibility(View.VISIBLE);
                    }

                }

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recycler.setLayoutManager(mLayoutManager);
                recycler.setItemAnimator(new DefaultItemAnimator());

                mAdapter = new Provider_Account_Adpater(beanArrayList, Admin_HistoryPage.this, (GetNotification) ctx);
                recycler.setAdapter(mAdapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


}

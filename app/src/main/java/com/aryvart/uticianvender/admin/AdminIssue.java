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

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.bean.UserActionNotificationBean;
import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.aryvart.uticianvender.genericclasses.MultipartUtility;
import com.aryvart.uticianvender.utician.SplashActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by android3 on 23/7/16.
 */
public class AdminIssue extends Activity  {
    LinearLayout lay_action, lay_info;
    TextView text_head;
    ProgressDialog pd;
    RecyclerView recyclerView;
    Context ctx;
    private AdminIssueAdapter mAdapter;
    ImageView image_action, image_info, menu_back;
    TextView emptyText;
    ProgressDialog pD;
    JSONArray notifyInfo, notifyAction;
    View itemView1;
    String str_uid;
    String timezonevalue;
    GeneralData gD;
    int nScreenHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.admin_issues);
            ctx = this;
            ctx = this;
            gD = new GeneralData(ctx);

            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = (int) ((float) nHeight / (float) 2.1);

            TimeZone tz = TimeZone.getDefault();
            System.out.println("TimeZone " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

            timezonevalue=tz.getID();
            Log.i("TZ", "timezone" + timezonevalue);
            lay_action = (LinearLayout) findViewById(R.id.lay_action);
            emptyText = (TextView) findViewById(R.id.emptyText);
            lay_info = (LinearLayout) findViewById(R.id.lay_info);
            text_head = (TextView) findViewById(R.id.textContent);
            menu_back = (ImageView) findViewById(R.id.menu_back);
            image_action = (ImageView) findViewById(R.id.image_action);
            image_info = (ImageView) findViewById(R.id.image_info);
            text_head.setText("Issues");
            image_action.setImageResource(R.drawable.a_userred);
            recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            menu_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    Intent i = new Intent(ctx, AdminList.class);
                    //  gD.screenback = 0;
                    startActivity(i);
                }
            });
            pD = new ProgressDialog(AdminIssue.this);
            lay_action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    text_head.setText("Issues");
                    image_action.setImageResource(R.drawable.a_userred);
                    image_info.setImageResource(R.drawable.providerred);
                    showView("userside");

                }
            });
            lay_info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    text_head.setText("Issues");
                    image_action.setImageResource(R.drawable.a_providergreen);
                    image_info.setImageResource(R.drawable.userissue);
                    showView("providerside");

                }
            });
            SplashActivity.sharedPreferences=getSharedPreferences("regid", Context.MODE_PRIVATE);

            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(ctx, "Unable to connect with Internet. Please check your internet connection and try again");
            }
            else
            {
            GetAdminIssues get_Provider_details = new GetAdminIssues( ctx, recyclerView);
            get_Provider_details.execute();}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showView(String data) {
       /* Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();*/
        try {
            if (data == "userside") {
                LoadLayout(recyclerView, notifyAction, data);
                if (notifyAction.length() > 0) {
                    emptyText.setVisibility(View.GONE);
                } else {
                    emptyText.setVisibility(View.VISIBLE);
                }

            } else {
               /* if (providerServicesPrevWeek.length() > 0) {*/
                LoadLayout(recyclerView, notifyInfo, data);
               /* } else {
                    Toast.makeText(UserHistoryPage.this, "No items to be found !", Toast.LENGTH_SHORT).show();
                }*/
                if (notifyInfo.length() > 0) {
                    emptyText.setVisibility(View.GONE);
                } else {
                    emptyText.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void LoadLayout(RecyclerView recyclerView, JSONArray providerServicesMonth, String data) {

        String name = null, image = null, time = null, date = null;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
     /*   recyclerView.addItemDecoration(new com.aryvart.utician.adapter.DividerItemDecoration(this, LinearLayoutManager.VERTICAL));*/
        ArrayList<UserActionNotificationBean> beanArrayList = new ArrayList<UserActionNotificationBean>();
      //  ArrayList<UserInfoNotificationBean> beanArrayList1 = new ArrayList<UserInfoNotificationBean>();
        JSONObject userNotifyJSONobject = null;
        JSONObject userInfoJSONobject = null;


        if (data.equals("userside")) {
            try {
                for (int i = 0; i < providerServicesMonth.length(); i++) {
                    UserActionNotificationBean userNotifyBean = new UserActionNotificationBean();
                    userInfoJSONobject = providerServicesMonth.getJSONObject(i);
                    userNotifyBean.setN_user_image(gD.common_baseurl+"upload/" + userInfoJSONobject.getString("userimage"));
                    userNotifyBean.setStr_name(userInfoJSONobject.getString("username"));
                    userNotifyBean.setStr_time(userInfoJSONobject.getString("idtype"));
                    beanArrayList.add(userNotifyBean);

                }
                AdminIssueAdapter userHistoryAdpater = new AdminIssueAdapter(beanArrayList, AdminIssue.this);
                recyclerView.setAdapter(userHistoryAdpater);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (data.equals("providerside")) {
            try {
                for (int i = 0; i < providerServicesMonth.length(); i++) {
                    UserActionNotificationBean userNotifyBean = new UserActionNotificationBean();
                    userInfoJSONobject = providerServicesMonth.getJSONObject(i);
                    userNotifyBean.setN_user_image(gD.common_baseurl+"upload/" + userInfoJSONobject.getString("providerimage"));
                    userNotifyBean.setStr_name(userInfoJSONobject.getString("providername"));
                    userNotifyBean.setStr_time(userInfoJSONobject.getString("idtype"));


                    beanArrayList.add(userNotifyBean);
                }
                AdminProviderIssueAdapter adminissue = new AdminProviderIssueAdapter(beanArrayList, AdminIssue.this);
                recyclerView.setAdapter(adminissue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(ctx, AdminList.class);
        //  gD.screenback = 0;
        startActivity(i);
    }

    class GetAdminIssues extends AsyncTask {
        String user_id = null;
        String sResponse = null;
        Context cont;
        RecyclerView recycler;

        public GetAdminIssues( Context context, RecyclerView recyclerView) {

            this.cont = context;
            this.recycler = recyclerView;

        }

        @Override
        protected void onPreExecute() {
            AdminIssue.this.runOnUiThread(new Runnable() {
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
                String requestURL = gD.common_baseurl+"issuelist.php";
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);

                multipart.addHeaderField("Content-Encoding", "gzip");
                multipart.addFormField("timezone", timezonevalue.trim());
                Log.i("RR", "timezonevalue" + timezonevalue);
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
            try {
                String name = null, image = null, time = null, date = null;
                ArrayList beanArrayList = new ArrayList<UserActionNotificationBean>();
                Log.i("HH", "strResp : " + beanArrayList.size());
                JSONObject jsobj = new JSONObject(sResponse);
                JSONObject userNotificationJSONobject = null;

                Log.i("HH", "strResp : " + sResponse);

                if (jsobj.getString("status").equalsIgnoreCase("success")) {
                    JSONArray providerActionJSONArray = jsobj.getJSONArray("user");
                    notifyInfo = jsobj.getJSONArray("provider");
                    notifyAction = jsobj.getJSONArray("user");

                    if (providerActionJSONArray.length() > 0) {
                        for (int i = 0; i < providerActionJSONArray.length(); i++) {
                            UserActionNotificationBean bean = new UserActionNotificationBean();
                            userNotificationJSONobject = notifyAction.getJSONObject(i);
                            Log.i("GG", "userNotifyJSONobject : " + gD.common_baseurl+"upload/" + userNotificationJSONobject.getString("userimage"));
                            bean.setN_user_image(gD.common_baseurl+"upload/" + userNotificationJSONobject.getString("userimage"));
                            bean.setStr_name(userNotificationJSONobject.getString("username"));
                            bean.setStr_time(userNotificationJSONobject.getString("idtype"));
                            beanArrayList.add(bean);
                        }
                    }
                 else{
                        emptyText.setVisibility(View.VISIBLE);
                    }



                    if(gD.alertDialog!=null) {
                        gD.alertDialog.dismiss();
                    }
                }

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recycler.setLayoutManager(mLayoutManager);
                recycler.setItemAnimator(new DefaultItemAnimator());

                mAdapter = new AdminIssueAdapter(beanArrayList, AdminIssue.this);
                recycler.setAdapter(mAdapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    @Override
    protected void onResume() {
        TimeZone tz = TimeZone.getDefault();
        System.out.println("TimeZone " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

        timezonevalue = tz.getID();
        super.onResume();

    }
}

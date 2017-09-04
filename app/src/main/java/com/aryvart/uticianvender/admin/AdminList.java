package com.aryvart.uticianvender.admin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryvart.uticianvender.Interface.GetNotification;
import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.aryvart.uticianvender.genericclasses.MultipartUtility;

import com.aryvart.uticianvender.imageCache.ImageLoader;
import com.aryvart.uticianvender.utician.SignInActivity;
import com.aryvart.uticianvender.utician.SplashActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.TreeMap;

/**
 * Created by android3 on 20/7/16.
 */
public class AdminList extends Activity implements GetNotification {
    static SharedPreferences sharedpreferences;
    LinearLayout lay_changepassword, lay_notification,lay_Share_Pay, lay_out_slider, ll_logout, lay_pagination, lay_transaction, lay_paymentsetting, lay_issues, llay_newarrival, lay_accepted, lay_decline;
    TextView text_head;
    String timezonevalue;

    RecyclerView recyclerView;
    ImageView userimage;
    AdminProvidersListAdapter mAdapter;
    Context ctx;
    ImageView image_prev_week, image_declined, image_accepted, img_arrival, menu_back;
    JSONArray providerServicesToday, providerServicesaccepted, providerServicesAll, providerServicesdeclined;
    TextView emptyText, username;
    ImageView menu_drawer;
    DrawerLayout sliding_filter;
    ProgressDialog dialog;
    ImageLoader imgLoader;
    int nScreenHeight;
    static int screenback=0;
    GeneralData gD;
    RelativeLayout lay_slider;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.admin_list);

            ctx = this;

            TimeZone tz = TimeZone.getDefault();
            System.out.println("TimeZone " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

            timezonevalue=tz.getID();
            Log.i("TZ", "timezone" + timezonevalue);
            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = (int) ((float) nHeight / (float) 2.4);
            imgLoader = new ImageLoader(ctx);
            dialog = new ProgressDialog(AdminList.this);

            gD = new GeneralData(ctx);

            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
            SplashActivity.autoLoginPreference = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
            sharedpreferences = getSharedPreferences("myprefer", Context.MODE_PRIVATE);
            SplashActivity.sharedPreferences = getSharedPreferences("regid", Context.MODE_PRIVATE);
            username = (TextView) findViewById(R.id.user_name);
            userimage = (ImageView) findViewById(R.id.provider_im);


            username.setText(SplashActivity.sharedPreferences.getString("fullname", null));
            imgLoader.DisplayImage(SplashActivity.sharedPreferences.getString("userimage", null), userimage);
           // imgLoader.DisplayImage(gD.common_baseurl + "upload/" + SplashActivity.sharedPreferences.getString("user_image", null), userimage);

           // Log.i("IMG", gD.common_baseurl + "upload/" + SplashActivity.sharedPreferences.getString("user_image", null));




            menu_drawer = (ImageView) findViewById(R.id.menu_drawer);
            sliding_filter = (DrawerLayout) findViewById(R.id.my_drawer_layout);
            lay_out_slider = (LinearLayout) findViewById(R.id.lay_out_slider);
            lay_slider= (RelativeLayout) findViewById(R.id.lay_slider);
            lay_out_slider.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lay_slider.setClickable(false);
                    lay_slider.setEnabled(false);
                    lay_slider.setFocusableInTouchMode(false);
                    lay_slider.setFocusable(false);
                }
            });

            userimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(AdminList.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent help = new Intent(getApplicationContext(), AdminSettings.class);
                        startActivity(help);
                    }
                }
            });
            // menu
            lay_changepassword = (LinearLayout) findViewById(R.id.lay_changepassword);
            lay_issues = (LinearLayout) findViewById(R.id.lay_issues);
            lay_paymentsetting = (LinearLayout) findViewById(R.id.lay_paymentsetting);
            lay_transaction = (LinearLayout) findViewById(R.id.lay_transaction);
            lay_pagination = (LinearLayout) findViewById(R.id.lay_pagination);
            lay_notification = (LinearLayout) findViewById(R.id.lay_notification);
            lay_Share_Pay = (LinearLayout) findViewById(R.id.lay_Share_Pay);
            ll_logout = (LinearLayout) findViewById(R.id.lay_logout);

            menu_drawer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sliding_filter.openDrawer(lay_out_slider);
                }
            });
            lay_changepassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent profile = new Intent(getApplicationContext(), AdminSettings.class);
                    startActivity(profile);
                }
            });
            lay_issues.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent help = new Intent(getApplicationContext(), AdminIssue.class);
                    startActivity(help);
                }
            });
            lay_paymentsetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent help = new Intent(getApplicationContext(), Admin_Payment.class);
                    startActivity(help);
                }
            });
            lay_Share_Pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent help = new Intent(getApplicationContext(), Admin_Share_Pay.class);
                    startActivity(help);

                }
            });
            lay_transaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent help = new Intent(getApplicationContext(), Admin_HistoryPage.class);
                    startActivity(help);

                }
            });
            lay_pagination.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent help = new Intent(getApplicationContext(), UserProviderList.class);
                    startActivity(help);

                }
            });

            lay_notification.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent help = new Intent(getApplicationContext(), AdminNotification.class);
                    startActivity(help);

                }
            });
            ll_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Logout();
                }
            });


            llay_newarrival = (LinearLayout) findViewById(R.id.llay_newarrival);
            lay_accepted = (LinearLayout) findViewById(R.id.lay_accepted);
            lay_decline = (LinearLayout) findViewById(R.id.lay_decline);

            text_head = (TextView) findViewById(R.id.textContent);
            menu_back = (ImageView) findViewById(R.id.menu_back);
            img_arrival = (ImageView) findViewById(R.id.img_arrival);
            image_accepted = (ImageView) findViewById(R.id.image_accepted);
            image_declined = (ImageView) findViewById(R.id.image_declined);
            recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            text_head.setText("New Arrival");
            // showView("newarrival");

            img_arrival.setImageResource(R.drawable.history_red);


            llay_newarrival.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    text_head.setText("New Arrival");
                    img_arrival.setImageResource(R.drawable.history_red);
                    image_accepted.setImageResource(R.drawable.history_green);
                    image_declined.setImageResource(R.drawable.history_green);
                    emptyText = (TextView) findViewById(R.id.emptyText);
                    showView("newarrival");

                }
            });
            lay_accepted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    text_head.setText("Accepted");
                    image_accepted.setImageResource(R.drawable.history_red);
                    image_declined.setImageResource(R.drawable.history_green);
                    img_arrival.setImageResource(R.drawable.history_green);
                    emptyText = (TextView) findViewById(R.id.emptyText);
                    showView("accepted");

                }
            });
            lay_decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    text_head.setText("Declined");
                    image_declined.setImageResource(R.drawable.history_red);
                    image_accepted.setImageResource(R.drawable.history_green);
                    img_arrival.setImageResource(R.drawable.history_green);
                    emptyText = (TextView) findViewById(R.id.emptyText);
                    showView("decline");
                }
            });
            SplashActivity.sharedPreferences = getSharedPreferences("regid", Context.MODE_PRIVATE);
            String strUSerID = "";
            strUSerID = SplashActivity.sharedPreferences.getString("UID", null);
            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(ctx, "Unable to connect with Internet. Please check your internet connection and try again");
            }else
            {

            GetProviderDetails get_Provider_details = new GetProviderDetails(ctx, recyclerView);
            get_Provider_details.execute();}

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    GetProviderDetails get_Provider_details = new GetProviderDetails(ctx, recyclerView);
                    get_Provider_details.execute();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showView(String data) {
       /* Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();*/
        try {
            if (data.equalsIgnoreCase("newarrival")) {

                if (providerServicesAll.length() > 0) {
                    emptyText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    LoadLayout(recyclerView, providerServicesAll, "others");
                } else {
                    recyclerView.setVisibility(View.GONE);
                    emptyText.setVisibility(View.VISIBLE);
                }
            } else if (data.equalsIgnoreCase("accepted")) {

                if (providerServicesaccepted.length() > 0) {
                    emptyText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    LoadLayout(recyclerView, providerServicesaccepted, "accept");
                } else {
                    recyclerView.setVisibility(View.GONE);
                    emptyText.setVisibility(View.VISIBLE);
                }
            } else if (data.equalsIgnoreCase("decline")) {

                if (providerServicesdeclined.length() > 0) {
                    emptyText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    LoadLayout(recyclerView, providerServicesdeclined, "decline");
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
    protected void onResume() {

        gD.screenback = 0;
        Log.i("LL","screenback");
        TimeZone tz = TimeZone.getDefault();
        System.out.println("TimeZone " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

        timezonevalue = tz.getID();
        super.onResume();

    }
    @Override
    public void setNotificationId(String strGCMid) {

    }

    @Override
    public void setProviderDetails(String strGCMid, String strName, String strImage, String straddress, String expertiseid, String strReferalcdoe) {

    }




    @Override
    public void getProviderId(String strProviderId, String strType) {
        try {
            Intent i = new Intent(AdminList.this, Admin_ProfilePage.class);
            i.putExtra("providerid", strProviderId);
            i.putExtra("pType", strType);
            Log.i("II..", "str_providerid" + strProviderId);
            Log.i("II..", "strType" + strType);
            startActivity(i);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void LoadLayout(RecyclerView recyclerView, JSONArray providerServicesMonth, String strType) {

        String name = null, image = null, time = null, date = null;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
     /*   recyclerView.addItemDecoration(new com.aryvart.utician.adapter.DividerItemDecoration(this, LinearLayoutManager.VERTICAL));*/
        ArrayList<AdminProviderListBean> adMnProviderBean = new ArrayList<AdminProviderListBean>();
        JSONObject providersServiceJSONobject = null;
        try {
            for (int i = 0; i < providerServicesMonth.length(); i++) {
                AdminProviderListBean adminProviderbean = new AdminProviderListBean();
                providersServiceJSONobject = providerServicesMonth.getJSONObject(i);
                adminProviderbean.setStr_jsonvalues(providersServiceJSONobject.getString("providerid"));
                adminProviderbean.setProvider_ID(providersServiceJSONobject.getString("providerid"));
                adminProviderbean.setProvider_Name(providersServiceJSONobject.getString("providername"));
                adminProviderbean.setProvider_image(gD.common_baseurl+"upload/" + providersServiceJSONobject.getString("imagename"));
                adminProviderbean.setCompany_name(providersServiceJSONobject.getString("registered_at"));
                // adminProviderbean.setCompany_address(providersServiceJSONobject.getString("companyaddress"));


                if (strType.equalsIgnoreCase("accept")) {
                    adminProviderbean.setIsAccepted("1");
                } else if (strType.equalsIgnoreCase("decline")) {
                    adminProviderbean.setIsAccepted("2");
                } else if (strType.equalsIgnoreCase("others")) {
                    adminProviderbean.setIsAccepted("0");
                }
                adMnProviderBean.add(adminProviderbean);
                mAdapter = new AdminProvidersListAdapter(adMnProviderBean, ctx, (GetNotification) ctx);
                recyclerView.setAdapter(mAdapter);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Logout() {


        try {
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

            tv_alert_Message.setText("Do you want to logout");

            Button btn_submit = (Button) dialogLayout.findViewById(R.id.ok);
            Button btn_cancel = (Button) dialogLayout.findViewById(R.id.cancel);


            FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, nScreenHeight);
            llayAlert.setLayoutParams(lparams);


            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    LogoutOut logoutOut = new LogoutOut();
                    logoutOut.execute();

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


    class GetProviderDetails extends AsyncTask {
        String user_id = null;
        String sResponse = null;
        Context cont;
        RecyclerView recycler;

        public GetProviderDetails(Context context, RecyclerView recyclerView) {

            this.cont = context;
            this.recycler = recyclerView;

        }

        @Override
        protected void onPreExecute() {
            AdminList.this.runOnUiThread(new Runnable() {
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
                String requestURL = gD.common_baseurl+"allprovider.php";
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
                    Log.i("BBB", "StrResp : " + jsonObj.toString());
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
                ArrayList<AdminProviderListBean> adMnProviderBean = new ArrayList<AdminProviderListBean>();

                JSONObject jsobj = new JSONObject(sResponse);
                JSONObject providersServiceJSONobject = null;
                Log.i("HH", "strResp : " + sResponse);
                if (jsobj.getString("status").equalsIgnoreCase("success")) {
                    JSONArray providerServicesJSONArray = jsobj.getJSONArray("others");
                    providerServicesdeclined = jsobj.getJSONArray("declined");
                    providerServicesaccepted = jsobj.getJSONArray("accepted");
                    providerServicesAll = jsobj.getJSONArray("others");

                    if (providerServicesJSONArray.length() > 0) {
                        for (int i = 0; i < providerServicesJSONArray.length(); i++) {
                            AdminProviderListBean adminProviderbean = new AdminProviderListBean();
                            providersServiceJSONobject = providerServicesAll.getJSONObject(i);
                            adminProviderbean.setProvider_ID(providersServiceJSONobject.getString("providerid"));
                            adminProviderbean.setProvider_Name(providersServiceJSONobject.getString("providername"));
                            adminProviderbean.setProvider_image(gD.common_baseurl+"upload/" + providersServiceJSONobject.getString("imagename"));
                            adminProviderbean.setCompany_name(providersServiceJSONobject.getString("nregistered_at"));
                            adminProviderbean.setStr_jsonvalues(providersServiceJSONobject.getString("providerid"));
                            adminProviderbean.setIsAccepted("0");
                            adMnProviderBean.add(adminProviderbean);
                        }
                    } else {
                        emptyText = (TextView) findViewById(R.id.emptyText);
                        emptyText.setVisibility(View.VISIBLE);
                    }


                }

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recycler.setLayoutManager(mLayoutManager);
                recycler.setItemAnimator(new DefaultItemAnimator());
               /* recycler.addItemDecoration(new com.aryvart.utician.adapter.DividerItemDecoration(ProviderHistoryPage.this, LinearLayoutManager.VERTICAL));*/
                mAdapter = new AdminProvidersListAdapter(adMnProviderBean, ctx, (GetNotification) ctx);
                recyclerView.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    class LogoutOut extends AsyncTask {
        String sResponse = null;

        @Override
        protected Object doInBackground(Object[] params) {
            try {


                String charset = "UTF-8";

                String requestURL = gD.common_baseurl+"logout.php";


                // 4. separate class for multipart content image uploaded task----------- vinoth
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                multipart.addHeaderField("Content-Encoding", "gzip");
                multipart.addHeaderField("User-Agent", "rajaji");
                multipart.addHeaderField("Test-Header", "Header-Value");
                multipart.addFormField("keywords", "Java,upload,Spring");

                // 5. set the user_image key word and multipart image file value ----------- vinoth

                multipart.addFormField("id", SplashActivity.sharedPreferences.getString("UID", null));

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
            // TODO Auto-generated method stub
            AdminList.this.runOnUiThread(new Runnable() {
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
                        /*Toast.makeText(AdminList.this, " logged out successfuly !", Toast.LENGTH_SHORT).show();*/

                        SharedPreferences.Editor editor = SplashActivity.autoLoginPreference.edit();
                        editor.putString("uname", null);
                        editor.putString("pwd", null);
                        editor.commit();

                        Intent nextScreenIntent = new Intent(AdminList.this, SignInActivity.class);
                        startActivity(nextScreenIntent);
                        finish();
                        finishAffinity();

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


    @Override
    public void onBackPressed() {
        if (gD.screenback == 0) {

           // Intent voicpage = new Intent(AdminList.this, AdminList.class);




           // startActivity(voicpage);

                Log.i("HH","hello 8**" +gD.screenback);
            Toast.makeText(AdminList.this, "Please press again to exit", Toast.LENGTH_SHORT).show();

            gD.screenback = 1;
        }
        else /*if(gD.screenback == 1)*/
        {

            Log.i("HH","hello 8 **" +gD.screenback);
            finish();
            finishAffinity();
        }

    }
}


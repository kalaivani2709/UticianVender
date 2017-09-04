package com.aryvart.uticianvender.provider;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryvart.uticianvender.Interface.GetBookingId;
import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.adapter.Dash_AppointmentAdpater;
import com.aryvart.uticianvender.adapter.Notification_Request_Adapter;
import com.aryvart.uticianvender.bean.AppointmentBean;
import com.aryvart.uticianvender.bean.ProviderBean;
import com.aryvart.uticianvender.bean.UserHistoryBean;
import com.aryvart.uticianvender.bean.commonBeanSupport;
import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.aryvart.uticianvender.genericclasses.MultipartUtility;

import com.aryvart.uticianvender.imageCache.ImageLoader;
import com.aryvart.uticianvender.utician.Help;
import com.aryvart.uticianvender.utician.Share_Layout;
import com.aryvart.uticianvender.utician.SignInActivity;
import com.aryvart.uticianvender.utician.SplashActivity;
import com.google.android.gms.location.LocationRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by android3 on 13/9/16.
 */
public class Provider_DashBoard extends Activity implements GetBookingId {


    static SharedPreferences sharedpreferences;
    RatingBar ratingBar;
    ProgressDialog dialog;
    Button btn_submit_review;
    ImageView close;
    TextView txt_username;
    EditText edit_review;
    ;
    String strtype, str_rating, str_des, str_id;
    TextView tv_lastApp_userName, service_type, time, balance;
    ImageView userprofile, user_image;
    String str_rate_count, str_notify_count;
    ImageView dash_back;
    Button btn_today, btn_tismonth, btn_tomo, btn_all;
    RecyclerView recyclerView;
    Context ctx;
    RelativeLayout ll_notify, ll_rate_review;
    LinearLayout lay_out_slider, lay_share, ll_profile, ll_appointments, ll_history, ll_avail, ll_settings, ll_help, ll_logout;
    String ss;
    JSONArray providerServicesToday, providerServicesTom, providerServicesMonth, providerServicesAll;
    DrawerLayout sliding_filter;
    TextView emptyText;
    ProgressDialog pD;
    ImageLoader imgLoader;
    ListView listServices;
    LinearLayout next_appointmentLay, reach_lay, start_lay, stop_lay, paid_lay, complete_lay;
    TextView no_appointment, username, listempty, leaveforappoitment, txt_rate_count, txt_notify_count;
    int nScreenHeight, ScreenHeight;
    ImageView userimage;

    String status, nextappoitn_time;
    String service,rate;

    NotificationManager manager;
    Notification myNotication;
    GeneralData gD;
    String strusername, userid, imagepath;
    String p_lat, p_long, row_id, str_appstatus;

    int nnScreenHeight,mScreenHeight;


    Thread t;
    JSONObject jsobj = null;

String bookingID;
    static int screenback=0;
    String timezonevalue;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)
            .setFastestInterval(16)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE,
            Manifest.permission.CAMERA, Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_WIFI_STATE
    };
    //persmission method.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        int internetPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.INTERNET);
        int access_network_Permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_NETWORK_STATE);
        int access_fine_loc_Permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int access_coarse_loc_Permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        int callPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE);
        int cameraPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);
        int smsPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.SEND_SMS);
        int wifiPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_WIFI_STATE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED || internetPermission != PackageManager.PERMISSION_GRANTED ||
                access_network_Permission != PackageManager.PERMISSION_GRANTED ||
                access_fine_loc_Permission != PackageManager.PERMISSION_GRANTED ||
                access_coarse_loc_Permission != PackageManager.PERMISSION_GRANTED ||
                callPermission != PackageManager.PERMISSION_GRANTED ||
                cameraPermission != PackageManager.PERMISSION_GRANTED ||
                smsPermission != PackageManager.PERMISSION_GRANTED ||
                wifiPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    boolean doubleBackToExitPressedOnce = false;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.dashboard_design);
            ctx = this;
            GeneralData.context = ctx;

            gD = new GeneralData(ctx);

            verifyStoragePermissions(this);
            TimeZone tz = TimeZone.getDefault();
            System.out.println("TimeZone " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

            timezonevalue=tz.getID();
            Log.i("TZ", "timezone" + timezonevalue);

            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());


            Log.i("LL","time  ****: " + date.split(" ")[1]);
            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);

            imgLoader = new ImageLoader(ctx);
            txt_rate_count = (TextView) findViewById(R.id.rating_count);
            txt_notify_count = (TextView) findViewById(R.id.notify_count);
            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            ScreenHeight = (int) ((float) nHeight / (float) 2.2);
            nnScreenHeight = (int) ((float) nHeight / (float) 1.4);
            mScreenHeight = (int) ((float) nHeight / (float) 1.7);
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            sliding_filter = (DrawerLayout) findViewById(R.id.my_drawer_layout);
            lay_out_slider = (LinearLayout) findViewById(R.id.lay_out_slider);

            nScreenHeight = (int) ((float) nHeight / (float) 1.4);
            pD = new ProgressDialog(Provider_DashBoard.this);
            recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

            SplashActivity.autoLoginPreference = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
            sharedpreferences = getSharedPreferences("myprefer", Context.MODE_PRIVATE);
            SplashActivity.sharedPreferences = getSharedPreferences("regid", Context.MODE_PRIVATE);
            username = (TextView) findViewById(R.id.user_name);
            userimage = (ImageView) findViewById(R.id.provider_im);
            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(ctx, "Unable to connect with Internet. Please check your internet connection and try again");
            }
            else
            {
            GetProviderDetails get_Provider_details = new GetProviderDetails(SplashActivity.sharedPreferences.getString("UID", null), ctx, recyclerView);
            get_Provider_details.execute();}
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    GetProviderDetails get_Provider_details = new GetProviderDetails(SplashActivity.sharedPreferences.getString("UID", null), ctx, recyclerView);
                    get_Provider_details.execute();
                }
            });
            userimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_DashBoard.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent help = new Intent(getApplicationContext(), ProviderSettings.class);
                        startActivity(help);
                    }
                }
            });
            username.setText(SplashActivity.sharedPreferences.getString("fullname", null));
            // imgLoader.DisplayImage(SplashActivity.sharedPreferences.getString("userimage", null), userimage);

            imgLoader.DisplayImage( SplashActivity.sharedPreferences.getString("user_image", null), userimage);

            Log.i("IMG", SplashActivity.sharedPreferences.getString("user_image", null));

            tv_lastApp_userName = (TextView) findViewById(R.id.tv_lastApp_userName);
            time = (TextView) findViewById(R.id.time);
            next_appointmentLay = (LinearLayout) findViewById(R.id.next_appointmentLay);

            reach_lay = (LinearLayout) findViewById(R.id.reach);


            start_lay = (LinearLayout) findViewById(R.id.start);

            stop_lay = (LinearLayout) findViewById(R.id.stop);

            complete_lay = (LinearLayout) findViewById(R.id.complete);

            paid_lay = (LinearLayout) findViewById(R.id.paid);


            no_appointment = (TextView) findViewById(R.id.no_appointment);
            leaveforappoitment = (TextView) findViewById(R.id.leaveforappoitment);
            listempty = (TextView) findViewById(R.id.listempty);
            balance = (TextView) findViewById(R.id.balance);
            service_type = (TextView) findViewById(R.id.service_type);
            userprofile = (ImageView) findViewById(R.id.userprofile);
            emptyText = (TextView) findViewById(R.id.emptyText);
            listServices = (ListView) findViewById(R.id.booking_Request);


            btn_today = (Button) findViewById(R.id.btn_today);
            btn_tomo = (Button) findViewById(R.id.btn_tomo);
            btn_tismonth = (Button) findViewById(R.id.btn_tismonth);
            btn_all = (Button) findViewById(R.id.btn_all);


            dash_back = (ImageView) findViewById(R.id.dash_back);

            dash_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            // menu
            ll_profile = (LinearLayout) findViewById(R.id.lay_profile);
            ll_appointments = (LinearLayout) findViewById(R.id.lay_appointment);
            ll_history = (LinearLayout) findViewById(R.id.lay_history);
            ll_notify = (RelativeLayout) findViewById(R.id.lay_notify);
            ll_avail = (LinearLayout) findViewById(R.id.lay_avail);
            ll_settings = (LinearLayout) findViewById(R.id.lay_settings);
            ll_help = (LinearLayout) findViewById(R.id.lay_help);

            lay_share = (LinearLayout) findViewById(R.id.lay_share);
            ll_rate_review = (RelativeLayout) findViewById(R.id.lay_rating);
            ll_logout = (LinearLayout) findViewById(R.id.lay_logout);
            GetProviderReviews getProviderReviews = new GetProviderReviews(SplashActivity.sharedPreferences.getString("UID", null), ctx);
            getProviderReviews.execute();

            NotificationCount notificationCount = new NotificationCount(SplashActivity.sharedPreferences.getString("UID", null), ctx);
            notificationCount.execute();


            dash_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sliding_filter.openDrawer(lay_out_slider);


                    NotificationCount notificationCount = new NotificationCount(SplashActivity.sharedPreferences.getString("UID", null), ctx);
                    notificationCount.execute();

                    GetProviderReviews getProviderReviews = new GetProviderReviews(SplashActivity.sharedPreferences.getString("UID", null), ctx);
                    getProviderReviews.execute();


                }
            });
            ll_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_DashBoard.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent help = new Intent(getApplicationContext(), Provider_Profile_Edit.class);
                        startActivity(help);
                    }
                }
            });
            ll_appointments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_DashBoard.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent help = new Intent(getApplicationContext(), ProviderAppointment.class);
                        startActivity(help);
                    }
                }
            });


            ll_history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_DashBoard.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent help = new Intent(getApplicationContext(), ProviderHistoryPage.class);
                        startActivity(help);
                    }
                }
            });
            ll_notify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_DashBoard.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent help = new Intent(getApplicationContext(), ProviderNotification.class);
                        startActivity(help);
                    }
                }
            });
            ll_avail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_DashBoard.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent help = new Intent(getApplicationContext(), ProviderAvailability.class);
                        startActivity(help);
                    }
                }
            });
            ll_settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_DashBoard.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent help = new Intent(getApplicationContext(), ProviderSettings.class);
                        startActivity(help);
                    }
                }
            });
            ll_help.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_DashBoard.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent help = new Intent(getApplicationContext(), Help.class);
                        startActivity(help);
                    }
                }
            });
            ll_rate_review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_DashBoard.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent help = new Intent(getApplicationContext(), ProviderReview.class);
                        startActivity(help);
                    }
                }
            });


            lay_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_DashBoard.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } else {

                        //  ReferalcodeTask referalcodeTask = new ReferalcodeTask(SplashActivity.sharedPreferences.getString("UID", null), ctx);
                        // referalcodeTask.execute();

                        Intent settings = new Intent(getApplicationContext(), Share_Layout.class);
                        startActivity(settings);


                    }
                }
            });
            ll_logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_DashBoard.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Logout();
                    }
                }
            });


            btn_today.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_DashBoard.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } else {
                        btn_today.setBackgroundColor(getResources().getColor(R.color.red));
                        btn_today.setTextColor(getResources().getColor(R.color.white));

                        btn_tomo.setBackgroundColor(getResources().getColor(R.color.white));
                        btn_tomo.setTextColor(getResources().getColor(R.color.app_green));

                        btn_tismonth.setBackgroundColor(getResources().getColor(R.color.white));
                        btn_tismonth.setTextColor(getResources().getColor(R.color.app_green));

                        btn_all.setBackgroundColor(getResources().getColor(R.color.white));
                        btn_all.setTextColor(getResources().getColor(R.color.app_green));


                        showView("today");

                    }
                }
            });
            btn_tomo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_DashBoard.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } else {
                        btn_today.setBackgroundColor(getResources().getColor(R.color.white));
                        btn_today.setTextColor(getResources().getColor(R.color.app_green));

                        btn_tomo.setBackgroundColor(getResources().getColor(R.color.red));
                        btn_tomo.setTextColor(getResources().getColor(R.color.white));

                        btn_tismonth.setBackgroundColor(getResources().getColor(R.color.white));
                        btn_tismonth.setTextColor(getResources().getColor(R.color.app_green));

                        btn_all.setBackgroundColor(getResources().getColor(R.color.white));
                        btn_all.setTextColor(getResources().getColor(R.color.app_green));


                        showView("tomorrow");
                    }
                }
            });
            btn_tismonth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_DashBoard.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } else {
                        btn_today.setBackgroundColor(getResources().getColor(R.color.white));
                        btn_today.setTextColor(getResources().getColor(R.color.app_green));

                        btn_tomo.setBackgroundColor(getResources().getColor(R.color.white));
                        btn_tomo.setTextColor(getResources().getColor(R.color.app_green));

                        btn_tismonth.setBackgroundColor(getResources().getColor(R.color.red));
                        btn_tismonth.setTextColor(getResources().getColor(R.color.white));

                        btn_all.setBackgroundColor(getResources().getColor(R.color.white));
                        btn_all.setTextColor(getResources().getColor(R.color.app_green));


                        showView("month");
                    }
                }
            });
            btn_all.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_DashBoard.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } else {
                        btn_today.setBackgroundColor(getResources().getColor(R.color.white));
                        btn_today.setTextColor(getResources().getColor(R.color.app_green));

                        btn_tomo.setBackgroundColor(getResources().getColor(R.color.white));
                        btn_tomo.setTextColor(getResources().getColor(R.color.app_green));

                        btn_tismonth.setBackgroundColor(getResources().getColor(R.color.white));
                        btn_tismonth.setTextColor(getResources().getColor(R.color.app_green));

                        btn_all.setBackgroundColor(getResources().getColor(R.color.red));
                        btn_all.setTextColor(getResources().getColor(R.color.white));


                        showView("all");

                    }
                }
            });
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
                flowers.setStr_rate(providersServiceJSONobject.getString("starttime"));
                flowers.setStr_leave(providersServiceJSONobject.getString("leaveforapp"));
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
            Dash_AppointmentAdpater mAdapter = new Dash_AppointmentAdpater(beanArrayList, Provider_DashBoard.this, (GetBookingId) ctx);
            recyclerView.setAdapter(mAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

public void callpaid(final String service, final String imagepath, final String rate, final String strusername, final String row_id, final String status)


{
    try {
        Log.i("HH","image" +imagepath);
        Log.i("HH","status" +status);

        LayoutInflater inflater = LayoutInflater.from(ctx);
        View dialogLayout = inflater.inflate(R.layout.layout_confirmation, null);

        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(ctx);
        alertDialogBuilder.setView(dialogLayout);
        alertDialogBuilder.setCancelable(true);


        final android.app.AlertDialog alertDialoggs = alertDialogBuilder.create();
        //  alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialoggs.show();

        LinearLayout llayAlert = (LinearLayout) dialogLayout.findViewById(R.id.llayalertDialog);
        TextView tv_alert_Title = (TextView) dialogLayout.findViewById(R.id.add_msg);
        TextView tv_alert_Message = (TextView) dialogLayout.findViewById(R.id.message);

        tv_alert_Message.setText("Time to get paid");


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
                alertDialoggs.dismiss();


                GenerateInvoiceTask generateinvoice = new GenerateInvoiceTask(ctx,row_id, status);
                generateinvoice.execute();



            }
        });
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
    public void serviceendid(final String jsonvalues, String strTitle) {

        no_appointment.setVisibility(View.GONE);
        try {
            jsobj = new JSONObject(jsonvalues);

            str_appstatus = jsobj.getString("apptstatus");
            Log.i("YY","strapp" +str_appstatus);

            p_lat = jsobj.getString("providerlatitude");
            p_long = jsobj.getString("providerlogitude");
            row_id = jsobj.getString("id");

            userid = jsobj.getString("userid");
            strusername = jsobj.getString("username");

            service = jsobj.getString("service");

            rate = jsobj.getString("rate");
            imagepath = jsobj.getString("image_path");

            status = String.valueOf(1);

            if (str_appstatus.equalsIgnoreCase("appointment")) {

                next_appointmentLay.setVisibility(View.VISIBLE);
                stop_lay.setVisibility(View.GONE);
                start_lay.setVisibility(View.GONE);
                complete_lay.setVisibility(View.GONE);
                paid_lay.setVisibility(View.GONE);
                reach_lay.setVisibility(View.GONE);
                service_type.setText(jsobj.getString("service"));
                time.setText(jsobj.getString("starttime"));
                nextappoitn_time = jsobj.getString("starttime");



                ss = jsobj.toString();
                Log.i("LL", "str_appstatus" + str_appstatus);
                tv_lastApp_userName.setText(jsobj.getString("username"));
                imgLoader.DisplayImage(gD.common_baseurl+"upload/" + jsobj.getString("image_path"), userprofile);
                no_appointment.setVisibility(View.GONE);


                leaveforappoitment.setVisibility(View.VISIBLE);
                leaveforappoitment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!gD.isConnectingToInternet()) {
                            Toast.makeText(Provider_DashBoard.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                        } else {
                            GetCurrentLoctaion getcurrentloc = new GetCurrentLoctaion(p_lat, p_long, row_id);
                            getcurrentloc.execute();

                        }
                    }
                });


                reach_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // ll_leave_appointment_layout.setVisibility(View.INVISIBLE);
                        if (!gD.isConnectingToInternet()) {
                            Toast.makeText(Provider_DashBoard.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                        } else {
                            ReachedTask uploadTask = new ReachedTask(row_id);
                            uploadTask.execute();


                        }
                    }
                });


                start_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (!gD.isConnectingToInternet()) {
                            Toast.makeText(Provider_DashBoard.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                        } else {

                            // Toast.makeText(ProviderArrived.this, "Service started..", Toast.LENGTH_SHORT).show();


                            StartClickTask uploadTask = new StartClickTask(row_id);
                            uploadTask.execute();


                        }
                    }
                });


                stop_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!gD.isConnectingToInternet()) {
                            Toast.makeText(Provider_DashBoard.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                        } else {
                            //  Toast.makeText(ProviderArrived.this, "Service ended.", Toast.LENGTH_SHORT).show();


                            EndClickTask uploadTask = new EndClickTask(row_id);
                            uploadTask.execute();

                        }
                    }
                });





                complete_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CompleteTask uploadTask = new CompleteTask(row_id, userid, strusername, imagepath);
                        uploadTask.execute();
                    }
                });


                paid_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (str_appstatus.equalsIgnoreCase("providerended"))
                        {
                            RaiseInvoiceTask generateinvoice = new RaiseInvoiceTask(ctx, row_id,service,strusername,rate,imagepath,status);
                            generateinvoice.execute();
                        }
                        else
                        {
callpaid(service,imagepath,rate,strusername,row_id,status);

                        }
                    }
                });










            }





            if (str_appstatus.equalsIgnoreCase("leaveforappointment")) {
                next_appointmentLay.setVisibility(View.GONE);

                reach_lay.setVisibility(View.VISIBLE);
                stop_lay.setVisibility(View.GONE);
                start_lay.setVisibility(View.GONE);
                complete_lay.setVisibility(View.GONE);
                paid_lay.setVisibility(View.GONE);




                reach_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReachedTask uploadTask = new ReachedTask(row_id);
                        uploadTask.execute();

                    }
                });


                start_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        StartClickTask uploadTask = new StartClickTask(row_id);
                        uploadTask.execute();
                    }
                });


                stop_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        EndClickTask uploadTask = new EndClickTask(row_id);
                        uploadTask.execute();
                    }
                });


                complete_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CompleteTask uploadTask = new CompleteTask(row_id, userid, strusername, imagepath);
                        uploadTask.execute();
                    }
                });


                paid_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (str_appstatus.equalsIgnoreCase("providerended")) {
                            RaiseInvoiceTask generateinvoice = new RaiseInvoiceTask(ctx, row_id, service, strusername, rate, imagepath, status);
                            generateinvoice.execute();
                        } else {
                            callpaid(service,imagepath,rate,strusername,row_id,status);
                        }
                    }
                });





            }
            else if (str_appstatus.equalsIgnoreCase("reached")) {
                reach_lay.setVisibility(View.VISIBLE);
                next_appointmentLay.setVisibility(View.GONE);
                start_lay.setVisibility(View.GONE);
                stop_lay.setVisibility(View.GONE);
                paid_lay.setVisibility(View.GONE);
                complete_lay.setVisibility(View.GONE);
                reach_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ReachedTask uploadTask = new ReachedTask(row_id);
                        uploadTask.execute();

                    }
                });


                start_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        StartClickTask uploadTask = new StartClickTask(row_id);
                        uploadTask.execute();
                    }
                });


                stop_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        EndClickTask uploadTask = new EndClickTask(row_id);
                        uploadTask.execute();
                    }
                });


                complete_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CompleteTask uploadTask = new CompleteTask(row_id, userid, strusername, imagepath);
                        uploadTask.execute();
                    }
                });


                paid_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (str_appstatus.equalsIgnoreCase("providerended")) {
                            RaiseInvoiceTask generateinvoice = new RaiseInvoiceTask(ctx, row_id, service, strusername, rate, imagepath, status);
                            generateinvoice.execute();
                        } else {
                            callpaid(service,imagepath,rate,strusername,row_id,status);
                        }
                    }
                });




            }
            else if (str_appstatus.equalsIgnoreCase("useracceptreach")) {
                reach_lay.setVisibility(View.GONE);
                next_appointmentLay.setVisibility(View.GONE);
                start_lay.setVisibility(View.VISIBLE);
                paid_lay.setVisibility(View.GONE);
                complete_lay.setVisibility(View.GONE);
                next_appointmentLay.setVisibility(View.GONE);



                start_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        StartClickTask uploadTask = new StartClickTask(row_id);
                        uploadTask.execute();
                    }
                });


                stop_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        EndClickTask uploadTask = new EndClickTask(row_id);
                        uploadTask.execute();
                    }
                });


                complete_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CompleteTask uploadTask = new CompleteTask(row_id, userid, strusername, imagepath);
                        uploadTask.execute();
                    }
                });


                paid_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (str_appstatus.equalsIgnoreCase("providerended")) {
                            RaiseInvoiceTask generateinvoice = new RaiseInvoiceTask(ctx, row_id, service, strusername, rate, imagepath, status);
                            generateinvoice.execute();
                        } else {
                            callpaid(service,imagepath,rate,strusername,row_id,status);
                        }
                    }
                });




            }
            else if (str_appstatus.equalsIgnoreCase("providerstarted")) {
                next_appointmentLay.setVisibility(View.GONE);
                start_lay.setVisibility(View.GONE);
                stop_lay.setVisibility(View.VISIBLE);
                paid_lay.setVisibility(View.GONE);
                reach_lay.setVisibility(View.GONE);
                complete_lay.setVisibility(View.GONE);



                stop_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        EndClickTask uploadTask = new EndClickTask(row_id);
                        uploadTask.execute();
                    }
                });


                complete_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CompleteTask uploadTask = new CompleteTask(row_id, userid, strusername, imagepath);
                        uploadTask.execute();
                    }
                });


                paid_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (str_appstatus.equalsIgnoreCase("providerended")) {
                            RaiseInvoiceTask generateinvoice = new RaiseInvoiceTask(ctx, row_id, service, strusername, rate, imagepath, status);
                            generateinvoice.execute();
                        } else {
                            callpaid(service,imagepath,rate,strusername,row_id,status);
                        }
                    }
                });




            }
            else if (str_appstatus.equalsIgnoreCase("useracceptstarted")) {
                next_appointmentLay.setVisibility(View.GONE);
                start_lay.setVisibility(View.GONE);
                stop_lay.setVisibility(View.VISIBLE);
                paid_lay.setVisibility(View.GONE);
                reach_lay.setVisibility(View.GONE);
                complete_lay.setVisibility(View.GONE);




                stop_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        EndClickTask uploadTask = new EndClickTask(row_id);
                        uploadTask.execute();
                    }
                });


                complete_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CompleteTask uploadTask = new CompleteTask(row_id, userid, strusername, imagepath);
                        uploadTask.execute();
                    }
                });


                paid_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (str_appstatus.equalsIgnoreCase("providerended")) {
                            RaiseInvoiceTask generateinvoice = new RaiseInvoiceTask(ctx, row_id, service, strusername, rate, imagepath, status);
                            generateinvoice.execute();
                        } else {
                            callpaid(service,imagepath,rate,strusername,row_id,status);
                        }
                    }
                });




            }


            else if (str_appstatus.equalsIgnoreCase("providerended")) {
                next_appointmentLay.setVisibility(View.GONE);
                stop_lay.setVisibility(View.GONE);
                start_lay.setVisibility(View.GONE);
                paid_lay.setVisibility(View.VISIBLE);
                reach_lay.setVisibility(View.GONE);
                complete_lay.setVisibility(View.GONE);



                complete_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CompleteTask uploadTask = new CompleteTask(row_id, userid, strusername, imagepath);
                        uploadTask.execute();
                    }
                });


                paid_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (str_appstatus.equalsIgnoreCase("providerended"))
                        {
                            RaiseInvoiceTask generateinvoice = new RaiseInvoiceTask(ctx, row_id, service, strusername, rate, imagepath, status);
                            generateinvoice.execute();
                        }
                        else
                        {
                            callpaid(service,imagepath,rate,strusername,row_id,status);
                        }
                    }
                });









            } else if (str_appstatus.equalsIgnoreCase("useracceptended")) {
                next_appointmentLay.setVisibility(View.GONE);
                stop_lay.setVisibility(View.GONE);
                paid_lay.setVisibility(View.VISIBLE);
                start_lay.setVisibility(View.GONE);
                reach_lay.setVisibility(View.GONE);
                complete_lay.setVisibility(View.GONE);
                complete_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CompleteTask uploadTask = new CompleteTask(row_id, userid, strusername, imagepath);
                        uploadTask.execute();
                    }
                });


                paid_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (str_appstatus.equalsIgnoreCase("providerended"))
                        {
                            RaiseInvoiceTask generateinvoice = new RaiseInvoiceTask(ctx, row_id, service, strusername, rate, imagepath, status);
                            generateinvoice.execute();
                        }
                        else
                        {
                            callpaid(service,imagepath,rate,strusername,row_id,status);
                        }
                    }
                });




            } else if (str_appstatus.equalsIgnoreCase("invoicegenerated")) {
                next_appointmentLay.setVisibility(View.GONE);
                complete_lay.setVisibility(View.VISIBLE);
                stop_lay.setVisibility(View.GONE);
                paid_lay.setVisibility(View.GONE);
                start_lay.setVisibility(View.GONE);
                reach_lay.setVisibility(View.GONE);
                complete_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CompleteTask uploadTask = new CompleteTask(row_id, userid, strusername, imagepath);
                        uploadTask.execute();
                    }
                });







            } else if (str_appstatus.equalsIgnoreCase("userpaid")) {
                next_appointmentLay.setVisibility(View.GONE);
                complete_lay.setVisibility(View.VISIBLE);
                paid_lay.setVisibility(View.GONE);
                start_lay.setVisibility(View.GONE);
                reach_lay.setVisibility(View.GONE);
                stop_lay.setVisibility(View.GONE);

                complete_lay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        CompleteTask uploadTask = new CompleteTask(row_id, userid, strusername, imagepath);
                        uploadTask.execute();
                    }
                });






            }




        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void navigatepage(String iscomplete, String paidstatus, String generate, String reached, String pse, String review, String strtype, String strresponse, String leave, String responseval) {
    }


    @Override
    public void paidstatus(final String id, String servicename, final String time, String name, String rating, String userimage, String date) {

        try {
            LayoutInflater inflater = LayoutInflater.from(ctx);
            View dialogLayout = inflater.inflate(R.layout.booking_accdec_alert, null);

            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(ctx);
            alertDialogBuilder.setView(dialogLayout);
            alertDialogBuilder.setCancelable(true);


            final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            //  alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            alertDialog.show();

            LinearLayout llayAlert = (LinearLayout) dialogLayout.findViewById(R.id.llayalertDialog);
            TextView tv_alert_Title = (TextView) dialogLayout.findViewById(R.id.txt_title);

            TextView tv_Username = (TextView) dialogLayout.findViewById(R.id.txt_username);
            TextView txt_date = (TextView) dialogLayout.findViewById(R.id.txt_date);
            TextView txt_time = (TextView) dialogLayout.findViewById(R.id.txt_time);
            TextView txt_day = (TextView) dialogLayout.findViewById(R.id.txt_day);
            TextView tv_Message = (TextView) dialogLayout.findViewById(R.id.txt_Message);
            ImageView rndUserImage = (ImageView) dialogLayout.findViewById(R.id.user_image);
            RatingBar ratingBar = (RatingBar) dialogLayout.findViewById(R.id.txt_Rating);
            ratingBar.setActivated(false);
            Log.i("KK", "date***" + date);
            Log.i("KK", "time***" + time);
            tv_Username.setText(name);
            // tv_Rating.setText(rating + " Rating");
            ratingBar.setRating(Float.parseFloat(rating));
            txt_day.setText(date.split(" ")[0]);
            txt_date.setText(date.split(" ")[1]+ " " +date.split(" ")[2]);
            txt_time.setText(time);
            tv_Message.setText(servicename);


            ratingBar.setFocusable(false);

            imgLoader.DisplayImage(userimage, rndUserImage);
            LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
            stars.getDrawable(2).setColorFilter(Color.parseColor("#FE3232"), PorterDuff.Mode.SRC_ATOP);
            stars.getDrawable(0).setColorFilter(Color.parseColor("#c8c8c8"), PorterDuff.Mode.SRC_ATOP);

            stars.getDrawable(1).setColorFilter(Color.parseColor("#c8c8c8"), PorterDuff.Mode.SRC_ATOP);


            Button btn_acccept = (Button) dialogLayout.findViewById(R.id.btn_Accept);
            Button btn_decline = (Button) dialogLayout.findViewById(R.id.btn_Decline);


            FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, nScreenHeight);
            llayAlert.setLayoutParams(lparams);


            btn_acccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_DashBoard.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } else {
                        GetAcceptDeclineResponse GADR = new GetAcceptDeclineResponse(ctx, id, "1", alertDialog);
                        GADR.execute();
                    }
                }
            });

            btn_decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(Provider_DashBoard.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } else {
                        GetAcceptDeclineResponse GADR = new GetAcceptDeclineResponse(ctx, id, "2", alertDialog);
                        GADR.execute();
                    }
                }
            });
        } catch (NumberFormatException e) {
            e.printStackTrace();
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

    private void Logout() {

        try {
            if (!gD.isConnectingToInternet()) {
                Toast.makeText(Provider_DashBoard.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
            } else {
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


                FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ScreenHeight);
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

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        gD.screenback = 0;
        try {

            TimeZone tz = TimeZone.getDefault();
            System.out.println("TimeZone " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

            timezonevalue = tz.getID();
            super.onResume();

            GetProviderReviews getProviderReviews = new GetProviderReviews(SplashActivity.sharedPreferences.getString("UID", null), ctx);
            getProviderReviews.execute();

            NotificationCount notificationCount = new NotificationCount(SplashActivity.sharedPreferences.getString("UID", null), ctx);
            notificationCount.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    @Override
    public void onBackPressed() {
        if (gD.screenback == 0) {

          //  Intent voicpage = new Intent(Provider_DashBoard.this, Provider_DashBoard.class);




          //  startActivity(voicpage);

            Log.i("HH","hello 8**" +gD.screenback);
            Toast.makeText(Provider_DashBoard.this, "Please press again to exit", Toast.LENGTH_SHORT).show();

            gD.screenback = 1;
        }
        else /*if(gD.screenback == 1)*/
        {

            Log.i("HH","hello 8 **" +gD.screenback);
            finish();
            finishAffinity();
        }

    }



    public void custompopup() {


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

            tv_alert_Message.setText("User still has not completed the payment .Do you Wish to proceed further?");

            Button btn_submit = (Button) dialogLayout.findViewById(R.id.ok);
            Button btn_cancel = (Button) dialogLayout.findViewById(R.id.cancel);


            FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ScreenHeight);
            llayAlert.setLayoutParams(lparams);


            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    complete_lay.setVisibility(View.VISIBLE);
                    paid_lay.setVisibility(View.GONE);
                    Intent startAct = new Intent(ctx, Provider_DashBoard.class);
                    startActivity(startAct);
                    finish();
                    finishAffinity();

                    alertDialog.dismiss();


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

    public void reviewpop(String str_userid, String str_username, String str_imagepath) {


        try {
            LayoutInflater inflater = LayoutInflater.from(ctx);
            View dialogLayout = inflater.inflate(R.layout.provider_review_popup, null);

            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(ctx);
            alertDialogBuilder.setView(dialogLayout);
            alertDialogBuilder.setCancelable(false);


            final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
            //  alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            alertDialog.show();

            LinearLayout llayAlert = (LinearLayout) dialogLayout.findViewById(R.id.llayalertDialog);


            FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, nnScreenHeight);
            llayAlert.setLayoutParams(lparams);
            ratingBar = (RatingBar) dialogLayout.findViewById(R.id.review_rating);
            btn_submit_review = (Button) dialogLayout.findViewById(R.id.but_submit);
            user_image = (ImageView) dialogLayout.findViewById(R.id.user_image);

            txt_username = (TextView) dialogLayout.findViewById(R.id.txt_username);

            edit_review = (EditText) dialogLayout.findViewById(R.id.edit_review);

            Button btn_cancel = (Button) dialogLayout.findViewById(R.id.butcancel);
            close = (ImageView) dialogLayout.findViewById(R.id.close);

            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
            //  str_provider_name=txt_provider_name.getText().toString();


            txt_username.setText(str_username);
            str_id = str_userid;


            String strPath = str_imagepath;

            Log.i("FF", "strimage" + strPath.length());

            if (strPath.length() == 0) {
                user_image.setBackgroundResource(R.drawable.default_user_icon);
            } else {

                String strimage = gD.common_baseurl+"upload/" + strPath;

                imgLoader.DisplayImage(strimage, user_image);
                //user_image.setImageBitmap(imgLoader.getBitmap(strimage));

            }


            btn_submit_review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    str_rating = String.valueOf(ratingBar.getRating());
                    str_des = edit_review.getText().toString();


                    Log.i("HHB", "Rating : " + ratingBar.getRating());
                    if (!str_rating.equalsIgnoreCase("0.0") && str_des.length() > 0) {
                        ReviewUploadTask uploadTask = new ReviewUploadTask(row_id);
                        uploadTask.execute();

                    } else {
                        if (str_rating.equals("0.0")) {
                            Toast.makeText(Provider_DashBoard.this, "Rating should not be zero !", Toast.LENGTH_SHORT).show();
                        } else if (str_des.length() == 0) {
                            Toast.makeText(Provider_DashBoard.this, "Comment should not be empty !", Toast.LENGTH_SHORT).show();
                        }
                    }


                    // Toast.makeText(Provider_Pay.this, " "+str_rating+" "+str_id+"- "+str_des, Toast.LENGTH_SHORT).show();


                }
            });


            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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

                    tv_alert_Message.setText("Do u proceed further without giving review .Do you Wish to proceed further?");

                    Button btn_submit = (Button) dialogLayout.findViewById(R.id.ok);
                    Button btn_cancel = (Button) dialogLayout.findViewById(R.id.cancel);


                    FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ScreenHeight);
                    llayAlert.setLayoutParams(lparams);


                    btn_submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i = new Intent(Provider_DashBoard.this, ProviderHistoryPage.class);
                            startActivity(i);
                            finish();
                            finishAffinity();


                            alertDialog.dismiss();


                        }
                    });


                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            alertDialog.dismiss();


                        }
                    });

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

        public GetProviderDetails(String userid, Context context, RecyclerView recyclerView) {
            this.user_id = userid;
            this.cont = context;
            this.recycler = recyclerView;

        }

        @Override
        protected void onPreExecute() {
            Provider_DashBoard.this.runOnUiThread(new Runnable() {
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
                String requestURL = gD.common_baseurl+"dashboard.php";
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
                ArrayList beanlist = new ArrayList<ProviderBean>();
                JSONObject jsobj = new JSONObject(sResponse);
                JSONObject providersServiceJSONobject = null;
                Log.i("HH", "strResp : " + sResponse);
                balance.setText("$" + jsobj.getString("providertotal"));
                if (jsobj.getString("status").equalsIgnoreCase("success")) {
                    JSONObject jsobj_appointments = jsobj.getJSONObject("appointment");


                    JSONObject jsobj_notifications = jsobj.getJSONObject("notification");


                    JSONArray providernotificationdetails = jsobj_notifications.getJSONArray("notification_details");
                    if (providernotificationdetails.length() > 0) {

                        listempty.setVisibility(View.GONE);

                        for (int i = 0; i < providernotificationdetails.length(); i++) {
                            ProviderBean PB = new ProviderBean();
                            PB.set_serviceName(providernotificationdetails.getJSONObject(i).getString("service"));
                            PB.setStr_name(providernotificationdetails.getJSONObject(i).getString("username"));

                            PB.setStr_rating(providernotificationdetails.getJSONObject(i).getString("useravgrating"));

                            PB.setStr_Date(providernotificationdetails.getJSONObject(i).getString("nstarttime"));
                            PB.setStr_miles(providernotificationdetails.getJSONObject(i).getString("ndate"));

                            PB.setStr_rating(providernotificationdetails.getJSONObject(i).getString("useravgrating"));

                            PB.setStr_providerid(providernotificationdetails.getJSONObject(i).getString("id"));

                            PB.setN_user_image(gD.common_baseurl + "upload/" + providernotificationdetails.getJSONObject(i).getString("image"));

                            beanlist.add(PB);
                        }


                    } else {
                        listempty.setVisibility(View.VISIBLE);
                    }

                    Notification_Request_Adapter pServiceadapter = new Notification_Request_Adapter(Provider_DashBoard.this, beanlist, (GetBookingId) ctx);
                    listServices.setAdapter(pServiceadapter);
                    pServiceadapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                    JSONArray providerServicesJSONArray = jsobj_appointments.getJSONArray("today");
                    providerServicesToday = jsobj_appointments.getJSONArray("today");
                    providerServicesTom = jsobj_appointments.getJSONArray("tomorrow");
                    providerServicesMonth = jsobj_appointments.getJSONArray("week");
                    providerServicesAll = jsobj_appointments.getJSONArray("month");
                    Log.i("GG", "providerServicesToday : " + providerServicesToday);

                    if (providerServicesToday.length() > 0) {
                        for (int i = 0; i < providerServicesJSONArray.length(); i++) {

                            AppointmentBean flowers = new AppointmentBean();
                            providersServiceJSONobject = providerServicesToday.getJSONObject(i);
                            Log.i("GG", "providersServiceJSONobject : " + providersServiceJSONobject);
                            flowers.setStr_name(providersServiceJSONobject.getString("username"));
                            flowers.setRow_id(providersServiceJSONobject.getString("id"));

                            flowers.setStr_leave(providersServiceJSONobject.getString("leaveforapp"));
                            flowers.setStr_Service(providersServiceJSONobject.getString("service"));
                            flowers.setStr_rate(providersServiceJSONobject.getString("starttime"));
                            flowers.setN_user_image(gD.common_baseurl+"upload/" + providersServiceJSONobject.getString("image_path"));
                            flowers.setStr_jsongvalues(providersServiceJSONobject.toString());

                            beanArrayList.add(flowers);







                        }
                    } else {
                        emptyText.setVisibility(View.VISIBLE);
                    }


                }
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recycler.setLayoutManager(mLayoutManager);
                recycler.setItemAnimator(new DefaultItemAnimator());

                Dash_AppointmentAdpater mAdapter = new Dash_AppointmentAdpater(beanArrayList, Provider_DashBoard.this, (GetBookingId) ctx);
                recycler.setAdapter(mAdapter);



            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    class GetAcceptDeclineResponse extends AsyncTask {
        String booking_Id = null;
        String sResponse = null;
        Context cont;
        String status;
        android.app.AlertDialog aD;

        public GetAcceptDeclineResponse(Context context, String userid, String status, android.app.AlertDialog alertDialog) {
            this.booking_Id = userid;
            this.cont = context;
            this.status = status;
            this.aD = alertDialog;
        }

        @Override
        protected void onPreExecute() {
            Provider_DashBoard.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gD.callload(ctx, nScreenHeight);
                }
            });
// TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] param) {
            try {

                String requestURL = gD.common_baseurl+"acceptappointment.php?id=" + booking_Id + "&status=" + status;

                //   APIcalls AceptApi = new APIcalls(requestURL);
                // AceptApi.Process();


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
        protected void onPostExecute(Object response) {
            if(gD.alertDialog!=null) {
                gD.alertDialog.dismiss();
            }
            try {
                ArrayList beanArrayList = new ArrayList<UserHistoryBean>();

                JSONObject jsobj = new JSONObject(sResponse);

                Log.i("HH", "strResp : " + sResponse);

                if (jsobj.getInt("code") == 2 || jsobj.getInt("code") == 0) {
                    aD.dismiss();


                    Intent i = new Intent(Provider_DashBoard.this, Provider_DashBoard.class);
                    startActivity(i);
                    finish();


                } else {
                    ((Activity) cont).finish();
                }

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
            Provider_DashBoard.this.runOnUiThread(new Runnable() {
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
                        /*Toast.makeText(ProviderCategory.this, " logged out successfuly !", Toast.LENGTH_SHORT).show();*/

                        SharedPreferences.Editor editor = SplashActivity.autoLoginPreference.edit();
                        editor.putString("uname", null);
                        editor.putString("pwd", null);
                        editor.commit();

                        Intent nextScreenIntent = new Intent(Provider_DashBoard.this, SignInActivity.class);
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

    //referal code system

    class GetProviderReviews extends AsyncTask {
        String provider_id = null;
        String sResponse = null;
        Context cont;
        RecyclerView recycler;

        public GetProviderReviews(String userid, Context context) {
            this.provider_id = userid;
            this.cont = context;

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Object doInBackground(Object[] param) {
            try {
                String charset = "UTF-8";
                String requestURL = gD.common_baseurl+"ratingcount.php";
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                multipart.addHeaderField("Content-Encoding", "gzip");
                multipart.addFormField("providerid", SplashActivity.sharedPreferences.getString("UID", null));
                List<String> response = multipart.finish();

                StringBuilder sb = new StringBuilder();
                for (String line : response) {
                    System.out.println(line);
                    sb.append(line);
                }
                try {
                    JSONObject jsonObj = new JSONObject(sb.toString());
                    Log.i("NNN", "StrResp : " + jsonObj.toString());
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

          //  pD.dismiss();

            try {

                String name = null, image = null, time = null, date = null;
                ArrayList beanArrayList = new ArrayList<commonBeanSupport>();

                JSONObject jsobj = new JSONObject(sResponse);

                Log.i("HH", "strResp : " + sResponse);

                if (jsobj.getString("status").equalsIgnoreCase("success")) {

                    str_rate_count = jsobj.getJSONObject("review_count").getString("reviewcount");
                    Log.i("str_rate_count", str_rate_count);

                    if (str_rate_count.equals("0")) {
                        txt_rate_count.setVisibility(View.GONE);

                    } else {

                        txt_rate_count.setText(str_rate_count);
                    }


                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

//click for leave for appoitnemnt

    class NotificationCount extends AsyncTask {
        String providerid = null;
        String sResponse = null;
        Context cont;
        RecyclerView recycler;

        public NotificationCount(String providerid, Context context) {
            this.providerid = providerid;
            this.cont = context;

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Object doInBackground(Object[] param) {
            try {
                String charset = "UTF-8";
                String requestURL = gD.common_baseurl+"notificationcount.php";
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                multipart.addHeaderField("Content-Encoding", "gzip");
                multipart.addFormField("providerid", SplashActivity.sharedPreferences.getString("UID", null));
                List<String> response = multipart.finish();

                StringBuilder sb = new StringBuilder();
                for (String line : response) {
                    System.out.println(line);
                    sb.append(line);
                }
                try {
                    JSONObject jsonObj = new JSONObject(sb.toString());
                    Log.i("NNN", "StrResp : " + jsonObj.toString());
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
                ArrayList beanArrayList = new ArrayList<commonBeanSupport>();
                JSONObject jsobj = new JSONObject(sResponse);

                Log.i("HH", "strResp : " + sResponse);

                if (jsobj.getString("status").equalsIgnoreCase("success")) {

                    str_notify_count = jsobj.getJSONObject("notificationcount").getString("notificationcount");
                    Log.i("str_rate_count", str_notify_count);

                    if (str_notify_count.equals("0")) {
                        txt_notify_count.setVisibility(View.GONE);

                    } else {
                        txt_notify_count.setText(str_notify_count);
                    }

                }
                // pD.dismiss();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }



    class GetCurrentLoctaion extends AsyncTask {

        String sResponse = null;
        String providerlatitude;
        String providerlongitude;
        String user_rowid;

        public GetCurrentLoctaion(String lat, String aLong, String row_id) {
            this.providerlatitude = lat;
            this.providerlongitude = aLong;
            user_rowid = row_id;
        }


        @Override
        protected Object doInBackground(Object[] params) {
            try {

                String charset = "UTF-8";

                String requestURL = gD.common_baseurl+"leaveforappoint.php";


                // 4. separate class for multipart content image uploaded task----------- vinoth
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                multipart.addHeaderField("Content-Encoding", "gzip");
                multipart.addHeaderField("User-Agent", "rajaji");
                multipart.addHeaderField("Test-Header", "Header-Value");


                multipart.addFormField("keywords", "Java,upload,Spring");

                // 5. set the user_image key word and multipart image file value ----------- vinoth


                multipart.addFormField("providerlongitude", providerlongitude);

                multipart.addFormField("id", user_rowid);


                multipart.addFormField("providerlatitude", providerlatitude);

                multipart.addFormField("providerid", SplashActivity.sharedPreferences.getString("UID", null));

                Log.e("providerlongitude", providerlongitude);
                Log.e("providerlatitude", providerlatitude);

                Log.e("providerid", SplashActivity.sharedPreferences.getString("UID", null));


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

                    Log.i("LLL", "StrResp : " + jsonObj.toString());

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
            Provider_DashBoard.this.runOnUiThread(new Runnable() {
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

                        //    gD.showAlertDialog(context, "", "You have left for appointment", nScreenHeight, 1);

                        LayoutInflater inflater = LayoutInflater.from(ctx);
                        View dialogLayout = inflater.inflate(R.layout.leave_fro_appointment, null);

                        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(ctx);
                        alertDialogBuilder.setView(dialogLayout);
                        alertDialogBuilder.setCancelable(false);


                        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                        //  alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        alertDialog.show();

                        LinearLayout llayAlert = (LinearLayout) dialogLayout.findViewById(R.id.llayalertDialog);
                        // TextView tv_alert_Title = (TextView) dialogLayout.findViewById(R.id.tv_alert_Title);
                        TextView tv_alert_Message = (TextView) dialogLayout.findViewById(R.id.message);
                        TextView text = (TextView) dialogLayout.findViewById(R.id.text);


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

                                alertDialog.dismiss();
                                Intent i = new Intent(Provider_DashBoard.this, Provider_DashBoard.class);
                                startActivity(i);

                            }
                        });


                        next_appointmentLay.setVisibility(View.GONE);
                        reach_lay.setVisibility(View.VISIBLE);



                    } else if (jsobj.getInt("code") == 7) {

                        // gD.showAlertDialog(context, "", jsobj.getString("status"), nScreenHeight, 1);
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

                        tv_alert_Message.setText(jsobj.getString("status"));

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
                                Intent i = new Intent(Provider_DashBoard.this, Provider_DashBoard.class);
                                startActivity(i);
                                finish();
                                alertDialog.dismiss();
                            }
                        });

                    } else if (jsobj.getInt("code") == 9) {

                        // gD.showAlertDialog(context, "", jsobj.getString("status"), nScreenHeight, 1);
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

                        tv_alert_Message.setText(jsobj.getString("status"));

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

                                Intent i = new Intent(Provider_DashBoard.this, Provider_DashBoard.class);
                                startActivity(i);
                                finish();

                                alertDialog.dismiss();
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

    //to reach msg send
    class ReachedTask extends AsyncTask {

        String sResponse = null;
        String row_id;

        public ReachedTask(String row_id) {

            this.row_id = row_id;
        }


        @Override
        protected Object doInBackground(Object[] params) {
            try {


                String charset = "UTF-8";

                String requestURL = gD.common_baseurl+"providerreach.php";


                // 4. separate class for multipart content image uploaded task----------- vinoth
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                multipart.addHeaderField("Content-Encoding", "gzip");
                multipart.addHeaderField("User-Agent", "rajaji");
                multipart.addHeaderField("Test-Header", "Header-Value");


                multipart.addFormField("keywords", "Java,upload,Spring");

                // 5. set the user_image key word and multipart image file value ----------- vinoth


                multipart.addFormField("id", row_id);


                Log.i("HH", "id1." + row_id);

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

                    Log.i("CCC", "StrResp : " + jsonObj.toString());

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
            Provider_DashBoard.this.runOnUiThread(new Runnable() {
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

                    if (jsobj.getInt("code") == 1) {
                        reach_lay.setVisibility(View.VISIBLE);

                        next_appointmentLay.setVisibility(View.GONE);


                        paid_lay.setVisibility(View.GONE);
                        complete_lay.setVisibility(View.GONE);
                        start_lay.setVisibility(View.GONE);
                        stop_lay.setVisibility(View.GONE);


                        LayoutInflater inflater = LayoutInflater.from(ctx);
                        View dialogLayout = inflater.inflate(R.layout.layout_confirmation, null);

                        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(ctx);
                        alertDialogBuilder.setView(dialogLayout);
                        alertDialogBuilder.setCancelable(false);


                        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                        //  alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        alertDialog.show();

                        LinearLayout llayAlert = (LinearLayout) dialogLayout.findViewById(R.id.llayalertDialog);
                         TextView tv_alert_Title = (TextView) dialogLayout.findViewById(R.id.add_msg);
                        TextView tv_alert_Message = (TextView) dialogLayout.findViewById(R.id.message);
                        tv_alert_Title.setVisibility(View.VISIBLE);
                        tv_alert_Message.setText("Waiting for client to confirm your arrival!");
                        tv_alert_Title.setText("(You'll be able to begin working soon)");

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


                                Intent i = new Intent(Provider_DashBoard.this, Provider_DashBoard.class);
                                startActivity(i);

                                alertDialog.dismiss();


                            }
                        });


                        //reachpop();

                    } else if (jsobj.getInt("code") == 2) {
                        reach_lay.setVisibility(View.GONE);
                        start_lay.setVisibility(View.VISIBLE);

                        next_appointmentLay.setVisibility(View.GONE);

                        Intent i = new Intent(Provider_DashBoard.this, Provider_DashBoard.class);
                        startActivity(i);

                    } else if (jsobj.getInt("code") == 3) {
                        gD.showAlertDialog(ctx, "", "You have reached your client", ScreenHeight, 1);
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

    class StartClickTask extends AsyncTask {

        String sResponse = null;
        String row_id;

        public StartClickTask(String row_id) {

            this.row_id = row_id;
        }


        @Override
        protected Object doInBackground(Object[] params) {
            try {


                String charset = "UTF-8";

                String requestURL = gD.common_baseurl+"startservice.php";


                // 4. separate class for multipart content image uploaded task----------- vinoth
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);

                multipart.addHeaderField("User-Agent", "rajaji");
                multipart.addHeaderField("Test-Header", "Header-Value");

                multipart.addHeaderField("Content-Encoding", "gzip");
                multipart.addFormField("keywords", "Java,upload,Spring");

                // 5. set the user_image key word and multipart image file value ----------- vinoth


                multipart.addFormField("id", row_id);
                multipart.addFormField("start", "1");
                multipart.addFormField("providerid", SplashActivity.sharedPreferences.getString("UID", null));


                Log.i("HH", "id1." + row_id);

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

                    Log.i("DDD", "StrResp : " + jsonObj.toString());

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
            Provider_DashBoard.this.runOnUiThread(new Runnable() {
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
                        TextView tv_alert_Title = (TextView) dialogLayout.findViewById(R.id.add_msg);
                        TextView tv_alert_Message = (TextView) dialogLayout.findViewById(R.id.message);

                        tv_alert_Message.setText("You have started the service!");


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

                                Intent i = new Intent(Provider_DashBoard.this, Provider_DashBoard.class);
                                startActivity(i);
                                alertDialog.dismiss();


                            }
                        });


                        start_lay.setVisibility(View.GONE);
                        stop_lay.setVisibility(View.VISIBLE);


                        next_appointmentLay.setVisibility(View.GONE);




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

    class EndClickTask extends AsyncTask {

        String sResponse = null;
        String row_id;

        public EndClickTask(String row_id) {
            this.row_id = row_id;
        }


        @Override
        protected Object doInBackground(Object[] params) {
            try {


                String charset = "UTF-8";

                String requestURL = gD.common_baseurl+"startservice.php";


                // 4. separate class for multipart content image uploaded task----------- vinoth
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                multipart.addHeaderField("Content-Encoding", "gzip");
                multipart.addHeaderField("User-Agent", "rajaji");
                multipart.addHeaderField("Test-Header", "Header-Value");


                multipart.addFormField("keywords", "Java,upload,Spring");

                // 5. set the user_image key word and multipart image file value ----------- vinoth


                multipart.addFormField("id", row_id);
                multipart.addFormField("end", "1");
                multipart.addFormField("providerid", SplashActivity.sharedPreferences.getString("UID", null));


                Log.i("HH", "id1." + row_id);

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

                    Log.i("EEE", "StrResp : " + jsonObj.toString());

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
            Provider_DashBoard.this.runOnUiThread(new Runnable() {
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


                    if (jsobj.getInt("code") == 3) {


                       // gD.showAlertDialog(ctx, "", "You have stopped the service!", ScreenHeight, 1);
                        stop_lay.setVisibility(View.GONE);
                        paid_lay.setVisibility(View.VISIBLE);

                        reach_lay.setVisibility(View.GONE);

                        next_appointmentLay.setVisibility(View.GONE);



                        complete_lay.setVisibility(View.GONE);
                        start_lay.setVisibility(View.GONE);


                        Intent nextScreenIntent = new Intent(Provider_DashBoard.this, Provider_DashBoard.class);
                        startActivity(nextScreenIntent);


                    }
                    else if (jsobj.getInt("code") == 0) {
                        stop_lay.setVisibility(View.VISIBLE);
                        gD.showAlertDialog(ctx, "", "Waiting on clients confirmation", ScreenHeight, 1);

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

    class RaiseInvoiceTask extends AsyncTask {

        String user_id = null;
        String sResponse = null;
        Context cont;
        String status;
        String strservice,strrate,strname,strimage;

        public RaiseInvoiceTask(Context context, String userid, String service, String strusername, String rate, String imagepath, String status) {
            this.user_id = userid;
            this.cont = context;
            this.status = status;
            strname=strusername;
            strservice=service;
            strimage=imagepath;
            strrate=rate;

        }

        @Override
        protected void onPreExecute() {
            Provider_DashBoard.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gD.callload(ctx, nScreenHeight);

                }
            });
// TODO Auto-generated method stub
            super.onPreExecute();
        }


        @Override
        protected Object doInBackground(Object[] params) {
            try {


                String requestURL = gD.common_baseurl+"generateinvoice.php?id=" + user_id;




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
                        alertDialogBuilder.setCancelable(true);


                        final android.app.AlertDialog alertDialoggs = alertDialogBuilder.create();
                        //  alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        alertDialoggs.show();

                        LinearLayout llayAlert = (LinearLayout) dialogLayout.findViewById(R.id.llayalertDialog);
                        TextView tv_alert_Title = (TextView) dialogLayout.findViewById(R.id.add_msg);
                        TextView tv_alert_Message = (TextView) dialogLayout.findViewById(R.id.message);

                        tv_alert_Message.setText("Time to get paid");


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

                                alertDialoggs.dismiss();


                                GenerateInvoiceTask generateinvoice = new GenerateInvoiceTask(ctx, row_id, status);
                                generateinvoice.execute();


                            }
                        });










                        complete_lay.setVisibility(View.GONE);
                        paid_lay.setVisibility(View.VISIBLE);


                        next_appointmentLay.setVisibility(View.GONE);




                    } else if (jsobj.getInt("code") == 4) {

                        gD.showAlertDialog(ctx, "", "Client did not confirm completion", ScreenHeight, 1);

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

    private class CompleteTask extends AsyncTask {
        String sResponse = null;
        String row_id, str_userid, str_username, str_imagepath;

        public CompleteTask(String rowId, String userid, String username, String imagepath) {
            row_id = rowId;
            str_userid = userid;
            str_username = username;
            str_imagepath = imagepath;
        }


        @Override
        protected Object doInBackground(Object[] objects) {
            Log.i("HH", "Else.....");
            String charset = "UTF-8";

            try {

                String requestURL = gD.common_baseurl+"acceptappointment.php?id=" + URLEncoder.encode(row_id, "utf-8") + "&complete=" + URLEncoder.encode("1", "utf-8");

                //   APIcalls AceptApi = new APIcalls(requestURL);
                // AceptApi.Process();


                try {

                    URL url;
                    HttpURLConnection urlConnection = null;
                    try {
                        Log.i("SR", "Login URL : " + requestURL.trim());
                        System.out.print("strHTTP_LOGIN_URL");
                        url = new URL(requestURL.trim());
//                        urlConnection.setRequestProperty("Accept-Encoding", "gzip");

                        urlConnection = (HttpURLConnection) url.openConnection();
                        int responseCode = urlConnection.getResponseCode();

                        sResponse = readStream(urlConnection.getInputStream());

                        Log.i("SR-del", "APIcalls_Process : " + sResponse);


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
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            gD.callload(ctx, nScreenHeight);

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

                        reviewpop(str_userid, str_username, str_imagepath);

                        /*Toast.makeText(getApplicationContext(), "deleted....", Toast.LENGTH_SHORT).show();*/
                    } else if (jsobj.getInt("code") == 3) {


                        custompopup();
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
            Provider_DashBoard.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    gD.callload(ctx, nScreenHeight);
                }
            });
// TODO Auto-generated method stub
            super.onPreExecute();
        }


        @Override
        protected Object doInBackground(Object[] params) {
            try {


                String requestURL = gD.common_baseurl+"generateinvoice.php?id=" + user_id + "&generate=" + status;

                //   APIcalls AceptApi = new APIcalls(requestURL);
                // AceptApi.Process();


                try {

                    URL url;
                    HttpURLConnection urlConnection = null;
                    try {
                        Log.i("SR", "Login URL : " + requestURL.trim());
                        System.out.print("strHTTP_LOGIN_URL");
                        url = new URL(requestURL.trim());

//                        urlConnection.setRequestProperty("Accept-Encoding", "gzip");
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

                    //    gD.showAlertDialog(ctx, "", "Your service is not ended by user", ScreenHeight, 1);
                        LayoutInflater inflater = LayoutInflater.from(ctx);
                        View dialogLayout = inflater.inflate(R.layout.layout_confirmation, null);

                        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(ctx);
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


                        FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ScreenHeight);
                        llayAlert.setLayoutParams(lparams);
                        int count = 1;
                        if (count == 1) {
                            btn_cancel.setVisibility(View.GONE);
                        }


                        btn_submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent i = new Intent(Provider_DashBoard.this, Provider_DashBoard.class);
                                startActivity(i);
                                alertDialog.dismiss();


                            }
                        });


                        complete_lay.setVisibility(View.VISIBLE);
                        paid_lay.setVisibility(View.GONE);

                        reach_lay.setVisibility(View.GONE);

                        next_appointmentLay.setVisibility(View.GONE);


                        start_lay.setVisibility(View.GONE);
                        stop_lay.setVisibility(View.GONE);

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


//checking generate page

    class ReviewUploadTask extends AsyncTask {

        String sResponse = null;
        String row_id;

        public ReviewUploadTask(String row_id) {

            this.row_id = row_id;
        }


        @Override
        protected Object doInBackground(Object[] params) {
            try {


                String charset = "UTF-8";

                String requestURL = gD.common_baseurl+"writereviewpro.php";


                // 4. separate class for multipart content image uploaded task----------- vinoth
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                multipart.addHeaderField("Content-Encoding", "gzip");
                multipart.addHeaderField("User-Agent", "rajaji");
                multipart.addHeaderField("Test-Header", "Header-Value");


                multipart.addFormField("keywords", "Java,upload,Spring");

                // 5. set the user_image key word and multipart image file value ----------- vinoth

                multipart.addFormField("description", str_des);
                multipart.addFormField("rating", str_rating);

                multipart.addFormField("userid", str_id);
                multipart.addFormField("id", row_id);
                multipart.addFormField("providerid", SplashActivity.sharedPreferences.getString("UID", null));

                Log.e("description", str_des);
                Log.e("rating", str_rating);
                Log.e("userid", str_id);
                Log.e("providerid", SplashActivity.sharedPreferences.getString("UID", null));

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
            Provider_DashBoard.this.runOnUiThread(new Runnable() {
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

                        tv_alert_Message.setText("Review posted successfully");

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
                                Intent nextScreenIntent = new Intent(Provider_DashBoard.this, Provider_DashBoard.class);
                                startActivity(nextScreenIntent);


                                finish();
                                alertDialog.dismiss();


                            }
                        });



                      /*  Intent nextScreenIntent = new Intent(Provider_CompleteRaisePage.this, ProviderHistoryPage.class);
                        startActivity(nextScreenIntent);
                        finish();*/

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
            Provider_DashBoard.this.runOnUiThread(new Runnable() {
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

                    tv_alert_Message.setText("Your booking has been cancelled");

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


                            Intent i=new Intent(Provider_DashBoard.this,Provider_DashBoard.class);
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


}

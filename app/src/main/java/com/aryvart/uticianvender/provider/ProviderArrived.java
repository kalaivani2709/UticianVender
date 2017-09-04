package com.aryvart.uticianvender.provider;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
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

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.genericclasses.AlertDialogManager;
import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.aryvart.uticianvender.genericclasses.MultipartUtility;

import com.aryvart.uticianvender.imageCache.ImageLoader;
import com.aryvart.uticianvender.user.DirectionsJSONParser;
import com.aryvart.uticianvender.user.GPSTracker;
import com.aryvart.uticianvender.utician.SplashActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;

/**
 * Created by Android2 on 7/18/2016.
 */
public class ProviderArrived extends Activity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)
            .setFastestInterval(16)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    public static boolean isGPSEnabled = false;
    public static boolean isNetworkEnabled = false;
    static SharedPreferences sharedpreferences;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE,
            Manifest.permission.CAMERA, Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_WIFI_STATE
    };

    public GoogleMap googleMap;
    Double lat = 0.0, longi = 0.0;

    ProgressDialog dialog;

    LocationManager locationManager;
    LatLng TutorialsPoint = new LatLng(lat, longi);
    LatLng TutorialsPoints = new LatLng(lat, longi);
    String strUserName;
    Marker marker;

    GeneralData gD;
    Context context;
    TextView username, rating, txt_time, txt_Address;
    ImageLoader imgLoader;
    ImageView userimage;

    String str_messagecontent;

    String  id;
    AlertDialogManager alert = new AlertDialogManager();
    String strjsondatas, phonenumber, strtype;
    ArrayList<LatLng> markerPoints;
    String name, time, latt, lng, userlatt, userlong,platt,plong;
    LinearLayout leaveforanappointment, call, msg, startend, ll_reached;
    TextView startlay, endlay;
    ImageView img_back;
    JSONObject jsobj = null;
    int nScreenHeight, lScreenHeight, ScreenHeight;
    RelativeLayout leaveAppontlayout, ll_leave_appointment_layout;

    MapFragment mapFragment;



    Marker mark, Destmark;
    Date date1, date2;

    Thread t;
    Calendar calander;
    SimpleDateFormat simpleDateFormat;
    String nextappointment_time, nextappointment_date, next_dayandtime;
    android.app.AlertDialog alertDialog;
    //    new Changes
    private GoogleApiClient mGoogleApiClient = null;

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
String timezonevalue;
    LinearLayout lay_clickaddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.provider_arrived);
            lay_clickaddress=(LinearLayout)findViewById(R.id.lay_clickaddress);
            dialog = new ProgressDialog(ProviderArrived.this);
            img_back = (ImageView) findViewById(R.id.img_back);
            img_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(ProviderArrived.this, ProviderAppointment.class);
                    startActivity(i);
                    finish();
                }
            });
            context = this;
            gD = new GeneralData(context);
            imgLoader = new ImageLoader(context);
            strjsondatas = getIntent().getStringExtra("msg");
            strtype = getIntent().getStringExtra("type");
            Log.i("JJ", "strjsondatas.." + strjsondatas);
            Log.i("JJ", "strtype.." + strtype);
            SplashActivity.sharedPreferences = getSharedPreferences("regid", Context.MODE_PRIVATE);
            verifyStoragePermissions(this);

            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(context, "Unable to connect with Internet. Please check your internet connection and try again");
            }

            try {
                final Timer timer = new Timer();
                //  final MyTimerTask myTimerTask = new MyTimerTask();
                jsobj = new JSONObject(strjsondatas);
                userimage = (ImageView) findViewById(R.id.userimage);
                username = (TextView) findViewById(R.id.username);
                txt_Address = (TextView) findViewById(R.id.user_address);
                txt_time = (TextView) findViewById(R.id.time);
                nextappointment_time = jsobj.getString("starttime");

                nextappointment_date = jsobj.getString("ndate");

                next_dayandtime = nextappointment_date + " " + nextappointment_time;

                Log.i("GG", "datetime" + next_dayandtime);

                TimeZone tz = TimeZone.getDefault();
                System.out.println("TimeZone " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

                timezonevalue = tz.getID();

                SimpleDateFormat simpleDateFormatTarget = new SimpleDateFormat("yyyy-M-dd hh:mm a");

                calander = Calendar.getInstance();
                simpleDateFormat = new SimpleDateFormat("yyyy-M-dd hh:mm a");


                String currenttimedate = new SimpleDateFormat("yyyy/M/dd hh:mm a").format(calander.getTime());


                Log.i("GG", "date" + currenttimedate);


                txt_time.setText(jsobj.getString("starttime"));
                rating = (TextView) findViewById(R.id.rating);
                rating.setText(jsobj.getString("useravgrating") + " " + "Rating");
                username.setText(jsobj.getString("username"));


                latt = jsobj.getString("userlatitude");
                // Getting the entered longitude
                lng = jsobj.getString("userlongitude");


                platt = jsobj.getString("providerlatitude");

                plong = jsobj.getString("providerlogitude");
/*
                lay_clickaddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uri = "http://maps.google.com/maps?saddr=" + latt+","+lng+"&daddr="+platt+","+plong+"&mode=driving";
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
                        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                        startActivity(intent);
                    }
                });*/

                String result = "";
                try {
                    Geocoder geocoder;
                    List<Address> addressList = null;
                    geocoder = new Geocoder(context, Locale.getDefault());
                    addressList = geocoder.getFromLocation(Double.parseDouble(latt), Double.parseDouble(lng), 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                            sb.append(address.getAddressLine(i));

                            Log.i("TT1", "address  ***" + address.getAddressLine(i));
                        }
                                 /*   sb.append(address.getLocality() + ",");
                                    sb.append(address.getPostalCode() + ",");
                                    sb.append(address.getCountryName());*/
                        result = sb.toString();
                        Log.e("AddrVal", result);


                        txt_Address.setText(result);
                        // txt_Address.setText(jsobj.getString("username"));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                phonenumber = jsobj.getString("phonenumber");
                str_messagecontent = "hi How are you";

                String strPath = jsobj.getString("image_path");

                Log.i("FF", "strimage" + strPath.length());

                if (strPath.length() == 0) {
                    userimage.setBackgroundResource(R.drawable.default_user_icon);
                } else {

                    String strimage = gD.common_baseurl+"upload/" + jsobj.getString("image_path");

                    userimage.setImageBitmap(imgLoader.getBitmap(strimage));

                }
                Log.i("HH", "phonenumber.." + phonenumber);


                id = jsobj.getString("id");
                Log.i("HH", "id.." + id);
                Log.i("HH", "latt.." + latt);
                Log.i("HH", "lng.." + lng);
                userlatt = jsobj.getString("userlatitude");
                userlong = jsobj.getString("userlongitude");
                startend = (LinearLayout) findViewById(R.id.ll_start_stop);
                call = (LinearLayout) findViewById(R.id.ll_call);
                msg = (LinearLayout) findViewById(R.id.ll_msg);
                startlay = (TextView) findViewById(R.id.txt_start);
                endlay = (TextView) findViewById(R.id.txt_stop);
                leaveAppontlayout = (RelativeLayout) findViewById(R.id.leaveAppontlayout);

                ll_leave_appointment_layout = (RelativeLayout) findViewById(R.id.ll_leave_appointment_layout);
                leaveforanappointment = (LinearLayout) findViewById(R.id.ll_leave_appointment);

                ll_reached = (LinearLayout) findViewById(R.id.ll_reached);


//difference between two times


                LayoutInflater inflaters = LayoutInflater.from(context);
                final View dialogLayouts = inflaters.inflate(R.layout.layout_confirmation, null);

                android.app.AlertDialog.Builder alertDialogBuilders = new android.app.AlertDialog.Builder(context);
                alertDialogBuilders.setView(dialogLayouts);
                alertDialogBuilders.setCancelable(false);


                alertDialog = alertDialogBuilders.create();
                alertDialog.setCanceledOnTouchOutside(true);

                t = new Thread() {
                    public void run() {
                        try {


                            SimpleDateFormat simpleDateFormatTarget = new SimpleDateFormat("yyyy-M-dd hh:mm a");
                            while (true) {
                                calander = Calendar.getInstance();
                                simpleDateFormat = new SimpleDateFormat("yyyy-M-dd hh:mm a");
                                String currenttime = new SimpleDateFormat("yyyy-M-dd hh:mm a").format(calander.getTime());


                                Log.i("TT", "date" + currenttime);
                                Log.i("TT", "nextappoitn_time_before" + nextappointment_time);

                                try {
                                    date1 = simpleDateFormatTarget.parse(currenttime);


                                    date2 = simpleDateFormatTarget.parse(next_dayandtime);



                                    long different = date2.getTime() - date1.getTime();
                                    long secondsInMilli = 1000;
                                    long minutesInMilli = secondsInMilli * 60;
                                    long hoursInMilli = minutesInMilli * 60;
                                    long daysInMilli = hoursInMilli * 24;

                                    long elapsedDays = different / daysInMilli;
                                    different = different % daysInMilli;

                                    long elapsedHours = different / hoursInMilli;
                                    different = different % hoursInMilli;

                                    long elapsedMinutes = different / minutesInMilli;
                                    different = different % minutesInMilli;

                                    long elapsedSeconds = different / secondsInMilli;

                                    System.out.printf(
                                            "%d days, %d hours, %d minutes, %d seconds%n",
                                            elapsedDays,
                                            elapsedHours, elapsedMinutes, elapsedSeconds);


                                    Log.i("TT", "differencehrs" + elapsedDays + " : " + elapsedHours + ":" + elapsedMinutes);

                                    sleep(1000);

                                    //     alertDialog.dismiss();

                                    if (elapsedDays >= 0 && elapsedHours >= 1 && elapsedMinutes > 0) {
                                        //   t.interrupt();


                                        ((Activity) context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                Log.i("RJ", "Dialog is showing : " + alertDialog.isShowing());

                                                //  alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                                if (!alertDialog.isShowing()) {
                                                    Log.i("RJ", "suspect...!!!!");
                                                    alertDialog.show();
                                                }


                                                LinearLayout llayAlert = (LinearLayout) dialogLayouts.findViewById(R.id.llayalertDialog);
                                                // TextView tv_alert_Title = (TextView) dialogLayout.findViewById(R.id.tv_alert_Title);
                                                TextView tv_alert_Message = (TextView) dialogLayouts.findViewById(R.id.message);
                                                Button btn_submit = (Button) dialogLayouts.findViewById(R.id.ok);
                                                Button btn_cancel = (Button) dialogLayouts.findViewById(R.id.cancel);

                                                tv_alert_Message.setText("Not yet! Can only leave an hour before appointment time");
                                                FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, lScreenHeight);
                                                llayAlert.setLayoutParams(lparams);

                                                //      holder.txt_msg.setVisibility(View.VISIBLE);
                                                //    holder.txt_msg.setText("Map unlock one hour before scheduled time");
                                                Log.i("TT", "GOOD AFTYERNOON heloooo");
                                                //alertDialog.dismiss();

                                                int count = 1;
                                                if (count == 1) {
                                                    btn_cancel.setVisibility(View.GONE);
                                                }


                                                btn_submit.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        Intent i = new Intent(ProviderArrived.this, ProviderAppointment.class);
                                                        startActivity(i);
                                                        alertDialog.dismiss();


                                                    }
                                                });



                                            }
                                        });
                                        // break;



                                    } else {
                                        ((Activity) context).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {

                                                // holder.txt_msg.setText("Map unlock one hour before scheduled time");
                                                Log.i("TT", "HIIIII heloooo");

                                            }
                                        });
                                    }


                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }


                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                };
                t.start(); // here 1000 means 1000 mills i.e. 1 second


                if (strtype.equalsIgnoreCase("leave")) {
                    leaveforanappointment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!gD.isConnectingToInternet()) {
                                Toast.makeText(ProviderArrived.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                            } else {
                                GetCurrentLoctaion getcurrentloc = new GetCurrentLoctaion(latt, lng);
                                getcurrentloc.execute();

                            }
                        }
                    });


                    ll_reached.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // ll_leave_appointment_layout.setVisibility(View.INVISIBLE);
                            if (!gD.isConnectingToInternet()) {
                                Toast.makeText(ProviderArrived.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                            } else {
                                ReachedTask uploadTask = new ReachedTask();
                                uploadTask.execute();


                            }
                        }
                    });

endlay.setEnabled(true);
                    startlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            if (!gD.isConnectingToInternet()) {
                                Toast.makeText(ProviderArrived.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                            } else {

                                    // Toast.makeText(ProviderArrived.this, "Service started..", Toast.LENGTH_SHORT).show();
                                    endlay.setEnabled(false);

                                    StartClickTask uploadTask = new StartClickTask();
                                    uploadTask.execute();



                            }
                        }
                    });

                    startlay.setEnabled(false);
                    endlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (!gD.isConnectingToInternet()) {
                                Toast.makeText(ProviderArrived.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                            } else {
                                //  Toast.makeText(ProviderArrived.this, "Service ended.", Toast.LENGTH_SHORT).show();


                                EndClickTask uploadTask = new EndClickTask();
                                uploadTask.execute();

                            }
                        }
                    });

                    call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!gD.isConnectingToInternet()) {
                                Toast.makeText(ProviderArrived.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(Provider_Side_Invoice.this, "Hoi", Toast.LENGTH_SHORT).show();
                                // String number=edittext1.getText().toString();
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                try {
                                    callIntent.setData(Uri.parse("tel:" + jsobj.getString("phonenumber")));
                                    startActivity(callIntent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

                    msg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (!gD.isConnectingToInternet()) {
                                Toast.makeText(ProviderArrived.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(Provider_Side_Invoice.this, "Hoi", Toast.LENGTH_SHORT).show();
                                sendSMSMessage();
                            }
                        }
                    });





                } else if (strtype.equalsIgnoreCase("afterclickleave")) {

                    leaveforanappointment.setVisibility(View.INVISIBLE);
                    ll_reached.setVisibility(View.VISIBLE);


                    ll_reached.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!gD.isConnectingToInternet()) {
                                Toast.makeText(ProviderArrived.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                            } else {
                                // ll_leave_appointment_layout.setVisibility(View.INVISIBLE);

                                ll_leave_appointment_layout.setVisibility(View.VISIBLE);

                                ReachedTask uploadTask = new ReachedTask();
                                uploadTask.execute();


                            }


                        }
                    });


                    endlay.setEnabled(false);
                    startlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!gD.isConnectingToInternet()) {
                                Toast.makeText(ProviderArrived.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProviderArrived.this, "Service started..", Toast.LENGTH_SHORT).show();
                                endlay.setEnabled(false);

                                StartClickTask uploadTask = new StartClickTask();
                                uploadTask.execute();


                            }
                        }
                    });

                    endlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (!gD.isConnectingToInternet()) {
                                Toast.makeText(ProviderArrived.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                            } else {
                                //  Toast.makeText(ProviderArrived.this, "Service ended.", Toast.LENGTH_SHORT).show();


                                EndClickTask uploadTask = new EndClickTask();
                                uploadTask.execute();

                            }
                        }
                    });

                    call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!gD.isConnectingToInternet()) {
                                Toast.makeText(ProviderArrived.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(Provider_Side_Invoice.this, "Hoi", Toast.LENGTH_SHORT).show();
                                // String number=edittext1.getText().toString();
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                try {
                                    callIntent.setData(Uri.parse("tel:" + jsobj.getString("phonenumber")));
                                    startActivity(callIntent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

                    msg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!gD.isConnectingToInternet()) {
                                Toast.makeText(ProviderArrived.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(Provider_Side_Invoice.this, "Hoi", Toast.LENGTH_SHORT).show();
                                sendSMSMessage();
                            }
                        }
                    });


                } else if (strtype.equalsIgnoreCase("clickreach")) {


                    leaveforanappointment.setVisibility(View.INVISIBLE);
                    ll_reached.setVisibility(View.VISIBLE);

                    ll_reached.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // ll_leave_appointment_layout.setVisibility(View.INVISIBLE);
                            if (!gD.isConnectingToInternet()) {
                                Toast.makeText(ProviderArrived.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                            } else {
                                ll_leave_appointment_layout.setVisibility(View.VISIBLE);
                                ReachedTask uploadTask = new ReachedTask();
                                uploadTask.execute();
                            }
                        }
                    });


                    endlay.setEnabled(false);
                    startlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (!gD.isConnectingToInternet()) {
                                Toast.makeText(ProviderArrived.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProviderArrived.this, "Service started..", Toast.LENGTH_SHORT).show();
                                endlay.setEnabled(false);

                                StartClickTask uploadTask = new StartClickTask();
                                uploadTask.execute();


//Toast.makeText(Provider_Side_Invoice.this, "Hoi", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    endlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (!gD.isConnectingToInternet()) {
                                Toast.makeText(ProviderArrived.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                            } else {
                                // Toast.makeText(ProviderArrived.this, "Service ended.", Toast.LENGTH_SHORT).show();


                                EndClickTask uploadTask = new EndClickTask();
                                uploadTask.execute();
                            }
                        }
                    });

                    call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!gD.isConnectingToInternet()) {
                                Toast.makeText(ProviderArrived.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(Provider_Side_Invoice.this, "Hoi", Toast.LENGTH_SHORT).show();
                                // String number=edittext1.getText().toString();
                                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                                try {
                                    callIntent.setData(Uri.parse("tel:" + jsobj.getString("phonenumber")));
                                    startActivity(callIntent);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

                    msg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!gD.isConnectingToInternet()) {
                                Toast.makeText(ProviderArrived.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(Provider_Side_Invoice.this, "Hoi", Toast.LENGTH_SHORT).show();
                                sendSMSMessage();
                            }
                        }
                    });


                } else if (strtype.equalsIgnoreCase("reached")) {


                    ll_leave_appointment_layout.setVisibility(View.INVISIBLE);
                    startend.setVisibility(View.VISIBLE);


                    endlay.setEnabled(false);
                    startlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (!gD.isConnectingToInternet()) {
                                Toast.makeText(ProviderArrived.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                            } else {                            //  Toast.makeText(ProviderArrived.this, "Service started..", Toast.LENGTH_SHORT).show();
                                endlay.setEnabled(false);

                                StartClickTask uploadTask = new StartClickTask();
                                uploadTask.execute();


                            }

//Toast.makeText(Provider_Side_Invoice.this, "Hoi", Toast.LENGTH_SHORT).show();
                        }
                    });

                    endlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!gD.isConnectingToInternet()) {
                                Toast.makeText(ProviderArrived.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                            } else {
                                //   Toast.makeText(ProviderArrived.this, "Service ended.", Toast.LENGTH_SHORT).show();


                                EndClickTask uploadTask = new EndClickTask();
                                uploadTask.execute();

                            }
                        }
                    });








                } else if (strtype.equalsIgnoreCase("clickstart")) {

                    ll_leave_appointment_layout.setVisibility(View.INVISIBLE);
                    startend.setVisibility(View.VISIBLE);


                    startlay.setEnabled(false);
                    endlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (!gD.isConnectingToInternet()) {
                                Toast.makeText(ProviderArrived.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                            } else {
                                // Toast.makeText(ProviderArrived.this, "Service ended.", Toast.LENGTH_SHORT).show();
                                startlay.setEnabled(false);

                                EndClickTask uploadTask = new EndClickTask();
                                uploadTask.execute();
                            }
                        }
                    });





                }


                else if (strtype.equalsIgnoreCase("endstart")) {

                    ll_leave_appointment_layout.setVisibility(View.INVISIBLE);
                    startend.setVisibility(View.VISIBLE);


                    startlay.setEnabled(false);
                    endlay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (!gD.isConnectingToInternet()) {
                                Toast.makeText(ProviderArrived.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                            } else {
                                // Toast.makeText(ProviderArrived.this, "Service ended.", Toast.LENGTH_SHORT).show();
                                startlay.setEnabled(false);

                                EndClickTask uploadTask = new EndClickTask();
                                uploadTask.execute();
                            }
                        }
                    });


                }




                MultiDex.install(this);
                DisplayMetrics dp = getResources().getDisplayMetrics();
                int nHeight = dp.heightPixels;
                nScreenHeight = (int) ((float) nHeight / (float) 2.4);
                ScreenHeight = (int) ((float) nHeight / (float) 2.2);
                lScreenHeight = (int) ((float) nHeight / (float) 1.9);
                Log.i("MD", "Height : " + nScreenHeight);
                gD.googleMapGeneral = null;
                locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!gD.isConnectingToInternet()) {
                    Toast.makeText(ProviderArrived.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                } else {

                    if (!isGPSEnabled && !isNetworkEnabled) {


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

                        tv_alert_Message.setText("GPS has been disabled in your device. Please enable it?");

                        Button btn_submit = (Button) dialogLayout.findViewById(R.id.ok);
                        Button btn_cancel = (Button) dialogLayout.findViewById(R.id.cancel);


                        FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ScreenHeight);
                        llayAlert.setLayoutParams(lparams);


                        btn_submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);


                                alertDialog.cancel();
                                mGoogleApiClient = new GoogleApiClient.Builder(context)
                                        .addApi(LocationServices.API)
                                        .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) context)
                                        .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) context)
                                        .build();
                            }
                        });

                        btn_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                alertDialog.dismiss();


                            }
                        });


                    } else {
                        if (!isMyServiceRunning(GPSTracker.class)) {


                            mGoogleApiClient = new GoogleApiClient.Builder(context)
                                    .addApi(LocationServices.API)
                                    .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) context)
                                    .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) context)
                                    .build();
                            //to start the service
                            //  startService(new Intent(User_Category.this, GPSTracker.class));
                        }
                        // getCurrentLocation("onload");
                    }

                }
                //to start the service
                startService(new Intent(ProviderArrived.this, GPSTracker.class));
                sharedpreferences = getSharedPreferences("myprefer", Context.MODE_PRIVATE);


                strUserName = getIntent().getStringExtra("userName");

                mapFragment = ((MapFragment) getFragmentManager().
                        findFragmentById(R.id.map));


                mapFragment.getMapAsync(this);

                mGoogleApiClient = new GoogleApiClient.Builder(context)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();


                markerPoints = new ArrayList<LatLng>();


                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }



            } catch (JSONException e) {
                e.printStackTrace();
            }


        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(ProviderArrived.this, GPSTracker_Provider.class));
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }

    }


    @Override
    public void onResume() {

        try {
            TimeZone tz = TimeZone.getDefault();
            System.out.println("TimeZone " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

            timezonevalue = tz.getID();

            super.onResume();
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        try {
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return false;
    }



    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        String url = null;

        try {
            // Origin of route
            String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

            // Destination of route
            String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


            // Building the parameters to the web service
            String parameters = str_origin + "&" + str_dest;

            // Output format
            String output = "json";

            // Building the url to the web service
            url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=AIzaSyCVzI-ok_PKVFELZtub8YPOmC8cykoF5kw&mode=driving";
            Log.e("URL", url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return url;
    }




    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {

        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    protected void sendSMSMessage() {
        Log.i("Send SMS", "");
        //  String phoneNo = txtphoneNo.getText().toString();
        // String message = txtMessage.getText().toString();

        try {

            Log.i("RR", "phonenumber" + phonenumber);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            // intent.putExtra("address", "98765433");
            intent.putExtra("sms_body", "hi");
            intent.setData(Uri.parse("smsto:" + phonenumber));
            context.startActivity(intent);


        } catch (Exception e) {
            alert.showAlertDialog(
                    ProviderArrived.this,
                    "SMS sending failed. Please try again",
                    false);
            e.printStackTrace();
        }
    }



    @Override
    public void onBackPressed() {

        try {
            Intent i=null;
            Log.i("KK", "i am Clicked in back");
            if (alertDialog.isShowing()) {


     i = new Intent(ProviderArrived.this, ProviderAppointment.class);
                Log.i("KK", "i am Clicked in back  ****");
            }
            else
            {
           i = new Intent(ProviderArrived.this, ProviderAppointment.class);
                Log.i("KK", "i am Clicked in back  ####");
            }
            startActivity(i);

            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onConnected(Bundle bundle) {
        // TODO Auto-generated method stub
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                REQUEST,
                this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            // TODO Auto-generated method stub
            if (location == null)
                return;


            if (marker == null) {

                marker = googleMap.addMarker(new MarkerOptions()
                        .flat(true)
                        // .icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarker))
                        .title("Your Current Position")
                        .anchor(0.5f, 0.5f)
                        .position(new LatLng(location.getLatitude(), location.getLongitude())));
                marker.remove();

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16.0f));


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap Map) {
        try {
            this.googleMap = Map;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            //googleMap.getUiSettings().setRotateGesturesEnabled(false);

            googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                @Override
                public void onCameraChange(CameraPosition position) {
                    // TODO Auto-generated method stub

                    Log.i("PP" + position.target.latitude, "" + position.target.longitude);

                    new ReverseGeocodingTask().execute(position.target);


                }
            });


            locationManager = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {

                if (isNetworkEnabled) {
                    if (locationManager != null) {
                        final Location location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            Log.d("activity", "LOC by Network");
                            //TutorialsPoint = new LatLng(11.909494041919183, 79.80592131614685);
                            TutorialsPoint = new LatLng(location.getLatitude(), location.getLongitude());

                            lay_clickaddress.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String uri = "http://maps.google.com/maps?saddr=" + location.getLatitude()+","+location.getLongitude()+"&daddr="+latt+","+lng+"&mode=driving";

                                    // String uri = "http://maps.google.com/maps?saddr=" + latt+","+lng+"&daddr="+location.getLatitude()+","+location.getLongitude()+"&mode=driving";
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                    startActivity(intent);
                                }
                            });

                            if (mark == null) {


                                mark = googleMap.addMarker(new MarkerOptions().position(TutorialsPoint));


                                //  TutorialsPoints = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));


                                //  Marker mark = gD.googleMapGeneral.addMarker(new MarkerOptions().position(TutorialsPoints).title("lat-" + Double.parseDouble(lat) + " Long-" + Double.parseDouble(lng)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(TutorialsPoint)      // Sets the center of the map to Mountain View
                                        .zoom(17)                   // Sets the zoom// Sets the orientation of the camera to east// Sets the tilt of the camera to 30 degrees
                                        .build();                   // Creates a CameraPosition from the builder
                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                mark.remove();
                            }
                            googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                                // Use default InfoWindow frame
                                @Override
                                public View getInfoWindow(Marker arg0) {
                                    return null;
                                }

                                // Defines the contents of the InfoWindow
                                @Override
                                public View getInfoContents(Marker arg0) {

                                    // Getting view from the layout file info_window_layout
                                    View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);

                                    // Getting the position from the marker
                                    LatLng latLng = arg0.getPosition();

                                    // Getting reference to the TextView to set latitude
                                    TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);

                                    // Getting reference to the TextView to set longitude
                                    TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);

                                    // Setting the latitude
                                    tvLat.setText(arg0.getTitle());

                    /*// Setting the longitude
                    tvLng.setText("Longitude:" + latLng.longitude);*/

                                    // Returning the view containing InfoWindow contents
                                    return v;

                                }
                            });

                            //for Destination


                            //TutorialsPoints = new LatLng(11.909494041919183, 79.80592131614685);

                            TutorialsPoints = new LatLng(Double.parseDouble(userlatt), Double.parseDouble(userlong));


                            // al.addMarker(new MarkerOptions().position(TutorialsPoints).title("lat-" + Double.parseDouble(lat) + " Long-" + Double.parseDouble(lng)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

                            final LatLng origin = TutorialsPoint;
                            final LatLng dest = TutorialsPoints;

                            Log.i("JJ", "TutorialsPointsdes.." + TutorialsPoints);
                            Log.i("JJ", "TutorialsPointorigii.." + TutorialsPoint);

                            String url = getDirectionsUrl(origin, dest);


                            DownloadTask downloadTask = new DownloadTask(origin, dest);
                            downloadTask.execute(url);

                        }
                    }
                }

            }

            if (Destmark == null) {
                //TutorialsPoints = new LatLng(11.909494041919183, 79.80592131614685);

                TutorialsPoints = new LatLng(Double.parseDouble(userlatt), Double.parseDouble(userlong));


                Destmark = googleMap.addMarker(new MarkerOptions().position(TutorialsPoints).title("lat-" + userlatt + " Long-" + userlong));
                //   Destmark = googleMap.addMarker(new MarkerOptions().position(TutorialsPoints).title("lat-" + Double.parseDouble(userlatt) + " Long-" + Double.parseDouble(userlong)).icon(BitmapDescriptorFactory.fromResource(R.drawable.greennewmarker)));

                //Marker mark = gD.googleMapGeneral.addMarker(new MarkerOptions().position(TutorialsPoints).title("lat-" + latt + " Long-" + lng).icon(BitmapDescriptorFactory.fromResource(R.drawable.greennewmarker)));

                final CameraPosition DestPosition = new CameraPosition.Builder()
                        .target(TutorialsPoints)      // Sets the center of the map to Mountain View
                        .zoom(17)                   // Sets the zoom// Sets the orientation of the camera to east// Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(DestPosition));

                Destmark.remove();

                googleMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if (marker.equals(Destmark)) {
                            Log.i("TSR", "i am Clicked in blue marker");

                        }
                        return true;

                    }
                });
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


    }





    @Override
    public void onMapClick(LatLng latLng) {

    }


    private class ReverseGeocodingTask extends AsyncTask<LatLng, Void, String> {
        double _latitude, _longitude;

        @Override
        protected String doInBackground(LatLng... params) {
            Geocoder geocoder = new Geocoder(context);
            _latitude = params[0].latitude;
            _longitude = params[0].longitude;
            Log.i("PP.." + _latitude, "" + _longitude);


            List<Address> addresses = null;
            String addressText = "";

            try {
                addresses = geocoder.getFromLocation(_latitude, _longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null && addresses.size() > 0) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    if (returnedAddress.getMaxAddressLineIndex() == (i - 1)) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i));
                    } else {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append(",");
                    }
                }
                addressText = strReturnedAddress.toString();

            }

            return addressText;
        }

        @Override
        protected void onPostExecute(String addressText) {
            final String result = addressText;
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    //tvAddress.setText(result);
                }
            });
        }
    }



    //send provider current location

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyInfoWindowAdapter() {
            myContentsView = getLayoutInflater().inflate(R.layout.user_current_loc_addr_alert, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            TextView tvSnippet = ((TextView) myContentsView.findViewById(R.id.text_address));
            tvSnippet.setText(marker.getSnippet());

            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        String data = "";

        LatLng OLatLang;
        LatLng dlatLang;

        public DownloadTask(LatLng origin, LatLng dest) {
            this.OLatLang = origin;
            this.dlatLang = dest;

        }

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service


            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                // ParseLegJson PL = new ParseLegJson(url[0]);
                // data = PL.sendGet();

                Log.d("BT", data);

            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            // Invokes the thread for parsing the JSON data
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);

//


        }
    }


//provider click reached

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);

                Log.i("RAJI", "RESP : " + routes.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {


            try {
              /*  if (googleMap != null) {
                    googleMap.clear();
                }*/

                Log.i("GPS", "Size : " + result.size());
                Log.i("RAJA", "RESP : " + result.toString());

                ArrayList<LatLng> points = null;
                PolylineOptions lineOptions = null;
                MarkerOptions markerOptions = new MarkerOptions();
                lineOptions = new PolylineOptions();

                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(0);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);



                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));


                        Log.i("RAJA", "lat : " + lat);
                        Log.i("RAJA", "lng : " + lng);

                        LatLng position = new LatLng(lat, lng);


                        for (Map.Entry<String, String> entry : point.entrySet()) {
                            if (entry.getKey().equalsIgnoreCase("man")) {
                                String strtitle = entry.getValue();

                                String strDist = "", strDur = "";
                                for (Map.Entry<String, String> entrys : point.entrySet()) {
                                    if (entrys.getKey().equalsIgnoreCase("dist")) {
                                        strDist = entrys.getValue();
                                    }
                                    if (entrys.getKey().equalsIgnoreCase("dur")) {
                                        strDur = entrys.getValue();
                                    }
                                }

                                if (strDist.length() > 0 && strDur.length() > 0) {
                                    if (strtitle.contains("turn")) {
                                        strtitle = "Take : " + strtitle + "\n Distance : " + strDist + "\n Duration : " + strDur;
                                    } else {
                                        strtitle = "Go : " + strtitle + "\n Distance : " + strDist + "\n Duration : " + strDur;
                                    }

                                    Log.i("RJ", "Name : " + strtitle);
                                }


                                mark = googleMap.addMarker(new MarkerOptions().position(position).snippet(strtitle).title(strtitle).icon(BitmapDescriptorFactory.fromResource(R.drawable.flag_small)));
                                mark.showInfoWindow();
                                //mark.setAlpha(0.0f);
                                // mark.setInfoWindowAnchor(.5f, 1.0f);

                            } else {
                                Log.i("GGH", "VAL : " + j);
                                if (j == 0) {
                                    mark = googleMap.addMarker(new MarkerOptions().position(position).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.rednewmarker)));
                                } else if (j != 2) {
                                    if (mark != null) {
                                        if (j == path.size() - 1) {
                                            mark = googleMap.addMarker(new MarkerOptions().position(position).title("").icon(BitmapDescriptorFactory.fromResource(R.drawable.greennewmarker)));
                                            // mark.showInfoWindow();
                                        }
                                    }
                                }
                            }
                        }
                        points.add(position);
                    }
                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(10);
                    lineOptions.color(Color.parseColor("#42dcdc"));
                }


                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        Log.i("GGGK", "HI....");
                        // googleMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
                        if (marker.getTitle().toString().length() > 0) {

                            marker.showInfoWindow();
                        }

                        return true;
                    }
                });


                // Drawing polyline in the Google Map for the i-th route
               /* if (googleMap != null) {
                    googleMap.clear();
                }*/

                googleMap.addPolyline(lineOptions);

                // Log.e("distance", distance);
                //  Log.e("duration", duration);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


// provider click start button

    class GetCurrentLoctaion extends AsyncTask {

        String sResponse = null;
        String providerlatitude;
        String providerlongitude;

        public GetCurrentLoctaion(String lat, String aLong) {
            this.providerlatitude = lat;
            this.providerlongitude = aLong;
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

                multipart.addFormField("id", id);


                multipart.addFormField("providerlatitude", providerlatitude);

                multipart.addFormField("providerid", SplashActivity.sharedPreferences.getString("UID", null));

                Log.e("providerlongitude", providerlongitude);
                Log.e("providerlatitude", providerlatitude);
                multipart.addFormField("timezone", timezonevalue);
                Log.i("RR", "timezonevalue" + timezonevalue);

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
            ProviderArrived.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gD.callload(context, nScreenHeight);



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

                        LayoutInflater inflater = LayoutInflater.from(context);
                        View dialogLayout = inflater.inflate(R.layout.leave_fro_appointment, null);

                        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
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
                            }
                        });


                        leaveforanappointment.setVisibility(View.INVISIBLE);
                        ll_reached.setVisibility(View.VISIBLE);

                    } else if (jsobj.getInt("code") == 7) {

                        // gD.showAlertDialog(context, "", jsobj.getString("status"), nScreenHeight, 1);
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
                                Intent i = new Intent(ProviderArrived.this, ProviderAppointment.class);
                                startActivity(i);
                                finish();
                                alertDialog.dismiss();
                            }
                        });

                    } else if (jsobj.getInt("code") == 9) {

                        // gD.showAlertDialog(context, "", jsobj.getString("status"), nScreenHeight, 1);
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

                                Intent i = new Intent(ProviderArrived.this, ProviderAppointment.class);
                                startActivity(i);
                                finish();

                                alertDialog.dismiss();
                            }
                        });

                    }


                } else {
                    dialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


    // provider click end button


    class ReachedTask extends AsyncTask {

        String sResponse = null;


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


                multipart.addFormField("id", id);


                Log.i("HH", "id1." + id);

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
            ProviderArrived.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gD.callload(context, nScreenHeight);



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
                        ll_leave_appointment_layout.setVisibility(View.VISIBLE);
                        //reachpop();

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


                                alertDialog.dismiss();


                            }
                        });
                        //  gD.showAlertDialog(context, "", "Waiting for client to confirm Your arrival!", ScreenHeight, 1);
                    } else if (jsobj.getInt("code") == 2) {
                        ll_leave_appointment_layout.setVisibility(View.INVISIBLE);
                        startend.setVisibility(View.VISIBLE);
                        startlay.setEnabled(true);
                        endlay.setEnabled(false);
                    } else if (jsobj.getInt("code") == 3) {
                        gD.showAlertDialog(context, "", "You have reached your client", nScreenHeight, 1);
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


                multipart.addFormField("id", id);
                multipart.addFormField("start", "1");
                multipart.addFormField("timezone", timezonevalue);
                Log.i("RR", "timezonevalue" + timezonevalue);
                multipart.addFormField("providerid", SplashActivity.sharedPreferences.getString("UID", null));


                Log.i("HH", "id1." + id);

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
            ProviderArrived.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gD.callload(context, nScreenHeight);



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
                        gD.showAlertDialog(context, "", "You have started the service!", nScreenHeight, 1);

                        endlay.setEnabled(true);
                        startlay.setClickable(false);
                        startlay.setEnabled(false);


                    }
                } else {
                    dialog.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    class EndClickTask extends AsyncTask {

        String sResponse = null;


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


                multipart.addFormField("id", id);
                multipart.addFormField("timezone", timezonevalue);
                Log.i("RR", "timezonevalue" + timezonevalue);
                multipart.addFormField("end", "1");
                multipart.addFormField("providerid", SplashActivity.sharedPreferences.getString("UID", null));


                Log.i("HH", "id1." + id);

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
            ProviderArrived.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gD.callload(context, nScreenHeight);



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




                        Intent i = new Intent(ProviderArrived.this, Provider_CompleteRaisePage.class);
                        i.putExtra("sR", strjsondatas);
                        i.putExtra("type", strtype);
                        startActivity(i);
                        finish();


                    } else if (jsobj.getInt("code") == 0) {
                        startlay.setEnabled(false);
                        endlay.setEnabled(true);

                        gD.showAlertDialog(context, "", "Waiting on client confirmation", nScreenHeight, 1);

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










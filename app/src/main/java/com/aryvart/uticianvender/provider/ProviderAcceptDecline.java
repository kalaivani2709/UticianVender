package com.aryvart.uticianvender.provider;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.bean.ProviderBean;
import com.aryvart.uticianvender.bean.UserHistoryBean;
import com.aryvart.uticianvender.genericclasses.GeneralData;

import com.aryvart.uticianvender.imageCache.ImageLoader;
import com.aryvart.uticianvender.user.DirectionsJSONParser;
import com.aryvart.uticianvender.user.GPSTracker;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by android3 on 17/7/16.
 */
public class ProviderAcceptDecline extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, GoogleMap.OnMapClickListener  {

    public static SharedPreferences sharedPreferences;

    public GoogleMap googleMap;
    Double lat = 0.0, longi = 0.0;
    Marker TP;
    ProgressDialog dialog;
    String result, userid;
    LocationManager locationManager;

    String strUserName;
    Marker marker;
    GeneralData gD;
    Context context;
    TextView username, txt_time, tv_totalRateAD;
    ImageLoader imgLoader;
    ImageView userimage;
    TextView txt_accept, txt_decline;
    ImageView back;
    String strjsondatas;
    LinearLayout llayServiceRN;
    ArrayList<ProviderBean> alPB;
    int nTotalAmount;
    ArrayList<LatLng> markerPoints;
    String status;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE,
            Manifest.permission.CAMERA, Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_WIFI_STATE
    };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    MapFragment mapFragment;

    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)
            .setFastestInterval(16)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    public static boolean isGPSEnabled = false;
    public static boolean isNetworkEnabled = false;
    static SharedPreferences sharedpreferences;

    Marker mark, Destmark;
    Date date1, date2;
    LatLng TutorialsPoint = new LatLng(lat, longi);
    LatLng TutorialsPoints = new LatLng(lat, longi);
    Thread t;
    Calendar calander;
    SimpleDateFormat simpleDateFormat;
    String nextappointment_time, nextappointment_date, next_dayandtime;
    android.app.AlertDialog alertDialog;
    //    new Changes
    private GoogleApiClient mGoogleApiClient = null;

    //date conversion
    private static String getCurrentDateInSpecificFormat(int nYear, int nMonth, int nDate, int nHour, int nMinute) {
        Log.i("RAJ", "getCurrentDateInSpecificFormat method : Year :" + nYear + ",Month : " + nMonth + ",Date : " + nDate + "Hour : " + nHour + "Minute : " + nMinute);

        Log.i("RAJ", "getCurrentDateInSpecificFormat month : " + nMonth);



        String formattedDate = "";
        try {
            String strCMonth = "";
            String strCDate = "";

            if (nMonth <= 9) {
                strCMonth = "0" + nMonth;
            } else {
                strCMonth = "" + nMonth;
            }

            if (nDate <= 9) {
                strCDate = "0" + nDate;
            } else {
                strCDate = "" + nDate;
            }
            String strNeedDateVal = nYear + "-" + strCMonth + "-" + strCDate + " " + nHour + ":" + nMinute;



            //New Changes for dat
            DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd hh:ss");
            Date date = originalFormat.parse(strNeedDateVal);
            String dayNumberSuffix = getDayNumberSuffix(Integer.parseInt(strCDate));
           /* DateFormat targetFormat = new SimpleDateFormat("MMM dd'" + dayNumberSuffix + "', yyyy hh:ss a");*/
            DateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy hh:ss a");
            formattedDate = targetFormat.format(date);


        } catch (Exception e) {
            e.printStackTrace();
        }



        return formattedDate;
    }

    private static String getDayNumberSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    int nScreenHeight;
    String userlatt,userlong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.provider_accept_decline);
            context = this;
            imgLoader = new ImageLoader(context);
            dialog = new ProgressDialog(ProviderAcceptDecline.this);
            verifyStoragePermissions(this);

            gD = new GeneralData(context);
            llayServiceRN = (LinearLayout) findViewById(R.id.llaySR);
            try {
                back = (ImageView) findViewById(R.id.img_back);
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Intent i = new Intent(ProviderAcceptDecline.this, ProviderHistoryPage.class);
                        startActivity(i);
                    }
                });
                strjsondatas = getIntent().getStringExtra("msg");
                Log.i("HH", "jsonvales1.." + strjsondatas);
                DisplayMetrics dp = getResources().getDisplayMetrics();
                int nHeight = dp.heightPixels;
                nScreenHeight = (int) ((float) nHeight / (float) 2.1);

                username = (TextView) findViewById(R.id.username);
                JSONObject jsobj = new JSONObject(strjsondatas);
                username.setText(jsobj.getString("username"));
                userimage = (ImageView) findViewById(R.id.userimage);

               // txt_date = (TextView) findViewById(R.id.txt_date);
                tv_totalRateAD = (TextView) findViewById(R.id.tv_totalRateAD);
                userid = jsobj.getString("id");

                userlatt = jsobj.getString("userlatitude");
                userlong = jsobj.getString("userlongitude");
                Log.i("HH", "latt.." + userlatt);
                Log.i("HH", "lng.." + userlong);
                txt_time = (TextView) findViewById(R.id.time);
                String strSeperateVal = jsobj.getString("date");
                String strTimeVal = jsobj.getString("nstarttime");
            /*    int nYear = Integer.parseInt(strSeperateVal.split("-")[0]), nMonth = Integer.parseInt(strSeperateVal.split("-")[1]), nDate = Integer.parseInt(strSeperateVal.split("-")[2]);
                int nHour = Integer.parseInt(strTimeVal.split(":")[0]);
                int nMinute = Integer.parseInt(strTimeVal.split(":")[1]);
                String strneedDate = getCurrentDateInSpecificFormat(nYear, nMonth - 1, nDate, nHour, nMinute);*/
                if (!gD.isConnectingToInternet()) {
                    gD.showAlertDialogNet(context, "Unable to connect with Internet. Please check your internet connection and try again");
                }
              //  txt_date.setText(strneedDate.split(" ")[0] + strneedDate.split(" ")[1] + strneedDate.split(" ")[2]);
                txt_time.setText(strTimeVal);
                Log.i("mA", "URL : " + gD.common_baseurl+"upload/" + jsobj.getString("image_path"));
                String strPath = jsobj.getString("image_path");
                if (strPath.length() == 0) {
                    userimage.setBackgroundResource(R.drawable.default_user_icon);
                } else {

                    String strimage = gD.common_baseurl+"upload/" + jsobj.getString("image_path");

                    userimage.setImageBitmap(imgLoader.getBitmap(strimage));

                }


                //userimage.setImageBitmap(imgLoader.getBitmap("http://aryvartdev.com/projects/utician/upload/" + jsobj.getString("image_path")));
                //imgLoader.DisplayImage("http://aryvartdev.com/projects/utician/upload/" + jsobj.getString("image_path"), userimage);
                alPB = new ArrayList<ProviderBean>();
                JSONArray jsAr = jsobj.getJSONArray("invoice");
                Log.i("sR", "ProviderInvoice : " + strjsondatas + "******** jsAr.length():" + jsAr.length());
                for (int i = 0; i < jsAr.length(); i++) {
                    Log.i("sR", "values " + jsAr.getJSONObject(i).getString("rate"));
                    LinearLayout llayEachLinear = new LinearLayout(this);
                    llayEachLinear.setOrientation(LinearLayout.HORIZONTAL);
                    llayEachLinear.setWeightSum(3);

                    LinearLayout.LayoutParams llayEachParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
                    llayEachLinear.setLayoutParams(llayEachParams);
                    llayEachParams.weight = 1;

                    LinearLayout.LayoutParams llayleftParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
                    LinearLayout llayLeft = new LinearLayout(this);
                    llayleftParams.weight = 2;
                    llayLeft.setLayoutParams(llayleftParams);

                    LinearLayout.LayoutParams llaycenterParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
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
                    txt_service.setPadding(3, 0, 0, 0);
                    txt_service.setGravity(Gravity.CENTER_VERTICAL);
                    txt_service.setLayoutParams(textParams);
                    txt_service.setText(jsAr.getJSONObject(i).getString("service"));
                    TextView txt_rate = new TextView(this);
                    txt_rate.setLayoutParams(textParams);
                    txt_rate.setPadding(3, 0, 0, 0);
                    txt_rate.setGravity(Gravity.CENTER_VERTICAL);
                    txt_rate.setText("$ " + jsAr.getJSONObject(i).getString("rate"));
                    LinearLayout.LayoutParams imagParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    ImageView img_service = new ImageView(this);
                    img_service.setPadding(0, 3, 0, 3);
                    img_service.setImageResource(R.drawable.blue_santa);


                    if (jsAr.getJSONObject(i).getString("service").equalsIgnoreCase("Barber")) {
                        img_service.setImageResource(R.drawable.blue_santa);
                    } else if (jsAr.getJSONObject(i).getString("service").equalsIgnoreCase("Hair Stylist")) {
                        img_service.setImageResource(R.drawable.blue_santa);
                    } else if (jsAr.getJSONObject(i).getString("service").equalsIgnoreCase("Makeup Artist")) {
                        img_service.setImageResource(R.drawable.blue_santa);
                    } else if (jsAr.getJSONObject(i).getString("service").equalsIgnoreCase("Nail Technician")) {
                        img_service.setImageResource(R.drawable.blue_santa);
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
                                tv_totalRateAD.setText("$ " + nTotalAmount);
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            MultiDex.install(this);
            txt_accept = (TextView) findViewById(R.id.txt_accept);
            txt_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(ProviderAcceptDecline.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        status = String.valueOf(1);



                    GetAcceptDeclineResponse get_accdecl_response = new GetAcceptDeclineResponse(context, userid, status);
                    get_accdecl_response.execute();

                }}
            });


            txt_decline = (TextView) findViewById(R.id.txt_decline);
            txt_decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(ProviderAcceptDecline.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        status = String.valueOf(2);

                        GetAcceptDeclineResponse get_accdecl_response = new GetAcceptDeclineResponse(context, userid, status);
                        get_accdecl_response.execute();

                    }
                }
            });


            gD.googleMapGeneral = null;
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!gD.isConnectingToInternet()) {
                Toast.makeText(ProviderAcceptDecline.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
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


                    FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, nScreenHeight);
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
            startService(new Intent(ProviderAcceptDecline.this, GPSTracker.class));
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
/*
            // for current location
            try {
                Handler h = new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if ((sharedpreferences.getString("Lat", null) != null) && (sharedpreferences.getString("Long", null) != null)) {
                            Log.e("Lat->", (sharedpreferences.getString("Lat", null)));
                            Log.e("Long->", (sharedpreferences.getString("Long", null)));


                            try {
                                Geocoder geocoder;
                                List<Address> addressList = null;
                                geocoder = new Geocoder(ProviderAcceptDecline.this, Locale.getDefault());
                                addressList = geocoder.getFromLocation(Double.parseDouble(sharedpreferences.getString("Lat", null)), Double.parseDouble(sharedpreferences.getString("Long", null)), 1);
                                if (addressList != null && addressList.size() > 0) {
                                    Address address = addressList.get(0);
                                    StringBuilder sb = new StringBuilder();
                                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                                        sb.append(address.getAddressLine(i)).append("\n");
                                    }
                                    sb.append(address.getLocality()).append("\n");
                                    sb.append(address.getPostalCode()).append("\n");
                                    sb.append(address.getCountryName());
                                    result = sb.toString();
                                    Log.e("Address-->", result);
                                    gD.googleMapGeneral.setInfoWindowAdapter(new MyInfoWindowAdapter());

                                    Log.i("JJ", "latlong.." + TutorialsPoint);


                                    TP = gD.googleMapGeneral.addMarker(new MarkerOptions().position(TutorialsPoint).snippet(result).icon(BitmapDescriptorFactory.fromResource(R.drawable.rednewmarker)));


                                    CameraPosition cameraPosition = new CameraPosition.Builder()
                                            .target(TutorialsPoint)      // Sets the center of the map to Mountain View
                                            .zoom(10)                   // Sets the zoom
                                                    // Sets the orientation of the camera to east
                                            .tilt(90)     // Sets the tilt of the camera to 30 degrees
                                            .build();                   // Creates a CameraPosition from the builder
                                    gD.googleMapGeneral.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                                    gD.googleMapGeneral.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                        @Override
                                        public boolean onMarkerClick(Marker marker) {
                                            if (marker.equals(TP)) {


                                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                                        .target(TutorialsPoint)      // Sets the center of the map to Mountain View
                                                        .zoom(10)                   // Sets the zoom
                                                                // Sets the orientation of the camera to east
                                                        .tilt(90)     // Sets the tilt of the camera to 30 degrees
                                                        .build();                   // Creates a CameraPosition from the builder
                                                gD.googleMapGeneral.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                                            }
                                            return true;

                                        }
                                    });


                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }, 1000);
            } catch (Exception e) {
                Log.e("KK", e.toString());

            }*/
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


    }


    @Override
    protected void onResume() {

        super.onResume();
    }




    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }



    private String readStream(InputStream in) {
        StringBuffer response = null;
        try {
            BufferedReader reader = null;
            response = new StringBuffer();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.toString();
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


            googleMap.getUiSettings().setRotateGesturesEnabled(true);


            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(false);

            googleMap.getUiSettings().setScrollGesturesEnabled(true);
            googleMap.getUiSettings().setTiltGesturesEnabled(true);

            googleMap.getUiSettings().setZoomGesturesEnabled(true);
            googleMap.getUiSettings().setAllGesturesEnabled(true);
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




    class GetAcceptDeclineResponse extends AsyncTask {
        String user_id = null;
        String sResponse = null;
        Context cont;
        String status;


        public GetAcceptDeclineResponse(Context context, String userid, String status) {
            this.user_id = userid;
            this.cont = context;
            this.status = status;
        }

        @Override
        protected void onPreExecute() {
            ProviderAcceptDecline.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gD.callload(context, nScreenHeight);



                }
            });
// TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] param) {
            try {

                String requestURL = gD.common_baseurl+"acceptappointment.php?id=" + user_id + "&status=" + status;




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

                if (jsobj.getInt("code") == 2)

                {

                    Intent voicpage = new Intent(ProviderAcceptDecline.this, ProviderHistoryPage.class);

                    // voicpage.putExtra("providerId", pId);
                    voicpage.putExtra("sR", strjsondatas);


                    startActivity(voicpage);
                    finish();


                } else {
                    ((Activity) cont).finish();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    public void onBackPressed() {
        finish();
       Intent i = new Intent(ProviderAcceptDecline.this, ProviderHistoryPage.class);
        startActivity(i);

    }



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
}
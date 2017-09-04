package com.aryvart.uticianvender.provider;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aryvart.uticianvender.R;

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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by android3 on 18/6/16.
 */
public class Provider_CompleteRaisePage extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnMapReadyCallback, GoogleMap.OnMapClickListener {

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
    private static final int REQUEST_EXTERNAL_STORAGE = 1;


    public static boolean isGPSEnabled = false;
    public static boolean isNetworkEnabled = false;
    public static SharedPreferences sharedPreferences;
    static SharedPreferences sharedpreferences;
    public GoogleMap googleMap;

    Double lat = 0.0, longi = 0.0;

    LocationManager locationManager;
    LatLng TutorialsPoint = new LatLng(lat, longi);
    LatLng TutorialsPoints = new LatLng(lat, longi);
    String strUserName;
    Marker marker;
    GeneralData gD;
    int nScreenHeight, ScreenHeight, nnScreenHeight;

    TextView username, txt_time;

    ImageView userimage, user_image;
    TextView raiseinvoise, complete;

    String distance = "";
    String duration = "";
    ArrayList<LatLng> markerPoints;

    RatingBar ratingBar;
    ProgressDialog dialog;
    Button btn_submit_review;
    ImageView close, imvBack;
    TextView txt_username;
    EditText edit_review;
    String strtype, str_rating, str_des, strjsondatas, str_id, userlatt, userlong;;
    ImageLoader imgLoader;
    Context context;
    String id, rowid;
    String latt, lng;
    private GoogleApiClient mGoogleApiClient = null;

    TextView rating;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;



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

    MapFragment mapFragment;
    Marker mark, Destmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.provider_completeraisepage);


            context = this;
            imgLoader = new ImageLoader(context);

            verifyStoragePermissions(this);
            gD = new GeneralData(context);
            dialog = new ProgressDialog(Provider_CompleteRaisePage.this);

            SplashActivity.sharedPreferences = context.getSharedPreferences("regid", Context.MODE_PRIVATE);
            sharedPreferences = getSharedPreferences("GenerateId", Context.MODE_PRIVATE);

            SharedPreferences.Editor editname = sharedPreferences.edit();
            editname.putInt("isGenerate", 0);
            editname.commit();

            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(context, "Unable to connect with Internet. Please check your internet connection and try again");
            }
            strjsondatas = getIntent().getStringExtra("sR");

            strtype = getIntent().getStringExtra("type");


            Log.i("GG", "jsonvales.." + strjsondatas);

            username = (TextView) findViewById(R.id.username);
            imvBack = (ImageView) findViewById(R.id.img_back);
            JSONObject jsobj = null;
            try {
                jsobj = new JSONObject(strjsondatas);


                username.setText(jsobj.getString("username"));

                id = jsobj.getString("id");

                latt = jsobj.getString("userlatitude");
                // Getting the entered longitude
                lng = jsobj.getString("userlongitude");

                userlatt = jsobj.getString("userlatitude");
                userlong = jsobj.getString("userlongitude");

                Log.i("JJ", "latt" + latt);

                Log.i("JJ", "lng" + lng);

                userimage = (ImageView) findViewById(R.id.userimage);

                // txt_date = (TextView) findViewById(R.id.txt_date);


                txt_time = (TextView) findViewById(R.id.time);
                rowid = jsobj.getString("id");
                String strSeperateVal = jsobj.getString("date");
                String strTimeVal = jsobj.getString("starttime");

                txt_time.setText(strTimeVal);

                username.setText(jsobj.getString("username"));
                rating = (TextView) findViewById(R.id.rating);
                rating.setText(jsobj.getString("useravgrating") + " " + "Rating");

                Log.i("mA", "URL : " + gD.common_baseurl+"upload/" + jsobj.getString("image_path"));

                String strPath = jsobj.getString("image_path");

                Log.i("FF", "strimage" + strPath.length());

                if (strPath.length() == 0) {
                    userimage.setBackgroundResource(R.drawable.default_user_icon);
                } else {

                    String strimage = gD.common_baseurl+"upload/" + jsobj.getString("image_path");

                    userimage.setImageBitmap(imgLoader.getBitmap(strimage));

                }


                MultiDex.install(this);

                raiseinvoise = (TextView) findViewById(R.id.raiseinvoise);
                complete = (TextView) findViewById(R.id.complete);


                raiseinvoise.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!gD.isConnectingToInternet()) {
                            Toast.makeText(Provider_CompleteRaisePage.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                        } else {
                            RaiseInvoiceTask generateinvoice = new RaiseInvoiceTask(context, rowid);
                            generateinvoice.execute();


                            //finish();
                        }
                    }
                });


                complete.setEnabled(false);
                complete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (!gD.isConnectingToInternet()) {
                            Toast.makeText(Provider_CompleteRaisePage.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                        } else {
                            CompleteTask uploadTask = new CompleteTask();
                            uploadTask.execute();
                        }
                    }
                });

                imvBack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!gD.isConnectingToInternet()) {
                            Toast.makeText(Provider_CompleteRaisePage.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent i = new Intent(Provider_CompleteRaisePage.this, ProviderAppointment.class);
                            startActivity(i);
                            finish();
                            //finishAffinity();
                        }
                    }
                });

                if (strtype.equalsIgnoreCase("raisepage")) {


                    complete.setVisibility(View.VISIBLE);
                    raiseinvoise.setVisibility(View.GONE);
                    complete.setEnabled(true);

                    complete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (!gD.isConnectingToInternet()) {
                                Toast.makeText(Provider_CompleteRaisePage.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                            } else {


                                CompleteTask uploadTask = new CompleteTask();
                                uploadTask.execute();

                            }
                        }
                    });

                }

                if (strtype.equalsIgnoreCase("paidprocessandcomplete")) {


                    complete.setVisibility(View.VISIBLE);
                    raiseinvoise.setVisibility(View.GONE);
                    complete.setEnabled(true);
                    complete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (!gD.isConnectingToInternet()) {
                                Toast.makeText(Provider_CompleteRaisePage.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                            } else {
                                CompleteTask uploadTask = new CompleteTask();
                                uploadTask.execute();
                            }
                        }
                    });

                }


                DisplayMetrics dp = getResources().getDisplayMetrics();
                int nHeight = dp.heightPixels;
                nScreenHeight = (int) ((float) nHeight / (float) 2.4);
                ScreenHeight = (int) ((float) nHeight / (float) 2.0);
                nnScreenHeight = (int) ((float) nHeight / (float) 1.4);
                gD.googleMapGeneral = null;

                locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
                isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (!gD.isConnectingToInternet()) {
                    Toast.makeText(Provider_CompleteRaisePage.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
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
                startService(new Intent(Provider_CompleteRaisePage.this, GPSTracker.class));
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



            } catch (JSONException e) {
                e.printStackTrace();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void reviewpop() {


        try {
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialogLayout = inflater.inflate(R.layout.provider_review_popup, null);

            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
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

            //str_des=et_reviews.getText().toString();
            JSONObject jsobj = null;
            try {
                jsobj = new JSONObject(strjsondatas);
                txt_username.setText(jsobj.getString("username"));
                str_id = jsobj.getString("userid");

                Log.e("userid", str_id);
                Log.e("username", jsobj.getString("username"));

                String strPath = jsobj.getString("image_path");

                Log.i("FF", "strimage" + strPath.length());

                if (strPath.length() == 0) {
                    user_image.setBackgroundResource(R.drawable.default_user_icon);
                } else {

                    String strimage = gD.common_baseurl+"upload/" + jsobj.getString("image_path");

                    imgLoader.DisplayImage(strimage, user_image);
                    //user_image.setImageBitmap(imgLoader.getBitmap(strimage));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            btn_submit_review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    str_rating = String.valueOf(ratingBar.getRating());
                    str_des = edit_review.getText().toString();


                    Log.i("HHB", "Rating : " + ratingBar.getRating());
                    if (!str_rating.equalsIgnoreCase("0.0") && str_des.length() > 0) {
                        ReviewUploadTask uploadTask = new ReviewUploadTask();
                        uploadTask.execute();

                    } else {
                        if (str_rating.equals("0.0")) {
                            Toast.makeText(Provider_CompleteRaisePage.this, "Rating should not be zero !", Toast.LENGTH_SHORT).show();
                        } else if (str_des.length() == 0) {
                            Toast.makeText(Provider_CompleteRaisePage.this, "Comment should not be empty !", Toast.LENGTH_SHORT).show();
                        }
                    }


                    // Toast.makeText(Provider_Pay.this, " "+str_rating+" "+str_id+"- "+str_des, Toast.LENGTH_SHORT).show();


                }
            });


            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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

                    tv_alert_Message.setText("Do u proceed further without giving review .Do you Wish to proceed further?");

                    Button btn_submit = (Button) dialogLayout.findViewById(R.id.ok);
                    Button btn_cancel = (Button) dialogLayout.findViewById(R.id.cancel);


                    FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ScreenHeight);
                    llayAlert.setLayoutParams(lparams);


                    btn_submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i = new Intent(Provider_CompleteRaisePage.this, ProviderHistoryPage.class);
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

    @Override
    protected void onResume() {
        try {
            Log.i("KV", "" + Provider_CompleteRaisePage.sharedPreferences.getInt("isGenerate", 0));
            if (Provider_CompleteRaisePage.sharedPreferences.getInt("isGenerate", 0) == 1) {
                raiseinvoise.setVisibility(View.GONE);
                complete.setEnabled(true);

            }

            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
            super.onResume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }









    public void custompopup() {


        try {
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

            tv_alert_Message.setText("User still has not completed the payment .Do you Wish to proceed further?");

            Button btn_submit = (Button) dialogLayout.findViewById(R.id.ok);
            Button btn_cancel = (Button) dialogLayout.findViewById(R.id.cancel);


            FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ScreenHeight);
            llayAlert.setLayoutParams(lparams);


            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent startAct = new Intent(context, Provider_DashBoard.class);
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

    @Override
    public void onBackPressed() {

        Intent i = new Intent(Provider_CompleteRaisePage.this, ProviderAppointment.class);
        startActivity(i);
        finish();
        ;

    }




    //checking generate page

    class ReviewUploadTask extends AsyncTask {

        String sResponse = null;


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
                multipart.addFormField("id", id);
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
            Provider_CompleteRaisePage.this.runOnUiThread(new Runnable() {
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

                        tv_alert_Message.setText("Review posted successfully");

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
                                Intent nextScreenIntent = new Intent(Provider_CompleteRaisePage.this, ProviderHistoryPage.class);
                                startActivity(nextScreenIntent);


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

    private class CompleteTask extends AsyncTask {
        String sResponse = null;
        String str_view_id, str_provide_id;


        @Override
        protected Object doInBackground(Object[] objects) {
            Log.i("HH", "Else.....");
            String charset = "UTF-8";

            try {

                String requestURL = gD.common_baseurl+"acceptappointment.php?id=" + URLEncoder.encode(id, "utf-8") + "&complete=" + URLEncoder.encode("1", "utf-8");




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
            gD.callload(context, nScreenHeight);



        }

        @Override
        protected void onPostExecute(Object sesponse) {
            try {
                if (sResponse != null) {
                    gD.callload(context, nScreenHeight);



                    JSONObject jsobj = new JSONObject(sResponse);
                    if (jsobj.getInt("code") == 2) {

                        reviewpop();

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
    class RaiseInvoiceTask extends AsyncTask {

        String user_id = null;
        String sResponse = null;
        Context cont;
        String status;

        public RaiseInvoiceTask(Context context, String userid) {
            this.user_id = userid;
            this.cont = context;
            this.status = status;
        }

        @Override
        protected void onPreExecute() {
            Provider_CompleteRaisePage.this.runOnUiThread(new Runnable() {
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


                String requestURL = gD.common_baseurl+"generateinvoice.php?id=" + user_id;

                //   APIcalls AceptApi = new APIcalls(requestURL);
                // AceptApi.Process();


                try {

                    URL url;
                    HttpURLConnection urlConnection = null;
                    try {
                        Log.i("SR", "Login URL : " + requestURL.trim());
                        System.out.print("strHTTP_LOGIN_URL");
                        url = new URL(requestURL.trim());

                  //      urlConnection.setRequestProperty("Accept-Encoding", "gzip");
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
                        alertDialogBuilder.setCancelable(true);


                        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                        //  alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        alertDialog.show();

                        LinearLayout llayAlert = (LinearLayout) dialogLayout.findViewById(R.id.llayalertDialog);
                        TextView tv_alert_Title = (TextView) dialogLayout.findViewById(R.id.add_msg);
                        TextView tv_alert_Message = (TextView) dialogLayout.findViewById(R.id.message);

                        tv_alert_Message.setText("Time to get paid");


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
                                Intent voicpage = new Intent(Provider_CompleteRaisePage.this, Provider_Generate.class);

                                // voicpage.putExtra("providerId", pId);
                                voicpage.putExtra("sR", strjsondatas);

                                voicpage.putExtra("type", String.valueOf(1));


                                startActivity(voicpage);

                            }
                        });







                    } else if (jsobj.getInt("code") == 4) {

                        gD.showAlertDialog(context, "", "Client did not confirm completion", nScreenHeight, 1);

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
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }



    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
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

                      /*      lay_clickaddress.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    String uri = "http://maps.google.com/maps?saddr=" + location.getLatitude()+","+location.getLongitude()+"&daddr="+latt+","+lng+"&mode=driving";

                                    // String uri = "http://maps.google.com/maps?saddr=" + latt+","+lng+"&daddr="+location.getLatitude()+","+location.getLongitude()+"&mode=driving";
                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                                    startActivity(intent);
                                }
                            });*/

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
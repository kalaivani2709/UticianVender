package com.aryvart.uticianvender.provider;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.genericclasses.AlertDialogManager;



/**
 * Created by Admin on 24-03-2016.
 */
public class GPSTracker_Provider extends Service implements LocationListener {

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 2; // 2 meters
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    public static boolean isGPSEnabled = false;
    public static boolean isNetworkEnabled = false;
    static boolean canGetLocation = false;

    protected LocationManager locationManager;
    Location location; // location
    double latitude; // latitude
    double longitude; // longitude
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    AlertDialogManager alert = new AlertDialogManager();
    ProgressDialog dialog;


    private static final String TAG = "BroadcastLocation";
    public static final String BROADCAST_ACTION = "com.aryvart.utician.locationupdate";
    private final Handler handler = new Handler();
    Intent intent;
    int counter = 0;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @SuppressWarnings("static-access")
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("onCreate", "Success !");
        Log.i("GPS", "onCreate process ");

        intent = new Intent(BROADCAST_ACTION);

    }

    private void CheckEnableGPS() {
        String provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.equals("")) {
            //GPS Enabled
            Toast.makeText(getApplication
                            (), "GPS Enabled: " + provider,
                    Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            startActivity(intent);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            handler.removeCallbacks(sendUpdatesToUI);
            handler.postDelayed(sendUpdatesToUI, 1000); // 1 second

            Log.i("GTP", TAG + "Service Called..........");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            latitudeAndLongtitude();
            handler.postDelayed(this, 10000); // 10 seconds
        }
    };

    public void locationsearch(Location location) {
        Log.i("GPS", "locationsearch process ");

        if (location != null) {
            //  Toast.makeText(getApplicationContext(), "isGPSEnabled!", Toast.LENGTH_SHORT).show();
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.e("GPS", String.valueOf(latitude));
            Log.e("GPS", String.valueOf(longitude));

            intent.putExtra("lat", String.valueOf(location.getLatitude()));
            intent.putExtra("long", String.valueOf(location.getLongitude()));
            sendBroadcast(intent);
            Log.i("GPS", TAG + "Value Sent to Activity..........");


            saveLatLong(latitude, longitude);


        }
    }



    public void latitudeAndLongtitude() {
        try {
            Log.i("GPS", "latitudeAndLongtitude process ");
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {
                Log.i("GPS", "latitudeAndLongtitude process IF");
                Toast.makeText(getApplicationContext(), "No provider found!", Toast.LENGTH_SHORT).show();
            } else {
                Log.i("GPS", "latitudeAndLongtitude process ELSE");
                GPSTracker_Provider.canGetLocation = true;
                if (isGPSEnabled) {
                    Log.i("GPS", "latitudeAndLongtitude process isGPSEnabled if");
                    if (location == null) {
                        Log.i("GPS", "latitudeAndLongtitude process isGPSEnabled if location 1");
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("Network", "Network");
                        if (locationManager != null) {
                            Log.i("GPS", "latitudeAndLongtitude process isGPSEnabled if location 2");
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            locationsearch(location);
                        }
                    } else {
                        Log.i("GPS", "latitudeAndLongtitude process isGPSEnabled if else ");
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        locationsearch(location);
                    }
                } else if (isNetworkEnabled) {
                    Log.i("GPS", "latitudeAndLongtitude process isNetworkEnabled if");
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        Log.i("GPS", "latitudeAndLongtitude process isNetworkEnabled if 1");
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        locationsearch(location);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void saveLatLong(Double latitude, Double longitude) {
        Log.i("GPS", "saveLatLong process ");

        sharedPreferences = getSharedPreferences("myprefer", MODE_PRIVATE);
        editor = sharedPreferences.edit();


        editor.putString("Lat", String.valueOf(latitude));
        Log.e("Latitude->", String.valueOf(latitude));
        editor.putString("Long", String.valueOf(longitude));
        Log.e("Longitude->", String.valueOf(longitude));
        editor.commit();


    }

    public Location getLocation() {
        return location;
    }


    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        // return longitude
        return longitude;
    }

    public void sendLocationToServer() {
        Log.i("sendLocationToServer", "Called!");

    }


    public void stopUsingGPS() {
        if (locationManager != null) {
            Log.i("GPS", "stopUsingGPS process ");
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(GPSTracker_Provider.this);
        }
    }


    @Override
    public void onDestroy() {
        Log.d("TT", "onDestroy Called");
        Log.i("GPS", "onDestroy process ");
        stopUsingGPS();
        super.onDestroy();
    }

    @SuppressWarnings("static-access")
    public boolean canGetLocation() {
        return this.canGetLocation;
    }


    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }


}
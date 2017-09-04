package com.aryvart.uticianvender.user;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Admin on 24-03-2016.
 */
public class GPSTracker extends Service implements LocationListener {

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

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @SuppressWarnings("static-access")
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("onCreate", "Success !");
        latitudeAndLongtitude();
    }

    private void CheckEnableGPS() {
        String provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if (!provider.equals("")) {
            //GPS Enabled
            //  Toast.makeText(getApplicationContext(), "GPS Enabled: " + provider,
            // Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            startActivity(intent);
        }

    }

    public void latitudeAndLongtitude() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!isGPSEnabled && !isNetworkEnabled) {

                //showGPSDisabledAlertToUser();

                Toast.makeText(getApplicationContext(), "No provider found!", Toast.LENGTH_SHORT).show();
            } else {
                GPSTracker.canGetLocation = true;
                if (isGPSEnabled) {
                    if (location == null) {
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
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("Network", "GPS");
                        if (locationManager != null) {
                            Log.d("locationManager", "is not null ");
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                Log.d("Network", "GPS lat and long gets ");
                                // Toast.makeText(getApplicationContext(), "isNetworkEnabled!", Toast.LENGTH_SHORT).show();
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                Log.e("LatiGN", String.valueOf(latitude));
                                Log.e("LongiGN", String.valueOf(longitude));
                                saveLatLong(latitude, longitude);
                           /* Helper.savePreferences(getApplicationContext(), NameConversion.LATITUDE, String.valueOf(latitude));
                            Helper.savePreferences(getApplicationContext(), NameConversion.LONGITUDE, String.valueOf(longitude));*/
                            } else {
                                Log.d("location", "is getting null ");
                            }
                        }
                    }
                } else if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            //     Toast.makeText(getApplicationContext(), "isGPSEnabled!", Toast.LENGTH_SHORT).show();
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            Log.e("LatiG", String.valueOf(latitude));
                            Log.e("LongiG", String.valueOf(longitude));
                            saveLatLong(latitude, longitude);


                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void saveLatLong(Double latitude, Double longitude) {

        try {
            sharedPreferences = getSharedPreferences("myprefer", MODE_PRIVATE);
            editor = sharedPreferences.edit();


            editor.putString("Lat", String.valueOf(latitude));
            Log.e("Latitude->", String.valueOf(latitude));
            editor.putString("Long", String.valueOf(longitude));
            Log.e("Longitude->", String.valueOf(longitude));
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    @Override
    public void onDestroy() {
        Log.d("TT", "onDestroy Called");
        stopUsingGPS();
        //stopSelf();

        super.onDestroy();
    }


    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */
    @SuppressWarnings("static-access")
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     */


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
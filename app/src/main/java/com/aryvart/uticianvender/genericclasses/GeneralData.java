package com.aryvart.uticianvender.genericclasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aryvart.uticianvender.R;
import com.google.android.gms.maps.GoogleMap;

import org.json.JSONObject;

public class GeneralData {
    public static GoogleMap googleMapGeneral;


    public static String strAddress;
    public static String strLatitutde;
    public static String strLongitude;

    public static JSONObject jsObjAds;
    public AlertDialog   alertDialog;

    public  static   int screenback=0;

    public static int ncount=0;

    public static GoogleMap prov_googleMapGeneral;
    public static String pro_searchAddress;
    public static String pro_searchLatitutde;
    public static String pro_searchLongitude;

    public static Context context;
    public static CoordinatorLayout coordinatorLayout;
    public SharedPreferences prefs;
    public static String common_baseurl="http://utician.com/api/";

    public GeneralData(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences("back_id", Context.MODE_PRIVATE);

        coordinatorLayout = (CoordinatorLayout) ((Activity) context).findViewById(R.id.coordinatorlayout);

    }

    public GeneralData() {

    }

    public static void showAlertDialogNet(final Context context, String strMessage) {

        try {
            if(!((Activity) context).isFinishing())
            {
                try {

                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                    builder.setMessage(strMessage);
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((Activity) context).finish();
                        }
                    });/*.setNegativeButt0on("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ( (Activity)context).finish();
                    }
                });*/

                    final android.support.v7.app.AlertDialog altDialog = builder.create();
                    altDialog.show();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isValidEmail(CharSequence target) {
        boolean isvalid = false;
        try {
            if (target == null) {
                return isvalid;
            } else {
                isvalid = android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isvalid;
    }

    /**
     * Show Alert Dialog based on width and height of the Screen resolution
     *
     * @param context
     * @param strTitle
     * @param strMessage
     * @param nScreenHeight ---Calculate the Screen height and With based on the Display metrics
     */
    public void showAlertDialog(final Context context, String strTitle, String strMessage, int nScreenHeight, int count) {

        try {
            if(!((Activity) context).isFinishing())
            {
                try {
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View dialogLayout = inflater.inflate(R.layout.layout_confirmation, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                    alertDialogBuilder.setView(dialogLayout);
                    alertDialogBuilder.setCancelable(false);


                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    //  alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    alertDialog.show();

                    LinearLayout llayAlert = (LinearLayout) dialogLayout.findViewById(R.id.llayalertDialog);
                    // TextView tv_alert_Title = (TextView) dialogLayout.findViewById(R.id.tv_alert_Title);
                    TextView tv_alert_Message = (TextView) dialogLayout.findViewById(R.id.message);

                    //tv_alert_Title.setText(strTitle);
                    tv_alert_Message.setText(strMessage);

                    Button btn_submit = (Button) dialogLayout.findViewById(R.id.ok);
                    Button btn_cancel = (Button) dialogLayout.findViewById(R.id.cancel);


                    FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, nScreenHeight);
                    llayAlert.setLayoutParams(lparams);


                    if (count == 1) {
                        btn_cancel.setVisibility(View.GONE);
                    }


                    btn_submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public  void callload(final Context context, int nScreenHeight)
    {


        try {
            if(!((Activity) context).isFinishing())
            {
                //show dialog
                View itemView1;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(false);
                itemView1 = LayoutInflater.from(context)
                        .inflate(R.layout.splash, null);
                alertDialog = builder.create();
                alertDialog.setView(itemView1);
                alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                alertDialog.show();





                final AnimationDrawable Anim;
                ImageView iv1 = (ImageView)itemView1. findViewById(R.id.imageView1);
                try {
                    BitmapDrawable frame1 = (BitmapDrawable) context.getResources().getDrawable(
                            R.drawable.m01);
                    BitmapDrawable frame2 = (BitmapDrawable)context. getResources().getDrawable(
                            R.drawable.m02);
                    BitmapDrawable frame3 = (BitmapDrawable) context.getResources().getDrawable(
                            R.drawable.m03);
                    BitmapDrawable frame4 = (BitmapDrawable) context.getResources().getDrawable(
                            R.drawable.m04);
                    BitmapDrawable frame5 = (BitmapDrawable) context.getResources().getDrawable(
                            R.drawable.m05);
                    BitmapDrawable frame6 = (BitmapDrawable) context.getResources().getDrawable(
                            R.drawable.m06);
                    BitmapDrawable frame7 = (BitmapDrawable)context. getResources().getDrawable(
                            R.drawable.m07);
                    BitmapDrawable frame8 = (BitmapDrawable) context.getResources().getDrawable(
                            R.drawable.m08);
                    BitmapDrawable frame9 = (BitmapDrawable)context. getResources().getDrawable(
                            R.drawable.m09);
                    BitmapDrawable frame10 = (BitmapDrawable) context.getResources().getDrawable(
                            R.drawable.m10);

                    BitmapDrawable frame11 = (BitmapDrawable)context. getResources().getDrawable(
                            R.drawable.m11);
                    BitmapDrawable frame12 = (BitmapDrawable)context. getResources().getDrawable(
                            R.drawable.m12);
                    BitmapDrawable frame13 = (BitmapDrawable)context. getResources().getDrawable(
                            R.drawable.m13);
                    BitmapDrawable frame14 = (BitmapDrawable) context.getResources().getDrawable(
                            R.drawable.m14);
                    BitmapDrawable frame15 = (BitmapDrawable)context. getResources().getDrawable(
                            R.drawable.m15);
                    BitmapDrawable frame16 = (BitmapDrawable)context. getResources().getDrawable(
                            R.drawable.m16);
                    BitmapDrawable frame17 = (BitmapDrawable)context. getResources().getDrawable(
                            R.drawable.m17);
                    BitmapDrawable frame18 = (BitmapDrawable) context.getResources().getDrawable(
                            R.drawable.m18);
                    BitmapDrawable frame19 = (BitmapDrawable)context. getResources().getDrawable(
                            R.drawable.m19);
                    BitmapDrawable frame20 = (BitmapDrawable)context. getResources().getDrawable(
                            R.drawable.m20);
                    Anim = new AnimationDrawable();
                    Anim.addFrame(frame1, 200);
                    Anim.addFrame(frame2, 200);
                    Anim.addFrame(frame3, 200);
                    Anim.addFrame(frame4, 200);
                    Anim.addFrame(frame5, 200);
                    Anim.addFrame(frame6, 200);
                    Anim.addFrame(frame7, 200);
                    Anim.addFrame(frame8, 200);
                    Anim.addFrame(frame9, 200);
                    Anim.addFrame(frame10, 200);

                    Anim.addFrame(frame11, 200);
                    Anim.addFrame(frame12, 200);
                    Anim.addFrame(frame13, 200);
                    Anim.addFrame(frame14, 200);
                    Anim.addFrame(frame15, 200);
                    Anim.addFrame(frame16, 200);
                    Anim.addFrame(frame17, 200);
                    Anim.addFrame(frame18, 200);
                    Anim.addFrame(frame19, 200);
                    Anim.addFrame(frame20, 200);
                    Anim.setOneShot(false);
                    iv1.setBackgroundDrawable(Anim);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {

                        public void run() {

                            Anim.start();

                        }
                    }, 1000);

                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public boolean isConnectingToInternet() {
        boolean isConnected = false;
        try {
            isConnected = false;


            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Network[] networks = connectivityManager.getAllNetworks();
                NetworkInfo networkInfo;
                for (Network mNetwork : networks) {
                    networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                    if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                        isConnected = true;
                    }
                }
            } else {
                if (connectivityManager != null) {
                    //noinspection deprecation
                    NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                    if (info != null) {
                        for (NetworkInfo anInfo : info) {
                            if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                                Log.d("Network", "NETWORKNAME: " + anInfo.getTypeName());
                                isConnected = true;
                            }
                        }
                    }
                }
            }

            Log.i("HHJ", "IsConected : " + isConnected);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return isConnected;
    }
}

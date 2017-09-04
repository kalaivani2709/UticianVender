package com.aryvart.uticianvender.newGCM;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.admin.AdminList;
import com.aryvart.uticianvender.admin.AdminNotification;
import com.aryvart.uticianvender.genericclasses.AlertDialogManager;
import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.aryvart.uticianvender.imageCache.ImageLoader;
import com.aryvart.uticianvender.provider.ProviderNotification;
import com.aryvart.uticianvender.provider.Provider_DashBoard;
import com.aryvart.uticianvender.utician.SplashActivity;
import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by kundan on 10/22/2015.
 */
public class PushNotificationService extends GcmListenerService {

    AlertDialogManager alert = new AlertDialogManager();
    private NotificationUtils notificationUtils;
    AlertDialog alertDialog = null;
    RatingBar ratingBar;
    Button btn_submit_review;
    ImageView user_image;
    TextView txt_username;
    EditText edit_review;
    ImageView close;
    GeneralData gD;
    String sname, simage, sproid;
    ImageLoader imgLoader;
    int nScreenHeight, ScreenHeight, mScreenHeight, lScreenHeight;
    String str_Rowid, str_providerid, str_proivdername, Str_userid, str_providerimg, str_des, str_rating;

    @Override
    public void onMessageReceived(String from, Bundle data) {


        String message_json = data.getString("message");
        imgLoader = new ImageLoader(GeneralData.context);
        DisplayMetrics dp = getResources().getDisplayMetrics();
        int nHeight = dp.heightPixels;
        nScreenHeight = (int) ((float) nHeight / (float) 1.3);
        lScreenHeight = (int) ((float) nHeight / (float) 1.5);
        mScreenHeight = (int) ((float) nHeight / (float) 1.9);
        ScreenHeight = (int) ((float) nHeight / (float) 2.0);
        JSONObject jsobj = null;
        try {

            jsobj = new JSONObject(message_json);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        //createNotification(mTitle, push_msg);
        Log.i("MSG", "Message : " + message_json + "Data : " + data);
        //  Log.i("KV", "response : " + response );
        processUserMessage("", jsobj.toString());
    }

    /**
     * Processing user specific push message
     * It will be displayed with / without image in push notification tray
     */
    private void processUserMessage(String title, final String data) {

        imgLoader = new ImageLoader(GeneralData.context);
        try {
            JSONObject datObj = new JSONObject(data);


            gD = new GeneralData(GeneralData.context);
            String strtype = datObj.getString("type");
            final String rowid = datObj.getString("bid");
            final String userid = datObj.getString("userid");
            final String providerid = datObj.getString("providerid");
            final String providername = datObj.getString("providername");
            final String providerimg = datObj.getString("providerimage");
            final String service = datObj.getString("service");
            final String rate = datObj.getString("rate");


            String pmessage1 = datObj.getString("message1").toString().trim();
            String message2 = datObj.getString("onscreenmessage").toString().trim();


            Log.i("GCM", "rowid  ## " + rowid);
            Log.i("GCM", "userid ##" + userid);

            Log.i("GCM", "pmessage1  ## " + pmessage1);
            Log.i("GCM", "onscreenmessage ##" + message2);

            Log.i("GCM", "providerimg  ## " + providerimg);

       /*     JSONObject mObj = datObj.getJSONObject("message");
            Message message = new Message();
            message.setMessage(mObj.getString("message"));
            message.setId(mObj.getString("message_id"));
            message.setCreatedAt(mObj.getString("created_at"));

            JSONObject uObj = datObj.getJSONObject("user");
            User user = new User();
            user.setId(uObj.getString("user_id"));
            user.setEmail(uObj.getString("email"));
            user.setName(uObj.getString("name"));
            message.setUser(user);*/

            // verifying whether the app is in background or foreground
            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {

                // app is in foreground, broadcast the push message
              /*  Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("type", Config.PUSH_TYPE_USER);
                pushNotification.putExtra("message", data);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);*/

                // play notification sound
                //NotificationUtils notificationUtils = new NotificationUtils();
                //  notificationUtils.playNotificationSound();

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());


                SplashActivity.sharedPreferences = getSharedPreferences("regid", Context.MODE_PRIVATE);

                // String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

                Intent resultIntent = null;
      if (SplashActivity.sharedPreferences.getString("utype", null).equalsIgnoreCase("provider")) {
                    resultIntent = new Intent(getApplicationContext(), ProviderNotification.class);
                    MyPreferenceManager pref = new MyPreferenceManager(SplashActivity.context);
                    pref.clear();
                    if (GeneralData.context instanceof Provider_DashBoard) {
                        Log.i("KS", "Hi Am kalaivani : ");

                        Intent resultIntenti = new Intent(this, Provider_DashBoard.class);
                        resultIntenti.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ((Provider_DashBoard) GeneralData.context).startActivity(resultIntenti);

                    }


                } else if (SplashActivity.sharedPreferences.getString("utype", null).equalsIgnoreCase("admin")) {
                    resultIntent = new Intent(getApplicationContext(), AdminNotification.class);
                    MyPreferenceManager pref = new MyPreferenceManager(SplashActivity.context);
                    pref.clear();
                    if (GeneralData.context instanceof AdminList) {
                        Log.i("KS", "Hi Am kalaivani : admin ");

                        Intent resultIntenti = new Intent(this, AdminList.class);
                        resultIntenti.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        ((AdminList) GeneralData.context).startActivity(resultIntenti);

                    }


                } else {
                    resultIntent = new Intent(getApplicationContext(), AdminNotification.class);
                    MyPreferenceManager pref = new MyPreferenceManager(SplashActivity.context);
                    pref.clear();
                }

                if (resultIntent != null) {
                    showNotificationMessage(getApplicationContext(), title, pmessage1, timeStamp, resultIntent);
                }

                if (strtype.equalsIgnoreCase("accepted")) {
                    //show dialog
                    final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme));

                    LayoutInflater inflater = (LayoutInflater) GeneralData.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View viewScheduleDialog = inflater.inflate(R.layout.accept_onscreen_popup, null);

                    TextView tvMessage = (TextView) viewScheduleDialog.findViewById(R.id.message);
                    final Button btnOk = (Button) viewScheduleDialog.findViewById(R.id.ok);
                    final Button btnCancel = (Button) viewScheduleDialog.findViewById(R.id.cancel);

                    builder.setCancelable(false);
                    builder.setView(viewScheduleDialog);


                    tvMessage.setText(message2);


                    try {
                        new Thread() {
                            @Override
                            public void run() {

                                Looper.prepare();
                                alertDialog = builder.create();

                                btnOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();


                                    }
                                });


                                alertDialog.getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
                                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
                                alertDialog.show();
                                Looper.loop();

                            }
                        }.start();
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                } else if (strtype.equalsIgnoreCase("Welcome to Utician,your account is now active!")) {
                    //show dialog
                    final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme));

                    LayoutInflater inflater = (LayoutInflater) GeneralData.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View viewScheduleDialog = inflater.inflate(R.layout.welc_utiican, null);

                    TextView tvMessage = (TextView) viewScheduleDialog.findViewById(R.id.message);
                    final Button btnOk = (Button) viewScheduleDialog.findViewById(R.id.ok);
                    final Button btnCancel = (Button) viewScheduleDialog.findViewById(R.id.cancel);

                    builder.setCancelable(false);
                    builder.setView(viewScheduleDialog);


                    try {
                        new Thread() {
                            @Override
                            public void run() {

                                Looper.prepare();
                                alertDialog = builder.create();

                                btnOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        alertDialog.dismiss();


                                    }
                                });


                                alertDialog.getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
                                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
                                alertDialog.show();
                                Looper.loop();

                            }
                        }.start();
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                } else if (strtype.equalsIgnoreCase("reach the destination")) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme));

                    LayoutInflater inflater = (LayoutInflater) GeneralData.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View viewScheduleDialog = inflater.inflate(R.layout.layout_confirmation, null);

                    TextView tvMessage = (TextView) viewScheduleDialog.findViewById(R.id.message);
                    final Button btnOk = (Button) viewScheduleDialog.findViewById(R.id.ok);
                    final Button btnCancel = (Button) viewScheduleDialog.findViewById(R.id.cancel);
                    btnOk.setText("Yes");
                    btnCancel.setText("No");
                    builder.setCancelable(false);
                    builder.setView(viewScheduleDialog);

                    tvMessage.setText(message2);


                    try {
                        new Thread() {
                            @Override
                            public void run() {

                                Looper.prepare();
                                alertDialog = builder.create();

                                btnOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //kalai
                                        //Do the Stuff what you want.......!!!
                                        //
                                        Log.i("PP", "hello ****");
                                        WebServerRegistrationTask webServer = new WebServerRegistrationTask(rowid);
                                        webServer.execute();

                                    }
                                });
                                btnCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //kalai
                                        //Do the Stuff what you want.......!!!
                                        //
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(GeneralData.context);

                                        LayoutInflater inflater = (LayoutInflater) GeneralData.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View viewScheduleDialog = inflater.inflate(R.layout.layout_confirmation, null);

                                        TextView tvMessage = (TextView) viewScheduleDialog.findViewById(R.id.message);
                                        final Button btnOk = (Button) viewScheduleDialog.findViewById(R.id.ok);
                                        final Button btnCancel = (Button) viewScheduleDialog.findViewById(R.id.cancel);

                                        builder.setCancelable(false);
                                        builder.setView(viewScheduleDialog);
                                        LinearLayout llayAlert = (LinearLayout) viewScheduleDialog.findViewById(R.id.llayalertDialog);

                                        FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, lScreenHeight);
                                        llayAlert.setLayoutParams(lparams);

                                        TextView add_msg = (TextView) viewScheduleDialog.findViewById(R.id.add_msg);
                                        add_msg.setVisibility(View.VISIBLE);


                                        add_msg.setText("(Menu->Notification->Action->Select Yes when Utician reach)");


                                        tvMessage.setText("Please confirm when your Utician has arrived");


                                        try {
                                            new Thread() {
                                                @Override
                                                public void run() {

                                                    Looper.prepare();
                                                    final AlertDialog alertDialog = builder.create();

                                                    int count = 1;
                                                    if (count == 1) {
                                                        btnCancel.setVisibility(View.GONE);
                                                    }


                                                    btnOk.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            //kalai
                                                            //Do the Stuff what you want.......!!!
                                                            //
                                                            alertDialog.dismiss();
                                                        }
                                                    });


                                                    alertDialog.getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
                                                    alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
                                                    alertDialog.show();
                                                    Looper.loop();

                                                }
                                            }.start();
                                        } catch (Exception e) {
                                            // TODO: handle exception
                                            e.printStackTrace();
                                        }


                                        alertDialog.dismiss();


                                    }
                                });

                                alertDialog.getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
                                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
                                alertDialog.show();
                                Looper.loop();

                            }
                        }.start();
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                } else if (strtype.equalsIgnoreCase("Service started")) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme));

                    LayoutInflater inflater = (LayoutInflater) GeneralData.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View viewScheduleDialog = inflater.inflate(R.layout.layout_confirmation, null);

                    TextView tvMessage = (TextView) viewScheduleDialog.findViewById(R.id.message);
                    final Button btnOk = (Button) viewScheduleDialog.findViewById(R.id.ok);
                    final Button btnCancel = (Button) viewScheduleDialog.findViewById(R.id.cancel);

                    builder.setCancelable(false);
                    builder.setView(viewScheduleDialog);
                    btnOk.setText("Yes");
                    btnCancel.setText("No");
                    tvMessage.setText(message2);


                    try {
                        new Thread() {
                            @Override
                            public void run() {

                                Looper.prepare();
                                alertDialog = builder.create();

                                btnOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //kalai
                                        //Do the Stuff what you want.......!!!
                                        //
                                        Log.i("PP", "hello");
                                        WebServerStartTask webServerStartTask = new WebServerStartTask(rowid, userid);
                                        webServerStartTask.execute();

                                    }
                                });
                                btnCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //kalai
                                        //Do the Stuff what you want.......!!!
                                        //


                                        final AlertDialog.Builder builder = new AlertDialog.Builder(GeneralData.context);

                                        LayoutInflater inflater = (LayoutInflater) GeneralData.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View viewScheduleDialog = inflater.inflate(R.layout.layout_confirmation, null);

                                        TextView tvMessage = (TextView) viewScheduleDialog.findViewById(R.id.message);
                                        final Button btnOk = (Button) viewScheduleDialog.findViewById(R.id.ok);
                                        final Button btnCancel = (Button) viewScheduleDialog.findViewById(R.id.cancel);

                                        builder.setCancelable(false);
                                        builder.setView(viewScheduleDialog);
                                        LinearLayout llayAlert = (LinearLayout) viewScheduleDialog.findViewById(R.id.llayalertDialog);

                                        FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, lScreenHeight);
                                        llayAlert.setLayoutParams(lparams);

                                        TextView add_msg = (TextView) viewScheduleDialog.findViewById(R.id.add_msg);
                                        add_msg.setVisibility(View.VISIBLE);


                                        add_msg.setText("(Menu->Notification->Action->Select Yes when services are start)");


                                        tvMessage.setText("Please confirm when your Utician has begin the service");


                                        try {
                                            new Thread() {
                                                @Override
                                                public void run() {

                                                    Looper.prepare();
                                                    final AlertDialog alertDialog = builder.create();

                                                    int count = 1;
                                                    if (count == 1) {
                                                        btnCancel.setVisibility(View.GONE);
                                                    }


                                                    btnOk.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            //kalai
                                                            //Do the Stuff what you want.......!!!
                                                            //
                                                            alertDialog.dismiss();
                                                        }
                                                    });


                                                    alertDialog.getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
                                                    alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
                                                    alertDialog.show();
                                                    Looper.loop();

                                                }
                                            }.start();
                                        } catch (Exception e) {
                                            // TODO: handle exception
                                            e.printStackTrace();
                                        }


                                        alertDialog.dismiss();
                                    }
                                });

                                alertDialog.getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
                                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
                                alertDialog.show();
                                Looper.loop();

                            }
                        }.start();
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                } else if (strtype.equalsIgnoreCase("service ended")) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme));

                    LayoutInflater inflater = (LayoutInflater) GeneralData.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View viewScheduleDialog = inflater.inflate(R.layout.layout_confirmation, null);

                    TextView tvMessage = (TextView) viewScheduleDialog.findViewById(R.id.message);
                    final Button btnOk = (Button) viewScheduleDialog.findViewById(R.id.ok);
                    final Button btnCancel = (Button) viewScheduleDialog.findViewById(R.id.cancel);

                    builder.setCancelable(false);
                    builder.setView(viewScheduleDialog);
                    btnOk.setText("Yes");
                    btnCancel.setText("No");
                    tvMessage.setText(message2);


                    try {
                        new Thread() {
                            @Override
                            public void run() {

                                Looper.prepare();
                                alertDialog = builder.create();

                                btnOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //kalai
                                        //Do the Stuff what you want.......!!!
                                        //
                                        WebServerStopTask webServerstopTask = new WebServerStopTask(rowid, userid);
                                        webServerstopTask.execute();

                                    }
                                });
                                btnCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //kalai
                                        //Do the Stuff what you want.......!!!
                                        //


                                        final AlertDialog.Builder builder = new AlertDialog.Builder(GeneralData.context);

                                        LayoutInflater inflater = (LayoutInflater) GeneralData.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View viewScheduleDialog = inflater.inflate(R.layout.layout_confirmation, null);

                                        TextView tvMessage = (TextView) viewScheduleDialog.findViewById(R.id.message);
                                        TextView add_msg = (TextView) viewScheduleDialog.findViewById(R.id.add_msg);
                                        final Button btnOk = (Button) viewScheduleDialog.findViewById(R.id.ok);
                                        final Button btnCancel = (Button) viewScheduleDialog.findViewById(R.id.cancel);
                                        add_msg.setVisibility(View.VISIBLE);
                                        builder.setCancelable(false);
                                        builder.setView(viewScheduleDialog);

                                        LinearLayout llayAlert = (LinearLayout) viewScheduleDialog.findViewById(R.id.llayalertDialog);

                                        FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, lScreenHeight);
                                        llayAlert.setLayoutParams(lparams);
                                        tvMessage.setText("Please confirm the service is complete  when the Utician is done ");
                                        add_msg.setText("(Menu->Notification->Action->Select Yes when services are complete)");


                                        try {
                                            new Thread() {
                                                @Override
                                                public void run() {

                                                    Looper.prepare();
                                                    final AlertDialog alertDialog = builder.create();

                                                    int count = 1;
                                                    if (count == 1) {
                                                        btnCancel.setVisibility(View.GONE);
                                                    }


                                                    btnOk.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            //kalai
                                                            //Do the Stuff what you want.......!!!
                                                            //
                                                            alertDialog.dismiss();
                                                        }
                                                    });


                                                    alertDialog.getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
                                                    alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
                                                    alertDialog.show();
                                                    Looper.loop();

                                                }
                                            }.start();
                                        } catch (Exception e) {
                                            // TODO: handle exception
                                            e.printStackTrace();
                                        }

                                        alertDialog.dismiss();
                                    }
                                });

                                alertDialog.getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
                                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
                                alertDialog.show();
                                Looper.loop();

                            }
                        }.start();
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                } else if (strtype.equalsIgnoreCase("payment process invoice generated")) {

                    if (!((Activity) GeneralData.context).isFinishing()) {
                        //show dialog
                        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme));

                        LayoutInflater inflater = (LayoutInflater) GeneralData.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View viewScheduleDialog = inflater.inflate(R.layout.popup_msgall, null);

                        TextView tvMessage = (TextView) viewScheduleDialog.findViewById(R.id.message);
                        final Button btnOk = (Button) viewScheduleDialog.findViewById(R.id.ok);
                        final Button btnCancel = (Button) viewScheduleDialog.findViewById(R.id.cancel);

                        builder.setCancelable(false);
                        builder.setView(viewScheduleDialog);
                        btnOk.setText("Pay");

                        tvMessage.setText(message2);


                        try {
                            new Thread() {
                                @Override
                                public void run() {

                                    Looper.prepare();
                                    alertDialog = builder.create();

                                    btnOk.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            alertDialog.dismiss();

                                            LayoutInflater inflater = LayoutInflater.from(GeneralData.context);
                                            View dialogLayout = inflater.inflate(R.layout.pay_popup, null);

                                            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(GeneralData.context);
                                            alertDialogBuilder.setView(dialogLayout);
                                            alertDialogBuilder.setCancelable(false);


                                            final android.app.AlertDialog alertDialogg = alertDialogBuilder.create();
                                            //  alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                            alertDialogg.show();

                                            LinearLayout llayAlert = (LinearLayout) dialogLayout.findViewById(R.id.llayalertDialog);
                                            // TextView tv_alert_Title = (TextView) dialogLayout.findViewById(R.id.tv_alert_Title);
                                            TextView services = (TextView) dialogLayout.findViewById(R.id.services);
                                            final Button pay = (Button) dialogLayout.findViewById(R.id.pay);

                                            // final ImageView btn_close = (ImageView) dialogLayout.findViewById(R.id.close);

                                            TextView rates = (TextView) dialogLayout.findViewById(R.id.rate);
                                            TextView pname = (TextView) dialogLayout.findViewById(R.id.txt_providername);
                                            ImageView provider_image = (ImageView) dialogLayout.findViewById(R.id.providerimage);


                                            services.setText(service);
                                            String strimage = gD.common_baseurl + "upload/" + providerimg;
                                            Log.i("GCM", "providerimg  ## " + providerimg);
                                            if (providerimg.length() == 0) {
                                                provider_image.setBackgroundResource(R.drawable.default_user_icon);
                                            } else {


                                                imgLoader.DisplayImage(strimage, provider_image);

                                            }
                                            rates.setText("$" + rate);

                                            pname.setText(providername);

                                            FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, mScreenHeight);
                                            llayAlert.setLayoutParams(lparams);


                                            try {
                                                new Thread() {
                                                    @Override
                                                    public void run() {

                                                        Looper.prepare();

                                                        pay.setOnClickListener(new View.OnClickListener() {
                                                            @Override
                                                            public void onClick(View v) {

                                                                alertDialogg.dismiss();
                                                                WebServerPayTask webServerPayTask = new WebServerPayTask(rowid, userid, providerid, providername, providerimg);
                                                                webServerPayTask.execute();


                                                            }
                                                        });


                                                    }
                                                }.start();

                                            } catch (Exception e) {
                                                // TODO: handle exception
                                                e.printStackTrace();
                                            }


                                        }
                                    });


                                    alertDialog.getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
                                    alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
                                    alertDialog.show();
                                    Looper.loop();

                                }
                            }.start();
                        } catch (Exception e) {
                            // TODO: handle exception
                            e.printStackTrace();
                        }
                    }


                } else {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme));

                    LayoutInflater inflater = (LayoutInflater) GeneralData.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View viewScheduleDialog = inflater.inflate(R.layout.layout_confirmation, null);

                    TextView tvMessage = (TextView) viewScheduleDialog.findViewById(R.id.message);
                    final Button btnOk = (Button) viewScheduleDialog.findViewById(R.id.ok);
                    final Button btnCancel = (Button) viewScheduleDialog.findViewById(R.id.cancel);

                    builder.setCancelable(false);
                    builder.setView(viewScheduleDialog);

                    tvMessage.setText(message2);


                    try {
                        new Thread() {
                            @Override
                            public void run() {

                                Looper.prepare();
                                final AlertDialog alertDialog = builder.create();

                                int count = 1;
                                if (count == 1) {
                                    btnCancel.setVisibility(View.GONE);
                                }


                                btnOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //kalai
                                        //Do the Stuff what you want.......!!!
                                        //
                                        alertDialog.dismiss();
                                    }
                                });


                                alertDialog.getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
                                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
                                alertDialog.show();
                                Looper.loop();

                            }
                        }.start();
                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                }


            } else {

                // app is in background. show the message in notification try
                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);

                // check for push notification image attachment

               /* if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, user.getName() + " : " + message.getMessage(), message.getCreatedAt(), resultIntent);
                } else {
                    // push notification contains image
                    // show it with the image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message.getMessage(), message.getCreatedAt(), resultIntent, imageUrl);
                }*/
                String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

                showNotificationMessage(getApplicationContext(), title, pmessage1, timeStamp, resultIntent);
            }
        } catch (Exception e) {
            Log.e("GCM", "json parsing error: " + e.getMessage());
//            Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {

        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);

    }


    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }


    public class WebServerRegistrationTask extends AsyncTask<Void, Void, String> {
        String strrowid;
        String response = "";

        public WebServerRegistrationTask(String rowid) {
            strrowid = rowid;
        }


        @Override
        protected String doInBackground(Void... params) {
            // SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PushNotificationService.this);

            Log.i("GCM", "jSucess **");
            URL url = null;
            try {
                url = new URL(Constants.WEB_SERVER_URL);
            } catch (MalformedURLException e) {
                e.printStackTrace();

                // sharedPreferences.edit().putString(Constants.PREF_GCM_REG_ID, "").apply();
            }
            Map<String, String> dataMap = new HashMap<String, String>();
            dataMap.put("id", strrowid);
            Log.i("GCM", "strrowid **" + strrowid);

            StringBuilder postBody = new StringBuilder();
            Iterator<Map.Entry<String, String>> iterator = dataMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, String> param = (Map.Entry<String, String>) iterator.next();
                postBody.append(param.getKey()).append('=')
                        .append(param.getValue());
                if (iterator.hasNext()) {
                    postBody.append('&');
                }
            }
            String body = postBody.toString();
            byte[] bytes = body.getBytes();

            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setFixedLengthStreamingMode(bytes.length);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded;charset=UTF-8");

                OutputStream out = conn.getOutputStream();
                out.write(bytes);
                out.close();
                StringBuffer sb = new StringBuffer();
                InputStream is = null;
                try {
                    is = conn.getInputStream();
                    int ch;

                    while ((ch = is.read()) != -1) {
                        sb.append((char) ch);
                    }
                    response = sb.toString();

                } catch (IOException e) {
                    throw e;
                }

                try {

                    JSONObject jsonObj = new JSONObject(sb.toString());

                    Log.i("DDD", "StrResp : " + jsonObj.toString());

                    response = jsonObj.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (is != null) {
                        is.close();
                    }
                }
                int status = conn.getResponseCode();
                if (status == 200) {
                    if (response.equals("1")) {


                        // ((Provider_DashBoard) GeneralData.context).finish();

                        // sharedPreferences.edit().putString(Constants.PREF_GCM_REG_ID, (Provider_DashBoard) GeneralData.context).apply();
                        Intent registrationComplete = new Intent(Constants.SERVER_SUCCESS);
                        LocalBroadcastManager.getInstance(PushNotificationService.this).sendBroadcast(registrationComplete);
                    }
                } else {
                    throw new IOException("Request failed with error code "
                            + status);
                }
            } catch (ProtocolException pe) {
                pe.printStackTrace();
                //sharedPreferences.edit().putString(Constants.PREF_GCM_REG_ID, "").apply();

            } catch (IOException io) {
                io.printStackTrace();
                //    sharedPreferences.edit().putString(Constants.PREF_GCM_REG_ID, "").apply();

            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            return response;
        }

        protected void onPostExecute(String result) {
            try {

                JSONObject jsobj = new JSONObject(result);


                if (jsobj.getString("code").equalsIgnoreCase("2")) {


                    Log.i("GCM", "jSucess ");

                    final AlertDialog.Builder builder = new AlertDialog.Builder(GeneralData.context);

                    LayoutInflater inflater = (LayoutInflater) GeneralData.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View viewScheduleDialog = inflater.inflate(R.layout.popup_msgall, null);

                    TextView tvMessage = (TextView) viewScheduleDialog.findViewById(R.id.message);
                    final Button btnOk = (Button) viewScheduleDialog.findViewById(R.id.ok);
                    final Button btnCancel = (Button) viewScheduleDialog.findViewById(R.id.cancel);

                    builder.setCancelable(false);
                    builder.setView(viewScheduleDialog);

                    tvMessage.setText("Your Utician has arrived for your appointment");


                    try {
                        new Thread() {
                            @Override
                            public void run() {

                                Looper.prepare();
                                final AlertDialog alertDialog = builder.create();

                                btnOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //kalai
                                        //Do the Stuff what you want.......!!!
                                        //
                                        alertDialog.dismiss();
                                    }
                                });


                                alertDialog.getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
                                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
                                alertDialog.show();
                                Looper.loop();

                            }
                        }.start();

                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }

                }
                alertDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }

    public class WebServerStartTask extends AsyncTask<Void, Void, String> {
        String strrowid, struserid;
        String response = "";

        public WebServerStartTask(String rowid, String userid) {
            struserid = userid;
            strrowid = rowid;
        }


        @Override
        protected String doInBackground(Void... params) {
            // SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PushNotificationService.this);

            Log.i("GCM", "jSucess **");
            URL url = null;
            String requestURL = gD.common_baseurl + "startservice.php";
            try {
                url = new URL(requestURL);
            } catch (MalformedURLException e) {
                e.printStackTrace();

                // sharedPreferences.edit().putString(Constants.PREF_GCM_REG_ID, "").apply();
            }
            Map<String, String> dataMap = new HashMap<String, String>();
            dataMap.put("id", strrowid);
            dataMap.put("userid", struserid);
            dataMap.put("start", "1");

            Log.i("GCM", "strrowid **" + strrowid);
            Log.i("GCM", "userid **" + struserid);
            Log.i("GCM", "strrowid **" + strrowid);
            StringBuilder postBody = new StringBuilder();
            Iterator<Map.Entry<String, String>> iterator = dataMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, String> param = (Map.Entry<String, String>) iterator.next();
                postBody.append(param.getKey()).append('=')
                        .append(param.getValue());
                if (iterator.hasNext()) {
                    postBody.append('&');
                }
            }
            String body = postBody.toString();
            byte[] bytes = body.getBytes();

            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setFixedLengthStreamingMode(bytes.length);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded;charset=UTF-8");

                OutputStream out = conn.getOutputStream();
                out.write(bytes);
                out.close();
                StringBuffer sb = new StringBuffer();
                InputStream is = null;
                try {
                    is = conn.getInputStream();
                    int ch;

                    while ((ch = is.read()) != -1) {
                        sb.append((char) ch);
                    }
                    response = sb.toString();

                } catch (IOException e) {
                    throw e;
                }

                try {

                    JSONObject jsonObj = new JSONObject(sb.toString());

                    Log.i("DDD", "StrResp : " + jsonObj.toString());

                    response = jsonObj.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (is != null) {
                        is.close();
                    }
                }
                int status = conn.getResponseCode();
                if (status == 200) {
                    if (response.equals("1")) {


                        // ((Provider_DashBoard) GeneralData.context).finish();

                        // sharedPreferences.edit().putString(Constants.PREF_GCM_REG_ID, (Provider_DashBoard) GeneralData.context).apply();
                        Intent registrationComplete = new Intent(Constants.SERVER_SUCCESS);
                        LocalBroadcastManager.getInstance(PushNotificationService.this).sendBroadcast(registrationComplete);
                    }
                } else {
                    throw new IOException("Request failed with error code "
                            + status);
                }
            } catch (ProtocolException pe) {
                pe.printStackTrace();
                //sharedPreferences.edit().putString(Constants.PREF_GCM_REG_ID, "").apply();

            } catch (IOException io) {
                io.printStackTrace();
                //    sharedPreferences.edit().putString(Constants.PREF_GCM_REG_ID, "").apply();

            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            return response;
        }

        protected void onPostExecute(String result) {
            try {

                JSONObject jsobj = new JSONObject(result);


                if (jsobj.getString("code").equalsIgnoreCase("3")) {


                    Log.i("GCM", "jSucess ");

                    final AlertDialog.Builder builder = new AlertDialog.Builder(GeneralData.context);

                    LayoutInflater inflater = (LayoutInflater) GeneralData.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View viewScheduleDialog = inflater.inflate(R.layout.popup_msgall, null);

                    TextView tvMessage = (TextView) viewScheduleDialog.findViewById(R.id.message);
                    final Button btnOk = (Button) viewScheduleDialog.findViewById(R.id.ok);
                    final Button btnCancel = (Button) viewScheduleDialog.findViewById(R.id.cancel);

                    builder.setCancelable(false);
                    builder.setView(viewScheduleDialog);

                    tvMessage.setText("Your service just begun");


                    try {
                        new Thread() {
                            @Override
                            public void run() {

                                Looper.prepare();
                                final AlertDialog alertDialog = builder.create();

                                btnOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //kalai
                                        //Do the Stuff what you want.......!!!
                                        //
                                        alertDialog.dismiss();
                                    }
                                });


                                alertDialog.getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
                                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
                                alertDialog.show();
                                Looper.loop();

                            }
                        }.start();

                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }

                }
                alertDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }


    public class WebServerStopTask extends AsyncTask<Void, Void, String> {
        String strrowid, struserid;
        String response = "";

        public WebServerStopTask(String rowid, String userid) {
            struserid = userid;
            strrowid = rowid;
        }


        @Override
        protected String doInBackground(Void... params) {
            // SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PushNotificationService.this);

            Log.i("GCM", "jSucess **");
            URL url = null;
            String requestURL = gD.common_baseurl + "startservice.php";
            try {
                url = new URL(requestURL);
            } catch (MalformedURLException e) {
                e.printStackTrace();

                // sharedPreferences.edit().putString(Constants.PREF_GCM_REG_ID, "").apply();
            }
            Map<String, String> dataMap = new HashMap<String, String>();
            dataMap.put("id", strrowid);
            dataMap.put("userid", struserid);
            dataMap.put("end", "1");


            Log.i("GCM", "userid **" + struserid);
            Log.i("GCM", "strrowid **" + strrowid);
            StringBuilder postBody = new StringBuilder();
            Iterator<Map.Entry<String, String>> iterator = dataMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, String> param = (Map.Entry<String, String>) iterator.next();
                postBody.append(param.getKey()).append('=')
                        .append(param.getValue());
                if (iterator.hasNext()) {
                    postBody.append('&');
                }
            }
            String body = postBody.toString();
            byte[] bytes = body.getBytes();

            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setFixedLengthStreamingMode(bytes.length);

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded;charset=UTF-8");

                OutputStream out = conn.getOutputStream();
                out.write(bytes);
                out.close();
                StringBuffer sb = new StringBuffer();
                InputStream is = null;
                try {
                    is = conn.getInputStream();
                    int ch;

                    while ((ch = is.read()) != -1) {
                        sb.append((char) ch);
                    }
                    response = sb.toString();

                } catch (IOException e) {
                    throw e;
                }

                try {

                    JSONObject jsonObj = new JSONObject(sb.toString());

                    Log.i("DDD", "StrResp : " + jsonObj.toString());

                    response = jsonObj.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (is != null) {
                        is.close();
                    }
                }
                int status = conn.getResponseCode();
                if (status == 200) {
                    if (response.equals("1")) {


                        // ((Provider_DashBoard) GeneralData.context).finish();

                        // sharedPreferences.edit().putString(Constants.PREF_GCM_REG_ID, (Provider_DashBoard) GeneralData.context).apply();
                        Intent registrationComplete = new Intent(Constants.SERVER_SUCCESS);
                        LocalBroadcastManager.getInstance(PushNotificationService.this).sendBroadcast(registrationComplete);
                    }
                } else {
                    throw new IOException("Request failed with error code "
                            + status);
                }
            } catch (ProtocolException pe) {
                pe.printStackTrace();
                //sharedPreferences.edit().putString(Constants.PREF_GCM_REG_ID, "").apply();

            } catch (IOException io) {
                io.printStackTrace();
                //    sharedPreferences.edit().putString(Constants.PREF_GCM_REG_ID, "").apply();

            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            return response;
        }

        protected void onPostExecute(String result) {
            try {

                JSONObject jsobj = new JSONObject(result);


                if (jsobj.getString("code").equalsIgnoreCase("3")) {


                    Log.i("GCM", "jSucess ");

                    final AlertDialog.Builder builder = new AlertDialog.Builder(GeneralData.context);

                    LayoutInflater inflater = (LayoutInflater) GeneralData.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View viewScheduleDialog = inflater.inflate(R.layout.popup_msgall, null);

                    TextView tvMessage = (TextView) viewScheduleDialog.findViewById(R.id.message);
                    final Button btnOk = (Button) viewScheduleDialog.findViewById(R.id.ok);
                    final Button btnCancel = (Button) viewScheduleDialog.findViewById(R.id.cancel);

                    builder.setCancelable(false);
                    builder.setView(viewScheduleDialog);

                    tvMessage.setText("Your service is complete");


                    try {
                        new Thread() {
                            @Override
                            public void run() {

                                Looper.prepare();
                                final AlertDialog alertDialog = builder.create();

                                btnOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //kalai
                                        //Do the Stuff what you want.......!!!
                                        //
                                        alertDialog.dismiss();
                                    }
                                });


                                alertDialog.getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
                                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
                                alertDialog.show();
                                Looper.loop();

                            }
                        }.start();

                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }

                }
                alertDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }

    public class WebServerPayTask extends AsyncTask<Void, Void, String> {
        String strrowid, struserid, strproviderid, strproviderimg, strprovidername;
        String response = "";

        public WebServerPayTask(String rowid, String userid, String providerid, String providername, String providerimg) {
            struserid = userid;
            strrowid = rowid;
            strproviderid = providerid;
            strprovidername = providername;
            strproviderimg = providerimg;
        }


        @Override
        protected String doInBackground(Void... params) {
            // SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PushNotificationService.this);

            Log.i("GCM", "jSucess **");
            URL url = null;
            String requestURL = gD.common_baseurl + "pay.php";
            try {
                url = new URL(requestURL);
            } catch (MalformedURLException e) {
                e.printStackTrace();

                // sharedPreferences.edit().putString(Constants.PREF_GCM_REG_ID, "").apply();
            }
            Map<String, String> dataMap = new HashMap<String, String>();
            dataMap.put("id", strrowid);


            Log.i("GCM", "strrowid **" + strrowid);
            StringBuilder postBody = new StringBuilder();
            Iterator<Map.Entry<String, String>> iterator = dataMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, String> param = (Map.Entry<String, String>) iterator.next();
                postBody.append(param.getKey()).append('=')
                        .append(param.getValue());
                if (iterator.hasNext()) {
                    postBody.append('&');
                }
            }
            String body = postBody.toString();
            byte[] bytes = body.getBytes();

            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setFixedLengthStreamingMode(bytes.length);
                conn.setRequestMethod("POST");

                conn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded;charset=UTF-8");

                OutputStream out = conn.getOutputStream();
                out.write(bytes);
                out.close();
                StringBuffer sb = new StringBuffer();
                InputStream is = null;
                try {
                    is = conn.getInputStream();
                    int ch;

                    while ((ch = is.read()) != -1) {
                        sb.append((char) ch);
                    }
                    response = sb.toString();

                } catch (IOException e) {
                    throw e;
                }

                try {

                    JSONObject jsonObj = new JSONObject(sb.toString());

                    Log.i("DDD", "StrResp : " + jsonObj.toString());

                    response = jsonObj.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (is != null) {
                        is.close();
                    }
                }
                int status = conn.getResponseCode();
                if (status == 200) {
                    if (response.equals("1")) {


                        // ((Provider_DashBoard) GeneralData.context).finish();

                        // sharedPreferences.edit().putString(Constants.PREF_GCM_REG_ID, (Provider_DashBoard) GeneralData.context).apply();
                        Intent registrationComplete = new Intent(Constants.SERVER_SUCCESS);
                        LocalBroadcastManager.getInstance(PushNotificationService.this).sendBroadcast(registrationComplete);
                    }
                } else {
                    throw new IOException("Request failed with error code "
                            + status);
                }
            } catch (ProtocolException pe) {
                pe.printStackTrace();
                //sharedPreferences.edit().putString(Constants.PREF_GCM_REG_ID, "").apply();

            } catch (IOException io) {
                io.printStackTrace();
                //    sharedPreferences.edit().putString(Constants.PREF_GCM_REG_ID, "").apply();

            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            return response;
        }

        protected void onPostExecute(String result) {
            try {

                JSONObject jsobj = new JSONObject(result);


                if (jsobj.getString("code").equalsIgnoreCase("2")) {


                    Log.i("GCM", "jSucess ");

                    final AlertDialog.Builder builder = new AlertDialog.Builder(GeneralData.context);

                    LayoutInflater inflater = (LayoutInflater) GeneralData.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View viewScheduleDialog = inflater.inflate(R.layout.popup_msgall, null);

                    TextView tvMessage = (TextView) viewScheduleDialog.findViewById(R.id.message);
                    final Button btnOk = (Button) viewScheduleDialog.findViewById(R.id.ok);
                    final Button btnCancel = (Button) viewScheduleDialog.findViewById(R.id.cancel);

                    builder.setCancelable(false);
                    builder.setView(viewScheduleDialog);

                    tvMessage.setText("You have succesfully paid your utician");


                    try {
                        new Thread() {
                            @Override
                            public void run() {

                                Looper.prepare();
                                final AlertDialog alertDialog = builder.create();

                                btnOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //kalai
                                        //Do the Stuff what you want.......!!!
                                        //
                                        alertDialog.dismiss();


                                        Log.i("PP", "str_providerid 2" + strproviderid);
                                        Log.i("PP", "str_providerimg 2" + strproviderimg);
                                        Log.i("PP", "str_proivdername 2" + strprovidername);
                                        Log.i("PP", "str_Rowid 2" + strrowid);
                                        Log.i("PP", "Str_userid 2" + struserid);
                                        reviewpop(strrowid, strproviderimg, strprovidername, strproviderid, struserid);
                                    }
                                });


                                alertDialog.getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
                                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
                                alertDialog.show();
                                Looper.loop();

                            }
                        }.start();

                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }

                }
                alertDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }

    public void reviewpop(String bookid, String spimage, String spname, String spid, final String struserid) {


        str_providerid = spid;
        str_providerimg = spimage;
        str_proivdername = spname;
        str_Rowid = bookid;
        Str_userid = struserid;
        Log.i("PP", "str_providerid 3" + str_providerid);
        Log.i("PP", "str_providerimg 3" + str_providerimg);
        Log.i("PP", "str_proivdername 3" + str_proivdername);
        Log.i("PP", "str_Rowid 3" + str_Rowid);
        Log.i("PP", "Str_userid 3" + Str_userid);


        Log.i("GCM", "jSucess ");


        LayoutInflater inflater = (LayoutInflater) GeneralData.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewScheduleDialog = inflater.inflate(R.layout.ser_review, null);


        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(GeneralData.context);
        alertDialogBuilder.setView(viewScheduleDialog);
        alertDialogBuilder.setCancelable(false);


        final android.app.AlertDialog alertDialogg = alertDialogBuilder.create();
        //  alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialogg.show();

        ratingBar = (RatingBar) viewScheduleDialog.findViewById(R.id.review_rating);
        btn_submit_review = (Button) viewScheduleDialog.findViewById(R.id.but_submit);

        txt_username = (TextView) viewScheduleDialog.findViewById(R.id.txt_username);

        edit_review = (EditText) viewScheduleDialog.findViewById(R.id.edit_review);

        final Button btn_cancel = (Button) viewScheduleDialog.findViewById(R.id.butcancel);
        ImageView close = (ImageView) viewScheduleDialog.findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("PP", "djdhkd");
                alertDialogg.dismiss();
            }
        });
        //  str_provider_name=txt_provider_name.getText().toString();

        ImageView provider_image = (ImageView) viewScheduleDialog.findViewById(R.id.user_image);

        txt_username.setText(str_proivdername);
        // str_id = jsobj.getString("userid");


        if (str_providerimg.length() == 0) {
            provider_image.setBackgroundResource(R.drawable.default_user_icon);
        } else {

            String strimage = gD.common_baseurl + "upload/" + str_providerimg;

            Log.i("PP", "strimage" + strimage);

            imgLoader.DisplayImage(strimage, provider_image);
            //user_image.setImageBitmap(imgLoader.getBitmap(strimage));

        }


        LinearLayout llayAlert = (LinearLayout) viewScheduleDialog.findViewById(R.id.llayalertDialog);

        FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, nScreenHeight);
        llayAlert.setLayoutParams(lparams);


        try {
            new Thread() {
                @Override
                public void run() {

                    Looper.prepare();

                    btn_submit_review.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            str_rating = String.valueOf(ratingBar.getRating());
                            str_des = edit_review.getText().toString();
                            // Toast.makeText(Provider_Pay.this, " "+str_rating+" "+str_id+"- "+str_des, Toast.LENGTH_SHORT).show();

                            Log.i("HHB", "Rating : " + ratingBar.getRating());
                            if (!str_rating.equalsIgnoreCase("0.0") && str_des.length() > 0) {
                                ReviewUploadTask uploadTask = new ReviewUploadTask(str_Rowid, struserid, str_providerid);
                                uploadTask.execute();
                                alertDialogg.dismiss();


                            } else {
                                if (str_rating.equals("0.0")) {
                                    Toast.makeText(GeneralData.context, "Rating should not be zero !", Toast.LENGTH_SHORT).show();
                                } else if (str_des.length() == 0) {
                                    Toast.makeText(GeneralData.context, "Comment should not be empty !", Toast.LENGTH_SHORT).show();
                                }
                            }


                        }
                    });
                    btn_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final AlertDialog.Builder builder = new AlertDialog.Builder(GeneralData.context);

                            LayoutInflater inflater = (LayoutInflater) GeneralData.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View viewScheduleDialog = inflater.inflate(R.layout.layout_confirmation, null);

                            TextView tvMessage = (TextView) viewScheduleDialog.findViewById(R.id.message);
                            final Button btnOk = (Button) viewScheduleDialog.findViewById(R.id.ok);
                            final Button btnCancel = (Button) viewScheduleDialog.findViewById(R.id.cancel);

                            builder.setCancelable(false);
                            builder.setView(viewScheduleDialog);

                            tvMessage.setText("Do you proceed further without giving review");


                            try {
                                new Thread() {
                                    @Override
                                    public void run() {

                                        Looper.prepare();
                                        final AlertDialog alertDialog = builder.create();


                                        btnOk.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {


                                            //    Intent resultIntenti = new Intent(GeneralData.context, UserHistoryPage.class);
                                             //   resultIntenti.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                             //   (GeneralData.context).startActivity(resultIntenti);


                                                alertDialog.dismiss();
                                                alertDialogg.dismiss();

                                            }
                                        });

                                        btnCancel.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                alertDialog.dismiss();


                                            }
                                        });


                                        alertDialog.getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
                                        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
                                        alertDialog.show();
                                        Looper.loop();

                                    }
                                }.start();

                            } catch (Exception e) {
                                // TODO: handle exception
                                e.printStackTrace();
                            }


                        }
                    });


                }
            }.start();

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }


    }


    public class ReviewUploadTask extends AsyncTask<Void, Void, String> {
        String strrowid, struserid, strproviderid;
        String response = "";


        public ReviewUploadTask(String str_rowid, String struserid, String str_providerid) {
            this.struserid = struserid;
            strrowid = str_rowid;
            strproviderid = str_providerid;

        }


        @Override
        protected String doInBackground(Void... params) {
            // SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PushNotificationService.this);

            Log.i("GCM", "jSucess **");
            URL url = null;
            String requestURL = gD.common_baseurl + "writereview.php";
            try {
                url = new URL(requestURL);
            } catch (MalformedURLException e) {
                e.printStackTrace();

                // sharedPreferences.edit().putString(Constants.PREF_GCM_REG_ID, "").apply();
            }
            Map<String, String> dataMap = new HashMap<String, String>();
            dataMap.put("id", strrowid);
            dataMap.put("userid", struserid);

            dataMap.put("description", str_des);
            dataMap.put("rating", str_rating);

            dataMap.put("providerid", strproviderid);


            Log.i("GCM", "strproviderid **" + strproviderid);
            Log.i("GCM", "str_rating **" + str_rating);
            Log.i("GCM", "str_des **" + str_des);
            Log.i("GCM", "userid **" + struserid);
            Log.i("GCM", "strrowid **" + strrowid);
            StringBuilder postBody = new StringBuilder();
            Iterator<Map.Entry<String, String>> iterator = dataMap.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, String> param = (Map.Entry<String, String>) iterator.next();
                postBody.append(param.getKey()).append('=')
                        .append(param.getValue());
                if (iterator.hasNext()) {
                    postBody.append('&');
                }
            }
            String body = postBody.toString();
            byte[] bytes = body.getBytes();

            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setFixedLengthStreamingMode(bytes.length);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Encoding", "gzip");
                conn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded;charset=UTF-8");

                OutputStream out = conn.getOutputStream();
                out.write(bytes);
                out.close();
                StringBuffer sb = new StringBuffer();
                InputStream is = null;
                try {
                    is = conn.getInputStream();
                    int ch;

                    while ((ch = is.read()) != -1) {
                        sb.append((char) ch);
                    }
                    response = sb.toString();

                } catch (IOException e) {
                    throw e;
                }

                try {

                    JSONObject jsonObj = new JSONObject(sb.toString());

                    Log.i("DDD", "StrResp : " + jsonObj.toString());

                    response = jsonObj.toString();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (is != null) {
                        is.close();
                    }
                }
                int status = conn.getResponseCode();
                if (status == 200) {
                    if (response.equals("1")) {


                        // ((Provider_DashBoard) GeneralData.context).finish();

                        // sharedPreferences.edit().putString(Constants.PREF_GCM_REG_ID, (Provider_DashBoard) GeneralData.context).apply();
                        Intent registrationComplete = new Intent(Constants.SERVER_SUCCESS);
                        LocalBroadcastManager.getInstance(PushNotificationService.this).sendBroadcast(registrationComplete);
                    }
                } else {
                    throw new IOException("Request failed with error code "
                            + status);
                }
            } catch (ProtocolException pe) {
                pe.printStackTrace();
                //sharedPreferences.edit().putString(Constants.PREF_GCM_REG_ID, "").apply();

            } catch (IOException io) {
                io.printStackTrace();
                //    sharedPreferences.edit().putString(Constants.PREF_GCM_REG_ID, "").apply();

            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            return response;
        }

        protected void onPostExecute(String result) {
            try {

                JSONObject jsobj = new JSONObject(result);


                if (jsobj.getString("code").equalsIgnoreCase("2")) {


                    Log.i("GCM", "jSucess ");

                    final AlertDialog.Builder builder = new AlertDialog.Builder(GeneralData.context);

                    LayoutInflater inflater = (LayoutInflater) GeneralData.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View viewScheduleDialog = inflater.inflate(R.layout.popup_msgall, null);

                    TextView tvMessage = (TextView) viewScheduleDialog.findViewById(R.id.message);
                    final Button btnOk = (Button) viewScheduleDialog.findViewById(R.id.ok);
                    final Button btnCancel = (Button) viewScheduleDialog.findViewById(R.id.cancel);

                    builder.setCancelable(false);
                    builder.setView(viewScheduleDialog);

                    tvMessage.setText("Review posted successfully");


                    try {
                        new Thread() {
                            @Override
                            public void run() {

                                Looper.prepare();
                                final AlertDialog alertDialog = builder.create();

                                btnOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        //kalai
                                        //Do the Stuff what you want.......!!!
                                        //

                                      //  Intent resultIntenti = new Intent(GeneralData.context, User_Category.class);
                                       // resultIntenti.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                      //  (GeneralData.context).startActivity(resultIntenti);
                                        alertDialog.dismiss();
                                    }
                                });


                                alertDialog.getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
                                alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);
                                alertDialog.show();
                                Looper.loop();

                            }
                        }.start();

                    } catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }

                }
                alertDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }
}






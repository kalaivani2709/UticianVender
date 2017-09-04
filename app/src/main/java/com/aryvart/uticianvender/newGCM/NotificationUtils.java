package com.aryvart.uticianvender.newGCM;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.genericclasses.AlertDialogManager;
import com.aryvart.uticianvender.utician.SplashActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by Ravi on 01/06/15.
 */
public class NotificationUtils {

    private static String TAG = NotificationUtils.class.getSimpleName();
    public MyPreferenceManager pref;
    AlertDialogManager alert = new AlertDialogManager();
    private Context mContext;

    public NotificationUtils() {
    }

    public NotificationUtils(Context mContext) {
        this.mContext = SplashActivity.context;
        pref = new MyPreferenceManager(SplashActivity.context);
    }

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = false;
        try {
            isInBackground = true;
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (String activeProcess : processInfo.pkgList) {
                            if (activeProcess.equals(context.getPackageName())) {
                                isInBackground = false;
                            }
                        }
                    }
                }
            } else {
                List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
                ComponentName componentInfo = taskInfo.get(0).topActivity;
                if (componentInfo.getPackageName().equals(context.getPackageName())) {
                    isInBackground = false;
                }
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        return isInBackground;
    }

    // Clears notification tray messages
    public static void clearNotifications() {
        NotificationManager notificationManager = (NotificationManager) SplashActivity.context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public void showNotificationMessage(String title, String message, String timeStamp, Intent intent) {
        showNotificationMessage(title, message, timeStamp, intent, null);
    }

    public void showNotificationMessage(final String title, final String message, final String timeStamp, Intent intent, String imageUrl) {
        try {
            // Check for empty push message
            if (TextUtils.isEmpty(message))
                return;
            // notification icon
            final int icon = R.drawable.logo;
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            final PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            mContext,
                            0,
                            intent,
                            PendingIntent.FLAG_CANCEL_CURRENT
                    );

            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                    mContext);

            final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + mContext.getPackageName() + "/raw/notification");

            if (!TextUtils.isEmpty(imageUrl)) {

                if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {

                    Bitmap bitmap = getBitmapFromURL(imageUrl);

                    if (bitmap != null) {
                        showBigNotification(bitmap, mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
                    } else {
                        showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
                    }
                }
            } else {
                showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
                playNotificationSound();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {

        try {
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

            if (Config.appendNotificationMessages) {
                // store the notification in shared pref first
                pref.addNotification(message);

                // get the notifications from shared preferences
                String oldNotification = pref.getNotifications();

                List<String> messages = Arrays.asList(oldNotification.split("\\|"));

                for (int i = messages.size() - 1; i >= 0; i--) {
                    inboxStyle.addLine(messages.get(i));
                }
            } else {
                inboxStyle.addLine(message);
            }

            Date today = Calendar.getInstance().getTime();

            Notification notification;
            notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setSound(alarmSound)
                    .setStyle(inboxStyle)
                    .setWhen(today.getTime())
                    .setSmallIcon(R.drawable.logo)
                    .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                    .setContentText(message)
                    .build();

            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(Config.NOTIFICATION_ID, notification);

            alert.showAlertDialog(mContext, "Booking Accept", false);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound) {
        Date today = Calendar.getInstance().getTime();
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);
        Notification notification;
        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(bigPictureStyle)
                .setWhen(today.getTime())
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(Config.NOTIFICATION_ID_BIG_IMAGE, notification);
    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Playing notification sound
    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + SplashActivity.context.getApplicationContext().getPackageName() + "/raw/notification");
            Ringtone r = RingtoneManager.getRingtone(SplashActivity.context.getApplicationContext(), alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

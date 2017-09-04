package com.aryvart.uticianvender.provider;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.genericclasses.AlertDialogManager;
import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.aryvart.uticianvender.genericclasses.MultipartUtility;
import com.aryvart.uticianvender.utician.SplashActivity;

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by android1 on 17/7/16.
 */
public class ProviderAvailability extends Activity {

    static final int TIME_DIALOG = 1;
    static final int DATE_DIALOG_ID = 2;
    LinearLayout from_date, from_time, to_date, to_time;
    TextView text_from_date, text_from_month, text_from_year, text_to_date, text_to_month, text_to_year, text_from_hour, text_from_min, text_to_hour, text_to_min;
    TextView from_am, from_pm, to_am, to_pm;
    String set = "", setDate = "";
    Button save;
    String str_date_from, timezonevalue,str_date_to, str_time_from, str_time_to, str_fromAMPM, string_toAMPM, hr_from, min_from, hr_to, min_to;
    boolean isEdit = false;
    LinearLayout my_layout;
    String am_pm, am_pm2;
    String strEditId, time;
    ProgressDialog pd;
    String requestURL = "";
    AlertDialogManager alert = new AlertDialogManager();
    ImageView menu_back;
    GeneralData gD;
    Context context;
    private int hour;
    private int minute;
    private int year, month, day;
    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {


        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            // TODO Auto-generated method stub
            hour = hourOfDay;
            minute = minutes;
            updateTime(hour, minute, set);
        }
    };
    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            showDate(arg1, arg2 + 1, arg3);
        }
    };

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



    private void showDate(int year, int month, int day) {
        try {
            String mm = "", dd = "", yy = "";
            if (month < 10) mm = "0" + month;
            else mm = String.valueOf(month);
            if (day < 10) dd = "0" + day;
            else dd = String.valueOf(day);
            yy = String.valueOf(year);
            if (setDate == "dateFrom") {
                text_from_date.setText(dd);
                text_from_month.setText(mm);
                text_from_year.setText(yy);
                str_date_from = yy + "-" + mm + "-" + dd;
                Log.e("Date_From", str_date_from);
                /*Toast.makeText(getApplicationContext(), str_date_from, Toast.LENGTH_SHORT).show();*/


            } else if (setDate == "dateTo") {
                text_to_date.setText(dd);
                text_to_month.setText(mm);
                text_to_year.setText(yy);
                str_date_to = yy + "-" + mm + "-" + dd;
                Log.e("Date_to", str_date_to);
                /*Toast.makeText(getApplicationContext(), str_date_to, Toast.LENGTH_SHORT).show();*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void updateTime(int hours, int mins, String data) {
        try {
            String timeSet = "";
            if (hours > 12) {
                hours -= 12;
                timeSet = "PM";
            } else if (hours == 0) {
                hours += 12;
                timeSet = "AM";
            } else if (hours == 12)
                timeSet = "PM";
            else
                timeSet = "AM";


            String hour = "";
            String minutes = "";
            if (hours < 10) {
                hour = "0" + hours;
            } else {
                hour = String.valueOf(hours);
            }

            time = hour;
            if (mins == 0) {
                minutes = "00";
            } else if (mins > 0 && mins <= 15) {
                minutes = "15";
            } else if (mins > 15 && mins <= 30) {
                minutes = "30";
            } else if (mins > 30 && mins <= 45) {
                minutes = "45";
            } else {
                if (hours <= 11) {

                    if (mins > 45) {

                        if (hours < 10) {
                            if(hours == 9 ) {
                                time =  String.valueOf(Integer.parseInt(hour) + 1);
                            }else{
                                time =  "0" + String.valueOf(Integer.parseInt(hour) + 1);
                            }
                        } else {
                            time = String.valueOf(Integer.parseInt(hour) + 1);
                        }

                        minutes = "00";
                    } else {
                        time = hour;
                        minutes = String.valueOf(mins);
                    }
                } else {
                    time = "01";
                    if (mins > 45) {
                        minutes = "00";
                    } else {
                        minutes = String.valueOf(mins);
                    }
                }


            }


            if (data == "timeFrom") {

                text_from_hour.setText(time);
                text_from_min.setText(minutes);

                str_fromAMPM = timeSet;
                str_time_from = time + ":" + minutes + ":" + "00" + " " + str_fromAMPM;
                Log.e("Time_From", str_time_from);
                /*Toast.makeText(getApplicationContext(), str_time_from, Toast.LENGTH_SHORT).show();*/
                if (timeSet == "AM") {
                    from_am.setTextColor(Color.parseColor("#42dcdc"));
                    from_pm.setTextColor(Color.parseColor("#666666"));
                } else {
                    from_pm.setTextColor(Color.parseColor("#42dcdc"));
                    from_am.setTextColor(Color.parseColor("#666666"));
                }
            } else {

                text_to_hour.setText(time);
                text_to_min.setText(minutes);

                string_toAMPM = timeSet;
                str_time_to = time + ":" + minutes + ":" + "00" + " " + string_toAMPM;
                Log.e("Time_to", str_time_to);
                /*Toast.makeText(getApplicationContext(), str_time_to, Toast.LENGTH_SHORT).show();*/
                if (timeSet == "PM") {
                    to_am.setTextColor(Color.parseColor("#666666"));
                    to_pm.setTextColor(Color.parseColor("#42dcdc"));
                } else {
                    to_pm.setTextColor(Color.parseColor("#666666"));
                    to_am.setTextColor(Color.parseColor("#42dcdc"));

                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }
int nScreenHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.availability);
            save = (Button) findViewById(R.id.save);
            my_layout = (LinearLayout) findViewById(R.id.avail_content);
            context = this;
            gD = new GeneralData(context);
            final Calendar c = Calendar.getInstance();
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
            //updateTime(hour, minute, "data");

            year = c.get(Calendar.YEAR);

            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            showDate(year, month + 1, day);
            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = (int) ((float) nHeight / (float) 2.1);

            TimeZone tz = TimeZone.getDefault();
            System.out.println("TimeZone " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

            timezonevalue=tz.getID();
            Log.i("TZ", "timezone" + timezonevalue);

            from_date = (LinearLayout) findViewById(R.id.lay_from_date);
            from_time = (LinearLayout) findViewById(R.id.lay_from_time);
            to_date = (LinearLayout) findViewById(R.id.lay_to_date);
            to_time = (LinearLayout) findViewById(R.id.lay_to_time);
            text_from_date = (TextView) findViewById(R.id.from_date);
            text_from_month = (TextView) findViewById(R.id.from_month);
            text_from_year = (TextView) findViewById(R.id.from_year);
            text_to_date = (TextView) findViewById(R.id.to_date);
            text_to_month = (TextView) findViewById(R.id.to_month);
            text_to_year = (TextView) findViewById(R.id.to_year);
            text_from_hour = (TextView) findViewById(R.id.from_hour);
            text_from_min = (TextView) findViewById(R.id.from_min);
            text_to_hour = (TextView) findViewById(R.id.to_hour);
            text_to_min = (TextView) findViewById(R.id.to_min);
            from_am = (TextView) findViewById(R.id.from_am);
            from_pm = (TextView) findViewById(R.id.from_pm);
            to_am = (TextView) findViewById(R.id.to_am);
            to_pm = (TextView) findViewById(R.id.to_pm);
            menu_back = (ImageView) findViewById(R.id.back_image);

            menu_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    Intent i = new Intent(context, Provider_DashBoard.class);
                    //  gD.screenback = 0;
                    startActivity(i);
                }
            });
            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(context, "Unable to connect with Internet. Please check your internet connection and try again");
            }

            from_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    showDialog(DATE_DIALOG_ID);
                    setDate = "dateFrom";
                }
            });
            from_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    set = "timeFrom";
                    showDialog(TIME_DIALOG);
                }
            });
            to_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(DATE_DIALOG_ID);
                    setDate = "dateTo";
                }
            });
            to_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    set = "timeTo";
                    showDialog(TIME_DIALOG);
                }
            });


            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(ProviderAvailability.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } else {

                        if (str_date_from.equalsIgnoreCase("")) {
                            Toast.makeText(getApplicationContext(), "Please enter your FROM date", Toast.LENGTH_SHORT).show();
                        } else if (str_date_to.equalsIgnoreCase("")) {
                            Toast.makeText(getApplicationContext(), "Please enter your TO date", Toast.LENGTH_SHORT).show();
                        } else if (str_time_from.equalsIgnoreCase("")) {
                            Toast.makeText(getApplicationContext(), "Please enter your FROM time", Toast.LENGTH_SHORT).show();
                        } else if (str_time_to.equalsIgnoreCase("")) {
                            Toast.makeText(getApplicationContext(), "Please enter your TO time", Toast.LENGTH_SHORT).show();
                        } else {

                            try {
                                String strFromHour = change24to12hrswiseversa(str_time_from, "hh:mm:ss a", "HH:mm:ss", 0);
                                String strToHour = change24to12hrswiseversa(str_time_to, "hh:mm:ss a", "HH:mm:ss", 0);
                            /*Toast.makeText(getApplicationContext(), strFromHour + "---" + strToHour, Toast.LENGTH_SHORT).show();*/

                                String str_from_Date = str_date_from + " " + strFromHour;
                                String str_to_Date = str_date_to + " " + strToHour;


                                if (isEdit) {
                                    requestURL = gD.common_baseurl+"aunavailability.php?providerid=" + URLEncoder.encode(SplashActivity.sharedPreferences.getString("UID", null), "utf-8") + "&from=" + URLEncoder.encode(str_from_Date, "utf-8") + "&to=" + URLEncoder.encode(str_to_Date, "utf-8") + "&id=" + URLEncoder.encode(strEditId, "utf-8") + "&timezone=" + URLEncoder.encode(timezonevalue.trim(), "utf-8") ;

                               /* requestURL = "http://www.aryvartdev.com/projects/utician/aunavailability.php?providerid=" + URLEncoder.encode("33", "utf-8") + "&from=" + URLEncoder.encode(str_from_Date, "utf-8") + "&to=" + URLEncoder.encode(str_to_Date, "utf-8") + "&id=" + URLEncoder.encode(strEditId, "utf-8");*/
                                } else {
                                    requestURL = gD.common_baseurl+"aunavailability.php?providerid=" + URLEncoder.encode(SplashActivity.sharedPreferences.getString("UID", null), "utf-8") + "&from=" + URLEncoder.encode(str_from_Date, "utf-8") + "&to=" + URLEncoder.encode(str_to_Date, "utf-8") +  "&timezone=" + URLEncoder.encode(timezonevalue.trim(), "utf-8");

                                /*requestURL = "http://www.aryvartdev.com/projects/utician/aunavailability.php?providerid=" + URLEncoder.encode("33", "utf-8") + "&from=" + URLEncoder.encode(str_from_Date, "utf-8") + "&to=" + URLEncoder.encode(str_to_Date, "utf-8");*/
                                }

                                String strChoosenFromhour = str_date_from + " " + strFromHour;
                                String strChoosenTohour = str_date_to + " " + strToHour;

                                String dateCurrent = strChoosenFromhour;
                                String dateChosen = strChoosenTohour;

                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                Date d1 = null;
                                Date d2 = null;
                                try {
                                    d1 = format.parse(dateCurrent);
                                    d2 = format.parse(dateChosen);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                long diff = d2.getTime() - d1.getTime();
                                long diffSeconds = diff / 1000 % 60;
                                long diffMinutes = diff / (60 * 1000) % 60;
                                long diffHours = diff / (60 * 60 * 1000);

                                SimpleDateFormat sdfCurrentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String CurrentDateandTime = sdfCurrentTime.format(new Date());
                                try {
                                    Date d3 = sdfCurrentTime.parse(CurrentDateandTime);

                                    if (d1.getTime() < d2.getTime()) {


                                        if (d3.getTime() < d1.getTime()) {
                                            my_layout.setVisibility(View.VISIBLE);
                                        /*txt_un_error_msg.setVisibility(View.GONE);*/
                                            GetCalendar getCalendar = new GetCalendar(URLEncoder.encode(SplashActivity.sharedPreferences.getString("UID", null), "utf-8"), requestURL, "insert");
                                        /*GetCalendar getCalendar = new GetCalendar(URLEncoder.encode("33", "utf-8"), requestURL, "insert");*/
                                            getCalendar.execute();
                                        } else {
                                            gD.showAlertDialog(context, "",   "Date or Time expires. Please enter a valid date or time", nScreenHeight, 1);

                                        }
                                    } else {
                                        gD.showAlertDialog(context, "",   "Date or Time expires. Please enter a valid date or time", nScreenHeight, 1);


                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
            });
            try {
                String requestURL = gD.common_baseurl+"aunavailability.php?providerid=" + URLEncoder.encode(SplashActivity.sharedPreferences.getString("UID", null), "utf-8") + "&timezone=" + URLEncoder.encode(timezonevalue.trim(), "utf-8") ;
                /*String requestURL = "http://www.aryvartdev.com/projects/utician/aunavailability.php?providerid=" + URLEncoder.encode("33", "utf-8");*/
                /*GetCalendar getCalendar = new GetCalendar(URLEncoder.encode("33", "utf-8"), requestURL, "onload");*/
                GetCalendar getCalendar = new GetCalendar(URLEncoder.encode(SplashActivity.sharedPreferences.getString("UID", null), "utf-8"), requestURL, "onload");
                /*GetCalendar getCalendar = new GetCalendar(URLEncoder.encode(SplashActivity.sharedPreferences.getString("UID", null), "utf-8"), requestURL, "onload");*/
                getCalendar.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected Dialog onCreateDialog(int id) {
        try {
            switch (id) {
                case TIME_DIALOG:
                    // set time picker as current time
                    return new TimePickerDialog(this, timePickerListener, hour, minute,
                            false);
                case DATE_DIALOG_ID:

                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DATE, 0);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(this, myDateListener, year, month, day);
                    datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
                    datePickerDialog.show();


                    // return new DatePickerDialog(this, myDateListener, year, month, day);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onResume() {


        try {
            TimeZone tz = TimeZone.getDefault();
            System.out.println("TimeZone " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

            timezonevalue = tz.getID();

            if (!gD.isConnectingToInternet()) {
                Toast.makeText(ProviderAvailability.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
            }


            super.onResume();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String change24to12hrswiseversa(String strFromstring, String strConvertFromformat,
                                            String strConvertToformat, int nVal) {
        SimpleDateFormat parseFormat, displayFormat;
        Date date;
        String strDate = null;
        try {
            if (nVal == 0) {
                parseFormat = new SimpleDateFormat(strConvertFromformat);
                displayFormat = new SimpleDateFormat(strConvertToformat);
                date = parseFormat.parse(strFromstring);
                strDate = displayFormat.format(date);
                Log.i("Date-if:", strDate);

            } else {
                parseFormat = new SimpleDateFormat(strConvertToformat);
                displayFormat = new SimpleDateFormat(strConvertFromformat);
                date = displayFormat.parse(strFromstring);
                strDate = parseFormat.format(date);
                Log.i("Date-else:", strDate);
            }

            System.out.println(displayFormat.format(date) + " = " + parseFormat.format(date));
            System.out.println("Date : " + strDate);
            Log.i("Date-try:", strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return strDate;

    }

    private String getCurrentDateInSpecificFormat(int nYear, int nMonth, int nDate, int nHour, int nMinute, String st_am_pm) {
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
            DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd hh:ss");
            Date date = originalFormat.parse(strNeedDateVal);

            Log.i("date", "" + date);
            String dayNumberSuffix = getDayNumberSuffix(Integer.parseInt(strCDate));
            DateFormat targetFormat = new SimpleDateFormat("MMM dd'" + dayNumberSuffix + "', yyyy hh:ss");
            /*DateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy / hh:ss");*/

            if (st_am_pm.equals("AM")) {
                formattedDate = targetFormat.format(date) + " " + "AM";
                Log.i("nan", formattedDate);
            } else {
                formattedDate = targetFormat.format(date) + " " + "PM";
                Log.i("nan", formattedDate);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return formattedDate;
    }

    private void createChild(String str1, String str2, final String id, final String str_provide_id, final int n_pos) {
        try {
            LayoutInflater inflater = LayoutInflater.from(ProviderAvailability.this);

            final View view1 = inflater.inflate(R.layout.provider_unavailability_contents, null);
            final LinearLayout child_lay = (LinearLayout) view1.findViewById(R.id.child_layout);

            if (n_pos % 2 != 0) {
                child_lay.setBackgroundColor(Color.parseColor("#e5e5e5"));
            } else {
                child_lay.setBackgroundColor(Color.parseColor("#f7f7f7"));
            }
            TextView txt_from = (TextView) view1.findViewById(R.id.txt_from);
            TextView txt_to = (TextView) view1.findViewById(R.id.txt_to);

            ImageView img_delete = (ImageView) view1.findViewById(R.id.img_delete);
            ImageView img_edit = (ImageView) view1.findViewById(R.id.img_edit);

            txt_from.setText("" + str1);
            txt_to.setText("" + str2);
            view1.setId(Integer.parseInt(id));


            img_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(ProviderAvailability.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("D-id", id);
                        Log.i("D-provid_id", str_provide_id);
                        DeleteUnavailabilty unavailabilty = new DeleteUnavailabilty(id, str_provide_id);
                      //
                        unavailabilty.execute();
                        my_layout.removeViewAt(my_layout.indexOfChild(view1));
                    }
                }
            });

            img_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(ProviderAvailability.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.i("E-id", id);
                        Log.i("E-provid_id", str_provide_id);

                        isEdit = true;
                        strEditId = id;
                        EditUnavailabilty editUnavailabilty = new EditUnavailabilty(id, str_provide_id);
                        editUnavailabilty.execute();
                    }
                }
            });

            my_layout.addView(view1);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }
    // Used to convert 24hr format to 12hr format with AM/PM values

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
    public void onBackPressed() {
        finish();
        Intent i = new Intent(context, Provider_DashBoard.class);
        //  gD.screenback = 0;
        startActivity(i);
    }

    private class GetCalendar extends AsyncTask {
        String sResponse = null;
        String strProvider_Id;
        String str_URL;
        String strReq_Type;

        public GetCalendar(String strProviderId, String strURL, String strType) {
            strProvider_Id = strProviderId;
            str_URL = strURL;
            Log.i("Str_ULR",str_URL);
            strReq_Type = strType;


        }


        @Override
        protected Object doInBackground(Object[] objects) {
            Log.i("HH", "Else.....");
            String charset = "UTF-8";
            // 4. separate class for multipart content image uploaded task----------- vinoth
            MultipartUtility multipart = null;
            try {
                // APIcalls AceptApi = new APIcalls(requestURL);
                // AceptApi.Process();
                try {
                    URL url;
                    HttpURLConnection urlConnection = null;
                    try {
                        Log.i("SR", "Login URL : " + str_URL.trim());
                        System.out.print("strHTTP_LOGIN_URL");
                        url = new URL(str_URL.trim());
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
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            gD.callload(context, nScreenHeight);
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
                        if (isEdit) {
                            isEdit = false;
                        }
                        /*Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();*/
                        try {
                            Log.i("RESP", "response : " + sResponse);

                            JSONObject json = new JSONObject(sResponse);
                            JSONArray jsArunAvailable = null;
                            jsArunAvailable = json.getJSONArray("unavailable");
                            my_layout.removeAllViews();
                            for (int i = 0; i < jsArunAvailable.length(); i++) {
                                String strFromdate = jsArunAvailable.getJSONObject(i).getString("fromdate").trim();
                                String strTodate = jsArunAvailable.getJSONObject(i).getString("todate").trim();
                                String str_providerid = jsArunAvailable.getJSONObject(i).getString("providerid").trim();
                                String str_id = jsArunAvailable.getJSONObject(i).getString("id").trim();

                                if (Integer.parseInt(strFromdate.split(" ")[0].split("-")[0]) != 0 && Integer.parseInt(strFromdate.split(" ")[0].split("-")[1]) != 0 && Integer.parseInt(strFromdate.split(" ")[0].split("-")[2]) != 0) {
                                    Log.i("ND", "Fromdate : " + strFromdate);
                                    Log.i("ND", "Todate : " + strTodate);
                                    Log.i("ND", "id:" + str_id);
                                    Log.i("ND", "providerid:" + str_providerid);
                                    //String Log.i("ND","id:"+str_id); strFromdate1=jsArunAvailable.getJSONObject(i).getString("fromdate");
                                    //String strTodate1=jsArunAvailable.getJSONObject(i).getString("todate");
                                    int nFromYear = Integer.parseInt(strFromdate.split(" ")[0].split("-")[0]);
                                    int nFromMonth = Integer.parseInt(strFromdate.split(" ")[0].split("-")[1]);
                                    int nFromDate = Integer.parseInt(strFromdate.split(" ")[0].split("-")[2]);
                                    String strHour = change24to12hrswiseversa(strFromdate.split(" ")[1], "HH:mm:ss", "hh:mm:ss a", 1);
                                    //01:12:00 PM
                                    int nFromHour = Integer.parseInt(strHour.split(" ")[0].split(":")[0]);
                                    int nFromMinute = Integer.parseInt(strHour.split(" ")[0].split(":")[1]);
                                    am_pm = strHour.split(" ")[1];
                                    Log.i("OO", "nFromMonth : " + nFromMonth);

                                    //Change Date in to String format for From Date

                                    int nh = Integer.parseInt(strHour.split(" ")[0].split(":")[0]);
                                    int nm = Integer.parseInt(strHour.split(" ")[0].split(":")[1]);
                                    Log.i("to-nm", "" + nh);
                                    Log.i("from-nm", "" + nm);
                                    String strNeededFromDate = getCurrentDateInSpecificFormat(nFromYear, nFromMonth, nFromDate, nFromHour, nFromMinute, am_pm);

                                    int nToYear = Integer.parseInt(strTodate.split(" ")[0].split("-")[0]);
                                    int nToMonth = Integer.parseInt(strTodate.split(" ")[0].split("-")[1]);
                                    int nToDate = Integer.parseInt(strTodate.split(" ")[0].split("-")[2]);
                                    Log.i("ND", "nToYear : " + nToYear);
                                    Log.i("ND", "nToMonth : " + nToMonth);
                                    Log.i("ND", "nToDate : " + nToDate);
                                    String strHourTwo = change24to12hrswiseversa(strTodate.split(" ")[1], "HH:mm:ss", "hh:mm:ss a", 1);
                                    //01:12:00 PM
                                    int nToHour = Integer.parseInt(strHourTwo.split(" ")[0].split(":")[0]);
                                    int nToMinute = Integer.parseInt(strHourTwo.split(" ")[0].split(":")[1]);
                                    am_pm2 = strHourTwo.split(" ")[1];
                                    //Change Date in to String format for From Date

                                   /* Log.i("OO", "nToMonth : " + nToMonth);

                                    Log.i("loose", "am_pm : " + am_pm);
                                    Log.i("loose", "am_pm2 : " + am_pm2);*/
                                    int nh1 = Integer.parseInt(strHourTwo.split(" ")[0].split(":")[0]);
                                    int nm1 = Integer.parseInt(strHourTwo.split(" ")[0].split(":")[1]);
                                   /* Log.i("to-nm", "" + nh1);
                                    Log.i("from-nm", "" + nm1);*/
                                    String strNeededToDate = getCurrentDateInSpecificFormat(nToYear, nToMonth, nToDate, nToHour, nToMinute, am_pm2);
                                    Log.i("to", strNeededToDate);
                                    Log.i("from", strNeededFromDate);

                                    Log.i("to-n", strHour);
                                    Log.i("from-n", strHourTwo);

                                    createChild(strNeededFromDate, strNeededToDate, str_id, str_providerid, i);
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else if (jsobj.getInt("code") == 5) {


                        gD.showAlertDialog(context, "",  "Date or Time expires. Please enter a valid date or time", nScreenHeight, 1);


                    }
                    str_date_from = "";
                    str_date_to = "";
                    str_time_from = "";
                    str_time_to = "";
                    from_am.setTextColor(Color.parseColor("#666666"));
                    from_pm.setTextColor(Color.parseColor("#666666"));
                    to_am.setTextColor(Color.parseColor("#666666"));
                    to_pm.setTextColor(Color.parseColor("#666666"));
                    text_from_date.setText("");
                    text_from_month.setText("");
                    text_from_year.setText("");
                    text_from_min.setText("");
                    text_from_hour.setText("");
                    text_to_date.setText("");
                    text_to_month.setText("");
                    text_to_year.setText("");
                    text_to_hour.setText("");
                    text_to_min.setText("");
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

    private class DeleteUnavailabilty extends AsyncTask {
        String sResponse = null;
        String str_view_id, str_provide_id;

        public DeleteUnavailabilty(String get_view_id, String str_provider_id) {
            str_view_id = get_view_id;
            str_provide_id = str_provider_id;
            Log.i("NN", str_view_id);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            Log.i("HH", "Else.....");
            String charset = "UTF-8";

            try {

                String requestURL = gD.common_baseurl+"aunavailability.php?providerid=" + URLEncoder.encode(str_provide_id, "utf-8") + "&id=" + URLEncoder.encode(str_view_id, "utf-8") + "&action=" + URLEncoder.encode("del", "utf-8")  + "&timezone=" + URLEncoder.encode(timezonevalue.trim(), "utf-8") ;

                //   APIcalls AceptApi = new APIcalls(requestURL);
                // AceptApi.Process();


                try {

                    URL url;
                    HttpURLConnection urlConnection = null;
                    try {
                        Log.i("SR", "Login URL : " + requestURL.trim());
                        System.out.print("strHTTP_LOGIN_URL");
                        url = new URL(requestURL.trim());
                    //    urlConnection.setRequestProperty("Accept-Encoding", "gzip");

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



                    if(gD.alertDialog!=null) {
                        gD.alertDialog.dismiss();
                    }
                    JSONObject jsobj = new JSONObject(sResponse);
                    if (jsobj.getInt("code") == 2) {

                        finish();
                        Intent ii = new Intent(ProviderAvailability.this, ProviderAvailability.class);
                        startActivity(ii);
                        /*Toast.makeText(getApplicationContext(), "deleted....", Toast.LENGTH_SHORT).show();*/
                    }
                } else {
                    pd.dismiss();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class EditUnavailabilty extends AsyncTask {
        String sResponse2 = null;
        String str_view_id, str_provide_id;

        public EditUnavailabilty(String get_view_id, String str_provider_id) {
            str_view_id = get_view_id;
            str_provide_id = str_provider_id;
            Log.i("NN", str_view_id);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            Log.i("HH", "Else.....");
            String charset = "UTF-8";
            try {
                String requestURL = gD.common_baseurl+"aunavailability.php?providerid=" + URLEncoder.encode(str_provide_id, "utf-8") + "&id=" + URLEncoder.encode(str_view_id, "utf-8") + "&action=" + URLEncoder.encode("edit", "utf-8") + "&timezone=" + URLEncoder.encode(timezonevalue.trim(), "utf-8") ;
                //   APIcalls AceptApi = new APIcalls(requestURL);
                // AceptApi.Process();
                try {

                    URL url;
                    HttpURLConnection urlConnection = null;
                    try {
                        Log.i("SR", "Login URL : " + requestURL.trim());
                      //  urlConnection.setRequestProperty("Accept-Encoding", "gzip");
                        System.out.print("strHTTP_LOGIN_URL");
                        url = new URL(requestURL.trim());
                        urlConnection = (HttpURLConnection) url.openConnection();
                        int responseCode = urlConnection.getResponseCode();
                        sResponse2 = readStream(urlConnection.getInputStream());
                        Log.i("SR-edit", "APIcalls_Process : " + sResponse2);

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
            return sResponse2;
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
                if (sResponse2 != null) {



                    if(gD.alertDialog!=null) {
                        gD.alertDialog.dismiss();
                    }
                    JSONObject jsobj = new JSONObject(sResponse2);

                    if (jsobj.getInt("code") == 2) {

                        my_layout.setVisibility(View.VISIBLE);

                        try {
                            JSONObject json = new JSONObject(sResponse2);

                            Log.i("RR", "response" + sResponse2);
                            JSONArray jsArunAvailable = null;
                            jsArunAvailable = json.getJSONArray("unavailable");
                            for (int i = 0; i < jsArunAvailable.length(); i++) {
                                String strFromdate = jsArunAvailable.getJSONObject(i).getString("fromdate").trim();
                                String strTodate = jsArunAvailable.getJSONObject(i).getString("todate").trim();
                                String str_providerid = jsArunAvailable.getJSONObject(i).getString("providerid").trim();
                                String str_id = jsArunAvailable.getJSONObject(i).getString("id").trim();
                                Log.i("ND", "Fromdate : " + strFromdate);
                                Log.i("ND", "Todate : " + strTodate);
                                Log.i("ND", "id:" + str_id);
                                Log.i("ND", "providerid:" + str_providerid);
                                String nFromYear = strFromdate.split("-")[0];
                                String nFromMonth = strFromdate.split("-")[1];
                                String nFromDate = strFromdate.split("-")[2];
                                String nfromDATE = nFromDate.split(" ")[0];

                                String strHour = change24to12hrswiseversa(strFromdate.split(" ")[1], "HH:mm:ss", "hh:mm:ss a", 1);
                                Log.i("pls come", strHour);
                                int nFromHour = Integer.parseInt(strHour.split(" ")[0].split(":")[0]);
                                int nFromMinute = Integer.parseInt(strHour.split(" ")[0].split(":")[1]);
                                String str_AMPM = strHour.split(" ")[1];
                                Log.i("UU", "time" + str_AMPM);
                                if (str_AMPM.equals("AM")) {
                                    from_pm.setTextColor(Color.parseColor("#666666"));
                                    from_am.setTextColor(Color.parseColor("#42dcdc"));
                                } else {
                                    from_am.setTextColor(Color.parseColor("#666666"));
                                    from_pm.setTextColor(Color.parseColor("#42dcdc"));
                                }
                                String strHourr = null, strMin = null;
                                if (nFromHour <= 9) {
                                    strHourr = "0" + nFromHour;
                                    text_from_hour.setText("" + strHourr);
                                    hr_from = strHourr;
                                } else {
                                    text_from_hour.setText("" + nFromHour);
                                    hr_from = "" + nFromHour;
                                }
                                /*n_hour = nFromHour;
                                n_min = nFromMinute;*/
                                if (nFromMinute <= 9) {
                                    strMin = "0" + nFromMinute;
                                    text_from_min.setText("" + strMin);
                                    min_from = strMin;
                                } else {
                                    text_from_min.setText("" + nFromMinute);
                                    min_from = "" + nFromMinute;

                                }
                                /*et_display_date_from.setText(nFromMonth + " " + nFromDateYear);*/
                                text_from_year.setText(nFromYear);
                                text_from_month.setText(nFromMonth);
                                text_from_date.setText(nfromDATE);
                                str_date_from = nFromYear + "-" + nFromMonth + "-" + nfromDATE;
                                str_time_from = hr_from + ":" + min_from + ":" + "00" + " " + str_AMPM;



                                //Change Date in to String format for From Date
                                //String strNeededFromDate = getCurrentDateInSpecificFormat(nFromYear, nFromMonth - 1, nFromDate, nFromHour, nFromMinute);
                                String nToYear = strTodate.split("-")[0];
                                String nToMonth = strTodate.split("-")[1];
                                String nToDate = strTodate.split(" ")[0];
                                String ntoDATE = nToDate.split("-")[2];

                                Log.i("ND", "nToDate:" + nToDate);
                                Log.i("ND", "ntoDATE:" + ntoDATE);
                               /* String nToTime = strFromdate.split(" ")[3];*/




                                String strHourTwo = change24to12hrswiseversa(strTodate.split(" ")[1], "HH:mm:ss", "hh:mm:ss a", 1);
                                Log.i("pls come", strHourTwo);
                                int nToHour = Integer.parseInt(strHourTwo.split(" ")[0].split(":")[0]);
                                int nToMinute = Integer.parseInt(strHourTwo.split(" ")[0].split(":")[1]);
                                String str_AMPM1 = strHourTwo.split(" ")[1];
                                Log.i("UU", "time" + str_AMPM1);
                                if (str_AMPM1.equals("AM")) {
                                    to_pm.setTextColor(Color.parseColor("#666666"));
                                    to_am.setTextColor(Color.parseColor("#42dcdc"));
                                } else {
                                    to_pm.setTextColor(Color.parseColor("#42dcdc"));
                                    to_am.setTextColor(Color.parseColor("#666666"));
                                }

                                String strHourr2 = null, strMin2 = null;
                                if (nToHour <= 9) {
                                    strHourr2 = "0" + nToHour;
                                    text_to_hour.setText("" + strHourr2);
                                    hr_to = strHourr2;

                                } else {
                                    text_to_hour.setText("" + nToHour);
                                    hr_to = "" + nToHour;
                                }

                                if (nToMinute <= 9) {
                                    strMin2 = "0" + nToMinute;
                                    text_to_min.setText("" + strMin2);
                                    min_to = strMin2;

                                } else {
                                    text_to_min.setText("" + nToMinute);
                                    min_to = "" + nToMinute;

                                }
                                text_to_year.setText(nToYear);
                                text_to_month.setText(nToMonth);
                                text_to_date.setText(ntoDATE);
                                str_date_to = nToYear + "-" + nToMonth + "-" + ntoDATE;
                                str_time_to = hr_to + ":" + min_to + ":" + "00" + " " + str_AMPM1;

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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





package com.aryvart.uticianvender.provider;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.customgallery.GalleryAlbumActivity;
import com.aryvart.uticianvender.genericclasses.AlertDialogManager;
import com.aryvart.uticianvender.genericclasses.FileUtils;
import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.aryvart.uticianvender.genericclasses.MultipartUtility;
import com.aryvart.uticianvender.utician.SplashActivity;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by android1 on 30/7/16.
 */
public class ProviderLicenseEdit extends Activity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE = 3;
    ImageView img_dob, img_back, img_license;
    boolean isLicenseUpload = false;
    TextView txt_dob;
    String strMultipleImagePath = "", mCurrentPhotoPath = "", str_capture_file_path;
    ArrayList<File> AlFileList;
    ArrayList<String> AlFileName;
    Context context;
    EditText ed_social_sec;
    String strEditTextVal;
    TextView ll_submit;
    AlertDialogManager alertDialog;
    String gallery_file;
    int len;
    String str_dob,str_social_sec,strMulImages,strTodayDate;
    private Calendar calendar;
    private int year, month, day;
    private ProgressDialog pdialog, pD;
    GeneralData gD;
    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day
            /*DatePickerDialog dialog = new DatePickerDialog(context, (DatePickerDialog.OnDateSetListener) arg0, year, month, day);
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, 1);
            Date newDate = c.getTime();
            dialog.getDatePicker().setMaxDate(newDate.getTime() - (newDate.getTime() % (24 * 60 * 60 * 1000)));*/
            //     Toast.makeText(ProviderCompanyInfoLicense.this, "hi", Toast.LENGTH_SHORT).show();
            showDate(arg1, arg2 + 1, arg3);
        }
    };
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };
    private GoogleApiClient client;
    int nScreenHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.provider_companyinfo_license);
            context = this;
            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = (int) ((float) nHeight / (float) 2.1);
            gD = new GeneralData(context);
            pdialog = new ProgressDialog(context);
            alertDialog = new AlertDialogManager();
            img_dob = (ImageView) findViewById(R.id.img_dob);
            img_license = (ImageView) findViewById(R.id.img_licenseUpload);
            img_back = (ImageView) findViewById(R.id.img_back);
            txt_dob = (TextView) findViewById(R.id.txt_disp_dob);
            ll_submit = (TextView) findViewById(R.id.llaySubmit);
            ed_social_sec = (EditText) findViewById(R.id.edSocialsecurityNo);
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            strTodayDate = dateFormat.format(date);
            ll_submit.setText("Submit");
            ed_social_sec.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (len > ed_social_sec.getText().length()) {
                        len--;
                        return;
                    }
                    len = ed_social_sec.getText().length();

                    if (len == 4 || len == 7) {
                        String number = ed_social_sec.getText().toString();
                        String dash = number.charAt(number.length() - 1) == '-' ? "" : "-";
                        number = number.substring(0, (len - 1)) + dash + number.substring((len - 1), number.length());
                        ed_social_sec.setText(number);
                        ed_social_sec.setSelection(number.length());
                    }
                }
            });

            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(context, "Unable to connect with Internet. Please check your internet connection and try again");
            }
            else
            {


            AfterLoginTask aft = new AfterLoginTask();
            aft.execute();}

            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);

            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            showDate(year, month + 1, day-2 );
            ll_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    clickevent();

                }
            });
            img_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            img_dob.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            ed_social_sec.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {




                        clickevent();




                        return true;
                    }
                    return false;
                }
            });
            img_license.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isLicenseUpload = true;

                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.custom_dialog_box);
                    dialog.setTitle("Alert Dialog View");
                    Button btnExit = (Button) dialog.findViewById(R.id.btnExit);
                    btnExit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.findViewById(R.id.btnChoosePath)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(ProviderLicenseEdit.this, GalleryAlbumActivity.class);
                                    i.putExtra("from","license");
                                    startActivity(i);
                                      /*  Intent i = new Intent(ProviderAfterLogin.this, AndroidCustomGalleryActivity.class);
                                        startActivity(i);*/
                                    dialog.dismiss();

                                }
                            });
                    dialog.findViewById(R.id.btnTakePhoto)
                            .setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    takePicture();
                                    dialog.dismiss();
                                }
                            });
                    dialog.show();
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
    public void clickevent() {
        try {
            if (!gD.isConnectingToInternet()) {
                Toast.makeText(ProviderLicenseEdit.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
            }
            else {

                Log.i("license", strMultipleImagePath);
                Log.i("dob", txt_dob.getText().toString());
                Log.i("social security", ed_social_sec.getText().toString().trim());

                str_dob = txt_dob.getText().toString().trim();
                str_social_sec = ed_social_sec.getText().toString().trim();
                strMulImages = gallery_file;


                Log.i("gallery", strMulImages);
                Log.i("company name", str_dob);
                Log.i("addr", str_social_sec);

                if (str_dob.length() > 0 && str_social_sec.length() > 0 && str_social_sec.length() > 10) {
                    //Create Asyntask and Send the isImageUpload,isLicenseUpload as a parameter and do further.

                    if(strTodayDate.equalsIgnoreCase(txt_dob.getText().toString().trim()))

                    {
                        Toast.makeText(ProviderLicenseEdit.this, "Please enter valid date of birth !", Toast.LENGTH_SHORT).show();
                    }

                    else {
                        String newStr = ed_social_sec.getText().toString().replaceAll("-", "");
                        Log.i("HH", "newStr" + newStr);
                        ProviderLicenseEditTask pCT = new ProviderLicenseEditTask(str_dob, str_social_sec, strMulImages);
                        pCT.execute();
                    }

                } else {

                    if (str_dob.length() == 0) {
                        Toast.makeText(ProviderLicenseEdit.this, "Date of birth should not be empty !", Toast.LENGTH_SHORT).show();
                    } else if (str_social_sec.length() == 0) {
                        Toast.makeText(ProviderLicenseEdit.this, "Social security number should not be empty !", Toast.LENGTH_SHORT).show();
                    } else if (str_social_sec.length() < 11) {
                        Toast.makeText(ProviderLicenseEdit.this, "Invalid social security number", Toast.LENGTH_SHORT).show();
                    }



                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {

            DatePickerDialog dialog = new DatePickerDialog(this, myDateListener, year, month, day );
            dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            return dialog;



        }
        return null;
    }

    private void showDate(int year, int month, int day) {





            txt_dob.setText(new StringBuilder().append(day).append("/")
                    .append(month).append("/").append(year));

    }



    private void takePicture() {

        try {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Log.i("MMM", "IOException");
                }
                if (photoFile != null) {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private File createImageFile() throws IOException {
        File image = null;
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            image = File.createTempFile(
                    imageFileName,  // prefix
                    ".jpg",         // suffix
                    storageDir      // directory
            );
            mCurrentPhotoPath = "file:" + image.getAbsolutePath();
            Log.i("capture paht:", mCurrentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        try {
            super.onResume();
            registerReceiver(broadcastReceiver, new IntentFilter("com.aryvart.multiselect"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);


            switch (requestCode) {

                case PICK_IMAGE:

                    Log.i("HH", "resultCode PICK_IMAGE case 1: " + resultCode);

                    try {
                        if (resultCode == RESULT_OK) {
                            AlFileList = new ArrayList<File>();
                            AlFileName = new ArrayList<String>();
                            Uri uri = data.getData();
                            if (uri == null) {
                                ClipData clipData = data.getClipData();
                                Log.d("HH", "Uri: " + resultCode);
                                if (clipData != null) {
                                    try {
                                        for (int i = 0; i < clipData.getItemCount(); i++) {
                                            String displayName = null;
                                            if (clipData.getItemCount() <= 10) {
                                                ClipData.Item item = clipData.getItemAt(i);
                                                Uri uriVal = item.getUri();
                                                String uriString = uriVal.toString();
                                                File myFile = new File(uriString);
                                                AlFileList.add(myFile);
                                                if (uriString.startsWith("content://")) {
                                                    Cursor cursor = null;
                                                    try {
                                                        cursor = context.getContentResolver().query(uriVal, null, null, null, null);
                                                        if (cursor != null && cursor.moveToFirst()) {
                                                            displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                                            File myFileVal = new File(uriString);
                                                            AlFileName.add(uriString);
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    } finally {
                                                        cursor.close();
                                                    }
                                                } else if (uriString.startsWith("file://")) {
                                                    displayName = myFile.getAbsolutePath();
                                                    AlFileName.add(displayName);
                                                }
                                                Log.i("HH", "AlFileName" + AlFileName.toString());
                                                Log.i("HH", "AlFileName" + AlFileName);
                                                Log.e("HH", "FileImages's Path:" + myFile.getAbsolutePath());
                                                Log.e("HH", "Images's Path:" + uriString);
                                            } else {
                                                Toast.makeText(ProviderLicenseEdit.this, "u reached max 10", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                strMultipleImagePath = AlFileName.toString();
                                Log.i("file path:", strMultipleImagePath);
                            } else {
                                try {
                                    String uriString = uri.toString();
                                    File myFile = new File(uriString);
                                    String path = myFile.getAbsolutePath();

                                    String displayName = null;

                                    AlFileList.add(myFile);

                                    if (uriString.startsWith("content://")) {
                                        Cursor cursor = null;
                                        try {
                                            cursor = context.getContentResolver().query(uri, null, null, null, null);
                                            if (cursor != null && cursor.moveToFirst()) {
                                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                                AlFileName.add(uriString);
                                            }
                                        } finally {
                                            cursor.close();
                                        }
                                    } else if (uriString.startsWith("file://")) {
                                        displayName = myFile.getAbsolutePath();
                                        AlFileName.add(displayName);
                                    }

                                    strMultipleImagePath = myFile.getAbsolutePath();
                                    Log.i("file path:", strMultipleImagePath);

                                    // edLicenseUpload.setText(strLicenseFilepath.substring(1, strLicenseFilepath.length() - 1));
                                } catch (Exception e) {

                                    e.printStackTrace();
                                }
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case 2:

                    Log.i("HH", "resultCode case 2: " + resultCode);

                    if (resultCode == RESULT_OK) {
                        Uri uri = data.getData();
                        Log.d("HH", "File Uri: " + uri.toString());
                        String path = null;
                        try {
                            path = FileUtils.getPath(this, uri);
                            strMultipleImagePath = path;
                            //edLicenseUpload.setText(stripExtension(strLicenseFilepath));
                            Log.d("HH", "File Path: " + strMultipleImagePath);
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case REQUEST_IMAGE_CAPTURE:
                    FileInputStream fis;
                    File file = null;
                    file = new File(Uri.parse(strMultipleImagePath).getPath());

                    fis = new FileInputStream(file);
                    str_capture_file_path = file.getAbsolutePath();
                    fis.close();

                    Log.i("SRV", "str_capture_file_path : " + str_capture_file_path);

                    //strMultipleImagePath = strVal;
                    break;

                default:
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateUI(Intent intent) {
        try {
            final String strVal = intent.getStringExtra("data");
            Log.i("VAL", "Path : " + strVal);

            strMultipleImagePath = "";

            ProviderLicenseEdit.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gallery_file = strVal.substring(1, strVal.length() - 1);
                    strMultipleImagePath = strVal;
                    Log.i("my file:", strMultipleImagePath);
                    //Using the Above path convert the path into file and send it to where ever you want
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();


    }



    class AfterLoginTask extends AsyncTask {
        String sResponse = null;

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                String charset = "UTF-8";
                String requestURL = gD.common_baseurl+"selectprovider.php";
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                multipart.addHeaderField("Content-Encoding", "gzip");
                multipart.addHeaderField("Accept", "application*/*");
                multipart.addHeaderField("Content-Encoding", "gzip");
                multipart.addHeaderField("Content-type", "application/x-www-form-urlencoded");

                multipart.addFormField("providerid", SplashActivity.sharedPreferences.getString("UID", null));

                List<String> response = multipart.finish();
                System.out.println("SERVER REPLIED:");
                StringBuilder sb = new StringBuilder();
                for (String line : response) {
                    System.out.println(line);
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
            gD.callload(context, nScreenHeight);


        }

        @Override
        protected void onPostExecute(Object sesponse) {
            try {
                JSONObject jsobj = new JSONObject(sResponse);
                if (jsobj.getInt("code") == 2) {
                    JSONArray jsArProviders = jsobj.getJSONArray("providers");
                    String dob = jsArProviders.getJSONObject(0).getString("dob");
                    String security_no = jsArProviders.getJSONObject(0).getString("socialsecurityno");
                    gallery_file = jsArProviders.getJSONObject(0).getString("license");
                    txt_dob.setText(dob);


                    String a=security_no.substring(0,3);
                    String ab=security_no.substring(3,5);
                    String ac=security_no.substring(5,8);

                    Log.i("HH","substring" + security_no );
                    Log.i("HH","substring--a--" + a );
                    Log.i("HH","substring--ab--" + ab );
                    Log.i("HH","substring--ac--" + ac );

                    String newstring = security_no.substring(0, 3) + "-" + security_no.substring(3,5) + "-" +security_no.substring(5,9);
                    ed_social_sec.setText(security_no);


                    if (gallery_file.trim().length() > 0) {
                        isLicenseUpload = true;
                        //    strMultipleImagePath = strGallery;
                        //    mCurrentPhotoPath = strGallery;
                    }

                }




                if(gD.alertDialog!=null) {
                    gD.alertDialog.dismiss();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }




    class ProviderLicenseEditTask extends AsyncTask {
        String sResponse = null;
        String str_dob, str_social_sec, strMulimages;

        public ProviderLicenseEditTask(String strDOB, String strSocialSec, String strMulImages) {
            str_dob = strDOB;
            str_social_sec = strSocialSec;
            strMulimages = strMulImages;



        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                String charset = "UTF-8";
                String requestURL = gD.common_baseurl+"editlicenseupload.php";
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);

                multipart.addHeaderField("Content-Encoding", "gzip");
                multipart.addHeaderField("Accept", "application*/*");
                multipart.addHeaderField("Content-Encoding", "gzip");
                multipart.addHeaderField("Content-type", "application/x-www-form-urlencoded");

                Log.i("TT", "strCname : " + str_dob);
                Log.i("TT", "strCaddress : " + str_social_sec);
                Log.i("TT", "strMulimages : " + strMulimages);

                multipart.addFormField("dob", str_dob);
                multipart.addFormField("socialsecurityno", str_social_sec);
                multipart.addFormField("providerid", SplashActivity.sharedPreferences.getString("UID", null));

                if (strMultipleImagePath.length() > 0) {
                    Log.i("Rr", " strMultipleImagePath : " + strMultipleImagePath);
                    String[] Sval = strMultipleImagePath.substring(1, strMultipleImagePath.length() - 1).split(",");
                    Log.i("Rr", " strMultipleImagePath after: " + Sval[0]);
                    for (int i = 0; i < Sval.length; i++) {
                        Log.i("Rr", "Get the Path *** : " + Sval[i]);
                        File sourceFile = new File(Sval[i].trim());
                        multipart.addFilePart("license[]", sourceFile);
                    }
                }

                if (mCurrentPhotoPath.length()>0)
                {
                    if (mCurrentPhotoPath.contains("file:"))
                    {
                        mCurrentPhotoPath=mCurrentPhotoPath.replaceAll("file:","");
                        mCurrentPhotoPath.trim();
                    }
                    if(mCurrentPhotoPath.contains(","))
                    {
                        Log.i("Rr", " strMultipleImagePath : " + mCurrentPhotoPath);
                        for (int i = 0; i < mCurrentPhotoPath.length(); i++) {
                            Log.i("Rr", "Get the Path : " + mCurrentPhotoPath);

                            File sourceFile = new File(mCurrentPhotoPath.trim());
                            Log.i("Rr", "sourceFile: " + sourceFile);
                            multipart.addFilePart("license[]", sourceFile.getAbsoluteFile());
                        }
                    }
                    else
                    {
                        File sourceFile = new File(mCurrentPhotoPath.trim());
                        Log.i("Rr", "sourceFile: " + sourceFile);
                        multipart.addFilePart("license[]", sourceFile.getAbsoluteFile());
                    }

                }





                List<String> response = multipart.finish();
                System.out.println("SERVER REPLIED:");
                StringBuilder sb = new StringBuilder();
                for (String line : response) {
                    System.out.println(line);
                    sb.append(line);
                }
                try {
                    JSONObject jsonObj = new JSONObject(sb.toString());
                    Log.i("SSS", "StrResp : " + jsonObj.toString());
                    sResponse = jsonObj.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                Log.i("NN", "i'm catched");
                e.printStackTrace();
            }
            return sResponse;
        }

        @Override
        protected void onPreExecute() {
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

                        tv_alert_Message.setText("Your License information changed successfully");

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
}

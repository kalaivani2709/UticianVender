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
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
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

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.bean.ImageItem;
import com.aryvart.uticianvender.customgallery.GalleryAlbumActivity;
import com.aryvart.uticianvender.genericclasses.AlertDialogManager;
import com.aryvart.uticianvender.genericclasses.FileUtils;
import com.aryvart.uticianvender.genericclasses.GeneralData;

import com.aryvart.uticianvender.utician.SignInActivity;
import com.aryvart.uticianvender.utician.SplashActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by android1 on 29/7/16.
 */
public class ProviderCompanyInfoLicense extends Activity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE = 3;
    ImageView img_dob, img_back, img_license;

    TextView txt_dob;
    ArrayList<ImageItem> alBitmap;
    ArrayList<ImageItem> alDeleteBmap;

    String strTodayDate;
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
    int nScreenHeight,nnScreenHeight;
    String str_dob, str_social_sec, strMulImages;
    GeneralData gD;

    private Calendar calendar;

    private int year, month, day;

    private ProgressDialog pdialog;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };
    private DatePickerDialog.OnDateSetListener myDateListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
            // TODO Auto-generated method stub
            // arg1 = year
            // arg2 = month
            // arg3 = day


            //     Toast.makeText(ProviderCompanyInfoLicense.this, "hi", Toast.LENGTH_SHORT).show();
            showDate(arg1, arg2 + 1, arg3);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.provider_companyinfo_license);
            context = this;
            pdialog = new ProgressDialog(context);
            alertDialog = new AlertDialogManager();
            gD = new GeneralData(context);
            SplashActivity.autoLoginPreference = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
            img_dob = (ImageView) findViewById(R.id.img_dob);
            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = (int) ((float) nHeight / (float) 2.1);
            nnScreenHeight = (int) ((float) nHeight / (float) 2.3);
            SplashActivity.sharedPreferences = getSharedPreferences("regid", Context.MODE_PRIVATE);

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = new Date();
            strTodayDate = dateFormat.format(date);
            Log.i("HI", "strTodayDate" + strTodayDate);
            img_license = (ImageView) findViewById(R.id.img_licenseUpload);
            img_back = (ImageView) findViewById(R.id.img_back);
            txt_dob = (TextView) findViewById(R.id.txt_disp_dob);
            ll_submit = (TextView) findViewById(R.id.llaySubmit);
            ed_social_sec = (EditText) findViewById(R.id.edSocialsecurityNo);
            ll_submit.setText("Next");
            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(context, "Unable to connect with Internet. Please check your internet connection and try again");
            }
            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);

            month = calendar.get(Calendar.MONTH);
            day = calendar.get(Calendar.DAY_OF_MONTH);
            showDate(year, month + 1, day);

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



            ll_submit.setOnClickListener(new View.OnClickListener()

                                         {
                                             @Override
                                             public void onClick(View v) {

                                                 clickevent();
                                             }
                                         }

            );
            img_back.setOnClickListener(new View.OnClickListener()

                                        {
                                            @Override
                                            public void onClick(View v) {

                                                popupcall("Do you want to logout",0,"back",nnScreenHeight);
                                               /* SharedPreferences.Editor editor = SplashActivity.autoLoginPreference.edit();
                                                editor.putString("uname", null);
                                                editor.putString("pwd", null);
                                                editor.commit();
                                                finish();*/
                                            }
                                        }

            );


            img_license.setOnClickListener(new View.OnClickListener()

                                           {
                                               @Override
                                               public void onClick(View v) {
                                                   //  isLicenseUpload = true;

                                                   final Dialog dialog = new Dialog(context);
                                                   dialog.setContentView(R.layout.custom_dialog_box);
                                                   //dialog.setTitle("Alert Dialog View");
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
                                                                   Intent i = new Intent(ProviderCompanyInfoLicense.this, GalleryAlbumActivity.class);
                                                                   i.putExtra("from","license");
                                                                   startActivity(i);
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

                                                   // show dialog on screen
                                                   dialog.show();
                                               }

                                           }

            );
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

public void clickevent()
{
    try {
        if (!gD.isConnectingToInternet()) {
            Toast.makeText(ProviderCompanyInfoLicense.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
        } else {
            str_dob = txt_dob.getText().toString().trim();
            str_social_sec = ed_social_sec.getText().toString().trim();
            strMulImages = gallery_file;


            if (str_dob.length() > 0 && str_social_sec.length() > 0 && str_social_sec.length() > 10) {
                if (strTodayDate.equalsIgnoreCase(txt_dob.getText().toString().trim()))

                {
                    Toast.makeText(ProviderCompanyInfoLicense.this, "Please enter valid date of birth !", Toast.LENGTH_SHORT).show();
                } else {
                    String newStr = ed_social_sec.getText().toString().replaceAll("-", "");
                    Log.i("HH", "newStr" + newStr);


                    CallProviderCompanyInfoLicense(str_dob, str_social_sec, strMulImages);
                }

            } else {

                if (str_dob.length() == 0) {
                    Toast.makeText(ProviderCompanyInfoLicense.this, "Date of birth should not be empty !", Toast.LENGTH_SHORT).show();
                } else if (str_social_sec.length() == 0) {
                    Toast.makeText(ProviderCompanyInfoLicense.this, "Social security number should not be empty !", Toast.LENGTH_SHORT).show();
                } else if (str_social_sec.length() < 11) {
                    Toast.makeText(ProviderCompanyInfoLicense.this, "Invalid social security number", Toast.LENGTH_SHORT).show();
                }
            }


        }
    } catch (Exception e) {
        e.printStackTrace();
    }


}
    private void CallProviderCompanyInfoLicense
(final String str_dob, final String str_social_sec, final String strMulImages) {
        gD.callload(context, nScreenHeight);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, gD.common_baseurl+"licenseupload.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {
                        Log.i("RAJ", "StrResp : " + response);


                        if (gD.alertDialog != null) {
                            gD.alertDialog.dismiss();
                        }
                        try {
                            //    JSONObject jsoBj = new JSONObject(response);


                            if (response != null) {


                                if (gD.alertDialog != null) {
                                    gD.alertDialog.dismiss();
                                }
                                JSONObject jsobj = new JSONObject(response);

                                if (jsobj.getInt("code") == 2) {
                                    //put value from response
                                    String strservicetype = jsobj.getJSONObject("provider_details").getString("services");
                                    SharedPreferences.Editor editor = SplashActivity.sharedPreferences.edit();
                                    editor.putString("serviceType", strservicetype);
                                    editor.commit();


                                    popupcall("You successfully added your information",1,"from_api", nScreenHeight);


                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                        NetworkResponse response = error.networkResponse;
                        if (error instanceof ServerError && response != null) {
                            try {
                                String res = new String(response.data,
                                        HttpHeaderParser.parseCharset(response.headers));

                                Toast.makeText(context, "res : " + res, Toast.LENGTH_LONG).show();

                                // Now you can use any deserializer to make sense of data
                                JSONObject obj = new JSONObject(res);
                            } catch (UnsupportedEncodingException e1) {
                                // Couldn't properly decode data to string
                                e1.printStackTrace();
                            } catch (JSONException e2) {
                                // returned data is not JSONObject?
                                e2.printStackTrace();
                            }
                        }

                    }
                }

        ) {

            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> licenseParams = new HashMap<String, String>();

                licenseParams.put("dob", str_dob);
                licenseParams.put("socialsecurityno", str_social_sec);
                licenseParams.put("providerid", SplashActivity.sharedPreferences.getString("UID", null));

                if (strMultipleImagePath.length() > 0) {
                    //begin
                    Log.i("Rr", " str_dob : " + str_dob);
                    Log.i("Rr", " socialsecurityno : " + str_social_sec);
                    Log.i("Rr", " strMultipleImagePath : " + strMultipleImagePath);
                    String[] Sval;
                    if (strMultipleImagePath.contains("[")) {
                        Sval = strMultipleImagePath.substring(1, strMultipleImagePath.length() - 1).split(",");
                        //Log.i("Rr", " strMultipleImagePath after: " + Sval[0]);
                        String strMultipleImage = "";
                        for (int i = 0; i < Sval.length; i++) {
                            Log.i("Rr", "Get the Path : " + Sval[i]);
                            File sourceFile = new File(Sval[i].trim());

                            Bitmap bitmap;
                            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                            bitmap = BitmapFactory.decodeFile(sourceFile.getAbsolutePath(),
                                    bitmapOptions);
                            strMultipleImage += getStringImage(bitmap);
                        }
                        if (strMulImages.contains(",")) {
                            strMultipleImage = strMultipleImage.substring(0, strMultipleImage.length() - 1);
                        }
                        Log.i("Rr", "strMultipleImage: " + strMultipleImage);
                        licenseParams.put("license", strMultipleImage);
                    } else {
     
                        //Log.i("Rr", " strMultipleImagePath after: " + Sval[0]);
                        String strMultipleImage = "";

                        Log.i("Rr", "Get the Path : " + strMultipleImagePath);
                        File sourceFile = new File(strMultipleImagePath.trim());

                       /* Bitmap bitmap;
                        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                        bitmap = BitmapFactory.decodeFile(sourceFile.getAbsolutePath(),
                                bitmapOptions);
                        strMultipleImage = getStringImage(bitmap);*/


                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                        Bitmap bitmap = BitmapFactory.decodeFile(sourceFile.getPath(), options);

                        strMultipleImage = getStringImage(bitmap);
                        Log.i("Rr", "strMultipleImage: " + strMultipleImage);
                        licenseParams.put("license", strMultipleImage);


                    }
                    //End
                }
                if (mCurrentPhotoPath.length() > 0) {
                    //begin
                    Log.i("Rr", " strMultipleImagePath : " + mCurrentPhotoPath);

                    String strMCurrentPath = "";
                    for (int i = 0; i < mCurrentPhotoPath.length(); i++) {
                        Log.i("Rr", "Get the Path : " + mCurrentPhotoPath);
                        File sourceFile = new File(mCurrentPhotoPath.trim());


                        Bitmap bitmap;
                        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                        bitmap = BitmapFactory.decodeFile(sourceFile.getAbsolutePath(),
                                bitmapOptions);
                        strMCurrentPath += getStringImage(bitmap);


                    }
                    if (strMCurrentPath.contains(",")) {
                        strMCurrentPath = strMCurrentPath.substring(0, strMCurrentPath.length() - 1);
                    }
                    licenseParams.put("license", strMCurrentPath);

                    //End
                }


                Map<String, String> params = licenseParams;

                params.put("Content-Encoding", "gzip");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                // params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }

        };

//   Object[] strResp1 = gD.CommonHTTP_post_URL_Call(context, "http://aryvartdev.com/projects/surf27/api/categorynews.php", hsParameters, "news", lvNews);

        RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);


        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }


    public String getStringImage(Bitmap bmp) {
        String encodedImage = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodedImage + ",";
    }


    private void takePicture() {


        try {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.i("MMM", "IOException");
                }
                // Continue onlactiveTakePhotoy if the File was successfully created
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
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            image = File.createTempFile(
                    imageFileName,  // prefix
                    ".jpg",         // suffix
                    storageDir      // directory
            );

            // Save a file: path for use with ACTION_VIEW intents
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
                            //If uri null its multiple file/image choosen
                            if (uri == null) {
                                ClipData clipData = data.getClipData();

                                Log.d("HH", "Uri: " + resultCode);

                                if (clipData != null) {
                                    try {
                                        for (int i = 0; i < clipData.getItemCount(); i++) {
                                            String displayName = null;
                                            if (clipData.getItemCount() <= 10) {
                                                //      Toast.makeText(ProviderAfterLogin.this, "count" + clipData.getItemCount(), Toast.LENGTH_SHORT).show();
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

                                                //  edLicenseUpload.setText(AlFileName.toString().substring(1, AlFileName.toString().length() - 1));

                                                Log.i("HH", "AlFileName" + AlFileName.toString());
                                                Log.i("HH", "AlFileName" + AlFileName);
                                                Log.e("HH", "FileImages's Path:" + myFile.getAbsolutePath());
                                                Log.e("HH", "Images's Path:" + uriString);
                                            } else {
                                                Toast.makeText(ProviderCompanyInfoLicense.this, "u reached max 10", Toast.LENGTH_SHORT).show();
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

                                                //  File myFileVal = new File(uriString);
                                                AlFileName.add(uriString);

                                                //  AlFileName.add(uriString);
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

                        //  else if (requestCode == FILE_SELECT_CODE && resultCode == RESULT_OK) {

                        // Get the Uri of the selected file
                        Uri uri = data.getData();
                        Log.d("HH", "File Uri: " + uri.toString());
                        // Get the path
                        String path = null;
                        try {
                            path = FileUtils.getPath(this, uri);


                            strMultipleImagePath = path;

                            //edLicenseUpload.setText(stripExtension(strLicenseFilepath));

                            Log.d("HH", "File Path: " + strMultipleImagePath);

                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }

                        // Get the file instance
                        // File file = new File(path);
                        // Initiate the upload
                    }
                    break;
                case REQUEST_IMAGE_CAPTURE:

                    strMulImages = "";

                    Log.i("SRV", "str_capture_file_path : " + mCurrentPhotoPath);

                    alBitmap = new ArrayList<ImageItem>();
                    alDeleteBmap = new ArrayList<ImageItem>();


                    File files = new File(mCurrentPhotoPath);

                    Log.i("SRV", "files : " + files.toString());


                  /*  ImageItem ImgI = new ImageItem();
                    ImgI.setBmpImage(getThumbnail(Uri.parse(files.toString())));
                    ImgI.setImage(mCurrentPhotoPath);
                    alBitmap.add(ImgI);
*/


                    strMultipleImagePath = files.getAbsolutePath();

                    Log.i("SRV", "str_capture_new_strMultipleImagePath: " + strMultipleImagePath);

                    if (strMultipleImagePath.contains("/file:")) {
                        strMultipleImagePath = strMultipleImagePath.replace("/file:", "");

                    }
                    gallery_file = strMultipleImagePath.trim();
                    Log.i("SRV", "gallery_file: " + gallery_file);
                    //strMultipleImagePath = strVal;
                    mCurrentPhotoPath = "";
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

            ProviderCompanyInfoLicense.this.runOnUiThread(new Runnable() {
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
    public void onBackPressed() {




        popupcall("Do you want to logout",0,"back", nnScreenHeight);

    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {


            DatePickerDialog dialog = new DatePickerDialog(this, myDateListener, year, month, day);
            dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
            return dialog;


        }
        return null;
    }

    private void showDate(int year, int month, int day) {
        txt_dob.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

public void popupcall(String s, int i, final String str_res, int size_l)
{


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

        tv_alert_Message.setText(s);

        Button btn_submit = (Button) dialogLayout.findViewById(R.id.ok);
        Button btn_cancel = (Button) dialogLayout.findViewById(R.id.cancel);


        FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, size_l);
        llayAlert.setLayoutParams(lparams);
        // int count = 1;
        if (i == 1) {
            btn_submit.setText("OK");
            btn_cancel.setVisibility(View.GONE);
        }
        else
        {
            btn_submit.setText("YES");
            btn_cancel.setText("NO");


            btn_submit.setVisibility(View.VISIBLE);
            btn_cancel.setVisibility(View.VISIBLE);
        }


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(str_res.equalsIgnoreCase("from_api"))
                {
                    Intent user_category = new Intent(ProviderCompanyInfoLicense.this, ProviderCompanyInfoPay.class);
                    user_category.putExtra("screenFrom", 1);
                    startActivity(user_category);
                    alertDialog.dismiss();
                    finish();
                    finishAffinity();

                }
                else
                {
                    SharedPreferences.Editor editor = SplashActivity.autoLoginPreference.edit();
                    editor.putString("uname", null);
                    editor.putString("pwd", null);
                    editor.commit();

                    Intent user_category = new Intent(ProviderCompanyInfoLicense.this, SignInActivity.class);

                    startActivity(user_category);
                    alertDialog.dismiss();
                    finish();
                }



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

}

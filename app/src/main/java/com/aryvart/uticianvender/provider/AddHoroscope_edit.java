package com.aryvart.uticianvender.provider;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.adapter.Custom_GridView_Adapter;
import com.aryvart.uticianvender.bean.ImageItem;
import com.aryvart.uticianvender.customgallery.GalleryAlbumActivity;
import com.aryvart.uticianvender.genericclasses.AlertDialogManager;
import com.aryvart.uticianvender.genericclasses.FileUtils;
import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.aryvart.uticianvender.genericclasses.MultipartUtility;

import com.aryvart.uticianvender.imageCache.ImageLoader;
import com.aryvart.uticianvender.utician.SplashActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by Androidwin on 1/25/2017.
 */

public class AddHoroscope_edit extends Activity  {


    AlertDialogManager alertDialog;



//edi

    private static final int FILE_SELECT_CODE = 0;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE = 3;
    public static SharedPreferences idprefadd;
    String strlocaddress, strloclatitude, strloclongtitude;
    String id;

    AlertDialogManager alert = new AlertDialogManager();
    String st_company_name, st_company_address;
    EditText et_company_name, et_company_address;
    ArrayList<File> AlFileList;
    GeneralData gD;

    ImageLoader imgLoader;
    Context context;
    ProgressBar progressBar2;
    SharedPreferences.Editor editor;

    String strArrayDownloadGalleryPath;
    EditText edtupload_image;
    String strMultipleImagePath = "", mCurrentPhotoPath = "";
    TextView submit;
    boolean isImageUpload = false;
    String strCompanyName, strCompanyAddress, strGallery;
    ImageView imvSelMultipleimages, img_back;
    ArrayList<String> AlFileName;
    int x=2;


    //new client reuirement
    GridView gV;
    Custom_GridView_Adapter cGAdapter;
    ArrayList<ImageItem> alBitmap;
    ArrayList<ImageItem> alDeleteBmap;
    String strMulImages;
    boolean isImageEditDelete = false;
    private ProgressDialog pD;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };


    int nScreenHeight;

ImageView providerimage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.provider_company_inforedit);

            context = this;
            gD = new GeneralData(context);
            imgLoader = new ImageLoader(context);
            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = (int) ((float) nHeight / (float) 2);
            //registration id
            SplashActivity.sharedPreferences = getSharedPreferences("regid", Context.MODE_PRIVATE);
            SplashActivity.autoLoginPreference = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
            imvSelMultipleimages = (ImageView) findViewById(R.id.imv_imageUpload);
            /*imvUploadLicense = (ImageView) findViewById(R.id.imv_licenseUpload);*/
            et_company_name = (EditText) findViewById(R.id.edCompanyName);
            edtupload_image = (EditText) findViewById(R.id.edImages);
            img_back = (ImageView) findViewById(R.id.img_back);
            img_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            et_company_address = (EditText) findViewById(R.id.editWorkLocation);
            submit = (TextView) findViewById(R.id.llaySubmit);
            providerimage = (ImageView) findViewById(R.id.img_logo);
            submit.setText("Submit");
            edtupload_image.setKeyListener(null);
            imvSelMultipleimages = (ImageView) findViewById(R.id.imv_imageUpload);

            gV = (GridView) findViewById(R.id.gridview);

            progressBar2 = (ProgressBar) findViewById(R.id.progressBar);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.app_green));
            }
            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(context, "Unable to connect with Internet. Please check your internet connection and try again");
            }
else
            {
            AfterLoginTask aft = new AfterLoginTask();
            aft.execute();}

            imvSelMultipleimages.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isImageUpload = true;

                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.custom_dialog_box);
                    //  dialog.setTitle("Alert Dialog View");
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
                                    Intent i = new Intent(AddHoroscope_edit.this, GalleryAlbumActivity.class);

                                    i.putExtra("from","gallery");
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

                    // show dialog on screen
                    dialog.show();
                }

            });
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    strCompanyName = et_company_name.getText().toString().trim();
                    strCompanyAddress = et_company_address.getText().toString().trim();
                    // strMulImages = ed_disp_images.getText().toString();
                    if (strCompanyName.length() > 0 && strCompanyAddress.length() > 0 && alBitmap.size() > 0) {

                        Log.i("LK", "connected");
                        strMulImages = mCurrentPhotoPath;
                        if (alBitmap.size() > 0) {
                            ArrayList<ImageItem> union = new ArrayList();

                            alBitmap.removeAll(new HashSet(alDeleteBmap));

                            Log.i("MYND", "alBitmap.size() : " + alBitmap.size());


                            strMulImages = "";

                            for (int i = 0; i < alBitmap.size(); i++) {
                                strMulImages += alBitmap.get(i).getImage() + ",";

                            }

                            if (strMulImages.contains(",")) {
                                strMulImages = strMulImages.substring(0, strMulImages.length() - 1);
                            }

                            Log.i("MYND", "IF Values : " + strMulImages);
                            Log.i("GG","Hi hello");

                            // CallProviderCompanyInfoLicense(strCompanyName, strCompanyAddress, strMulImages);

                            //Toast.makeText(AddHoroscope.this, strMulImages, Toast.LENGTH_SHORT).show();
                          uploadImage(strCompanyName,strCompanyAddress);
                        }
                    }

                    else {
                        if (strCompanyName.length() == 0) {
                            Toast.makeText(AddHoroscope_edit.this, "Company name should not be empty !", Toast.LENGTH_SHORT).show();
                        } else if (strCompanyAddress.length() == 0) {
                            Toast.makeText(AddHoroscope_edit.this, "Company address should not be empty !", Toast.LENGTH_SHORT).show();
                        } else if (alBitmap.size() == 0) {
                            Log.i("AA", "firstttttt");
                            // showAlertDialog(context, "Do u want continue without adding image", str_dob, str_social_sec, strMulImages);
                            Log.i("AA", "second");

                          //  Toast.makeText(AddHoroscope_edit.this, "Gallery should not be empty !", Toast.LENGTH_SHORT).show();
                            LayoutInflater inflater = LayoutInflater.from(context);
                            View dialogLayout = inflater.inflate(R.layout.addservice_popup, null);


                            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
                            alertDialogBuilder.setView(dialogLayout);
                            alertDialogBuilder.setCancelable(true);


                            final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                            //  alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                            alertDialog.show();

                            LinearLayout llayAlert = (LinearLayout) dialogLayout.findViewById(R.id.llayalertDialog);
                            // TextView tv_alert_Title = (TextView) dialogLayout.findViewById(R.id.tv_alert_Title);
                            TextView tv_alert_Message = (TextView) dialogLayout.findViewById(R.id.message);

                            tv_alert_Message.setText("You sure you don't want to add any photos?");

                            Button btn_submit = (Button) dialogLayout.findViewById(R.id.ok);
                            Button btn_cancel = (Button) dialogLayout.findViewById(R.id.cancel);


                            FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, nScreenHeight);
                            llayAlert.setLayoutParams(lparams);


                            btn_submit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    uploadImage(strCompanyName,strCompanyAddress);

                                }

                            });


                            btn_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    alertDialog.dismiss();

                                }

                            });

                        }


                    }

                }
            });
            gV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ImageItem img = alBitmap.get(position);
                    alDeleteBmap.add(img);
                    cGAdapter.aBtmap.remove(position);
                    cGAdapter.notifyDataSetChanged();

                    if (alBitmap.size() == 0) {
                        //edttext empty
                    }


                }
            });
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }


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
                multipart.addHeaderField("User-Agent", "rajaji");
                multipart.addHeaderField("Test-Header", "Header-Value");
                multipart.addFormField("keywords", "Java,upload,Spring");
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

                    if(gD.alertDialog!=null) {
                        gD.alertDialog.dismiss();
                    }                    alBitmap = new ArrayList<ImageItem>();
                    alDeleteBmap = new ArrayList<ImageItem>();

                    JSONArray jsArProviders = jsobj.getJSONArray("providers");
                    strCompanyName = jsArProviders.getJSONObject(0).getString("companyname");
                    strCompanyAddress = jsArProviders.getJSONObject(0).getString("companyaddress");
                    String strProviderName = jsArProviders.getJSONObject(0).getString("providername");
                    String strimagePath = jsArProviders.getJSONObject(0).getString("image_path");


                    strGallery = jsArProviders.getJSONObject(0).getString("image");
                    strloclatitude = jsArProviders.getJSONObject(0).getString("latitude");
                    strloclongtitude = jsArProviders.getJSONObject(0).getString("longitude");


                    strArrayDownloadGalleryPath = strGallery;

                    et_company_name.setText(strCompanyName);
                    et_company_address.setText(strCompanyAddress);
                    //edtupload_image.setText(strGallery);
                    edtupload_image.setText("");
                    if (strGallery.trim().length() > 0) {
                        isImageUpload = false;

                    }



                   LoadImageTask lIT = new LoadImageTask();
                    lIT.execute();

                }






            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
    private class LoadImageTask extends AsyncTask {

        @Override
        protected void onPreExecute() {
           // gD.callload(context, nScreenHeight);

            progressBar2.setVisibility(View.VISIBLE);




        }
        @Override
        protected Object doInBackground(Object[] params) {
            String[] strImagesAr = strGallery.split(",");

            for (int i = 0; i < strImagesAr.length; i++) {
                if (strImagesAr[i].length() > 0) {

                    try {

                        String strGalPath = "";
                        ImageItem ImgI = new ImageItem();
                        Log.i("LL","images-"+strImagesAr[i]);
                        if (strImagesAr[i].contains("rn_")) {
                            strGalPath = gD.common_baseurl+"photogallery/" + URLEncoder.encode(strImagesAr[i].replace("rn", ""), "UTF-8");
                            ImgI.setImage(strImagesAr[i].replace("rn", ""));
                        } else {
                            strGalPath = gD.common_baseurl+"photogallery/" + URLEncoder.encode(strImagesAr[i], "UTF-8");
                            ImgI.setImage(strImagesAr[i]);
                        }
                        Log.i("MYND", "strGalPath : " + strGalPath);
                        // Bitmap bitmap = BitmapFactory.decodeStream((InputStream) new URL(strGalPath).getContent());
                        ImgI.setBmpImage(imgLoader.getBitmap(strGalPath));

                        alBitmap.add(ImgI);
/*
                        Thread timethread = new Thread() {

                            public void run() {
                                try {
                                    sleep(7000);
                                    if(gD.alertDialog!=null) {
                                        gD.alertDialog.dismiss();
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        timethread.start();*/
                      /*  if(gD.alertDialog!=null) {
                            gD.alertDialog.dismiss();
                        }*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return alBitmap;
        }



        @Override
        protected void onPostExecute(Object o) {

            progressBar2.setVisibility(View.GONE);
           /* if(gD.alertDialog!=null) {
                gD.alertDialog.dismiss();
            }*/
            cGAdapter = new Custom_GridView_Adapter(context, alBitmap);
            gV.setAdapter(cGAdapter);
        }
    }

    private void updateUI(final Intent intent) {
        try {
            final String strVal = intent.getStringExtra("data");
            Log.i("VAL", "Path : " + strVal);

            strMultipleImagePath = "";

            AddHoroscope_edit.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    alBitmap = new ArrayList<ImageItem>();
                    alDeleteBmap = new ArrayList<ImageItem>();

                    //   ed_disp_images.setText(strVal.substring(1, strVal.length() - 1));
                    strMultipleImagePath = strVal;
                    Log.i("MYND", strMultipleImagePath);

                    String[] strImagesAr = strVal.substring(1, strVal.length() - 1).split(",");

                    for (int i = 0; i < strImagesAr.length; i++) {
                        File fff = new File(strImagesAr[i]);
                        try {
                            ImageItem ImgI = new ImageItem();
                            Log.i("file", "file://" + fff.toString());

                            ImgI.setBmpImage(getThumbnail(Uri.parse("file://" + fff.toString())));

                            //ImgI.setBmpImage(loadBitmap("file://" + fff.toString()));
                            ImgI.setImage(strImagesAr[i]);
                            alBitmap.add(ImgI);
                            Log.i("RR", "file://" + alBitmap.size());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    cGAdapter = new Custom_GridView_Adapter(context, alBitmap);
                    gV.setAdapter(cGAdapter);


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    protected void onResume() {
        try {
            if (gD.pro_searchAddress != null && gD.pro_searchLatitutde != null && gD.pro_searchLongitude != null) {
                strlocaddress = gD.pro_searchAddress;
                strloclatitude = gD.pro_searchLatitutde;
                strloclongtitude = gD.pro_searchLongitude;
                et_company_address.setText(strlocaddress);
            }
            super.onResume();
            registerReceiver(broadcastReceiver, new IntentFilter("com.aryvart.multiselect"));

        } catch (Exception e) {
            e.printStackTrace();
        }
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
            mCurrentPhotoPath = image.getAbsolutePath();
            //  ed_disp_images.setText(mCurrentPhotoPath);
            Log.i("capture paht:", mCurrentPhotoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);


            switch (requestCode) {

                case PICK_IMAGE:
                    mCurrentPhotoPath = "";
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
                                                Toast.makeText(AddHoroscope_edit.this, "u reached max 10", Toast.LENGTH_SHORT).show();
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

                 /*     FileInputStream fis;
                    File file = null;
                    file = new File(mCurrentPhotoPath);
//
                  fis = new FileInputStream(file);
                    str_capture_file_path = file.getAbsolutePath();
                    fis.close();
*/
                    // Log.i("SRV", "str_capture_file_path : " + file.getAbsolutePath());


                  /*  ImageItem ImgI = new ImageItem();
                    ImgI.setBmpImage(getThumbnail(Uri.parse(file.toString())));
                    ImgI.setImage("file://" + mCurrentPhotoPath);
                    alBitmap.add(ImgI);


                    cGAdapter = new Custom_GridView_Adapter(context, alBitmap);
                    gV.setAdapter(cGAdapter);

*/
                    Log.i("SRV", "str_capture_file_path : " + mCurrentPhotoPath);

                    alBitmap = new ArrayList<ImageItem>();
                    alDeleteBmap = new ArrayList<ImageItem>();


                    File files = new File("file://" + mCurrentPhotoPath);

                    Log.i("SRV", "files : " + files.toString());

                    ImageItem ImgI = new ImageItem();
                    ImgI.setBmpImage(getThumbnail(Uri.parse(files.toString())));
                    ImgI.setImage(mCurrentPhotoPath);
                    alBitmap.add(ImgI);

                    Log.i("RR", "file://" + alBitmap.size());
                    cGAdapter = new Custom_GridView_Adapter(context, alBitmap);
                    gV.setAdapter(cGAdapter);


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

    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException {
        Bitmap bitmap = null;
        try {
            InputStream input = getContentResolver().openInputStream(uri);

            BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
            onlyBoundsOptions.inJustDecodeBounds = true;
            onlyBoundsOptions.inDither = true;//optional
            onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
            BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
            input.close();
            if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
                return null;

            int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

            double ratio = (originalSize > 300) ? (originalSize / 300) : 1.0;

            BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
            bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
            bitmapOptions.inDither = true;//optional
            bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
            input = this.getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
    }

    public String getStringImage(Bitmap bmp) {
        String encodedImage = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encodedImage;
    }

    private void uploadImage(final String strCname, final String strCaddress) {
        //Showing the progress dialog
        gD.callload(context, nScreenHeight);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,  gD.common_baseurl+"editcompanyinfo.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //    JSONObject jsoBj = new JSONObject(response);

                            Log.i("KAL", "StrResp : " + response);

                            if (response != null) {


                                if (gD.alertDialog != null) {
                                    gD.alertDialog.dismiss();
                                }
                                final JSONObject jsobj = new JSONObject(response);



                                Log.i("KAL", "StrResp--> : " + jsobj.toString());
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

                                    tv_alert_Message.setText("You have successfully updated your information");

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


                                            String address = null;
                                            try {
                                                address = jsobj.getJSONObject("provider_details").getString("companyaddress");

                                                SharedPreferences.Editor edit = SplashActivity.sharedPreferences.edit();
                                                edit.putString("companyname", jsobj.getJSONObject("provider_details").getString("companyname"));
                                                edit.commit();

                                                finish();
                                                alertDialog.dismiss();

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            Log.e("UI", address);
                                        }

                                    });

                                }

                                else
                                {
                                    Log.i("KAL", "StrResp--> : " + "fails");

                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        if (gD.alertDialog != null) {
                            gD.alertDialog.dismiss();
                        }
                        //Showing toast
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                //String image = getStringImage(bmp);

                //Getting Image Name


                //Creating parameters
                String str_get_image = "";
                for (int j = 0; j < alBitmap.size(); j++) {
                    Log.i("MYND", "Value : " + alBitmap.get(j).getBmpImage());

                    str_get_image += getStringImage(alBitmap.get(j).getBmpImage()) + ",";
                }

                if (str_get_image.contains(",")) {
                    str_get_image = str_get_image.substring(0, str_get_image.length() - 1);
                }

                Log.i("DA", "Value : " + str_get_image);


                Map<String, String> params = new Hashtable<String, String>();

                Log.i("TT", "strCname : " + strCname.trim());
                Log.i("TT", "strCaddress : " + strCaddress.trim());

                Log.i("TT", "latitude : " + strloclatitude.trim());
                Log.i("TT", "longitude : " + strloclongtitude.trim());
                Log.i("TT", "files : " + str_get_image);
                Log.i("TT", "id : " + SplashActivity.sharedPreferences.getString("UID", null));
                params.put("companyname",  strCname.trim());
                params.put("companyaddress",  strCaddress.trim());
                params.put("latitude",  strloclatitude.trim());
                params.put("longitude",  strloclongtitude.trim());
                params.put("providerid", SplashActivity.sharedPreferences.getString("UID", null));
                if(str_get_image.equalsIgnoreCase(""))
                {
                    params.put("remove", "1");
                }
                else {
                    params.put("files", str_get_image);
                }



                //returning parameters
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

         /*       // params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Content-Encoding", "gzip");*/
                return params;
            }
            @Override protected void deliverResponse(String response) {
                super.deliverResponse(response);
            }


            @Override public void deliverError(VolleyError error)
            {
                super.deliverError(error);
                Log.e("deliverResponse", "getNetworkTimeMs : " + error);
            }

        };



        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
        RetryPolicy policy = new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 48,
                x, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }


    @Override
    public void onBackPressed() {
        strlocaddress = "";
        super.onBackPressed();
    }



}

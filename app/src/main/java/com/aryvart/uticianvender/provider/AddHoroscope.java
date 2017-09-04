package com.aryvart.uticianvender.provider;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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
import com.aryvart.uticianvender.imageCache.ImageLoader;
import com.aryvart.uticianvender.utician.SignInActivity;
import com.aryvart.uticianvender.utician.SplashActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;



/**
 * Created by android2 on 16/6/16.
 */
public class AddHoroscope extends Activity  {

    String str_span = "Email your horoscope to horoscope@easy-marry.com or send it to any of our offices mentioning your Matrimony ID";
    TextView txt_span;
    Button btn_rate;
    int x=2;// retry count
    DrawerLayout myTabsDrawer;
    LinearLayout ll_parent_menu, ll_my_profile;
    LinearLayout myTabsSliderLayout, matchesLayout, mailboxLayout, dailyrecomLayout, editProLayout, upgradeLayout, settingsLayout, feedbackLayout, searchLayout, addhoroLAyout;
    private ViewPager viewPager;
    TextView txt_mymatches, txt_mailbox, txt_dailyrecom, txt_edit_profile, txt_upgrade_acc, txt_settings, txt_feedback, txt_search, txt_add_horo;
    ImageView img_menu, img_messenger, img_mymatches_grey, img_mailbox_grey, img_dailyrecom_grey, img_editprof_grey, img_upgradeacc_grey, img_settings_grey, img_feedback_grey, img_search_grey, img_horo_grey;

  //  ImageView img_edit_my_profile;
    Context ctx;
    ImageLoader imgLoader;
    ProgressBar pg_profile_comp_level;
    TextView txt_level_percent, txt_name, txt_id, txt_terms;

    String str_user_id, str_image, str_name, str_prof_compl_level, str_easy_marry_id, str_rate, mCurrentPhotoPath = "",
            strMultipleImagePath = "", strMulImages,str_edit_from;
    Button btn_choose_file, btn_add_horoscope;
    boolean isImageUpload = false;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int PICK_IMAGE = 3;
    ArrayList<File> AlFileList;
    ArrayList<String> AlFileName;
    GridView gV;
    Custom_GridView_Adapter cGAdapter;
TextView txt_title_txt,txt_horo_images_error;
    ArrayList<ImageItem> alBitmap;
    ArrayList<ImageItem> alDeleteBmap;
    String str_horo_id,str_horo_images;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };
    String[] strGalleryURL;
    IntentFilter filter1;
    TextView txt_copyright;


    String strCompanyName, strCompanyAddress;


    TextView ll_submit;
    EditText ed_disp_images, edCompanyAddress, edCompanyName;
    String strlocaddress, strloclatitude, strloclongtitude;
    AlertDialogManager alertDialog;
    GeneralData gD;
    int nScreenHeight,nnScreenHeight;

    ImageView img_imageUpload, img_back;


    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE,
            Manifest.permission.CAMERA, Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_WIFI_STATE
    };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.provider_companyinfo_new);

            ctx = this;
            gD = new GeneralData(ctx);

            verifyStoragePermissions(this);
            img_back = (ImageView) findViewById(R.id.img_back);
            edCompanyAddress = (EditText) findViewById(R.id.editWorkLocation);
            ed_disp_images = (EditText) findViewById(R.id.edImages);

            ed_disp_images.setKeyListener(null);
            edCompanyName = (EditText) findViewById(R.id.edCompanyName);
            imgLoader = new ImageLoader(ctx);

            img_imageUpload = (ImageView) findViewById(R.id.imv_imageUpload);
            ll_submit = (TextView) findViewById(R.id.llaySubmit);
            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = (int) ((float) nHeight / (float) 2.1);
            nnScreenHeight = (int) ((float) nHeight / (float) 2.3);
            gV = (GridView) findViewById(R.id.gridview);
            ll_submit.setText("Next");
            SplashActivity.autoLoginPreference = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);

            SplashActivity.sharedPreferences = getSharedPreferences("regid", Context.MODE_PRIVATE);
            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(ctx, "Unable to connect with Internet. Please check your internet connection and try again");
            }
            img_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    popupcall("Do you want to logout",0,"back");
                      /*  SharedPreferences.Editor editor = SplashActivity.autoLoginPreference.edit();
                        editor.putString("uname", null);
                        editor.putString("pwd", null);
                        editor.commit();
                        finish();*/
                }
            });
            img_imageUpload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isImageUpload = true;

                    final Dialog dialog = new Dialog(ctx);
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
                                    Intent i = new Intent(AddHoroscope.this, GalleryAlbumActivity.class);
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
            ll_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    strCompanyName = edCompanyName.getText().toString().trim();
                    strCompanyAddress = edCompanyAddress.getText().toString().trim();
                    // strMulImages = ed_disp_images.getText().toString();

                    if(alBitmap!=null ) {
                        if (strCompanyName.length() > 0 && strCompanyAddress.length() > 0 && alBitmap.size()>0) {

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

                                // CallProviderCompanyInfoLicense(strCompanyName, strCompanyAddress, strMulImages);

                                //Toast.makeText(AddHoroscope.this, strMulImages, Toast.LENGTH_SHORT).show();
                                uploadImage(strCompanyName, strCompanyAddress);
                            }
                        }

                        else if(alBitmap.size()==0)
                        {
                            LayoutInflater inflater = LayoutInflater.from(ctx);
                            View dialogLayout = inflater.inflate(R.layout.addservice_popup, null);


                            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(ctx);
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
                                    uploadImage(strCompanyName, strCompanyAddress);

                                }

                            });


                            btn_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {


                                    alertDialog.dismiss();

                                }

                            });

                        }
                        else if (strCompanyName.length() == 0) {
                            Toast.makeText(AddHoroscope.this, "Company name should not be empty !", Toast.LENGTH_SHORT).show();
                        }else if (strCompanyAddress.length() == 0) {
                            Toast.makeText(AddHoroscope.this, "Company address should not be empty !", Toast.LENGTH_SHORT).show();
                        }




                    }
                        else {
                            if (strCompanyName.length() == 0) {
                                Toast.makeText(AddHoroscope.this, "Company name should not be empty !", Toast.LENGTH_SHORT).show();
                            }else if (strCompanyAddress.length() == 0) {
                                Toast.makeText(AddHoroscope.this, "Company address should not be empty !", Toast.LENGTH_SHORT).show();
                            }

                            else {
                                Log.i("AA", "firstttttt");
                                // showAlertDialog(context, "Do u want continue without adding image", str_dob, str_social_sec, strMulImages);
                                Log.i("AA", "second");

                               // Toast.makeText(AddHoroscope.this, "Gallery should not be empty !", Toast.LENGTH_SHORT).show();
                                LayoutInflater inflater = LayoutInflater.from(ctx);
                                View dialogLayout = inflater.inflate(R.layout.addservice_popup, null);


                                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(ctx);
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
                                        uploadImage(strCompanyName, strCompanyAddress);

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

            /*     else{


                        if (strCompanyName.length() == 0) {
                            Toast.makeText(AddHoroscope.this, "Company name should not be empty !", Toast.LENGTH_SHORT).show();
                        }else if (strCompanyAddress.length() == 0) {
                            Toast.makeText(AddHoroscope.this, "Company address should not be empty !", Toast.LENGTH_SHORT).show();
                        }

                        else {
                            Log.i("AA", "firstttttt");
                            // showAlertDialog(context, "Do u want continue without adding image", str_dob, str_social_sec, strMulImages);
                            Log.i("AA", "second");

                            Toast.makeText(AddHoroscope.this, "Gallery should not be empty !", Toast.LENGTH_SHORT).show();

                        }
                    }*/

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
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void updateUI(final Intent intent) {
        try {
            final String strVal = intent.getStringExtra("data");
            Log.i("VAL", "Path : " + strVal);

            strMultipleImagePath = "";

            AddHoroscope.this.runOnUiThread(new Runnable() {
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
                            ImgI.setBmpImage(getThumbnail(Uri.parse("file://" + fff.toString())));
                            ImgI.setImage(strImagesAr[i]);
                            alBitmap.add(ImgI);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    cGAdapter = new Custom_GridView_Adapter(ctx, alBitmap);
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
                Log.i("HH","---"+strlocaddress);
                Log.i("HH","---------"+strloclatitude);
                strlocaddress = gD.pro_searchAddress;
                strloclatitude = gD.pro_searchLatitutde;
                strloclongtitude = gD.pro_searchLongitude;
                edCompanyAddress.setText(strlocaddress);
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
        // Create an image file name
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
                                ClipData clipData = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                                    clipData = data.getClipData();
                                }

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
                                                        cursor = ctx.getContentResolver().query(uriVal, null, null, null, null);
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
                                                Toast.makeText(AddHoroscope.this, "u reached max 10", Toast.LENGTH_SHORT).show();
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
                                            cursor = ctx.getContentResolver().query(uri, null, null, null, null);
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


                    cGAdapter = new Custom_GridView_Adapter(ctx, alBitmap);
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
        gD.callload(ctx, nScreenHeight);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,  gD.common_baseurl+"companyinfo.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (gD.alertDialog != null) {
                            gD.alertDialog.dismiss();
                        }
                        try {
                            //    JSONObject jsoBj = new JSONObject(response);

                            Log.i("KAL", "StrResp : " + response);

                            if (response != null) {


                                if (gD.alertDialog != null) {
                                    gD.alertDialog.dismiss();
                                }
                                final JSONObject jsobj = new JSONObject(response);

                                Log.i("KAL", "StrResp--> : " + jsobj.toString());
                                if (jsobj.getString("status") .equalsIgnoreCase("success")) {
                                    edCompanyAddress.setText("");
                                    LayoutInflater inflater = LayoutInflater.from(ctx);
                                    View dialogLayout = inflater.inflate(R.layout.layout_confirmation, null);

                                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(ctx);
                                    alertDialogBuilder.setView(dialogLayout);
                                    alertDialogBuilder.setCancelable(false);


                                    final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                    //  alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                    alertDialog.show();

                                    LinearLayout llayAlert = (LinearLayout) dialogLayout.findViewById(R.id.llayalertDialog);
                                    // TextView tv_alert_Title = (TextView) dialogLayout.findViewById(R.id.tv_alert_Title);
                                    TextView tv_alert_Message = (TextView) dialogLayout.findViewById(R.id.message);

                                    tv_alert_Message.setText("You successfully added your information");

                                    Button btn_submit = (Button) dialogLayout.findViewById(R.id.ok);
                                    Button btn_cancel = (Button) dialogLayout.findViewById(R.id.cancel);


                                    FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, nScreenHeight);
                                    llayAlert.setLayoutParams(lparams);
                                    int count = 1;
                                    if (count == 1) {
                                        btn_submit.setText("OK");
                                        btn_cancel.setVisibility(View.GONE);
                                    }


                                    btn_submit.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            //put value from response
                                            String strservicetype = null;
                                            try {

                                                Intent user_category = new Intent(AddHoroscope.this, ProviderCompanyInfoLicense.class);
                                                user_category.putExtra("screenFrom", 1);
                                                startActivity(user_category);
                                                finish();
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                    finishAffinity();
                                                }
                                                alertDialog.dismiss();





                                                strservicetype = jsobj.getJSONObject("provider_details").getString("services");

                                                SharedPreferences.Editor editor = SplashActivity.sharedPreferences.edit();
                                                editor.putString("serviceType", strservicetype);
                                                editor.commit();


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }




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
                Map<String, String> params = new Hashtable<String, String>();
  if(alBitmap!=null ) {
    //Creating parameters
    String str_get_image = "";
    for (int j = 0; j < alBitmap.size(); j++) {
        Log.i("MYND", "Value : " + alBitmap.get(j).getBmpImage());

        str_get_image += getStringImage(alBitmap.get(j).getBmpImage()) + ",";
    }

    if (str_get_image.contains(",")) {
        str_get_image = str_get_image.substring(0, str_get_image.length() - 1);
    }
    Log.i("TT", "files : " + str_get_image);

        params.put("files", str_get_image);

}
                else

{
    params.put("remove", "1");
}


                Log.i("TT", "strCname : " + strCname);
                Log.i("TT", "strCaddress : " + strCaddress.trim());

                Log.i("TT", "latitude : " + strloclatitude.trim());
                Log.i("TT", "longitude : " + strloclongtitude.trim());


                params.put("companyname",  strCname.trim());
                params.put("companyaddress",  strCaddress.trim());
                params.put("latitude",  strloclatitude.trim());
                params.put("longitude",  strloclongtitude.trim());
                params.put("providerid", SplashActivity.sharedPreferences.getString("UID", null));



                //returning parameters
                return params;
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();


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




        RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 48,
                x, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
    @Override
    public void onBackPressed() {
        popupcall("Do you want to logout",0,"back");
       /* SharedPreferences.Editor editor = SplashActivity.autoLoginPreference.edit();
        editor.putString("uname", null);
        editor.putString("pwd", null);
        editor.commit();
        finish();*/
    }
    public void popupcall(String s, int i, final String str_res)
    {


        try {
            LayoutInflater inflater = LayoutInflater.from(ctx);
            View dialogLayout = inflater.inflate(R.layout.layout_confirmation, null);

            android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(ctx);
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


            FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, nnScreenHeight);
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
                        Intent user_category = new Intent(AddHoroscope.this, ProviderCompanyInfoLicense.class);
                        user_category.putExtra("screenFrom", 1);
                        startActivity(user_category);
                        finish();
                        finishAffinity();
                        alertDialog.dismiss();

                    }
                    else
                    {
                        SharedPreferences.Editor editor = SplashActivity.autoLoginPreference.edit();
                        editor.putString("uname", null);
                        editor.putString("pwd", null);
                        editor.commit();

                        Intent user_category = new Intent(AddHoroscope.this, SignInActivity.class);

                        startActivity(user_category);
                        finish();
                        alertDialog.dismiss();
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



}

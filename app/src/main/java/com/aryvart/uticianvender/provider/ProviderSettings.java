package com.aryvart.uticianvender.provider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.aryvart.uticianvender.genericclasses.AlertDialogManager;
import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.aryvart.uticianvender.genericclasses.MultipartUtility;
import com.aryvart.uticianvender.genericclasses.RealPathUtil;

import com.aryvart.uticianvender.imageCache.ImageLoader;
import com.aryvart.uticianvender.utician.SplashActivity;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by Android2 on 7/19/2016.
 */
public class ProviderSettings extends Activity {
    public static final int REQUEST_CODE_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    ImageView user_image;
    ImageView back_image, edit_profile, change_password;
    LinearLayout edit_imae_lay, change_password_image, ll_change_pass, ll_edit_profile;
    EditText edt_yourname, edt_phone, edit_old_pass, edit_new_pass, edit_conf_pass;
    ImageView edit_ok, change_ok;
    ProgressDialog pd;
    ImageView rnd_image;

    private final int GALLERY_ACTIVITY_CODE=200;
    private final int RESULT_CROP = 400;
    String str_fullname, str_phonenum,str_account,str_routing, strrealpath,strrealpathe, str_get_old_pass, str_get_new_pass, str_get_new_confirm_pass;
    AlertDialogManager alert = new AlertDialogManager();


    // Left side Image
    LinearLayout rl_left;
    ImageView img_left;
    TextView txt_left;

    // Right side image
    LinearLayout rl_right;
    ImageView img_right;
    TextView txt_right;
    ImageLoader imgLoader;
    String mCurrentPhotoPath;
    String str_capture_file_path;
    Context context;
    GeneralData gD;
    String strImage_path;
    int nScreenHeight;
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
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.provider_seetings);
            rnd_image = (ImageView) findViewById(R.id.provider_image);
            back_image = (ImageView) findViewById(R.id.back_image);
            edit_profile = (ImageView) findViewById(R.id.edit_profile);
            change_password = (ImageView) findViewById(R.id.change_password);
            imgLoader = new ImageLoader(ProviderSettings.this);
        /*change_cancel = (ImageView) findViewById(R.id.change_cancel);*/
            change_ok = (ImageView) findViewById(R.id.change_ok);
        /*edit_cancel = (ImageView) findViewById(R.id.edit_cancel);*/
            edit_ok = (ImageView) findViewById(R.id.edit_ok);

            context=this;

            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = (int) ((float) nHeight / (float) 2.4);
            verifyStoragePermissions(this);
            edt_yourname = (EditText) findViewById(R.id.edt_yourname);
            edt_phone = (EditText) findViewById(R.id.edt_phone);
            edit_old_pass = (EditText) findViewById(R.id.edit_old_pass);
            edit_new_pass = (EditText) findViewById(R.id.edit_new_pass);
            edit_conf_pass = (EditText) findViewById(R.id.edit_conf_pass);
            gD = new GeneralData(context);

            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(context, "Unable to connect with Internet. Please check your internet connection and try again");
            }
            SplashActivity.sharedPreferences=getSharedPreferences("regid", Context.MODE_PRIVATE);

            edt_yourname.setText(SplashActivity.sharedPreferences.getString("fullname", null));
            edt_phone.setText(SplashActivity.sharedPreferences.getString("phone_num", null));
            imgLoader.DisplayImage(SplashActivity.sharedPreferences.getString("user_image", null), rnd_image);

            Log.i("IMG", SplashActivity.sharedPreferences.getString("user_image", null));
//       Log.i("IMGG", SplashActivity.sharedPreferences.getString("real_path", null));


            rl_left = (LinearLayout) findViewById(R.id.rl_left);
            img_left = (ImageView) findViewById(R.id.img_left);
            txt_left = (TextView) findViewById(R.id.txt_left);

            rl_right = (LinearLayout) findViewById(R.id.rl_right);
            img_right = (ImageView) findViewById(R.id.img_right);
            txt_right = (TextView) findViewById(R.id.txt_right);

            str_fullname = edt_yourname.getText().toString();
            str_phonenum = edt_phone.getText().toString();
            str_get_old_pass = edit_old_pass.getText().toString();
            str_get_new_pass = edit_new_pass.getText().toString();
            str_get_new_confirm_pass = edit_conf_pass.getText().toString();

            back_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    Intent i = new Intent(context, Provider_DashBoard.class);
                    //  gD.screenback = 0;
                    startActivity(i);
                }
            });
            rnd_image.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (SplashActivity.sharedPreferences.getString("user_image", null).equalsIgnoreCase("http://utician.com/api/upload/")) {
                        add_image();
                        Log.i("HH", "hello ****");
                    } else {
                        Log.i("HH","hello");
                        update();

                    }
                }
            });
            edit_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(ProviderSettings.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        str_fullname = edt_yourname.getText().toString().trim();
                        str_phonenum = edt_phone.getText().toString().trim();

                        if (str_phonenum.length() > 0 || str_fullname.length() > 0) {
                            if (str_phonenum.length() >= 10) {

                                uploadImage(null,"field");
                             //   EditProfileTask edtprofiletask = new EditProfileTask("field");
                              //  edtprofiletask.execute();
                            } else {
                                if (str_phonenum.length() == 0) {
                                    Toast.makeText(getApplicationContext(), "Please enter your phone number", Toast.LENGTH_SHORT).show();
                                } else if (str_phonenum.length() < 10) {
                                    Toast.makeText(ProviderSettings.this, "Enter valid phone number !", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            Toast.makeText(ProviderSettings.this, "Fields cannot be empty!.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            edit_conf_pass.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {

                        clickevent();


                        return true;
                    }
                    return false;
                }
            });


            change_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickevent();
            }
            });


            edit_imae_lay = (LinearLayout) findViewById(R.id.edit_image_lay);
            ll_edit_profile = (LinearLayout) findViewById(R.id.edit_profile_layout);
            change_password_image = (LinearLayout) findViewById(R.id.changepassword_image_lay);
            ll_change_pass = (LinearLayout) findViewById(R.id.chage_pwd_layout);




            editProfile();
            rl_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (txt_left.getText().equals("Change Password")) {
                        changePassword();

                        /*Toast.makeText(UserProviderDetails.this, "services", Toast.LENGTH_SHORT).show();*/
                    }  else {
                        editProfile();


                      //  Toast.makeText(ProviderSettings.this, "reviews", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            rl_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (txt_right.getText().equals("Edit Profile")) {
                        editProfile();

                        /*Toast.makeText(UserProviderDetails.this, "services", Toast.LENGTH_SHORT).show();*/
                    } else {
                        changePassword();

                       // Toast.makeText(ProviderSettings.this, "reviews", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

public void clickevent()
{

    try {
        if (!gD.isConnectingToInternet()) {
            Toast.makeText(ProviderSettings.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
        }
        else {
            str_get_new_pass = edit_new_pass.getText().toString().trim();
            str_get_new_confirm_pass = edit_conf_pass.getText().toString().trim();
            str_get_old_pass = edit_old_pass.getText().toString().trim();

            if (str_get_old_pass.length() > 0 && str_get_new_pass.length() > 0 && str_get_new_confirm_pass.length() > 0 && str_get_new_pass.length() > 5 && str_get_new_confirm_pass.length() > 5) {
                if (str_get_new_pass.equals(str_get_new_confirm_pass)) {
                    ChangePasswordTask chngepasstask = new ChangePasswordTask();
                    chngepasstask.execute();
                } else {
                    Toast.makeText(ProviderSettings.this, "New password and Confirm password doesn't match !", Toast.LENGTH_SHORT).show();
                }


            } else {
                if (str_get_old_pass.length() == 0) {
                    Toast.makeText(ProviderSettings.this, "Enter old password.", Toast.LENGTH_SHORT).show();
                } else if (str_get_new_pass.length() == 0) {
                    Toast.makeText(ProviderSettings.this, "Enter new password.", Toast.LENGTH_SHORT).show();
                } else if (str_get_new_pass.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password must be in 6 to 20 characters", Toast.LENGTH_SHORT).show();
                } else if (str_get_new_confirm_pass.length() == 0) {
                    Toast.makeText(ProviderSettings.this, "Enter confirm password.", Toast.LENGTH_SHORT).show();
                } else if (str_get_new_confirm_pass.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password must be in 6 to 20 characters", Toast.LENGTH_SHORT).show();
                } else if (str_get_old_pass.equals(str_get_new_pass)) {
                    Toast.makeText(ProviderSettings.this, "Old and New password should not same.", Toast.LENGTH_SHORT).show();
                } else if (!str_get_new_pass.equals(str_get_new_confirm_pass)) {
                    Toast.makeText(ProviderSettings.this, "Old and New password doesn't match.", Toast.LENGTH_SHORT).show();
                }
            }

        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}
    public void editProfile(){
        ll_edit_profile.setVisibility(View.VISIBLE);

        ll_change_pass.setVisibility(View.GONE);
        img_left.setBackgroundResource(R.drawable.edit_red);


        img_right.setBackgroundResource(R.drawable.grey_edit_lock);

        txt_left.setText("Edit Profile");

    }
    public void changePassword(){
        try {
            ll_edit_profile.setVisibility(View.GONE);

            ll_change_pass.setVisibility(View.VISIBLE);

            img_right.setBackgroundResource(R.drawable.pass_red);
            img_left.setBackgroundResource(R.drawable.black_change_pwd);

            txt_right.setText("Change Password");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void add_image() {
        try {
            final CharSequence[] items = {"Take Photo", "Choose from Library"};
            AlertDialog.Builder builder = new AlertDialog.Builder(ProviderSettings.this);
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // camera
                    if (item == 0) {
                        takePicture();
                    } else {
                        openGallery();
                    }
                }
            });
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void update() {
        try {
            final CharSequence[] items = {"Take Photo", "Choose from Library",
                    "Remove Image"};
            AlertDialog.Builder builder = new AlertDialog.Builder(ProviderSettings.this);
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    // camera
                    if (item == 0) {
                        takePicture();
                    } else if (item == 1) {
                        openGallery();
                    } else {

                        if (!gD.isConnectingToInternet()) {
                            Toast.makeText(ProviderSettings.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            rnd_image.setImageDrawable(
                                    getResources().getDrawable(R.drawable.default_user_icon));
                            uploadImage(null,"remove");
                          //  EditProfileTask edtprofiletask = new EditProfileTask("remove");
                        //    edtprofiletask.execute();
                        }
                    }
                }
            });
            builder.show();
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
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    startActivityForResult(cameraIntent, REQUEST_CODE_TAKE_PICTURE);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_GALLERY);

      //  startActivityForResult(intent, GALLERY_ACTIVITY_CODE);
    }






    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == REQUEST_CODE_GALLERY) {


                    Uri selectedImageUri = data.getData();
                    String[] projection = {MediaStore.MediaColumns.DATA};
                    Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                            null);
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    cursor.moveToFirst();

                    String selectedImagePath = cursor.getString(column_index);

                    Bitmap bm;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(selectedImagePath, options);
                    final int REQUIRED_SIZE = 200;
                    int scale = 1;
                    while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                            && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                        scale *= 2;
                    options.inSampleSize = scale;
                    options.inJustDecodeBounds = false;
                    bm = BitmapFactory.decodeFile(selectedImagePath, options);
                    rnd_image.setImageBitmap(bm);

                    try {
                        if (requestCode == 1 && resultCode == RESULT_OK) {
                            String realPath;
                            // SDK < API11
                            if (Build.VERSION.SDK_INT < 11) {
                                realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, data.getData());
                            }
                            // SDK >= 11 && SDK < 19
                            else if (Build.VERSION.SDK_INT < 19) {
                                realPath = RealPathUtil.getRealPathFromURI_API11to18(this, data.getData());
                            }
                            // SDK > 19 (Android 4.4)
                            else {
                                realPath = RealPathUtil.getRealPathFromURI_API19(this, data.getData());
                                Log.i("realll path->", realPath);
                            }
                            Uri uriFromPath = Uri.fromFile(new File(realPath));
                            File sourceFilePath = new File(realPath);
                            String strrealpath = realPath;


                            uploadImage(bm,"image");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else if (requestCode == REQUEST_CODE_TAKE_PICTURE && resultCode == RESULT_OK) {
                    try {
                        final int IMAGE_MAX_SIZE = 300;

                        // Bitmap bitmap;
                        File file = null;
                        FileInputStream fis;
                        BitmapFactory.Options opts;
                        int resizeScale;
                        Bitmap bmp;
                        file = new File(Uri.parse(mCurrentPhotoPath).getPath());
                        // This bit determines only the width/height of the
                        // bitmap
                        // without loading the contents
                        opts = new BitmapFactory.Options();
                        opts.inJustDecodeBounds = true;
                        fis = new FileInputStream(file);
                        BitmapFactory.decodeStream(fis, null, opts);
                        fis.close();

                        // Find the correct scale value. It should be a power of
                        // 2
                        resizeScale = 1;

                        if (opts.outHeight > IMAGE_MAX_SIZE
                                || opts.outWidth > IMAGE_MAX_SIZE) {
                            resizeScale = (int) Math.pow(2, (int) Math.round(Math.log(IMAGE_MAX_SIZE / (double) Math.max(opts.outHeight, opts.outWidth)) / Math.log(0.5)));
                        }

                        // Load pre-scaled bitmap
                        opts = new BitmapFactory.Options();
                        opts.inSampleSize = resizeScale;


                        fis = new FileInputStream(file);

                        str_capture_file_path = file.getAbsolutePath();

                        Log.i("SRV", "capture file path : " + str_capture_file_path);

                        bmp = BitmapFactory.decodeStream(fis, null, opts);
                        Bitmap getBitmapSize = BitmapFactory.decodeResource(
                                getResources(), R.drawable.default_user_icon);

                        rnd_image.setImageBitmap(bmp);
                        //  rnd_image.setRotation(90);
                        fis.close();

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                        if (!gD.isConnectingToInternet()) {
                            Toast.makeText(ProviderSettings.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                        } else {
                            //  EditProfileTask edtprofiletask = new EditProfileTask("capture");
                            // edtprofiletask.execute();
                            uploadImage(bmp, "capture");

                            SplashActivity.sharedPreferences = getSharedPreferences("regid", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editorVal = SplashActivity.sharedPreferences.edit();
                            editorVal.putString("imageS", str_capture_file_path);
                            editorVal.commit();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }














    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(context, Provider_DashBoard.class);
        //  gD.screenback = 0;
        startActivity(i);
    }

    /*  ChangePasswordTasks*/
    class ChangePasswordTask extends AsyncTask {

        String sResponse = null;

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                String charset = "UTF-8";
                String requestURL = gD.common_baseurl+"changepassword.php";

                MultipartUtility multipart = new MultipartUtility(requestURL, charset);

                multipart.addHeaderField("Content-Encoding", "gzip");
                multipart.addHeaderField("User-Agent", "rajaji");
                multipart.addHeaderField("Test-Header", "Header-Value");
                multipart.addFormField("description", "CoolPictures");
                multipart.addFormField("keywords", "Java,upload,Spring");

                multipart.addFormField("oldpassword", str_get_old_pass);
                multipart.addFormField("newpassword", str_get_new_pass);
                multipart.addFormField("id", SplashActivity.sharedPreferences.getString("UID", null));

                // 6. upload finish ----------- vinoth
                List<String> response = multipart.finish();
                System.out.println("SERVER REPLIED:");
                StringBuilder sb = new StringBuilder();
                for (String line : response) {
                    System.out.println(line);
                    sb.append(line);
                }
                try {
                    JSONObject jsonObj = new JSONObject(sb.toString());
                    Log.i("SSu", "StrResp : " + jsonObj.toString());
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
            // TODO Auto-generated method stub
            super.onPreExecute();

            gD.callload(context, nScreenHeight);


        }

        @Override
        protected void onPostExecute(Object sesponse) {
            try {
                if (sResponse != null) {
                    JSONObject jsobj = new JSONObject(sResponse);

                    if (jsobj.getInt("code") == 2) {
                        SharedPreferences.Editor editor = SplashActivity.autoLoginPreference.edit();
                        editor.putString("pwd", str_get_new_pass);
                        editor.commit();

                        Toast.makeText(ProviderSettings.this, "Your password has been change successfully", Toast.LENGTH_SHORT).show();
                        Intent NextScreenIntent = new Intent(ProviderSettings.this, Provider_DashBoard.class);
                        startActivity(NextScreenIntent);
                    } else if (jsobj.getInt("code") == 0) {

                        gD.showAlertDialog(context, "",jsobj.getString("status").toString(), nScreenHeight, 1);

                    } else if (jsobj.getInt("code") == 3) {

                        gD.showAlertDialog(context, "",jsobj.getString("status").toString(), nScreenHeight, 1);


                        edit_old_pass.setText("");
                        edit_new_pass.setText("");
                        edit_conf_pass.setText("");

                    }



                    if(gD.alertDialog!=null) {
                        gD.alertDialog.dismiss();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //persmission method.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void uploadImage(final Bitmap bmp, final String strREQ) {
        //Showing the progress dialog
        gD.callload(context, nScreenHeight);
















        StringRequest stringRequest = new StringRequest(Request.Method.POST,  gD.common_baseurl+"providereditprofile.php",
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

                                    strImage_path = jsobj.getJSONObject("provider_details").getString("image_path");
                                    // String id = json.getString("userid");
                                    String strImagepath = gD.common_baseurl+"upload/" + strImage_path;
                                    SharedPreferences.Editor editor = SplashActivity.sharedPreferences.edit();
                                    editor.putString("phone_num", str_phonenum);
                                    editor.putString("fullname", str_fullname);
                                    editor.putString("routing",str_routing);
                                    editor.putString("account",str_account);
                                    editor.putString("user_image", strImagepath);
                                    editor.putString("real_path", strImage_path);
                                    Log.i("TT", "image" + strImage_path);
                                    Log.i("TT", "name" + str_fullname);
                                    editor.commit();
//                        pd.dismiss();
                                    Toast.makeText(ProviderSettings.this, "Your Profile has been update successfully", Toast.LENGTH_SHORT).show();
                                    Intent NextScreenIntent = new Intent(ProviderSettings.this, Provider_DashBoard.class);
                                    startActivity(NextScreenIntent);

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
                String image = null;
if(bmp!=null)
{
     image = getStringImage(bmp);
}


                Map<String, String> params = new Hashtable<String, String>();

                if (strREQ.equalsIgnoreCase("field")) {
                    params.put("id", SplashActivity.sharedPreferences.getString("UID", null));
                    params.put("username", str_fullname);
                    params.put("phonenumber", str_phonenum);
//                    params.put("account", str_account);
                 //   params.put("routing", str_routing);
                } else if (strREQ.equalsIgnoreCase("image")) {
                    params.put("id", SplashActivity.sharedPreferences.getString("UID", null));

                    params.put("image", image);
                }
                else if (strREQ.equalsIgnoreCase("capture")) {
                    params.put("id", SplashActivity.sharedPreferences.getString("UID", null));

                    params.put("image", image);
                } else if (strREQ.equalsIgnoreCase("remove")) {
                    params.put("id", SplashActivity.sharedPreferences.getString("UID", null));
                    params.put("remove","1" );

                }

                //returning parameters
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

              //  params.put("Content-Type", "application/json");
             //   params.put("Content-Encoding", "gzip");
                return params;
            }


        };
        RetryPolicy policy = new DefaultRetryPolicy(30000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);


        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
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




}

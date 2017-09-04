package com.aryvart.uticianvender.utician;

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
import android.graphics.Typeface;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.aryvart.uticianvender.Interface.GetNotification;
import com.aryvart.uticianvender.Interface.OAuthAuthenticationListener;
import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.TermsCondition;
import com.aryvart.uticianvender.adapter.SpinnerAdap;
import com.aryvart.uticianvender.bean.SpinItemData;
import com.aryvart.uticianvender.genericclasses.AlertDialogManager;
import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.aryvart.uticianvender.genericclasses.MultipartUtility;
import com.aryvart.uticianvender.genericclasses.RealPathUtil;


import com.aryvart.uticianvender.newGCM.GCMClientManager;



import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.TreeMap;



/**
 * Created by Android2 on 7/12/2016.
 */
public class SignUpActivity extends Activity implements GetNotification, OAuthAuthenticationListener {

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.CALL_PHONE,
            Manifest.permission.CAMERA, Manifest.permission.SEND_SMS, Manifest.permission.ACCESS_WIFI_STATE
    };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    public static final int REQUEST_CODE_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    private static final String TWITTER_KEY = "Ty3Y4VFp7PYMDX7Hp0SBCfAub";
    private static final String TWITTER_SECRET = "1gTvViwSAlt3m7kiii1UInRXCjqkKoO2wLUZosDNUIFUYfswsD";
    public static Context context;
    ImageView img_back;
    //Spinner sp_selecttype;
    SpinnerAdapter adapter;
    CheckBox ch_terms;
    EditText et_yourname, et_username, et_email, et_password, et_phonenumber,edit_promocode;
    TextView txt_sign_up;
    boolean isterms = false;
    int n_spinitem = 1;
    String sp_data;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    AlertDialogManager alert = new AlertDialogManager();
    //Gallery imge picker
    ImageView rm_user_img;
    String strrealpath, str_full_name,str_user_promo, checkbocond, str_user_name, str_user_email, str_user_pass, str_phone_number;
    // Twitter
    Button txt_signup;
    LinearLayout txt_twitter_login, ll_instagram;
    ProgressDialog progress, progressDialog;
    int nScreenHeight,mScreenHeight,oScreenHeight;
    //Facebook
    LinearLayout ll_fb_login;
    String fid = "";
    //GCM Id

    String timezonevalue,simage;
    String strGCMid = "";
    String PROJECT_NUMBER = "311648921952";
    ProgressDialog pd;
    ToggleButton toggleBtn;


    // private CallbackManager callbackManager;

    private String twitter_id = "", twitter_name = "", t_full_name = "", t_profile_image;//twitter




    private String strInstaUserId = "";
    GeneralData gD;
    Bitmap bn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.sign_up);
            context = this;
            gD = new GeneralData(context);

            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(context, "Unable to connect with Internet. Please check your internet connection and try again");
            }

            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = (int) ((float) nHeight / (float) 1.9);
            mScreenHeight = (int) ((float) nHeight / (float) 2.4);
            oScreenHeight = (int) ((float) nHeight / (float) 1.8);
            verifyStoragePermissions(this);
            //  sp_selecttype = (Spinner) findViewById(R.id.sp_select_type);
            rm_user_img = (ImageView) findViewById(R.id.user_image);
            img_back = (ImageView) findViewById(R.id.img_back);
            et_yourname = (EditText) findViewById(R.id.edit_yourname);
            et_username = (EditText) findViewById(R.id.edit_username);
            et_phonenumber = (EditText) findViewById(R.id.phonenumber);
            et_email = (EditText) findViewById(R.id.edit_email);
            txt_sign_up = (Button) findViewById(R.id.txt_signup);
            et_password = (EditText) findViewById(R.id.edit_password);
            edit_promocode= (EditText) findViewById(R.id.edit_promocode);

            Typeface tf = Typeface.createFromAsset(getAssets(),
                    "arial.ttf");


            et_username.setTypeface(tf);
            et_yourname.setTypeface(tf);
            et_phonenumber.setTypeface(tf);
            et_email.setTypeface(tf);
            et_password.setTypeface(tf);


            TextView terms_condition = (TextView) findViewById(R.id.terms_condition);
            terms_condition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    Intent help = new Intent(getApplicationContext(), TermsCondition.class);
                    startActivity(help);
                }
            });


            txt_sign_up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("strGMid", strGCMid);
                    register(strGCMid);
                }
            });


            img_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            rm_user_img.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (rm_user_img.getDrawable() != null) {
                        update();
                    } else {
                        add_image();
                    }

                }
            });

            //checkbox
            ch_terms = (CheckBox) findViewById(R.id.ch_select);
            ch_terms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView.isChecked()) {
                        isterms = true;
                    } else {
                        isterms = false;
                    }
                }
            });






            progress = new ProgressDialog(SignUpActivity.this);
            progress.setMessage("please wait..");

            progress.setIndeterminate(false);
            progress.setCancelable(false);
            twitter_id = twitter_name = t_full_name = t_profile_image = "";  // twitter



            GCMClientManager pushClientManager = new GCMClientManager(this, PROJECT_NUMBER);
            pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
                @Override
                public void onSuccess(String registrationId, boolean isNewRegistration) {

                    strGCMid = registrationId;

                    Log.d("RegID", registrationId);
                    //send this registrationId to your server
                }

                @Override
                public void onFailure(String ex) {
                    super.onFailure(ex);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int responseCode,
                                    Intent intent) {
        //for twitter callback result.

        // callbackManager.onActivityResult(requestCode, responseCode, intent);

        try {
            if (responseCode == Activity.RESULT_OK) {
                if (requestCode == REQUEST_CODE_GALLERY) {

                    Uri selectedImageUri = intent.getData();
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

                    rm_user_img.setImageBitmap(bm);


                    try {
                        if (requestCode == 1 && responseCode == RESULT_OK) {
                            String realPath;
                            // SDK < API11
                            if (Build.VERSION.SDK_INT < 11) {
                                realPath = RealPathUtil.getRealPathFromURI_BelowAPI11(this, intent.getData());
                            }
                            // SDK >= 11 && SDK < 19
                            else if (Build.VERSION.SDK_INT < 19) {
                                realPath = RealPathUtil.getRealPathFromURI_API11to18(this, intent.getData());
                            }
                            // SDK > 19 (Android 4.4)
                            else {
                                realPath = RealPathUtil.getRealPathFromURI_API19(this, intent.getData());
                                Log.i("realll path->", realPath);
                            }


                            Uri uriFromPath = Uri.fromFile(new File(realPath));

                            // 1. Get the image path from the gallery------------ vinoth
                            File sourceFilePath = new File(realPath);


                            strrealpath = realPath;
                            bn=bm;
                            Log.i("DD", sourceFilePath.getAbsolutePath());
                            Log.d("HMKCODE", "Real Path: " + realPath);

                            // 2. Upload the multipart content of the image to the server------------ vinoth
                            // ImageUploadMultipartTask imUMT = new ImageUploadMultipartTask(sourceFilePath.getAbsolutePath(), "http://aryvartdev.com/projects/utician/register.php?user_id=",);
                            //imUMT.execute();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                } else if (requestCode == REQUEST_CODE_TAKE_PICTURE) {
                    File f = new File(Environment.getExternalStorageDirectory().toString());
                    for (File temp : f.listFiles()) {
                        if (temp.getName().equals("temp.jpg")) {
                            f = temp;
                            break;
                        }
                    }
                    try {
                        Bitmap bitmap;
                        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

                        bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                                bitmapOptions);

                        rm_user_img.setImageBitmap(bitmap);
                        bn=bitmap;
                        String path = Environment
                                .getExternalStorageDirectory()
                                + File.separator
                                + "Phoenix" + File.separator + "default";
                        f.delete();
                        OutputStream outFile = null;
                        File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                        try {
                            outFile = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                            outFile.flush();
                            outFile.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (responseCode != Activity.RESULT_CANCELED) {
                Log.d("error", "Problem getting the picture from gallery");
                // Toast.makeText(getActivity(), R.string.general_error, Toast.LENGTH_LONG).show();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }


    private void update() {

        try {
            final CharSequence[] items = {"Take Photo", "Choose from Library",
                    "Remove Image"};

            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
            // builder.setTitle("Select Image");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) { // pick from
                    // camera
                    if (item == 0) {
                        takePicture();
                    } else if (item == 1) {
                        openGallery();
                    } else {
                        rm_user_img.setImageDrawable(
                                getResources().getDrawable(R.drawable.default_user_icon));
                    }
                }
            });
            builder.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void add_image() {

        try {
            final CharSequence[] items = {"Take Photo", "Choose from Library"};

            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);

            // builder.setTitle("Select Image");

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

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_GALLERY);
    }

    public void register(final String GCMID) {

        try {


                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(SignUpActivity.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    }
                    else {

                    str_full_name = et_yourname.getText().toString().trim();
                    str_user_name = et_username.getText().toString().trim();
                    str_user_email = et_email.getText().toString().trim();
                    str_user_pass = et_password.getText().toString().trim();
                        str_user_promo=edit_promocode.getText().toString();
                    str_phone_number = et_phonenumber.getText().toString().trim();

                    if (str_full_name.length() > 0 && str_phone_number.length() > 0 && str_user_name.length() > 0 && str_user_email.length() > 0 && str_user_pass.length() > 0  && isterms && str_phone_number.length() > 9 && str_user_pass.length() > 5)

                    {
                        try {
                            if (!str_user_email.matches(emailPattern)) {
                                Toast.makeText(getApplicationContext(), "Enter the valid email id !", Toast.LENGTH_SHORT).show();
                            } else {
                                ImageUploadTask imgtask = new ImageUploadTask(GCMID);
                                imgtask.execute();

                               /* Register obj = new Register(strGCMid);
                                obj.execute();*/
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        if (str_full_name.length() == 0) {
                            Toast.makeText(getApplicationContext(), "Please enter your Name", Toast.LENGTH_SHORT).show();
                        } else if (str_user_name.length() == 0) {
                            Toast.makeText(getApplicationContext(), "Please enter your UserName", Toast.LENGTH_SHORT).show();
                        } else if (str_user_email.length() == 0) {
                            Toast.makeText(getApplicationContext(), "Please enter your email id", Toast.LENGTH_SHORT).show();
                        } else if (str_phone_number.length() == 0) {
                            Toast.makeText(getApplicationContext(), "Please enter your phone number", Toast.LENGTH_SHORT).show();
                        } else if (str_phone_number.length() < 10) {
                            Toast.makeText(getApplicationContext(), "Please enter valid phone number", Toast.LENGTH_SHORT).show();
                        } else if (str_user_pass.length() == 0) {
                            Toast.makeText(getApplicationContext(), "Please enter the password ", Toast.LENGTH_SHORT).show();
                        } else if (str_user_pass.length() < 6) {
                            Toast.makeText(getApplicationContext(), "Password must be in 6 to 20 characters", Toast.LENGTH_SHORT).show();
                        } else if (!isterms) {
                            Toast.makeText(getApplicationContext(), "Accept Terms and Condition", Toast.LENGTH_SHORT).show();
                        }
                    }
                }



        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void setNotificationId(final String strGCMid) {

    }

    @Override
    public void setProviderDetails(String strGCMid, String strName, String strImage, String straddress, String expertiseid, String strReferalcdoe) {

    }



    @Override
    public void getProviderId(String strProviderId, String strType) {

    }

    @Override
    public void getServicePrice(String n_price) {

    }

    @Override
    public void getServiceDuration(String n_duration) {

    }

    @Override
    public ArrayList<String> getServicName(TreeMap<Integer, String> str_serviceName) {
        return null;
    }

    @Override
    public void setProviderdet(String strGCMid, String strName, String strImage, String straddress, String strExpertise, String strReferalcode) {

    }




    @Override
    public void NotifyFilter(String strFilterVal) {

    }

    @Override
    public void setProvideraddress(String strlatitude, String strlongitude, String straddress) {

    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onFail(String error) {

    }
    @Override
    protected void onResume() {


        TimeZone tz = TimeZone.getDefault();
        System.out.println("TimeZone " + tz.getDisplayName(false, TimeZone.SHORT) + " Timezon id :: " + tz.getID());

        timezonevalue = tz.getID();

        if (!gD.isConnectingToInternet()) {
            Toast.makeText(SignUpActivity.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
        }


        super.onResume();
    }
    @Override
    public void onReceived(final String strMessage, final String strId) {

        Log.i("SS", "i'mCalled... yahoooooooooooooooo!!!!!!!!!!!!" + strMessage);
        Log.i("SS", "Id" + strId);
        strInstaUserId = strId;
        Log.i("VV", "Id" + strInstaUserId);

        Log.i("SS", "ELSE i'm checking 6...");

        et_yourname.setText(strMessage);

        Log.i("RS", "userID : " + strId);

        Log.i("SS", "ELSE i'm checking 7...");

       /* SignUpActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {


            }
        });*/
    }


    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }



    class ImageUploadTask extends AsyncTask {

        String sResponse = null;
        String str_gcmid;

        public ImageUploadTask(String str_id) {
            this.str_gcmid = str_id;
        }


        @Override
        protected Object doInBackground(Object[] params) {
            try {

                    String charset = "UTF-8";

                    String requestURL = gD.common_baseurl+"registerwithimg.php";
                    // 4. separate class for multipart content image uploaded task----------- vinoth

                    MultipartUtility multipart = new MultipartUtility(requestURL, charset);

                    multipart.addHeaderField("Content-Encoding", "gzip");
                    multipart.addHeaderField("Accept", "application*/*");
                    multipart.addHeaderField("Content-type", "application/x-www-form-urlencoded");

                    // 5. set the user_image key word and multipart image file value ----------- vinoth
                    if (fid.length() > 0 || twitter_id.length() > 0 || strInstaUserId.length() > 0) {
                       /* multipart.addFormField("username", "");
                        multipart.addFormField("password", "");*/
                        multipart.addFormField("fbid", fid);
                        multipart.addFormField("twid", twitter_id);
                        multipart.addFormField("instid", strInstaUserId);
                    } else {
                        multipart.addFormField("username", str_user_name);
                        multipart.addFormField("password", str_user_pass);
                    }
                    multipart.addFormField("fullname", str_full_name);
                    multipart.addFormField("email", str_user_email);


                    multipart.addFormField("timezone", timezonevalue);
                    Log.i("RR", "timezonevalue" + timezonevalue);




                    if(bn!=null)
                    {
                        simage = getStringImage(bn);
                    }


Log.i("LAT","simage"+simage);

                if (strrealpath != null) {
                    File sourceFile = new File(strrealpath);
                    Log.i("image path", String.valueOf(sourceFile));
                    Log.i("LAT","sourceFile"+ sourceFile);
                    multipart.addFilePart("image", sourceFile);
                }



                    multipart.addFormField("usertype", "Provider");
                    multipart.addFormField("gcmid", str_gcmid.trim());

                    multipart.addFormField("phone", str_phone_number.trim());
if(str_user_promo.length()>0)
{

    multipart.addFormField("referal", str_user_promo.trim());
    Log.i("LAT", "str_user_promo : " + str_user_promo);

}

                    Log.i("LAT", "if Suren need username : " + str_user_name);
                    Log.i("LAT", "if  Suren need password : " + str_user_pass);
                    Log.i("LAT", "if Suren need fullname : " + str_full_name);
                    Log.i("LAT", "if Suren need email : " + str_user_email);
                    Log.i("LAT", "if  Suren need usertype : " + sp_data);
                    Log.i("LAT", "if  Suren need fbid : " + fid);
                    Log.i("LAT", "if Suren need twid : " + twitter_id);
                    Log.i("LAT", "if Suren need gcmid : " + str_gcmid);
                    Log.i("LAT", "Suren need phone : " + str_phone_number);
                    // 6. upload finish ----------- vinoth
                    List<String> response = multipart.finish();
                    System.out.println("SERVER REPLIED:");
                    StringBuilder sb = new StringBuilder();
                    for (String line : response) {
                        System.out.println(line);
                        // 6. get the server response for image upload ----------- vinoth
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
                    Log.i("LAT", "Response Register : " + sResponse);
                    if (jsobj.getInt("code") == 0) {


                        gD.showAlertDialog(context, "",   "An account with this username already exists.", mScreenHeight, 1);

                    } else if (jsobj.getInt("code") == 1) {

                        gD.showAlertDialog(context, "",  "An account with this email address already exists.", mScreenHeight, 1);


                    }


                    else if (jsobj.getInt("code") == 9) {


                        gD.showAlertDialog(context, "",  jsobj.getString("status"), mScreenHeight, 1);


                    }

                    else if (jsobj.getInt("code") == 11) {
                        gD.showAlertDialog(context, "",  jsobj.getString("status"), mScreenHeight, 1);


                    }

                    else if (jsobj.getInt("code") == 12) {

                        gD.showAlertDialog(context, "",  jsobj.getString("status"), oScreenHeight, 1);


                    }
                    else if (jsobj.getInt("code") == 13) {

                        gD.showAlertDialog(context, "",  jsobj.getString("status"), mScreenHeight, 1);

                    }
                   else if (jsobj.getInt("code") == 2) {


                        final String strUserImagePath = jsobj.getJSONObject("registered_user").getString("image_path");




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

                        tv_alert_Message.setText("You have successfully registered, please verify your email address and log back in to continue");

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

                                Intent NextScreenIntent = new Intent(SignUpActivity.this, SignInActivity.class);
                                NextScreenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                NextScreenIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(NextScreenIntent);
                                finish();

                                alertDialog.dismiss();
                                String strImagepath = gD.common_baseurl+"upload/" + strUserImagePath;

                                SplashActivity.sharedPreferences = getSharedPreferences("regid", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editorVal = SplashActivity.sharedPreferences.edit();
                                editorVal.putString("user_image", strImagepath);


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




    //client registration

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

package com.aryvart.uticianvender.admin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.adapter.GridGalleryAdapter;
import com.aryvart.uticianvender.bean.ImageItem;
import com.aryvart.uticianvender.bean.ProviderBean;
import com.aryvart.uticianvender.bean.ProviderReviewBean;
import com.aryvart.uticianvender.bean.UserHistoryBean;
import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.aryvart.uticianvender.genericclasses.MultipartUtility;
import com.aryvart.uticianvender.utician.SignInActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by android3 on 20/7/16.
 */
public class Admin_ProfilePage extends Activity {
    LinearLayout lay_license, llay_service, lay_profile, lay_serivces, lay_gallery;
    TextView text_head,emptygallery;
    RelativeLayout llay_gallery,llay_license;
    ProgressDialog pd;
    RecyclerView recyclerView;
    Context ctx;
    ImageView image_service, image_license, image_gallery, image_profile, menu_back;
    JSONArray providerServicesToday, providerServicesYesterday, providerServicesAll, providerServicesPrevWeek;
    TextView emptyText;
    ProviderBean pB;
    ProgressDialog dialog;
    RelativeLayout llay_profile;
    ArrayList<ProviderReviewBean> alPRB;
    ArrayList<ProviderBean> alPB;
    String str_providerid, str_status;
    RelativeLayout ll_acceptdec;
    String status;
    ListView listServices;
    TextView socialsecurityno, preference,email_add, phone_num, dateofbirth, btn_accept, btn_decline, companyName, companyAddress, noOfEmployees;

    //Admin Gallery
    GridView gallery_grid, license_grid;
    String[] strGalleryURL;
    String[] strLicenseURL;
    ArrayList<String> alGalleryPath;
    ArrayList<String> alLicensePath;
    int nScreenHeight;
    String providerGalleryURL ;

    String providerLicenseURL ;
    //Liscense
    WebView license_view;
    String strDocPath;
    GeneralData gD;
    private GridGalleryAdapter gridAdapter;
    ProgressBar progressBar2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.admin_profile);

            ctx = this;
            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = nHeight / 2;
            dialog = new ProgressDialog(Admin_ProfilePage.this);


            progressBar2 = (ProgressBar) findViewById(R.id.progressBar);

            gD = new GeneralData(ctx);

            str_providerid = getIntent().getStringExtra("providerid");

            Log.i("II", "str_providerid" + str_providerid);

            lay_license = (LinearLayout) findViewById(R.id.lay_license);
            emptyText = (TextView) findViewById(R.id.emptyText);

            emptygallery = (TextView) findViewById(R.id.emptyText1);

            llay_service = (LinearLayout) findViewById(R.id.llay_service);
            llay_profile = (RelativeLayout) findViewById(R.id.llay_profile);
            llay_gallery = (RelativeLayout) findViewById(R.id.llay_gallery);
            llay_license = (RelativeLayout) findViewById(R.id.llay_license);

            gallery_grid = (GridView) findViewById(R.id.admin_gallery_grid);

            license_grid = (GridView) findViewById(R.id.admin_license_grid);
            // license_view=(WebView)findViewById(R.id.pdfWebView);

            lay_profile = (LinearLayout) findViewById(R.id.lay_profile);
            lay_serivces = (LinearLayout) findViewById(R.id.lay_serivces);
            lay_gallery = (LinearLayout) findViewById(R.id.lay_gallery);
            text_head = (TextView) findViewById(R.id.textContent);
            menu_back = (ImageView) findViewById(R.id.menu_back);
            image_profile = (ImageView) findViewById(R.id.image_profile);
            image_gallery = (ImageView) findViewById(R.id.image_gallery);
            image_license = (ImageView) findViewById(R.id.image_license);
            image_service = (ImageView) findViewById(R.id.image_service);
            text_head.setText("Profile");
            image_profile.setImageResource(R.drawable.history_red);

            companyName = (TextView) findViewById(R.id.company_name);
            companyAddress = (TextView) findViewById(R.id.company_address);
            socialsecurityno = (TextView) findViewById(R.id.employees);
            phone_num = (TextView) findViewById(R.id.phone_num);
            dateofbirth = (TextView) findViewById(R.id.dob);
            preference = (TextView) findViewById(R.id.preference);
            email_add = (TextView) findViewById(R.id.email_add);
            listServices = (ListView) findViewById(R.id.services_list);

            ll_acceptdec = (RelativeLayout) findViewById(R.id.llay_acceptdecline);
            btn_accept = (TextView) findViewById(R.id.accept);
            btn_decline = (TextView) findViewById(R.id.decline);

            str_status = getIntent().getStringExtra("pType");
            Log.i("II", "str_status" + str_status);

            if (str_status.equals("0")) {
                btn_decline.setVisibility(View.VISIBLE);
                btn_accept.setVisibility(View.VISIBLE);
            } else if (str_status.equals("1")) {
                btn_accept.setVisibility(View.GONE);
                btn_decline.setVisibility(View.VISIBLE);
                //  ll_acceptdec.setBackgroundColor(Color.parseColor("#008000"));

            } else if (str_status.equals("2")) {
                btn_decline.setVisibility(View.GONE);
                btn_accept.setVisibility(View.VISIBLE);
                // ll_acceptdec.setBackgroundColor(Color.parseColor("#B22222"));
            }
            menu_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(Admin_ProfilePage.this,SignInActivity.class);
                    startActivity(i);
                    finish();
                }
            });

            lay_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    text_head.setText("Profile");
                    llay_profile.setVisibility(View.VISIBLE);
                    llay_service.setVisibility(View.GONE);
                    llay_gallery.setVisibility(View.GONE);
                    llay_license.setVisibility(View.GONE);
                    image_profile.setImageResource(R.drawable.history_red);
                    image_gallery.setImageResource(R.drawable.history_green);
                    image_service.setImageResource(R.drawable.history_green);
                    image_license.setImageResource(R.drawable.history_green);
                    // showView("all");

                }
            });
            lay_serivces.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    text_head.setText("Services");
                    llay_profile.setVisibility(View.GONE);
                    llay_service.setVisibility(View.VISIBLE);
                    llay_gallery.setVisibility(View.GONE);
                    llay_license.setVisibility(View.GONE);
                    image_service.setImageResource(R.drawable.history_red);
                    image_profile.setImageResource(R.drawable.history_green);
                    image_gallery.setImageResource(R.drawable.history_green);
                    image_license.setImageResource(R.drawable.history_green);
                    // showView("yesterday");

                }
            });
            lay_gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // gD.callload(ctx, nScreenHeight);
                    text_head.setText("Photos");
                    llay_profile.setVisibility(View.GONE);
                    llay_service.setVisibility(View.GONE);
                    llay_gallery.setVisibility(View.VISIBLE);
                    llay_license.setVisibility(View.GONE);
                    image_gallery.setImageResource(R.drawable.history_red);
                    image_profile.setImageResource(R.drawable.history_green);
                    image_service.setImageResource(R.drawable.history_green);
                    image_license.setImageResource(R.drawable.history_green);
                    //   showView("today");

                    GetProfileDetails get_Provider_details = new GetProfileDetails(str_providerid);
                    get_Provider_details.execute();



                }
            });
            lay_license.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  //  progressBar2.setVisibility(View.VISIBLE);
                    text_head.setText("License");
                    llay_profile.setVisibility(View.GONE);
                    llay_service.setVisibility(View.GONE);
                    llay_gallery.setVisibility(View.GONE);
                    llay_license.setVisibility(View.VISIBLE);
                    image_license.setImageResource(R.drawable.history_red);
                    image_profile.setImageResource(R.drawable.history_green);
                    image_service.setImageResource(R.drawable.history_green);
                    image_gallery.setImageResource(R.drawable.history_green);





                }
            });
        /*SplashActivity.sharedPreferences = getSharedPreferences("regid", Context.MODE_PRIVATE);
        String strUSerID = "";
        strUSerID = SplashActivity.sharedPreferences.getString("UID", null);*/


            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(ctx, "Unable to connect with Internet. Please check your internet connection and try again");
            }else {
                GetProviderDetails get_Provider_details = new GetProviderDetails(str_providerid);
                get_Provider_details.execute();
            }
            btn_accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    status = String.valueOf(1);
                    // Intent voicpage = new Intent(AcceptorDecline.this, Provider_CompleteRaisePage.class);

                    // voicpage.putExtra("providerId", pId);
                    // voicpage.putExtra("sR", strjsondatas);


                    //  startActivity(voicpage);


                    GetAcceptDeclineResponse get_accdecl_response = new GetAcceptDeclineResponse(ctx, str_providerid, status);
                    get_accdecl_response.execute();
                }
            });
            btn_decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    status = String.valueOf(2);

                    GetAcceptDeclineResponse get_accdecl_response = new GetAcceptDeclineResponse(ctx, str_providerid, status);
                    get_accdecl_response.execute();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
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
        return response.toString();
    }

    //getprofiledetails
    class GetProviderDetails extends AsyncTask {
        String provider_id = null;
        String sResponse = null;

        public GetProviderDetails(String provider_id) {
            this.provider_id = provider_id;

        }

        @Override
        protected void onPreExecute() {
            Admin_ProfilePage.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gD.callload(ctx, nScreenHeight);



                }
            });
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] param) {

            try {
                String charset = "UTF-8";
                String requestURL = gD.common_baseurl+"selectprovider.php";
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);

                multipart.addHeaderField("Content-Encoding", "gzip");
                multipart.addFormField("providerid", provider_id);
                List<String> response = multipart.finish();

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
                e.printStackTrace();
            }
            return sResponse;
        }

        @Override
        protected void onPostExecute(Object response) {


    if(gD.alertDialog!=null) {
                                        gD.alertDialog.dismiss();
                                    }


            try {
                JSONObject jsobj = new JSONObject(sResponse);
                alPB = new ArrayList();
                alPRB = new ArrayList();
                if (jsobj.getString("status").equalsIgnoreCase("success")) {
                    JSONArray providersJSONArray = jsobj.getJSONArray("providers");
                    JSONArray providerServicesJSONArray = jsobj.getJSONArray("services");

                    for (int i = 0; i < providersJSONArray.length(); i++) {
                        JSONObject pObj = providersJSONArray.getJSONObject(i);

                        //providerImageimgLoader.getBitmap("http://aryvartdev.com/projects/utician/upload/" + pObj.getString("image_path"));
                        // imgLoader.DisplayImage("http://aryvartdev.com/projects/utician/upload/" + pObj.getString("image_path"), providerImage);


                        companyName.setText(pObj.getString("companyname"));


                        phone_num.setText(pObj.getString("phonenumber"));
                        companyAddress.setText(pObj.getString("companyaddress"));
                        socialsecurityno.setText(pObj.getString("socialsecurityno"));
                        dateofbirth.setText(pObj.getString("dob"));
                        preference.setText(pObj.getString("preferences"));
                        email_add.setText(pObj.getString("email"));
                        // strDocPath = "http://aryvartdev.com/projects/utician/license/" + pObj.getString("license");
                        // Log.i("license", strDocPath);

                        // license_view.getSettings().setJavaScriptEnabled(true);
                        // license_view.getSettings().setPluginState(WebSettings.PluginState.ON);
                        // license_view.loadUrl("https://docs.google.com/gview?embedded=true&url=" + strDocPath);

                       /* pB.set_ProviderGallery(pObj.getString("image"));*/


                        strLicenseURL = pObj.getString("license").split(",");
                        Log.i("SSS", "license result : " + pObj.getString("license"));
                        //for license
                        if(pObj.getString("license").length()==0)
                        {

                            emptyText.setVisibility(View.VISIBLE);
                            emptyText.setText("No license uploaded");
                            Log.i("PP","HElloooooo");
                        }

                        else
                        {

                            try {



                                alLicensePath = new ArrayList<String>();
                                for (int j = 0; j < strLicenseURL.length; j++) {
                                    alLicensePath.add(gD.common_baseurl+"license/" + strLicenseURL[j]);

                                    Log.i("SSS", "licenseimg : " + strLicenseURL);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            final ArrayList<ImageItem> alImages = new ArrayList<ImageItem>();
                            for (int k = 0; k < alLicensePath.size(); k++) {
                                try {

                                    URL url = new URL(alLicensePath.get(k));
                                    final String strImageURl = alLicensePath.get(k);

                                    ImageItem imageItems = new ImageItem();
                                    imageItems.setImage(strImageURl);
                                    alImages.add(imageItems);
                                    Log.i("SSS", "license : " + gD.common_baseurl+"license/" + strLicenseURL[i]);
                                   /* if(gD.alertDialog!=null) {
                                        gD.alertDialog.dismiss();
                                    }*/
                                    gridAdapter = new GridGalleryAdapter(Admin_ProfilePage.this, R.layout.gallery_grid_items, alImages);
                                    license_grid.setAdapter(gridAdapter);


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }


                            //  Toast.makeText(Admin_ProfilePage.this, "no image available", Toast.LENGTH_SHORT).show();
                        }




                    }

                    if (jsobj.getJSONArray("services") != null || (!jsobj.getJSONArray("services").equals(""))) {

                        for (int i = 0; i < providerServicesJSONArray.length(); i++) {
                            pB = new ProviderBean();
                            JSONObject providersServiceJSONobject = providerServicesJSONArray.getJSONObject(i);
                            pB.set_serviceName(providersServiceJSONobject.getString("service_name"));
                            pB.set_serviceCharge(providersServiceJSONobject.getString("rate"));

                            pB.set__ProviderName(providersServiceJSONobject.getString("expertise_name"));
                            pB.setStr_service_information(providersServiceJSONobject.getString("description"));
                            Log.e("service", providersServiceJSONobject.getString("service_name"));
                            Log.e("rate", providersServiceJSONobject.getString("rate"));
                            alPB.add(pB);
                        }
                        AdminProviderServiceAdapter pServiceadapter = new AdminProviderServiceAdapter(Admin_ProfilePage.this, alPB);
                        listServices.setAdapter(pServiceadapter);
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    //profile


    //getprofiledetails
    class GetProfileDetails extends AsyncTask {
        String provider_id = null;
        String sResponse = null;

        public GetProfileDetails(String provider_id) {
            this.provider_id = provider_id;

        }

        @Override
        protected void onPreExecute() {
           progressBar2.setVisibility(View.VISIBLE);
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] param) {

            try {
                String charset = "UTF-8";
                String requestURL = gD.common_baseurl+"selectprovider.php";
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);

                multipart.addHeaderField("Content-Encoding", "gzip");
                multipart.addFormField("providerid", provider_id);
                List<String> response = multipart.finish();

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
                e.printStackTrace();
            }
            return sResponse;
        }

        @Override
        protected void onPostExecute(Object response) {





            try {
                JSONObject jsobj = new JSONObject(sResponse);
                alPB = new ArrayList();
                alPRB = new ArrayList();
                Log.i("TT" ,sResponse );
                if (jsobj.getString("status").equalsIgnoreCase("success")) {
                    progressBar2.setVisibility(View.GONE);


                    JSONArray providersJSONArray = jsobj.getJSONArray("providers");
                    JSONArray providerServicesJSONArray = jsobj.getJSONArray("services");

                    for (int i = 0; i < providersJSONArray.length(); i++) {
                        JSONObject pObj = providersJSONArray.getJSONObject(i);

                        //providerImageimgLoader.getBitmap("http://aryvartdev.com/projects/utician/upload/" + pObj.getString("image_path"));
                        // imgLoader.DisplayImage("http://aryvartdev.com/projects/utician/upload/" + pObj.getString("image_path"), providerImage);





                        if(pObj.getString("image").length()==0) {

                            emptygallery.setVisibility(View.VISIBLE);
                            emptygallery.setText("No image uploaded");


                        }
                        else

                        {
                            try {

                                strGalleryURL = pObj.getString("image").split(",");

                                Log.i("SSS", "galleryimg : " + pObj.getString("image"));


                                alGalleryPath = new ArrayList<String>();
                                for (int j = 0; j < strGalleryURL.length; j++) {
                                    alGalleryPath.add(gD.common_baseurl+"photogallery/" + strGalleryURL[j]);


                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }



                            final ArrayList<ImageItem> alImage = new ArrayList<ImageItem>();
                            for (int k = 0; k < alGalleryPath.size(); k++) {
                                try {

                                    URL url = new URL(alGalleryPath.get(k));
                                    final String strImageURl = alGalleryPath.get(k);

                                    ImageItem imageItems = new ImageItem();
                                    imageItems.setImage(strImageURl);
                                    alImage.add(imageItems);
                                    // gD.callload(ctx, nScreenHeight);

                                    gridAdapter = new GridGalleryAdapter(Admin_ProfilePage.this, R.layout.gallery_grid_items, alImage);
                                    gallery_grid.setAdapter(gridAdapter);
                              /*  if(gD.alertDialog!=null) {
                                    gD.alertDialog.dismiss();
                                }
*/

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                        }












                    }



                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }





















    //acceptdeclinetask
    class GetAcceptDeclineResponse extends AsyncTask {
        String user_id = null;
        String sResponse = null;
        Context cont;
        String status;


        public GetAcceptDeclineResponse(Context context, String userid, String status) {
            this.user_id = userid;
            this.cont = context;
            this.status = status;
        }

        @Override
        protected void onPreExecute() {
            Admin_ProfilePage.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gD.callload(ctx, nScreenHeight);



                }
            });
// TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] param) {
            try {

                String requestURL = gD.common_baseurl+"adminapproveprovider.php?providerid=" + user_id + "&status=" + status;

                //   APIcalls AceptApi = new APIcalls(requestURL);
                // AceptApi.Process();


                try {

                    URL url;
                    HttpURLConnection urlConnection = null;
                    try {
                        Log.i("SR", "Login URL : " + requestURL.trim());
                        System.out.print("strHTTP_LOGIN_URL");
                        url = new URL(requestURL.trim());


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
        protected void onPostExecute(Object response) {


            if(gD.alertDialog!=null) {
                gD.alertDialog.dismiss();
            }
            try {
                ArrayList beanArrayList = new ArrayList<UserHistoryBean>();

                JSONObject jsobj = new JSONObject(sResponse);

                Log.i("HH", "strResp : " + sResponse);

                if (jsobj.getInt("code") == 2)

                {


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

                    tv_alert_Message.setText("You have successfully accept the utician account");

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


                            Intent raiseinvoicerpage = new Intent(Admin_ProfilePage.this, AdminList.class);
                            startActivity(raiseinvoicerpage);
                            finish();
                            alertDialog.dismiss();


                        }
                    });


                } else {



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

                    tv_alert_Message.setText("You have declined the Utician account");

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


                            Intent raiseinvoicerpage = new Intent(Admin_ProfilePage.this, AdminList.class);
                            startActivity(raiseinvoicerpage);
                            finish();
                            alertDialog.dismiss();


                        }
                    });


                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
    @Override
    public void onBackPressed() {

        Intent i=new Intent(Admin_ProfilePage.this,SignInActivity.class);
        startActivity(i);
        finish();;

    }
}


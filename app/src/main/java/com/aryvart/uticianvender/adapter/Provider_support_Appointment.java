package com.aryvart.uticianvender.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.bean.commonBeanSupport;
import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.aryvart.uticianvender.genericclasses.MultipartUtility;

import com.aryvart.uticianvender.imageCache.DisplayImageOptions;
import com.aryvart.uticianvender.imageCache.ImageLoader;
import com.aryvart.uticianvender.provider.Provider_support;
import com.aryvart.uticianvender.utician.SplashActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android3 on 19/7/16.
 */
public class Provider_support_Appointment extends BaseAdapter {
    ArrayList<commonBeanSupport> alReviewBean;
    Context context;
    ImageLoader imageLoader;
    Button btn_submit_review;
    ImageView provider_iamge;
    EditText  edit_phone, edit_message;
    TextView provider_name;
    ImageView close;
    GeneralData gD;
    int nScreenHeight,mScreenHeight;
    ImageLoader imgLoader;
    String str_email, str_phone, str_message, userid, issueid,emailid,phone_number;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    DisplayImageOptions options;

    public Provider_support_Appointment(Context cont, ArrayList<commonBeanSupport> reviewBean) {
        this.context = cont;
        this.alReviewBean = reviewBean;
        imageLoader = new ImageLoader(context);

        gD = new GeneralData(context);
        DisplayMetrics dp = ((Activity) context).getResources().getDisplayMetrics();
        int nHeight = dp.heightPixels;

        nScreenHeight = (int) ((float) nHeight / (float) 2);
        mScreenHeight= (int) ((float) nHeight / (float) 1.4);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_user_icon)
                .showImageForEmptyUri(R.drawable.default_user_icon)
                .showImageOnFail(R.drawable.default_user_icon).cacheInMemory(true)
                .cacheOnDisc(true).considerExifParams(true)
                /* .displayer(new RoundedBitmapDisplayer(2)) */.build();
    }

    @Override
    public int getCount() {
        return alReviewBean.size();
    }

    @Override
    public commonBeanSupport getItem(int position) {
        return alReviewBean.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            ViewHolder view;
            if (convertView == null) {
                Log.i("MM", "IF : ");
                view = new ViewHolder();

                convertView = inflater.inflate(R.layout.support_appointment_items, parent, false);

                // get layout from mobile.xml


                view.service = (TextView) convertView.findViewById(R.id.txt_service);
                view.name = (TextView) convertView.findViewById(R.id.txt_name);
                view.dateDetails = (TextView) convertView.findViewById(R.id.txt_dattime);
                view.btn_issue = (Button) convertView.findViewById(R.id.btn_issue);

                view.imv_support_user = (ImageView) convertView.findViewById(R.id.usr_img);
                //convertView.setOnClickListener(this);



                convertView.setTag(view);

            } else {
                view = (ViewHolder) convertView.getTag();
            }


            final commonBeanSupport accountbean = alReviewBean.get(position);

            userid = accountbean.getUserid();
            issueid = accountbean.getIssueid();


            emailid = accountbean.getStremailid();
            phone_number = accountbean.getStrphonenumbver();

            view.name.setText(accountbean.getStrUserName());
            view.service.setText(accountbean.getStrservices());
            view.dateDetails.setText(accountbean.getStrdateDetails());
            imageLoader.DisplayImage(alReviewBean.get(position).getStrUserImage(), view.imv_support_user);
            view.btn_issue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(context, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } else {

                        LayoutInflater inflater = LayoutInflater.from(context);
                        View itemView1 = inflater.inflate(R.layout.issue_popup, null);

                        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
                        alertDialogBuilder.setView(itemView1);
                        alertDialogBuilder.setCancelable(false);


                        final android.app.AlertDialog alertDialogg = alertDialogBuilder.create();
                        //  alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        alertDialogg.show();

                        LinearLayout llayAlert = (LinearLayout) itemView1.findViewById(R.id.llayalertDialog);
                        // TextView tv_alert_Title = (TextView) dialogLayout.findViewById(R.id.tv_alert_Title);
                        TextView tv_alert_Message = (TextView) itemView1.findViewById(R.id.message);
                        FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, mScreenHeight);
                        llayAlert.setLayoutParams(lparams);


                        btn_submit_review = (Button) itemView1.findViewById(R.id.but_submit);
                        provider_iamge = (ImageView) itemView1.findViewById(R.id.provider_image);
                        provider_name = (TextView) itemView1.findViewById(R.id.txt_providername);


                        SplashActivity.sharedPreferences = context.getSharedPreferences("regid", Context.MODE_PRIVATE);
                        Log.i("JJJ", "image.." + SplashActivity.sharedPreferences.getString("user_image", null));
                        provider_name.setText(SplashActivity.sharedPreferences.getString("fullname", null));
                        imageLoader.DisplayImage(SplashActivity.sharedPreferences.getString("user_image", null), provider_iamge);


                        //  edit_email = (EditText) itemView1.findViewById(R.id.edit_email);
                        edit_message = (EditText) itemView1.findViewById(R.id.edit_message);
                        edit_phone = (EditText) itemView1.findViewById(R.id.edit_phone);
                        // edit_email.setText(emailid);
                        String a = phone_number.substring(0, 3);
                        String ab = phone_number.substring(3, 6);
                        String ac = phone_number.substring(6, 10);

                        Log.i("HH", "substring" + phone_number);
                        Log.i("HH", "substring--a--" + a);
                        Log.i("HH", "substring--ab--" + ab);
                        Log.i("HH", "substring--ac--" + ac);

                        String newstring = "(" + phone_number.substring(0, 3) + ")" + phone_number.substring(3, 6) + "-" + phone_number.substring(6, 10);

                        Log.i("HH", "newstring" + newstring);
                        edit_phone.setText(newstring);


                        close = (ImageView) itemView1.findViewById(R.id.close);

                        close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialogg.dismiss();
                            }
                        });


                        btn_submit_review.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                str_email = emailid;
                                str_message = edit_message.getText().toString();
                                str_phone = edit_phone.getText().toString();
                                if (str_email.length() > 0 && str_message.length() > 0 && str_phone.length() > 0 && str_phone.length() > 9)

                                {
                                    try {

                                        if (gD.isValidEmail(str_email)) {
                                            IssueUploadTask uploadTask = new IssueUploadTask(context,accountbean.getIssueid(),accountbean.getUserid(),accountbean.getStremailid());
                                            uploadTask.execute();


                                        } else {

                                            Toast.makeText(context, "Enter the valid email id !", Toast.LENGTH_SHORT).show();

                                        }


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    if (str_email.length() == 0) {
                                        Toast.makeText(context, "Please enter your email id", Toast.LENGTH_SHORT).show();
                                    } else if (str_phone.length() == 0) {
                                        Toast.makeText(context, "Please enter your phone number", Toast.LENGTH_SHORT).show();
                                    } else if (str_phone.length() < 10) {
                                        Toast.makeText(context, "Please enter valid phone number", Toast.LENGTH_SHORT).show();
                                    } else if (str_message.length() == 0) {
                                        Toast.makeText(context, "Please enter your message ", Toast.LENGTH_SHORT).show();
                                    }
                                }


                            }
                        });


                    }


                }
            });

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return convertView;
    }


    class ViewHolder {

        public TextView name, service;
        public TextView dateDetails;
        public Button btn_issue;
        public ImageView imv_support_user;

    }

    class IssueUploadTask extends AsyncTask {

        String sResponse = null;
        ProgressDialog dialog;
        Context context;
        String issuteid,prid,stremailid;

        public IssueUploadTask(Context context, String issueid, String userid, String stremailid) {
            this.context = context;
            issuteid=issueid;
            prid=userid;
            this. stremailid=stremailid;


        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {


                String charset = "UTF-8";

                String requestURL = gD.common_baseurl+"issueraise.php";


                // 4. separate class for multipart content image uploaded task----------- vinoth
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);

                multipart.addHeaderField("User-Agent", "rajaji");
                multipart.addHeaderField("Test-Header", "Header-Value");
                multipart.addHeaderField("Content-Encoding", "gzip");

                multipart.addFormField("keywords", "Java,upload,Spring");

                // 5. set the user_image key word and multipart image file value ----------- vinoth

                multipart.addFormField("issueid", issuteid.trim());
                multipart.addFormField("email", stremailid.trim());

                multipart.addFormField("userid", prid.trim());
                Log.e("issueid", issuteid);
                Log.e("email", stremailid);
                Log.e("userid", prid);
                multipart.addFormField("phone", str_phone.trim());


                multipart.addFormField("message", str_message.trim());


                multipart.addFormField("idtype", "1");
                multipart.addFormField("issueon", "1");

                multipart.addFormField("providerid", SplashActivity.sharedPreferences.getString("UID", null));


                Log.e("phone", str_phone);
                Log.e("message", str_message);



                Log.e("providerid", SplashActivity.sharedPreferences.getString("UID", null));

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

                        tv_alert_Message.setText("Thanks for submitting your issue! A Utician member will reachout to you shortly");
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
                                ((Activity) context).finish();
                                Intent i=new Intent(context, Provider_support.class);
                                context.startActivity(i);

                                alertDialog.dismiss();


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

package com.aryvart.uticianvender.admin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryvart.uticianvender.Interface.GetNotification;
import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.aryvart.uticianvender.genericclasses.MultipartUtility;
import com.aryvart.uticianvender.utician.SplashActivity;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by android3 on 4/11/16.
 */
public class Admin_Share_Pay extends Activity implements GetNotification {


    ImageView menu_back;
    LinearLayout lay_transaction, lay_invoice, referred_utician;
    ImageView image_transaction, image_invoice, image_account;
    TextView textContent, balance;
    ProgressDialog pD;
    JSONArray notifyPending, notifyfulltrans,notify_referred_utiican;
    RecyclerView recyclerView;
    TextView emptyText;
    Context ctx;
    GeneralData gD;
    int nScreenHeight;


    String str_id, str_paypalid, str_totalamt, str_jsonvalue,str_keyvalue;

    private Admin_Share_Pay_Adapter mAdapter;


    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;


    private static final String CONFIG_CLIENT_ID = "AVbzvh0SFX_3lj80TLWxqxiKjkl8hP2cs_A4Ve-6OAAeeuZ875dE4sBLsFZiNlmnNdiJsI4fAF_-k2BQ";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID)
                    // The following are only used in PayPalFuturePaymentActivity.
            .merchantName("Hipster Store")
            .merchantPrivacyPolicyUri(
                    Uri.parse("https://www.example.com/privacy"))
            .merchantUserAgreementUri(
                    Uri.parse("https://www.example.com/legal"));



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.admin_share_pay);

            SplashActivity.sharedPreferences = getSharedPreferences("regid", Context.MODE_PRIVATE);

            ctx = this;
            pD = new ProgressDialog(Admin_Share_Pay.this);


            gD = new GeneralData(ctx);

            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = (int) ((float) nHeight / (float) 2.1);

            Intent intent = new Intent(this, PayPalService.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
            startService(intent);


            textContent = (TextView) findViewById(R.id.textContent);
            balance = (TextView) findViewById(R.id.balance);
            emptyText = (TextView) findViewById(R.id.emptyText);
            menu_back = (ImageView) findViewById(R.id.menu_back);
            image_transaction = (ImageView) findViewById(R.id.image_transaction);
            image_invoice = (ImageView) findViewById(R.id.image_invoice);
              image_account = (ImageView) findViewById(R.id.image_refer);
            recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
            lay_transaction = (LinearLayout) findViewById(R.id.lay_transaction);
            lay_invoice = (LinearLayout) findViewById(R.id.lay_invoice);

            referred_utician = (LinearLayout) findViewById(R.id.referred_utician);
            //  lay_account = (LinearLayout) findViewById(R.id.lay_account);
            textContent.setText("Client");
            image_transaction.setImageResource(R.drawable.apendingrednew);
       /* showView("transactions");*/
            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(ctx, "Unable to connect with Internet. Please check your internet connection and try again");
            }
            else {
                GetTranscationDetails get_Provider_details = new GetTranscationDetails(ctx, recyclerView);
                get_Provider_details.execute();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                getWindow().setStatusBarColor(getResources().getColor(R.color.app_green));
            }

            lay_transaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textContent.setText("Client");
                    image_transaction.setImageResource(R.drawable.apendingrednew);
                    image_invoice.setImageResource(R.drawable.apendingreennew);
                     image_account.setImageResource(R.drawable.apendingreennew);
                    showView("client");

                }
            });
            lay_invoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    textContent.setText("Utician");
                    image_transaction.setImageResource(R.drawable.apendingreennew);
                    image_invoice.setImageResource(R.drawable.apendingrednew);
                     image_account.setImageResource(R.drawable.apendingreennew);
                    showView("utician");

                }
            });
            referred_utician.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textContent.setText("Referred Utician");
                    image_transaction.setImageResource(R.drawable.apendingreennew);
                    image_invoice.setImageResource(R.drawable.apendingreennew);
                    image_account.setImageResource(R.drawable.apendingrednew);
                    showView("referred_utiican");

                }
            });
            menu_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        finish();
                        Intent i = new Intent(ctx, AdminList.class);
                        //  gD.screenback = 0;
                        startActivity(i);

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showView(String data) {
       /* Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();*/
        try {
            if (data.equalsIgnoreCase("client")) {
                LoadLayout(recyclerView, notifyfulltrans, data);
                if (notifyfulltrans.length() > 0) {
                    emptyText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    emptyText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }

            }

            else  if (data.equalsIgnoreCase("utician")) {
               /* if (providerServicesPrevWeek.length() > 0) {*/
                LoadLayout(recyclerView, notifyPending, data);
               /* } else {
                    Toast.makeText(UserHistoryPage.this, "No items to be found !", Toast.LENGTH_SHORT).show();
                }*/
                if (notifyPending.length() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyText.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    emptyText.setVisibility(View.VISIBLE);
                }
            }



            else {
               /* if (providerServicesPrevWeek.length() > 0) {*/
                LoadLayout(recyclerView, notify_referred_utiican, "referal_provider");
               /* } else {
                    Toast.makeText(UserHistoryPage.this, "No items to be found !", Toast.LENGTH_SHORT).show();
                }*/
                if (notify_referred_utiican.length() > 0) {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyText.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    emptyText.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void LoadLayout(RecyclerView recyclerView, JSONArray providerServicesMonth, String data) {

        String name = null, image = null, time = null, date = null;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
     /*   recyclerView.addItemDecoration(new com.aryvart.utician.adapter.DividerItemDecoration(this, LinearLayoutManager.VERTICAL));*/
        ArrayList beanArrayList = new ArrayList<AdminProviderListBean>();

        JSONObject userNotifyJSONobject = null;
        JSONObject usertransactionJSONobject = null;


        try {
            for (int i = 0; i < providerServicesMonth.length(); i++) {
                AdminProviderListBean adminProviderbean = new AdminProviderListBean();
                usertransactionJSONobject = providerServicesMonth.getJSONObject(i);
                adminProviderbean.setProvider_ID(usertransactionJSONobject.getString("id"));
                adminProviderbean.setProvider_Name(usertransactionJSONobject.getString("fullname"));
                adminProviderbean.setProvider_image(gD.common_baseurl+"upload/" + usertransactionJSONobject.getString("image_path"));

if(data.equalsIgnoreCase("referal_provider")) {
    adminProviderbean.setCompany_name(usertransactionJSONobject.getString("booking_count"));
}
                else
{
    adminProviderbean.setCompany_name(usertransactionJSONobject.getString("referal_count"));
}
                adminProviderbean.setStr_usertype(usertransactionJSONobject.getString("usertype"));

                adminProviderbean.setStr_pro_balance(usertransactionJSONobject.getString("amount"));
                adminProviderbean.setStr_key(usertransactionJSONobject.getString("key"));

                Log.i("HH", "key : " + usertransactionJSONobject.getString("key"));
                adminProviderbean.setStrpaypalid(usertransactionJSONobject.getString("paypal_id"));
                adminProviderbean.setStr_jsonvalues(usertransactionJSONobject.toString());
                beanArrayList.add(adminProviderbean);
            }
                /*AdminIssueAdapter userHistoryAdpater = new AdminIssueAdapter(beanArrayList, AdminIssue.this);
                recyclerView.setAdapter(userHistoryAdpater);*/
            mAdapter = new Admin_Share_Pay_Adapter(beanArrayList, Admin_Share_Pay.this, (GetNotification) ctx);
            recyclerView.setAdapter(mAdapter);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(ctx, AdminList.class);
        //  gD.screenback = 0;
        startActivity(i);
    }
    @Override
    public void setNotificationId(String strGCMid) {

    }

    @Override
    public void setProviderDetails(String strid, String strpaypailid, String strcount, String strtotal, String expertiseid, String strReferalcdoe) {


    }


    @Override
    public void getProviderId(String strProviderId, String strType) {

        Intent i = new Intent(Admin_Share_Pay.this, Admin_Transaction_details.class);
        i.putExtra("providerid", strProviderId);
        i.putExtra("jsonvalues", strType);
        Log.i("II..", "str_providerid" + strProviderId);
        Log.i("II..", "jsonvalues" + strType);
        startActivity(i);
        finish();


    }

    @Override
    public void getServicePrice(String n_price) {

    }

    @Override
    public void getServiceDuration(String n_duration) {

    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == REQUEST_CODE_PAYMENT) {
                if (resultCode == Activity.RESULT_OK) {
                    PaymentConfirmation confirm = data
                            .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                    if (confirm != null) {
                        try {


                            JSONObject jsonObj = new JSONObject(confirm.toJSONObject().toString());

                            final String paymentId = jsonObj.getJSONObject("response").getString("id").trim();
                            Log.i("RR", "paymentId" + paymentId);


                            System.out.println(confirm.toJSONObject().toString(4));
                            System.out.println(confirm.getPayment().toJSONObject()
                                    .toString(4));
                            Log.i("RR", "paymentId ***" + confirm.toJSONObject().toString(4));
                            Log.i("RR", "paymentId ###" + confirm.getPayment().toJSONObject()
                                    .toString(4));
                            Log.i("RR", "Success");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    System.out.println("The user canceled.");
                } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                    System.out
                            .println("An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
                }
            } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
                if (resultCode == Activity.RESULT_OK) {
                    PayPalAuthorization auth = data
                            .getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                    if (auth != null) {
                        try {
                            Log.i("FuturePaymentExample", auth.toJSONObject()
                                    .toString(4));

                            String authorization_code = auth.getAuthorizationCode();
                            Log.i("FuturePaymentExample", authorization_code);

                            sendAuthorizationToServer(auth);
                            Toast.makeText(getApplicationContext(),
                                    "Future Payment code received from PayPal",
                                    Toast.LENGTH_LONG).show();

                        } catch (JSONException e) {
                            Log.e("FuturePaymentExample",
                                    "an extremely unlikely failure occurred: ", e);
                        }
                    }
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.i("FuturePaymentExample", "The user canceled.");
                } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {
                    Log.i("FuturePaymentExample",
                            "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendAuthorizationToServer(PayPalAuthorization authorization) {

    }

    public void onFuturePaymentPurchasePressed(View pressed) {
        // Get the Application Correlation ID from the SDK
        String correlationId = PayPalConfiguration
                .getApplicationCorrelationId(this);

        Log.i("FuturePaymentExample", "Application Correlation ID: "
                + correlationId);

        // TODO: Send correlationId and transaction details to your server for
        // processing with
        // PayPal...
        Toast.makeText(getApplicationContext(),
                "App Correlation ID received from SDK", Toast.LENGTH_LONG)
                .show();
    }

    @Override
    public void onDestroy() {
        // Stop service when done
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    public ArrayList<String> getServicName(TreeMap<Integer, String> str_serviceName) {
        return null;
    }

    @Override
    public void setProviderdet(String strid, String strpaypailid, String strcount, String strtotal, String jsonvalue, String str_key) {

        try {
            AdminPayTask adminPayTask = new AdminPayTask(strid,strpaypailid,strtotal,jsonvalue,str_key,ctx);
            adminPayTask.execute();


            str_id = strid;
            str_paypalid = strpaypailid;
            str_totalamt = strtotal;
            str_jsonvalue = jsonvalue;
            str_keyvalue=str_key;

            Log.i("TT", "paypalid **: " + str_paypalid);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void onFuturePaymentPressed(View pressed) {
        Intent intent = new Intent(Admin_Share_Pay.this,
                PayPalFuturePaymentActivity.class);

        startActivityForResult(intent, REQUEST_CODE_FUTURE_PAYMENT);
    }

    @Override
    public void NotifyFilter(String strFilterVal) {

    }

    @Override
    public void setProvideraddress(String strlatitude, String strlongitude, String straddress) {

    }

    class GetTranscationDetails extends AsyncTask {
        String user_id = null;
        String sResponse = null;
        Context cont;
        RecyclerView recycler;

        public GetTranscationDetails(Context context, RecyclerView recyclerView) {

            this.cont = context;
            this.recycler = recyclerView;

        }

        @Override
        protected void onPreExecute() {
            Admin_Share_Pay.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    gD.callload(ctx, nScreenHeight);



                }
            });
        }

        @Override
        protected Object doInBackground(Object[] param) {
            try {
                String charset = "UTF-8";
                String requestURL = gD.common_baseurl+"referaluserlist.php";
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);

                multipart.addHeaderField("Accept", "application*/*");
                multipart.addHeaderField("Content-Encoding", "gzip");
                multipart.addHeaderField("Content-type", "application/x-www-form-urlencoded");
                // multipart.addFormField("providerid", SplashActivity.sharedPreferences.getString("UID", null));
                List<String> response = multipart.finish();

                StringBuilder sb = new StringBuilder();
                for (String line : response) {
                    System.out.println(line);
                    sb.append(line);
                }
                try {
                    JSONObject jsonObj = new JSONObject(sb.toString());
                    Log.i("NNN-notify", "StrResp : " + jsonObj.toString());
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
                String name = null, image = null, time = null, date = null;
                ArrayList beanArrayList = new ArrayList<AdminProviderListBean>();
                Log.i("HH", "strResp : " + beanArrayList.size());
                JSONObject jsobj = new JSONObject(sResponse);
                JSONObject usertransactionJSONobject = null;

                Log.i("HH", "strResp : " + sResponse);

                if (jsobj.getString("status").equalsIgnoreCase("success")) {
                    JSONArray providerActionJSONArray = jsobj.getJSONArray("userlist");
                    notifyPending = jsobj.getJSONArray("providerlist");
                    notifyfulltrans = jsobj.getJSONArray("userlist");
                    notify_referred_utiican = jsobj.getJSONArray("referedproviders");

                    if (providerActionJSONArray.length() > 0) {
                        for (int i = 0; i < providerActionJSONArray.length(); i++) {
                            AdminProviderListBean adminProviderbean = new AdminProviderListBean();
                            usertransactionJSONobject = notifyfulltrans.getJSONObject(i);
                            adminProviderbean.setProvider_ID(usertransactionJSONobject.getString("id"));
                            adminProviderbean.setProvider_Name(usertransactionJSONobject.getString("fullname"));
                            adminProviderbean.setProvider_image(gD.common_baseurl+"upload/" + usertransactionJSONobject.getString("image_path"));
                            adminProviderbean.setCompany_name(usertransactionJSONobject.getString("referal_count"));
                            Log.i("TT", "count Adapter **: " + usertransactionJSONobject.getString("referal_count"));

                            adminProviderbean.setStr_pro_balance(usertransactionJSONobject.getString("amount"));
                            adminProviderbean.setStr_key(usertransactionJSONobject.getString("key"));

                            adminProviderbean.setStr_usertype(usertransactionJSONobject.getString("usertype"));

                            Log.i("HH", "key : " + usertransactionJSONobject.getString("key"));
                            adminProviderbean.setStrpaypalid(usertransactionJSONobject.getString("paypal_id"));
                            adminProviderbean.setStr_jsonvalues(usertransactionJSONobject.toString());
                            beanArrayList.add(adminProviderbean);
                        }
                    } else {
                        emptyText.setVisibility(View.VISIBLE);
                    }

                }

                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                recycler.setLayoutManager(mLayoutManager);
                recycler.setItemAnimator(new DefaultItemAnimator());

                mAdapter = new Admin_Share_Pay_Adapter(beanArrayList, Admin_Share_Pay.this, (GetNotification) ctx);
                recycler.setAdapter(mAdapter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    class AdminPayTask extends AsyncTask {

        String sResponse = null;
        Context context;

        String str_paypal, strid, strkey, strtotalamount, strjsonresponse;



        public AdminPayTask(String str_id, String str_paypalid, String str_totalamt, String str_jsonvalue, String str_key, Context ctx) {

            str_paypal = str_paypalid;
           strid = str_id;
            strtotalamount=str_totalamt;
            strjsonresponse=str_jsonvalue;
            context = ctx;
            strkey=str_key;

        }


        @Override
        protected Object doInBackground(Object[] params) {
            try {


                String charset = "UTF-8";

                String requestURL = gD.common_baseurl+"referalpay.php";


                // 4. separate class for multipart content image uploaded task----------- vinoth
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);
                multipart.addHeaderField("Content-Encoding", "gzip");

                Log.i("TT", "strid : " + strid);


                Log.i("TT", "paypalid : " + str_paypal);
                Log.i("TT", "strtotalamount : " + strtotalamount);


                multipart.addFormField("id", strid);


                multipart.addFormField("paypalid", str_paypal);
                multipart.addFormField("amount", strtotalamount);

                if (strkey.equalsIgnoreCase("3"))
                {

                             multipart.addFormField("providerpay", String.valueOf(1));
                    Log.i("TT", "providerpay : " + String.valueOf(1));
                }
                //multipart.addFormField("providerid", SplashActivity.sharedPreferences.getString("UID", null));



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

                Log.i("NN", "i'm catched");
                e.printStackTrace();
            }


            return sResponse;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            gD.callload(ctx, nScreenHeight);


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
                        Intent i = new Intent(Admin_Share_Pay.this, Admin_Amount_Pay.class);
                    i.putExtra("Resp",sResponse);
                   startActivity(i);
                        finish();
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

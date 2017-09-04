package com.aryvart.uticianvender.provider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.bean.SpinnerBean;
import com.aryvart.uticianvender.genericclasses.AlertDialogManager;
import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.aryvart.uticianvender.genericclasses.MultipartUtility;
import com.aryvart.uticianvender.user.SpinnerAdapter;
import com.aryvart.uticianvender.utician.SignInActivity;
import com.aryvart.uticianvender.utician.SplashActivity;

import org.json.JSONObject;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by android1 on 29/7/16.
 */
public class ProviderCompanyInfoPay extends Activity implements AdapterView.OnItemSelectedListener {
    ImageView img_back;
    TextView ll_next;

    String str_get_paypal_id;
    AlertDialogManager alertDialog;
    Context context;
    String region_spinvalue, locality_spinvalue;
    GeneralData gD;
    int nScreenHeight,nnScreenHeight;
    int n_spinitem_region, nspinitem_locality;
    Spinner ed_locality, ed_region;

    String str_get_first_name, str_get_last_name, str_get_address, str_get_postalcode, str_get_region, str_get_account_no, str_get_routing, str_get_taxid;
    EditText ed_first_name, ed_last_name, ed_address_name, ed_account_no, ed_taxid, ed_routing, ed_postalcode;
    private ProgressDialog pdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.provider_companyinfo_payment);
            img_back = (ImageView) findViewById(R.id.img_back);
            ll_next = (TextView) findViewById(R.id.llayNext);
            context = this;
            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = (int) ((float) nHeight / (float) 2.1);
            nnScreenHeight = (int) ((float) nHeight / (float) 2.3);
            SplashActivity.sharedPreferences = getSharedPreferences("regid", Context.MODE_PRIVATE);

            ll_next.setText("Next");
            gD = new GeneralData(context);
            pdialog = new ProgressDialog(context);

            SplashActivity.autoLoginPreference = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
            alertDialog = new AlertDialogManager();

            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(context, "Unable to connect with Internet. Please check your internet connection and try again");
            }

            ed_first_name = (EditText) findViewById(R.id.ed_first_name);
            ed_last_name = (EditText) findViewById(R.id.ed_last_name);
            ed_address_name = (EditText) findViewById(R.id.ed_address_name);
            ed_locality = (Spinner) findViewById(R.id.ed_locality);
            ed_postalcode = (EditText) findViewById(R.id.ed_postalcode);
            ed_region = (Spinner) findViewById(R.id.ed_region);
            ed_account_no = (EditText) findViewById(R.id.ed_account_no);
            ed_routing = (EditText) findViewById(R.id.ed_routing);
            ed_routing.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {

callevent();





                        return true;
                    }
                    return false;
                }
            });

            ed_taxid = (EditText) findViewById(R.id.ed_taxid);
            ed_locality.setOnItemSelectedListener(this);
            ed_region.setOnItemSelectedListener(this);

            ArrayList<SpinnerBean> alBeanLoc = new ArrayList<SpinnerBean>();
            SpinnerBean sB = new SpinnerBean(context,"");
            sB.setSpinName("CITY");
            alBeanLoc.add(sB);

            SpinnerAdapter spinadapter = new SpinnerAdapter(ProviderCompanyInfoPay.this, alBeanLoc);
            ed_locality.setAdapter(spinadapter);

            ArrayList<SpinnerBean> alBeanReg = new ArrayList<SpinnerBean>();
            SpinnerBean sB1 = new SpinnerBean(context,"");
            sB1.setSpinName("STATE");
            alBeanReg.add(sB1);

            SpinnerAdapter spinadapterreg = new SpinnerAdapter(ProviderCompanyInfoPay.this, alBeanReg);
            ed_region.setAdapter(spinadapterreg);

            ed_region.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    if (position != 0) {
                        n_spinitem_region = position;
                        SpinnerBean mSelected = (SpinnerBean) parentView.getItemAtPosition(position);
                        region_spinvalue = mSelected.getSpinName();
                    } else {
                        n_spinitem_region = 0;

                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {

                }
            });
            ed_locality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

                    if (position != 0) {
                        nspinitem_locality = position;
                        SpinnerBean mSelected = (SpinnerBean) parentView.getItemAtPosition(position);
                        locality_spinvalue = mSelected.getSpinName();
                    } else {
                        nspinitem_locality = 0;

                        // Showing selected spinner item
                        //    Toast.makeText(ProviderCompanyInfoPay.this, "Selected locality: " + locality_spinvalue, Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {

                }
            });

            ed_postalcode.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start, int before,
                                          int count) {
                    // TODO Auto-generated method stub
                    Log.i("GG","Intiialtext" + s.length());
                    Log.i("GG","Intiialtext" +start);
                    Log.i("GG","Intiialtext" +count);
                    Log.i("GG","Intiialtext" +before);


                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.i("GG","AFTERCHANDE" +  s.length());
                    if (ed_postalcode.length() >= 5 && ed_postalcode.length()<=9) {
                        Log.i("GG", "AFTERCHANDE ***" + s.length());

                        GetAddresinfoTask Gdt = new GetAddresinfoTask("get", "http://api.geonames.org/postalCodeSearch?postalcode=" + ed_postalcode.getText().toString().trim() + "&maxRows=10&username=aryvartzip");
                        Gdt.execute();


                    }

                    else
                    {
                        ArrayList<SpinnerBean> spin = new ArrayList<SpinnerBean>();
                       SpinnerBean spinbean=new SpinnerBean(context,"");
                        spinbean.setSpinName("CITY");
                        spin.add(spinbean);
                        SpinnerAdapter spinadapter = new SpinnerAdapter(ProviderCompanyInfoPay.this, spin);
                        ed_locality.setAdapter(spinadapter);

                        ArrayList<SpinnerBean> spin_reg = new ArrayList<SpinnerBean>();
                        SpinnerBean spinbean_reg=new SpinnerBean(context,"");
                        spinbean_reg.setSpinName("STATE");
                        spin_reg.add(spinbean_reg);
                        SpinnerAdapter spinadapter_reg = new SpinnerAdapter(ProviderCompanyInfoPay.this, spin_reg);
                        ed_region.setAdapter(spinadapter_reg);


                        Log.i("GG", "AFTERCHANDE  ###" + s.length());

                    }

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count,
                                              int after) {
                    // TODO Auto-generated method stub
                    Log.i("GG","beforeCHANDE" +s.length());
                    Log.i("GG","beforeCHANDE" +start);
                    Log.i("GG","beforeCHANDE" +count);
                    Log.i("GG","beforeCHANDE" +after);

                }

            });

            img_back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupcall("Do you want to logout",0,"back",nnScreenHeight);

/*
                    SharedPreferences.Editor editor = SplashActivity.autoLoginPreference.edit();
                    editor.putString("uname", null);
                    editor.putString("pwd", null);
                    editor.commit();
                    finish();*/
                }
            });
            ll_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //  str_get_paypal_id=ed_paypal_id.getText().toString().trim();
                    callevent();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

public  void callevent()
{

    try {
        if (!gD.isConnectingToInternet()) {
            Toast.makeText(ProviderCompanyInfoPay.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
        } else {

            str_get_first_name = ed_first_name.getText().toString().trim();
            str_get_last_name = ed_last_name.getText().toString().trim();
            str_get_address = ed_address_name.getText().toString().trim();
            //  str_get_locality= ed_locality.getText().toString().trim();
            str_get_postalcode = ed_postalcode.getText().toString().trim();
            //  str_get_region= ed_region.getText().toString().trim();
            str_get_account_no = ed_account_no.getText().toString().trim();
            str_get_routing = ed_routing.getText().toString().trim();

            str_get_taxid = ed_taxid.getText().toString().trim();


            //  Log.i("paypal_id", str_get_paypal_id);

            if (str_get_first_name.length() > 0 && str_get_last_name.length() > 0 && str_get_address.length() > 0 && str_get_postalcode.length() > 0 && nspinitem_locality > 0 && n_spinitem_region > 0 && str_get_taxid.length() > 0 && str_get_account_no.length() > 0 && str_get_routing.length() > 0 && str_get_taxid.length() > 8 && str_get_routing.length() > 8) {

                //  if (gD.isValidEmail(str_get_paypal_id)) {

                ProviderCompanyInfoPayTask pCT = new ProviderCompanyInfoPayTask(str_get_paypal_id);
                pCT.execute();
                //  }



            } else {
                if (str_get_first_name.length() == 0) {
                    Toast.makeText(ProviderCompanyInfoPay.this, "First name can't be empty!", Toast.LENGTH_SHORT).show();
                } else if (str_get_last_name.length() == 0) {
                    Toast.makeText(ProviderCompanyInfoPay.this, "Last name can't be empty!", Toast.LENGTH_SHORT).show();
                } else if (str_get_address.length() == 0) {
                    Toast.makeText(ProviderCompanyInfoPay.this, "Address can't be empty!", Toast.LENGTH_SHORT).show();
                } else if (str_get_postalcode.length() == 0) {
                    Toast.makeText(ProviderCompanyInfoPay.this, "Zip code can't be empty!", Toast.LENGTH_SHORT).show();
                } else if (nspinitem_locality == 0) {
                    Toast.makeText(ProviderCompanyInfoPay.this, "City can't be empty!", Toast.LENGTH_SHORT).show();
                } else if (n_spinitem_region == 0) {
                    Toast.makeText(ProviderCompanyInfoPay.this, "State can't be empty!", Toast.LENGTH_SHORT).show();
                } else if (str_get_taxid.length() == 0) {
                    Toast.makeText(ProviderCompanyInfoPay.this, "Taxid or Social security can't be empty!", Toast.LENGTH_SHORT).show();
                } else if (str_get_taxid.length() < 9) {
                    Toast.makeText(ProviderCompanyInfoPay.this, "Please enter valid taxid!", Toast.LENGTH_SHORT).show();
                }

                else if (str_get_account_no.length() == 0) {
                    Toast.makeText(ProviderCompanyInfoPay.this, "Account number can't be empty!", Toast.LENGTH_SHORT).show();
                }
                else if (str_get_account_no.length() < 10) {
                    Toast.makeText(ProviderCompanyInfoPay.this, "Please enter valid account number!", Toast.LENGTH_SHORT).show();
                }

                else if (str_get_routing.length() == 0) {
                    Toast.makeText(ProviderCompanyInfoPay.this, "Routing number can't be empty!", Toast.LENGTH_SHORT).show();
                } else if (str_get_routing.length() < 9) {
                    Toast.makeText(ProviderCompanyInfoPay.this, "Please enter valid routing number", Toast.LENGTH_SHORT).show();
                }
            }

        }
    } catch (Exception e) {
        e.printStackTrace();
    }


}
    @Override
    public void onBackPressed() {
        popupcall("Do you want to logout",0,"back", nnScreenHeight);

     /*   SharedPreferences.Editor editor = SplashActivity.autoLoginPreference.edit();
        editor.putString("uname", null);
        editor.putString("pwd", null);
        editor.commit();
        finish();*/
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        //  Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    class GetAddresinfoTask extends AsyncTask {

        String strResp;
        String strempty;
        String strRequest;
        String strUrl;
        NodeList nodelist;

        GetAddresinfoTask(String strReq, String strURL) {
            this.strRequest = strReq;
            this.strUrl = strURL;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] params) {



            try {
                URL url = new URL(strUrl);
                DocumentBuilderFactory dbf = DocumentBuilderFactory
                        .newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                // Download the XML file
                Document doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();
                // Locate the Tag Name
                nodelist = doc.getElementsByTagName("code");


            } catch (Exception e) {
                e.printStackTrace();
            }

            return strResp;
        }

        @Override
        protected void onPostExecute(Object o) {

            try {

                Log.i("NN", "strResp : " + strResp);

                ArrayList<SpinnerBean> alBeanLocality = new ArrayList<SpinnerBean>();
                ArrayList<SpinnerBean> alBeanRegion = new ArrayList<SpinnerBean>();
                LoadSpinner(ed_locality, "name", "CITY", alBeanLocality);
                LoadSpinner(ed_region, "adminCode1", "STATE", alBeanRegion);


            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPostExecute(o);
        }

        private String getNode(String sTag, Element eElement) {
            String strReturnval = null;
            try {
                NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
                strReturnval = "";
                if (nlList.getLength() > 0) {
                    Node nValue = (Node) nlList.item(0);

                    if (nValue.getNodeValue() != null) {
                        strReturnval = nValue.getNodeValue();
                        return strReturnval;
                    }

                } else {
                    strReturnval = "";

                    return strReturnval;
                }
            } catch (DOMException e) {
                e.printStackTrace();
            }

            return strReturnval;
        }

        private void LoadSpinner(Spinner spin, String strNode, String strSpinfirst, ArrayList<SpinnerBean> alBean) {


            try {
                SpinnerBean sB = new SpinnerBean(context,"");
                sB.setSpinName(strSpinfirst);
                alBean.add(sB);
                Log.i("NN", "alBean : " + alBean.toString());


                for (int temp = 0; temp < nodelist.getLength(); temp++) {
                    Node nNodeVal = nodelist.item(temp);
                    if (nNodeVal.getNodeType() == Node.ELEMENT_NODE) {

                        Element eElement = (Element) nNodeVal;

                        String strNodeValue = getNode(strNode, eElement);
                        SpinnerBean sB1 = new SpinnerBean(context,strNode);
                        if (strNodeValue.length() > 0) {
                            sB1.setSpinName(strNodeValue);
                        }
                        // hs.addAll(alBean);
                        alBean.add(sB1);
                        Log.i("NN", "alBean--> : " + alBean.toString());
                    }
                }

                //Converting ArrayList to HashSet to remove duplicates
                LinkedHashSet<SpinnerBean> listToSet = new LinkedHashSet<SpinnerBean>(alBean);

                //Creating Arraylist without duplicate values
                ArrayList<SpinnerBean> listWithoutDuplicates = new ArrayList<SpinnerBean>(listToSet);

                System.out.println("size of ArrayList without duplicates: " + listToSet.size());
                System.out.println("ArrayList after removing duplicates in same order: " + listWithoutDuplicates);


                if (alBean.size() > 1) {

                      /*  Set<SpinnerBean> hs = new HashSet<>();
                        hs.addAll(alBean);
                        alBean.clear();
                        alBean.addAll(hs);

                        Log.e("UOP", alBean.toString());
                        Log.i("NN", "alBean 2--> : " + alBean.toString());
                        Log.i("NN", "Set--> : " + alBean.toString());*/
                        SpinnerAdapter spinadapter = new SpinnerAdapter(ProviderCompanyInfoPay.this, listWithoutDuplicates);
                        spin.setAdapter(spinadapter);
                       // ed_locality.setSelection(0);
                       // ed_region.setSelection(0);
                        // spinadapter.clear();
                        spinadapter.notifyDataSetChanged();
                    } else {

                        Toast.makeText(ProviderCompanyInfoPay.this, "Please enter the correct zip code ", Toast.LENGTH_SHORT).show();
                       // Toast.makeText(ProviderCompanyInfoPay.this, "Please enter the correct zip code otherwise you can't choose the area for further operation to be done !", Toast.LENGTH_SHORT).show();
                    }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }





    }

    class ProviderCompanyInfoPayTask extends AsyncTask {

        String sResponse = null;

        String str_paypal, str_social_sec, strMulimages;

        public ProviderCompanyInfoPayTask(String strPaypal) {
            str_paypal = strPaypal;
        }


        @Override
        protected Object doInBackground(Object[] params) {
            try {


                String charset = "UTF-8";

                String requestURL = gD.common_baseurl+"braintree/html/submerchant.php";


                // 4. separate class for multipart content image uploaded task----------- vinoth
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);

                multipart.addHeaderField("Content-Encoding", "gzip");
                // 5. set the user_image key word and multipart image file value ----------- vinoth

                // strCname, strCaddress, strNemp, strMulimages, strLicenseupload, strSsecurityNo

                //  Log.i("TT", "paypalid : " + str_paypal);
                multipart.addFormField("firstname", str_get_first_name);
                multipart.addFormField("lastname", str_get_last_name);
                multipart.addFormField("streetaddress", str_get_address);
                multipart.addFormField("region", region_spinvalue);
                multipart.addFormField("postalcode", str_get_postalcode);
                multipart.addFormField("taxid", str_get_taxid);
                multipart.addFormField("locality", locality_spinvalue);

                multipart.addFormField("accountnumber", str_get_account_no);
                multipart.addFormField("routingnumber", str_get_routing);


                Log.i("KK", "firstname" + str_get_first_name);
                Log.i("KK", "lastname" + str_get_last_name);
                Log.i("KK", "streetaddress" + str_get_address);
                Log.i("KK", "region" + region_spinvalue);
                Log.i("KK", "postalcode" + str_get_postalcode);
                Log.i("KK", "taxid" + str_get_taxid);
                Log.i("KK", "locality" + locality_spinvalue);
                Log.i("KK", "accountnumber" + str_get_account_no);
                Log.i("KK", "routingnumber" + str_get_routing);

                //multipart.addFormField("providerid", SplashActivity.sharedPreferences.getString("UID", null));

                multipart.addFormField("providerid", SplashActivity.sharedPreferences.getString("UID", null));

                //  Log.i("HH", " strrealpath : " + strrealpath + "**********" + strrealpath.trim().length());


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


                        String strservicetype = jsobj.getJSONObject("provider_details").getString("services");
                        SharedPreferences.Editor editor = SplashActivity.sharedPreferences.edit();
                        editor.putString("serviceType", strservicetype);
                        editor.commit();

                        popupcall("You successfully added your information",1,"from_api", nScreenHeight);


                    } else if (jsobj.getInt("code") == 3) {
                        Toast.makeText(ProviderCompanyInfoPay.this, jsobj.getString("message"), Toast.LENGTH_SHORT).show();
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


    public void popupcall(String s, int i, final String str_res, int str_size)
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


            FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, str_size);
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
                        Intent user_category = new Intent(ProviderCompanyInfoPay.this, ProviderAddServices.class);
                        user_category.putExtra("screenFrom", 1);
                        startActivity(user_category);
                        finish();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            finishAffinity();
                        }
                        alertDialog.dismiss();

                    }
                    else
                    {
                        SharedPreferences.Editor editor = SplashActivity.autoLoginPreference.edit();
                        editor.putString("uname", null);
                        editor.putString("pwd", null);
                        editor.commit();

                        Intent user_category = new Intent(ProviderCompanyInfoPay.this, SignInActivity.class);

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

}

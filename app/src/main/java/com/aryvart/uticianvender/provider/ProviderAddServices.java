package com.aryvart.uticianvender.provider;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
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
import com.aryvart.uticianvender.adapter.CustomArrayAdapter;
import com.aryvart.uticianvender.bean.SpinnerBean;
import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.aryvart.uticianvender.genericclasses.MultipartUtility;
import com.aryvart.uticianvender.utician.SignInActivity;
import com.aryvart.uticianvender.utician.SplashActivity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by ${Rajaji} on 12-07-2016.
 */
public class ProviderAddServices extends Activity {
    Context context;
    ImageView imvAddServices;
    int nCount = 0;
    LinearLayout llayParent, llaySubmit, llayBack;

    int nFrom;
    int nScreenHeight,nnScreenHeight;
    ArrayList<Integer> alCheckServicePresent = new ArrayList<Integer>();


    JSONArray jsARsubmit;
    ArrayList<String> alCheckNamePresent;
    boolean isDuplicate = false;
    private ProgressDialog pDialog;
    GeneralData gD;
    String strdescrption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = this;
            setContentView(R.layout.provider_addservices);
            SplashActivity.autoLoginPreference = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
            SplashActivity.sharedPreferences = getSharedPreferences("regid", Context.MODE_PRIVATE);

            gD = new GeneralData(context);
            pDialog = new ProgressDialog(this);
            DisplayMetrics dp = getResources().getDisplayMetrics();
            int nHeight = dp.heightPixels;
            nScreenHeight = (int) ((float) nHeight / (float) 2.1);
            nnScreenHeight = (int) ((float) nHeight / (float) 2.3);
            SplashActivity.autoLoginPreference = getSharedPreferences("autoLogin", Context.MODE_PRIVATE);
            if (!gD.isConnectingToInternet()) {
                gD.showAlertDialogNet(context, "Unable to connect with Internet. Please check your internet connection and try again");
            }
            imvAddServices = (ImageView) findViewById(R.id.imv_Addservices);
            llayParent = (LinearLayout) findViewById(R.id.llayParent);
            llaySubmit = (LinearLayout) findViewById(R.id.llaySubmit);
            llayBack = (LinearLayout) findViewById(R.id.llayBack);
            llayBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    popupcall("Do you want to logout",0,"back",nnScreenHeight);

                }
            });


            nFrom = 1;
            if (nFrom == 1) {
                //   CreateChildLayout(nCount);

                CreateChildLayout();
                nCount++;
            } else {
                //Do Edit functionality
            }


            imvAddServices.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    CreateChildLayout();
                       nCount++;


                }
            });

            llaySubmit.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {

                                                  if (!gD.isConnectingToInternet()) {
                                                      Toast.makeText(ProviderAddServices.this, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                                                  }
                                                  else {



                                                  jsARsubmit = new JSONArray();
                                                  alCheckNamePresent = new ArrayList<String>();
                                                  final ArrayList<String> duplicatedObjects = new ArrayList<String>();

                                                  Spinner spinServices = null;
                                                  EditText edNoofExperts = null, edDuration = null, edRate = null, edTypeofStyles = null;


                                                  for (int i = 0; i < llayParent.getChildCount(); i++) {

                                                      //Layout1
                                                      spinServices = (Spinner) ((LinearLayout) ((LinearLayout) llayParent.getChildAt(i)).getChildAt(0)).getChildAt(0);
                                                      edNoofExperts = (EditText) ((LinearLayout) ((LinearLayout) llayParent.getChildAt(i)).getChildAt(0)).getChildAt(1);

                                                      //Layout2
                                                      edDuration = (EditText) ((LinearLayout) ((LinearLayout) llayParent.getChildAt(i)).getChildAt(1)).getChildAt(0);
                                                      edRate = (EditText) ((LinearLayout) ((LinearLayout) llayParent.getChildAt(i)).getChildAt(1)).getChildAt(1);

                                                      //Layout3
                                                      edTypeofStyles = (EditText) ((LinearLayout) ((LinearLayout) llayParent.getChildAt(i)).getChildAt(2)).getChildAt(0);


                                                      if (spinServices.getSelectedItemPosition() == 0) {
                                                          Toast.makeText(ProviderAddServices.this, "Please select the expertise !", Toast.LENGTH_SHORT).show();
                                                      } else if (edNoofExperts.length() == 0) {
                                                          Toast.makeText(ProviderAddServices.this, "Please enter the types of services !", Toast.LENGTH_SHORT).show();
                                                      } else if (edDuration.length() == 0) {
                                                          Toast.makeText(ProviderAddServices.this, "Please fill the duration !", Toast.LENGTH_SHORT).show();
                                                      } else if (edRate.length() == 0) {
                                                          Toast.makeText(ProviderAddServices.this, "Please fill the rate !", Toast.LENGTH_SHORT).show();
                                                      } else if (edTypeofStyles.length() == 0) {
                                                          Toast.makeText(ProviderAddServices.this, "Please add ur additonal information !", Toast.LENGTH_SHORT).show();
                                                      }


                                                      if (edNoofExperts.getText().toString().trim().length() > 0) {
                                                          alCheckNamePresent.add(edNoofExperts.getText().toString());

                                                      }


                                                      if (spinServices.getSelectedItemPosition() != 0 && edNoofExperts.getText().toString().trim().length() > 0 && edDuration.getText().toString().trim().length() > 0 && edRate.getText().toString().trim().length() > 0 && edTypeofStyles.getText().toString().trim().length() > 0) {
                                                          //Add JsonArry for Server Request

                                                          //[{"rate":"200","dur":"30","experts":"12","service":"1","duration":"3"}]
                                                          try {
                                                              SpinnerBean sB = (SpinnerBean) (spinServices).getSelectedItem();
                                                              int nVal = sB.getnId();

                                                              JSONObject jsObj = new JSONObject();
                                                              jsObj.put("rate", edRate.getText().toString().trim());
                                                              jsObj.put("dur", edDuration.getText().toString().trim());
                                                              jsObj.put("service", edNoofExperts.getText().toString().trim().replaceAll(" ", "_"));
                                                              jsObj.put("expertise", String.valueOf(nVal));


                                                              strdescrption=edTypeofStyles.getText().toString().replaceAll(" ","_");
                                                              Log.i("HH", "Descpr" + strdescrption);
                                                              jsObj.put("description", strdescrption.trim());

                                                              jsARsubmit.put(jsObj);


                                                          } catch (JSONException e) {
                                                              e.printStackTrace();
                                                          }

                                                      } else {
                                                          break;
                                                      }


                                                  }


                                                  Set<String> set = new HashSet<String>() {
                                                      @Override
                                                      public boolean add(String e) {
                                                          if (contains(e)) {
                                                              duplicatedObjects.add(e);
                                                              isDuplicate = true;
                                                          }
                                                          return super.add(e);
                                                      }
                                                  };
                                                  for (String t : alCheckNamePresent) {
                                                      set.add(t);
                                                  }

                                                  if (spinServices.getSelectedItemPosition() != 0 && edNoofExperts.getText().toString().trim().length() > 0 && edDuration.getText().toString().trim().length() > 0 && edRate.getText().toString().trim().length() > 0 && edTypeofStyles.getText().toString().trim().length() > 0 && !isDuplicate) {

                                                      showAlertDialog(context, "Do you wish to save these services?");

                                                  } else {
                                                      if (isDuplicate) {
                                                          String strVal = "";
                                                          for (int k = 0; k < duplicatedObjects.size(); k++) {
                                                              strVal += duplicatedObjects.get(k) + ",";
                                                          }
                                                          Toast.makeText(ProviderAddServices.this, "These are duplicate Names : " + strVal.substring(0, strVal.length() - 1), Toast.LENGTH_SHORT).show();
                                                      }
                                                  }

                                              }
                                              }
                                          }

            );
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onBackPressed() {
        popupcall("Do you want to logout",0,"back", nnScreenHeight);

    }


    private void CreateChildLayout() {
        try {
            final LinearLayout llayMain = new LinearLayout(context);
            llayMain.setBackgroundResource(R.drawable.layout_border);
            llayMain.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams llayMainParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            llayMainParams.setMargins(2, 5, 2, 0);
            llayMain.setLayoutParams(llayMainParams);

            LinearLayout llaySub1 = new LinearLayout(context);
            llaySub1.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams llaySubParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llaySub1.setLayoutParams(llaySubParams1);

            LinearLayout llaySub2 = new LinearLayout(context);
            llaySub2.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams llaySubParams2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llaySub2.setLayoutParams(llaySubParams2);


            LinearLayout llaySub3 = new LinearLayout(context);
            llaySub3.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams llaySubParams3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            llaySub3.setLayoutParams(llaySubParams3);


            int Layout1width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
            int Layout1height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());

            int Layout2width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110, getResources().getDisplayMetrics());
            int Layout2height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());


            int Layout3width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 320, getResources().getDisplayMetrics());


            int SpinWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 165, getResources().getDisplayMetrics());
            int SpinHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());

            LinearLayout.LayoutParams llaySub1params = new LinearLayout.LayoutParams(Layout1width, Layout1height);
            llaySub1params.setMargins(10, 10, 5, 10);

            LinearLayout.LayoutParams llaySub2params = new LinearLayout.LayoutParams(Layout2width, Layout2height);
            llaySub2params.setMargins(10, 10, 5, 10);

            LinearLayout.LayoutParams llaySub3params = new LinearLayout.LayoutParams(Layout3width, Layout2height);
            llaySub3params.setMargins(10, 10, 10, 10);


            LinearLayout.LayoutParams llaySpinparams = new LinearLayout.LayoutParams(SpinWidth, SpinHeight);
            llaySpinparams.setMargins(10, 10, 10, 10);

            ArrayList<SpinnerBean> alSpin = new ArrayList<SpinnerBean>();
            final Spinner spinServices = new Spinner(context);
            spinServices.setLayoutParams(llaySpinparams);

            SpinnerBean sb = new SpinnerBean(context,"");
            sb.setnId(0);
            sb.setSpinName("Select Expertise");

            SpinnerBean sb1 = new SpinnerBean(context,"");
            sb1.setnId(1);
            sb1.setSpinName("Barber");

            SpinnerBean sb2 = new SpinnerBean(context,"");
            sb2.setnId(2);
            sb2.setSpinName("Hair Stylist");

            SpinnerBean sb3 = new SpinnerBean(context,"");
            sb3.setnId(3);
            sb3.setSpinName("Makeup Artist");

            SpinnerBean sb4 = new SpinnerBean(context,"");
            sb4.setnId(4);
            sb4.setSpinName("Nail Technician");

            alSpin.add(sb);
            alSpin.add(sb1);
            alSpin.add(sb2);
            alSpin.add(sb3);
            alSpin.add(sb4);

            CustomArrayAdapter myAdapter = new CustomArrayAdapter(ProviderAddServices.this, R.layout.spinner_item, alSpin);
            spinServices.setAdapter(myAdapter);
            InputFilter filter = new InputFilter() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                public CharSequence filter(CharSequence src, int start, int end,
                                           Spanned dest, int dstart, int dend) {
                    if(src.equals("")){ // for backspace
                        return src;
                    }
                    if(src.toString().matches("[a-zA-Z ., ]+")){
                        return src;
                    }
                    return "";
                }
            };

            EditText edNoofExperts = new EditText(context);
            final EditText edAvgDuration = new EditText(context);
          final  EditText edRate = new EditText(context);
            edNoofExperts.setPadding(8, 5, 5, 5);
            edNoofExperts.setBackgroundResource(R.drawable.edittext_rectangle);
            //edNoofExperts.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
            edNoofExperts.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
            edNoofExperts.setHint("NAME OF SERVICE");
            edNoofExperts.setTextSize(10);
            edNoofExperts.setFilters(new InputFilter[]{filter});
            edNoofExperts.setLayoutParams(llaySub1params);
            edNoofExperts.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if (actionId == 0 || actionId == EditorInfo.IME_ACTION_DONE) {
                        edAvgDuration.requestFocus();
                    }
                    return false;
                }
            });


            edAvgDuration.setPadding(8, 5, 5, 5);
            edAvgDuration.setBackgroundResource(R.drawable.edittext_rectangle);
            edAvgDuration.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
            edAvgDuration.setInputType(InputType.TYPE_CLASS_NUMBER);
            edAvgDuration.setHint("DURATION (Mins)");
            edAvgDuration.setTextSize(10);
            edAvgDuration.setLayoutParams(llaySub2params);

            edAvgDuration.setOnEditorActionListener(new TextView.OnEditorActionListener() {

                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                    if (actionId == 0 || actionId == EditorInfo.IME_ACTION_DONE) {
                        edRate.requestFocus();
                    }
                    return false;
                }
            });


            llaySub1.addView(spinServices);
            llaySub1.addView(edNoofExperts);

            ImageView imvClose = new ImageView(context);
            imvClose.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, 50, Gravity.RIGHT));
            imvClose.setImageResource(R.drawable.red_cross);
            imvClose.setScaleType(ImageView.ScaleType.FIT_END);
            imvClose.setPadding(0, 5, 5, 0);
            llaySub1.addView(imvClose);



            edRate.setPadding(8, 5, 5, 5);
            edRate.setInputType(InputType.TYPE_CLASS_NUMBER);
            edRate.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
            edRate.setBackgroundResource(R.drawable.edittext_rectangle);
            edRate.setHint("RATE ($)");
            edRate.setLayoutParams(llaySub2params);
            edRate.setTextSize(10);

            llaySub2.addView(edAvgDuration);
            llaySub2.addView(edRate);


            EditText edStyles = new EditText(context);
            edStyles.setPadding(8, 5, 5, 5);
            edStyles.setBackgroundResource(R.drawable.edittext_rectangle);
            edStyles.setSingleLine(false);
            edStyles.setHint("ADDITIONAL INFORMATION");
            edStyles.setTextSize(10);
            edStyles.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
            edStyles.setMaxLines(3);
            edStyles.setLayoutParams(llaySub3params);
            edStyles.setFilters(new InputFilter[]{filter});

            llaySub3.addView(edStyles);


            llayMain.addView(llaySub1);
            llayMain.addView(llaySub2);
            llayMain.addView(llaySub3);


            llayParent.addView(llayMain);




            imvClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (nCount > 1) {
                        llayParent.removeViewAt(llayParent.indexOfChild(llayMain));
                        nCount--;

                        for (int i = 0; i < alCheckServicePresent.size(); i++) {
                            if (alCheckServicePresent.get(i) == llayMain.getId()) {
                                // got the duplicate element
                                alCheckServicePresent.remove(i);
                            }
                        }
                    } else {
                        Toast.makeText(ProviderAddServices.this, "Must one service need to proceed further !", Toast.LENGTH_SHORT).show();
                    }

                }
            });


            spinServices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position != 0) {
                        SpinnerBean mSelected = (SpinnerBean) parent.getItemAtPosition(position);
                        Log.i("RAJ", "Id : " + mSelected.getnId());

                        alCheckServicePresent.add(mSelected.getnId());
                        llayMain.setId(mSelected.getnId());



                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void showAlertDialog(final Context context, String strTitle) {


        try {
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

            tv_alert_Message.setText(strTitle);

            Button btn_submit = (Button) dialogLayout.findViewById(R.id.ok);
            Button btn_cancel = (Button) dialogLayout.findViewById(R.id.cancel);


            FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, nScreenHeight);
            llayAlert.setLayoutParams(lparams);


            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("SE", "Send : " + jsARsubmit.toString());
                    sendSubmitRequest sSR = new sendSubmitRequest(context);
                    sSR.execute();

                }

            });


            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.i("HH", "HEllo : ");
                 /*   CreateChildLayout();
                    nCount++;*/
                    alertDialog.dismiss();

                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class sendSubmitRequest extends AsyncTask {
        String sResponse = null;
        Context context;

        public sendSubmitRequest(Context Conte) {
            context = Conte;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {


                String charset = "UTF-8";

                String requestURL = gD.common_baseurl+"newaddservice.php";


                // 4. separate class for multipart content image uploaded task-----------
                MultipartUtility multipart = new MultipartUtility(requestURL, charset);

                multipart.addHeaderField("Content-Encoding", "gzip");
                // 5. set the user_image key word and multipart image file value -----------

                // Log.i("SE", "ProviderID : " + SplashActivity.sharedPreferences.getString("UID", null));

                multipart.addFormField("providerid", SplashActivity.sharedPreferences.getString("UID", null));

                //  multipart.addFormField("providerid", String.valueOf(2));
                multipart.addFormField("services", jsARsubmit.toString());

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
                    final JSONObject jsobj = new JSONObject(sResponse);

                    if (jsobj.getInt("code") == 2) {
                        popupcall("You successfully added your information",1,"from_api", nScreenHeight);
                        String  strservicetype = jsobj.getJSONObject("data").getString("services");
                        SharedPreferences.Editor editor = SplashActivity.sharedPreferences.edit();
                        editor.putString("serviceType", strservicetype);
                        editor.commit();





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
    public void popupcall(String s, int i, final String str_res, int size)
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


            FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, size);
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
                        Intent user_category = new Intent(ProviderAddServices.this, Provider_ChooseService.class);
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

                        Intent user_category = new Intent(ProviderAddServices.this, SignInActivity.class);

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

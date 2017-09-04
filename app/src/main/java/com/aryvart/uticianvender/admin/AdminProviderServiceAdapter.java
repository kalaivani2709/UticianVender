package com.aryvart.uticianvender.admin;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.bean.ProviderBean;

import com.aryvart.uticianvender.imageCache.ImageLoader;


import java.util.ArrayList;


/**
 * Created by android1 on 17/5/16.
 */
public class AdminProviderServiceAdapter extends BaseAdapter {
    ArrayList<ProviderBean> alBean;
    Context context;

    String strProviderName;
    ImageLoader imgLoader;
    ProviderBean reviewBean;
    int nScreenHeight;


    public AdminProviderServiceAdapter(Context cont, ArrayList<ProviderBean> providerBean) {
        this.context = cont;
        this.alBean = providerBean;
        Log.i("KK", "name" + strProviderName);
        imgLoader = new ImageLoader(context);

        DisplayMetrics dp = ((Activity) context).getResources().getDisplayMetrics();
        int nHeight = dp.heightPixels;
        nScreenHeight = (int) ((float) nHeight / (float) 2.2);



    }

    @Override
    public int getCount() {
        return alBean.size();
    }

    @Override
    public ProviderBean getItem(int position) {
        return alBean.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        try {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            ViewHolder view;
            if (convertView == null) {
                view = new ViewHolder();

                convertView = inflater.inflate(R.layout.admin_providerservicesitems, parent, false);



                // get layout from mobile.xml
                view.serviceName = (TextView) convertView.findViewById(R.id.txt_services);
                view.rate = (TextView) convertView.findViewById(R.id.txt_price);

                convertView.setTag(view);

            } else {
                view = (ViewHolder) convertView.getTag();
            }

            reviewBean = alBean.get(position);


            view.serviceName.setText(reviewBean.get__serviceName());
            view.rate.setText("$" + reviewBean.get_serviceCharge());
            // get layout from mobile.xml
            view.information = (ImageView) convertView.findViewById(R.id.information);
            view.information.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    reviewBean = alBean.get(position);
                  //  Log.i("KK", "strinformation" + strinformation);
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View dialogLayout = inflater.inflate(R.layout.default_popup, null);

                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
                    alertDialogBuilder.setView(dialogLayout);
                    alertDialogBuilder.setCancelable(true);


                    final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                    //  alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    alertDialog.show();

                    LinearLayout llayAlert = (LinearLayout) dialogLayout.findViewById(R.id.llayalertDialog);
                    // TextView tv_alert_Title = (TextView) dialogLayout.findViewById(R.id.tv_alert_Title);
                    TextView tv_alert_Message = (TextView) dialogLayout.findViewById(R.id.text);
                  //  TextView expertise_name = (TextView) dialogLayout.findViewById(R.id.tv_expertise_name);
                  //  expertise_name.setText(reviewBean.get__ProviderName());

                   tv_alert_Message.setText(reviewBean.getStr_service_information());

                    ImageView btn_submit = (ImageView) dialogLayout.findViewById(R.id.menu_back);
                    Button btn_cancel = (Button) dialogLayout.findViewById(R.id.cancel);


                    FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, nScreenHeight);
                    llayAlert.setLayoutParams(lparams);


                    btn_submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            alertDialog.dismiss();


                        }
                    });

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        return convertView;
    }


    class ViewHolder {

        public TextView serviceName;
        public TextView rate;
        public ImageView information;
    }

}




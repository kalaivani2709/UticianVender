package com.aryvart.uticianvender.adapter;

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

import com.aryvart.uticianvender.Interface.GetNotification;
import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.bean.ProviderBean;

import com.aryvart.uticianvender.imageCache.ImageLoader;

import java.util.ArrayList;

/**
 * Created by android1 on 17/5/16.
 */
public class ProviderServicesAdapter extends BaseAdapter {
    ArrayList<ProviderBean> alBean;
    Context context;
    String strinformation;

    String strProviderName;
    ImageLoader imgLoader;
    GetNotification GetNotify;

    ProviderBean reviewBean;

    String OnlineStatus;
    int nScreenHeight;
    ImageView provider_image;
    TextView provider_name;


    public ProviderServicesAdapter(Context cont, ArrayList<ProviderBean> providerBean, GetNotification notify, String str_onlinestatus) {
        this.context = cont;
        this.alBean = providerBean;
        this.GetNotify = notify;
        Log.i("KK", "name" + strProviderName);
        OnlineStatus = str_onlinestatus;
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

                convertView = inflater.inflate(R.layout.user_provider_service_list, null);


                view.serviceName = (TextView) convertView.findViewById(R.id.serviceName);
                view.rate = (TextView) convertView.findViewById(R.id.rate);


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
                    Log.i("KK", "strinformation" + strinformation);
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

                    tv_alert_Message.setText(reviewBean.getStr_service_information());
                   // TextView expertise_name = (TextView) dialogLayout.findViewById(R.id.tv_expertise_name);
                   // expertise_name.setText(reviewBean.get__ProviderName());

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

        public TextView serviceName,expertise_name;
        public TextView expertiseName;
        public TextView rate;
        public ImageView information;
    }

}




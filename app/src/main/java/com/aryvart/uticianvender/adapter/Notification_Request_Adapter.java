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
import android.widget.Toast;

import com.aryvart.uticianvender.Interface.GetBookingId;
import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.bean.ProviderBean;
import com.aryvart.uticianvender.genericclasses.GeneralData;

import com.aryvart.uticianvender.imageCache.ImageLoader;

import java.util.ArrayList;

/**
 * Created by android3 on 13/9/16.
 */
public class Notification_Request_Adapter extends BaseAdapter {
    ArrayList<ProviderBean> alBean;
    Context context;
    String strinformation;

    String strProviderName;
    ImageLoader imgLoader;
    GetBookingId GetNotify;



    int nScreenHeight;
    ImageView provider_image;
    TextView provider_name;

    GeneralData gD;
    public Notification_Request_Adapter(Context cont, ArrayList beanArrayList, GetBookingId notify) {
        this.context = cont;
        this.alBean = beanArrayList;
        this.GetNotify = notify;
        Log.i("KK", "name" + strProviderName);

        imgLoader = new ImageLoader(context);
        DisplayMetrics dp = ((Activity) context).getResources().getDisplayMetrics();
        int nHeight = dp.heightPixels;
        gD = new GeneralData(context);
        nScreenHeight = (int) ((float) nHeight / (float) 2.4);


    }

    @Override
    public int getCount() {
        return alBean.size();
        //return null;
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
            Log.i("NDD", "POstion : " + position);


            if (convertView == null) {
                view = new ViewHolder();

                convertView = inflater.inflate(R.layout.bookingrequest, parent, false);


                // get layout from mobile.xml

                view.serviceName = (TextView) convertView.findViewById(R.id.bookingreq);
                view.llay_bookrow = (LinearLayout) convertView.findViewById(R.id.llay_row);
                if (position % 2 == 0) {
                    view.llay_bookrow.setBackgroundResource(R.color.app_green);


                } else {
                    //odd row
                    view.llay_bookrow.setBackgroundResource(R.color.app_lgreen);
                  //  convertView.setBackgroundResource(R.color.app_green);
                    //we need tp set the bg color here
                }
                final ProviderBean   reviewBean = alBean.get(position);

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        if (!gD.isConnectingToInternet()) {
                            Toast.makeText(context, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                        }
                        else {

                        GetNotify.paidstatus(reviewBean.getStr_providerid(), reviewBean.get__serviceName(), reviewBean.getStr_Date(), reviewBean.getStr_name(), reviewBean.getStr_rating(), reviewBean.getN_user_image(), reviewBean.getStr_miles());
                        Log.i("KK", "date" + reviewBean.getStr_name());
                        Log.i("HH", "Helloooooooooooooo");
                    }}
                });


                convertView.setTag(view);


                // get layout from mobile.xml
                view.information=(ImageView)convertView.findViewById(R.id.information);
                view.information.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.i("KK", "strinformation" + strinformation);
                        LayoutInflater inflater = LayoutInflater.from(context);
                        View dialogLayout = inflater.inflate(R.layout.bubble_notify_request, null);

                        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
                        alertDialogBuilder.setView(dialogLayout);
                        alertDialogBuilder.setCancelable(true);


                        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                        //  alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        alertDialog.show();

                        LinearLayout llayAlert = (LinearLayout) dialogLayout.findViewById(R.id.llayalertDialog);
                        // TextView tv_alert_Title = (TextView) dialogLayout.findViewById(R.id.tv_alert_Title);
                        TextView txt_name = (TextView) dialogLayout.findViewById(R.id.txt_name);
                        TextView txt_service = (TextView) dialogLayout.findViewById(R.id.txt_date);
                        TextView txt_date = (TextView) dialogLayout.findViewById(R.id.txt_service);
                        txt_name.setText(reviewBean.getStr_name());
                        txt_service.setText(reviewBean.get__serviceName());

                        txt_date.setText(reviewBean.getStr_Date());

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






            } else {
                view = (ViewHolder) convertView.getTag();
            }


            ProviderBean  reviewBean = alBean.get(position);


            strinformation = reviewBean.getStr_service_information();
            reviewBean.get__serviceName();
            reviewBean.getStr_Date();
            reviewBean.getStr_name();
            reviewBean.getStr_rating();
            reviewBean.getN_user_image();
            reviewBean.getStr_miles();
            reviewBean.getStr_providerid();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return convertView;
    }


    class ViewHolder {

        public TextView serviceName;
        public TextView expertiseName;
        public TextView rate;
        public ImageView information;
        LinearLayout llay_bookrow;
    }

}




package com.aryvart.uticianvender.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.aryvart.uticianvender.Interface.GetBookingId;
import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.bean.AppointmentBean;

import com.aryvart.uticianvender.imageCache.ImageLoader;

import java.util.ArrayList;

/**
 * Created by android3 on 6/4/16.
 */
public class ProviderAppointmentAdapter extends RecyclerView
        .Adapter<ProviderAppointmentAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private static MyClickListener myClickListener;
    Context context;
    ImageLoader imgLoader;
    GetBookingId getvalues;
    String type="abc";
    String strlat, strlong;
int ScreenHeight;
    private ArrayList<AppointmentBean> sDataset;


    public ProviderAppointmentAdapter(ArrayList<AppointmentBean> myDataset, Context cont, GetBookingId getid) {

        sDataset = myDataset;
        context = cont;
        getvalues= getid;
        imgLoader = new ImageLoader(context);
        DisplayMetrics dp = ((Activity) context).getResources().getDisplayMetrics();
        int nHeight = dp.heightPixels;

        ScreenHeight = (int) ((float) nHeight / (float) 2.2);
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.provider_appointment_items, parent, false);

        final DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        try {




            final DataObjectHolder finalDataObjectHolder = dataObjectHolder;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int nPosition = finalDataObjectHolder.getAdapterPosition();
                    Log.i("JJ", "lat.." + strlat);
                    Log.i("JJ", "long.." + strlong);
                    String status = sDataset.get(nPosition).getacceptStatus();
                    String iscomplete = sDataset.get(nPosition).getStr_iscomplete();
                    String paidstatus = sDataset.get(nPosition).getStr_paidstatus();
                    String generate = sDataset.get(nPosition).getStr_generate();
                    String reached = sDataset.get(nPosition).getStr_reached();
                    String pse = sDataset.get(nPosition).getStr_pse();
                    String review = sDataset.get(nPosition).getStr_review();
                    String jsonvalues = sDataset.get(nPosition).getStr_jsongvalues();
                    String leave = sDataset.get(nPosition).getStr_leave();

                    String responsevalue = sDataset.get(nPosition).getStr_response();


                    Log.i("JJ", "appstatus" + responsevalue);

                    getvalues.navigatepage(iscomplete, paidstatus, generate, reached, pse, review, type, jsonvalues, leave, responsevalue);


                }
            });


            final   TextView  cancel = (TextView) view.findViewById(R.id.cancel);

       cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int nPosition = dataObjectHolder.getAdapterPosition();


                        final String booking_id = sDataset.get(nPosition).getRow_id();


                    LayoutInflater inflater = LayoutInflater.from(context);
                    View dialogLayout = inflater.inflate(R.layout.layout_confirmation, null);

                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
                    alertDialogBuilder.setView(dialogLayout);
                    alertDialogBuilder.setCancelable(true);


                    final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                    //  alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    alertDialog.show();

                    LinearLayout llayAlert = (LinearLayout) dialogLayout.findViewById(R.id.llayalertDialog);
                    // TextView tv_alert_Title = (TextView) dialogLayout.findViewById(R.id.tv_alert_Title);
                    TextView tv_alert_Message = (TextView) dialogLayout.findViewById(R.id.message);

                    tv_alert_Message.setText("Are you sure want to cancel?");

                    Button btn_submit = (Button) dialogLayout.findViewById(R.id.ok);
                    Button btn_cancel = (Button) dialogLayout.findViewById(R.id.cancel);
                    btn_submit.setText("Yes");
                    btn_cancel.setText("No");

                    FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ScreenHeight);
                    llayAlert.setLayoutParams(lparams);


                    btn_submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            getvalues.bookingid(booking_id, "");
                            alertDialog.dismiss();
                        }
                    });

                    btn_cancel.setOnClickListener(new View.OnClickListener() {
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
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        try {
            AppointmentBean accountbean = sDataset.get(position);

            holder.name.setText(accountbean.getStr_name());
            holder.service.setText(accountbean.getStr_Service());
            holder.time.setText(accountbean.getStr_Time());
            holder.amt.setText(" $" + accountbean.getStr_miles());
            if (accountbean.getacceptStatus().equalsIgnoreCase("3")) {
                holder.accept_status.setVisibility(View.VISIBLE);
                holder.accept_status.setText("Booking has been cancelled");
            }
            if (accountbean.getStr_leave().equalsIgnoreCase("1")) {
                holder.cancel.setVisibility(View.GONE);

            }

            // change24to12hrswiseversa("06:30:00 PM", "hh:mm:ss a", "HH:mm:ss", 0);
            imgLoader.DisplayImage(sDataset.get(position).getN_user_image(), holder.imv_Product);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void addItem(AppointmentBean dataObj, int index) {
        sDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        sDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return sDataset.size();
    }



    public interface MyClickListener {
        public void onItemClick(int position, View v);

    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        public TextView name;
        public TextView service;
        public TextView time;
        public TextView amt;
        public TextView accept_status;
        public ImageView imv_Product;
        ProgressBar pB;
  public TextView cancel;

        public DataObjectHolder(View itemView) {
            super(itemView);

        cancel = (TextView) itemView.findViewById(R.id.cancel);
            name = (TextView) itemView.findViewById(R.id.txt_user_name);
            service = (TextView) itemView.findViewById(R.id.txt_services);
            time = (TextView) itemView.findViewById(R.id.txt_date_time);
            amt = (TextView) itemView.findViewById(R.id.txt_price);
            accept_status = (TextView) itemView.findViewById(R.id.accept_status);
            imv_Product = (ImageView) itemView.findViewById(R.id.user_img);
            //  pB = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }


}


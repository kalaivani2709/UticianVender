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
import android.widget.Toast;

import com.aryvart.uticianvender.Interface.GetBookingId;
import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.bean.AppointmentBean;
import com.aryvart.uticianvender.genericclasses.GeneralData;

import com.aryvart.uticianvender.imageCache.ImageLoader;

import java.util.ArrayList;


/**
 * Created by android3 on 13/9/16.
 */
public class Dash_AppointmentAdpater extends RecyclerView
        .Adapter<Dash_AppointmentAdpater
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private static MyClickListener myClickListener;
    Context context;
    ImageLoader imgLoader;
    GetBookingId getvalues;
int ScreenHeight;

    private ArrayList<AppointmentBean> sDataset;
    GeneralData gD;



    public Dash_AppointmentAdpater(ArrayList<AppointmentBean> myDataset, Context cont, GetBookingId getid) {

        sDataset = myDataset;
        context = cont;
        getvalues= getid;
        imgLoader = new ImageLoader(context);
        gD = new GeneralData(context);
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
        DataObjectHolder dataObjectHolder = null;
        try {
            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.provider_dashappoitmnetsitems, parent, false);

            dataObjectHolder = new DataObjectHolder(view);

            final DataObjectHolder finalDataObjectHolder = dataObjectHolder;





            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int nPosition = finalDataObjectHolder.getAdapterPosition();
                    String jsonvalues = sDataset.get(nPosition).getStr_jsongvalues();
                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(context, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    } else {


                        getvalues.serviceendid(jsonvalues, "");


                    }


                }
            });

            final   TextView  cancel = (TextView) view.findViewById(R.id.cancel);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int nPosition = finalDataObjectHolder.getAdapterPosition();


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
    public void onBindViewHolder(final DataObjectHolder holder, final int position) {

        try {
          AppointmentBean accountbean = sDataset.get(position);









            holder.name.setText(accountbean.getStr_name());
            holder.service.setText(accountbean.getStr_Service());
            holder.time.setText(accountbean.getStr_Time());

            if (position % 2 == 0) {
                //even row
                holder.llay_row.setBackgroundResource(R.color.app_lgreen);
                //we need tp set the bg color here

            } else {
                //odd row
               holder.llay_row.setBackgroundResource(R.color.app_green);
                //we need tp set the bg color here
            }

            if (accountbean.getStr_leave().equalsIgnoreCase("1")) {
                holder.cancel.setVisibility(View.GONE);

            }

            // change24to12hrswiseversa("06:30:00 PM", "hh:mm:ss a", "HH:mm:ss", 0);
            imgLoader.DisplayImage(sDataset.get(position).getN_user_image(), holder.imv_Product);









        } catch (Exception e) {
            e.printStackTrace();
        }


        // holder.imv_Product.setImageBitmap(mDataset.get(position).getBmp_Product());
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
        public TextView txt_msg;
        public ImageView imv_Product;
        LinearLayout llay_row;
        public TextView cancel;
        ProgressBar pB;


        public DataObjectHolder(View itemView) {
            super(itemView);
            cancel = (TextView) itemView.findViewById(R.id.cancel);
            name = (TextView) itemView.findViewById(R.id.txt_user_name);
            service = (TextView) itemView.findViewById(R.id.txt_services);
            time = (TextView) itemView.findViewById(R.id.time);

            llay_row=(LinearLayout)itemView.findViewById(R.id.lay_row);

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


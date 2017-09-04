package com.aryvart.uticianvender.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;


import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.bean.commonBeanSupport;

import com.aryvart.uticianvender.imageCache.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Admin on 29-03-2016.
 */

public class ProviderRatingAdapter extends RecyclerView
        .Adapter<ProviderRatingAdapter
        .DataObjectHolder> {

    private static MyClickListener myClickListener;
    Context context;
    ImageLoader imgLoader;
    private ArrayList<commonBeanSupport> mDataset;
    int nScreenHeight;



    public ProviderRatingAdapter(ArrayList<commonBeanSupport> myDataset, Context cont) {

        mDataset = myDataset;
        context = cont;
        imgLoader = new ImageLoader(context);
        DisplayMetrics dp = ((Activity) context).getResources().getDisplayMetrics();
        int nHeight = dp.heightPixels;

        nScreenHeight = (int) ((float) nHeight / (float) 2.2);
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
                    .inflate(R.layout.provider_review_item, parent, false);

            dataObjectHolder = new DataObjectHolder(view);

            final DataObjectHolder finalDataObjectHolder = dataObjectHolder;



          final TextView  status = (TextView) view.findViewById(R.id.tv_rating_status);
            dataObjectHolder.imv_ratingPerson.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    final int nPosition = finalDataObjectHolder.getAdapterPosition();
                status.setVerticalScrollBarEnabled(true);
                    LayoutInflater inflater = LayoutInflater.from(context);
                    View dialogLayout = inflater.inflate(R.layout.default_popup, null);

                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
                    alertDialogBuilder.setView(dialogLayout);
                    alertDialogBuilder.setCancelable(true);


                    final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                    //  alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    alertDialog.show();

                    LinearLayout llayAlert = (LinearLayout) dialogLayout.findViewById(R.id.llayalertDialog);
                     TextView tv_alert_Title = (TextView) dialogLayout.findViewById(R.id.text_tv);
                    TextView tv_alert_Message = (TextView) dialogLayout.findViewById(R.id.text);

                    tv_alert_Title.setText("Reviews");

                    tv_alert_Message.setText(mDataset.get(nPosition).getStrdateDetails());

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


                    status.setMovementMethod(new ScrollingMovementMethod());
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
            commonBeanSupport accountbean = mDataset.get(position);
            holder.name.setText(accountbean.getStrUserName());
            holder.status.setText(accountbean.getStrdateDetails());
            holder.tv_rating_status.setRating(Float.parseFloat(accountbean.getStrRating()));
            holder.status.setMovementMethod(new ScrollingMovementMethod());
            holder.status.setVerticalScrollBarEnabled(true);

            imgLoader.DisplayImage(mDataset.get(position).getStrUserImage(), holder.imv_ratingPerson);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void addItem(commonBeanSupport dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        public TextView name;
        public TextView status;

        public ImageView imv_ratingPerson;
        public RatingBar tv_rating_status;


        public DataObjectHolder(View itemView) {
            super(itemView);

            tv_rating_status = (RatingBar) itemView.findViewById(R.id.tv_rating);
            name = (TextView) itemView.findViewById(R.id.tv_rating_personName);
            status = (TextView) itemView.findViewById(R.id.tv_rating_status);
            imv_ratingPerson = (ImageView) itemView.findViewById(R.id.reviewer_image);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (myClickListener != null) {
                myClickListener.onItemClick(getAdapterPosition(), v);
            }
        }
    }

}


package com.aryvart.uticianvender.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.bean.commonBeanSupport;

import com.aryvart.uticianvender.imageCache.ImageLoader;


import java.util.ArrayList;

/**
 * Created by Admin on 29-03-2016.
 */

public class LastApppointmentRecyclerAdapter extends RecyclerView
        .Adapter<LastApppointmentRecyclerAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "LastApppointmentRecyclerAdapter";
    private static MyClickListener myClickListener;
    Context context;
    ImageLoader imgLoader;
    private ArrayList<commonBeanSupport> mDataset;



    public LastApppointmentRecyclerAdapter(ArrayList<commonBeanSupport> myDataset, Context cont) {

        mDataset = myDataset;
        context = cont;
        imgLoader = new ImageLoader(context);
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.support_appointment_items, parent, false);

        final DataObjectHolder dataObjectHolder = new DataObjectHolder(view);

        dataObjectHolder.imv_support_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  int nPosition = dataObjectHolder.getAdapterPosition();
                //sE.callProductScreen(Integer.parseInt(mDataset.get(nPosition).getId()));

                Toast.makeText(context, "hi...........", Toast.LENGTH_SHORT).show();
            }
        });
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        try {
            commonBeanSupport accountbean = mDataset.get(position);
            holder.name.setText(accountbean.getStrUserName());
            holder.service.setText(accountbean.getStrservices());
            holder.dateDetails.setText(accountbean.getStrdateDetails());
            imgLoader.DisplayImage(mDataset.get(position).getStrUserImage(), holder.imv_support_user);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // holder.imv_Product.setImageResource(R.drawable.girl);
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
        public TextView name,service;
        public TextView dateDetails;
        public Button btn_issue;
        public ImageView imv_support_user;

        ProgressBar pB;


        public DataObjectHolder(View itemView) {
            super(itemView);

            service = (TextView) itemView.findViewById(R.id.txt_service);
            name = (TextView) itemView.findViewById(R.id.txt_name);
            dateDetails = (TextView) itemView.findViewById(R.id.txt_dattime);
            btn_issue = (Button) itemView.findViewById(R.id.btn_issue);

            imv_support_user = (ImageView) itemView.findViewById(R.id.usr_img);
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


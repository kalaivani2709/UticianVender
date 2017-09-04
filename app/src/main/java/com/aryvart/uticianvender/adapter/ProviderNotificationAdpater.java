package com.aryvart.uticianvender.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aryvart.uticianvender.R;

import com.aryvart.uticianvender.bean.ProviderNotificationBean;

import com.aryvart.uticianvender.imageCache.ImageLoader;


import java.util.ArrayList;

/**
 * Created by android3 on 14/6/16.
 */
public class ProviderNotificationAdpater extends RecyclerView
        .Adapter<ProviderNotificationAdpater
        .DataObjectHolder> {

    private static MyClickListener myClickListener;
    Context context;
    ImageLoader imgLoader;
    private ArrayList<ProviderNotificationBean> mDataset;



    public ProviderNotificationAdpater(ArrayList<ProviderNotificationBean> myDataset, Context cont) {

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
        DataObjectHolder dataObjectHolder = null;
        try {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.provider_notiify_items, parent, false);

            dataObjectHolder = new DataObjectHolder(view);

            dataObjectHolder.userimg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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
            ProviderNotificationBean accountbean = mDataset.get(position);
            holder.description.setText(accountbean.getStr_descption());
            holder.time.setText(accountbean.getStr_time());
            holder.servcie.setText(accountbean.getStr_service());

            // holder.userimg.setImageResource(R.drawable.default_user_icon);


            String strType = mDataset.get(position).getStr_type();
            Log.i("KKK", "strType" +strType);


            if (position == mDataset.size() - 1) {
                //move to activity from here and call rest
                Log.i("KKK", "Scroll view item last came!!!!!");

            }


            if (strType.equalsIgnoreCase("Welcome to Utician,your account is now active!")) {

                //holder.userimg.serIma(getRR.drawable.default_app_icon);

              //  imgLoader.DisplayImage(mDataset.get(position).getN_user_image(), holder.userimg);

                holder.userimg.setImageResource(R.drawable.default_app_icon);
            }

         else  if (strType.equalsIgnoreCase("Admin decline your account. Please contact him")) {

                //holder.userimg.serIma(getRR.drawable.default_app_icon);

                //  imgLoader.DisplayImage(mDataset.get(position).getN_user_image(), holder.userimg);

                holder.userimg.setImageResource(R.drawable.default_app_icon);
            }


            else
            {
                imgLoader.DisplayImage(mDataset.get(position).getN_user_image(), holder.userimg);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addItem(ProviderNotificationBean dataObj, int index) {
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
        public TextView description;
        public TextView time;

        public TextView servcie;

        public ImageView userimg;




        public DataObjectHolder(View itemView) {
            super(itemView);


            description = (TextView) itemView.findViewById(R.id.txt_descrpiton);
            time = (TextView) itemView.findViewById(R.id.time);
            servcie = (TextView) itemView.findViewById(R.id.txt_service);

            userimg = (ImageView) itemView.findViewById(R.id.userimg);

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
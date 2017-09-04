package com.aryvart.uticianvender.admin;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aryvart.uticianvender.Interface.GetNotification;
import com.aryvart.uticianvender.Interface.UserNotify;
import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.bean.UserActionNotificationBean;

import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.aryvart.uticianvender.imageCache.ImageLoader;

import java.util.ArrayList;

/**
 * Created by android3 on 23/7/16.
 */
public class UserListAdapter extends RecyclerView
        .Adapter<UserListAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "LastApppointmentRecyclerAdapter";
    private static MyClickListener myClickListener;
    Context context;
    ImageLoader imgLoader;
    UserNotify userNotify;
    GetNotification GetNotify;

    GeneralData gD;

    private ArrayList<UserActionNotificationBean> mDataset;

    public UserListAdapter(ArrayList beanArrayList, Context cont, GetNotification getnotify) {
        mDataset = beanArrayList;
        context = cont;
        imgLoader = new ImageLoader(context);
        this.GetNotify = getnotify;
        gD = new GeneralData(context);
    }



    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.userproviderlistitems, parent, false);

        final DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        try {



            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(context, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        final int nPosition = dataObjectHolder.getAdapterPosition();

                        String jsondatas=mDataset.get(nPosition).getStrjsonvalues();

                        GetNotify.getProviderId("", jsondatas);

                    }}
            });










        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {

        try {
            UserActionNotificationBean accountbean = mDataset.get(position);
            holder.name.setText(accountbean.getStr_name());
            holder.time.setText(accountbean.getStr_time());
            Log.i("UD", "date : " + accountbean.getStr_time());
            String jsonvalues = mDataset.get(position).getStrjsonvalues();
            // holder.userimg.setImageResource(R.drawable.default_user_icon);
            imgLoader.DisplayImage(mDataset.get(position).getN_user_image(), holder.userimg);




        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addItem(UserActionNotificationBean dataObj, int index) {
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
        public TextView time;

        public TextView service;
        public Button btn_issue;

        public ImageView userimg;

        ProgressBar pB;


        public DataObjectHolder(View itemView) {
            super(itemView);

            ;
            name = (TextView) itemView.findViewById(R.id.provider_name);
            time = (TextView) itemView.findViewById(R.id.provider_date);

            userimg = (ImageView) itemView.findViewById(R.id.provider_image);


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
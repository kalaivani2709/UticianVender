package com.aryvart.uticianvender.admin;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aryvart.uticianvender.Interface.UserNotify;
import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.bean.UserActionNotificationBean;

import com.aryvart.uticianvender.imageCache.ImageLoader;

import java.util.ArrayList;

/**
 * Created by android3 on 23/7/16.
 */
public class AdminIssueAdapter extends RecyclerView
        .Adapter<AdminIssueAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "LastApppointmentRecyclerAdapter";
    private static MyClickListener myClickListener;
    Context context;
    ImageLoader imgLoader;
    UserNotify userNotify;
    private ArrayList<UserActionNotificationBean> mDataset;

    public AdminIssueAdapter(ArrayList beanArrayList, Context cont) {
        mDataset = beanArrayList;
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
                .inflate(R.layout.admin_issue_items, parent, false);

        final DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        try {

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final int nPosition = dataObjectHolder.getAdapterPosition();
                   // userNotify.getActionTitle(mDataset.get(nPosition).getStr_descption());
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
            UserActionNotificationBean accountbean = mDataset.get(position);
            holder.name.setText(accountbean.getStr_name());
            holder.time.setText(accountbean.getStr_time());


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


            name = (TextView) itemView.findViewById(R.id.txt_name);
            time = (TextView) itemView.findViewById(R.id.txt_date_time);

            userimg = (ImageView) itemView.findViewById(R.id.provider_img);

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
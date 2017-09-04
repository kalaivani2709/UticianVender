package com.aryvart.uticianvender.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aryvart.uticianvender.Interface.GetNotification;
import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.admin.AdminProviderListBean;
import com.aryvart.uticianvender.genericclasses.GeneralData;

import com.aryvart.uticianvender.imageCache.ImageLoader;

import java.util.ArrayList;

/**
 * Created by android3 on 26/8/16.
 */
public class Provider_Account_Adpater extends RecyclerView
        .Adapter<Provider_Account_Adpater
        .DataObjectHolder> {
    private static MyClickListener myClickListener;
    Context context;
    ImageLoader imgLoader;
    String abc;
    GetNotification GetNotify;

    GeneralData gD;
    private ArrayList<AdminProviderListBean> sDataset;

    public Provider_Account_Adpater(ArrayList<AdminProviderListBean> myDataset, Context cont, GetNotification getnotify) {

        this.sDataset = myDataset;
        context = cont;
        this.GetNotify = getnotify;
        imgLoader = new ImageLoader(context);
        gD = new GeneralData(context);
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.provider_account_items, parent, false);
        final DataObjectHolder dataObjectHolder = new DataObjectHolder(view);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!gD.isConnectingToInternet()) {
                    Toast.makeText(context, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                }
                else {
                    final int nPosition = dataObjectHolder.getAdapterPosition();
                abc = sDataset.get(nPosition).getProvider_ID();

                String jsondatas=sDataset.get(nPosition).getStr_jsonvalues();
                GetNotify.getProviderId(abc, jsondatas);

            }}
        });
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, int position) {

        try {
            AdminProviderListBean adminProviderBean = sDataset.get(position);

            holder.provider_name.setText(adminProviderBean.getProvider_Name());
            holder.provider_balacne.setText(adminProviderBean.getStr_pro_balance());
            holder.provider_date.setText(adminProviderBean.getCompany_name());

            //  providerid = adminProviderBean.getStr_jsonvalues();

            imgLoader.DisplayImage(sDataset.get(position).getProvider_image(), holder.provider_image);


            holder.btn_arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    {
                        final int nPosition = holder.getAdapterPosition();
                        abc = sDataset.get(nPosition).getProvider_ID();



                    }


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return sDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);

    }

    public class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        public TextView provider_name;
        public TextView provider_date;
        public TextView provider_balacne;
        public Button btn_arrow;

        public ImageView provider_image;

        public DataObjectHolder(View itemView) {
            super(itemView);
            btn_arrow = (Button) itemView.findViewById(R.id.backarrow);
            provider_name = (TextView) itemView.findViewById(R.id.provider_name);
            provider_date = (TextView) itemView.findViewById(R.id.provider_date);
            provider_balacne = (TextView) itemView.findViewById(R.id.provi_balance);

            provider_image = (ImageView) itemView.findViewById(R.id.provider_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }
}




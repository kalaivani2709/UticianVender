package com.aryvart.uticianvender.admin;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aryvart.uticianvender.Interface.GetNotification;
import com.aryvart.uticianvender.R;

import com.aryvart.uticianvender.imageCache.ImageLoader;

import java.util.ArrayList;

public class AdminProvidersListAdapter extends RecyclerView
        .Adapter<AdminProvidersListAdapter
        .DataObjectHolder> {
    private static MyClickListener myClickListener;
    Context context;
    ImageLoader imgLoader;
    String abc;
    GetNotification GetNotify;
    String providerid;
    private ArrayList<AdminProviderListBean> sDataset;

    public AdminProvidersListAdapter(ArrayList<AdminProviderListBean> myDataset, Context cont, GetNotification notify) {

        this.sDataset = myDataset;
        context = cont;
        this.GetNotify = notify;

        imgLoader = new ImageLoader(context);
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_provider_listcontent, parent, false);
        final DataObjectHolder dataObjectHolder = new DataObjectHolder(view);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int nPosition = dataObjectHolder.getAdapterPosition();
                abc = sDataset.get(nPosition).getProvider_ID();
                String strType = sDataset.get(nPosition).getIsAccepted();

                GetNotify.getProviderId(abc, strType);


            }
        });
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, int position) {

        try {
            AdminProviderListBean adminProviderBean = sDataset.get(position);

            holder.provider_name.setText(adminProviderBean.getProvider_Name());
            holder.provider_date.setText(adminProviderBean.getCompany_name());
            providerid = adminProviderBean.getStr_jsonvalues();

            imgLoader.DisplayImage(sDataset.get(position).getProvider_image(), holder.provider_image);


            holder.btn_arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    {
                        final int nPosition = holder.getAdapterPosition();
                        abc = sDataset.get(nPosition).getProvider_ID();

                        String strType = sDataset.get(nPosition).getIsAccepted();

                        GetNotify.getProviderId(abc, strType);


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
        public Button btn_arrow;

        public ImageView provider_image;

        public DataObjectHolder(View itemView) {
            super(itemView);
            btn_arrow = (Button) itemView.findViewById(R.id.backarrow);
            provider_name = (TextView) itemView.findViewById(R.id.provider_name);
            provider_date = (TextView) itemView.findViewById(R.id.provider_date);

            provider_image = (ImageView) itemView.findViewById(R.id.provider_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }
}




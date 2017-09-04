package com.aryvart.uticianvender.admin;

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
import android.widget.TextView;
import android.widget.Toast;

import com.aryvart.uticianvender.Interface.GetNotification;
import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.genericclasses.AlertDialogManager;
import com.aryvart.uticianvender.genericclasses.GeneralData;

import com.aryvart.uticianvender.imageCache.ImageLoader;

import java.util.ArrayList;

/**
 * Created by android3 on 4/11/16.
 */
public class Admin_Share_Pay_Adapter extends RecyclerView
        .Adapter<Admin_Share_Pay_Adapter
        .DataObjectHolder> {
    private static MyClickListener myClickListener;
    Context context;
    ImageLoader imgLoader;
    String abc;
    GetNotification GetNotify;
    String providerid;
    GeneralData gD;
    private ArrayList<AdminProviderListBean> sDataset;
    String str_Data;
    AlertDialogManager alert = new AlertDialogManager();
    int nScreenHeight;

    public Admin_Share_Pay_Adapter(ArrayList<AdminProviderListBean> myDataset, Context cont, GetNotification getnotify) {

        this.sDataset = myDataset;
        context = cont;
        this.GetNotify = getnotify;
        imgLoader = new ImageLoader(context);
        gD = new GeneralData(context);
        DisplayMetrics dp = ((Activity) context).getResources().getDisplayMetrics();
        int nHeight = dp.heightPixels;

        nScreenHeight = (int) ((float) nHeight / (float) 2.1);

    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.admin_share_pay_items, parent, false);
        final DataObjectHolder dataObjectHolder = new DataObjectHolder(view);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!gD.isConnectingToInternet()) {
                    Toast.makeText(context, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                }
                else {
                    final int nPosition = dataObjectHolder.getAdapterPosition();
                    Log.i("TT", "count Adapter1 **: " + sDataset.get(nPosition).getCompany_name().length());

                }}
        });
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, int position) {

        try {
            AdminProviderListBean adminProviderBean = sDataset.get(position);
            adminProviderBean.getStrpaypalid();
            holder.provider_name.setText(adminProviderBean.getProvider_Name());
            holder.provider_balacne.setText(adminProviderBean.getStr_pro_balance());
            holder.provider_date.setText(adminProviderBean.getCompany_name());

            str_Data = adminProviderBean.getStr_usertype();

            imgLoader.DisplayImage(sDataset.get(position).getProvider_image(), holder.provider_image);

            final int nPosition = holder.getAdapterPosition();
            Log.i("KK","hello" + str_Data);

            if(str_Data.equalsIgnoreCase("referedprovider"))
            {
               holder. txt_count.setText(" Service Count :");

                if(Integer.parseInt(sDataset.get(nPosition).getCompany_name())>75) {

                    holder.  btn_pay.setBackgroundResource(R.color.red);
                }

                else
                {
                    holder.  btn_pay.setBackgroundResource(R.color.grey);
                }



            }
            else {
                holder. txt_count.setText(" Referral Count :");
                holder. btn_pay.setBackgroundResource(R.color.red);
            }
            Log.i("TT", "count Adapter **: " + sDataset.get(nPosition).getCompany_name());
            holder.btn_pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    {


                        if(str_Data.equalsIgnoreCase("referedprovider"))
                        {
                            if(Integer.parseInt(sDataset.get(nPosition).getCompany_name())>75)
                            {
                                abc = sDataset.get(nPosition).getProvider_ID();

                                String totalamount = sDataset.get(nPosition).getStr_pro_balance();
                                String count = sDataset.get(nPosition).getCompany_name();
                                String paypalid = sDataset.get(nPosition).getStrpaypalid();
                                String jsonresponse = sDataset.get(nPosition).getStr_jsonvalues();
                                String key = sDataset.get(nPosition).getStr_key();


                                Log.i("TT", "count Adapter **: " + count);
                                GetNotify.setProviderdet(abc, paypalid, count, totalamount, jsonresponse, key);

                            }
                            else
                            {

                                LayoutInflater inflater = LayoutInflater.from(context);
                                View dialogLayout = inflater.inflate(R.layout.popup_msgall, null);

                                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
                                alertDialogBuilder.setView(dialogLayout);
                                alertDialogBuilder.setCancelable(false);


                                final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                //  alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                                alertDialog.show();

                                LinearLayout llayAlert = (LinearLayout) dialogLayout.findViewById(R.id.llayalertDialog);
                                // TextView tv_alert_Title = (TextView) dialogLayout.findViewById(R.id.tv_alert_Title);
                                TextView tv_alert_Message = (TextView) dialogLayout.findViewById(R.id.message);
                                FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, nScreenHeight);
                                llayAlert.setLayoutParams(lparams);
                                tv_alert_Message.setText("Utician have to complete 75 service then only you can pay to utician");

                                Button btn_submit = (Button) dialogLayout.findViewById(R.id.ok);
                                Button btn_cancel = (Button) dialogLayout.findViewById(R.id.cancel);





                                btn_submit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        alertDialog.dismiss();


                                    }
                                });





                            }


                        }

                        else {



                            abc = sDataset.get(nPosition).getProvider_ID();

                            String totalamount = sDataset.get(nPosition).getStr_pro_balance();
                            String count = sDataset.get(nPosition).getCompany_name();
                            String paypalid = sDataset.get(nPosition).getStrpaypalid();
                            String jsonresponse = sDataset.get(nPosition).getStr_jsonvalues();

                            Log.i("TT", "paypalid Adapter **: " + paypalid);
                            GetNotify.setProviderdet(abc, paypalid, count, totalamount, jsonresponse, " ");

                        }


                    }


                }
            });
        } catch (NumberFormatException e) {
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
        public TextView provider_balacne,txt_count;
        public Button btn_pay;

        public ImageView provider_image;

        public DataObjectHolder(View itemView) {
            super(itemView);
            btn_pay = (Button) itemView.findViewById(R.id.btn_pay);
            provider_name = (TextView) itemView.findViewById(R.id.provider_name);
            provider_date = (TextView) itemView.findViewById(R.id.provider_date);
            provider_balacne = (TextView) itemView.findViewById(R.id.provi_balance);
            txt_count = (TextView) itemView.findViewById(R.id.text_count);
            provider_image = (ImageView) itemView.findViewById(R.id.provider_image);
            itemView.setOnClickListener(this);



        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }
}




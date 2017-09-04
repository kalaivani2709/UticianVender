package com.aryvart.uticianvender.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aryvart.uticianvender.Interface.GetBookingId;
import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.bean.UserHistoryBean;
import com.aryvart.uticianvender.genericclasses.GeneralData;

import com.aryvart.uticianvender.imageCache.ImageLoader;
import com.aryvart.uticianvender.provider.ProviderAcceptDecline;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by android3 on 18/6/16.
 */
public class ProviderHistoryAdapter extends RecyclerView.Adapter<ProviderHistoryAdapter.Holder> {

    ArrayList<UserHistoryBean> sDataset;
    Context context;
    ImageLoader imgLoader;
    GetBookingId getnoti;

    int nScreenHeight,ScreenHeight;
    GeneralData gD;

    public ProviderHistoryAdapter(ArrayList<UserHistoryBean> myAdapter, Context con, GetBookingId ctx) {
        this.sDataset = myAdapter;
        this.context = con;
        imgLoader = new ImageLoader(context);
        getnoti = ctx;
        gD = new GeneralData(context);
        DisplayMetrics dp = ((Activity) context).getResources().getDisplayMetrics();
        int nHeight = dp.heightPixels;
        nScreenHeight = (int) ((float) nHeight / (float) 2.4);
        ScreenHeight = (int) ((float) nHeight / (float) 2.0);

    }

    private static String getCurrentDateInSpecificFormat(int nYear, int nMonth, int nDate, int nHour, int nMinute) {


        Log.i("RAJ", "getCurrentDateInSpecificFormat method : Year :" + nYear + ",Month : " + nMonth + ",Date : " + nDate + "Hour : " + nHour + "Minute : " + nMinute);
        Log.i("RAJ", "getCurrentDateInSpecificFormat month : " + nMonth);

        String formattedDate = "";
        try {
            String strCMonth = "";
            String strCDate = "";
            if (nMonth <= 9) {
                strCMonth = "0" + nMonth;
            } else {
                strCMonth = "" + nMonth;
            }
            if (nDate <= 9) {
                strCDate = "0" + nDate;
            } else {
                strCDate = "" + nDate;
            }
            String strNeedDateVal = nYear + "-" + strCMonth + "-" + strCDate + " " + nHour + ":" + nMinute;
            //New Changes for date
            DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd hh:ss");
            Date date = originalFormat.parse(strNeedDateVal);
            String dayNumberSuffix = getDayNumberSuffix(Integer.parseInt(strCDate));
          /*  DateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy / hh:ss a");*/
            DateFormat targetFormat = new SimpleDateFormat("MMM dd'" + dayNumberSuffix + "', yyyy hh:ss a");

            formattedDate = targetFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formattedDate;
    }

    private static String getDayNumberSuffix(int day) {
        if (day >= 11 && day <= 13) {
            return "th";
        }
        switch (day % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        Holder dataObjectHolder = null;
        try {
            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.provider_historytab, parent, false);

            dataObjectHolder = new Holder(view);
            final Holder finalDataObjectHolder = dataObjectHolder;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (!gD.isConnectingToInternet()) {
                        Toast.makeText(context, "Unable to connect with Internet. Please check your internet connection and try again", Toast.LENGTH_SHORT).show();
                    }
                    else {
                    final int nPosition = finalDataObjectHolder.getAdapterPosition();

                    String jsonvale = sDataset.get(nPosition).getStr_jsonvalue();
                    String straccept = sDataset.get(nPosition).getStr_toaccept();
                    String strisaccept = sDataset.get(nPosition).getStr_isaccept();
                    String responsevalue = sDataset.get(nPosition).getStr_response();

                    Log.i("HH", "straccept1.." + straccept);
                    Log.i("HH", "strisaccept1.." + strisaccept);
                    Log.i("HH", "responsevalue.." + responsevalue);

                    if (responsevalue.equalsIgnoreCase("completed")) {
                        getnoti.bookingid("", jsonvale);


                    } else {

                        if (straccept.equalsIgnoreCase("0") && strisaccept.equalsIgnoreCase("0")) {
                            Intent i = new Intent(context, ProviderAcceptDecline.class);
                            String jsonvalues = sDataset.get(nPosition).getStr_jsonvalue();
                            i.putExtra("msg", jsonvalues);
                            Log.i("HH", "jsonvales.." + jsonvalues);
                            context.startActivity(i);
                            ((Activity) context).finish();
                        }
                        if (straccept.equalsIgnoreCase("1") && strisaccept.equalsIgnoreCase("0")) {
                            Intent i = new Intent(context, ProviderAcceptDecline.class);
                            String jsonvalues = sDataset.get(nPosition).getStr_jsonvalue();
                            i.putExtra("msg", jsonvalues);
                            Log.i("HH", "jsonvales.." + jsonvalues);
                            context.startActivity(i);
                            ((Activity) context).finish();
                        }else if (straccept.equalsIgnoreCase("0") && strisaccept.equalsIgnoreCase("1")) {

                            gD.showAlertDialog(context, "", "Booking accepted", nScreenHeight, 1);
                        }
                        else if (straccept.equalsIgnoreCase("1") && strisaccept.equalsIgnoreCase("1")) {

                            gD.showAlertDialog(context, "", "Booking accepted", nScreenHeight, 1);
                        } else if (straccept.equalsIgnoreCase("0") && strisaccept.equalsIgnoreCase("2")) {

                            gD.showAlertDialog(context, "", "Booking rejected", nScreenHeight, 1);
                        }
                        else if (straccept.equalsIgnoreCase("1") && strisaccept.equalsIgnoreCase("2")) {

                            gD.showAlertDialog(context, "", "Booking rejected", nScreenHeight, 1);
                        }else if (straccept.equalsIgnoreCase("0") && strisaccept.equalsIgnoreCase("3")) {

                            gD.showAlertDialog(context, "", "User cancelled booking", nScreenHeight, 1);
                        }
                        else if (straccept.equalsIgnoreCase("1") && strisaccept.equalsIgnoreCase("3")) {

                            gD.showAlertDialog(context, "", "User cancelled booking", nScreenHeight, 1);
                        }



                        else if (straccept.equalsIgnoreCase("0") && strisaccept.equalsIgnoreCase("5")) {

                            gD.showAlertDialog(context, "", "Booking auto cancelled", nScreenHeight, 1);
                        }
                        else if (straccept.equalsIgnoreCase("1") && strisaccept.equalsIgnoreCase("5")) {

                            gD.showAlertDialog(context, "", "Booking auto cancelled", nScreenHeight, 1);
                        } else if (straccept.equalsIgnoreCase("0") && strisaccept.equalsIgnoreCase("7")) {

                            gD.showAlertDialog(context, "", "Time Exceeded", nScreenHeight, 1);
                        }
                        else if (straccept.equalsIgnoreCase("1") && strisaccept.equalsIgnoreCase("7")) {

                            gD.showAlertDialog(context, "", "Time Exceeded", nScreenHeight, 1);
                        }
                        else if (straccept.equalsIgnoreCase("0") && strisaccept.equalsIgnoreCase("8")) {

                            gD.showAlertDialog(context, "", "Your appointment has been cancelled by yourself", nScreenHeight, 1);
                        }
                        else if (straccept.equalsIgnoreCase("1") && strisaccept.equalsIgnoreCase("8")) {

                            gD.showAlertDialog(context, "", "Your appointment has been cancelled by yourself", nScreenHeight, 1);
                        }



                        else if (sDataset.get(nPosition).getStr_response().equalsIgnoreCase("providerreviewed")) {
                            gD.showAlertDialog(context, "", "This service is been closed", nScreenHeight, 1);

                        }

                    }


                }
            }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        try {
            UserHistoryBean accountbean = sDataset.get(position);
            holder.txt_parlour_name.setText(accountbean.getStr_shop_name());
            // holder.txt_addr.setText(accountbean.getStr_address());
            holder.txt_services.setText(accountbean.getStr_services());
            holder.txt_addr.setVisibility(View.GONE);
            String strDateval = accountbean.getStr_date_time().split(" ")[0];
            String strTimeval = accountbean.getStr_date_time().split(" ")[1];

            String straccept = accountbean.getStr_toaccept();
            String strisaccept = accountbean.getStr_isaccept();

            int nYear = Integer.parseInt(strDateval.split("-")[0]);
            int nMonth = Integer.parseInt(strDateval.split("-")[1]);
            int nDate = Integer.parseInt(strDateval.split("-")[2]);
            int nHour = Integer.parseInt(strTimeval.split(":")[0]);
            int nMinute = Integer.parseInt(strTimeval.split(":")[1]);

            holder.txt_dat_tim.setText(getCurrentDateInSpecificFormat(nYear, nMonth, nDate, nHour, nMinute));
            holder.txt_price.setText(accountbean.getStr_price());

            if (sDataset.get(position).getStr_response().equalsIgnoreCase("completed")) {
                holder.txt_info.setText("Still review is not posted");
            } else if (sDataset.get(position).getStr_response().equalsIgnoreCase("providerreviewed")) {
                holder.txt_info.setText("Service completed succesfully");
            } else {

                if (straccept.equalsIgnoreCase("0") && strisaccept.equalsIgnoreCase("1")) {
                    holder.txt_info.setText("Booking accepted");

                }
                if (straccept.equalsIgnoreCase("1") && strisaccept.equalsIgnoreCase("1")) {
                    holder.txt_info.setText("Booking accepted");

                }else if (straccept.equalsIgnoreCase("0") && strisaccept.equalsIgnoreCase("2")) {
                    holder.txt_info.setText("Booking rejected");

                }
                else if (straccept.equalsIgnoreCase("1") && strisaccept.equalsIgnoreCase("2")) {
                    holder.txt_info.setText("Booking rejected");

                } else if (straccept.equalsIgnoreCase("0") && strisaccept.equalsIgnoreCase("3")) {
                    holder.txt_info.setText("User cancelled booking");

                }
                else if (straccept.equalsIgnoreCase("1") && strisaccept.equalsIgnoreCase("3")) {
                    holder.txt_info.setText("User cancelled booking");

                }else if (straccept.equalsIgnoreCase("0") && strisaccept.equalsIgnoreCase("5")) {
                    holder.txt_info.setText("Booking auto cancelled");

                }
                else if (straccept.equalsIgnoreCase("1") && strisaccept.equalsIgnoreCase("5")) {
                    holder.txt_info.setText("Booking auto cancelled");

                }else if (straccept.equalsIgnoreCase("0") && strisaccept.equalsIgnoreCase("7")) {

                    holder.txt_info.setText("Time Exceeded");
                }else if (straccept.equalsIgnoreCase("1") && strisaccept.equalsIgnoreCase("7")) {

                    holder.txt_info.setText("Time Exceeded");
                }


                else if (straccept.equalsIgnoreCase("0") && strisaccept.equalsIgnoreCase("8")) {

                    holder.txt_info.setText("Your appointment has been cancelled by yourself");
                }else if (straccept.equalsIgnoreCase("1") && strisaccept.equalsIgnoreCase("8")) {

                    holder.txt_info.setText("Your appointment has been cancelled by yourself");
                }









            }

            imgLoader.DisplayImage(sDataset.get(position).getStr_user_img(), holder.provider_image);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return sDataset.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        public TextView txt_parlour_name,
                txt_services, txt_dat_tim, txt_price, txt_addr, txt_info;
        public ImageView provider_image;

        public Holder(View itemView) {
            super(itemView);
            provider_image = (ImageView) itemView.findViewById(R.id.provider_img);
            txt_parlour_name = (TextView) itemView.findViewById(R.id.txt_parlour_name);
            txt_addr = (TextView) itemView.findViewById(R.id.txt_address);
            txt_services = (TextView) itemView.findViewById(R.id.txt_services);
            txt_dat_tim = (TextView) itemView.findViewById(R.id.txt_date_time);
            txt_price = (TextView) itemView.findViewById(R.id.txt_price);
            txt_info = (TextView) itemView.findViewById(R.id.info);
        }
    }

}

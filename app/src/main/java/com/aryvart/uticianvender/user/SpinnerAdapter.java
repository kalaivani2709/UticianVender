package com.aryvart.uticianvender.user;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.bean.SpinnerBean;

import java.util.ArrayList;

/**
 * To load the Spinner values using this Adapter
 * Created by Rajaji on 14-04-2016.
 */
public class SpinnerAdapter extends ArrayAdapter<SpinnerBean> {

    // Your sent context
    private Context context;
    // Your custom values for the spinner (User)
    // private SpinnerBean[] values;

    ArrayList<SpinnerBean> alSpinBean;

    public SpinnerAdapter(Context context, ArrayList<SpinnerBean> beanArrayList) {
      super(context, R.layout.spinner_item, beanArrayList);
        this.context = context;
        this.alSpinBean = beanArrayList;
    }



    public int getCount() {
        return alSpinBean.size();
    }

    public SpinnerBean getItem(int position) {
        return alSpinBean.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView tv;

    }

    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }


    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        View rowView = null;
        try {
            Holder holder = new Holder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.spinner_item, null);
            holder.tv = (TextView) rowView.findViewById(R.id.tvSpinItem);

            SpinnerBean sB = alSpinBean.get(position);
            holder.tv.setTextColor(Color.BLACK);
         //   Log.i("BB", "sB.get_spinid().toString() : " + sB.get_spinid().toString());
            holder.tv.setText(sB.getSpinName().toString());


} catch (Exception e) {
        e.printStackTrace();
        }

        return rowView;
        }

}
package com.aryvart.uticianvender.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.bean.SpinnerBean;

import java.util.ArrayList;


public class CustomArrayAdapter extends ArrayAdapter<SpinnerBean> {

    private ArrayList<SpinnerBean> alBean;
    private Context context;

    public CustomArrayAdapter(Context context, int nResourceId, ArrayList<SpinnerBean> objects) {
        super(context, nResourceId, objects);
        this.alBean = objects;
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.spinner_item, parent, false);
        TextView label = (TextView) row.findViewById(R.id.tvSpinItem);

        SpinnerBean sB = alBean.get(position);

        label.setText(sB.getSpinName());

        return row;
    }

}
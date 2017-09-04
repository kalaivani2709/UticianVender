package com.aryvart.uticianvender.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.bean.SpinItemData;

import java.util.ArrayList;

/**
 * Created by android3 on 27/4/16.
 */
public class SpinnerAdap extends ArrayAdapter {
    int groupid;
    Context context;
    ArrayList<SpinItemData> list;
    LayoutInflater inflater;

    public SpinnerAdap(Context context, int groupid, int id, ArrayList<SpinItemData> list) {
        super(context, id, list);
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.groupid = groupid;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = inflater.inflate(groupid, parent, false);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.img);
        imageView.setImageResource(list.get(position).getImageId());

        TextView textView = (TextView) itemView.findViewById(R.id.txt);
        textView.setText(list.get(position).getText());

        return itemView;
    }

    public View getDropDownView(int position, View convertView, ViewGroup
            parent) {
        return getView(position, convertView, parent);

    }
}


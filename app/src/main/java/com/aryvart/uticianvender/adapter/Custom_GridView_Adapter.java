package com.aryvart.uticianvender.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.bean.ImageItem;

import java.util.ArrayList;

/**
 * Created by android3 on 1/9/16.
 */
public class Custom_GridView_Adapter extends BaseAdapter {
    Context ctx;
    public ArrayList<ImageItem> aBtmap;

    public Custom_GridView_Adapter(Context context, ArrayList<ImageItem> alBitmap) {
        this.ctx = context;
        this.aBtmap = alBitmap;
        Log.i("RR", "adapter://" + alBitmap.size());
    }

    @Override
    public int getCount() {
        return aBtmap.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        Holder holder = new Holder();
        final View rowView;
        LayoutInflater inflater = (LayoutInflater) ctx.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rowView = inflater.inflate(R.layout.grid_single, null);
        holder.tv = (TextView) rowView.findViewById(R.id.grid_text);
        holder.img = (ImageView) rowView.findViewById(R.id.grid_image);

        Log.i("RR", "adapter-->" + aBtmap.get(position));
        ImageItem ImgI = aBtmap.get(position);
        holder.img.setImageBitmap(ImgI.getBmpImage());

        return rowView;
    }

    public class Holder {
        TextView tv;
        ImageView img;
    }
}

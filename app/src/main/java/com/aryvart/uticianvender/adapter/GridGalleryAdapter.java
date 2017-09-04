package com.aryvart.uticianvender.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.bean.ImageItem;
import com.aryvart.uticianvender.genericclasses.GeneralData;
import com.aryvart.uticianvender.imageCache.ImageLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Android2 on 7/13/2016.
 */
public class GridGalleryAdapter extends ArrayAdapter {
    ImageLoader imgLoader;
    int nScreenHeight;
    private Context context;
    private int layoutResourceId;
    GeneralData gD;
    private ArrayList<ImageItem> data = new ArrayList<ImageItem>();

    public GridGalleryAdapter(Context context, int layoutResourceId, ArrayList<ImageItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        imgLoader = new ImageLoader(context);

        DisplayMetrics dp = ((Activity) context).getResources().getDisplayMetrics();
        int nHeight = dp.heightPixels;

        gD = new GeneralData(context);
        nScreenHeight = (int) ((float) nHeight / (float) 1.5);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = null;
        try {
            row = convertView;
            ViewHolder holder = null;

            final ImageItem item = data.get(position);

            if (row == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) row.findViewById(R.id.image);
              //  gD.callload(context, nScreenHeight);
               /// holder. progressBar2 = (ProgressBar)row. findViewById(R.id.progressBar);

             //   holder.    progressBar2.setVisibility(View.VISIBLE);
                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        LayoutInflater inflater = LayoutInflater.from(context);
                        View dialogLayout = inflater.inflate(R.layout.imagezoom, null);

                        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
                        alertDialogBuilder.setView(dialogLayout);
                        alertDialogBuilder.setCancelable(true);


                        final android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                        //  alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        alertDialog.show();

                        RelativeLayout llayAlert = (RelativeLayout) dialogLayout.findViewById(R.id.llayalertDialog);


                        ImageView gallery_img = (ImageView) dialogLayout.findViewById(R.id.img_gallery);

                        Picasso.with(context)
                                .load(item.getImage())
                                .placeholder(R.drawable.default_user_icon)
                                .into(gallery_img);

                           // imgLoader.DisplayImage(item.getImage(), gallery_img);

                        FrameLayout.LayoutParams lparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, nScreenHeight);
                        llayAlert.setLayoutParams(lparams);





                    }
                });


                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }
            Log.i("PP", "item" + item.getImage().length());


            // imgLoader.DisplayImage(item.getImage(), holder.image);
//        holder.progressBar2.setVisibility(View.GONE);

            Picasso.with(context)
                    .load(item.getImage())
                    .placeholder(R.drawable.default_user_icon)
                    .into(holder.image);
            // holder. progressBar2.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }

//       holder. progressBar1.setVisibility(View.GONE);
        return row;
    }

    static class ViewHolder {
        ImageView image;
        ProgressBar progressBar2;
    }
}

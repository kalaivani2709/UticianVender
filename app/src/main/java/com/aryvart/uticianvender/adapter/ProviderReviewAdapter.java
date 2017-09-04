package com.aryvart.uticianvender.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.aryvart.uticianvender.R;
import com.aryvart.uticianvender.bean.ProviderReviewBean;
import com.aryvart.uticianvender.imageCache.DisplayImageOptions;
import com.aryvart.uticianvender.imageCache.ImageLoader;

import java.util.ArrayList;

/**
 * Created by android1 on 17/5/16.
 */
public class ProviderReviewAdapter extends BaseAdapter {
    ArrayList<ProviderReviewBean> alReviewBean;
    Context context;
    ImageLoader imageLoader;


    DisplayImageOptions options;

    public ProviderReviewAdapter(Context cont, ArrayList<ProviderReviewBean> reviewBean) {
        this.context = cont;
        this.alReviewBean = reviewBean;
        imageLoader = new ImageLoader(context);

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_user_icon)
                .showImageForEmptyUri(R.drawable.default_user_icon)
                .showImageOnFail(R.drawable.default_user_icon).cacheInMemory(true)
                .cacheOnDisc(true).considerExifParams(true)
                /* .displayer(new RoundedBitmapDisplayer(2)) */.build();
    }

    @Override
    public int getCount() {
        return alReviewBean.size();
    }

    @Override
    public ProviderReviewBean getItem(int position) {
        return alReviewBean.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            ViewHolder view;
            if (convertView == null) {
                Log.i("MM", "IF : ");
                view = new ViewHolder();

                convertView = inflater.inflate(R.layout.user_provider_review_content, parent, false);

                // get layout from mobile.xml
                view.tvUsername = (TextView) convertView.findViewById(R.id.reviewer_name);
                view.tvDescription = (TextView) convertView.findViewById(R.id.reviewer_comment);
                view.ReviewratingBar = (RatingBar) convertView.findViewById(R.id.review_rating);
                view.reviewerImage = (ImageView) convertView.findViewById(R.id.reviewer_image);




                convertView.setTag(view);
    
            } else {
                view = (ViewHolder) convertView.getTag();
            }

            final ProviderReviewBean reviewBean = alReviewBean.get(position);


            view.ReviewratingBar.setRating(Float.parseFloat(reviewBean.get_rating()));

            view.tvUsername.setText(reviewBean.get_nickName());
            view.tvDescription.setText(reviewBean.get_descrtiption());
            String imageUrl = reviewBean.get_imageName();

            imageLoader.DisplayImage(imageUrl, view.reviewerImage);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return convertView;
    }


    class ViewHolder {

        public TextView tvUsername;
        public TextView tvDescription;
        RatingBar ReviewratingBar;
        ImageView reviewerImage;
    }

}

package com.aryvart.uticianvender.bean;

import android.graphics.Bitmap;

/**
 * Created by Android2 on 7/13/2016.
 */
public class ImageItem {
    public Bitmap bmpImage;
    private String image;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Bitmap getBmpImage() {
        return bmpImage;
    }

    public void setBmpImage(Bitmap bmpImage) {
        this.bmpImage = bmpImage;
    }


}
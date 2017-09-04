package com.aryvart.uticianvender.bean;

/**
 * Created by android3 on 27/4/16.
 */
public class SpinItemData {

    String text;
    Integer imageId;
    public SpinItemData(String text, Integer imageId){
        this.text=text;
        this.imageId=imageId;
    }

    public String getText(){
        return text;
    }

    public Integer getImageId(){
        return imageId;
    }
}
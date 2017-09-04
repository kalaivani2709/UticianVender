package com.aryvart.uticianvender.bean;

import android.content.Context;
import android.util.Log;

/**
 * Created by ${Rajaji} on 12-07-2016.
 */
public class SpinnerBean implements Comparable<SpinnerBean>   {
    String spinName;
    Context context;
    int nId;

    public String getSpinName() {
        return spinName;
    }
    public SpinnerBean(Context ctx, String spin) {
        this.spinName = spin;
        this.context = ctx;
    }

    public void setSpinName(String spinName) {
        this.spinName = spinName;
    }

    public int getnId() {
        return nId;
    }

    public void setnId(int nId) {
        this.nId = nId;
    }

    public boolean equals(Object o) {

        Log.i("KK", "In equals " + "value is :" + this.spinName);
        //Toast.makeText(context, "You have already chosen this " + this.languages + " category please check !", Toast.LENGTH_SHORT).show();
        Log.i("KK", "Choose same**# " + "value is :");
        SpinnerBean lBean = (SpinnerBean) o;
        if (lBean.getSpinName().equals(this.spinName)) {
            Log.i("KK", "Choose same " + "value is :");
            //       Toast.makeText(context, "You have already chosen this  category please check !", Toast.LENGTH_SHORT).show();
            return true;
        }
        Log.i("KK", "Choose same** " + "value is :");


        //   Toast.makeText(context, "You have already chosen this " + this.languages + " category please check !", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public int hashCode() {
        System.out.println("In hashcode " + "value is :" + this.spinName);
        int hash = 3;
        hash = 7 * hash + this.spinName.hashCode();
        // Toast.makeText(context, "You have already chosen this " + this.languages + " category please check !", Toast.LENGTH_SHORT).show();
        return hash;
    }

    @Override
    public int compareTo(SpinnerBean another) {

        // Toast.makeText(context, "You have already chosen this " + this.languages + " category please check !", Toast.LENGTH_SHORT).show();
        return nId - another.nId;
    }

}

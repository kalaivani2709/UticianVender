package com.aryvart.uticianvender.genericclasses;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by android3 on 12/10/16.
 */
public class MyApplication extends Application {

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
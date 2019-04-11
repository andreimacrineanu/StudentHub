package com.project.studenthub;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.support.multidex.MultiDexApplication;


public class App extends MultiDexApplication{

    @SuppressLint("StaticFieldLeak")
    private static Context instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = getApplicationContext();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    public static Context getInstance(){
        return instance;
    }

}

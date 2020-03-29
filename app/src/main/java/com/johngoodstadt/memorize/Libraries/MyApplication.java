package com.johngoodstadt.memorize.Libraries;

import android.app.Application;
import android.content.Context;

import com.johngoodstadt.memorize.BuildConfig;
import com.johngoodstadt.memorize.utils.Constants;



public class MyApplication extends Application {
    private static Context context;


    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();


        Constants.whichApp.isRunning = Constants.whichApp.target.music;
        Constants.GlobalVariables.INSTANCE.hasImages = true;
        if (BuildConfig.APPLICATION_ID.equals("com.johngoodstadt.memorize.yoga")) {
            Constants.whichApp.isRunning = Constants.whichApp.target.yoga;
            Constants.GlobalVariables.INSTANCE.hasImages = true;
        }else if (BuildConfig.APPLICATION_ID.equals("com.johngoodstadt.memorize.legal.demo")) {
            Constants.whichApp.isRunning = Constants.whichApp.target.legaldemo;
            Constants.GlobalVariables.INSTANCE.hasImages = false;
        }else if (BuildConfig.APPLICATION_ID.equals("com.johngoodstadt.memorize.general")) {
            Constants.whichApp.isRunning = Constants.whichApp.target.general;
            Constants.GlobalVariables.INSTANCE.hasImages = false;
        }



    }

    public static Context getAppContext() {
        return MyApplication.context;
    }


}


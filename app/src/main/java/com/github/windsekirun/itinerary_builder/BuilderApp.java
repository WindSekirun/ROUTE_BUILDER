package com.github.windsekirun.itinerary_builder;

import android.app.Application;
import android.content.Context;

/**
 * Created by Pyxis on 2016. 10. 6..
 */
public class BuilderApp extends Application {
    public static Context mContext = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
}

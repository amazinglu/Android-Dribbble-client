package com.example.amazinglu.my_dribbble;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * the class name here is the same as the name of the application in AndroidManifest
 * Fresco can noly be initialized once
 * need to initialize Fresco in this class
 * */
public class MyDribbbleApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}

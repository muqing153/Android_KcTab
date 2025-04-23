package com.muqing.kctab;

import android.app.Application;

import com.muqing.wj;

public class main extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        wj.data = wj.data(this);
    }
}

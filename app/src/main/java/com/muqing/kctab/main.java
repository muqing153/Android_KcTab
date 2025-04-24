package com.muqing.kctab;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.color.DynamicColors;
import com.muqing.wj;

public class main extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        wj.data = wj.data(this);

        SharedPreferences sharedPreferences = getSharedPreferences("theme", MODE_PRIVATE);
        int mods = sharedPreferences.getInt("mods", 0);
        setThemeMode(mods);
        //是否大于Android12+=
        boolean isAndroid12 = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S;
        if (isAndroid12) {
            DynamicColors.applyToActivitiesIfAvailable(this);
        }
    }

    public static void setThemeMode(int position) {
        switch (position) {
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO); // 强制浅色
                break;
            case 2:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);// 强制深色
                break;
        }
    }
}

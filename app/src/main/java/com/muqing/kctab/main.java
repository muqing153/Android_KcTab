package com.muqing.kctab;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.color.DynamicColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.muqing.AppCompatActivity;
import com.muqing.gj;
import com.muqing.wj;

public class main extends Application {
    private static Application application;

    //校园网IP
    public static String XYIP = "10.200.30.200";
    public static Application getApplication() {
        return application;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        gj.Debug = (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        wj.data = wj.data(this);

        SharedPreferences sharedPreferences = getSharedPreferences("theme", MODE_PRIVATE);
        int mods = sharedPreferences.getInt("mods", 0);
        setThemeMode(mods);
        //是否大于Android12+=
        if (sharedPreferences.getBoolean("dynamic", false) && DynamicColors.isDynamicColorAvailable()) {
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


    public static AlertDialog LoadIng(Context context) {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(context);
        dialog.setView(R.layout.load_dialog);
        AlertDialog show = dialog.show();
        show.setCanceledOnTouchOutside(false);
        show.setCancelable(false);
        if (show.getWindow() != null) {
            show.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        return show;
    }
}

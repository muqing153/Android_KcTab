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

import java.io.File;
import java.time.LocalDate;

public class main extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
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
    public static String getSchoolYearTerm(LocalDate date) {
        int year = date.getYear();
        int month = date.getMonthValue();

        String schoolYear;
        String term;

        if (month >= 8) {
            // 8月 ~ 12月：新学年上学期
            schoolYear = year + "-" + (year + 1);
            term = "1";
        } else if (month == 1) {
            // 1月：依然是上学期，但属于上一年的学年
            schoolYear = (year - 1) + "-" + year;
            term = "1";
        } else {
            // 2月 ~ 7月：是下学期，属于上一个学年
            schoolYear = (year - 1) + "-" + year;
            term = "2";
        }

        return schoolYear + "-" + term;
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

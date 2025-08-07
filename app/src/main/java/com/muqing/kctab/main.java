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

    /**
     * 比较不存在的学年
     * @param currentTerm
     * @param savedTerm
     * @return
     */
    public static boolean isFutureSchoolYearTerm(String currentTerm, String savedTerm) {
        if (savedTerm == null || currentTerm == null) return false;

        String[] curParts = currentTerm.split("-");
        String[] savParts = savedTerm.split("-");

        if (curParts.length != 3 || savParts.length != 3) return false;

        int curStart = Integer.parseInt(curParts[0]);
        int curEnd = Integer.parseInt(curParts[1]);
        int curTerm = Integer.parseInt(curParts[2]);

        int savStart = Integer.parseInt(savParts[0]);
        int savEnd = Integer.parseInt(savParts[1]);
        int savTerm = Integer.parseInt(savParts[2]);

        // 比较起始年
        if (savStart > curStart) {
            return true;
        } else if (savStart == curStart) {
            // 同一个学年，比较学期（1是上学期，2是下学期）
            return savTerm > curTerm;
        } else {
            return false;
        }
    }

    public static int getXueNianPosition(String schoolYearTerm) {
        if (schoolYearTerm == null || !schoolYearTerm.matches("\\d{4}-\\d{4}-[12]")) {
            gj.sc("输入格式错误，应为：YYYY-YYYY-1 或 YYYY-YYYY-2");
            return 0;
        }

        String[] parts = schoolYearTerm.split("-");
        String term = parts[2];

        if ("1".equals(term)) {
            return 1;
        } else {
            return 2;
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

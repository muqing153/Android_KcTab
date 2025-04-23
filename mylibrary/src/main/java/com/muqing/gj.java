package com.muqing;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Objects;

public class gj {
    private static final String TAG = "打印";

    public static void sc(Object objects) {
        Log.i(TAG, "sc: " + objects);
    }


    /**
     * 判断是否是平板
     *
     * @param context
     * @return
     */
    public static boolean isTablet(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels > dm.heightPixels;
    }

    public static int getThemeColor(Context context, int id) {
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(id, typedValue, true)) {
            return typedValue.data;
        } else {
            return -1;
        }
    }

    public static void llq(Context context, String str, Class<?> llq) {
        context.startActivity(new Intent(context, llq).putExtra("url", str));
    }

    public static void fx(Context context, String str) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, str);
        context.startActivity(shareIntent);
    }


    /**
     * 复制文字到剪切板
     */
    public static void fz(Context context, String text) {
        ClipboardManager systemService =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建能够存入剪贴板的ClipData对象
        //‘Label’这是任意文字标签
        ClipData mClipData = ClipData.newPlainText("Label", text);
        //将ClipData数据复制到剪贴板：
        systemService.setPrimaryClip(mClipData);
        gj.ts(context, "复制成功");
    }

    public static void ts(Context a, String b) {
        Toast.makeText(a, b, Toast.LENGTH_SHORT).show();
    }

    public static boolean isWiFiConnected() {
        try {
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                    if (networkInterface.getDisplayName().contains("wlan")) {
                        return true;  // Wi-Fi网络
                    } else if (networkInterface.getDisplayName().contains("rmnet")) {
                        return false;  // 流量网络
                    }
                }
            }
        } catch (SocketException e) {
            gj.sc(e);
        }
        return false;  // 默认为流量网络
    }

    public static void tcjp(EditText editText) {
        editText.requestFocus();//获取焦点
        InputMethodManager imm = (InputMethodManager)
                editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        gj.sc(imm.isActive());
        //没有显示键盘，弹出
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    public static void ycjp(EditText editText) {
        InputMethodManager imm = (InputMethodManager)
                editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) //有显示键盘，隐藏
            imm.hideSoftInputFromWindow(editText.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
    }


    public static int getbackgroundColor(AppCompatActivity appCompatActivity) {
        TypedArray array = appCompatActivity.getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.colorBackground
//                android.R.attr.textColorPrimary,
        });
        int backgroundColor = array.getColor(0, 0xFF00FF);
//        int textColor = array.getColor(1, 0xFF00FF);
        array.recycle();
        return backgroundColor;
    }

    public static int getztl(Context context) {
        // 获得状态栏高度
        @SuppressLint({"InternalInsetResource", "DiscouragedApi"}) int resourceId =
                context.getResources().
                        getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * dp转px
     *
     * @param context
     * @param dpValue
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}

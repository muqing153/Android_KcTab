package com.muqing.kctab;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.muqing.gj;
import com.muqing.wl;

import org.json.JSONObject;

public class GXThread extends Thread {

    private final Activity activity;
    /**
     * 自动更新
     *
     * @param activity
     */
    public GXThread(Activity activity) {
        this.activity = activity;
        SharedPreferences sp = activity.getSharedPreferences("setting", MODE_PRIVATE);
        if (sp.getBoolean("jcgx", true)) {
            long lastTime = System.currentTimeMillis(); // 获取当前时间
            long jcgxTime = sp.getLong("jcgx_time", 0);
            if (lastTime - jcgxTime > 3600000) {
                // 大于一小时
                gj.sc("超过一小时了");
                sp.edit().putLong("jcgx_time", lastTime).apply();
                start();
            } else {
                // 未超过一小时
                gj.sc("未超过一小时");
            }
        }
    }

    private Runnable runnable;
    public GXThread(Activity activity, Runnable runnable) {
        this.activity = activity;
        this.runnable = runnable;
        start();
    }

    @Override
    public void run() {
        //检测是否Debug运行
        if (gj.Debug) {
//            return;
        }
        try {
            String versionName = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
            String hq = wl.post("https://muqingcandy.top/php/GetKCTabBB.php", new Object[][]{
                    {"version", versionName}
            });
            gj.sc(hq);
            if (hq != null) {
                JSONObject jsonObject = new JSONObject(hq);
                String message = jsonObject.getString("msg");
                if (jsonObject.getInt("code") == 200) {
                    String nickname = jsonObject.getString("name");
                    String version = jsonObject.getString("version");
                    String apk_url = jsonObject.getString("apk_url");
                    //获取本地版本versionName
                    if (!version.equals(versionName)) {
                        activity.runOnUiThread(() -> new MaterialAlertDialogBuilder(activity)
                                .setTitle(nickname)
                                .setMessage(message + "\n" + versionName + "->" + version)
                                .setPositiveButton("确定", (dialogInterface, i) -> gj.llq(activity, apk_url))
                                .show());
                    } else if (runnable != null) {
                        activity.runOnUiThread(runnable);
                    }
                    return;
                }
                throw new Exception(message);
            }
            throw new Exception("网络连接失败");
        } catch (Exception e) {
            gj.sc(this.getClass().getName() + " " + e.getMessage());
            error(e.getMessage());
        }
    }

    public void error(String msg) {

    }
}
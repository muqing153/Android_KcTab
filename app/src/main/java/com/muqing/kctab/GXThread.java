package com.muqing.kctab;

import android.app.Activity;
import android.content.pm.ApplicationInfo;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.muqing.gj;
import com.muqing.wl;

import org.json.JSONObject;

public class GXThread extends Thread {

    Activity activity;

    public GXThread(Activity activity) {
        this.activity = activity;
        start();
    }

    Runnable runnable;

    public GXThread(Activity activity, Runnable runnable) {
        this.activity = activity;
        this.runnable = runnable;
        start();
    }

    @Override
    public void run() {
        //检测是否Debug运行
        if ((activity.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
            return;
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

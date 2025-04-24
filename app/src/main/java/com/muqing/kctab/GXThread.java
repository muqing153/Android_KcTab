package com.muqing.kctab;

import android.app.Activity;
import android.content.DialogInterface;

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

    @Override
    public void run() {

        try {
            String hq = wl.hq("https://muqingcandy.top/yiyoubiao.json", "");
            if (hq != null) {
                JSONObject jsonObject = new JSONObject(hq);
                String nickname = jsonObject.getString("name");
                String message = jsonObject.getString("message");
                String version = jsonObject.getString("version");
                //获取本地版本versionName
                String versionName = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
                if (!version.equals(versionName)) {
                    activity.runOnUiThread(() -> new MaterialAlertDialogBuilder(activity)
                            .setTitle(nickname)
                            .setMessage(message + "\n" + versionName + "->" + version)
                            .setPositiveButton("确定", (dialogInterface, i) -> {
                                gj.llq(activity, "https://muqingcandy.top");
                            })
                            .show());
                }
            }
        } catch (Exception e) {
            gj.sc(this.getClass().getName() + " " + e.getMessage());
        }

    }
}

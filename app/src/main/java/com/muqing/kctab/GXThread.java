package com.muqing.kctab;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.content.FileProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.muqing.gj;
import com.muqing.wj;
import com.muqing.wl;

import org.json.JSONObject;

import java.io.File;

public class GXThread extends Thread {

    private final Activity activity;
    private String version;//当前软件的版本号
    private String newversion;//最新的版本号
    private String apk_url;

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
        try {
            version = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
            String hq = wl.post("https://muqingcandy.top/php/GetKCTabBB.php", new Object[][]{
                    {"version", version}
            });
            gj.sc(hq);
            if (hq != null) {
                JSONObject jsonObject = new JSONObject(hq);
                String message = jsonObject.getString("msg");
                if (jsonObject.getInt("code") == 200) {
                    String nickname = jsonObject.getString("name");
                    newversion = jsonObject.getString("version");
                    apk_url = jsonObject.getString("apk_url");
                    //获取本地版本versionName
                    if (!newversion.equals(version)) {
                        activity.runOnUiThread(() -> new MaterialAlertDialogBuilder(activity)
                                .setTitle(nickname)
                                .setMessage(message + "\n" + version + "->" + newversion)
                                .setPositiveButton("确定", (dialogInterface, i) -> gx())
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

    public void gx() {
        if (!activity.getPackageManager().canRequestPackageInstalls()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivity(intent);
            return;
        }
        File file = new File(wj.data, "apk/");
        if (!file.exists()) {
            boolean mkdirs = file.mkdirs();
            if (!mkdirs) {
                gj.sc("创建文件夹失败");
                return;
            }
        }
        file = new File(file, newversion + ".apk");
        gj.sc(file.getPath());
        if (file.exists()) {
            gj.sc("文件存在");
            installApk(file);
        } else {
            gj.sc("从网络中下载:" + apk_url);
            wl.xz(apk_url, file);
        }
    }

    public void gx(String url, String newversion) {
        this.apk_url = url;
        this.newversion = newversion;
        this.gx();
    }



    public void installApk(File apkFile) {
        if (apkFile == null || !apkFile.exists()) return;

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri apkUri;
        apkUri = FileProvider.getUriForFile(
                activity,
                activity.getPackageName() + ".fileprovider",
                apkFile
        );
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        activity.startActivity(intent);
    }

}
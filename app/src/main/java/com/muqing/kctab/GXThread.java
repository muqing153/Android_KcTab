package com.muqing.kctab;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.muqing.gj;
import com.muqing.kctab.databinding.DialogDownloadApkBinding;
import com.muqing.wj;
import com.muqing.wl;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class GXThread extends Thread {

    private final Activity activity;
    private String version;//当前软件的版本号
    private String newversion;//最新的版本号
    private String apk_url;

    /**
     * 自动更新
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
            String hq = wl.post("https://muqingcandy.top/api/GetKCTabBB", new Object[][]{
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
                        activity.runOnUiThread(() -> {

                            MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(activity);
                            DialogDownloadApkBinding dialogBinding = DialogDownloadApkBinding.inflate(activity.getLayoutInflater());
                            dialog.setView(dialogBinding.getRoot());
                            AlertDialog c = dialog.create();
                            if (c.getWindow() != null) {
                                c.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            }
                            dialogBinding.bbh.setText(String.format("%s %s->%s", nickname, version, newversion));
                            dialogBinding.info.setText(message);
                            dialogBinding.no.setOnClickListener(view -> c.dismiss());
                            dialogBinding.web.setOnClickListener(view -> {
                                c.dismiss();
                                gj.llq(activity, apk_url);
                            });
                            dialogBinding.app.setOnClickListener(view -> {
                                gx(c, dialogBinding);
                            });
                            c.setCanceledOnTouchOutside(false);
                            c.setCancelable(false);
                            c.show();
                        });
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

    public void gx(Dialog dialog, DialogDownloadApkBinding dialogBinding) {
        if (!activity.getPackageManager().canRequestPackageInstalls()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivity(intent);
            return;
        }
        if (!new File(wj.data, "apk/").exists()) {
            boolean mkdirs = new File(wj.data, "apk/").mkdirs();
            if (!mkdirs) {
                gj.sc("创建文件夹失败");
                return;
            }
        }
        File file = new File(new File(wj.data, "apk/" + newversion + ".apk").getPath());
//        gj.sc(file.getPath());
//        wj.sc(file);
        if (file.exists()) {
            gj.sc("文件存在");
            installApk(file);
        } else {
            gj.sc("从网络中下载:" + apk_url);
            OkHttpClient client = new OkHttpClient.Builder().build();
            Request request = new Request.Builder()
                    .url(apk_url)
                    .build();
            dialogBinding.gx.setVisibility(View.VISIBLE);
            dialogBinding.no.setEnabled(false);
            dialogBinding.web.setEnabled(false);
            dialogBinding.app.setEnabled(false);
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(activity, "下载失败", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (!response.isSuccessful() || response.body() == null) {
                        activity.runOnUiThread(() -> {
                            Toast.makeText(activity, "下载失败", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        });
                        return;
                    }

                    InputStream is = null;
                    FileOutputStream fos = null;
                    try {
                        long total = response.body().contentLength(); // 文件总大小
                        long downloaded = 0;

                        is = response.body().byteStream();
                        fos = new FileOutputStream(file);

                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = is.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                            downloaded += len;

                            int progress = (int) (downloaded * 100 / total);

                            // 更新进度条 UI
                            activity.runOnUiThread(() -> {
                                dialogBinding.gxbar.setProgress(progress);
                                dialogBinding.gxtext.setText(String.format(Locale.CANADA, "%d %%", progress));
                            });
                        }
                        fos.flush();

                        // 下载完成 → 安装
                        activity.runOnUiThread(() -> {
                            Toast.makeText(activity, "下载完成", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            installApk(file);
                        });
                    } catch (Exception e) {
                        gj.sc(e);
                        activity.runOnUiThread(() -> {
                            Toast.makeText(activity, "下载失败", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        });
                    } finally {
                        if (is != null) is.close();
                        if (fos != null) fos.close();
                    }
                }
            });

        }
    }

    public void gx(String url, String newversion) {
        this.apk_url = url;
        this.newversion = newversion;
//        this.gx();
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
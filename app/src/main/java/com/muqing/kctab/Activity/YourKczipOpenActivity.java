package com.muqing.kctab.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.muqing.AppCompatActivity;
import com.muqing.gj;
import com.muqing.kctab.databinding.ActivityYourkczipBinding;
import com.muqing.wj;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class YourKczipOpenActivity extends AppCompatActivity<ActivityYourkczipBinding> {
    @Override
    protected ActivityYourkczipBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivityYourkczipBinding.inflate(layoutInflater);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();

        Uri data = getIntent().getData();
        gj.sc(data);
        if (data != null) {
            try {
//                gj.sc(inputStream);
                if (wj.data == null) {
                    wj.data = wj.data(this);
                }
                File outputDir = new File(wj.data, "TabList");
                wj.sc(outputDir);
                if (!outputDir.exists()) outputDir.mkdirs();
                InputStream inputStream = getContentResolver().openInputStream(data);
                unzipFromUri(this, inputStream, outputDir);
                gj.sc(outputDir);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "无法读取文件", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static boolean unzipFromUri(Context context, InputStream inputStream, File targetDir) throws IOException {
//              = context.getContentResolver().openInputStream(uri);
        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(inputStream));
        ZipEntry ze;
        while ((ze = zis.getNextEntry()) != null) {
            File outFile = new File(targetDir, ze.getName());
            if (ze.isDirectory()) {
                outFile.mkdirs();
            } else {
                // 确保父文件夹存在
                outFile.getParentFile().mkdirs();

                try (FileOutputStream fos = new FileOutputStream(outFile)) {
                    byte[] buffer = new byte[4096];
                    int count;
                    while ((count = zis.read(buffer)) != -1) {
                        fos.write(buffer, 0, count);
                    }
                }
            }
            zis.closeEntry();
        }
        Toast.makeText(context, "解压完成", Toast.LENGTH_SHORT).show();
        return true;
    }


}

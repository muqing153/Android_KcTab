package com.muqing;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Objects;

public class wj {
    //    File internalStorageDir = getApplicationContext().getFilesDir();

    public static boolean xrwb(String fileurl, String name, String text) {

        if (text == null) {
            text = "";
        }
        File file = new File(fileurl, name);
//如果文件不存在，创建文件
        try {
            File parentFile = file.getParentFile();
            if (!parentFile.isDirectory()) {
                parentFile.mkdirs();
            }
            if (!file.exists())
                file.createNewFile();
//创建FileOutputStream对象，写入内容
            FileOutputStream fos = new FileOutputStream(file);
//向文件中写入内容
            fos.write(text.getBytes());
            fos.close();
            return true;
        } catch (Exception e) {
            gj.sc("xrwb: " + e.getMessage());

        }
        return false;
    }

    public static boolean xrwb(String filename, String text) {
        if (text == null) {
            text = "";
        }
        File file = new File(filename);
//如果文件不存在，创建文件
        try {
            File parentFile = file.getParentFile();
            if (!parentFile.isDirectory()) {
                parentFile.mkdirs();
            }
            if (!file.exists())
                file.createNewFile();
//创建FileOutputStream对象，写入内容
            FileOutputStream fos = new FileOutputStream(file);
//向文件中写入内容
            fos.write(text.getBytes());
            fos.close();
            return true;
        } catch (Exception e) {
            gj.sc("xrwb: " + e.getMessage());
        }
        return false;
    }
    public static boolean xrwb(File file, String text) {
        if (text == null) {
            text = "";
        }
//如果文件不存在，创建文件
        try {
            File parentFile = file.getParentFile();
            if (!parentFile.isDirectory()) {
                parentFile.mkdirs();
            }
            if (!file.exists())
                file.createNewFile();
//创建FileOutputStream对象，写入内容
            FileOutputStream fos = new FileOutputStream(file);
//向文件中写入内容
            fos.write(text.getBytes());
            fos.close();
            return true;
        } catch (Exception e) {
            gj.sc("xrwb: " + e.getMessage());
        }
        return false;
    }

    public static String $;
    public static String data;

    public static String data(Context context) {
//        return context.getApplicationContext().getFilesDir().toString();
        return Objects.requireNonNull(context.getExternalFilesDir(null)).toString();
    }


    public static String dqwb(String fileurl, String name) {
        try {
            File file = new File(fileurl, name);
            if (!file.exists()) {
                return null;
            }
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            StringBuilder str = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                str.append(line);
            }
            br.close();
            fis.close();
            return str.toString();
        } catch (Exception e) {
            gj.sc("dqwb: " + e.getMessage());
        }
        return null;
    }

    public static String dqwb(String filename) {
        try {
            File file = new File(filename);
            if (!file.exists()) {
                return null;
            }
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            StringBuilder str = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                str.append(line);
            }
            br.close();
            fis.close();
            return str.toString();
        } catch (Exception e) {
            gj.sc("dqwb: " + e.getMessage());
        }
        return null;
    }


    public static class SaveBitMap {
        public String FileName = String.valueOf(System.currentTimeMillis());

        public SaveBitMap(Context context, Bitmap bitmap) {
            // 判断 Android 版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10 及以上版本使用 MediaStore 保存图片
                saveImageApi29AndAbove(context, bitmap);
            } else {
                // Android 9 及以下版本使用传统存储方式
                saveImageApi28AndBelow(context, bitmap);
            }
        }

        private void saveImageApi29AndAbove(Context context, Bitmap bitmap) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "my_image.jpg"); // 设置图片的文件名
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg"); // 设置图片类型
            contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + FileName); // 设置保存的文件夹（相册）

            // 获取系统图片内容提供者的 Uri
            Uri imageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            try (OutputStream outputStream = context.getContentResolver().openOutputStream(imageUri)) {
                if (outputStream != null) {
                    // 将 Bitmap 压缩成 JPEG 格式并保存
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    Toast.makeText(context, "图片保存成功", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "图片保存失败", Toast.LENGTH_SHORT).show();
            }
        }

        private void saveImageApi28AndBelow(Context context, Bitmap bitmap) {
            File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), FileName);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, "my_image.jpg");
            try (FileOutputStream out = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out); // 保存为 JPEG 格式
                out.flush();
                MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, null);
                Toast.makeText(context, "图片保存成功", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "图片保存失败", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

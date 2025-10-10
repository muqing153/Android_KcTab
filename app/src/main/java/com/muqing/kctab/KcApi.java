package com.muqing.kctab;

import static com.muqing.kctab.LoginApi.Token;
import static com.muqing.kctab.MainActivity.extractDate;

import android.icu.text.SimpleDateFormat;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.muqing.gj;
import com.muqing.wj;
import com.muqing.wl;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class KcApi {
    public static String api = "http://jw.qdpec.edu.cn:8088";

    @Nullable
    public static String GetCurriculum(String week, String kbjcmsid) throws Exception {
        if (kbjcmsid == null) {
            kbjcmsid = "";
        }
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder().url(api + "/njwhd/student/curriculum?week=" + week + "&kbjcmsid=" + kbjcmsid).method("POST", body).addHeader("Pragma", "no-cache").addHeader("token", Token).addHeader("User-Agent", "Apifox/1.0.0 (https://apifox.com)").addHeader("Accept", "*/*").addHeader("Host", "jw.qdpec.edu.cn:8088").addHeader("Connection", "keep-alive").build();
        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            gj.sc("GetCurriculum: 请求错误");
            return null;
        }
        if (response.body() != null) {
            return response.body().string();
        } else {
            return null;
        }
    }

    /**
     * 获取当前周数
     * 根据开学日期和当前日期计算第几周
     *
     * @param startDate 开学日期（格式：yyyy-MM-dd）
     * @param endDate   当前日期（格式：yyyy-MM-dd）
     * @return 当前是第几周
     */
    public static int getWeek(String startDate, String endDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            long start = sdf.parse(startDate).getTime();
            long end = sdf.parse(endDate).getTime();

            // 计算相差天数
            long diffDays = TimeUnit.DAYS.convert(end - start, TimeUnit.MILLISECONDS);

            // 计算第几周（从第1周开始算）
            int week = (int) (diffDays / 7) + 1;

            // 如果未到开学日期，则返回 1
            return Math.max(week, 1);

        } catch (ParseException e) {
            gj.sc(e);
            return 1;
        }
    }

    public static int teachingWeek() {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder().url("http://jw.qdpec.edu.cn:8088/njwhd/teachingWeek")
                    .method("POST", body).addHeader("Accept", "application/json, text/plain, */*")
                    .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8").addHeader("Cache-Control", "no-cache")
                    .addHeader("Connection", "keep-alive")
                    .addHeader("Origin", "http://jw.qdpec.edu.cn:8088")
                    .addHeader("Referer", "http://jw.qdpec.edu.cn:8088/")
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/138.0.0.0 Safari/537.36 Edg/138.0.0.0")
                    .addHeader("token", Token)
                    .addHeader("Host", "jw.qdpec.edu.cn:8088").build();
            Response response = client.newCall(request).execute();
            String teachingWeek = response.body().string();
            JSONObject jsonObject = new JSONObject(teachingWeek);
            int code = jsonObject.getInt("code");
            JSONArray data = jsonObject.getJSONArray("data");
            return data.length();
        } catch (Exception e) {
            gj.sc(e);
        }
        return 0;
    }

    public static boolean Load() {
        try {
            int length = teachingWeek();
            if (length > 0) {
                Curriculum curriculum = new Curriculum();
                for (int i = 1; i <= length; i++) {
                    String value = GetCurriculum(String.valueOf(i), "");
                    if (value == null) {
                        return false;
                    }
                    Curriculum.JieXi(value, curriculum);

                }
                wj.xrwb(MainActivity.fileTabList, new Gson().toJson(curriculum));
            }
            return true;
        } catch (Exception e) {
            gj.sc(e);
        }
        return false;
    }

    public static void putjsonkc(Curriculum curriculum) {
    }

//    public static boolean Load(int week) throws Exception {
//        String value = GetCurriculum(String.valueOf(week), "");
//        if (value == null) {
//            return false;
//        }
//        putjsonkc(curriculum, gson);
//        return true;
//    }

    public static Curriculum GetCurriculumFile(String path) {
        String dqwb = wj.dqwb(path);
        Curriculum curriculum = new Gson().fromJson(dqwb, Curriculum.class);
        return curriculum;
    }

    public static List<String> GetPathFileList(File dirpath) {
        List<String> list = new ArrayList<>();
        Pattern datePattern = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}.*");
        File[] files = dirpath.listFiles((dir, name) -> datePattern.matcher(name).matches());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Arrays.sort(files, (f1, f2) -> {
            LocalDate d1 = extractDate(f1.getName(), datePattern, formatter);
            LocalDate d2 = extractDate(f2.getName(), datePattern, formatter);
            return d1.compareTo(d2); // 升序；改成 d2.compareTo(d1) 为降序
        });
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isFile()) {
                try {
                    String dqwb = wj.dqwb(file);
                    Curriculum curriculum = new Gson().fromJson(dqwb, Curriculum.class);
//                    curriculum.data.get(0).week = i + 1;
                    wj.xrwb(file, new Gson().toJson(curriculum));
                    list.add(file.getPath());
                } catch (Exception e) {
                    gj.sc(e);
                }
            }
        }
        return list;
    }


}

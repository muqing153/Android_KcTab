package com.muqing.kctab;

import static com.muqing.kctab.LoginApi.Token;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.muqing.gj;
import com.muqing.wj;

import java.io.File;
import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class KcApi {
    public static String api="http://jw.qdpec.edu.cn:8088";
    @Nullable
    public static String GetCurriculum(String week, String kbjcmsid) throws Exception {
        if (kbjcmsid == null) {
            kbjcmsid = "";
        }
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(api + "/njwhd/student/curriculum?week=" + week + "&kbjcmsid=" + kbjcmsid)
                .method("POST", body)
                .addHeader("Pragma", "no-cache")
                .addHeader("token", Token)
                .addHeader("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                .addHeader("Accept", "*/*")
                .addHeader("Host", "jw.qdpec.edu.cn:8088")
                .addHeader("Connection", "keep-alive")
                .build();
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
     *
     * @return
     */
    public static int getWeek() {
        // 获取当前日期（格式为 yyyy-MM-dd）
        LocalDate currentDate = LocalDate.now();
        // 正则表达式，用于匹配字符串中的日期格式
        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        // 初始化周数计数器，从第1周开始
        int i = 1;
        // 遍历 TabList 列表
        for (String s : MainActivity.TabList) {
            // 在字符串中查找符合日期格式的部分
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                // 解析找到的日期字符串
                String dateStr = matcher.group();
                LocalDate date2 = LocalDate.parse(dateStr);
                // 如果当前日期在找到的日期之前
                if (currentDate.isBefore(date2) || currentDate.isEqual(date2)) {
                    // 返回解析后的课程表对象
                    return i;
                }
            }
            // 周数递增
            i++;
        }
        // 如果遍历结束没有找到符合条件的数据，返回 null
        return 0;
    }

    public static boolean Load(String Token) {
        try {
//            File file = MainActivity.fileTabList;
            LoginApi.Token = Token;
            Gson gson = new Gson();
            for (int i = 1; i <= 20; i++) {
                String value = GetCurriculum(String.valueOf(i), "");
//                gj.sc(""+value);
                if (value == null) {
                    return false;
                }
                Curriculum curriculum = gson.fromJson(value, Curriculum.class);
                curriculum.data.get(0).week = i;
                putjsonkc(curriculum, gson);
            }
            return true;
        } catch (Exception e) {
            gj.sc(e);
        }
        return false;
    }

    public static void putjsonkc(Curriculum curriculum, Gson gson) {
        int length = curriculum.data.get(0).date.size();
        String zc = curriculum.data.get(0).date.get(length - 1).mxrq;
        String semesterId = curriculum.data.get(0).topInfo.get(0).semesterId;
        File file = new File(wj.data, "TabList/" + semesterId);
        gj.sc(file.toString());
        wj.xrwb(new File(file, zc + ".txt"), gson.toJson(curriculum));
    }

    public static boolean Load(String Token, Integer[] week) throws Exception {
//        File file = MainActivity.fileTabList;
        LoginApi.Token = Token;
        Gson gson = new Gson();
        for (int i : week) {
            String value = GetCurriculum(String.valueOf(i), "");
            gj.sc("Load(String Token, Integer[] week)  " + value);
            if (value == null) {
                return false;
            }
            Curriculum curriculum = gson.fromJson(value, Curriculum.class);
            curriculum.data.get(0).week = i;
            putjsonkc(curriculum, gson);
        }
        return true;
    }

    /**
     * 获取当前周的课程表数据。
     * 遍历 TabList 中的字符串，找到第一个日期在当前日期之后的项，
     * 然后解析该项对应的课程表数据并返回。
     *
     * @return 课程表对象 Curriculum，如果未找到符合条件的数据则返回 null。
     */
    @Nullable
    public static Curriculum GetCurriculum() {
        // 获取当前日期（格式为 yyyy-MM-dd）
        LocalDate currentDate = LocalDate.now();

        // 正则表达式，用于匹配字符串中的日期格式
        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        // 初始化周数计数器，从第1周开始
        int i = 1;
        // 遍历 TabList 列表
        for (String s : MainActivity.TabList) {
            // 在字符串中查找符合日期格式的部分
            Matcher matcher = pattern.matcher(s);
            if (matcher.find()) {
                // 解析找到的日期字符串
                String dateStr = matcher.group();
                LocalDate date2 = LocalDate.parse(dateStr);
                // 如果当前日期在找到的日期之前
                if (currentDate.isBefore(date2) || currentDate.isEqual(date2)) {
                    // 使用 dqwb 方法处理字符串，获取对应的课程表 JSON
                    String dqwb = wj.dqwb(s);
                    // 将 JSON 字符串反序列化为 Curriculum 对象
                    Curriculum curriculum = new Gson().fromJson(dqwb, Curriculum.class);
                    // 将第一个数据项的 week 设置为当前周数
                    curriculum.data.get(0).week = i;
                    // 如果当前本周 benzou 还未设置，赋值为当前周
                    if (MainActivity.benzhou == 0) {
                        MainActivity.benzhou = i;
                    }
                    // 返回解析后的课程表对象
                    return curriculum;
                }
            }
            // 周数递增
            i++;
        }
        // 如果遍历结束没有找到符合条件的数据，返回 null
        return null;
    }

    public static Curriculum GetCurriculumFile(String path) {
        int i = 1;
        for (String s :
                MainActivity.TabList) {
            if (s.equals(path)) {
                break;
            }
            i++;
        }
        String dqwb = wj.dqwb(path);
        Curriculum curriculum = new Gson().fromJson(dqwb, Curriculum.class);
        curriculum.data.get(0).week = i;
        return curriculum;
    }


}

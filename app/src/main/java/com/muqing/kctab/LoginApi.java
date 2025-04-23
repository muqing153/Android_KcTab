package com.muqing.kctab;


import android.text.TextUtils;

import com.muqing.gj;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginApi {
    public static String Token;


    public static boolean IsToken(String token) {
        if (TextUtils.isEmpty(token)) {
            return false;
        }
        //Token长度不正确
        if (token.length() < 100 || token.length() > 200) {
            return false;
        }


        String[] parts = token.split("\\.");
        if (parts.length != 3) return false;

        for (String part : parts) {
            if (!part.matches("^[A-Za-z0-9_-]+$")) {
                return false;
            }
        }
        return true;
    }
    public static String Login(String account, String password) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("http://jw.qdpec.edu.cn:8088/njwhd/login?userNo=" + account + "&pwd=" + password + "&encode=1")
                .method("POST", body)
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
                .addHeader("Cache-Control", "no-cache")
                .addHeader("Connection", "keep-alive")
                .addHeader("Origin", "http://jw.qdpec.edu.cn:8088")
                .addHeader("Pragma", "no-cache")
                .addHeader("Referer", "http://jw.qdpec.edu.cn:8088/")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/135.0.0.0 Safari/537.36 Edg/135.0.0.0")
                .addHeader("token", "null")
                .addHeader("Host", "jw.qdpec.edu.cn:8088")
                .build();
        try {
            Response response = client.newCall(request).execute();
            String string = Objects.requireNonNull(response.body()).string();
            JSONObject jsonObject = new JSONObject(string);
            JSONObject data = jsonObject.getJSONObject("data");
            return data.getString("token");
        } catch (Exception e) {
            gj.sc(e);
        }
        return null;
    }

    public static String encrypt(String input) throws Exception {
        String key = "qzkj1kjghd=876&*"; // 16字节密钥
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");

        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        // JS 中加密的是 JSON.stringify(input)，即："Muqing153@"
        byte[] encrypted = cipher.doFinal(("\"" + input + "\"").getBytes(StandardCharsets.UTF_8));
        String s = Base64.getEncoder().encodeToString(encrypted);
        return Base64.getEncoder().encodeToString(s.getBytes());
    }





}

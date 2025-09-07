package com.muqing.kctab.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;

import com.muqing.AppCompatActivity;
import com.muqing.gj;
import com.muqing.kctab.databinding.ActivityWebBinding;

import java.util.Map;
import java.util.Objects;

public class WebActivity extends AppCompatActivity<ActivityWebBinding> {

    @Override
    public void setOnApplyWindowInsetsListener(Insets systemBars, View v) {
//        super.setOnApplyWindowInsetsListener(systemBars, v);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();

        binding.swipe.setRefreshing(true);
        if (ContextCompat.checkSelfPermission(WebActivity.this, Manifest.permission.CHANGE_NETWORK_STATE)
                == PackageManager.PERMISSION_GRANTED) {
            // 有权限，执行绑定
            bindProcessToWifiNetwork(WebActivity.this);
        } else {
            // 没权限，可以提醒用户，或者跳过绑定
            gj.sc("没有权限");
        }
        // 刷新完成后，记得取消动画
        binding.swipe.setRefreshing(false);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        String url = intent.getStringExtra("url");
        // 启用 JavaScript
        WebSettings webSettings = binding.web.getSettings();
        webSettings.setJavaScriptEnabled(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(binding.web, true);
// 禁用缓存
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//        binding.web.clearCache(true);
//        binding.web.clearHistory();
        if (url != null) {
            binding.web.loadUrl(url);
        }
        // 设置 WebViewClient，以便在 WebView 内部处理链接而不是启动浏览器
        TypedArray array = getTheme().obtainStyledAttributes(new int[]{
                android.R.attr.colorBackground,
                android.R.attr.textColorPrimary,
        });
        int backgroundColor = array.getColor(0, 0xFFF5F5F5);
        String hexColor = String.format("#%06X", (0xFFFFFF & backgroundColor));
        Log.d("Background Color", "背景色 (Hex): " + hexColor);
        binding.web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 注入 JS 修改背景颜色为黑色
                binding.web.evaluateJavascript(
                        "document.body.style.backgroundColor = '" + hexColor + "';", null);
                view.evaluateJavascript(
                        "document.querySelectorAll('.login .log-input > div > input[data-v-4f122882]').forEach(el => {" +
                                "  el.style.backgroundColor = '" + hexColor + "';" +
                                "});", null);
                CookieManager cookieManager = CookieManager.getInstance();
                String cookies = cookieManager.getCookie(url);

                if (cookies != null) {
                    String[] cookieArray = cookies.split(";");
                    for (String cookie : cookieArray) {
                        Log.d("Cookie", "Cookie: " + cookie.trim());
                    }
                } else {
                    Log.d("Cookies", "没有 Cookies");
                }
                gj.sc(url);
                if (Objects.equals(url, "http://protal.qdpec.edu.cn:19580/favicon.ico")) {
                    view.loadUrl("https://www.muqingcandy.top/");

                }
            }

//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                gj.sc(request.getUrl().toString());
//                return super.shouldOverrideUrlLoading(view, request);
//            }

            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.equals("http://protal.qdpec.edu.cn:19580/favicon.ico")) {
                    view.post(() -> view.loadUrl("http://jw.qdpec.edu.cn:8088/njwhd/loginSso"));
                }
                // ✅ 打印请求头
                Map<String, String> headers = request.getRequestHeaders();
                for (Map.Entry<String, String> entry : headers.entrySet()) {
//                    gj.sc(entry.getKey() + ": " + entry.getValue());
                    if (entry.getKey().equals("Token") || entry.getKey().equals("token")) {
                        EndToken(entry.getValue());
                    }
                }

                // 不拦截，交回系统默认加载
                return super.shouldInterceptRequest(view, request);
            }


        });
        binding.swipe.setOnRefreshListener(() -> {
            binding.web.reload();
            if (ContextCompat.checkSelfPermission(WebActivity.this, Manifest.permission.CHANGE_NETWORK_STATE)
                    == PackageManager.PERMISSION_GRANTED) {
                // 有权限，执行绑定
                bindProcessToWifiNetwork(WebActivity.this);
            } else {
                // 没权限，可以提醒用户，或者跳过绑定
                gj.sc("没有权限");
            }
            binding.swipe.setRefreshing(false); // 结束刷新状态
        });
    }

    private void EndToken(String token) {
        if (token == null || token.equals("null") || token.isEmpty()) {
            return;
        }
        Intent resultIntent = new Intent();
        resultIntent.putExtra("token", token);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    public void bindProcessToWifiNetwork(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return;

        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI);

        connectivityManager.requestNetwork(builder.build(), new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                // 绑定当前进程到这个网络
                connectivityManager.bindProcessToNetwork(network);
            }
        });
    }

    @Override
    protected ActivityWebBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivityWebBinding.inflate(layoutInflater);
    }
}
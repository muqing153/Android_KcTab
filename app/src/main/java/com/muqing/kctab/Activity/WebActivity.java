package com.muqing.kctab.Activity;

import static com.muqing.kctab.MainActivity.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.muqing.AppCompatActivity;
import com.muqing.gj;
import com.muqing.kctab.R;
import com.muqing.kctab.databinding.ActivityWebBinding;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class WebActivity extends AppCompatActivity<ActivityWebBinding> {

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        String url = intent.getStringExtra("url");
        // 启用 JavaScript
        WebSettings webSettings = binding.web.getSettings();
        webSettings.setJavaScriptEnabled(true);
// 禁用缓存
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        if (url != null) {
            binding.web.loadUrl(url);
        }
//        binding.web.loadUrl("https://www.muqingcandy.top/");
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

            }

            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                // ✅ 打印请求头
                Map<String, String> headers = request.getRequestHeaders();
                String url = request.getUrl().toString();
//                gj.sc("请求 URL: " + url);
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

    @Override
    protected ActivityWebBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivityWebBinding.inflate(layoutInflater);
    }
}
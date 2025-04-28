package com.muqing.kctab.Activity;

import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;

import com.muqing.AppCompatActivity;
import com.muqing.gj;
import com.muqing.kctab.databinding.ActivityAboutSoftwareBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.noties.markwon.Markwon;
import io.noties.markwon.html.HtmlPlugin;
public class activity_about_software extends AppCompatActivity<ActivityAboutSoftwareBinding> {

    @Override
    public void setOnApplyWindowInsetsListener(Insets systemBars, View v) {
        v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            binding.text2.setText(String.format("%s Bate", versionName));
        } catch (PackageManager.NameNotFoundException e) {
            gj.sc(e.getMessage());
        }
        setBackToolsBar(binding.toolbar);
        binding.collapsingToolbar.setTitle("关于软件");
        AssetManager assets = getAssets();
        try {
            InputStream open = assets.open("about.md");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(open));
            StringBuilder stringBuilder = new StringBuilder();
            String ling;
            while ((ling = bufferedReader.readLine()) != null) {
                stringBuilder.append(ling).append("\n");
            }

            Markwon markwon = Markwon.builder(this)
//                    .usePlugin(ImagesPlugin.create())  // 图片支持
                    .usePlugin(HtmlPlugin.create())    // HTML支持
                    .build();
            markwon.setMarkdown(binding.text1, stringBuilder.toString());

            open.close();
            bufferedReader.close();

        } catch (IOException e) {
            binding.text1.setText(String.format("错误:%s", e));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected ActivityAboutSoftwareBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivityAboutSoftwareBinding.inflate(layoutInflater);
    }


}
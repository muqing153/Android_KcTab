package com.muqing.kctab.Activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.muqing.AppCompatActivity;
import com.muqing.kctab.Adapter.AutoTableAdapter;
import com.muqing.kctab.DataType.TableStyleData;
import com.muqing.kctab.databinding.ActivityAutoTableBinding;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AutoTableActivity extends AppCompatActivity<ActivityAutoTableBinding> {

    public List<TableStyleData> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        setBackToolsBar(binding.toolbar);
        try {
            // 读取 JSON
            TableStyleData tableStyleData = loadJSONFromAsset(this, "Untitled-1.json");
            dataList.add(tableStyleData);
            dataList.add(loadJSONFromAsset(this, "Untitled2.json"));
            // 应用属性
        } catch (Exception e) {
            e.printStackTrace();
        }
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerview.setAdapter(new AutoTableAdapter(this, dataList));
    }
    public static TableStyleData loadJSONFromAsset(Context context, String fileName) throws IOException {
        InputStream is = context.getAssets().open(fileName);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        String s = new String(buffer, StandardCharsets.UTF_8);
        return new Gson().fromJson(s, TableStyleData.class);
    }

    @Override
    protected ActivityAutoTableBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivityAutoTableBinding.inflate(layoutInflater);
    }
}
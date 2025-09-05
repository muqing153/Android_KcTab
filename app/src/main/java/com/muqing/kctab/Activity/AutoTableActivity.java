package com.muqing.kctab.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.google.gson.Gson;
import com.muqing.AppCompatActivity;
import com.muqing.kctab.Adapter.AutoTableAdapter;
import com.muqing.kctab.DataType.TableStyleData;
import com.muqing.kctab.databinding.ActivityAutoTableBinding;
import com.muqing.kctab.databinding.ItemTableAutoBinding;

import java.util.ArrayList;
import java.util.List;

public class AutoTableActivity extends AppCompatActivity<ActivityAutoTableBinding> {

    public List<TableStyleData> dataList = new ArrayList<>();
    TableStyleData tableStyleData;

    //    public class TableItemData {
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        setBackToolsBar(binding.toolbar);
        dataList.add(new TableStyleData());
        TableStyleData d = new TableStyleData();
        d.cardUseCompatPadding = false;
        d.cardElevation = 0;
        d.cardCornerRadius = 0;
        d.width = 80;
        d.height = 138;
        dataList.add(d);
        binding.recyclerview.setLayoutManager(new GridLayoutManager(this, 2));
        binding.recyclerview.setAdapter(new AutoTableAdapter(this, dataList));
    }

    @Override
    protected ActivityAutoTableBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivityAutoTableBinding.inflate(layoutInflater);
    }
}
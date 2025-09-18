package com.muqing.kctab.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.muqing.BaseAdapter;
import com.muqing.gj;
import com.muqing.kctab.databinding.ItemTableHBinding;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class TableHAdapter extends BaseAdapter<ItemTableHBinding, String> {
    public TableHAdapter(Context context, List<String> dataList) {
        super(context, dataList);
    }

    @Override
    protected ItemTableHBinding getViewBindingObject(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemTableHBinding.inflate(inflater, parent, false);
    }

    @Override
    protected void onBindView(String data, ItemTableHBinding viewBinding, ViewHolder<ItemTableHBinding> viewHolder, int position) {
        if (position == 0) {
            int width = viewBinding.getRoot().getLayoutParams().width;
            viewBinding.getRoot().getLayoutParams().width = width / 2 + gj.dp2px(context, 10);
        }
        viewBinding.titleRi.setText(data);
    }
}
package com.muqing.kctab.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.muqing.BaseAdapter;
import com.muqing.gj;
import com.muqing.kctab.Curriculum;
import com.muqing.kctab.MainActivity;
import com.muqing.kctab.databinding.ZhouItemBinding;

import java.util.List;
import java.util.stream.Stream;

public class ZhouAdapter extends BaseAdapter<ZhouItemBinding, String> {
    final int ColorThis, ColorWhen;

    public ZhouAdapter(Context context, List<String> dataList) {
        super(context, dataList);
        ColorThis = gj.getThemeColor(context, com.google.android.material.R.attr.colorSurfaceContainerLow);
        ColorWhen = gj.getThemeColor(context, com.google.android.material.R.attr.colorSurface);
    }

    @Override
    protected ZhouItemBinding getViewBindingObject(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ZhouItemBinding.inflate(inflater, parent, false);
    }

    public boolean gaoliang = true;
    public int week = 0;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onBindView(String data, ZhouItemBinding viewBinding, ViewHolder<ZhouItemBinding> viewHolder, int position) {
        viewBinding.title.setText(data);
        if (gaoliang && Integer.parseInt(data) == week) {
            viewBinding.getRoot().setEnabled(false);
            viewBinding.getRoot().setCardBackgroundColor(ColorWhen);
        } else {
            viewBinding.getRoot().setCardBackgroundColor(ColorThis);
            viewBinding.getRoot().setEnabled(true);
        }
        viewBinding.getRoot().setOnClickListener(view -> {
            week = position + 1;
            onclick(position);
            notifyDataSetChanged();
        });
    }

    public void onclick(int position) {
    }
}

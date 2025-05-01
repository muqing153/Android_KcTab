package com.muqing.kctab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.muqing.Dialog.BottomSheetDialog;
import com.muqing.gj;
import com.muqing.kctab.Adapter.ZhouAdapter;
import com.muqing.kctab.databinding.ZhouDialogBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class zhouDialog extends BottomSheetDialog {
    public ZhouAdapter zhouAdapter;
    public ZhouDialogBinding binding;

    @SuppressLint("NotifyDataSetChanged")
    public zhouDialog(@NonNull Context context) {
        super(context);
        binding = ZhouDialogBinding.inflate(LayoutInflater.from(context));
        setContentView(binding.getRoot());
// 设置最大高度 60% 屏幕高度
        ViewGroup.LayoutParams params = binding.getRoot().getLayoutParams();
        params.height = (int) (Resources.getSystem().getDisplayMetrics().heightPixels * 0.5);
        binding.getRoot().setLayoutParams(params);


        LoadAdapter();
        int itemWidthDp = 100;
        float density = context.getResources().getDisplayMetrics().density;
        int itemWidthPx = (int) (itemWidthDp * density + 0.5f);

//        int screenWidthPx = context.getResources().getDisplayMetrics().widthPixels;
        binding.getRoot().post(() -> {
            int width = binding.getRoot().getWidth();
            int spanCount = Math.max(5, width / itemWidthPx);
            binding.recyclerview.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
            binding.recyclerview.setAdapter(zhouAdapter);
            binding.fh.setOnClickListener(view -> {
                zhouAdapter.week = MainActivity.benzhou;
                zhouAdapter.onclick(MainActivity.benzhou - 1);
                zhouAdapter.notifyDataSetChanged();
            });
        });
        show();
    }

    public void LoadAdapter() {
        ArrayList<String> objects = new ArrayList<>();
        for (int i = 0; i < MainActivity.TabList.size(); i++) {
            objects.add(String.valueOf(i + 1));
        }
        zhouAdapter = new ZhouAdapter(this.getContext(), objects) {
            @Override
            public void onclick(int position) {
                super.onclick(position);
                click(position);
            }
        };
    }

    public abstract void click(int position);

}

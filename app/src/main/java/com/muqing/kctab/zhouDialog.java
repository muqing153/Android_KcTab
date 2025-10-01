package com.muqing.kctab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import com.muqing.Dialog.BottomSheetDialog;
import com.muqing.ViewUI.BaseBottomDialog;
import com.muqing.gj;
import com.muqing.kctab.Adapter.ZhouAdapter;
import com.muqing.kctab.databinding.ZhouDialogBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class zhouDialog extends BaseBottomDialog<ZhouDialogBinding> {
    @SuppressLint("NotifyDataSetChanged")
    public zhouDialog(@NonNull Context context, int type) {
        super(context);
        ViewGroup.LayoutParams params = binding.getRoot().getLayoutParams();
        params.height = (int) (Resources.getSystem().getDisplayMetrics().heightPixels * 0.5);
        binding.getRoot().setLayoutParams(params);
        binding.slider.addOnChangeListener((slider, value, fromUser) -> binding.starttext.setText(String.valueOf(Math.round(value))));
        int length = MainActivity.TabList.size();
        binding.starttext.setText(String.valueOf(type));
        binding.endtext.setText(String.valueOf(length));
        binding.fh.setOnClickListener(view -> binding.slider.setValue(MainActivity.benzhou));
        show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        //获取当前slider 的value值
        int value = (int) binding.slider.getValue() - 1;
        click(value);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected ZhouDialogBinding getViewBindingObject(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ZhouDialogBinding.inflate(inflater, parent, false);
    }

    public abstract void click(int position);

}

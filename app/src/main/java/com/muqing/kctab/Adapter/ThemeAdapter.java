package com.muqing.kctab.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import com.muqing.BaseAdapter;
import com.muqing.kctab.databinding.ItemThemeBinding;

import java.util.List;

public abstract class ThemeAdapter extends BaseAdapter<com.muqing.kctab.databinding.ItemThemeBinding, Drawable> {
    public ThemeAdapter(Context context, List<Drawable> dataList) {
        super(context, dataList);
    }

    @Override
    protected ItemThemeBinding getViewBindingObject(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemThemeBinding.inflate(inflater, parent, false);
    }


    public int mode = 0;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onBindView(Drawable data, ItemThemeBinding viewBinding, ViewHolder<ItemThemeBinding> viewHolder, int position) {
        viewBinding.imageView.setImageDrawable(data);
        if (position == mode) {
            viewBinding.getRoot().setScaleX(1.2f);
            viewBinding.getRoot().setScaleY(1.2f);
        } else {
            viewBinding.getRoot().setScaleX(1f);
            viewBinding.getRoot().setScaleY(1f);
        }
        viewBinding.getRoot().setOnClickListener(view -> {
            if (position == mode) {
                return;
            }
            mode = position;
            SharedPreferences sharedPreferences = context.getSharedPreferences("theme", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("mods", position);
            editor.apply();
            notifyDataSetChanged();
            OnClick(position);
        });
    }

    public abstract void OnClick(int position);

}

package com.muqing.kctab.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;

import java.util.ArrayList;
import java.util.List;

import com.muqing.gj;
import com.muqing.kctab.fragment.kecheng;

public class KeChengPageAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter {

    public final List<com.muqing.kctab.fragment.kecheng> data = new ArrayList<>();

    public KeChengPageAdapter(@NonNull FragmentManager fa, @NonNull Lifecycle lifecycle) {
        super(fa, lifecycle);
    }

    public void addPage(kecheng kecheng) {
        data.add(kecheng);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        try {
            return data.get(position);
        } catch (Exception e) {
            gj.sc(e);
        }
        return new Fragment();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}

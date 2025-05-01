package com.muqing.kctab.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

import com.muqing.kctab.fragment.kecheng;
public class KeChengPageAdapter extends androidx.viewpager2.adapter.FragmentStateAdapter{

    public final List<com.muqing.kctab.fragment.kecheng> data = new ArrayList<>();
    public KeChengPageAdapter(@NonNull FragmentActivity fa) {
        super(fa);
    }


    public void addPage(kecheng kecheng) {
        data.add(kecheng);
    }
    public void removePage(int position) {
        if (position >= 0 && position < data.size()) {
            data.remove(position);
            notifyItemRemoved(position);
        }
    }
    public void removePage(kecheng kecheng) {
        data.remove(kecheng);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return data.get(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}

package com.muqing.kctab.fragment;

import static com.muqing.kctab.MainActivity.schedule;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.muqing.Fragment;
import com.muqing.gj;
import com.muqing.kctab.Adapter.GridAdapter;
import com.muqing.kctab.Curriculum;
import com.muqing.kctab.KcLei;
import com.muqing.kctab.MainActivity;
import com.muqing.kctab.databinding.FragmentKebiaoBinding;

import java.util.ArrayList;
import java.util.List;

public class kecheng extends Fragment<FragmentKebiaoBinding> {
    public Curriculum curriculum;
    public GridAdapter adapter;

    public kecheng(Curriculum curriculum) {
        this.curriculum = curriculum;

    }

    public kecheng() {
    }


    @Override
    protected FragmentKebiaoBinding getViewBindingObject(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentKebiaoBinding.inflate(inflater, container, false);
    }

    @Override
    public void setUI(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        GridLayoutManager layoutManager = new GridLayoutManager(this.getContext(), 8); // 列
        binding.recyclerview.setLayoutManager(layoutManager);
        if (curriculum != null && curriculum.data != null) {
            adapter = new GridAdapter(this.getContext(), GetKcLei(curriculum));
            adapter.zhou = curriculum.data.get(0).week;
            binding.recyclerview.setAdapter(adapter);
        }


    }

    private List<KcLei> GetKcLei(Curriculum curriculum) {
        List<KcLei> list = new ArrayList<>();
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 8; col++) {
                if (row == 0 && col == 0) {
                    list.add(new KcLei("节/日期"));
                    continue;
                }
                list.add(new KcLei("R" + (row + 1) + " C" + (col + 1)));
            }
        }
        Curriculum.DataItem dataItem = curriculum.data.get(0);
        for (int i = 0; i < dataItem.date.size(); i++) {
            Curriculum.DateInfo dateInfo = dataItem.date.get(i);
            list.set(i + 1, new KcLei(String.format("%s(%s)", dateInfo.xqmc, dateInfo.rq)));
        }
        for (int i = 0, j = 8; i < schedule.length; i++, j += 8) {
            KcLei kcLei = new KcLei(schedule[i].session);
            kcLei.message = schedule[i].time1 + "\n" + schedule[i].time2;
            list.set(j, kcLei);
        }
        // 1. 遍历每个节次，创建行数据
        String[] ric = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
        int ric_i = 0;
        for (int i = 0, j = 9; i < 5; i++) {
            for (int k = 0; k < 7; k++, j++) {
                int finalRic_i = ric_i;
                int finalI = k + 1;
                Curriculum.Course result = dataItem.courses.stream().filter(c -> c.classTime.endsWith(String.format("%s%s", ric[finalRic_i], ric[finalRic_i + 1])) && c.weekDay == finalI).findFirst().orElse(null);
//                gj.sc(result);
                if (result == null) {
                    result = new Curriculum.Course();
                    result.startTime = schedule[i].time1.split("-")[0];
                    result.endTime = schedule[i].time2.split("-")[1];
                    result.weekDay = k + 1;
                }
                KcLei kcLei = new KcLei(result);
                list.set(j, kcLei);
            }
            j++;
            ric_i += 2;
        }
        return list;
    }

}

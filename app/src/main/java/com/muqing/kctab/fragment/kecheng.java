package com.muqing.kctab.fragment;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.gson.Gson;
import com.muqing.Fragment;
import com.muqing.gj;
import com.muqing.kctab.Adapter.GridAdapter;
import com.muqing.kctab.Curriculum;
import com.muqing.kctab.MainActivity;
import com.muqing.kctab.databinding.FragmentKebiaoBinding;
import com.muqing.wj;

import java.util.ArrayList;
import java.util.List;

public class kecheng extends Fragment<FragmentKebiaoBinding> {
    public GridAdapter adapter;
    private String FilePath;
    private int zhou = 0;

    public static kecheng newInstance(String filePath, int zhou) {
        kecheng fragment = new kecheng();
        Bundle args = new Bundle();
        args.putString("filePath", filePath);
        args.putInt("zhou", zhou);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            FilePath = getArguments().getString("filePath");
            zhou = getArguments().getInt("zhou");
        }
    }


    public kecheng() {
    }


    @Override
    protected FragmentKebiaoBinding getViewBindingObject(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentKebiaoBinding.inflate(inflater, container, false);
    }

    public static final MainActivity.ScheduleItem[] schedule = {new MainActivity.ScheduleItem("1.2", "08:20-09:05", "09:15-10:00"),
            new MainActivity.ScheduleItem("3.4", "10:10-11:40", "10:30-12:00"),
            new MainActivity.ScheduleItem("5.6", "13:30-14:15", "14:25-15:10"),
            new MainActivity.ScheduleItem("7.8", "15:20-16:05", "16:15-17:00"),
            new MainActivity.ScheduleItem("9.10", "18:30-19:15", "19:25-20:10")};


    public Curriculum curriculum;
    @Override
    public void setUI(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        gj.sc("启动Fragment UI " + binding);
        GridLayoutManager layoutManager = new GridLayoutManager(this.getContext(), 8); // 列
        binding.recyclerview.setLayoutManager(layoutManager);
        if (FilePath != null) {
//            gj.sc("启动Fragment UI 读取文件");
            String dqwb = wj.dqwb(FilePath, "");
            curriculum = new Gson().fromJson(dqwb, Curriculum.class);
            curriculum.data.get(0).week = zhou;
        }

        if (curriculum != null && curriculum.data != null) {
//            gj.sc("启动Fragment UI 初始化表内容");
            adapter = new GridAdapter(this.getContext(), GetKcLei(curriculum)) {
                @Override
                public void ShowLongDelete(Curriculum.Course course) {
                    gj.sc("成功删除了一个数据com：" + course);
                    curriculum.data.get(0).courses.remove(course);
                    wj.xrwb(FilePath, new Gson().toJson(curriculum));
                }

                @Override
                public void ShowLongAdd(Curriculum.Course obj) {
                    curriculum.data.get(0).courses = GetListKc(dataList);
                    wj.xrwb(FilePath, new Gson().toJson(curriculum));
                }
            };
            adapter.zhou = curriculum.data.get(0).week;
            binding.recyclerview.setAdapter(adapter);
            binding.recyclerview.post(() -> {
                adapter.Load(binding.recyclerview);
                binding.horizontal.scrollTo(adapter.ItemXY[0], adapter.ItemXY[1]);
            });
        }
        //获取屏幕高度
//        int heightPixels = Resources.getSystem().getDisplayMetrics().heightPixels;
//        binding.horizontal.getLayoutParams().height = heightPixels;
//        binding.horizontal.buildLayer();
//        binding.horizontal.setBackgroundColor(Color.GRAY);
    }

    private List<Curriculum.Course> GetListKc(List<Curriculum.Course> dataList) {
        List<Curriculum.Course> filtered = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            if (i > 8 && i % 8 != 0) {
                Curriculum.Course course = dataList.get(i);
                if (course.classTime == null) {
                    continue;
                }
                filtered.add(course);
            }
        }
        return filtered;

    }

    private List<Curriculum.Course> GetKcLei(Curriculum curriculum) {
        List<Curriculum.Course> list = new ArrayList<>();
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 8; col++) {
                if (row == 0 && col == 0) {
                    Curriculum.Course course = new Curriculum.Course();
                    course.courseName = "节/日";
                    list.add(course);
                    continue;
                }
                Curriculum.Course course = new Curriculum.Course();
                list.add(course);
            }
        }
        Curriculum.DataItem dataItem = curriculum.data.get(0);
        for (int i = 0; i < dataItem.date.size(); i++) {
            Curriculum.DateInfo dateInfo = dataItem.date.get(i);
            Curriculum.Course course = new Curriculum.Course();
            course.courseName = String.format("%s(%s)", dateInfo.xqmc, dateInfo.rq);
            list.set(i + 1, course);
        }
        for (int i = 0, j = 8; i < schedule.length; i++, j += 8) {
            Curriculum.Course kcLei = new Curriculum.Course();
            kcLei.courseName = schedule[i].session;
            kcLei.classroomName = schedule[i].time1 + "\n" + schedule[i].time2;
//            kcLei.classroomName = schedule[i].time1.split("-")[0] + "\n" + schedule[i].time2.split("-")[1];
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
//                    result.startTime = schedule[i].time1.split("-")[0];
//                    result.endTime = schedule[i].time2.split("-")[1];
//                    result.weekDay = k + 1;
                }
                list.set(j, result);
            }
            j++;
            ric_i += 2;
        }
        return list;
    }
}
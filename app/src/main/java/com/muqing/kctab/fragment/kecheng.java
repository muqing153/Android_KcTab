package com.muqing.kctab.fragment;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.gson.Gson;
import com.muqing.Fragment;
import com.muqing.gj;
import com.muqing.kctab.Adapter.GridAdapter;
import com.muqing.kctab.Curriculum;
import com.muqing.kctab.MainActivity;
import com.muqing.kctab.R;
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

    public void toolbar_time() {
        if (!isAdded()) return;
        gj.sc("启动Fragment UI 读取时间");
        TextView viewById = requireActivity().findViewById(R.id.toolbar_time);
        viewById.setText(curriculum.data.get(0).date.get(0).mxrq);
    }

    @Override
    public void setUI(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
                public void ShowLongDelete(List<Curriculum.Course> course) {
                    gj.sc("成功删除了一个数据com：" + course);
                    curriculum.data.get(0).courses.remove(course);
                    wj.xrwb(FilePath, new Gson().toJson(curriculum));
                }

                @Override
                public boolean ShowLongAdd(List<Curriculum.Course> obj) {
//                    curriculum.data.get(0).courses = GetListKc(dataList);
                    for (Curriculum.Course course :
                            obj) {
//                        gj.sc("成功添加了一个数据com：" + course);
                        curriculum.data.get(0).courses.add(course);
                    }
                    return wj.xrwb(FilePath, new Gson().toJson(curriculum));
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

    private List<Curriculum.Course> GetListKc(List<List<Curriculum.Course>> dataList) {
        List<Curriculum.Course> filtered = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            if (i > 8 && i % 8 != 0) {
                for (Curriculum.Course course : dataList.get(i)) {
                    if (course.classTime == null) {
                        continue;
                    }
                    filtered.add(course);
                }
            }
        }
        return filtered;

    }

    private List<List<Curriculum.Course>> GetKcLei(Curriculum curriculum) {
        List<List<Curriculum.Course>> list = new ArrayList<>();
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 8; col++) {
                List<Curriculum.Course> a = new ArrayList<>();
                if (row > 0 && col > 0) {
                    Curriculum.Course course = new Curriculum.Course();
                    course.startTime = schedule[row - 1].time1.split("-")[0];
                    course.endTime = schedule[row - 1].time2.split("-")[1];
                    a.add(course);
                }
                list.add(a);
            }
        }
//初始化头部
        Curriculum.DataItem dataItem = curriculum.data.get(0);
        {
            List<Curriculum.Course> arrayList = new ArrayList<>();
            Curriculum.Course course = new Curriculum.Course();
            course.courseName = "节/日";
            arrayList.add(course);
            list.set(0, arrayList);
        }
        for (int i = 0; i < dataItem.date.size(); i++) {
            List<Curriculum.Course> arrayList = new ArrayList<>();
            Curriculum.DateInfo dateInfo = dataItem.date.get(i);
            Curriculum.Course course = new Curriculum.Course();
            course.courseName = String.format("%s(%s)", dateInfo.xqmc, dateInfo.rq);
            arrayList.add(course);
            list.set(i + 1, arrayList);
        }
        // 创建节次数据
        for (int i = 0, j = 8; i < schedule.length; i++, j += 8) {
            List<Curriculum.Course> arrayList = new ArrayList<>();
            Curriculum.Course kcLei = new Curriculum.Course();
            kcLei.courseName = schedule[i].session;
            kcLei.classroomName = schedule[i].time1 + "\n" + schedule[i].time2;
//            kcLei.classroomName = schedule[i].time1.split("-")[0] + "\n" + schedule[i].time2.split("-")[1];
            arrayList.add(kcLei);
            list.set(j, arrayList);
        }

//        48
        // 1. 遍历每个节次，创建行数据
        for (Curriculum.Course adapter : dataItem.courses) {
            String classTime = adapter.classTime;
            // 提取第一个数字字符
            String part1 = classTime.substring(0, 1); // "1"
            // 提取第二部分
            String part2 = classTime.substring(1, 3); // "03"
            // 提取第三部分
            String part3 = classTime.substring(3, 5); // "04"
            // 提取第四部分
            String part4 = classTime.substring(1, 5);
            // 转成整数
            int num1 = Integer.parseInt(part1); // 1
            int i = GetInt(part4);
            int p = num1 + 8 * i;
//            System.out.println(p);
            List<Curriculum.Course> courses = list.get(p);
            if (courses.get(0).courseName == null) {
                courses.clear();
            }
            courses.add(adapter);
            list.set(p, courses);
        }
        return list;
    }

    private static final String[] classTime = {
            "0102", "0304", "0506", "0708", "0910"
    };

    static int GetInt(String str) {
        for (int i = 0; i < classTime.length; i++) {
            if (classTime[i].equals(str)) {
                return i + 1;
            }
        }
        return 0;
    }
}
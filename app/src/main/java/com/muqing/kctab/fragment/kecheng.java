package com.muqing.kctab.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.muqing.Fragment;
import com.muqing.gj;
import com.muqing.kctab.Adapter.AutoTableAdapter;
import com.muqing.kctab.Adapter.GridAdapter;
import com.muqing.kctab.Adapter.TableTimeAdapter;
import com.muqing.kctab.Curriculum;
import com.muqing.kctab.DataType.TableTimeData;
import com.muqing.kctab.MainActivity;
import com.muqing.kctab.R;
import com.muqing.kctab.databinding.FragmentKebiaoBinding;
import com.muqing.kctab.databinding.GridItemBinding;
import com.muqing.kctab.databinding.ItemTableHBinding;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class kecheng extends Fragment<FragmentKebiaoBinding> {
    public int ValueZhou = 1;

    public static kecheng newInstance(int zhou) {
        kecheng fragment = new kecheng();
        Bundle args = new Bundle();
        args.putInt("zhou", zhou);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ValueZhou = getArguments().getInt("zhou");

        }
    }

    @Override
    protected FragmentKebiaoBinding getViewBindingObject(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentKebiaoBinding.inflate(inflater, container, false);
    }

    public Handler handler;
    TableTimeAdapter timeAdapter;

    @Override
    public void onStart() {
        super.onStart();
        if (isAdded() && isVisible()) {
            binding.recyclerviewH.removeAllViews();
            binding.tablelayout.removeAllViews();
            recyclerViews.clear();
            recyclerHViews.clear();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
            layoutParams.weight = 1;
            {
                ItemTableHBinding tableHBinding = ItemTableHBinding.inflate(getLayoutInflater(), binding.recyclerviewH, false);
                LocalDate now = LocalDate.now();
                if (MainActivity.TableStyle != null) {
                    AutoTableAdapter.bindView(MainActivity.TableStyle, tableHBinding.getRoot(), true);
                }
                tableHBinding.tableHtitle.setText(String.valueOf(now.getDayOfMonth()));
                tableHBinding.titleRi2.setText("Day");
                binding.recyclerviewH.addView(tableHBinding.getRoot(),
                        new LinearLayout.LayoutParams(gj.dp2px(requireActivity(), 35), ViewGroup.LayoutParams.MATCH_PARENT));
            }
            String[] weekDates = Curriculum.getWeekDates("2025-09-01", ValueZhou);
            for (int i = 0; i < 7; i++) {
                RecyclerView recyclerView = new RecyclerView(requireActivity());
                recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false));
//            recyclerView 禁止滚动
                recyclerView.setNestedScrollingEnabled(false);
                ItemTableHBinding tableHBinding = ItemTableHBinding.inflate(getLayoutInflater(), binding.recyclerviewH, false);
                if (MainActivity.TableStyle != null) {
                    AutoTableAdapter.bindView(MainActivity.TableStyle, tableHBinding.getRoot(), true);
                }
                tableHBinding.tableHtitle.setText(HList[i]);
                tableHBinding.titleRi2.setText(weekDates[i].split("-")[2]);
                recyclerHViews.add(tableHBinding);
                recyclerViews.add(recyclerView);
                binding.recyclerviewH.addView(tableHBinding.getRoot(), layoutParams);
                binding.tablelayout.addView(recyclerView, layoutParams);
            }
            Load();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void Load() {
        if (handler == null && ValueZhou == MainActivity.benzhou) {
            handler = new Handler(Looper.getMainLooper());
        }
        GetKcLei(TableList, MainActivity.curriculum);
        for (int i = 0; i < TableList.size(); i++) {
            RecyclerView recyclerView = recyclerViews.get(i);
            if (recyclerView != null) {
                GridAdapter adapter = new GridAdapter(getContext(), TableList.get(i)) {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public boolean update(List<Curriculum.Course> list, Curriculum.Course course, int position) {
                        return false;
                    }
                };
                adapter.showInfo = true;
                recyclerView.setAdapter(adapter);
            }
        }
        if (handler != null) {
            handler.post(this::LoadHander);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            NextItemBinding = null;
        }
    }

    private GridItemBinding NextItemBinding;

    private void LoadHander() {
//        gj.sc("开始加载课程表");
        if (isAdded() && isVisible()) {
            LocalDate now = LocalDate.now();
            int Day = now.getDayOfWeek().getValue();
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm", Locale.CHINA));
            for (int i = Day - 1; i < 7; i++) {
                List<List<Curriculum.Course>> lists = TableList.get(i);
                for (int y = 0; y < lists.size(); y++) {
                    Curriculum.Course data = lists.get(y).get(0);
                    if (TextUtils.isEmpty(data.courseName)) {
                        continue;
                    }
                    String weekNoteDetail = data.weekNoteDetail;
                    int[] section = getSection(weekNoteDetail);
                    String startTime = TableTimeData.tableTimeData[section[0] - 1].starttime;
                    String endTime = TableTimeData.tableTimeData[section[section.length - 1] - 1].endtime;
                    if ((time.compareTo(startTime) >= 0 && time.compareTo(endTime) <= 0 || startTime.compareTo(time) > 0)
                           ) {
                        View viewByPosition = Objects.requireNonNull(recyclerViews.get(i).getLayoutManager()).findViewByPosition(y);
                        if (viewByPosition != null) {
                            if (NextItemBinding == null) {
                                NextItemBinding = GridItemBinding.bind(viewByPosition);
                                NextItemBinding.getRoot().setStrokeColor(MainActivity.ThisColor);
                                NextItemBinding.getRoot().setStrokeWidth(3);
                            } else if (NextItemBinding.getRoot() != viewByPosition) {
                                NextItemBinding.getRoot().setStrokeColor(Color.parseColor("#80646464"));
                                NextItemBinding.getRoot().setStrokeWidth(1);
                                NextItemBinding = null;
                            }
                            break;
                        }
                    }
                }
                if (NextItemBinding != null) {
                    break;
                }
                time = "08:00";
            }
        }
        handler.postDelayed(this::LoadHander, 1000);
    }

    public Curriculum curriculum;


    List<RecyclerView> recyclerViews = new ArrayList<>();
    List<ItemTableHBinding> recyclerHViews = new ArrayList<>();

    private final String[] HList = new String[]{"一", "二", "三", "四", "五", "六", "日"};

    @SuppressLint("SetTextI18n")
    @Override
    public void setUI(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        数组转List
        binding.recyclerviewTime.setAdapter(timeAdapter = new TableTimeAdapter(this.requireActivity(), Arrays.asList(TableTimeData.tableTimeData)));

    }

    List<List<List<Curriculum.Course>>> TableList = new ArrayList<>();

    public static List<Curriculum.Course> newCourse(int L, int H) {
        List<Curriculum.Course> a = new ArrayList<>();
        Curriculum.Course course = new Curriculum.Course();
        course.startTime = TableTimeData.tableTimeData[H].starttime;
        course.endTime = TableTimeData.tableTimeData[H].endtime;
        course.weekDay = L + 1;
//        course.height = 1;
        course.classTime = String.format(Locale.CANADA, "%d%02d", L + 1, H + 1);
        a.add(course);
        return a;
    }

    public static List<List<List<Curriculum.Course>>> GetKcLei(List<List<List<Curriculum.Course>>> TableList, Curriculum curriculum) {
        TableList.clear();
        for (int L = 0; L < 7; L++) {
            TableList.add(new ArrayList<>());
            for (int H = 0; H < TableTimeData.tableTimeData.length; H++) {
                List<Curriculum.Course> a = newCourse(L, H);
                TableList.get(L).add(a);
            }
        }
        if (curriculum != null) {
            // 1. 遍历每个节次，创建行数据
            for (Curriculum.Course course : curriculum.course) {
                List<Curriculum.classWeekDetails> details = course.classWeekDetails;
                if (details != null) {
                    for (Curriculum.classWeekDetails section : details) {
                        // 判断当前周是否在范围内
//                        gj.sc(section.weeks);
                        if (isWeekInRange(section.weeks, MainActivity.ValueZhou)) {
                            List<Curriculum.Weekday> weekdays = section.weekdays;
                            if (weekdays != null) {
                                for (Curriculum.Weekday weekday : weekdays) {
                                    if (weekday.jie != null) {
                                        String[] jies = weekday.jie.split(",");
                                        if (jies.length > 0) {
                                            int first = Integer.parseInt(jies[0].substring(0, 1)); // 1
                                            int lastTwo = Integer.parseInt(jies[0].substring(1));
                                            // 深拷贝课程对象
                                            Curriculum.Course c = new Gson().fromJson(new Gson().toJson(course), Curriculum.Course.class);
                                            c.weekNoteDetail = weekday.jie;
                                            c.classroomName = weekday.classroomName;
                                            List<Curriculum.Course> d = TableList.get(first - 1).get(lastTwo - 1);
                                            if (TextUtils.isEmpty(d.get(0).courseName)) {
                                                d.clear();
                                                d.add(c);
                                            } else {
                                                d.add(c);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
//                for (List<List<Curriculum.Course>> list : TableList) {
//                    for (int i = 0; i < list.size(); i++) {
//                        Curriculum.Course s = list.get(i).get(0);
//                        for (int j = 1; j < s.height; j++) {
//                            list.remove(i + 1);
//                        }
//                    }
//                }
            }


            for (int i = 0; i < TableList.size(); i++) {
                List<List<Curriculum.Course>> row = TableList.get(i);
                for (int index = 0; index < row.size(); index++) {
                    try {
                        List<Curriculum.Course> courseList = row.get(index);
                        if (courseList.isEmpty()) continue;

                        Curriculum.Course firstCourse = courseList.get(0);
                        if (firstCourse.weekNoteDetail == null) continue;

                        int[] item = getSection(firstCourse.weekNoteDetail);
                        if (item.length - 1 > 0) {
                            // 安全删除，防止越界
                            int start = Math.min(index + 1, row.size());
                            int end = Math.min(start + (item.length - 1), row.size());
                            if (start < end) {
                                row.subList(start, end).clear();
                            }
                        }
                    } catch (Exception e) {
                        gj.error(e);
                    }
                }
            }

        }

//        gj.sc(new Gson().toJson(TableList));
        return TableList;

    }

    public static int[] getSection(String time) {
        if (time == null || time.isEmpty()) {
            return new int[1];
        }
        String[] parts = time.split(",");
        int[] section = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            String c = parts[i];
            section[i] = Integer.parseInt(c.substring(1, 3));
        }
        return section;
    }


    public static boolean isWeekInRange(String weeks, int zhou) {
        if (weeks == null || weeks.isEmpty()) {
            return false;
        }
        String[] parts = weeks.split(",");
        for (String part : parts) {
            part = part.trim();
            if (part.contains("-")) {
                String[] range = part.split("-");
                try {
                    int start = Integer.parseInt(range[0].trim());
                    int end = Integer.parseInt(range[1].trim());
                    if (zhou >= start && zhou <= end) {
                        return true;
                    }
                } catch (NumberFormatException e) {
                    // 如果格式错误直接忽略当前项
                }
            } else {
                try {
                    int num = Integer.parseInt(part);
                    if (num == zhou) {
                        return true;
                    }
                } catch (NumberFormatException e) {
                    // 忽略非数字
                }
            }
        }

        return false;
    }
}
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
import com.muqing.wj;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class kecheng extends Fragment<FragmentKebiaoBinding> {
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
        if (FilePath != null && curriculum == null) {
            String dqwb = wj.dqwb(FilePath, "");
            curriculum = new Gson().fromJson(dqwb, Curriculum.class);
            curriculum.data.get(0).week = zhou;
        }
        if (handler == null && zhou == MainActivity.benzhou) {
            handler = new Handler(Looper.getMainLooper());
        }
        toolbar_time();
        if (curriculum != null && curriculum.data != null) {
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences("kebiao", Context.MODE_PRIVATE);
            if (timeAdapter != null) {
                timeAdapter.showJie = sharedPreferences.getBoolean("showJie", true);
                timeAdapter.notifyDataSetChanged();
            }
            boolean showInfo = sharedPreferences.getBoolean("showInfo", true);
            for (int i = 0; i < curriculum.data.get(0).date.size(); i++) {
                recyclerHViews.get(i).titleRi2.setText(curriculum.data.get(0).date.get(i).rq);
                recyclerHViews.get(i).titleRi2.setVisibility(View.VISIBLE);
            }
            GetKcLei(TableList, curriculum);
            for (int i = 0; i < TableList.size(); i++) {
                RecyclerView recyclerView = recyclerViews.get(i);
                if (recyclerView != null) {
                    GridAdapter adapter = new GridAdapter(this.getContext(), TableList.get(i)) {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public boolean update(List<Curriculum.Course> list, Curriculum.Course course, int position) {
                            List<Curriculum.Course> coursesData = curriculum.data.get(0).courses;
                            coursesData.clear();
                            for (List<List<Curriculum.Course>> listList : TableList) {
                                for (List<Curriculum.Course> courses : listList) {
                                    for (Curriculum.Course s : courses) {
                                        if (IsCourse(s)) {
                                            coursesData.add(s);
                                        }
                                    }
                                }
                            }
                            notifyDataSetChanged();
                            wj.xrwb(FilePath, new Gson().toJson(curriculum));
                            return false;
                        }
                    };
                    adapter.showInfo = showInfo;
                    recyclerView.setAdapter(adapter);
                }
            }
            if (handler != null) {
                handler.post(this::LoadHander);
            }
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
                    if (IsCourse(data)) {
                        if ((time.compareTo(data.startTime) >= 0 && time.compareTo(data.endTime) <= 0 || data.startTime.compareTo(time) > 0)
                                && Objects.equals(data.weekDay, i + 1)) {
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

    public void toolbar_time() {
        if (isAdded() && isVisible() && curriculum != null) {
            TextView viewById = requireActivity().findViewById(R.id.toolbar_time);
            viewById.setText(curriculum.data.get(0).date.get(0).mxrq);
        }
    }

    List<RecyclerView> recyclerViews = new ArrayList<>();
    List<ItemTableHBinding> recyclerHViews = new ArrayList<>();

    private final String[] HList = new String[]{"一", "二", "三", "四", "五", "六", "日"};

    @SuppressLint("SetTextI18n")
    @Override
    public void setUI(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        数组转List
        binding.recyclerviewTime.setAdapter(timeAdapter = new TableTimeAdapter(this.requireActivity(), Arrays.asList(TableTimeData.tableTimeData)));
        //获取今天几号
//        Load();
    }

    List<List<List<Curriculum.Course>>> TableList = new ArrayList<>();

    public static List<Curriculum.Course> newCourse(int L, int H) {
        List<Curriculum.Course> a = new ArrayList<>();
        Curriculum.Course course = new Curriculum.Course();
        course.startTime = TableTimeData.tableTimeData[H].starttime;
        course.endTime = TableTimeData.tableTimeData[H].endtime;
        course.weekDay = L + 1;
        course.height = 1;
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
        Curriculum.DataItem dataItem = curriculum.data.get(0);
        dataItem.courses.sort((d1, d2) -> {
            int w1 = d1.weekDay; // 假设取第一个
            int w2 = d2.weekDay;
//            gj.sc("比较 " + d1.weekDay + " " + d2.weekDay);
            return Integer.compare(w1, w2);
        });
        // 1. 遍历每个节次，创建行数据
        for (Curriculum.Course adapter : dataItem.courses) {
            try {
                adapter.height = 1;
                String classTime = adapter.classTime;//101020304...
                List<Number> parts = GetParts(classTime);
                List<List<Curriculum.Course>> lists = TableList.get(parts.get(0).intValue() - 1);
                adapter.height = parts.size() - 1;
                int addp = parts.get(1).intValue() - 1;
                List<Curriculum.Course> courses = lists.get(addp);
                if (IsCourseList(courses)) {
                    gj.sc("重复的数据" + classTime);
                    courses.add(adapter);
                } else {
                    courses.clear();
                    courses.add(adapter);
                }
            } catch (Exception e) {
                gj.sc(e);
            }
        }
        for (List<List<Curriculum.Course>> list : TableList) {
            for (int i = 0; i < list.size(); i++) {
                Curriculum.Course s = list.get(i).get(0);
                for (int j = 1; j < s.height; j++) {
                    list.remove(i + 1);
                }
            }
        }
        return TableList;
    }

    public static List<Number> GetParts(String classTime) {
        List<Number> parts = new ArrayList<>();
        int index = 0;
// 第一个字符，取 1 位
        parts.add(Integer.parseInt(classTime.substring(index, index + 1)));
        index += 1;
// 后续每次取 2 位，直到结束 不准重复
        while (index + 2 <= classTime.length()) {
            parts.add(Integer.parseInt(classTime.substring(index, index + 2)));
            index += 2;
        }
        return parts;
    }

    public static boolean IsCourseList(List<Curriculum.Course> course) {
        for (Curriculum.Course course1 : course) {
            boolean b = IsCourse(course1);
            if (!b) {
                return false;
            }
        }
        return true;
    }

    public static boolean IsCourse(Curriculum.Course course) {
        if (course.classTime == null || course.endTime == null || course.startTime == null) {
            return false;
        }
        if (course.weekDay < 0 || course.weekDay > 7) {
            return false;
        }
        return !TextUtils.isEmpty(course.courseName) && !TextUtils.isEmpty(course.teacherName) && !TextUtils.isEmpty(course.getClassroomName()) && !TextUtils.isEmpty(course.ktmc);
    }
}
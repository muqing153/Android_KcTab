package com.muqing.kctab.fragment;

import static com.muqing.wj.data;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.muqing.Fragment;
import com.muqing.gj;
import com.muqing.kctab.Adapter.GridAdapter;
import com.muqing.kctab.Adapter.TableHAdapter;
import com.muqing.kctab.Adapter.TableTimeAdapter;
import com.muqing.kctab.Curriculum;
import com.muqing.kctab.DataType.TableTimeData;
import com.muqing.kctab.MainActivity;
import com.muqing.kctab.R;
import com.muqing.kctab.databinding.FragmentKebiaoBinding;
import com.muqing.kctab.databinding.GridItemBinding;
import com.muqing.wj;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
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

    public kecheng() {
    }

    @Override
    protected FragmentKebiaoBinding getViewBindingObject(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        return FragmentKebiaoBinding.inflate(inflater, container, false);
    }

    public Handler handler;

    @Override
    public void onStart() {
        super.onStart();
        if (isAdded()) {
            if (FilePath != null) {
//            gj.sc("启动Fragment UI 读取文件");
                String dqwb = wj.dqwb(FilePath, "");
                curriculum = new Gson().fromJson(dqwb, Curriculum.class);
                curriculum.data.get(0).week = zhou;
            }
            if (handler == null && zhou == MainActivity.benzhou) {
                handler = new Handler();
            }
            if (curriculum != null && curriculum.data != null) {
//                HList = new String[]{"日期", "一", "二", "三", "四", "五", "六", "日"};
                for (int i = 0; i < curriculum.data.get(0).date.size(); i++) {
                    HList[i + 1] += "(" + curriculum.data.get(0).date.get(i).rq + ")";
                }
                GetKcLei(curriculum);
                for (int i = 0; i < TableList.size(); i++) {
                    GridAdapter adapter = new GridAdapter(this.getContext(), TableList.get(i)) {
                        @Override
                        public boolean update(List<Curriculum.Course> list, Curriculum.Course course
                                , int position) {
                            boolean isCourse = false;
                            for (Curriculum.Course c : list) {
                                if (IsCourse(c)) {
                                    isCourse = true;
                                    curriculum.data.get(0).courses.add(c);
                                }
                            }
                            if (!isCourse) {
                                curriculum.data.get(0).courses.remove(course);
                            }
                            notifyDataSetChanged();
                            wj.xrwb(FilePath, new Gson().toJson(curriculum));
                            return false;
                        }
                    };
                    RecyclerView recyclerView = recyclerViews.get(i);
                    if (recyclerView != null) {
                        recyclerView.setAdapter(adapter);
                    }
                }
                toolbar_time();
                if (handler != null) {
                    handler.post(this::LoadHander);
                }
            }
        }
    }

    private int Day = -1;
    private String time = "08:00";

    private GridItemBinding NextItemBinding;

    private void LoadHander() {
        if (isAdded()) {
            if (Day == -1) {
                //获取当前星期
                LocalDate now = LocalDate.now();
                Day = now.getDayOfWeek().getValue();
//                获取当前时间 08:00
            }
//            time="20:00";
            time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm", Locale.CHINA));
            for (int i = Day - 1; i < 7; i++) {
                List<List<Curriculum.Course>> lists = TableList.get(i);
                for (int y = 0; y < lists.size(); y++) {
                    Curriculum.Course data = lists.get(y).get(0);
                    if (IsCourse(data)) {
                        if (time.compareTo(data.startTime) >= 0 && time.compareTo(data.endTime) <= 0 && Objects.equals(data.weekDay, i + 1)) {
                            View viewByPosition = Objects.requireNonNull(recyclerViews.get(i).getLayoutManager()).findViewByPosition(y);
                            if (viewByPosition != null) {
                                if (NextItemBinding == null) {
                                    NextItemBinding = GridItemBinding.bind(viewByPosition);
                                    NextItemBinding.getRoot().setStrokeWidth(3);
//                                    NextItemBinding.getRoot().getLocationInWindow(ItemXY);
                                } else if (NextItemBinding.getRoot() != viewByPosition) {
                                    NextItemBinding.getRoot().setStrokeWidth(0);
                                    NextItemBinding = null;
                                }
                                break;
                            }
                        }
                        if (data.startTime.compareTo(time) > 0 && Objects.equals(data.weekDay, i + 1)) {
                            View viewByPosition = Objects.requireNonNull(recyclerViews.get(i).getLayoutManager()).findViewByPosition(y);
//                            gj.sc("B " + i + " " + time);
                            if (viewByPosition != null) {
                                if (NextItemBinding == null) {
                                    NextItemBinding = GridItemBinding.bind(viewByPosition);
                                    NextItemBinding.getRoot().setStrokeWidth(3);
//                                    NextItemBinding.getRoot().getLocationInWindow(ItemXY);
                                } else if (NextItemBinding.getRoot() != viewByPosition) {
                                    NextItemBinding.getRoot().setStrokeWidth(0);
                                    NextItemBinding = null;
                                }
                                break;
                            }
                        }
//                            View viewByPosition = Objects.requireNonNull(recyclerView.getLayoutManager()).findViewByPosition(y);
//                        Curriculum.Course course = list.get(0);
//                        gj.sc(course.classTime + " " + course.courseName);
                    }
                }
                if (NextItemBinding != null) {
                    break;
                }
                time = "08:00";
            }
//            RecyclerView recyclerView = recyclerViews.get(Day);
//            GridAdapter adapter = (GridAdapter) recyclerView.getAdapter();
        }
        handler.postDelayed(this::LoadHander, 1000);
    }

    public Curriculum curriculum;

    public void toolbar_time() {
        if (!isAdded()) return;
        TextView viewById = requireActivity().findViewById(R.id.toolbar_time);
        viewById.setText(curriculum.data.get(0).date.get(0).mxrq);
    }

    List<RecyclerView> recyclerViews = new ArrayList<>();

    String[] HList = new String[]{"日期", "一", "二", "三", "四", "五", "六", "日"};

    @Override
    public void setUI(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding.recyclerviewH.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerviewTime.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
//        数组转List
        TableTimeData[] data = TableTimeData.tableTimeData;
        binding.recyclerviewTime.setAdapter(new TableTimeAdapter(this.getContext(), Arrays.asList(data)));
        binding.recyclerviewH.setAdapter(new TableHAdapter(this.getContext(), Arrays.asList(HList)));
        binding.tablelayout.removeAllViews();
        recyclerViews.clear();
        for (int i = 0; i < 7; i++) {
            RecyclerView recyclerView = new RecyclerView(requireContext());
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
            binding.tablelayout.addView(recyclerView);
            recyclerViews.add(recyclerView);
        }
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

    private void GetKcLei(Curriculum curriculum) {
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
        int delta = 0;
        int day = 1;
        for (Curriculum.Course adapter : dataItem.courses) {
//            = new ArrayList<>();
            if (day != adapter.weekDay) {
                delta = 0;
                day = adapter.weekDay;
                gj.sc("换天-----------------------------------------------" + day);
            }
            adapter.height = 1;
            String classTime = adapter.classTime;//101020304...
            List<Number> parts = GetParts(classTime);
            List<List<Curriculum.Course>> lists = TableList.get(parts.get(0).intValue() - 1);
//            parts.remove(0);
            adapter.height = parts.size() - 1;
            int addp = parts.get(1).intValue() - 1 - delta;
            List<Curriculum.Course> courses = lists.get(
                    addp
            );
            courses.clear();
            courses.add(adapter);
            gj.sc("添加课程的位置 " + addp);
            for (int i = 2; i < parts.size(); i++) {
                delta++;
                addp++;
                gj.sc("删除 " + classTime + " 删除位置 " + addp);
                lists.remove(addp);
            }
        }
//        return list;
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
        if (TextUtils.isEmpty(course.courseName)
                || TextUtils.isEmpty(course.teacherName)
                || TextUtils.isEmpty(course.getClassroomName())
                || TextUtils.isEmpty(course.ktmc)) {
            return false;
        }

        return true;
    }

}
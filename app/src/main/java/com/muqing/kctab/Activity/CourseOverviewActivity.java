package com.muqing.kctab.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.graphics.Insets;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.muqing.AppCompatActivity;
import com.muqing.gj;
import com.muqing.kctab.Adapter.CourseOverviewAdapter;
import com.muqing.kctab.Curriculum;
import com.muqing.kctab.MainActivity;
import com.muqing.kctab.databinding.ActivityCourseOverviewBinding;
import com.muqing.kctab.fragment.kecheng;
import com.muqing.wj;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseOverviewActivity extends AppCompatActivity<ActivityCourseOverviewBinding> {

    List<Curriculum.Course> list = new ArrayList<>();


    @Override
    public void setOnApplyWindowInsetsListener(Insets systemBars, View v) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        setBackToolsBar(binding.toolbar);
        int i = 1;
        for (String s : MainActivity.TabList) {
            String dqwb = wj.dqwb(s, "");
            Curriculum c = new Gson().fromJson(dqwb, Curriculum.class);
            c.data.get(0).week = i;
            for (Curriculum.Course course : c.data.get(0).courses) {
                boolean b = kecheng.IsCourse(course);
                if (b) {
                    boolean exists = false;
                    for (Curriculum.Course added : list) {
                        if (added.courseName != null
                                && added.courseName.equals(course.courseName)) {
                            if (!added.getClassroomName().contains(course.getClassroomName())) {
                                added.classroomName += "/" + course.getClassroomName();
                            }
                            if (!added.Time.contains(course.startTime + "-" + course.endTime)) {
                                added.Time += "/" + course.startTime + "-" + course.endTime;
                            }
                            if (!added.Zhou.contains(String.valueOf(i))) {
                                added.Zhou += "/" + i;
                            }
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        if (course.Time == null) {
                            course.Time = course.startTime + "-" + course.endTime;
                        }
                        if (course.Zhou == null) {
                            course.Zhou = String.valueOf(i);
                        }
                        list.add(course);
                    }
                }
            }
            i++;
        }
        binding.recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recyclerview.setAdapter(new CourseOverviewAdapter(this, list));
    }

    @Override
    protected ActivityCourseOverviewBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivityCourseOverviewBinding.inflate(layoutInflater);
    }
}
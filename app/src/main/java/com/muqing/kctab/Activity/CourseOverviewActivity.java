package com.muqing.kctab.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.core.graphics.Insets;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.muqing.AppCompatActivity;
import com.muqing.kctab.Adapter.CourseOverviewAdapter;
import com.muqing.kctab.Curriculum;
import com.muqing.kctab.MainActivity;
import com.muqing.kctab.databinding.ActivityCourseOverviewBinding;
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
            for (Curriculum.Course cours : c.data.get(0).courses) {
                // 查找是否已存在同名课程
                Optional<Curriculum.Course> existingCourse = list.stream()
                        .filter(existing -> existing.courseName.equals(cours.courseName))
                        .findFirst();
                if (existingCourse.isPresent()) {
                    // 如果存在，追加教室信息
                    Curriculum.Course found = existingCourse.get();
                    if (!found.getClassroomName().contains(cours.getClassroomName())) {
                        found.classroomName += "/" + cours.getClassroomName();
                    }
                    if (found.Time == null) {
                        found.Time = found.startTime + "-" + found.endTime;
                    }
                    cours.Time = cours.startTime + "-" + cours.endTime;
                    if (!found.Time.contains(cours.Time)) {
                        found.Time += "/" + cours.Time;
                    }
                    if (found.Zhou == null) {
                        found.Zhou = String.valueOf(i);
                    }
                    if (!found.Zhou.contains(String.valueOf(i))) {
                        found.Zhou += "/" + i;
                    }
                    // 也可以追加其他信息，如教师、时间等
                    // found.teacherName += " / " + cours.teacherName;
                } else {
                    list.add(cours);
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
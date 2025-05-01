package com.muqing.kctab.Activity;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.muqing.AppCompatActivity;
import com.muqing.kctab.R;
import com.muqing.kctab.databinding.ActivityCourseOverviewBinding;

public class CourseOverviewActivity extends AppCompatActivity<ActivityCourseOverviewBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();

    }

    @Override
    protected ActivityCourseOverviewBinding getViewBindingObject(LayoutInflater layoutInflater) {
        return ActivityCourseOverviewBinding.inflate(layoutInflater);
    }
}
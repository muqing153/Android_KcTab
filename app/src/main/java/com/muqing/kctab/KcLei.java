package com.muqing.kctab;

import androidx.annotation.Nullable;

public class KcLei {
    public String title;
    public String message;
    public Curriculum.Course data = null;

    public KcLei(String title) {
        this.title = title;
        init();
    }

    public KcLei() {
        init();
    }

    public KcLei(@Nullable Curriculum.Course course) {
        if (course == null) {
            data = new Curriculum.Course();
            return;
        }
        data = course;
        title = course.courseName;
        if (title != null) {
            message = course.getClassroomName();
        }
    }

    public void init() {

    }

}

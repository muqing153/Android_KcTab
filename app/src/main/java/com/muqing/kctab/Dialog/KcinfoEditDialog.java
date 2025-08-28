package com.muqing.kctab.Dialog;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muqing.ViewUI.BaseDialog;
import com.muqing.kctab.Curriculum;
import com.muqing.kctab.databinding.DialogKcinfoEditBinding;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

/**
 *
 */
public class KcinfoEditDialog extends BaseDialog<DialogKcinfoEditBinding> {
    List<Curriculum.Course> data;

    public KcinfoEditDialog(Context context, List<Curriculum.Course> data) {
        super(context);
        Type listType = new TypeToken<List<Curriculum.Course>>() {
        }.getType();
        Gson gson = new Gson();
        this.data = gson.fromJson(gson.toJson(data), listType);
        initView();
        show();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
    }

    int position = 1;
    Curriculum.Course course;

    @Override
    protected void initView() {
        if (data.isEmpty()) {
            data.add(new Curriculum.Course());
        }
        course = data.get(position - 1);
        // 课程名
        binding.kc.setText(course.courseName == null ? "" : course.courseName);

        // 教师
        binding.teacher.setText(course.teacherName == null ? "" : course.teacherName);

        // 班级
        binding.classroom.setText(course.ktmc == null ? "" : course.ktmc);

        // 教室
        binding.address.setText(course.classroomName == null ? "" : course.classroomName);

        // 时间
        String start = course.startTime == null ? "" : course.startTime;
        String end = course.endTime == null ? "" : course.endTime;
        binding.time.setText(String.format(Locale.getDefault(), "%s-%s", start, end));
        binding.positionView.setText(String.format(Locale.getDefault(), "%d/%d", position, data.size()));
        binding.previousView.setOnClickListener(view -> {
            if (position > 1) {
                position--;
                course = data.get(position - 1);
                binding.kc.setText(course.courseName);
                binding.teacher.setText(course.teacherName);
                binding.classroom.setText(course.ktmc);
                binding.address.setText(course.classroomName);
//                binding.time.setText(String.format(Locale.getDefault(), "%s-%s", data1.startTime, data1.endTime));
                binding.positionView.setText(String.format(Locale.getDefault(), "%d/%d", position, data.size()));
            }
        });
        binding.nextView.setOnClickListener(view -> {
            if (position == data.size() && !TextUtils.isEmpty(course.courseName)
                    && !TextUtils.isEmpty(course.teacherName) && !TextUtils.isEmpty(course.ktmc) && !TextUtils.isEmpty(course.classroomName)) {
                Curriculum.Course c = new Curriculum.Course();
                c.startTime = course.startTime;
                c.endTime = course.endTime;
                data.add(c);
            }
            if (position < data.size()) {
                position++;
                course = data.get(position - 1);
                binding.kc.setText(course.courseName);
                binding.teacher.setText(course.teacherName);
                binding.classroom.setText(course.ktmc);
                binding.address.setText(course.classroomName);
//                binding.time.setText(String.format(Locale.getDefault(), "%s-%s", c.startTime, c.endTime));
                binding.positionView.setText(String.format(Locale.getDefault(), "%d/%d", position, data.size()));
            }
        });

        binding.cancle.setOnClickListener(view -> dismiss());

        binding.sure.setOnClickListener(view -> dismiss(data));
        EditBind();
    }

    public void dismiss(List<Curriculum.Course> course) {
        dismiss();
    }

    private static class TextWatcherDebug implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

    private void EditBind() {
        binding.kc.addTextChangedListener(new TextWatcherDebug() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                course.courseName = editable.toString();
            }
        });
        binding.teacher.addTextChangedListener(new TextWatcherDebug() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                course.teacherName = editable.toString();
            }
        });
        binding.classroom.addTextChangedListener(new TextWatcherDebug() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                course.ktmc = editable.toString();
            }
        });
        binding.address.addTextChangedListener(new TextWatcherDebug() {
            @Override
            public void afterTextChanged(Editable editable) {
                super.afterTextChanged(editable);
                course.classroomName = editable.toString();
            }
        });
    }

    @Override
    protected DialogKcinfoEditBinding getViewBindingObject(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return DialogKcinfoEditBinding.inflate(inflater, parent, false);
    }

}

package com.muqing.kctab.Dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.muqing.ViewUI.BaseDialog;
import com.muqing.gj;
import com.muqing.kctab.Curriculum;
import com.muqing.kctab.databinding.DialogKcinfoEditBinding;
import com.muqing.kctab.fragment.kecheng;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

/**
 *
 */
public class KcinfoEditDialog extends BaseDialog<DialogKcinfoEditBinding> {
    List<Curriculum.Course> data;
    public static Curriculum.Course course_static;


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
        gj.sc(course.classTime + " " + course.height);
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
            previous();
        });
        binding.nextView.setOnClickListener(view -> {
            next();
        });

        binding.cancle.setOnClickListener(view -> dismiss());
        binding.sure.setOnClickListener(view -> {
            if (data.size() == 1) {
                dismiss(data);
                return;
            }
            for (Curriculum.Course c : data) {
                boolean b = kecheng.IsCourse(c);
                if (!b) {
                    data.remove(c);
                }
            }
            dismiss(data);
        });
        binding.copyView.setOnClickListener(view -> {
            Gson gson = new Gson();
            course_static = gson.fromJson(gson.toJson(course), Curriculum.Course.class);
            gj.ts(getContext(), "复制成功！");
        });
        binding.pasteView.setOnClickListener(view -> {
            if (course_static != null) {
                course.courseName = course_static.courseName;
                course.teacherName = course_static.teacherName;
                course.ktmc = course_static.ktmc;
                course.classroomName = course_static.classroomName;
                gj.sc(course.classTime);
                setEdit(course);
                course_static = null;
            } else {
                gj.ts(getContext(), "没有粘贴内容！");
            }
        });
        binding.deleteView.setOnClickListener(view -> {
            if (data.size() < 2) {
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("提示")
                        .setMessage("你只有这一节课了是否要清空？")
                        .setPositiveButton("确定", (dialogInterface, i) -> {
                            Curriculum.Course c = new Curriculum.Course();
                            c.weekDay = course.weekDay;
                            c.startTime = course.startTime;
                            c.endTime = course.endTime;
                            List<Number> numbers = kecheng.GetParts(course.classTime);
                            c.classTime = String.format(Locale.getDefault(), "%d%02d", numbers.get(0).intValue(), numbers.get(1).intValue());
//                            c.classTime = course.classTime;
                            c.height = 1;
//                            course = c;
                            data.remove(course);
                            data.add(c);
                            course = c;
                            setEdit(course);
                        }).setNegativeButton("取消", null)
                        .show();
                return;
            } else if (position < data.size()) {
                data.remove(position - 1);
                position--;
                next();
            } else {
                data.remove(position - 1);
                previous();
            }
            gj.ts(getContext(), "删除成功！");
        });

        EditBind();
    }

    private void previous() {
        if (position > 1) {
            position--;
            course = data.get(position - 1);
            setEdit(course);
        }
    }

    private void next() {
        if (position == data.size() && !TextUtils.isEmpty(course.courseName)
                && !TextUtils.isEmpty(course.teacherName) && !TextUtils.isEmpty(course.ktmc) && !TextUtils.isEmpty(course.classroomName)) {
            Curriculum.Course c = new Curriculum.Course();
            c.startTime = course.startTime;
            c.endTime = course.endTime;
            c.weekDay = course.weekDay;
            c.classTime = course.classTime;
            data.add(c);
        }
        if (position < data.size()) {
            position++;
            course = data.get(position - 1);
            setEdit(course);
        }
    }


    private void setEdit(Curriculum.Course course) {
        binding.kc.setText(course.courseName);
        binding.teacher.setText(course.teacherName);
        binding.classroom.setText(course.ktmc);
        binding.address.setText(course.classroomName);
//                binding.time.setText(String.format(Locale.getDefault(), "%s-%s", data1.startTime, data1.endTime));
        binding.positionView.setText(String.format(Locale.getDefault(), "%d/%d", position, data.size()));
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

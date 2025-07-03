package com.muqing.kctab.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.muqing.BaseAdapter;
import com.muqing.kctab.Curriculum;
import com.muqing.kctab.databinding.ItemKcinfoDialogBinding;
import com.muqing.kctab.databinding.KcinfoDialogBinding;

import java.util.List;
import java.util.Locale;

public class KcInfoAdapter extends BaseAdapter<ItemKcinfoDialogBinding, Curriculum.Course> {

    public KcInfoAdapter(Context context, List<Curriculum.Course> dataList) {
        super(context, dataList);
    }

    @Override
    protected ItemKcinfoDialogBinding getViewBindingObject(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemKcinfoDialogBinding.inflate(inflater, parent, false);
    }

    @Override
    protected void onBindView(Curriculum.Course data, ItemKcinfoDialogBinding viewBinding, ViewHolder<ItemKcinfoDialogBinding> viewHolder, int position) {
        viewBinding.kc.setText(data.courseName);
        viewBinding.teacher.setText(String.format(data.teacherName));
        //分割班级
        String[] split = data.ktmc.split(",");
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            stringBuffer.append(split[i]);
            if (i != split.length - 1) {
                stringBuffer.append("\n");
            }
        }
        viewBinding.classroom.setText(stringBuffer.toString());
        viewBinding.address.setText(String.format(data.classroomName));
        viewBinding.time.setText(String.format(Locale.getDefault(), "%s-%s", data.startTime, data.endTime));
    }
}

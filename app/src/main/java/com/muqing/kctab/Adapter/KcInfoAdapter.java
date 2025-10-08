package com.muqing.kctab.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.muqing.BaseAdapter;
import com.muqing.gj;
import com.muqing.kctab.Curriculum;
import com.muqing.kctab.DataType.TableTimeData;
import com.muqing.kctab.databinding.ItemKcinfoDialogBinding;
import com.muqing.kctab.databinding.KcinfoDialogBinding;
import com.muqing.kctab.fragment.kecheng;

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
// 课程名
        viewBinding.kc.setText(
                data.courseName == null ? "" : data.courseName
        );

// 教师
        viewBinding.teacher.setText(
                data.teacherName == null ? "" : data.teacherName
        );

// 分割班级
        if (data.ktmc != null && !data.ktmc.isEmpty()) {
            String[] split = data.ktmc.split(",");
            StringBuilder stringBuffer = new StringBuilder();
            for (int i = 0; i < split.length; i++) {
                stringBuffer.append(split[i] == null ? "" : split[i]);
                if (i != split.length - 1) {
                    stringBuffer.append("\n");
                }
            }
            viewBinding.classroom.setText(stringBuffer.toString());
        } else {
            viewBinding.classroom.setText("");
        }

// 教室
        viewBinding.address.setText(
                data.classroomName == null ? "" : data.classroomName
        );

// 时间
        String weekNoteDetail = data.weekNoteDetail;
        if (weekNoteDetail != null && !weekNoteDetail.isEmpty()) {
            int[] section = kecheng.getSection(weekNoteDetail);
            gj.sc(new Gson().toJson( section));
            String format = String.format(Locale.getDefault(), "%s-%s", TableTimeData.tableTimeData[section[0] - 1].starttime
                    , TableTimeData.tableTimeData[section[section.length - 1] - 1].endtime);
            viewBinding.time.setText(format);

        }

    }
}

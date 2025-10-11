package com.muqing.kctab.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.muqing.BaseAdapter;
import com.muqing.gj;
import com.muqing.kctab.Curriculum;
import com.muqing.kctab.Dialog.KcinfoBottomDialog;
import com.muqing.kctab.MainActivity;
import com.muqing.kctab.databinding.GridItemBinding;
import com.muqing.kctab.fragment.kecheng;

import java.util.List;

public class GridAdapter extends BaseAdapter<GridItemBinding, List<Curriculum.Course>> {
    public int zhou = 0;
    public boolean showInfo = true;


    public GridAdapter(Context context, List<List<Curriculum.Course>> dataList) {
        super(context, dataList);
    }

    @Override
    protected GridItemBinding getViewBindingObject(LayoutInflater inflater, ViewGroup parent, int viewType) {
        GridItemBinding inflate = GridItemBinding.inflate(inflater, parent, false);
        if (MainActivity.TableStyle != null) {
            AutoTableAdapter.bindView(MainActivity.TableStyle, inflate.getRoot());
        }
        return inflate;
    }

    @Override
    protected void onBindView(List<Curriculum.Course> data, GridItemBinding viewBinding, ViewHolder<GridItemBinding> viewHolder, int position) {
        Curriculum.Course course = data.get(0);
        int[] section = kecheng.getSection(course.weekNoteDetail);
        int maxLines = viewBinding.tabletitle.getMaxLines();
        viewBinding.getRoot().getLayoutParams().height = (MainActivity.TableStyle == null ? gj.dp2px(context, 70) :
                MainActivity.TableStyle.table.getHeight(context)) * section.length;
        viewBinding.getRoot().requestLayout();
        maxLines *= section.length;
        viewBinding.tabletitle.setMaxLines(maxLines);
        viewBinding.tablemessage.setMaxLines(maxLines);
        viewBinding.tablemessage.setVisibility(View.GONE);
        viewBinding.tabletitle.setText(course.courseName);
        if (!TextUtils.isEmpty(course.courseName) && showInfo) {
            viewBinding.tablemessage.setVisibility(View.VISIBLE);
            viewBinding.tablemessage.setText(course.getClassroomName());
        }
        CharSequence text = viewBinding.listSize.getText();
        if (data.size() > 1) {
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < data.size(); i++) {
                stringBuffer.append(text);
                if (i != data.size() - 1) {
                    stringBuffer.append(' ');
                }
            }
            viewBinding.listSize.setText(stringBuffer);
            viewBinding.listSize.setVisibility(View.VISIBLE);
        } else {
            viewBinding.listSize.setVisibility(View.GONE);
        }
        viewBinding.getRoot().setOnClickListener(v -> {
            KcinfoBottomDialog dialog = new KcinfoBottomDialog(context, data);
            dialog.setOnDismissListener(dialogInterface -> {
//                if (!new Gson().toJson(data).equals(new Gson().toJson(dialog.data))) {
//                    data.clear();
//                    data.addAll(dialog.data);
//                    notifyItemChanged(position);
//                    update(data, course, position);
//                }
            });
        });
    }

    /**
     * 更新保存课表
     *
     * @param course
     * @return
     */
    public boolean update(List<Curriculum.Course> data, Curriculum.Course course, int position) {
        return false;
    }
}
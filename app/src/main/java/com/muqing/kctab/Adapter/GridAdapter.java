package com.muqing.kctab.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.muqing.BaseAdapter;
import com.muqing.gj;
import com.muqing.kctab.Curriculum;
import com.muqing.kctab.DataType.TableStyleData;
import com.muqing.kctab.Dialog.KcinfoBottomDialog;
import com.muqing.kctab.MainActivity;
import com.muqing.kctab.databinding.GridItemBinding;
import com.muqing.kctab.fragment.kecheng;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GridAdapter extends BaseAdapter<GridItemBinding, List<Curriculum.Course>> {
    public int zhou = 0;
    public boolean showInfo = true;

    TableStyleData tablestyle;

    public GridAdapter(Context context, List<List<Curriculum.Course>> dataList) {
        super(context, dataList);
        Gson gson = new Gson();
        SharedPreferences a = context.getSharedPreferences("tablestyle", Context.MODE_PRIVATE);
        tablestyle = gson.fromJson(a.getString("tablestyle", gson.toJson(new TableStyleData())), TableStyleData.class);
    }

    @Override
    protected GridItemBinding getViewBindingObject(LayoutInflater inflater, ViewGroup parent, int viewType) {
        GridItemBinding inflate = GridItemBinding.inflate(inflater, parent, false);
        inflate.getRoot().setUseCompatPadding(tablestyle.cardUseCompatPadding);
        if (tablestyle.cardElevation > -1) {
            inflate.getRoot().setCardElevation(tablestyle.cardElevation);
        }
        if (tablestyle.cardCornerRadius > -1) {
            inflate.getRoot().setRadius(tablestyle.cardCornerRadius);
        }
        if (tablestyle.height > -1) {
            inflate.line1.getLayoutParams().height = gj.dp2px(context, tablestyle.height);
        }
        if (tablestyle.width > -1) {
            inflate.line1.getLayoutParams().width = gj.dp2px(context, tablestyle.width);
        }
        return inflate;
    }

    private int height = 0;

    @Override
    protected void onBindView(List<Curriculum.Course> data, GridItemBinding viewBinding, ViewHolder<GridItemBinding> viewHolder, int position) {
        Curriculum.Course course = data.get(0);
        int maxLines = viewBinding.title.getMaxLines();
        viewBinding.getRoot().post(() -> {
            if (height == 0) {
                height = viewBinding.getRoot().getHeight();
            }
//            gj.sc("height" + height + " " + course.height);
            viewBinding.getRoot().getLayoutParams().height = height * course.height;
            viewBinding.getRoot().requestLayout();
        });
        maxLines *= course.height;
        viewBinding.title.setMaxLines(maxLines);
        viewBinding.message.setMaxLines(maxLines);
        viewBinding.message.setVisibility(View.GONE);
        viewBinding.title.setText(course.courseName);
        if (!TextUtils.isEmpty(course.courseName) && showInfo) {
            viewBinding.message.setVisibility(View.VISIBLE);
            viewBinding.message.setText(course.getClassroomName());
        }

        viewBinding.line1.setVisibility(View.VISIBLE);
        if (data.size() > 1) {
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < data.size(); i++) {
                stringBuffer.append('-');
                if (i != data.size() - 1) {
                    stringBuffer.append(' ');
                }
            }
            viewBinding.listSize.setText(stringBuffer);
            viewBinding.listSize.setVisibility(View.VISIBLE);

        }
        viewBinding.getRoot().setOnClickListener(v -> {
            gj.sc(position + "--" + (position + 1) + "--" + data.get(0).classTime);
            KcinfoBottomDialog dialog = new KcinfoBottomDialog(context, data);
            dialog.setOnDismissListener(dialogInterface -> {
                if (data != dialog.data) {
                    data.clear();
                    data.addAll(dialog.data);
//                    gj.sc(data.get(0).classTime );
                    String classTime = data.get(0).classTime;
                    notifyItemChanged(position);
                    if (!kecheng.IsCourseList(data)) {
                        List<Number> numbers = kecheng.GetParts(classTime);
                        if (numbers.size() > 1) {
                            for (int j = 1; j < numbers.size(); j++) {
                                dataList.add(position + j, kecheng.newCourse(numbers.get(0).intValue() - 1, numbers.get(j).intValue()
                                ));
                            }
                        }
                    }
                    update(data, course, position);
//                    notifyDataSetChanged();
                }
            });
        });
//        viewBinding.getRoot().setOnLongClickListener(view -> ShowLong(data, view, position));
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
    public int[] ItemXY = new int[]{0, 0};
}
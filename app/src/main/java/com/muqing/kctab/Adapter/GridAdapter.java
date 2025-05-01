package com.muqing.kctab.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.muqing.BaseAdapter;
import com.muqing.Dialog.BottomSheetDialog;
import com.muqing.gj;
import com.muqing.kctab.Curriculum;
import com.muqing.kctab.KcLei;
import com.muqing.kctab.MainActivity;
import com.muqing.kctab.databinding.GridItemBinding;
import com.muqing.kctab.databinding.KcinfoDialogBinding;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class GridAdapter extends BaseAdapter<GridItemBinding, KcLei> {

    public final int ColorThis;
    //当前课的颜色高亮
    public final int ColorWhen;
    //下节课的颜色高亮
    public final int ColorNext;
    public int zhou = 0;

    public GridAdapter(Context context, List<KcLei> dataList) {

        super(context, dataList);
        ColorThis = gj.getThemeColor(context, com.google.android.material.R.attr.colorSurfaceContainerLow);
        ColorWhen = gj.getThemeColor(context, com.google.android.material.R.attr.colorPrimary);
        ColorNext = gj.getThemeColor(context, com.google.android.material.R.attr.colorOnPrimary);
    }


    @Override
    protected GridItemBinding getViewBindingObject(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return GridItemBinding.inflate(inflater, parent, false);
    }

    @Override
    protected void onBindView(KcLei data, GridItemBinding viewBinding, BaseAdapter.ViewHolder<GridItemBinding> viewHolder, int position) {
        viewBinding.title.setText(data.title);
        if (position < 8) {
            viewBinding.message.setVisibility(View.GONE);
        } else {
            viewBinding.getRoot().setCardBackgroundColor(ColorThis);
            viewBinding.message.setVisibility(View.VISIBLE);
            viewBinding.message.setText(data.message);
        }
        viewBinding.getRoot().setOnClickListener(v -> {
            Curriculum.Course course = data.data;
            if (course != null && course.courseName != null) {
                ShowKc(course);
            }
        });
    }

    private void ShowKc(Curriculum.Course course) {
//        String format = String.format(Locale.getDefault(), "%s\n" +
//                "老师:%s\n" +
//                "班级:%s\n" +
//                "地点:%s\n" +
//                "时间:%s-%s", course.courseName, course.teacherName, course.ktmc, course.classroomName, course.startTime, course.endTime);
        KcinfoDialogBinding binding = KcinfoDialogBinding.inflate(LayoutInflater.from(context));
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);

        bottomSheetDialog.setContentView(binding.getRoot());
        ViewGroup.LayoutParams params = binding.getRoot().getLayoutParams();
        params.height = (int) (Resources.getSystem().getDisplayMetrics().heightPixels * 0.5);
        binding.getRoot().setLayoutParams(params);
        binding.kc.setText(course.courseName);
        binding.teacher.setText(course.teacherName);
        //分割班级
        String[] split = course.ktmc.split(",");
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            stringBuffer.append(split[i]);
            if (i != split.length - 1) {
                stringBuffer.append("\n");
            }
        }
        binding.classroom.setText(stringBuffer.toString());
        binding.address.setText(course.getClassroomName());
        binding.time.setText(String.format(Locale.getDefault(), "%s-%s", course.startTime, course.endTime));
        bottomSheetDialog.show();
    }

    GridItemBinding ItemBinding, NextItemBinding;
    public boolean isjt = false;//是否处于截图状态 截图状态不高亮

    public void Load(RecyclerView recyclerView) throws Exception {
        if (isjt) {
            if (ItemBinding != null) {
                ItemBinding.getRoot().setCardBackgroundColor(ColorThis);
            }
            if (NextItemBinding != null) {
                NextItemBinding.getRoot().setCardBackgroundColor(ColorThis);
            }
            return;
        }
//        gj.sc(zhou + " " + MainActivity.benzhou);
        if (zhou != MainActivity.benzhou) {
            //不是本周的课程不高亮
            return;
        }
        int weekDay = MainActivity.Week; // 获取当前星期
//        weekDay = 1;
        String time = MainActivity.Time; // 获取当前时间
//        time = "08:00";
        for (int x = 1, y = weekDay + 8; x < 6; y += 8, x++) {
            KcLei kcLei = dataList.get(y);
            if (kcLei.data != null && kcLei.title != null) {
                if (kcLei.data.startTime != null && kcLei.data.endTime != null) {
                    // 格式化成 HH:mm
                    // 生成 1~7 之间的随机星期
//                time是否在Start和End之间
                    if (time.compareTo(kcLei.data.startTime) >= 0 && time.compareTo(kcLei.data.endTime) <= 0 && Objects.equals(kcLei.data.weekDay, weekDay)) {
                        View viewByPosition = Objects.requireNonNull(recyclerView.getLayoutManager()).findViewByPosition(y);
                        if (viewByPosition != null) {
                            if (ItemBinding != null && ItemBinding.getRoot() != viewByPosition) {
                                ItemBinding.getRoot().setCardBackgroundColor(ColorThis);
                            }
                            ItemBinding = GridItemBinding.bind(viewByPosition);
                            ItemBinding.getRoot().setCardBackgroundColor(ColorWhen);
                            ItemBinding.getRoot().getLocationInWindow(MainActivity.ItemXY);
//                            gj.sc("ItemBinding:" + MainActivity.ItemXY[0] + " " + MainActivity.ItemXY[1]);
                            break;
                        }
                    } else if (ItemBinding != null) {
                        ItemBinding.getRoot().setCardBackgroundColor(ColorThis);
                        ItemBinding = null;
                    }


                    if (kcLei.data.startTime.compareTo(time) > 0 && Objects.equals(kcLei.data.weekDay, weekDay)) {
                        View viewByPosition = Objects.requireNonNull(recyclerView.getLayoutManager()).findViewByPosition(y);
                        if (viewByPosition != null) {
                            if (NextItemBinding != null && NextItemBinding.getRoot() != viewByPosition) {
                                NextItemBinding.getRoot().setCardBackgroundColor(ColorThis);
                            }
                            NextItemBinding = GridItemBinding.bind(viewByPosition);
                            NextItemBinding.getRoot().setCardBackgroundColor(ColorNext);
                            NextItemBinding.getRoot().getLocationInWindow(MainActivity.ItemXY);
//                            gj.sc("NextItemBinding " + MainActivity.ItemXY[0] + " " + MainActivity.ItemXY[1]);
                        }
                        break;
                    } else if (NextItemBinding != null) {

                        NextItemBinding.getRoot().setCardBackgroundColor(ColorThis);
                        NextItemBinding = null;
                    }
                }
            }
        }
        if (NextItemBinding == null && ItemBinding == null && weekDay + 1 != 8) {
            MainActivity.Week++;
            MainActivity.Time = "08:00";
            Load(recyclerView);
        }
    }

}
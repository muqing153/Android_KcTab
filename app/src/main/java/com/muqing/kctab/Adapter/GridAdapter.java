package com.muqing.kctab.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.muqing.BaseAdapter;
import com.muqing.Dialog.BottomSheetDialog;
import com.muqing.gj;
import com.muqing.kctab.Curriculum;
import com.muqing.kctab.MainActivity;
import com.muqing.kctab.R;
import com.muqing.kctab.databinding.GridItemBinding;
import com.muqing.kctab.databinding.KcinfoDialogBinding;
import com.muqing.kctab.fragment.kecheng;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class GridAdapter extends BaseAdapter<GridItemBinding, Curriculum.Course> {

    public final int ColorThis;
    //当前课的颜色高亮
    public final int ColorWhen;
    //下节课的颜色高亮
    public final int ColorNext;
    public int zhou = 0;

    public GridAdapter(Context context, List<Curriculum.Course> dataList) {
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
    protected void onBindView(Curriculum.Course data, GridItemBinding viewBinding, ViewHolder<GridItemBinding> viewHolder, int position) {
        viewBinding.title.setText(data.courseName);
        if (position < 8 || data.classroomName == null) {
            viewBinding.message.setVisibility(View.GONE);
            viewBinding.getRoot().getLayoutParams().height = 130;
        } else {
            viewBinding.getRoot().setCardBackgroundColor(ColorThis);
            viewBinding.message.setVisibility(View.VISIBLE);
            viewBinding.message.setText(data.classroomName);
        }
        viewBinding.getRoot().setOnClickListener(v -> {
            if (data.courseName != null && data.classTime != null) {
                ShowKc(data);
            }
        });
        viewBinding.getRoot().setOnLongClickListener(view -> ShowLong(data, view, position));
    }

    public static Curriculum.Course kcleishuju;
    public boolean isjt = false;//是否处于截图状态 截图状态不高亮
    public static int isjq = -1;//是否处于剪切状态 -1是无剪切 大于是记录上一个剪切的位置
    @SuppressLint("StaticFieldLeak")
    public static GridAdapter jqadapter;//获取剪切的课表Adapter用于更新
    @SuppressLint({"NonConstantResourceId"})
    private boolean ShowLong(Curriculum.Course data, View view, int position) {
        if (position < 8) {
            return true;
        }
        for (int i = 1; i <= 5; i++) {
            if (position == 8 * i) {
                return true;
            }
        }

        PopupMenu popupMenu = new PopupMenu(context, view);
        popupMenu.getMenuInflater().inflate(R.menu.kc_menu, popupMenu.getMenu());
        int size = 4;
        //保证是否以及复制数据 并且复制的数据不能可data相同位置
        if (kcleishuju == null || kcleishuju.equals(data)) {
            MenuItem menuItem = popupMenu.getMenu().findItem(R.id.menu_zt);
            size--;
            menuItem.setVisible(false);
        }
        if (data.classroomName == null) {
            popupMenu.getMenu().findItem(R.id.menu_fz).setVisible(false);
            popupMenu.getMenu().findItem(R.id.menu_jq).setVisible(false);
            popupMenu.getMenu().findItem(R.id.menu_sc).setVisible(false);
            size -= 3;
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            Gson gson = new Gson();
            switch (item.getItemId()) {
                case R.id.menu_fz:
                    kcleishuju = gson.fromJson(gson.toJson(data), Curriculum.Course.class);
                    jqadapter = null;
                    isjq = -1;
                    break;
                case R.id.menu_jq:
                    isjq = position;
                    jqadapter = GridAdapter.this;
                    kcleishuju = gson.fromJson(gson.toJson(data), Curriculum.Course.class);
                    break;
                case R.id.menu_sc:
                    dataList.set(position, new Curriculum.Course());
                    ShowLongDelete(data);
                    break;
                case R.id.menu_zt:
                    dataList.set(position, kcleishuju);
                    int spanCount = 8; // 每行的列数
                    int row = position / spanCount;   // 第几行（从0开始）
                    int column = position % spanCount; // 第几列（从0开始）
                    dataList.get(position).startTime = kecheng.schedule[row - 1].time1.split("-")[0];
                    dataList.get(position).endTime = kecheng.schedule[row - 1].time2.split("-")[1];
                    if (row > 1) {
                        row += position / spanCount - 1;
                    }
                    dataList.get(position).classTime = String.format(Locale.CANADA, "%2d%02d%02d", column, row, row + 1);
                    dataList.get(position).weekDay = column;

                    ShowLongAdd(dataList.get(position));
                    if (isjq != -1 && jqadapter != null) {
                        jqadapter.ShowLongDelete(jqadapter.dataList.get(isjq));
                        jqadapter.dataList.set(isjq, new Curriculum.Course());
                        jqadapter.notifyItemChanged(isjq);
                        jqadapter = null;
                        isjq = -1;
                    }
                    kcleishuju = null;
                    break;
                default:
            }
            notifyItemChanged(position);
            return false;
        });
        if (size != 0) {
            popupMenu.show();
            return false;
        }
        return true;
    }

    public void ShowLongDelete(Curriculum.Course course) {
    }

    public void ShowLongAdd(Curriculum.Course course) {
    }


    private void ShowKc(Curriculum.Course course) {
/*        String format = String.format(Locale.getDefault(), "%s\n" +
                "老师:%s\n" +
                "班级:%s\n" +
                "地点:%s\n" +
                "时间:%s-%s", course.courseName, course.teacherName, course.ktmc, course.classroomName, course.startTime, course.endTime);*/
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

    public int[] ItemXY = new int[]{0, 0};

    public void Load(RecyclerView recyclerView) {
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
            Curriculum.Course data = dataList.get(y);
            if (data.courseName != null) {
                if (data.startTime != null && data.endTime != null) {
                    // 格式化成 HH:mm
                    // 生成 1~7 之间的随机星期
//                time是否在Start和End之间
                    if (time.compareTo(data.startTime) >= 0 && time.compareTo(data.endTime) <= 0 && Objects.equals(data.weekDay, weekDay)) {
                        View viewByPosition = Objects.requireNonNull(recyclerView.getLayoutManager()).findViewByPosition(y);
                        if (viewByPosition != null) {
                            if (ItemBinding != null && ItemBinding.getRoot() != viewByPosition) {
                                ItemBinding.getRoot().setCardBackgroundColor(ColorThis);
                            }
                            ItemBinding = GridItemBinding.bind(viewByPosition);
                            ItemBinding.getRoot().setCardBackgroundColor(ColorWhen);
                            ItemBinding.getRoot().getLocationInWindow(ItemXY);
//                            gj.sc("ItemBinding:" + ItemXY[0] + " " + ItemXY[1]);
                            break;
                        }
                    } else if (ItemBinding != null) {
                        ItemBinding.getRoot().setCardBackgroundColor(ColorThis);
                        ItemBinding = null;
                    }


                    if (data.startTime.compareTo(time) > 0 && Objects.equals(data.weekDay, weekDay)) {
                        View viewByPosition = Objects.requireNonNull(recyclerView.getLayoutManager()).findViewByPosition(y);
                        if (viewByPosition != null) {
                            if (NextItemBinding != null && NextItemBinding.getRoot() != viewByPosition) {
                                NextItemBinding.getRoot().setCardBackgroundColor(ColorThis);
                            }
                            NextItemBinding = GridItemBinding.bind(viewByPosition);
                            NextItemBinding.getRoot().setCardBackgroundColor(ColorNext);
                            NextItemBinding.getRoot().getLocationInWindow(ItemXY);
//                            gj.sc("ItemBinding:" + ItemXY[0] + " " + ItemXY[1]);
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
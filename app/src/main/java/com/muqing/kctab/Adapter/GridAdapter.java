package com.muqing.kctab.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Handler;
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

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class GridAdapter extends BaseAdapter<GridItemBinding, List<Curriculum.Course>> {
    public int zhou = 0;
    private final boolean showJie, showInfo;

    TableStyleData tablestyle;

    public GridAdapter(Context context, List<List<Curriculum.Course>> dataList) {
        super(context, dataList);
        Gson gson = new Gson();
        SharedPreferences a = context.getSharedPreferences("tablestyle", Context.MODE_PRIVATE);
        tablestyle = gson.fromJson(a.getString("tablestyle", gson.toJson(new TableStyleData())), TableStyleData.class);
        SharedPreferences sharedPreferences = context.getSharedPreferences("kebiao", Context.MODE_PRIVATE);
        showJie = sharedPreferences.getBoolean("showJie", true);
        showInfo = sharedPreferences.getBoolean("showInfo", true);
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
            inflate.line2.getLayoutParams().width = gj.dp2px(context, tablestyle.width);
        }
//        inflate.getRoot().setCardBackgroundColor(ColorNext);
        return inflate;
    }

    @Override
    protected void onBindView(List<Curriculum.Course> data, GridItemBinding viewBinding, ViewHolder<GridItemBinding> viewHolder, int position) {
        Curriculum.Course course;
        if (data.isEmpty()) {
            course = null;
            viewBinding.line1.setVisibility(View.GONE);
            viewBinding.line2.setVisibility(View.VISIBLE);
        } else {
            course = data.get(0);
        }
        viewBinding.line1.setVisibility(View.GONE);
        viewBinding.line2.setVisibility(View.GONE);
        viewBinding.message.setVisibility(View.GONE);
        if (course != null) {
            if (position < 8) {
                viewBinding.titleRi.setText(course.courseName);
                viewBinding.message.setVisibility(View.GONE);
                viewBinding.line2.setVisibility(View.VISIBLE);
            } else if (position % 8 == 0) {
                if (showJie) {
                    viewBinding.title.setText(course.courseName);
                } else {
                    viewBinding.title.setVisibility(View.GONE);
                }
                viewBinding.message.setVisibility(View.VISIBLE);
                viewBinding.message.setText(course.getClassroomName());
                viewBinding.line1.setVisibility(View.VISIBLE);
            } else {
                viewBinding.title.setText(course.courseName);
                if (!TextUtils.isEmpty(course.courseName) && showInfo) {
                    viewBinding.message.setVisibility(View.VISIBLE);
                    viewBinding.message.setText(course.getClassroomName());
                }
                viewBinding.line1.setVisibility(View.VISIBLE);
            }
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
        }
        viewBinding.getRoot().setOnClickListener(v -> {
            if (position > 8 && position % 8 != 0) {
                ShowKc(data, position);
            }
        });
//        viewBinding.getRoot().setOnLongClickListener(view -> ShowLong(data, view, position));
    }

    /**
     * 更新保存课表
     *
     * @param course
     * @return
     */
    public boolean update(List<List<Curriculum.Course>> course, int position) {
        return false;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void ShowKc(List<Curriculum.Course> data, int position) {
        KcinfoBottomDialog dialog = new KcinfoBottomDialog(context, data);
        dialog.setOnDismissListener(dialogInterface -> {
            if (data != dialog.data) {
                data.clear();
                data.addAll(dialog.data);
                update(dataList, position);
            }
        });
    }

    public GridItemBinding ItemBinding, NextItemBinding;
    public int[] ItemXY = new int[]{0, 0};
    public int Day = -1;

    public void Load(RecyclerView recyclerView) {
        if (Day == -1) {
            Day = LocalDate.now().getDayOfWeek().getValue();
        }
//        weekDay = 1;
        String time = "08:00";
        if (Day == LocalDate.now().getDayOfWeek().getValue()) {
            time = MainActivity.Time; // 获取当前时间
        }
//        随机生成时间 每过3S
//        time = "15:15";
        for (int x = 1, y = Day + 8; x < 6; y += 8, x++) {
            Curriculum.Course data = dataList.get(y).get(0);
            if (data.courseName != null) {
                if (data.startTime != null && data.endTime != null) {
//                time是否在Start和End之间
                    if (time.compareTo(data.startTime) >= 0 && time.compareTo(data.endTime) <= 0 && Objects.equals(data.weekDay, Day)) {
                        View viewByPosition = Objects.requireNonNull(recyclerView.getLayoutManager()).findViewByPosition(y);
                        if (viewByPosition != null) {
                            if (ItemBinding == null) {
                                ItemBinding = GridItemBinding.bind(viewByPosition);
                                ItemBinding.getRoot().setStrokeWidth(3);
                                ItemBinding.getRoot().getLocationInWindow(ItemXY);
                            } else if (ItemBinding.getRoot() != viewByPosition) {
                                ItemBinding.getRoot().setStrokeWidth(0);
                                ItemBinding = null;
                            }
                            break;
                        }
                    }
                    gj.sc("NextItemBinding " + Day + " " + time);

                    if (data.startTime.compareTo(time) > 0 && Objects.equals(data.weekDay, Day)) {
                        View viewByPosition = Objects.requireNonNull(recyclerView.getLayoutManager()).findViewByPosition(y);
                        if (viewByPosition != null) {
                            if (NextItemBinding == null) {
                                NextItemBinding = GridItemBinding.bind(viewByPosition);
                                NextItemBinding.getRoot().setStrokeWidth(3);
                                NextItemBinding.getRoot().getLocationInWindow(ItemXY);
                            } else if (NextItemBinding.getRoot() != viewByPosition) {
                                NextItemBinding.getRoot().setStrokeWidth(0);
                                NextItemBinding = null;
                            }
                            break;
                        }
                    }
                }
            }
        }
        if (NextItemBinding == null && ItemBinding == null && Day + 1 != 8) {
            Day++;
        }
    }
}
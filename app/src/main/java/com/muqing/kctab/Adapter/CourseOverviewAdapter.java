package com.muqing.kctab.Adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.muqing.BaseAdapter;
import com.muqing.gj;
import com.muqing.kctab.Curriculum;
import com.muqing.kctab.databinding.ItemKechengBinding;

import java.util.List;

public class CourseOverviewAdapter extends BaseAdapter<ItemKechengBinding, Curriculum.Course> {
    int dp2px;

    public CourseOverviewAdapter(Context context, List<Curriculum.Course> dataList) {
        super(context, dataList);
        dp2px = gj.dp2px(context, 500);
    }

    @Override
    protected ItemKechengBinding getViewBindingObject(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemKechengBinding.inflate(inflater, parent, false);
    }


    ItemKechengBinding ThisBind, TopBind;
    boolean isDh = false;


    @Override
    protected void onBindView(Curriculum.Course data, ItemKechengBinding viewBinding, ViewHolder<ItemKechengBinding> viewHolder, int position) {
        viewBinding.title.setText(data.courseName);
        viewBinding.message.setText(data.teacherName);

        viewBinding.infoSj.setText(data.Time);
        viewBinding.infoDd.setText(data.getClassroomName());
        viewBinding.infoZhou.setText(data.Zhou);
        viewBinding.getRoot().setOnClickListener(view -> {
            if (isDh) {
                return;
            }
            isDh = true;
            if (TopBind != null && TopBind == viewBinding) {
                collapseHeight(TopBind.infoLin);
                TopBind = null;
                return;
            }
            if (TopBind != null) {
                collapseHeight(TopBind.infoLin);
            }
            ThisBind = viewBinding;
            expandHeight(ThisBind.infoLin);
            //标记上一个
            TopBind = ThisBind;
        });
    }

    /**
     * 展开高度
     *
     * @param view
     */
    public void expandHeight(final View view) {
        view.setVisibility(View.VISIBLE);
        isDh = false;
    }

    /**
     * 折叠高度
     *
     * @param view
     */
    public void collapseHeight(final View view) {
        view.setVisibility(View.GONE);
        isDh = false;
    }

}

package com.muqing.kctab.Dialog;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DividerItemDecoration;

import com.muqing.ViewUI.BaseBottomDialog;
import com.muqing.kctab.Adapter.KcInfoAdapter;
import com.muqing.kctab.Curriculum;
import com.muqing.kctab.databinding.KcinfoDialogBinding;

import java.util.List;

public class KcinfoBottomDialog extends BaseBottomDialog<KcinfoDialogBinding> {
    public List<Curriculum.Course> data;


    public KcinfoBottomDialog(Context context, List<Curriculum.Course> data) {
        super(context);
        this.data = data;
        initView();
        show();
    }

    @Override
    protected void initView() {
        ViewGroup.LayoutParams params = binding.getRoot().getLayoutParams();
        params.height = (int) (Resources.getSystem().getDisplayMetrics().heightPixels * 0.75);
        binding.getRoot().setLayoutParams(params);
        binding.recyclerview.setAdapter(new KcInfoAdapter(getContext(), data));
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        binding.recyclerview.addItemDecoration(decoration);
//        编辑当前课程
        binding.editView.setOnClickListener(view -> new KcinfoEditDialog(getContext(), data) {
            @Override
            public void dismiss(List<Curriculum.Course> data) {
                super.dismiss();
                KcinfoBottomDialog.this.data = data;
                KcinfoBottomDialog.this.binding.recyclerview.setAdapter(new KcInfoAdapter(getContext(), data));
            }
        });
    }

    public void dismiss(List<Curriculum.Course> data) {
    }

    @Override
    protected KcinfoDialogBinding getViewBindingObject(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return KcinfoDialogBinding.inflate(inflater, parent, false);
    }

}

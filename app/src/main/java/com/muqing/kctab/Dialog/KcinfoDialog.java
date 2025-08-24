package com.muqing.kctab.Dialog;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;

import com.muqing.Dialog.BottomSheetDialog;
import com.muqing.ViewUI.BaseDialog;
import com.muqing.kctab.Adapter.KcInfoAdapter;
import com.muqing.kctab.Curriculum;
import com.muqing.kctab.databinding.KcinfoDialogBinding;

import java.util.List;

public class KcinfoDialog extends BaseDialog<KcinfoDialogBinding> {
    List<Curriculum.Course> course;
    public KcinfoDialog(Context context,List<Curriculum.Course> course) {
        super(context);
        this.course = course;
        initView();
        show();
    }

    @Override
    protected void initView() {
        ViewGroup.LayoutParams params = binding.getRoot().getLayoutParams();
        params.height = (int) (Resources.getSystem().getDisplayMetrics().heightPixels * 0.75);
        binding.getRoot().setLayoutParams(params);
        binding.recyclerview.setAdapter(new KcInfoAdapter(getContext(), course));
        DividerItemDecoration decoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        binding.recyclerview.addItemDecoration(decoration);
    }

    @Override
    protected KcinfoDialogBinding getViewBindingObject(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return KcinfoDialogBinding.inflate(inflater, parent, false);
    }

}

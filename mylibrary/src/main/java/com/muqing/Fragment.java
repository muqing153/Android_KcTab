package com.muqing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

public abstract class Fragment<ViewBindingType extends ViewBinding> extends androidx.fragment.app.Fragment {
    public ViewBindingType binding;

    protected abstract ViewBindingType getViewBindingObject(@NonNull LayoutInflater inflater, @Nullable ViewGroup container);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (binding == null) {
            binding = getViewBindingObject(inflater, container);
        }
        setUI(inflater, container, savedInstanceState);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 视图销毁时将binding设置为null，避免内存泄漏
        binding = null;
    }
    public abstract void setUI(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);
}

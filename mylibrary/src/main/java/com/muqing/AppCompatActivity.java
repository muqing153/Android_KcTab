package com.muqing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.appbar.MaterialToolbar;

public abstract class AppCompatActivity<ViewBindingType extends ViewBinding> extends androidx.appcompat.app.AppCompatActivity {

    public ViewBindingType binding;

    protected abstract ViewBindingType getViewBindingObject(LayoutInflater layoutInflater);

    protected ViewBindingType getViewBinding() {
        binding = getViewBindingObject(getLayoutInflater());
        return binding;
    }

    public void setContentView() {
        EdgeToEdge.enable(this);
        setContentView(getViewBinding().getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            setOnApplyWindowInsetsListener(systemBars, v);
            return insets;
        });
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getOnBackPressedDispatcher().hasEnabledCallbacks()) {
                    BackPressed();
                }
            }
        });
    }

    public void setOnApplyWindowInsetsListener(Insets systemBars, View v) {
        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
    }

    public void setToolsBar(MaterialToolbar toolbar) {
        setSupportActionBar(toolbar);
    }

    public void setBackToolsBar(MaterialToolbar toolbar) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    public void BackPressed() {
        finish();
    }


}

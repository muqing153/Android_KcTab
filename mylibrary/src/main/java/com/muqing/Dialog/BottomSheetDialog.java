package com.muqing.Dialog;

import android.content.Context;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class BottomSheetDialog extends com.google.android.material.bottomsheet.BottomSheetDialog {
    public BottomSheetDialog(@NonNull Context context) {
        super(context);
        setOnShowListener(d -> {
            FrameLayout bottomSheet = findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<?> behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED); // 全展开
            }
        });
    }
}

package com.muqing.ViewUI;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.muqing.R;
import com.muqing.databinding.ViewSeetingTextBinding;

public class SettingTextView extends LinearLayout {
    public SettingTextView(Context context) {
        super(context);
        Init(context, null);
    }

    public SettingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Init(context, attrs);
    }

    public SettingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    ViewSeetingTextBinding binding;

    private void Init(Context context, AttributeSet attrs) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.view_seeting_text, this);
        binding = ViewSeetingTextBinding.bind(inflate);
//        android:clickable="true"
//        android:focusable="true"
        setClickable(true);
        setFocusable(true);
        if (attrs != null) {
            @SuppressLint("Recycle") TypedArray typedArray =
                    context.obtainStyledAttributes(attrs, R.styleable.SettingTextView);
            String title = typedArray.getString(R.styleable.SettingTextView_title);
            String message = typedArray.getString(R.styleable.SettingTextView_message);
            Drawable icon = typedArray.getDrawable(R.styleable.SettingTextView_icon);
            if (title != null) {
                binding.title.setText(title);
            }
            if (icon != null) {
                binding.icon.setImageDrawable(icon);
            }
            if (message != null) {
                binding.message.setVisibility(VISIBLE);
                binding.message.setText(message);
            }
        }

    }

    public void setTitle(String title) {
        binding.title.setText(title);
    }

    public void setMessage(String message) {
        binding.message.setVisibility(VISIBLE);
        binding.message.setText(message);
    }

}

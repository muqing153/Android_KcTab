package com.muqing.kctab.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.muqing.BaseAdapter;
import com.muqing.kctab.MainActivity;
import com.muqing.kctab.databinding.DialogZhouBoxBinding;
import com.muqing.kctab.databinding.ItemZhouBoxBinding;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ZhouBoxAdapter extends BaseAdapter<ItemZhouBoxBinding, String> {
    public List<Integer> zhou;
    @SuppressLint("NotifyDataSetChanged")
    public ZhouBoxAdapter(Context context, List<String> dataList, DialogZhouBoxBinding dialogZhouBoxBinding, List<Integer> zhou) {
        super(context, dataList);
        this.zhou = zhou;
        dialogZhouBoxBinding.all.setOnClickListener(view -> {
            zhou.clear();
            for (int i = 0; i < dataList.size(); i++) {
                zhou.add(i+1);
            }
            notifyDataSetChanged();
        });
        dialogZhouBoxBinding.benzhou.setOnClickListener(view -> {
            zhou.clear();
            zhou.add(MainActivity.benzhou);
            notifyDataSetChanged();
        });
    }

    @Override
    protected ItemZhouBoxBinding getViewBindingObject(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemZhouBoxBinding.inflate(inflater, parent, false);
    }
    AtomicBoolean userClicked = new AtomicBoolean(false);
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onBindView(String data, ItemZhouBoxBinding viewBinding, ViewHolder<ItemZhouBoxBinding> viewHolder, int position) {
        viewBinding.title.setText(data);
        viewBinding.title.setChecked(zhou.contains(position + 1));

        viewBinding.title.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                userClicked.set(true);
            }
            return false; // 不消费事件，确保还能正常切换选中状态
        });

        viewBinding.title.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (userClicked.getAndSet(false)) {
                    // 仅在用户手动点击时执行
//                    gj.sc(zhou);
                    int pos = position + 1;
                    if (b) {
                        zhou.add(pos);
                    } else {
                        zhou.remove((Object) pos);
                    }
                }
            }
        });
    }
}

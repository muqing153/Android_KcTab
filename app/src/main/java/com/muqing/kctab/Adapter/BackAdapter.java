package com.muqing.kctab.Adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.muqing.BaseAdapter;
import com.muqing.gj;
import com.muqing.kctab.databinding.BackItemBinding;
import com.muqing.kctab.databinding.ZhouItemBinding;

import java.util.List;

public class BackAdapter extends BaseAdapter<BackItemBinding, Bitmap> {
    public static int position = 0;
    public int recyclerWidth = 0;
    private boolean DH = false;


    public BackAdapter(Context context, List<Bitmap> dataList) {
        super(context, dataList);
    }

    @Override
    protected BackItemBinding getViewBindingObject(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return BackItemBinding.inflate(inflater, parent, false);
    }

    public BackItemBinding backItemBinding;

    @Override
    protected void onBindView(Bitmap data, BackItemBinding viewBinding, ViewHolder<BackItemBinding> viewHolder, int p) {
        ViewGroup.LayoutParams layoutParams = viewBinding.card.getLayoutParams();
//        if (p == this.position) {
//            layoutParams.height = recyclerWidth /2;
//            layoutParams.width = recyclerWidth;
//        } else {
//        }
        if (backItemBinding == null && position == p) {
            backItemBinding = viewBinding;
            backItemBinding.getRoot().setScaleX(1.1f);
            backItemBinding.getRoot().setScaleY(1.1f);
        }
        layoutParams.height = recyclerWidth / 2 - 60;
        layoutParams.width = recyclerWidth - 60;
        viewBinding.card.setLayoutParams(layoutParams);
        viewBinding.card.setOnClickListener(view -> {
            if (backItemBinding == viewBinding || DH || backItemBinding == null) {
                return;
            }
            position = p;
//            backItemBinding.getRoot().setEnabled(false);
            AnimatorSet animatorSet = new AnimatorSet();
            {
                DH = true;
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(backItemBinding.getRoot(), "scaleX", 1.1f, 1f);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(backItemBinding.getRoot(), "scaleY", 1.1f, 1f);
                backItemBinding = viewBinding;
                ObjectAnimator scaleXB = ObjectAnimator.ofFloat(backItemBinding.getRoot(), "scaleX", 1f, 1.1f);
                ObjectAnimator scaleYB = ObjectAnimator.ofFloat(backItemBinding.getRoot(), "scaleY", 1f, 1.1f);
                animatorSet.playTogether(scaleX, scaleY, scaleXB, scaleYB);
                animatorSet.setDuration(300);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        DH = false;

                    }
                });
                animatorSet.start();
            }
            onclick(data);
        });
        viewBinding.image.setImageBitmap(data);
//        viewBinding.getRoot().invalidate();
    }

    public void onclick(Bitmap bitmap) {

    }
}

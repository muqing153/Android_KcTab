package com.muqing.ViewUI;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import com.google.android.material.bottomsheet.BottomSheetDragHandleView;

public class BottomSheet extends BottomSheetDragHandleView {
    private float initialX = 0f;
    private float lastTouchX = 0f;
    private float maxXOffset = 50f; // 最大偏移量
    private boolean isScaling = false;

    public BottomSheet(@NonNull Context context) {
        super(context);
    }

    public BottomSheet(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BottomSheet(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        maxXOffset = parentWidth / 4f;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = getTranslationX();
                    lastTouchX = event.getRawX();
                    if (!isScaling) {
                        animate().scaleX(1.2f).scaleY(1.2f)
                                .setDuration(200)
                                .setInterpolator(new FastOutSlowInInterpolator())
                                .withLayer()
                                .start();
                        isScaling = true;
                    }
                    break;

                case MotionEvent.ACTION_MOVE:
                    float dx = (event.getRawX() - lastTouchX) * 0.3f; // 调整移动速度
                    float fl = initialX + dx;
                    if (fl < maxXOffset && fl > -maxXOffset) {
                        setTranslationX(fl);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    AnimatorSet animatorSet = new AnimatorSet();

                    ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(this, "scaleX", 1f);
                    scaleXAnimator.setDuration(200);
                    scaleXAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

                    ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(this, "scaleY", 1f);
                    scaleYAnimator.setDuration(200);
                    scaleYAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

                    ObjectAnimator translationAnimator = ObjectAnimator.ofFloat(this, "translationX", 0f);
                    translationAnimator.setDuration(200);
                    translationAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

                    animatorSet.playTogether(scaleXAnimator, scaleYAnimator, translationAnimator);
                    animatorSet.start();
                    animatorSet.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {}

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            isScaling = false;
                            setTranslationX(0f);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {}

                        @Override
                        public void onAnimationRepeat(Animator animation) {}
                    });
                    break;
            }
        }
        return true;
    }
}

package com.muqing.kctab;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.HorizontalScrollView;

public class SmartHorizontalScrollView extends HorizontalScrollView {

    private float startX;
    private float startY;
    private final int touchSlop;

    public SmartHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = ev.getX();
                startY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(ev.getX() - startX);
                float dy = Math.abs(ev.getY() - startY);
                if (dx > dy && dx > touchSlop) {
                    return true; // 横向滑动时拦截
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}

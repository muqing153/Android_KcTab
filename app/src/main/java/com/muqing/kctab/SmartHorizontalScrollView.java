package com.muqing.kctab;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.HorizontalScrollView;

public class SmartHorizontalScrollView extends HorizontalScrollView {

    private float startX, startY;
    private final int touchSlop;
    private boolean isBeingDragged;

    public SmartHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startX = ev.getX();
                startY = ev.getY();
                isBeingDragged = false;
                // 默认不允许 ViewPager2 拦截事件
                getParent().requestDisallowInterceptTouchEvent(true);
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = ev.getX() - startX;
                float dy = Math.abs(ev.getY() - startY);

                if (Math.abs(dx) > touchSlop && Math.abs(dx) > dy) {
                    if ((dx > 0 && isAtLeftEdge()) || (dx < 0 && isAtRightEdge())) {
                        // 滑动到最左或最右边界，允许 ViewPager2 处理滑动
                        getParent().requestDisallowInterceptTouchEvent(false);
                        return false; // 不拦截，让父 View（ViewPager2）接管
                    } else {
                        // 自身处理滑动
                        getParent().requestDisallowInterceptTouchEvent(true);
                        isBeingDragged = true;
                        return true;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                isBeingDragged = false;
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    private boolean isAtLeftEdge() {
        return getScrollX() == 0;
    }

    private boolean isAtRightEdge() {
        return getScrollX() >= getChildAt(0).getWidth() - getWidth();
    }
}

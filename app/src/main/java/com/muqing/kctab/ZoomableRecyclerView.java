package com.muqing.kctab;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class ZoomableRecyclerView extends RecyclerView {
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private Matrix matrix;
    private float scaleFactor = 1.0f;
    private PointF translation = new PointF(0, 0);

    public ZoomableRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public ZoomableRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomableRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        matrix = new Matrix();
        initGestureDetectors(context);
    }

    private void initGestureDetectors(Context context) {
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scaleFactor *= detector.getScaleFactor();
                scaleFactor = Math.max(0.5f, Math.min(scaleFactor, 3.0f)); // 限制缩放范围
                invalidate();
                return true;
            }
        });

        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
                translation.x -= distanceX / scaleFactor; // 平移需要考虑缩放比例
                translation.y -= distanceY / scaleFactor;
                invalidate();
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        scaleGestureDetector.onTouchEvent(e);
        gestureDetector.onTouchEvent(e);
        return super.onTouchEvent(e);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        matrix.reset();
        matrix.postScale(scaleFactor, scaleFactor);
        matrix.postTranslate(translation.x * scaleFactor, translation.y * scaleFactor);
//        canvas.concat(matrix);
        super.dispatchDraw(canvas);
//        canvas.restore();
    }
}

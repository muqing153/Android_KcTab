package com.muqing.kctab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class Text extends AppCompatTextView {

    public Text(Context context) {
        this(context, null);
    }

    public Text(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public Text(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 获取当前TextView的Paint对象
        TextPaint textPaint = getPaint();

        // 保存原来的文本颜色
        int currentTextColor = getCurrentTextColor();
        // 设置描边效果
        textPaint.setStyle(Paint.Style.STROKE);  // 设置为描边
        textPaint.setStrokeWidth(1);  // 设置描边的宽度
        //灰色
        setTextColor(MainActivity.backgroundColor);
        // 绘制描边
        super.onDraw(canvas);

        // 恢复原来的文本颜色并绘制文本内容
        textPaint.setStyle(Paint.Style.FILL);  // 恢复为填充
        setTextColor(currentTextColor);        // 恢复原来的颜色
        super.onDraw(canvas);
    }
}

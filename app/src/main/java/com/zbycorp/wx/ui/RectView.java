package com.zbycorp.wx.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

/**
 * Description：
 *
 * @author:hongb
 * @date:2023/12/9
 */
public class RectView extends View {
    private Paint mPaint;
    private Rect mRect;

    public RectView(Context context) {
        this(context,null);
    }

    public RectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setFakeBoldText(true);
        setBackgroundColor(Color.TRANSPARENT);
    }

    public void setRect(Rect rect) {
        this.mRect = rect;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mRect != null) {
            Log.i("RectView","绘制矩形");
            canvas.drawRect(mRect, mPaint);
//            layout(mRect.left, mRect.top, mRect.right,mRect.right);
        }
    }
}

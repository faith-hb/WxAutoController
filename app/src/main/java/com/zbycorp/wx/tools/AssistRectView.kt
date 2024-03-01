package com.zbycorp.wx.tools

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View

/**
 * 自定义View：根据rect绘制view
 * @author:hongb
 * @date:2024/2/29
 */
class AssistRectView(context: Context, private val rect: Rect) : View(context) {
    private val paint = Paint()

    init {
        // 设置画笔颜色
        paint.color = Color.RED
        paint.strokeWidth = 4f
        // 设置画笔风格为描边
        paint.style = Paint.Style.STROKE
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRect(rect, paint)
    }
}
package com.zbycorp.wx.capture

import android.content.res.Resources
import android.graphics.Bitmap
import android.media.Image
import java.nio.ByteBuffer

/**
 * 屏幕工具类
 * @author:hongb
 * @date:2024/3/4
 */
object CaptureUtils {
    fun getScreenWidth(): Int {
        return Resources.getSystem().displayMetrics.widthPixels
    }

    fun getScreenHeight(): Int {
        return Resources.getSystem().displayMetrics.heightPixels
    }

    fun getScreenDensityDpi(): Int {
        return Resources.getSystem().displayMetrics.densityDpi
    }

    /**
     * 将 Image 转换成 Bitmap 对象
     */
    fun imageToBitmap(image: Image): Bitmap {
        val width = image.width
        val height = image.height
        val planes = image.planes
        val buffer: ByteBuffer = planes[0].buffer
        // 两个像素的距离
        val pixelStride = planes[0].pixelStride
        // 整行的距离
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * width
        var bitmap = Bitmap.createBitmap(
            width + rowPadding / pixelStride,
            image.height,
            Bitmap.Config.ARGB_8888
        )
        bitmap.copyPixelsFromBuffer(buffer)
        image.close()
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height)
        return bitmap
    }
}
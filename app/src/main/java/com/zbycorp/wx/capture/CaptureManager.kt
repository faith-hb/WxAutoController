package com.zbycorp.wx.capture

import android.app.Activity
import android.content.Intent
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.zbycorp.wx.ui.MainActivity

/**
 * Description：
 * @author:hongb
 * @date:2024/3/4
 */
class CaptureManager private constructor() {
    private var mediaProjectionManager: MediaProjectionManager? = null
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null

    companion object {
        const val TAG = "CaptureManager"
        const val REQUEST_MEDIA_PROJECTION = 1
        const val DISPLAY_NAME = "AiScreenCapture"
        val instance: CaptureManager by lazy { CaptureManager() }
    }

    init {
        // 初始化代码
        imageReader =
            ImageReader.newInstance(
                CaptureUtils.getScreenWidth(),
                CaptureUtils.getScreenHeight(),
                PixelFormat.RGBA_8888,
                1
            )
    }

    /**
     * 获取MediaProjectionManager对象
     * @param activity
     */
    private fun getMpm(activity: Activity): MediaProjectionManager? {
        if (mediaProjectionManager == null) {
            mediaProjectionManager =
                activity.getSystemService(AppCompatActivity.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        }
        return mediaProjectionManager
    }

    /**
     * 开始截屏
     * @param activity
     */
    fun startCapture(activity: Activity, iv: ImageView? = null) {
        Log.d(TAG, "Starting screen capture")
        if (mediaProjection == null) {
            val mpm = getMpm(activity)
            activity.startActivityForResult(
                mpm?.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION
            )
        } else {
            handleCapture(iv)
        }
    }

    /**
     * 绑定OnActivityResult
     * @param resultCode
     * @param data
     */
    fun bindOnActivityResult(activity: Activity, resultCode: Int, data: Intent?) {
        val mpm = getMpm(activity)
        mediaProjection = mpm?.getMediaProjection(resultCode, data)
    }

    /**
     * 处理截图
     * @param iv 可选参数
     */
    fun handleCapture(iv: ImageView? = null) {
        Log.d(MainActivity.TAG, "Handling screen capture")
        virtualDisplay = mediaProjection?.createVirtualDisplay(
            DISPLAY_NAME,
            CaptureUtils.getScreenWidth(),
            CaptureUtils.getScreenHeight(),
            CaptureUtils.getScreenDensityDpi(),
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface,
            null, null
        )
        iv?.let {
            Handler(Looper.getMainLooper()).postDelayed({
                val image = imageReader?.acquireLatestImage()
                if (image != null) {
                    Log.d(TAG, "get image: $image")
                    it.setImageBitmap(CaptureUtils.imageToBitmap(image))
                } else {
                    Log.d("~~~", "image == null")
                }
                stopScreenCapture()
            }, 5000)
        }
    }

    /**
     * 停止截图
     */
    private fun stopScreenCapture() {
        Log.i(TAG, "stopScreenCapture, virtualDisplay = $virtualDisplay")
        virtualDisplay?.release()
        virtualDisplay = null
    }
}
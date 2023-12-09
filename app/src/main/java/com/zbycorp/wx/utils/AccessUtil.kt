package com.zbycorp.wx.utils

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Path
import android.graphics.Point
import android.graphics.Rect
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView
import android.widget.Toast
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.anim.DefaultAnimator
import com.lzf.easyfloat.enums.ShowPattern
import com.zbycorp.wx.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("StaticFieldLeak")
internal object AccessUtil {

    const val TAG = "AccessUtil"

    /**
     * textView
     */
    val TEXT_VIEW = "android.widget.TextView"
    /**
     * buttonView
     */
    val BUTTON_VIEW = "android.widget.Button"
    /**
     * 输入框
     */
    val EDIT_TEXT = "android.widget.EditText"
    /**
     * WebView
     */
    val WEBVIEW = "android.webkit.WebView"

    fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    /**
     * Gesture手势实现滚动(Android7+)
     * 解决滚动距离不可控制问题
     * @param distanceX 向右滚动为负值 向左滚动为正值
     * @param distanceY 向下滚动为负值 向上滚动为正值
     */
    fun scrollByNode(
        service: AccessibilityService,
        nodeInfo: AccessibilityNodeInfo,
        distanceX: Int = 0,
        distanceY: Int = 0
    ): Boolean {
        val rect = Rect()
        nodeInfo.getBoundsInScreen(rect)
        val point = Point((rect.left + rect.right) / 2, (rect.top + rect.bottom) / 2)
        val builder = GestureDescription.Builder()
        val path = Path()
        path.moveTo(point.x.toFloat(), point.y.toFloat())
        path.lineTo(point.x.toFloat() + distanceX, point.y.toFloat() + distanceY)
//        path.moveTo(point.x.toFloat(), 500f)
//        path.lineTo(point.x.toFloat() + distanceX, 100f)
        Log.e(
            TAG,
            "moveStartX=${point.x.toFloat()} moveStartY=${point.y.toFloat()} " +
                    "to " +
                    "moveEndX=${point.x.toFloat() + distanceX} moveEndY=${point.y.toFloat() + distanceY}"
        )
        builder.addStroke(GestureDescription.StrokeDescription(path, 50L, 520L))
        val gesture = builder.build()
        return service.dispatchGesture(
            gesture,
            object : AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription) {
                    Log.e(TAG, "scroll ok onCompleted")
                }

                override fun onCancelled(gestureDescription: GestureDescription) {
                    Log.e(TAG, "scroll ok onCancelled")
                }
            },
            null
        )
    }

    fun mockClkByNode(
        service: AccessibilityService,
        rect: Rect
    ): Boolean {
//        val point = Point((rect.left + rect.right) / 2, (rect.top + rect.bottom) / 2)
        val point = Point(rect.right - 10, (rect.top + rect.bottom) / 2)
        val builder = GestureDescription.Builder()
        val path = Path()
        path.moveTo(point.x.toFloat(), point.y.toFloat())
        Log.e(
            TAG,
            "moveStartX=${point.x.toFloat()} moveStartY=${point.y.toFloat()}"
        )
        builder.addStroke(GestureDescription.StrokeDescription(path, 50L, 520L))
        val gesture = builder.build()
        return service.dispatchGesture(
            gesture,
            object : AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription) {
                    Log.e(TAG, "click ok onCompleted")
                }

                override fun onCancelled(gestureDescription: GestureDescription) {
                    Log.e(TAG, "click ok onCancelled")
                }
            },
            null
        )
    }

    /**
     * 填充EditText
     *
     * @param service
     * @param viewId
     * @param content
     */
    fun fillInput(service: AccessibilityService, viewId: String, content: String?) {
        val viewList = findNodesByViewId(service, viewId)
        if (viewList?.isNotEmpty() == true) {
            for (accessibilityNodeInfo in viewList) {
                if (accessibilityNodeInfo.className == EDIT_TEXT && accessibilityNodeInfo.isEnabled) {
                    val clip = ClipData.newPlainText("label", content)
                    val clipboardManager =
                        service.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboardManager.primaryClip = clip
                    accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE)
                }
            }
        }
    }

    fun fillInput(
        service: AccessibilityService,
        accessibilityNodeInfo: AccessibilityNodeInfo,
        content: String?
    ) {
        if (accessibilityNodeInfo.className == EDIT_TEXT && accessibilityNodeInfo.isEnabled) {
            val clip = ClipData.newPlainText("label", content)
            val clipboardManager =
                service.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.primaryClip = clip
            accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE)
        }
    }

    /**
     * 查找控件并点击其父控件
     *
     * @param service
     * @param text
     */
    fun findViewAndPerformClickParentByText(service: AccessibilityService, text: String?) {
        val viewList = findNodesByText(service, text)
        if (viewList?.isNotEmpty() == true) {
            for (i in viewList.indices) {
                //微信7.0.4版本特殊处理，7.0.4只能从父控件点击，然后这个通过当前页面文案来查找控件，要排除输入框的内容
                val node = viewList[i]
                val parentNode = node.parent

                if (node.text.toString() == text && node.className == TEXT_VIEW) {
                    parentNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    break
                }
            }
        }
    }

    /**
     * 查找控件并点击其父控件
     *
     * @param viewId
     */
    fun findViewAndPerformClickParent(service: AccessibilityService, viewId: String) {
        val viewList = findNodesByViewId(service, viewId)
        if (viewList?.isNotEmpty() == true) {
            for (i in viewList.indices) {
                val nodeInfo = viewList[i].parent
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
        }
    }

    /**
     * 查找节点并模拟点击
     *
     * @param viewId
     */
    fun findViewIdAndGetText(service: AccessibilityService, viewId: String) {
        val viewList = findNodesByViewId(service, viewId)
        if (viewList?.isNotEmpty() == true) {
            for (accessibilityNodeInfo in viewList) {
                // 获取当前文本内容
                Log.i(TAG, "accessibilityNodeInfo：${accessibilityNodeInfo.text}")
            }
        }
    }

    /**
     * 查找节点并模拟点击
     *
     * @param viewId
     */
    fun findViewIdAndPerformClick(service: AccessibilityService, viewId: String) {
        val viewList = findNodesByViewId(service, viewId)
        if (viewList?.isNotEmpty() == true) {
            for (accessibilityNodeInfo in viewList) {
                accessibilityNodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
            }
        }
    }

    /**
     * 通过viewId查找所有节点
     *
     * @param service
     * @param viewId
     * @return
     */
    fun findNodesByViewId(service: AccessibilityService, viewId: String?): List<AccessibilityNodeInfo>? {
        return if (service.rootInActiveWindow != null) service.rootInActiveWindow.findAccessibilityNodeInfosByViewId(
            viewId
        ) else null
    }

    /**
     * 通过文本查找所有节点
     *
     * @param text
     * @return
     */
    fun findNodesByText(service: AccessibilityService, text: String?): List<AccessibilityNodeInfo>? {
        return if (service.rootInActiveWindow != null) service.rootInActiveWindow.findAccessibilityNodeInfosByText(text) else null
    }

    var mTargetTv: TextView? = null
    fun showWindowTips(context: Activity) {
        var layout = LayoutInflater.from(context).inflate(R.layout.pop_window, null)
        mTargetTv = layout.findViewById(R.id.tv_tips)
        EasyFloat.with(context).setShowPattern(ShowPattern.ALL_TIME).setLayout(layout)
            .setGravity(Gravity.BOTTOM,0,-190)
            .setMatchParent(widthMatch = true)
            .show()
    }

    fun dismissWindowTips() {
        EasyFloat.dismiss(force = true)
    }

    fun updateTips(tips: String) {
//        mTargetTv?.apply {
//            post {
//                Log.e(TAG,"tips内容：$tips")
//                text = tips
//            }
//        }
//        GlobalScope.launch(Dispatchers.Main) {
            Log.e(TAG,"tips内容：$tips")
            mTargetTv?.text = tips
//        }
    }
}

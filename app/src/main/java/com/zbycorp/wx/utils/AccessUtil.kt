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
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView
import android.widget.Toast
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.lzf.easyfloat.interfaces.OnFloatAnimator
import com.lzf.easyfloat.utils.DisplayUtils
import com.zbycorp.wx.R
import com.zbycorp.wx.tools.AssistRectView
import com.zbycorp.wx.ui.RectView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    private var mAct: Activity? = null

    fun bindActivity(act: Activity?) {
        mAct = act
    }

    fun getActivity(): Activity? {
        return mAct
    }

    fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun getStatusBarHeight(context: Context): Int {
        var statusBarHeight = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
        }
        return statusBarHeight
    }

    /**
     * Gesture手势实现滚动(Android7+)
     * 解决滚动距离不可控制问题
     * @param distanceX 向右滚动为负值 向左滚动为正值
     * @param distanceY 向上滚动为负值 向下滚动为正值
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
        // Android7中新加入的API
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
        rect: Rect,
        isSend: Boolean = false, isLongClk: Boolean = false
    ): Boolean {
        var point = Point((rect.left + rect.right) / 2, (rect.top + rect.bottom) / 2)
        if (isSend) {
            point = Point(rect.right - 10, (rect.top + rect.bottom) / 2)
        }
        val path = Path()
        val pointX = point.x.toFloat()
        val pointY = point.y.toFloat()
        if (pointX < 0 || pointY < 0) {
            Log.e(TAG, "mockClkByNode=>path路径不能为负数")
            return false
        }
        path.moveTo(pointX, pointY)
//        path.lineTo(pointX, pointY)
//        if (isSend) {
//            path.moveTo(pointX, pointY)
//            path.lineTo(pointX, pointY)
//        } else {
//            path.moveTo(pointX, pointY)
//        }
        Log.e(TAG, "mockClkByNode=>pointX=$pointX pointY=$pointY")
        // duration参数设置320会触发长按事件
        val gesture = GestureDescription.Builder().addStroke(
            GestureDescription.StrokeDescription(
                path, 0L, if (isLongClk) 320L else 120L
            )
        ).build()
        return service.dispatchGesture(
            gesture,
            object : AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription) {
                    Log.i(TAG, "mockClkByNode=>click ok onCompleted pointX=$pointX pointY=$pointY")
                }

                override fun onCancelled(gestureDescription: GestureDescription) {
                    Log.i(TAG, "mockClkByNode=>click ok onCancelled")
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

    fun getNodeRect(nodeInfo: AccessibilityNodeInfo): Rect? {
        if (nodeInfo == null) return null
        val rect = Rect()
        nodeInfo.toString().apply {
            val boundsInScreen = substring(
                indexOf("boundsInScreen: Rect") + "boundsInScreen: Rect".length,
                indexOf(
                    ";",
                    indexOf("boundsInScreen: Rect")
                )
            )
            Log.i(
                TAG,
                "getNodeRect：nodeInfo=$nodeInfo"
            )
            Log.i(
                TAG,
                "getNodeRect：boundsInScreen=$boundsInScreen"
            )
            boundsInScreen.apply {
                var indexS = 0
                var left =
                    substring(
                        indexOf("(") + 1,
                        indexOf(",")
                    ).toInt()
                var top =
                    substring(
                        indexOf(", ") + 2,
                        indexOf(" -")
                    ).toInt()
                indexS = indexOf(",", indexOf("- "))
                var right =
                    substring(
                        indexOf("- ") + 2,
                        indexS
                    ).toInt()
                var bottom =
                    substring(
                        indexOf(
                            ", ",
                            indexS
                        ) + 2,
                        indexOf(")")
                    ).toInt()
//                Log.i(
//                    TAG,
//                    "getNodeRect：商品item按钮坐标 left=$left top=$top right=$right bottom=$bottom"
//                )
                rect.left = left
                rect.top = top
                rect.right = right
                rect.bottom = bottom

//                Log.i(
//                    TAG,
//                    "getNodeRect：商品item中讲解按钮坐标 left=${rect.left} top=${rect.top} right=${rect.right} bottom=${rect.bottom}"
//                )
            }
        }
        return rect
    }

    fun traverseNodeByTxt(
        service: AccessibilityService,
        node: AccessibilityNodeInfo?,
        txt: String,
        isExecuteClk: Boolean = false
    ) {
        if (node == null) return
        var isExist = false
        // 处理当前节点，满足节点条件即终止遍历
        if (node.text != null && TextUtils.equals(node.text, txt)) {
            isExist = true
            updateTips("锁定目标")
            Log.i(DyAccessUtil.TAG, "traverseNodeCenter：找到【$txt】节点")
            val rect = getNodeRect(node)
            if (rect != null) {
                mockClkByNode(service, rect)
            }
        }
//        Log.i(TAG, "traverseNodeCenter：node.text=${node.text} isExist=$isExist")
        for (i in 0 until node.childCount) {
            if (isExist) {
                break
            }
            val childNode = node.getChild(i)
            traverseNodeByTxt(service, childNode, txt, isExecuteClk)
        }
    }

    fun showAssistBox(context: Activity, rect: Rect) {
        val layout = AssistRectView(context, rect)
        layout.setOnClickListener {
            dismissAssistBox()
        }
        EasyFloat.with(context).setShowPattern(ShowPattern.ALL_TIME).setLayout(layout)
            .setMatchParent(widthMatch = true)
            .setGravity(
                Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK,
                offsetY = -DisplayUtils.getStatusBarHeight(context)
            )
            .setDisplayHeight { context ->
                DisplayUtils.getScreenHeight(context)
            }
            .setTag("assistBox")
            .show()
    }

    fun dismissAssistBox() {
        EasyFloat.dismiss(tag = "assistBox", force = true)
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
        Log.e(TAG, "tips内容：$tips")
        mTargetTv?.text = tips
    }
}

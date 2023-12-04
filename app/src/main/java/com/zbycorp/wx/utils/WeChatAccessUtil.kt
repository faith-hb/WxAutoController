package com.zbycorp.wx.utils

import android.accessibilityservice.AccessibilityService
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import com.zbycorp.wx.access.WeChatAccessService.Companion.TAG
import com.zbycorp.wx.contants.KsResId
import java.util.*

internal object WeChatAccessUtil {
    /**
     * textView
     */
    private val TEXT_VIEW = "android.widget.TextView"
    /**
     * 输入框
     */
    private val EDIT_TEXT = "android.widget.EditText"

    fun openKsApp(activity: Activity) {
        val intent = Intent()
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        intent.setClassName(
            KsResId.KS_PACKAGE,
            "com.yxcorp.gifshow.HomeActivity"
        )
        activity.startActivity(intent)
    }

    fun openMockApp(activity: Activity) {
        val intent = Intent()
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        intent.setClassName(
            "com.hongb.funcdemo",
            "com.hongb.funcdemo.lib.thread.InterfaceActivity"
        )
        activity.startActivity(intent)
    }

    @Throws(InterruptedException::class)
    fun mockSendMessage(service: AccessibilityService) {
//        val viewList = findNodesByViewId(
//            service,
//            SEARCH_ID
//        )
        Thread.sleep(500)
        findViewIdAndPerformClick(
            service,
            "com.hongb.funcdemo:id/addDataBtn"
        )
        Thread.sleep(2500)
        findViewIdAndPerformClick(
            service,
            "com.hongb.funcdemo:id/getDataBtn"
        )
    }

    @Throws(InterruptedException::class)
    fun mockKsMessage(service: AccessibilityService) {

        Thread.sleep(500)
        findViewIdAndGetText(
            service,
            KsResId.LIVE_PAGE.AUDIENCE_COUNT
        )
//        Thread.sleep(1500)
//        fillInput(
//            service,
//            "com.smile.gifmaker:id/editor",
//            "谢谢进入直播间"
//        )
//        Thread.sleep(1500)
//        findViewIdAndPerformClick(
//            service,
//            "com.smile.gifmaker:id/finish_button"
//        )

//        Thread.sleep(500)
//        Log.i("助手", "mock快手开播点击")
//        findViewIdAndPerformClick(
//            service,
//            "com.smile.gifmaker:id/shoot_container"
//        )
//        Thread.sleep(2500)
//        Log.i("助手", "mock快手搜索点击")
//        findViewIdAndPerformClick(
//            service,
//            "com.smile.gifmaker:id/search_btn"
//        )
    }

    /**
     * 填充EditText
     *
     * @param service
     * @param viewId
     * @param content
     */
    private fun fillInput(service: AccessibilityService, viewId: String, content: String?) {
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

    /**
     * 查找控件并点击其父控件
     *
     * @param service
     * @param text
     */
    private fun findViewAndPerformClickParentByText(service: AccessibilityService, text: String?) {
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
    private fun findViewAndPerformClickParent(service: AccessibilityService, viewId: String) {
        val viewList = findNodesByViewId(service, viewId)
        if (viewList?.isNotEmpty() == true) {
            for (i in viewList.indices) {
                //微信7.0.4版本特殊处理
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
    private fun findViewIdAndGetText(service: AccessibilityService, viewId: String) {
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
    private fun findViewIdAndPerformClick(service: AccessibilityService, viewId: String) {
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
    private fun findNodesByViewId(service: AccessibilityService, viewId: String?): List<AccessibilityNodeInfo>? {
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
    private fun findNodesByText(service: AccessibilityService, text: String?): List<AccessibilityNodeInfo>? {
        return if (service.rootInActiveWindow != null) service.rootInActiveWindow.findAccessibilityNodeInfosByText(text) else null
    }
}

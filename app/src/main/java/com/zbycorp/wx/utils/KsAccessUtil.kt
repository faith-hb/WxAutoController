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
import android.widget.Toast
import com.zbycorp.wx.contants.KsResId
import java.util.*

internal object KsAccessUtil {
    const val TAG = "快手"
    /**
     * textView
     */
    private val TEXT_VIEW = "android.widget.TextView"
    /**
     * 输入框
     */
    private val EDIT_TEXT = "android.widget.EditText"

    /**
     * 他人主页的自动化是否执行完毕
     */
    private var userProfileIsExecuteFinish = false

    /**
     * 私信页面的自动化是否执行完毕
     */
    private var imChatIsExecuteFinish = false

    fun openKsApp(activity: Activity) {
        val intent = Intent()
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        intent.setClassName(
            KsResId.KS_PACKAGE,
            "com.yxcorp.gifshow.HomeActivity"
        )
        activity.startActivity(intent)
    }

    @Throws(InterruptedException::class)
    fun liveKsMessage(service: AccessibilityService) {
        // 获取控件内容
        Thread.sleep(500)
        val viewList = findNodesByViewId(service, KsResId.LIVE_PAGE.AUDIENCE_COUNT)
        if (viewList?.isNotEmpty() == true && viewList.size == 1) {
            Toast.makeText(
                service.applicationContext,
                "观看人数：${viewList[0].text}",
                Toast.LENGTH_SHORT
            ).show()
        }
        // 填充输入框
        Thread.sleep(1500)
        fillInput(
            service,
            KsResId.LIVE_PAGE.EDITOR,
            "欢迎进入直播间"
        )
        // 发送弹幕消息
        Thread.sleep(2500)
        findViewIdAndPerformClick(
            service,
            KsResId.LIVE_PAGE.FINISH_BUTTON
        )

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

    @Throws(InterruptedException::class)
    fun userProfileKsMessage(service: AccessibilityService) {
        if (userProfileIsExecuteFinish) {
            Log.i(TAG, "他人主页的自动化操作已结束")
            return
        }
        // 点击关注
        Thread.sleep(500)
        findViewIdAndPerformClick(service, KsResId.USER_PROFILE_PAGE.HEADER_FOLLOW_BUTTON)
        // 进入发送私信页面
        Thread.sleep(2500)
        findViewIdAndPerformClick(service, KsResId.USER_PROFILE_PAGE.SEND_MESSAGE)
        userProfileIsExecuteFinish = true
    }

    @Throws(InterruptedException::class)
    fun imChatKsMessage(service: AccessibilityService) {
        if (imChatIsExecuteFinish) {
            Log.i(TAG, "发送私信自动化操作已结束")
            return
        }
        // 点击输入框，输入填入内容
        Thread.sleep(1500)
        findViewIdAndPerformClick(service, KsResId.IM_CHAT_PAGE.EDITOR)
        Thread.sleep(1000)
        fillInput(service, KsResId.IM_CHAT_PAGE.EDITOR, "你好，你好")
        // 发送内容
        Thread.sleep(2500)
        findViewIdAndPerformClick(service, KsResId.IM_CHAT_PAGE.SEND_BTN)
        imChatIsExecuteFinish = true
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

package com.zbycorp.wx.utils

import android.accessibilityservice.AccessibilityService
import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import com.zbycorp.wx.contants.KsResId
import com.zbycorp.wx.utils.AccessUtil.fillInput
import com.zbycorp.wx.utils.AccessUtil.findNodesByViewId
import com.zbycorp.wx.utils.AccessUtil.findViewIdAndPerformClick
import java.util.*

internal object KsAccessUtil {
    const val TAG = "快手"
    /**
     * 他人主页的自动化是否执行完毕
     */
    open var userProfileIsExecuteFinish = false

    /**
     * 私信页面的自动化是否执行完毕
     */
    open var imChatIsExecuteFinish = false

    fun openKsApp(activity: Activity) {
        val intent = Intent()
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        intent.setClassName(
            KsResId.KS_PACKAGE,
            KsResId.SPLASH_PAGE
        )
        activity.startActivity(intent)
    }

    @Throws(InterruptedException::class)
    fun liveKsMessage(service: AccessibilityService) {
        // 检查是否有托管弹窗
        Thread.sleep(3200)
        val viewEscrowList = findNodesByViewId(service, KsResId.LIVE_PAGE.START_ESCROW)
        if (viewEscrowList?.isNotEmpty() == true) {
            Log.i(TAG, "直播间页面-点击了【开始托管】")
            viewEscrowList[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
            // 有托管，下面的流程不执行
            return
        }
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

    }

    @Throws(InterruptedException::class)
    fun liveEscrowKsMessage(service: AccessibilityService) {
//        if (userProfileIsExecuteFinish) {
//            Log.i(TAG, "他人主页的自动化操作已结束")
//            return
//        }
        // 点击售卖商品
        Thread.sleep(1200)
        val sellViewList = findNodesByViewId(service, KsResId.LIVE_ESCROW_PAGE.SELL_GOODS)
        if (sellViewList?.isNotEmpty() == true && sellViewList.size > 3) {
            val nodeInfo = sellViewList[3].parent
            Log.i(TAG, "托管页面-点击了【售卖商品】 nodeInfo=$nodeInfo")
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
        // 点击商品管理
        Thread.sleep(2500)
        var viewList = findNodesByViewId(service, KsResId.LIVE_ESCROW_PAGE.GOODS_DIALOG)
        if (viewList?.isNotEmpty() == true) {
            val rootNodeInfo = viewList[0]
            Log.i(
                TAG,
                "托管页面-点击了【商品管理】viewList=${viewList.size} rootNodeInfo=${rootNodeInfo.childCount}"
            )
            if (rootNodeInfo.childCount > 4) {
                Log.i(TAG, "托管页面-点击了【商品管理】=${rootNodeInfo.getChild(4)}")
                rootNodeInfo.getChild(4).performAction(AccessibilityNodeInfo.ACTION_CLICK)

                Thread.sleep(1200)

            }
//
        }
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
}

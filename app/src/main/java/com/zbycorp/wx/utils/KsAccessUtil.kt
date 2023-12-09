package com.zbycorp.wx.utils

import android.accessibilityservice.AccessibilityService
import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Looper
import android.provider.Settings.Global
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import com.zbycorp.wx.contants.KsResId
import com.zbycorp.wx.utils.AccessUtil.TEXT_VIEW
import com.zbycorp.wx.utils.AccessUtil.fillInput
import com.zbycorp.wx.utils.AccessUtil.findNodesByText
import com.zbycorp.wx.utils.AccessUtil.findNodesByViewId
import com.zbycorp.wx.utils.AccessUtil.findViewIdAndPerformClick
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
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

    /**
     * 直播间页面
     */
    // 开始托管点击是否执行完毕
    open var startEscrowIsFinish = false
    // 是否发送完弹幕
    open var sendDmIsFinish = false

    /**
     * 托管页
     */
    // 售卖商品点击执行是否完毕
    open var sellGoodsIsFinish = false
    // 商品管理点击执行是否完毕
    open var goodsManagerIsFinish = false
    // 开始讲解点击执行是否完毕
    open var startExplainIsFinish = false

    // 关注是否执行完毕
    open var followIsExecuteFinish = false
    // 进入私信页面是否执行完毕
    open var enterMessageIsExecuteFinish = false


    fun openKsApp(activity: Activity) {
        val intent = Intent()
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        intent.setClassName(
            KsResId.KS_PACKAGE,
            KsResId.SPLASH_PAGE
        )
        activity.startActivity(intent)
    }

    fun liveKsMessage(service: AccessibilityService) {
        if (!startEscrowIsFinish) {
            AccessUtil.updateTips("进入直播间：准备演示商品自动化上架")
        }
        GlobalScope.launch(Dispatchers.Main) {
            delay(3200)
            // 托管
            if (!startEscrowIsFinish) {
                val viewEscrowList =
                    findNodesByViewId(service, KsResId.LIVE_PAGE.START_ESCROW)
                // 检查是否有托管弹窗
                if (viewEscrowList?.isNotEmpty() == true) {
                    Log.i(TAG, "直播间页面-点击了【开始托管】")
                    AccessUtil.updateTips("模拟点击：开始托管")
                    viewEscrowList[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    startEscrowIsFinish = true
                    // 有托管，下面的流程不执行
                    return@launch
                }
            }

            if (sendDmIsFinish) {
                Log.i(TAG, "直播间页面-弹幕发送过了，不要再继续发送")
                return@launch
            }
            // 编辑框内容写入
            findViewIdAndPerformClick(service, KsResId.LIVE_PAGE.COMMENT_TEXT_VIEW)
            Log.i(TAG, "直播间页面-准备发送弹幕")
            delay(2500)
            Log.i(TAG, "直播间页面-发送弹幕")
            fillInput(
                service,
                KsResId.LIVE_PAGE.EDITOR,
                "欢迎进入直播间"
            )

            // 发送弹幕
            delay(1200)
            sendDmIsFinish = true
            AccessUtil.updateTips("模拟点击：发送弹幕")
            findViewIdAndPerformClick(
                service,
                KsResId.LIVE_PAGE.FINISH_BUTTON
            )
        }
    }

    fun liveEscrowKsMessage(service: AccessibilityService) {
        Log.i(TAG, "liveEscrowKsMessage->>>>>>>>>>>>>>>>>>>>>>>>>>>start<<<<<<<<<<<<<<<<<<<<<<<")
        GlobalScope.launch(Dispatchers.Main) {
            if (!sellGoodsIsFinish) {
                delay(1600)
                AccessUtil.updateTips("模拟点击：售卖商品")
                val sellViewList = findNodesByText(service, "售卖商品")
                Log.i(
                    TAG,
                    "节点：nodeCount=${sellViewList?.size} 售卖商品 currIsMainThread=${Looper.getMainLooper().isCurrentThread}"
                )
                if (sellViewList?.isNotEmpty() == true) {
                    val node = sellViewList[0]
                    if (TEXT_VIEW == node.className.toString() && "售卖商品" == node.text.toString()) {
                        Log.i(TAG, "模拟点击：售卖商品")
                        node.parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                        sellGoodsIsFinish = true
                    }
                }
            }

            if (goodsManagerIsFinish) return@launch
            delay(2200)
            val viewList = findNodesByViewId(service, KsResId.LIVE_ESCROW_PAGE.GOODS_DIALOG)
            Log.i(TAG, "节点：nodeCount=${viewList?.size}")
            if (viewList?.isNotEmpty() == true) {
                for (index in viewList.indices) {
                    val nodeInfo1 = viewList[index]
//                            Log.i(TAG,"一级递归：childCount=${nodeInfo1.childCount} nodeInfo1=$nodeInfo1")
                    for (index2 in 0 until nodeInfo1.childCount) {
                        val nodeInfo2 = nodeInfo1.getChild(index2)
//                                Log.i(TAG,"二级递归：childCount=${nodeInfo2.childCount} nodeInfo2=$nodeInfo2")
                        for (index3 in 0 until nodeInfo2.childCount) {
                            val nodeInfo3 = nodeInfo2.getChild(index3)
//                                    Log.i(TAG,"三级递归：childCount=${nodeInfo3.childCount} index=$index3 clsName=${nodeInfo3.className} text=${nodeInfo3.text}")
//                                    if (TEXT_VIEW == nodeInfo3.className.toString()) {
//                                        if ("商品管理" == nodeInfo3.text.toString() && !goodsManagerIsFinish) {
//                                            Log.i(TAG, "三级递归：模拟点击【商品管理】")
//                                            nodeInfo3.parent.parent.performAction(
//                                                AccessibilityNodeInfo.ACTION_CLICK
//                                            )
//                                            goodsManagerIsFinish = true
//                                        }
//                                    }
                            for (index4 in 0 until nodeInfo3.childCount) {
                                val nodeInfo4 = nodeInfo3.getChild(index4) ?: continue
//                                        Log.i(
//                                            TAG,
//                                            "四级递归：childCount=${nodeInfo4.childCount} index=$index4 clsName=${nodeInfo4.className} text=${nodeInfo4.text}"
//                                        )
                                if (TEXT_VIEW == nodeInfo4.className.toString()) {
                                    if ("开始讲解" == nodeInfo4.text.toString() && !startExplainIsFinish) {
                                        AccessUtil.updateTips("模拟点击：开始讲解（第一个商品）")
                                        Log.i(
                                            TAG,
                                            "四级递归：模拟点击【开始讲解】index=$index4 parent=${nodeInfo4.parent.className}"
                                        )
                                        nodeInfo4.parent.performAction(
                                            AccessibilityNodeInfo.ACTION_CLICK
                                        )
                                        startExplainIsFinish = true
                                    }
                                }
                            }
                        }
                    }
                }
            }
            delay(3200)
            AccessUtil.updateTips("商品上架流程自动化演示结束")
        }

        Log.i(TAG, "liveEscrowKsMessage->>>>>>>>>>>>>>>>>>>>>>>>>>>end<<<<<<<<<<<<<<<<<<<<<<<")
    }

    fun userProfileKsMessage(service: AccessibilityService) {
        if (!followIsExecuteFinish) {
            AccessUtil.updateTips("进入他人主页：演示自动化关注和发送私信")
        }
        Log.i(TAG, "userProfileKsMessage->>>>>>>>>>>>>>>>>>>>>>>>>>>start<<<<<<<<<<<<<<<<<<<<<<<")
        GlobalScope.launch(Dispatchers.Main) {
            if (followIsExecuteFinish) return@launch
            delay(2200)
            AccessUtil.updateTips("模拟点击：关注")
            Log.i(TAG, "模拟点击：关注")
            findViewIdAndPerformClick(
                service,
                KsResId.USER_PROFILE_PAGE.HEADER_FOLLOW_BUTTON
            )
            followIsExecuteFinish = true

            if (enterMessageIsExecuteFinish) return@launch
            delay(2200)
            Log.i(TAG, "模拟点击：打开私信页面")
            var viewList = findNodesByViewId(
                service,
                KsResId.USER_PROFILE_PAGE.SEND_MESSAGE_SMALL_ICON
            )
            if (viewList.isNullOrEmpty()) {
                viewList =
                    findNodesByViewId(service, KsResId.USER_PROFILE_PAGE.SEND_MESSAGE)
            }
            if (viewList?.isNotEmpty() == true) {
                viewList[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
                enterMessageIsExecuteFinish = true
            }
        }

        Log.i(
            TAG,
            "userProfileKsMessage->>>>>>>>>>>>>>>>>>>>>>>>>>>>>end<<<<<<<<<<<<<<<<<<<<<<<<<<"
        )
    }

    fun imChatKsMessage(service: AccessibilityService) {
        if (imChatIsExecuteFinish) return
        Log.i(
            TAG,
            "imChatKsMessage->>>>>>>>>>>>>>>>>>>>>>>>>>>>>start<<<<<<<<<<<<<<<<<<<<<<<<<<"
        )
        AccessUtil.updateTips( "进入私信页面")
        GlobalScope.launch(Dispatchers.Main) {
            delay(3200)
            AccessUtil.updateTips("自动化填写文本框内容")
            Log.i(TAG, "模拟点击：输入框")
            // 点击编辑框
            findViewIdAndPerformClick(service, KsResId.IM_CHAT_PAGE.EDITOR)
            delay(1200)
            Log.i(TAG, "模拟输入：私信内容")
            // 往编辑框填写内容
            fillInput(service, KsResId.IM_CHAT_PAGE.EDITOR, "你好，你好")

            val mockSendSx = withContext(Dispatchers.Default) {
                delay(2500)
                "模拟点击：发送私信"
            }
            AccessUtil.updateTips(mockSendSx)
            Log.i(TAG, "模拟点击：发送私信")
            // 点击发送内容
            findViewIdAndPerformClick(service, KsResId.IM_CHAT_PAGE.SEND_BTN)
            imChatIsExecuteFinish = true
            delay(3200)
            AccessUtil.updateTips("关注 & 发私信自动化演示结束")
        }
        Log.i(
            TAG,
            "imChatKsMessage->>>>>>>>>>>>>>>>>>>>>>>>>>>>>end<<<<<<<<<<<<<<<<<<<<<<<<<<"
        )
    }
}

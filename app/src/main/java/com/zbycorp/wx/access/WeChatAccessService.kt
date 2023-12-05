package com.zbycorp.wx.access

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.zbycorp.wx.contants.KsResId
import com.zbycorp.wx.utils.KsAccessUtil
import com.zbycorp.wx.utils.KsAccessUtil.TAG
import java.lang.Exception

class WeChatAccessService : AccessibilityService() {

    companion object {
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
//        Log.i(TAG,"eventType=${event.eventType}")
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                Log.i(TAG,"TYPE_WINDOW_CONTENT_CHANGED->className=${event.className}")
            }
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> { // 界面变化事件
                Log.i(TAG,"className=${event.className}")
                when (event.className.toString()) {
                    KsResId.LIVE_PAGE -> {
                        Log.i(TAG, "进入快手直播间页面")
                        KsAccessUtil.liveKsMessage(this@WeChatAccessService)
                    }

                    KsResId.USER_PROFILE_PAGE -> {
                        Log.i(TAG, "进入快手他人主页页面")
                        KsAccessUtil.userProfileKsMessage(this@WeChatAccessService)
                    }

                    KsResId.IM_CHAT_PAGE -> {
                        Log.i(TAG, "进入快手会话（发私信）页面")
                        KsAccessUtil.imChatKsMessage(this@WeChatAccessService)
                    }
                    KsResId.LIVE_ESCROW_PAGE -> {
                        Log.i(TAG, "进入快手托管页面")
                        try {
                            KsAccessUtil.liveEscrowKsMessage(this@WeChatAccessService)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                var nodeInfo = event.source
//                Log.i(TAG, "模拟点击(来自监听Service的响应)：${nodeInfo?.text}")
            }
        }
    }

    override fun onInterrupt() {

    }
}

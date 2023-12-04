package com.zbycorp.wx.access

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.zbycorp.wx.utils.WeChatAccessUtil

class WeChatAccessService : AccessibilityService() {

    private val TAG = "助手"

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
//        Log.i(TAG,"eventType=${event.eventType}")
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> { // 界面变化事件
                Log.i(TAG,"className=${event.className}")
                when (event.className.toString()) {
                    WeChatAccessUtil.WECHAT_CLASS_LAUNCHUI -> { // 进入微信首页
                        try {
                            Log.i(TAG,"nameContentList长度=${WeChatAccessUtil.nameContentList?.size}")
//                            if (WeChatAccessUtil.nameContentList?.isNotEmpty() == true) {
                                WeChatAccessUtil.sendMessage(this@WeChatAccessService)
//                            }
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }
                    "com.hongb.funcdemo.lib.thread.InterfaceActivity" -> {
                        Log.i(TAG, "进入mockApp首页")
                        WeChatAccessUtil.mockSendMessage(this@WeChatAccessService)
                    }
                    "com.yxcorp.gifshow.HomeActivity" -> {
                        Log.i(TAG, "进入快手首页")
//                        WeChatAccessUtil.mockKsMessage(this@WeChatAccessService)
                    }
                    "com.yxcorp.gifshow.detail.PhotoDetailActivity" -> {
                        Log.i(TAG, "进入快手直播间页面")
                        WeChatAccessUtil.mockKsMessage(this@WeChatAccessService)
                    }
                    WeChatAccessUtil.WECHAT_CLASS_CHATUI -> Log.i(TAG, "微信聊天页面启动")
                }
            }
            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                var nodeInfo = event.source
                Log.i(TAG, "模拟点击(来自监听Service的响应)：${nodeInfo?.text}")
            }
        }


    }

    override fun onInterrupt() {

    }
}

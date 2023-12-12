package com.zbycorp.wx.access

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.webkit.WebView
import com.zbycorp.wx.contants.DyResId
import com.zbycorp.wx.contants.KsResId
import com.zbycorp.wx.utils.AccessUtil
import com.zbycorp.wx.utils.DyAccessUtil
import com.zbycorp.wx.utils.KsAccessUtil
import java.lang.Exception

class AccessService : AccessibilityService() {

    companion object {
        const val TAG = "无障碍服务"
    }

    override fun onServiceConnected() { // 开启无障碍权限时被调用
        super.onServiceConnected()
        Log.i(TAG,"onServiceConnected...")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
//        Log.i(TAG,"eventType=${event.eventType}")
        when (event.eventType) {
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {
                Log.i(TAG, "TYPE_WINDOW_CONTENT_CHANGED->className=${event.className}")
            }

            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> { // 界面变化事件
                Log.i(TAG, "pkgName=${event.packageName} className=${event.className}")
                when (event.packageName) {
                    KsResId.KS_PACKAGE -> {
                        handleKsEvent(event)
                    }

                    DyResId.DY_PACKAGE -> {
                        handleDyEvent(event)
                    }
                }
            }

            AccessibilityEvent.TYPE_VIEW_CLICKED -> {
                var nodeInfo = event.source
//                Log.i(TAG, "模拟点击(来自监听Service的响应)：${nodeInfo?.text}")
                if (nodeInfo != null && "android.webkit.WebView" == nodeInfo.className) {
                    Log.i(TAG, "webview")
                    val webView = nodeInfo.parent as WebView
//                    var rootNode = webView.accessibilityNodeProvider.createAccessibilityNodeInfo()
                }
            }
        }
    }

    private fun handleKsEvent(event: AccessibilityEvent) {
        when (event.className.toString()) {
            KsResId.SPLASH_PAGE -> {
                AccessUtil.updateTips("来到快手首页")
            }
            KsResId.LIVE_PAGE -> {
                Log.w(TAG, "进入快手直播间页面")
                KsAccessUtil.liveKsMessage(this@AccessService)
            }

            KsResId.USER_PROFILE_PAGE -> {
                Log.w(TAG, "进入快手他人主页页面")
                KsAccessUtil.userProfileKsMessage(this@AccessService)
            }

            KsResId.IM_CHAT_PAGE -> {
                Log.w(TAG, "进入快手会话（发私信）页面")
                KsAccessUtil.imChatKsMessage(this@AccessService)
            }

            KsResId.LIVE_ESCROW_PAGE -> {
                Log.w(TAG, "进入快手托管页面")
                try {
                    KsAccessUtil.liveEscrowKsMessage(this@AccessService)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun handleDyEvent(event: AccessibilityEvent) {
        when (event.className.toString()) {
            DyResId.MAIN_PAGE -> {
                AccessUtil.updateTips("进入首页")
            }

            "com.ss.android.ugc.aweme.live.LiveDummyActivity" -> {
                AccessUtil.updateTips("进入电商带货页面")
            }

            DyResId.LIVE_PAGE -> {

            }

            DyResId.LIVE_CENTER_CONTROL_PAGE -> {
                AccessUtil.updateTips("进入直播中控页面")
                serviceInfo.flags =
                    serviceInfo.flags or AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY
                DyAccessUtil.liveCenterControlMessage(this@AccessService)
            }

            else -> {
                DyAccessUtil.commentIsExecuteFinish = false
            }
        }
    }

    override fun onInterrupt() {
        Log.w(TAG, "onInterrupt...")
    }

    override fun onDestroy() {
        super.onDestroy()

    }
}

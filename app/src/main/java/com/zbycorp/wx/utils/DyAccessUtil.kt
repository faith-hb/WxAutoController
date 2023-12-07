package com.zbycorp.wx.utils

import android.accessibilityservice.AccessibilityService
import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import com.zbycorp.wx.contants.DyResId
import com.zbycorp.wx.utils.AccessUtil.TEXT_VIEW
import com.zbycorp.wx.utils.AccessUtil.findNodesByViewId
import java.util.*

internal object DyAccessUtil {
    const val TAG = "抖音"

    /**
     * 他人主页的自动化是否执行完毕
     */
    private var userProfileIsExecuteFinish = false

    /**
     * 去开播自动化是否执行完毕
     */
    private var goOpenIsExecuteFinish = false

    fun openDyApp(activity: Activity) {
        val intent = Intent()
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        intent.setClassName(
            DyResId.DY_PACKAGE,
            DyResId.SPLASH_PAGE
        )
        activity.startActivity(intent)
    }

    @Throws(InterruptedException::class)
    fun liveMessage(service: AccessibilityService) {

    }

    @Throws(InterruptedException::class)
    fun liveCenterControlMessage(service: AccessibilityService) {
        Thread.sleep(3200)
        var viewList = findNodesByViewId(service, DyResId.LIVE_CENTER_CONTROL_PAGE.ROOT_ID)
        Log.i(TAG, "viewList size=${viewList?.size}")
        if (viewList?.isEmpty() == true) { // 走备用ID
            viewList = findNodesByViewId(service, DyResId.LIVE_CENTER_CONTROL_PAGE.ROOT_ID_BACKUP)
        }
        if (viewList?.isNotEmpty() == true) {
            Log.i(TAG, "childCount=${viewList[0].childCount}")
            val nodeInfo = viewList[0]
            for (index in 0 until nodeInfo.childCount) {
                val childNodeInfo = nodeInfo.getChild(index)
                Log.i(
                    TAG,
                    "一级递归：index=$index childCount=${childNodeInfo.childCount} clsName=${childNodeInfo.className}"
                )
                if ("android.webkit.WebView" == childNodeInfo.className) {
//                    Log.i(TAG, "一级递归：webview url=${(childNodeInfo as WebView).url}")
                }
                for(index2 in 0 until childNodeInfo.childCount) {
                    val childNodeInfo2 = childNodeInfo.getChild(index2)
                    Log.i(TAG, "二级递归：index=$index2 childCount=${childNodeInfo2.childCount} clsName=${childNodeInfo2.className}")
                    for (index3 in 0 until childNodeInfo2.childCount) {
                        val childNodeInfo3 = childNodeInfo2.getChild(index3)
                        Log.i(TAG, "二级递归：index=$index3 childCount=${childNodeInfo3.childCount} clsName=${childNodeInfo3.className}")
                        for (index4 in 0 until childNodeInfo3.childCount) {
                            val childNodeInfo4 = childNodeInfo3.getChild(index4)
                            Log.i(TAG, "三级递归：index=$index4 childCount=${childNodeInfo4.childCount} clsName=${childNodeInfo4.className}")
                            for (index5 in 0 until childNodeInfo4.childCount) {
                                val childNodeInfo5 = childNodeInfo4.getChild(index5)
                                Log.i(TAG, "四级递归：index=$index5 childCount=${childNodeInfo5.childCount} clsName=${childNodeInfo5.className}")
                                for (index6 in 0 until childNodeInfo5.childCount) {
                                    val childNodeInfo6 = childNodeInfo5.getChild(index6)
                                    Log.i(TAG, "五级递归：index=$index6 childCount=${childNodeInfo6.childCount} clsName=${childNodeInfo6.className}")
                                    if (TEXT_VIEW == childNodeInfo6.className) {
                                        Log.i(TAG, "五级递归：textView内容=${childNodeInfo6.text}")
                                        if ("去开播" == childNodeInfo6.text.toString() && !goOpenIsExecuteFinish) {
                                            // 触发点击事件
                                            childNodeInfo6.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                            goOpenIsExecuteFinish = true
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

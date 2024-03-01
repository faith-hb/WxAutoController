package com.zbycorp.wx.utils

import android.accessibilityservice.AccessibilityService
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.text.TextUtils
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import com.lzf.easyfloat.utils.DisplayUtils
import com.zbycorp.wx.contants.DyResId
import com.zbycorp.wx.contants.DyResId.ACTION
import com.zbycorp.wx.utils.AccessUtil.BUTTON_VIEW
import com.zbycorp.wx.utils.AccessUtil.EDIT_TEXT
import com.zbycorp.wx.utils.AccessUtil.TEXT_VIEW
import com.zbycorp.wx.utils.AccessUtil.WEBVIEW
import com.zbycorp.wx.utils.AccessUtil.fillInput
import com.zbycorp.wx.utils.AccessUtil.findNodesByViewId
import com.zbycorp.wx.utils.AccessUtil.mockClkByNode
import com.zbycorp.wx.utils.AccessUtil.scrollByNode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal object DyAccessUtil {
    const val TAG = "DyAccessUtil:"

    /**
     * 他人主页的自动化是否执行完毕
     */
    private var userProfileIsExecuteFinish = false

    /**
     * 去开播自动化是否执行完毕
     */
    private var goOpenIsExecuteFinish = false
    /**
     * 用户评论点击自动化是否执行完毕
     */
    var commentIsExecuteFinish = false

    /**
     * 输入框的父节点
     */
    private var edtRootNodeInfo: AccessibilityNodeInfo? = null

    private var isFillEndEdt = false

    fun openDyApp(activity: Activity) {
        val intent = Intent()
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK // 保证每次进入到首页
        intent.setClassName(
            DyResId.DY_PACKAGE,
            DyResId.SPLASH_PAGE
        )
        activity.startActivity(intent)
    }

    fun openPageByClsName(context: Context, actClsName: String) {
        try {
            val intent = Intent()
            intent.setClassName(DyResId.DY_PACKAGE,actClsName)
            context.startActivity(intent)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 开始滚动
     */
    private fun startScroll(service: AccessibilityService, nodeInfo: AccessibilityNodeInfo) {
        if (nodeInfo.childCount > 0 && WEBVIEW == nodeInfo.getChild(0).className.toString()) {
            // 向上滑动
            val isScroll = scrollByNode(service, nodeInfo.getChild(0), 0, -240)
            Log.i(TAG, "webview滑动：isScroll=$isScroll")
        }
    }

    fun mainTab(service: AccessibilityService) {
        GlobalScope.launch(Dispatchers.Main) {
            delay(3200)
            AccessUtil.updateTips("模拟点击：我的")
            // 获取当前活动窗口的根节点
            val rootNode = service.rootInActiveWindow
            AccessUtil.traverseNodeByTxt(service, rootNode, "我", isExecuteClk = true)

            delay(3200)
            AccessUtil.updateTips("模拟点击：电商带货")
            val viewList = findNodesByViewId(service, DyResId.MINE_PAGE.HSV_LAYOUT)
            if (viewList.isNullOrEmpty()) {
                AccessUtil.updateTips("中断：电商带货节点ID变更")
                cancel()
                return@launch
            }
            if (viewList[0].childCount > 0) {
                val accessibilityNodeInfo = viewList[0].getChild(0)
                val targetNodeInfo = accessibilityNodeInfo.getChild(0)
                if (targetNodeInfo != null) {
                    val rect = AccessUtil.getNodeRect(targetNodeInfo)
                    if (rect != null) {

                        AccessUtil.getActivity()?.let {
                            AccessUtil.showAssistBox(it, rect)
                            delay(3200)
                            AccessUtil.dismissAssistBox()
                        }
                        delay(600)

                        mockClkByNode(service, rect)
                        cancel()
                    }
                }
            }
        }
    }

    private suspend fun traverseNodeCenter(service: AccessibilityService, node: AccessibilityNodeInfo?) {
        if (node == null) return
        var isExist = false
        // 处理当前节点，满足节点条件即终止遍历
        if (node.text != null && TextUtils.equals(node.text, "直播中控")) {
            isExist = true
            AccessUtil.updateTips("锁定目标")
            Log.i(TAG, "traverseNodeCenter：找到【直播中控】节点")
            val rect = AccessUtil.getNodeRect(node)
            if (rect != null) {
                AccessUtil.getActivity()?.let {
                    AccessUtil.showAssistBox(it, rect)
                    delay(3200)
                    AccessUtil.dismissAssistBox()
                }
                delay(600)
                mockClkByNode(service, rect)
            }
        }
//        Log.i(TAG, "traverseNodeCenter：node.text=${node.text} isExist=$isExist")
        for (i in 0 until node.childCount) {
            if (isExist) {
                break
            }
            val childNode = node.getChild(i)
            traverseNodeCenter(service, childNode)
        }
    }

    private fun traverseNodeGetWebView(
        service: AccessibilityService, node: AccessibilityNodeInfo?
    ): AccessibilityNodeInfo? {
        if (node == null) return null
        var isExit = false
        // 处理当前节点，满足节点条件即终止遍历
        if (WEBVIEW == node.className.toString()) {
            isExit = true
        }
        for (i in 0 until node.childCount) {
            if (isExit) {
                break
            }
            val childNode = node.getChild(i)
            traverseNodeGetWebView(service, childNode)
        }
        return node
    }

    private suspend fun traverseNodeExplain(
        shopName: String, service: AccessibilityService, node: AccessibilityNodeInfo?, scrollY: Int
    ) {
        if (node == null) return
        val rectExplain = Rect()
        // 处理当前节点，满足节点条件即终止遍历
        if (BUTTON_VIEW == node.className.toString() && node.text != null && node.text.contains(shopName)) {
            AccessUtil.updateTips("商品讲解：$shopName")
            val rect = AccessUtil.getNodeRect(node)
            if (rect != null) {
                // 精准计算ItemView坐标
                if (rect.right > 1080) {
                    rect.right = 1080
                }
                rect.top = rect.top + Math.abs(scrollY)
                rect.bottom = rect.bottom + Math.abs(scrollY)
                Log.i(TAG, "traverseNodeExplain=>rect=$rect")
                AccessUtil.getActivity()?.let {
//                    val statusH = DisplayUtils.getStatusBarHeight(it)
                    val showRect = Rect(rect)
//                    showRect.top = showRect.top - statusH
//                    showRect.bottom = showRect.bottom - statusH
                    AccessUtil.showAssistBox(it, showRect)
                    delay(3200)
                    AccessUtil.dismissAssistBox()
                }

//                delay(1200)
//                rectExplain.right = rect.right - 40
//                rectExplain.left = rectExplain.right - 202
//                rectExplain.bottom = rect.bottom - 68
//                rectExplain.top = rectExplain.bottom - 68
//
//
//                AccessUtil.getActivity()?.let {
//                    AccessUtil.showAssistBox(it, rectExplain)
//                    delay(3200)
//                    AccessUtil.dismissAssistBox()
//                }
//
//                AccessUtil.updateTips("模拟点击：准备话术【$shopName】")
//                delay(1200)
//                mockClkByNode(service, rectExplain, isSend = false, isLongClk = false)

                // 从ItemView中计算【讲解】坐标
                rectExplain.right = rect.right - 46
                rectExplain.left = rectExplain.right - 130
                rectExplain.bottom = rect.bottom - 52
                rectExplain.top = rectExplain.bottom - 72

                Log.i(TAG, "traverseNodeExplain=>rectExplain=$rectExplain")
                AccessUtil.getActivity()?.let {
                    AccessUtil.showAssistBox(it, rectExplain)
                    delay(3200)
                    AccessUtil.dismissAssistBox()
                }
                AccessUtil.updateTips("模拟点击：讲解【$shopName】商品")
                delay(600)
                mockClkByNode(service, rectExplain, isSend = false, isLongClk = false)
                return
            }
        }
        for (i in 0 until node.childCount) {
            val childNode = node.getChild(i)
            traverseNodeExplain(shopName, service, childNode, scrollY)
        }
    }

    fun centerTab(service: AccessibilityService) {
        GlobalScope.launch(Dispatchers.Main) {
            delay(4200)
            AccessUtil.updateTips("模拟点击：直播中控")
            delay(1200)
            // 获取当前活动窗口的根节点
            val rootNode = service.rootInActiveWindow
            traverseNodeCenter(service, rootNode)
        }
    }

    private suspend fun traverseNode(
        action: String,
        service: AccessibilityService,
        nodeInfo:AccessibilityNodeInfo
    ): Boolean {
        Log.i(TAG, "childCount=${nodeInfo.childCount} clsName=${nodeInfo.className}")
        if (ACTION.START_SCROLL == action) {
            startScroll(service, nodeInfo)
            return true
        }
        // 递归查找操作节点
        for (index in 0 until nodeInfo.childCount) {
            val childNodeInfo = nodeInfo.getChild(index)
            Log.i(
                TAG,
                "一级递归：index=$index childCount=${childNodeInfo.childCount} clsName=${childNodeInfo.className}"
            )
            for (index2 in 0 until childNodeInfo.childCount) {
                val childNodeInfo2 = childNodeInfo.getChild(index2)
                Log.i(
                    TAG,
                    "二级递归：index=$index2 childCount=${childNodeInfo2.childCount} clsName=${childNodeInfo2.className}"
                )
                if (TEXT_VIEW == childNodeInfo2.className) {
                    Log.i(TAG, "二级递归：textView内容=${childNodeInfo2.text}")
                }
                for (index3 in 0 until childNodeInfo2.childCount) {
                    val childNodeInfo3 = childNodeInfo2.getChild(index3) ?: continue
                    Log.i(
                        TAG,
                        "二级递归：index=$index3 childCount=${childNodeInfo3.childCount} clsName=${childNodeInfo3.className}"
                    )
                    for (index4 in 0 until childNodeInfo3.childCount) {
                        val childNodeInfo4 = childNodeInfo3.getChild(index4) ?: continue
                        Log.i(
                            TAG,
                            "三级递归：index=$index4 childCount=${childNodeInfo4.childCount} clsName=${childNodeInfo4.className}"
                        )
                        if (TEXT_VIEW == childNodeInfo4.className) {
                            Log.i(
                                TAG,
                                "三级递归：textView内容=${childNodeInfo4.text}"
                            )
                        }
                        for (index5 in 0 until childNodeInfo4.childCount) {
                            val childNodeInfo5 = childNodeInfo4.getChild(index5) ?: continue
                            Log.i(
                                TAG,
                                "四级递归：index=$index5 childCount=${childNodeInfo5.childCount} clsName=${childNodeInfo5.className}"
                            )
                            if (TEXT_VIEW == childNodeInfo5.className) {
                                Log.i(TAG, "四级递归：textView内容=${childNodeInfo5.text}")
                            }
                            for (index6 in 0 until childNodeInfo5.childCount) {
                                val childNodeInfo6 = childNodeInfo5.getChild(index6) ?: continue
                                Log.i(
                                    TAG,
                                    "五级递归：index=$index6 childCount=${childNodeInfo6.childCount} clsName=${childNodeInfo6.className}"
                                )
                                if (TEXT_VIEW == childNodeInfo6.className) {
                                    Log.i(TAG, "五级递归：textView内容=${childNodeInfo6.text}")
                                    if ("去开播" == childNodeInfo6.text.toString() && !goOpenIsExecuteFinish) {
                                        // 触发点击事件
                                        childNodeInfo6.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                        goOpenIsExecuteFinish = true
                                    }
                                }
                                for (index7 in 0 until childNodeInfo6.childCount) {
                                    val childNodeInfo7 =
                                        childNodeInfo6.getChild(index7) ?: continue
                                    Log.i(
                                        TAG,
                                        "六级递归：index=$index7 childCount=${childNodeInfo7?.childCount} clsName=${childNodeInfo7?.className}"
                                    )
                                    if (TEXT_VIEW == childNodeInfo7.className) {
                                        Log.i(
                                            TAG,
                                            "六级递归：textView内容=${childNodeInfo7.text}"
                                        )
                                    } else if (BUTTON_VIEW == childNodeInfo7.className && childNodeInfo7.text.toString()
                                            .contains("LION狮王大白牙膏WHITE去黄去牙")
                                    ) {
                                        Log.i(
                                            TAG,
                                            "六级递归111：buttonView内容=${childNodeInfo7.text}"
                                        )
                                        if (ACTION.EXPLAIN_GOODS == action) {
                                            val rect = AccessUtil.getNodeRect(childNodeInfo7)
                                            if (rect != null) {
                                                rect.left = rect.right - 160
                                                rect.top = rect.bottom - 65
                                                rect.right = rect.right - 50
                                                rect.bottom = rect.bottom - 15
                                                for (index in 0 until 2) {
                                                    rect.top--
                                                    rect.bottom--
                                                    Thread.sleep(420)
                                                    mockClkByNode(service, rect)
                                                }
                                                Log.i(
                                                    TAG,
                                                    "六级递归111：触发讲解自动化"
                                                )
                                            }
                                            break
                                        }
                                    }
                                    for (index8 in 0 until childNodeInfo7.childCount) {
                                        val childNodeInfo8 =
                                            childNodeInfo7.getChild(index8) ?: continue
                                        Log.i(
                                            TAG,
                                            "七级递归：index=$index8 childCount=${childNodeInfo8.childCount} clsName=${childNodeInfo8.className}"
                                        )
                                        if (TEXT_VIEW == childNodeInfo8.className) {
                                            Log.i(
                                                TAG,
                                                "七级递归：textView内容=${childNodeInfo8.text}"
                                            )
                                            if ("用户评论" == childNodeInfo8.text.toString() && !commentIsExecuteFinish && ACTION.USER_COMMENT == action) {
                                                edtRootNodeInfo =
                                                    childNodeInfo8.parent.parent.parent
                                                childNodeInfo8.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                                commentIsExecuteFinish = true
                                                break
                                            } else if ("send_icon" == childNodeInfo8.viewIdResourceName && ACTION.SEND_COMMENT == action) {
                                                Log.i(
                                                    TAG,
                                                    "七级递归：点击发送按钮 childNodeInfo8=${childNodeInfo8}"
                                                )
                                                val rect = AccessUtil.getNodeRect(childNodeInfo8)
                                                if (rect != null) {
                                                    rect.top = 1254
                                                    rect.bottom = 1325
                                                    mockClkByNode(service, rect, isSend = true)
                                                    break
                                                }
                                            }
                                        }
                                        for (index9 in 0 until childNodeInfo8.childCount) {
                                            val childNodeInfo9 =
                                                childNodeInfo8.getChild(index9) ?: continue
                                            Log.i(
                                                TAG,
                                                "八级递归：index=$index9 childCount=${childNodeInfo9.childCount} clsName=${childNodeInfo9.className} text=${childNodeInfo9.text}"
                                            )
                                            if (EDIT_TEXT == childNodeInfo9.className.toString() && ACTION.INPUT_COMMENT == action) {
//                                                startScroll(service, nodeInfo)
                                                Log.i(TAG, "八级递归：开始点击输入框")
                                                childNodeInfo9.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                                delay(1200)
                                                Log.i(TAG, "八级递归：开始粘贴内容")
                                                fillInput(
                                                    service, childNodeInfo9, "欢迎进入直播间..."
                                                )
                                                delay(1000)
                                                isFillEndEdt = true
                                                break
                                            }
                                            for (index10 in 0 until childNodeInfo9.childCount) {
                                                val childNodeInfo10 =
                                                    childNodeInfo9.getChild(index10) ?: continue
                                                Log.i(
                                                    TAG,
                                                    "九级递归：index=$index10 childCount=${childNodeInfo10.childCount} clsName=${childNodeInfo10.className}"
                                                )
                                                for (index11 in 0 until childNodeInfo10.childCount) {
                                                    val childNodeInfo11 =
                                                        childNodeInfo10.getChild(index11)
                                                            ?: continue
                                                    Log.i(
                                                        TAG,
                                                        "十级递归：index=$index11 childCount=${childNodeInfo11.childCount} clsName=${childNodeInfo11.className}"
                                                    )
                                                    if (TEXT_VIEW == childNodeInfo11.className) {
                                                        Log.i(
                                                            TAG,
                                                            "十级递归：textView内容=${childNodeInfo11.text}"
                                                        )
                                                    }
                                                    for (index12 in 0 until childNodeInfo11.childCount) {
                                                        val childNodeInfo12 =
                                                            childNodeInfo11.getChild(index12)
                                                                ?: continue
                                                        Log.i(
                                                            TAG,
                                                            "十一级递归：index=$index11 childCount=${childNodeInfo12.childCount} clsName=${childNodeInfo12.className}"
                                                        )
                                                        if (TEXT_VIEW == childNodeInfo12.className) {
                                                            Log.i(
                                                                TAG,
                                                                "十一级递归：textView内容=${childNodeInfo12.text}"
                                                            )
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
            }
        }
        return true
    }

    fun liveCenterControlMessage(service: AccessibilityService) {
        GlobalScope.launch(Dispatchers.Main) {
            delay(5200)
//            Log.i(TAG, "***************滑动->start*********************")
            AccessUtil.updateTips("模拟滑动：向上滑动${240/2}个像素")
//            // 获取当前活动窗口的根节点
            val rootNode = service.rootInActiveWindow
            val nodeInfo = traverseNodeGetWebView(service, rootNode)
            if (nodeInfo != null) {
                val isScroll = scrollByNode(service, nodeInfo, 0, -240)
                Log.i(TAG, "webview滑动：isScroll=$isScroll")
            }
            Log.i(TAG, "***************滑动->end*********************")

            // mock讲解第一个商品
            delay(5200)
            traverseNodeExplain("嘿满赞北部湾食盐", service, rootNode,-240)

            // mock讲解第二个商品
            delay(3200)
            traverseNodeExplain("洁丽雅浴室拖鞋男四季", service, rootNode,-240)

            return@launch


            Log.i(TAG, "***************用户评论->start*********************")
            delay(3200)
            AccessUtil.updateTips("模拟点击：用户评论")
            var viewCommentList =
                findNodesByViewId(service, DyResId.LIVE_CENTER_CONTROL_PAGE.ROOT_ID)
            if (viewCommentList?.isEmpty() == true) {
                viewCommentList =
                    findNodesByViewId(service, DyResId.LIVE_CENTER_CONTROL_PAGE.ROOT_ID_BACKUP)
            }
            if (viewCommentList?.isEmpty() == true) {
                Log.i(TAG, "***************用户评论->无根节点信息*********************")
                return@launch
            }
            traverseNode(ACTION.USER_COMMENT, service, viewCommentList!![0])
            Log.i(TAG, "***************用户评论->end*********************")

            if (!commentIsExecuteFinish) return@launch
            Log.i(TAG, "***************输入评论->start*********************")
            delay(3200)
            AccessUtil.updateTips("自动化填写文本框内容")
            var viewInputList =
                findNodesByViewId(service, DyResId.LIVE_CENTER_CONTROL_PAGE.ROOT_ID)
            if (viewInputList?.isEmpty() == true) {
                viewInputList =
                    findNodesByViewId(service, DyResId.LIVE_CENTER_CONTROL_PAGE.ROOT_ID_BACKUP)
            }
            if (viewInputList?.isEmpty() == true) {
                Log.i(TAG, "***************输入评论->无根节点信息*********************")
                return@launch
            }
            traverseNode(ACTION.INPUT_COMMENT, service, viewInputList!![0])
            Log.i(TAG, "***************输入评论->end*********************")

            if (!isFillEndEdt) return@launch
            Log.i(TAG, "***************发送评论->start*********************")
            delay(2200)
            AccessUtil.updateTips("模拟点击：发送弹幕")
            var viewSendList =
                findNodesByViewId(service, DyResId.LIVE_CENTER_CONTROL_PAGE.ROOT_ID)
            if (viewSendList?.isEmpty() == true) {
                viewSendList =
                    findNodesByViewId(service, DyResId.LIVE_CENTER_CONTROL_PAGE.ROOT_ID_BACKUP)
            }
            if (viewSendList?.isEmpty() == true) {
                Log.i(TAG, "***************发送评论->无根节点信息*********************")
                return@launch
            }
            traverseNode(ACTION.SEND_COMMENT, service, viewSendList!![0])
            Log.i(TAG, "***************发送评论->end*********************")

            delay(2200)
            AccessUtil.updateTips("自动化演示结束")
            delay(1200)
            AccessUtil.dismissWindowTips()
        }
    }
}

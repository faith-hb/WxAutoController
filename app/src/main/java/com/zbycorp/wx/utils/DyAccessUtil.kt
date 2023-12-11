package com.zbycorp.wx.utils

import android.accessibilityservice.AccessibilityService
import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Rect
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
        intent.flags = FLAG_ACTIVITY_NEW_TASK
        intent.setClassName(
            DyResId.DY_PACKAGE,
            DyResId.SPLASH_PAGE
        )
        activity.startActivity(intent)
    }

    /**
     * 开始滚动
     */
    private suspend fun startScroll(service: AccessibilityService, nodeInfo: AccessibilityNodeInfo) {
        if (nodeInfo.childCount > 0 && WEBVIEW == nodeInfo.getChild(0).className.toString()) {
            // 向上滑动
            val isScroll = scrollByNode(service, nodeInfo.getChild(0), 0, -240)
            Log.i(TAG, "webview滑动：isScroll=$isScroll")
            AccessUtil.updateTips("向上滑动")
        }
    }

    private suspend fun getNodeRect(nodeInfo: AccessibilityNodeInfo): Rect? {
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
                "getNodeRect：讲解 boundsInScreen=$boundsInScreen"
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
                Log.i(
                    TAG,
                    "getNodeRect：商品item按钮坐标 left=$left top=$top right=$right bottom=$bottom"
                )
                rect.left = left
                rect.top = top
                rect.right = right
                rect.bottom = bottom

                Log.i(
                    TAG,
                    "getNodeRect：商品item中讲解按钮坐标 left=${rect.left} top=${rect.top} right=${rect.right} bottom=${rect.bottom}"
                )
            }
        }
        return rect
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
                                            val rect = getNodeRect(childNodeInfo7)
                                            if (rect != null) {
                                                rect.left = rect.right - 160
                                                rect.top = rect.bottom - 65
                                                rect.right = rect.right - 50
                                                rect.bottom = rect.bottom - 15
                                                for (index in 0 until 2) {
                                                    rect.top--
                                                    rect.bottom--
                                                    Thread.sleep(420)
                                                    mockClkByNode(service, rect,false)
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
                                                val rect = getNodeRect(childNodeInfo8)
                                                if (rect != null) {
                                                    rect.top = 1254
                                                    rect.bottom = 1325
                                                    mockClkByNode(service, rect!!, true)
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
        AccessUtil.updateTips("进入直播中控页面")
        GlobalScope.launch(Dispatchers.Main) {
            delay(3200)
            var viewGoodsList = findNodesByViewId(service, DyResId.LIVE_CENTER_CONTROL_PAGE.ROOT_ID)
            if (viewGoodsList?.isEmpty() == true) { // 走备用ID
                viewGoodsList =
                    findNodesByViewId(service, DyResId.LIVE_CENTER_CONTROL_PAGE.ROOT_ID_BACKUP)
            }
            Log.i(TAG, "viewList size=${viewGoodsList?.size}")
            if (viewGoodsList?.isEmpty() == true) {
                Log.i(TAG, "自动化停止，没有找到节点信息")
                return@launch
            }
            Log.i(TAG, "***************讲解商品->start*********************")
            AccessUtil.updateTips("模拟点击：开始讲解（第一个商品）")
            traverseNode(ACTION.EXPLAIN_GOODS, service, viewGoodsList!![0])
            Log.i(TAG, "***************讲解商品->end*********************")

//            Log.i(TAG, "***************滑动->start*********************")
//            delay(3200)
//            var viewScrollList =
//                findNodesByViewId(service, DyResId.LIVE_CENTER_CONTROL_PAGE.ROOT_ID)
//            if (viewScrollList?.isEmpty() == true) {
//                viewScrollList =
//                    findNodesByViewId(service, DyResId.LIVE_CENTER_CONTROL_PAGE.ROOT_ID_BACKUP)
//            }
//            if (viewScrollList?.isEmpty() == true) {
//                Log.i(TAG, "***************滑动->无根节点信息*********************")
//                return@launch
//            }
//            traverseNode(ACTION.START_SCROLL, service, viewScrollList!![0])
//            Log.i(TAG, "***************滑动->end*********************")

            Log.i(TAG, "***************用户评论->start*********************")
            delay(3200)
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

            Log.i(TAG, "***************滑动->start*********************")
            delay(1200)
            var viewScrollList =
                findNodesByViewId(service, DyResId.LIVE_CENTER_CONTROL_PAGE.ROOT_ID)
            if (viewScrollList?.isEmpty() == true) {
                viewScrollList =
                    findNodesByViewId(service, DyResId.LIVE_CENTER_CONTROL_PAGE.ROOT_ID_BACKUP)
            }
            if (viewScrollList?.isEmpty() == true) {
                Log.i(TAG, "***************滑动->无根节点信息*********************")
                return@launch
            }
            traverseNode(ACTION.START_SCROLL, service, viewScrollList!![0])
            Log.i(TAG, "***************滑动->end*********************")

            if (!commentIsExecuteFinish) return@launch
            Log.i(TAG, "***************输入评论->start*********************")
            delay(3200)
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
            delay(3200)
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
        }

//                if (!isFillEndEdt) return
//                Log.i(TAG, "**************************************发送弹幕*************************************")
////                Thread.sleep(2200)
//                var sendViewList = findNodesByViewId(service, DyResId.LIVE_CENTER_CONTROL_PAGE.ROOT_ID)
//                Log.i(TAG, "edtViewList size=${viewList?.size}")
//                if (sendViewList?.isEmpty() == true) { // 走备用ID
//                    sendViewList = findNodesByViewId(service, DyResId.LIVE_CENTER_CONTROL_PAGE.ROOT_ID_BACKUP)
//                }
////                if (sendViewList?.isNotEmpty() == true) {
////                    val nodeInfo = sendViewList[0]
////                    Log.i(TAG, "childCount=${nodeInfo.childCount} clsName=${nodeInfo.className}")
////                    for (index in 0 until nodeInfo.childCount) {
////                        val childNodeInfo = nodeInfo.getChild(index)
////                        Log.i(
////                            TAG,
////                            "一级递归：index=$index childCount=${childNodeInfo.childCount} clsName=${childNodeInfo.className}"
////                        )
////                        for (index2 in 0 until childNodeInfo.childCount) {
////                            val childNodeInfo2 = childNodeInfo.getChild(index2)
////                            Log.i(
////                                TAG,
////                                "二级递归：index=$index2 childCount=${childNodeInfo2.childCount} clsName=${childNodeInfo2.className}"
////                            )
////                            if (TEXT_VIEW == childNodeInfo2.className) {
////                                Log.i(TAG, "二级递归：textView内容=${childNodeInfo2.text}")
////                            }
////                            for (index3 in 0 until childNodeInfo2.childCount) {
////                                val childNodeInfo3 = childNodeInfo2.getChild(index3)
////                                Log.i(
////                                    TAG,
////                                    "二级递归：index=$index3 childCount=${childNodeInfo3.childCount} clsName=${childNodeInfo3.className}"
////                                )
////                                for (index4 in 0 until childNodeInfo3.childCount) {
////                                    val childNodeInfo4 = childNodeInfo3.getChild(index4)
////                                    Log.i(
////                                        TAG,
////                                        "三级递归：index=$index4 childCount=${childNodeInfo4.childCount} clsName=${childNodeInfo4.className}"
////                                    )
////                                    if (TEXT_VIEW == childNodeInfo4.className) {
////                                        Log.i(
////                                            TAG,
////                                            "三级递归：textView内容=${childNodeInfo4.text}"
////                                        )
////                                    }
////                                    for (index5 in 0 until childNodeInfo4.childCount) {
////                                        val childNodeInfo5 = childNodeInfo4.getChild(index5)
////                                        Log.i(
////                                            TAG,
////                                            "四级递归：index=$index5 childCount=${childNodeInfo5.childCount} clsName=${childNodeInfo5.className}"
////                                        )
////                                        if (TEXT_VIEW == childNodeInfo5.className) {
////                                            Log.i(
////                                                TAG,
////                                                "四级递归：textView内容=${childNodeInfo5.text}"
////                                            )
////                                        }
////                                        for (index6 in 0 until childNodeInfo5.childCount) {
////                                            val childNodeInfo6 = childNodeInfo5.getChild(index6)
////                                            Log.i(
////                                                TAG,
////                                                "五级递归：index=$index6 childCount=${childNodeInfo6.childCount} clsName=${childNodeInfo6.className}"
////                                            )
////                                            if (TEXT_VIEW == childNodeInfo6.className) {
////                                                Log.i(
////                                                    TAG,
////                                                    "五级递归：textView内容=${childNodeInfo6.text}"
////                                                )
////                                                if ("去开播" == childNodeInfo6.text.toString() && !goOpenIsExecuteFinish) {
////                                                    // 触发点击事件
////                                                    childNodeInfo6.performAction(
////                                                        AccessibilityNodeInfo.ACTION_CLICK
////                                                    )
////                                                    goOpenIsExecuteFinish = true
////                                                }
////                                            }
////                                            for (index7 in 0 until childNodeInfo6.childCount) {
////                                                val childNodeInfo7 =
////                                                    childNodeInfo6.getChild(index7) ?: continue
////                                                Log.i(
////                                                    TAG,
////                                                    "六级递归：index=$index7 childCount=${childNodeInfo7?.childCount} clsName=${childNodeInfo7?.className}"
////                                                )
////                                                if (TEXT_VIEW == childNodeInfo7.className) {
////                                                    Log.i(
////                                                        TAG,
////                                                        "六级递归：textView内容=${childNodeInfo7.text}"
////                                                    )
////                                                } else if (BUTTON_VIEW == childNodeInfo7.className) {
////                                                    Log.i(
////                                                        TAG,
////                                                        "六级递归：buttonView内容=${childNodeInfo7.text}"
////                                                    )
////                                                }
////                                                for (index8 in 0 until childNodeInfo7.childCount) {
////                                                    val childNodeInfo8 =
////                                                        childNodeInfo7.getChild(index8)
////                                                    Log.i(
////                                                        TAG,
////                                                        "七级递归：index=$index8 childCount=${childNodeInfo8.childCount} windowId=${childNodeInfo8.windowId} clsName=${childNodeInfo8.className}"
////                                                    )
////                                                    if (TEXT_VIEW == childNodeInfo8.className) {
////                                                        Log.i(
////                                                            TAG,
////                                                            "七级递归：textView内容=${childNodeInfo8.text}"
////                                                        )
////                                                        // 发送按钮
////                                                        if ("send_icon" == childNodeInfo8.viewIdResourceName) {
////                                                            Log.i(
////                                                                TAG,
////                                                                "七级递归：点击发送按钮 childNodeInfo8=${childNodeInfo8}"
////                                                            )
////                                                            childNodeInfo8.toString().apply {
////                                                                val boundsInScreen = substring(
////                                                                    indexOf("boundsInScreen: Rect") + "boundsInScreen: Rect".length,
////                                                                    indexOf(
////                                                                        ";",
////                                                                        indexOf("boundsInScreen: Rect")
////                                                                    )
////                                                                )
////                                                                Log.i(
////                                                                    TAG,
////                                                                    "七级递归：发送按钮坐标 boundsInScreen=$boundsInScreen"
////                                                                )
////                                                                val rect = Rect()
////                                                                boundsInScreen.apply {
////                                                                    var indexS = 0
////                                                                    var tapX0 =
////                                                                        substring(
////                                                                            indexOf("(")+1,
////                                                                            indexOf(",")
////                                                                        ).toInt()
////                                                                    var tapY0 =
////                                                                        substring(
////                                                                            indexOf(", ")+2,
////                                                                            indexOf(" -")
////                                                                        ).toInt()
////                                                                    indexS = indexOf(",",indexOf("- "))
////                                                                    var tapX1 =
////                                                                        substring(
////                                                                            indexOf("- ")+2,
////                                                                            indexS
////                                                                        ).toInt()
////                                                                    var tapY1 =
////                                                                        substring(
////                                                                            indexOf(
////                                                                                ", ",
////                                                                                indexS
////                                                                            ) + 2,
////                                                                            indexOf(")")
////                                                                        ).toInt()
////                                                                    Log.i(
////                                                                        TAG,
////                                                                        "七级递归：发送按钮坐标 tapX0=$tapX0 tapY0=$tapY0 tapX1=$tapX1 tapY1=$tapY1"
////                                                                    )
////                                                                    rect.left = tapX0
//////                                                                    rect.top = tapY0
////                                                                    rect.top = 1254
////                                                                    rect.right = tapX1
//////                                                                    rect.bottom = tapY1
////                                                                    rect.bottom = 1325
////                                                                }
//////                                                                AccessUtil.showRectCheck(service,rect,0,0)
//////                                                                mockClkByNode(service, rect)
////                                                            }
////                                                        }
////                                                    }
////                                                }
////                                            }
////                                        }
////                                    }
////                                }
////                            }
////                        }
////                    }
////                }
//
//            }
//        }
    }
}

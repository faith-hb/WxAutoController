package com.zbycorp.wx.contants;

/**
 * Description：快手布局元素ID
 * @author:hongb
 * @date:2023/12/4
 */
public interface KsResId {
    /**
     * 快手包名
     */
    String KS_PACKAGE = "com.smile.gifmaker";
    /**
     * 快手布局ID前缀
     */
    String BASE_LAYOUT_ID = "com.smile.gifmaker:id/";
    /**
     * 启动页
     */
    String SPLASH_PAGE = "com.yxcorp.gifshow.HomeActivity";
    /**
     * 直播间页面
     */
    String LIVE_PAGE = "com.yxcorp.gifshow.detail.PhotoDetailActivity";
    /**
     * 托管页面
     */
    String LIVE_ESCROW_PAGE = "com.kuaishou.live.escrow.basic.activity.LiveEscrowActivity";
    /**
     * 他人主页
     */
    String USER_PROFILE_PAGE = "com.yxcorp.gifshow.profile.activity.UserProfileActivity";
    /**
     * 他人私信页面
     */
    String IM_CHAT_PAGE = "com.yxcorp.gifshow.message.imchat.acivity.IMChatActivity";

    /**
     * 直播间页面
     */
    interface LIVE_PAGE {
        /**
         * 托管弹窗
         */
        // 开始托管
        String START_ESCROW = BASE_LAYOUT_ID + "live_escrow_entry_start_button";
        // 托管弹窗关闭
        String CLOSE_ESCROW = BASE_LAYOUT_ID + "live_escrow_entry_close";
        // 观看人数
        String AUDIENCE_COUNT = BASE_LAYOUT_ID + "live_audience_count_text";
        // 输入框选中控件TextSwitcher
        String COMMENT_TEXT_VIEW = BASE_LAYOUT_ID + "live_comment_text_view";
        // 评论输入框
        String EDITOR = BASE_LAYOUT_ID + "editor";
        // 发送按钮（输入的内容进行发送）
        String FINISH_BUTTON = BASE_LAYOUT_ID + "finish_button";
    }

    /**
     * 个人主页
     */
    interface USER_PROFILE_PAGE {
        // 更多按钮
        String MORE_BTN = BASE_LAYOUT_ID + "more_btn";
        // 粉丝-个人主页-发私信(关注前)
        String SEND_MESSAGE_SMALL_ICON = BASE_LAYOUT_ID + "send_message_small_icon";
        // 粉丝-个人主页-发私信(关注后)
        String SEND_MESSAGE = BASE_LAYOUT_ID + "send_message";
        // 观众关注/回关
        String HEADER_FOLLOW_BUTTON = BASE_LAYOUT_ID + "header_follow_button";
    }

    /**
     * 他人私信页面
     */
    interface IM_CHAT_PAGE {
        // 评论输入框
        String EDITOR = BASE_LAYOUT_ID + "editor";
        // 发送按钮
        String SEND_BTN = BASE_LAYOUT_ID + "send_btn";
    }

    interface LIVE_ESCROW_PAGE {
        // 售卖商品
        String SELL_GOODS = BASE_LAYOUT_ID + "live_bottom_bar_item_icon_image_view";
        // 商品管理弹窗
        String GOODS_DIALOG = BASE_LAYOUT_ID + "krn_content_container";
    }
}

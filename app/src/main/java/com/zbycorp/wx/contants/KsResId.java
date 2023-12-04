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
     * 直播间页面
     */
    String LIVE_PAGE = "com.yxcorp.gifshow.detail.PhotoDetailActivity";
    /**
     * 他人主页
     */
    String USER_PROFILE_PAGE = "com.yxcorp.gifshow.profile.activity.UserProfileActivity";

    /**
     * 直播间页面
     */
    interface LIVE_PAGE {
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
        String MORE_BTN = "more_btn";
        // 粉丝-个人主页-发私信
        String SEND_MESSAGE_SMALL_ICON = "send_message_small_icon";
        // 观众关注/回关
        String HEADER_FOLLOW_BUTTON = "header_follow_button";
    }
}

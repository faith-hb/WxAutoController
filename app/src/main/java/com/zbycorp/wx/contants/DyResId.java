package com.zbycorp.wx.contants;

/**
 * Description：抖音布局元素ID
 * @author:hongb
 * @date:2023/12/6
 */
public interface DyResId {
    /**
     * 抖音包名
     */
    String DY_PACKAGE = "com.ss.android.ugc.aweme";
    /**
     * 抖音布局ID前缀
     */
    String BASE_LAYOUT_ID = "com.ss.android.ugc.aweme:id/";
    /**
     * 启动页
     */
    String SPLASH_PAGE = "com.ss.android.ugc.aweme.splash.SplashActivity";
    /**
     * 直播间页面
     */
    String LIVE_PAGE = "com.ss.android.ugc.aweme.live.LivePlayActivity";
    /**
     * 直播中控
     */
    String LIVE_CENTER_CONTROL_PAGE = "com.ss.android.ugc.aweme.bullet.ui.BulletContainerActivity";


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
     * 直播中控页面
     */
    interface LIVE_CENTER_CONTROL_PAGE {
        // 根节点ID
        String ROOT_ID = BASE_LAYOUT_ID + "bqq";
        String ROOT_ID_BACKUP = BASE_LAYOUT_ID + "bqx";
        // 商品管理弹窗
        String GOODS_DIALOG = BASE_LAYOUT_ID + "krn_content_container";
    }
}

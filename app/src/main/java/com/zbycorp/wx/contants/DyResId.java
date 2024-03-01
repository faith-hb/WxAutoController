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
     * 首页
     */
    String MAIN_PAGE = "com.ss.android.ugc.aweme.main.MainActivity";
    /**
     * 直播间页面
     */
    String LIVE_PAGE = "com.ss.android.ugc.aweme.live.LivePlayActivity";
    /**
     * 电商带货页面
     */
    String LIVE_DUMMY = "com.ss.android.ugc.aweme.live.LiveDummyActivity";
    /**
     * 直播中控
     */
    String LIVE_CENTER_CONTROL_PAGE = "com.ss.android.ugc.aweme.bullet.ui.BulletContainerActivity";

    /**
     * 抖音主页面
     */
    interface MAIN_PAGE {
        String TAB_ROOT_VIEW = BASE_LAYOUT_ID + "root_view";
        String TAB_HOME = BASE_LAYOUT_ID + "d5o";
        String TAB_PLUS = BASE_LAYOUT_ID + "vk1";
        String TAB_MINE = BASE_LAYOUT_ID + "vk4";
    }

    /**
     * 我的页面
     */
    interface MINE_PAGE {
//        String HSV_LAYOUT = BASE_LAYOUT_ID + "pgy";
        String HSV_LAYOUT = BASE_LAYOUT_ID + "pji";
    }


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
     * 电商带货页面
     */
    interface LIVE_DUMMY_PAGE {
        String ROOT = "root";
        String IOE = BASE_LAYOUT_ID + "ioe";
        String SHOP_WINDOW_MAIN_PAGE_CONTAINER = "shopWindowMainPageContainer";
    }

    /**
     * 直播中控页面
     */
    interface LIVE_CENTER_CONTROL_PAGE {
        // 根节点ID
        String ROOT_ID = BASE_LAYOUT_ID + "bqq";
        String ROOT_ID_BACKUP = BASE_LAYOUT_ID + "bqx";
        /**
         * 滑动后的rootId
         */
        String LIVE_CONTROL = "live_control";
        // 商品管理弹窗
        String GOODS_DIALOG = BASE_LAYOUT_ID + "krn_content_container";
    }

    /**
     * 节点行为
     */
    interface ACTION {
        /**
         * 自动滑动
         */
        String START_SCROLL = "start_scroll";
        /**
         * 讲解商品
         */
        String EXPLAIN_GOODS = "explain_goods";
        /**
         * 用户评论
         */
        String USER_COMMENT = "user_comment";
        /**
         * 输入评论
         */
        String INPUT_COMMENT = "input_comment";
        /**
         * 发送评论
         */
        String SEND_COMMENT = "send_comment";
    }
}

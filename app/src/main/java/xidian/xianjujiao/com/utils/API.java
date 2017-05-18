package xidian.xianjujiao.com.utils;

public class API {
    public static final String BASE_URL = "http://60.205.179.162/";
    //3DMGame网站地址
    public static final String DMGEAME_URL = "http://www.3dmgame.com";
    //服务器接口地址
    public static final String API_URL = "http://www.3dmgame.com/sitemap/api.php";
    //新闻列表接口地址
//    public static final String NEWS_URL = "http://www.3dmgame.com/sitemap/api.php?row=10&typeid=1&paging=1&page=%s";
    //文章列表接口地址
    public static final String ARTICLE_URL = "http://www.3dmgame.com/sitemap/api.php?row=10&typeid=%s&paging=1&page=%s";
    //文章详情的接口地址
    public static final String ChapterContent_URL = "http://www.3dmgame.com/sitemap/api.php?id=%s&typeid=%s";
    //评论列表接口
    public static final String COMMENT_URL = "http://www.3dmgame.com/sitemap/api.php?type=1&aid=%s&pageno=%s";
    //评论提交接口
    public static final String COMMENT_COMMIT_URL = "http://www.3dmgame.com/sitemap/api.php?type=2";
    //游戏列表获取接口
    public static final String GAME_URL = "http://www.3dmgame.com/sitemap/api.php?row=10&typeid=%s&paging=1&page=%s";

    // 首页模块列表API
    public static final String CHANNEL_LIST_URL = BASE_URL + "api/toutiao/getModuleList";
    // 轮播列表
    public static final String HEADLINE_SHUFFLING_URL = BASE_URL +"api/toutiao/getModuleLunbo?module_id=%s";
    // 新闻列表接口地址
    public static final String NEWS_URL = BASE_URL + "api/toutiao/getModuleNews?pageSize=10&module_id=%s&page=%s";
    public static final String NEWS_DETAIL_URL = BASE_URL + "api/toutiao/getNewsDetail?news_id=%s";
    // 搜索接口

    public static final String SEARCH_URL = BASE_URL + "api/toutiao/searchNewsList?pageSize=10&page=%s&name=%s";
    // 聚焦轮播列表

    public static final String FOCUS_SHUFFLING_URL = BASE_URL + "api/jujiao/getLunboList";
    // 聚焦的新闻列表
    public static final String FOCUS_NEWS_URL = BASE_URL + "api/jujiao/getModuleNews?page=%s";


    // 直播模块的列表
    public static final String LIVE_MODULE_NAME_LIST = BASE_URL +"api/live/getModuleList";
    public static final String LIVE_MODULE_NEWS_LIST = BASE_URL +"api/live/getModuleNews?module_id=%s&page=%s";


    public static final String GET_CODE_URL = BASE_URL + "api/validate-code";
    public static final String LOGIN_URL = BASE_URL + "api/phoneLogin";
}

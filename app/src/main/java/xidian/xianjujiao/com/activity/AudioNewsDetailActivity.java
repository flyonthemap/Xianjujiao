package xidian.xianjujiao.com.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsoluteLayout;

import com.dl7.player.media.IjkPlayerView;
import com.google.gson.Gson;

import com.software.shell.fab.ActionButton;

import com.sohu.cyan.android.sdk.api.CyanSdk;
import com.sohu.cyan.android.sdk.exception.CyanException;
import com.squareup.picasso.Picasso;
import com.umeng.socialize.Config;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import butterknife.ButterKnife;
import butterknife.OnClick;
import fm.jiecao.jcvideoplayer_lib.JCUtils;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import xidian.xianjujiao.com.R;
import xidian.xianjujiao.com.entity.NewsDetailData;
import xidian.xianjujiao.com.utils.API;
import xidian.xianjujiao.com.utils.Constant;
import xidian.xianjujiao.com.utils.DateUtils;
import xidian.xianjujiao.com.utils.JsonUtils;
import xidian.xianjujiao.com.utils.SystemBarTintManager;
import xidian.xianjujiao.com.utils.ToastUtil;
import xidian.xianjujiao.com.utils.UiUtils;

/**
 * 文章详情界面
 */
public class AudioNewsDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "NewsDetailActivity";
    private WebView comment_web;
    private Toolbar toolbar;
    private String body;
    private String id;
    private String newsId;
    private String title;//标题
    private String writer;//作者
    private String senddate;//发布时间
    private SmoothProgressBar webProgress;//进度条
    private String audioUrl;
    private String videoUrl;

    final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
            {
                    SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                    SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
            };
    private String decode;
    private String source;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        initWindow();
        initView();
//        id = getIntent().getStringExtra("type");
        newsId = getIntent().getStringExtra("newsId");

        Log.e(TAG,"newsId="+newsId);
        String url = String.format(API.NEWS_DETAIL_URL, newsId);
        Log.e(TAG,url);
        //下载网络数据
        x.http().get(new RequestParams(url), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String json = new String(result);
                Log.e(TAG,json);
//                //json解析
                NewsDetailData detail = new Gson().fromJson(JsonUtils.removeBOM(json), NewsDetailData.class);
                body = detail.data.content;//文章内容
                title = detail.data.title;//文章标题
                writer = "张三";//文章作者
                senddate = detail.data.create_time;//文章发布时间
                audioUrl = detail.data.audio;
                videoUrl = detail.data.video;
                source = detail.data.from;

//                arcurl = detail.getArcurl();
                initData();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
        initListener();
        initCySdk();
    }



    @Override
    protected void onResume() {
        super.onResume();
//        MobclickAgent.onPause(this);
    }

    //初始化窗体布局
    private void initWindow() {
        SystemBarTintManager tintManager;
        //由于沉浸式状态栏需要在Android4.4.4以上才能使用
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintColor(getResources().getColor(R.color.fab_material_white));
            tintManager.setStatusBarTintEnabled(true);
        }
    }


    //获取控件
    private void initView() {
        comment_web = (WebView) findViewById(R.id.coment_web);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        webProgress = (SmoothProgressBar) findViewById(R.id.web_progress);
        //2.替代
        setSupportActionBar(toolbar);
        //设置标题
        ActionBar ab = getSupportActionBar();
        ab.setTitle("新闻详情");
        //设置标题栏字体颜色
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlack));
        if(ab != null)
            ab.setDisplayHomeAsUpEnabled(true);


    }

    private void initCySdk() {
        CyanSdk cyanSdk;
        com.sohu.cyan.android.sdk.api.Config config = new com.sohu.cyan.android.sdk.api.Config();
        config.ui.toolbar_bg = Color.WHITE;
        // config.ui.style="indent";
        // config.ui.depth = 1;
        // config.ui.sub_size = 20;
        config.comment.showScore = false;
        config.comment.uploadFiles = true;

        config.comment.useFace = false;
        config.login.SSO_Assets_ICon = "ico31.png";
        config.login.SSOLogin = true;
        config.login.loginActivityClass = LoginActivity.class;
        String url = "http://changyan.sohu.com/front-demo/page-wap-index.html";
        String sourceId= "yemianzai-changyan-0100";
        try {
            CyanSdk.register(AudioNewsDetailActivity.this, "cyt05XnIt", "2ebafaec5d751fa024180699f8e19f65",
                    "http://your_site.com", config);
        } catch (CyanException e) {
            e.printStackTrace();
        }
        cyanSdk = CyanSdk.getInstance(this);
        cyanSdk.addCommentToolbar(AudioNewsDetailActivity.this,newsId,"主题是什么",url);

    }

    //初始化数据
    private void initData() {
        //启用支持javascript
        WebSettings settings = comment_web.getSettings();
        settings.setJavaScriptEnabled(true);
//        settings.setAllowFileAccess(true);

//        settings.setLoadWithOverviewMode(true);
        // by ROM
//        settings.setTextSize(WebSettings.TextSize.LARGER);//设置字体大小
//        settings.setDefaultTextEncodingName("utf-8");//设置默认编码格式
//        //自适应屏幕
//        settings.setUseWideViewPort(true);
        comment_web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                webProgress.setProgress(newProgress);
                if (webProgress != null && newProgress != 100) {
                    webProgress.setVisibility(View.VISIBLE);
                } else if (webProgress != null) {
                    webProgress.setVisibility(View.GONE);
                }
            }
        });

        comment_web.addJavascriptInterface(new JCCallBack(), "jcvd");

        //加载网络资源
        if (body != null) {
            try {
                //由于body的数据进行了URLEncode编码，所以需要我们再进行URLDecoder解码
                //否则只能显示图片
                decode = URLDecoder.decode(body, "utf-8");
                Log.e("result", "" + decode);
                String date = DateUtils.dateFormat(senddate);//发布时间
                String html = "<!DOCTYPE html>" +
                        "<html>" +
                        "<body>" +
                        "<h3 style=\"width:100%;height:40px;text-align:center\" >" +title+ "</h3>" +
                        "<p style=\"width:100%;height:15px;font-size:12px;text-align:center;padding=5px 0\">" +
                        "来源:"+ source +"&nbsp;&nbsp;&nbsp;&nbsp;"+"发布时间:" + date+"&nbsp;&nbsp;&nbsp;&nbsp;"+


                        "<div id=\"cont\" style=\"width:100%;height:200px\">" +
                        "<p style=\"width:100%;height:200px;\">" +

                        "<style>img{width:100%;height:auto}</style>" +
                        body+
                        "<script>" +
                        "    var cont=document.getElementById(\"cont\");" +
                        "    jcvd.adViewJieCaoVideoPlayer(-1,200,70,0,0)" +
                        "</script>" +
                        "</body>" +
                        "</html>";
                comment_web.loadDataWithBaseURL(null, html, "text/html", "charset=UTF-8", null);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


        }

    }

    //改写物理按键——返回的逻辑
    //返回无效是重定向的原因
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (comment_web != null && comment_web.canGoBack()) {
                comment_web.canGoBack();
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);

    }

    //设置事件监听
    private void initListener() {
        //toolbard的返回按钮事件监听
        toolbar.setNavigationOnClickListener(this);


    }

    //toolbar事件监听方法
    @Override
    public void onClick(View v) {
        //返回上一页
        finish();
    }



    public class JCCallBack {

        @JavascriptInterface
        public void adViewJieCaoVideoPlayer(final int width, final int height, final int top, final int left, final int index) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (index == 0) {
                        JCVideoPlayerStandard webVieo = new JCVideoPlayerStandard(AudioNewsDetailActivity.this);
                        webVieo.setUp("http://video.jiecao.fm/11/16/c/68Tlrc9zNi3JomXpd-nUog__.mp4",
                                JCVideoPlayer.SCREEN_LAYOUT_LIST, "嫂子骑大马");
                        Picasso.with(AudioNewsDetailActivity.this)
                                .load("http://img4.jiecaojingxuan.com/2016/11/16/1d935cc5-a1e7-4779-bdfa-20fd7a60724c.jpg@!640_360")
                                .into(webVieo.thumbImageView);
                        ViewGroup.LayoutParams ll = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        AbsoluteLayout.LayoutParams layoutParams = new AbsoluteLayout.LayoutParams(ll);
                        int dp = UiUtils.dip2px(120);
                        Log.e(TAG,dp+"");
                        layoutParams.y = UiUtils.dip2px(120);
                        layoutParams.x = UiUtils.dip2px( left);
                        layoutParams.height = 200;
                        layoutParams.width =  ViewGroup.LayoutParams.MATCH_PARENT;
                        comment_web.addView(webVieo, layoutParams);
                        Log.e(TAG,"距离父控件上方的距离"+webVieo.getBottom());
                    } else {
                        IjkPlayerView mPlayerView = new IjkPlayerView(UiUtils.getContext());
                        mPlayerView.init()
//                                .alwaysFullScreen()
//                                .enableOrientation()
                                .setVideoPath("http://video.jiecao.fm/11/16/c/68Tlrc9zNi3JomXpd-nUog__.mp4")
                                .enableDanmaku(false)
                                .setTitle("这是个跑马灯TextView，标题要足够长才会跑。-(゜ -゜)つロ 乾杯~");
                    }

                }
            });

        }
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}

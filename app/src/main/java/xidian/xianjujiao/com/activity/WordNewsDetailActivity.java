package xidian.xianjujiao.com.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sohu.cyan.android.sdk.api.Config;
import com.sohu.cyan.android.sdk.api.CyanSdk;
import com.sohu.cyan.android.sdk.exception.CyanException;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import butterknife.Bind;
import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import xidian.xianjujiao.com.R;
import xidian.xianjujiao.com.entity.NewsDetailData;
import xidian.xianjujiao.com.utils.API;
import xidian.xianjujiao.com.utils.DateUtils;
import xidian.xianjujiao.com.utils.JsonUtils;
import xidian.xianjujiao.com.utils.UiUtils;

/**
 * Created by flyonthemap on 2017/5/4.
 */
public class WordNewsDetailActivity extends AppCompatActivity {

    private final String TAG = "NewVedioViewNewsDetail";
    @Bind(R.id.word_news_content)
    WebView webView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.web_progress)
    SmoothProgressBar smoothProgressBar;
    @Bind(R.id.ib_search)
    ImageButton ibSearch;
    @Bind(R.id.title)
    TextView tvTitle;
    CyanSdk cyanSdk;
    private String newsId;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_news_detail);
        ButterKnife.bind(this);
        initView();

        newsId = getIntent().getStringExtra("newsId");
        initData(newsId);
    }


    private void initCySdk(NewsDetailData detailData,String url) {
        CyanSdk cyanSdk;
        Config config = new Config();
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
        try {
            CyanSdk.register(WordNewsDetailActivity.this, "cyt05XnIt", "2ebafaec5d751fa024180699f8e19f65",
                    "http://your_site.com", config);
        } catch (CyanException e) {
            e.printStackTrace();
        }
        cyanSdk = CyanSdk.getInstance(this);
        cyanSdk.addCommentToolbar(WordNewsDetailActivity.this,newsId,detailData.data.title,url);

    }


    //获取控件
    private void initView() {
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //获取到标题栏控件
        ibSearch.setVisibility(View.GONE);
        ab.setTitle("新闻详情");
        //设置标题栏字体颜色
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlack));

    }


    //初始化数据
    private void initData(String newsId) {
        final String url = String.format(API.NEWS_DETAIL_URL, newsId);
        Log.e(TAG,url);
        //下载网络数据
        x.http().get(new RequestParams(url), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                NewsDetailData detail = new Gson().fromJson(JsonUtils.removeBOM(result), NewsDetailData.class);

                setWebContent(detail);
                initCySdk(detail,url);
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




    }

    private void setWebContent(NewsDetailData detail) {
        String body = detail.data.content;//文章内容
        String title = detail.data.title;//文章标题
        String source = detail.data.from;
        String newsCreateTime = detail.data.create_time;//文章发布时间
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                smoothProgressBar.setProgress(newProgress);
                if (smoothProgressBar != null && newProgress != 100) {
                    smoothProgressBar.setVisibility(View.VISIBLE);
                } else if (smoothProgressBar != null) {
                    smoothProgressBar.setVisibility(View.GONE);
                }
            }
        });

        //
        if (body != null) {
            try {
                String decode = URLDecoder.decode(body, "utf-8");
                Log.e("result", "" + decode);
                String date = DateUtils.dateFormat(newsCreateTime);//发布时间
                String html2 =
                        "<!DOCTYPE html>" +
                                "<html>" +
                                "<body>" +
                                "<h3 style=\"width:100%;height:40px;text-align:center\" >" +title+ "</h3>" +
                                "<p style=\"width:100%;height:15px;font-size:12px;text-align:center;padding=5px 0\">" +
                                "来源:"+ source +"&nbsp;&nbsp;&nbsp;&nbsp;"+"发布时间:" + date+"&nbsp;&nbsp;&nbsp;&nbsp;"+
                                "</p>" +
                                "<style>img{width:100%;height:auto}</style>" +
                                body+
                                "</body>" +
                                "</html>";
                webView.loadDataWithBaseURL(null, html2, "text/html", "charset=UTF-8", null);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


        }

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
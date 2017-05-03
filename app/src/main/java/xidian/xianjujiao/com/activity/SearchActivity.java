package xidian.xianjujiao.com.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.classic.common.MultipleStatusView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import xidian.xianjujiao.com.R;
import xidian.xianjujiao.com.adapter.NewsAdapter;
import xidian.xianjujiao.com.entity.NewsData;
import xidian.xianjujiao.com.utils.API;
import xidian.xianjujiao.com.utils.Constant;
import xidian.xianjujiao.com.utils.JsonUtils;
import xidian.xianjujiao.com.utils.ToastUtil;
import xidian.xianjujiao.com.utils.UiUtils;

/**
 * @author wuyinlei
 * @function 搜索界面
 */

@ContentView(R.layout.activity_search)
public class SearchActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener{
    private String TAG = "SearchActivity";
    @ViewInject(R.id.et_search)
    private EditText et_search;
    @ViewInject(R.id.lv_news_result)
    private ListView news_lv;
    @ViewInject(R.id.tv_cancel)
    private TextView tv_cancel;
    @ViewInject(R.id.fl_no_data)
    private FrameLayout mNoLayout;
    @ViewInject(R.id.mulStatusView)
    private MultipleStatusView multipleStatusView;

    private View mFootView;


    // 全部的新闻数据
    private List<NewsData.NewItem> newsItemList = new ArrayList<>();
    // 新加载的新闻数据
    private List<NewsData.NewItem> newNewsData;

    // 加载更多需要的数据
    private int currentPage = 1;//当前页
    private boolean isBottom;//是否到底部的标记
    private boolean isLoadData = false;//判断是否已经在加载数据




    //是否显示搜索结果的状体标志
    private final static int NO_THING = 0;
    private final static int SHOW_DATA = 1;
    private static int STATE = 0;  //默认的是没有数据

    private NewsAdapter newsAdapter;
    private String mKey;  //key值

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        initListView();
        setListener();
    }



    /**
     * 改变搜索装填
     *
     * @param state 搜索key值
     */
    private void changeStates(int state) {
        switch (state) {
            case NO_THING:
                mNoLayout.setVisibility(View.VISIBLE);
                news_lv.setVisibility(View.INVISIBLE);
                break;

            case SHOW_DATA:
                mNoLayout.setVisibility(View.GONE);
                news_lv.setVisibility(View.VISIBLE);
                break;


        }
    }



    private void initListView() {
        mFootView = UiUtils.inflate(R.layout.listview_footer_loading);
        news_lv.addFooterView(mFootView, null, false);
        // 最开始的时候加载按钮不显示
        mFootView.setVisibility(View.GONE);

    }


    //下载网络数据
    private void startSearch(final int page) {
        multipleStatusView.showLoading();
        String newsUrl = String.format(API.SEARCH_URL,page,mKey);
        Log.e(TAG,newsUrl);
        x.http().get(new RequestParams(newsUrl), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                multipleStatusView.showContent();
                Log.e(TAG,result);
                if (page == 1) {
                    newsItemList.clear();
                }
                newNewsData = JsonUtils.parseNewsData(result);
                if (newNewsData != null) {
                    if (newNewsData.size() > 0) {
                        STATE = SHOW_DATA;
                        newsItemList.addAll(newNewsData);
                        setAdapter();
                    } else {
                        multipleStatusView.showEmpty();
                        STATE = NO_THING;
                    }
                } else {
                    multipleStatusView.showEmpty();
                    STATE = NO_THING;
                }
                changeStates(STATE);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {


                newsAdapter.notifyDataSetChanged();
            }
        });
    }

    //设置适配器
    private void setAdapter() {
        newsAdapter = new NewsAdapter(UiUtils.getContext(), newsItemList);
        news_lv.setAdapter(newsAdapter);
    }

    //设置监听事件
    private void setListener() {
        news_lv.setOnItemClickListener(this);
        news_lv.setOnScrollListener(this);

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                et_search.setText("");

//                newsItemList.clear();
                mKey  = tv_cancel.getText().toString();
                mNoLayout.setVisibility(View.INVISIBLE);
                news_lv.setVisibility(View.INVISIBLE);
                if (!TextUtils.isEmpty(mKey) && mKey.length() > 3) {

                    startSearch(1);

                    changeStates(STATE);

                } else {
                    STATE = NO_THING;

                    changeStates(STATE);

                }
            }
        });

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //点击条目跳转到详情界面
        Intent intent = new Intent(this, VideoNewsDetailActivity.class);
        Bundle bundle = new Bundle();
        NewsData.NewItem curItem = newsItemList.get(position);
        bundle.putInt("type", curItem.type);
        bundle.putString("newsId", curItem.news_id);
        intent.putExtras(bundle);
        startActivity(intent);
    }


    //////////////////////listview的滑动事件监听/////////////////
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //isBottom是自定义的boolean变量，用于标记是否滑动到底部
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && isBottom && !isLoadData) {
            //如果加载到底部则加载下一页的数据显示到listview中
            currentPage++;
            //将加载数据的状态设置为true
            isLoadData = true;
            String newsUrl = String.format(API.SEARCH_URL,currentPage, mKey);
            // 显示加载更多进度条
            mFootView.setVisibility(View.VISIBLE);
            x.http().get(new RequestParams(newsUrl), new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    newNewsData = JsonUtils.parseNewsData(result);
                    if(newNewsData == null){
                        mFootView.setVisibility(View.GONE);//设置隐藏进度条
                    }else{
                        if (newNewsData.size() != 0) {
                            mFootView.setVisibility(View.GONE);//设置隐藏进度条
                            newsItemList.addAll(newNewsData);
                            newsAdapter.notifyDataSetChanged();
                            //请求完数据之后将状态设为false
                            isLoadData = false;
                        }else  {
                            news_lv.removeFooterView(mFootView);
                            mFootView = UiUtils.inflate(R.layout.listview_footer_no_more);
                            news_lv.addFooterView(mFootView, null, false);
                            isLoadData = false;
                        }
                    }

                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    ToastUtil.showShort(UiUtils.getContext(),"加载失败");
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {
                    newsAdapter.notifyDataSetChanged();
                }
            });

        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //若第一个可见的item的下标+可见的条目的数量=当前listview中总的条目数量，则说明已经到达底部
        isBottom = ((firstVisibleItem + visibleItemCount) == totalItemCount);
    }

}

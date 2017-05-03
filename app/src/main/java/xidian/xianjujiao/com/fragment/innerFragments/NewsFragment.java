package xidian.xianjujiao.com.fragment.innerFragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.classic.common.MultipleStatusView;
import com.google.gson.Gson;
import com.viewpagerindicator.CirclePageIndicator;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import xidian.xianjujiao.com.R;
import xidian.xianjujiao.com.activity.VideoNewsDetailActivity;
import xidian.xianjujiao.com.adapter.HeadLinesHeaderAdapter;
import xidian.xianjujiao.com.adapter.NewsAdapter;
import xidian.xianjujiao.com.entity.ListHeaderData;
import xidian.xianjujiao.com.entity.NewsData;
import xidian.xianjujiao.com.utils.API;
import xidian.xianjujiao.com.utils.Constant;
import xidian.xianjujiao.com.utils.JsonUtils;
import xidian.xianjujiao.com.utils.NetUtils;
import xidian.xianjujiao.com.utils.ToastUtil;
import xidian.xianjujiao.com.utils.UiUtils;


/**
 * 新闻类的Fragemnt
 */
public class NewsFragment extends Fragment implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {
    @Bind(R.id.multiplestatusview)
    MultipleStatusView multiplestatusview;
    @Bind(R.id.srl_refresh)
    SwipeRefreshLayout refreshLayout;
    TextView tvTitle;// 头条新闻的标题

    CirclePageIndicator mIndicator;// 头条新闻位置指示器
    ViewPager mViewPager;
    private View view;
    private ListView news_lv;
    private NewsAdapter adapter;
    private View mHeadView;
    private static Handler mHandler;
    private View mFootView;

    // 保存全部的条目数据
    private List<NewsData.NewItem> newsItemList = new ArrayList<>();
    //Android自带下拉刷新控件
    private List<NewsData.NewItem> newNewsData;
    private int currentPage = 1;//当前页
    private boolean isBottom;//是否到底部的标记
    private boolean isLoadData = false;//判断是否已经在加载数据
    private String typeId;

    private HeadLinesHeaderAdapter topNewsAdapter;
    private List<ListHeaderData.Shuffling> mTopNewsList;
    private boolean isFirst = true;
    // 避免页数无限度增加
    private boolean hasMore = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_news, container, false);
        ButterKnife.bind(this, view);

        initView();
        setAdapter();
        setListener();
        setSwipeRefreshInfo();
        return view;
    }



    //获取控件
    private void initView() {
        Log.e("HeadlinesFragment","initView()2调用了");
        mHeadView = View.inflate(getActivity(),R.layout.banner_view, null);
        tvTitle = (TextView) mHeadView.findViewById(R.id.tv_title);
        mIndicator = (CirclePageIndicator) mHeadView.findViewById(R.id.indicator);
        mViewPager = (ViewPager) mHeadView.findViewById(R.id.vp_news);
        typeId = getArguments().getString("typeid");

        news_lv = (ListView) view.findViewById(R.id.content_view);

        //添加头部轮播控件
        news_lv.addHeaderView(mHeadView);
        //添加底部加载更多控件
        mFootView = UiUtils.inflate(R.layout.listview_footer_loading);
        mFootView.setVisibility(View.GONE);
        news_lv.addFooterView(mFootView, null, false);
        adapter = new NewsAdapter(getActivity(), newsItemList);
        //初始化图片轮播数据
        initBanner();
    }

    //初始化图片轮播数据
    private void initBanner() {
        String shufflingUrl = String.format(API.HEADLINE_SHUFFLING_URL, typeId);
        x.http().get(new RequestParams(shufflingUrl), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                ListHeaderData listHeaderData = gson.fromJson(result,ListHeaderData.class);
                mTopNewsList = listHeaderData.lunbo;
                mTopNewsList.addAll(listHeaderData.lunbo);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
                if (mTopNewsList != null) {
                    topNewsAdapter = new HeadLinesHeaderAdapter(mTopNewsList);
                    mViewPager.setAdapter(topNewsAdapter);
                    mIndicator.setViewPager(mViewPager);
                    mIndicator.setSnap(true);// 支持快照显示
                    mIndicator.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                        @Override
                        public void onPageSelected(int position) {
                            tvTitle.setText(mTopNewsList.get(position).title);
                        }
                    });

                    mIndicator.onPageSelected(0);// 让指示器重新定位到第一个点
                    tvTitle.setText(mTopNewsList.get(0).title);
                    if (mHandler == null) {
                        mHandler = new Handler() {
                            public void handleMessage(android.os.Message msg) {
                                int currentItem = mViewPager.getCurrentItem();

                                if (currentItem < mTopNewsList.size() - 1) {
                                    currentItem++;
                                } else {
                                    currentItem = 0;
                                }

                                mViewPager.setCurrentItem(currentItem);// 切换到下一个页面
                                topNewsAdapter.notifyDataSetChanged();
                                mHandler.sendEmptyMessageDelayed(0, 3000);// 继续延时3秒发消息,
                                // 形成循环
                            };
                        };

                        mHandler.sendEmptyMessageDelayed(0, 3000);// 延时3秒后发消息
                    }
                }
            }
        });


    }

    //请求新闻数据
    private void requestNewsFromSever(final int page) {

        multiplestatusview.showLoading();
        //使用xutils请求网络数据
        String newsUrl = String.format(API.NEWS_URL, typeId,page);
        x.http().get(new RequestParams(newsUrl), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                multiplestatusview.showContent();
                newNewsData = JsonUtils.parseNewsData(result);
                if (page == 1) {
                    newsItemList.clear();
                }
                if (newNewsData.isEmpty()){
                    multiplestatusview.showEmpty();
                }
                newsItemList.addAll(newNewsData);
                refreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (NetUtils.isNetConnected(getActivity())){
                    multiplestatusview.showError();
                }else {
                    multiplestatusview.showNoNetwork();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

                if (page == 1) {
                    refreshLayout.setRefreshing(false);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    //设置适配器
    private void setAdapter() {
        news_lv.setAdapter(adapter);
    }

    //设置监听事件
    private void setListener() {
        //listview的条目事件点击
        news_lv.setOnItemClickListener(this);
        //listview的滑动监听
        news_lv.setOnScrollListener(this);
        //点击重试
        multiplestatusview.setOnRetryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(Constant.DEBUG,"下载方法被调用了1");
                hasMore = true;
                isFirst = true;
                requestNewsFromSever(1);
            }
        });
    }


    /**
     * listview的点击事件
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //点击条目跳转到详情界面
        Intent intent = new Intent(getContext(), VideoNewsDetailActivity.class);
        //将Url地址获取到
        Bundle bundle = new Bundle();
        ///////////此处减-1是因为在listview头部添加了一个viewpager，
        // 造成所有listview的条目的位置都往下移了一个
        int curPosition = position -1;
        NewsData.NewItem curItem = newsItemList.get(curPosition);
        bundle.putInt("type", curItem.type);
        bundle.putString("newsId", curItem.news_id);
//        Log.e(Constant.DEBUG,"=====>"+ "" + typeid + "=====》" + ariticleId);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    //////////////////////listview的滑动事件监听/////////////////
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //isBottom是自定义的boolean变量，用于标记是否滑动到底部
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && isBottom && !isLoadData) {
            //如果加载到底部则加载下一页的数据显示到listview中
            if(hasMore)
                loadMoreData();

        }
    }

    private void loadMoreData() {

        currentPage++;
        isLoadData = true;//将加载数据的状态设置为true
        String newsUrl = String.format(API.NEWS_URL,typeId, currentPage);
        mFootView.setVisibility(View.VISIBLE);//设置进度条出现
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
                        adapter.notifyDataSetChanged();
                        //请求完数据之后将状态设为false
                        isLoadData = false;
                    }else if(newNewsData.size() == 0) {
                        news_lv.removeFooterView(mFootView);
                        mFootView = UiUtils.inflate(R.layout.listview_footer_no_more);
                        news_lv.addFooterView(mFootView, null, false);
                        isLoadData = false;
                        hasMore = false;
                    }
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.showShort(getActivity(),"加载失败");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //若第一个可见的item的下标+可见的条目的数量=当前listview中总的条目数量，则说明已经到达底部
        isBottom = ((firstVisibleItem + visibleItemCount) == totalItemCount);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
    private void setSwipeRefreshInfo() {
        if(isFirst){
            requestNewsFromSever(1);
            isFirst = false;
        }
        refreshLayout.setColorSchemeResources(R.color.blue, R.color.green, R.color.orange);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                hasMore = true;
                requestNewsFromSever(1);
            }
        });

    }

    /**
     * 判断是否滑动到顶端
     * @return
     */
    private boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (news_lv instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) news_lv;
                return absListView.getChildCount() > 0 &&
                        (absListView.getFirstVisiblePosition() > 0 ||
                                absListView.getChildAt(0).getTop() < absListView.getPaddingTop());

            } else {
                return ViewCompat.canScrollVertically(news_lv, -1) || news_lv.getScrollY() > 0;
            }

        } else {

            return ViewCompat.canScrollVertically(news_lv, -1);

        }

    }



}

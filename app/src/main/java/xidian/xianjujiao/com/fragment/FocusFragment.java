package xidian.xianjujiao.com.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import xidian.xianjujiao.com.R;
import xidian.xianjujiao.com.adapter.FocusHorizontalAdapter;
import xidian.xianjujiao.com.adapter.FocusNewsAdapter;
import xidian.xianjujiao.com.adapter.FocusViewPagerAdapter;
import xidian.xianjujiao.com.entity.FocusHeaderData;
import xidian.xianjujiao.com.entity.FocusNewsData;
import xidian.xianjujiao.com.utils.API;
import xidian.xianjujiao.com.utils.JsonUtils;
import xidian.xianjujiao.com.utils.NetUtils;
import xidian.xianjujiao.com.utils.ToastUtil;
import xidian.xianjujiao.com.utils.UiUtils;
import xidian.xianjujiao.com.view.HorizontalListView;

/**
 * Created by flyonthemap on 2017/4/29.
 */

public class FocusFragment extends Fragment implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener{


    private View view;
//    @Bind(R.id.msv_focus)
//    MultipleStatusView multiplestatusview;
    // ListView header
    private List<FocusHeaderData.FocusShuffling> focusShufflingList;
    private HorizontalListView horizontalListView;
    private FocusViewPagerAdapter focusViewPagerAdapter;
    private View headerView;
    private ViewPager focusViewPager;
    private TextView tvTitle;
    private TextView tvFocusModule;
    // FocusListView
    private ListView lvFocus;
    private View mFootView;
    private List<FocusNewsData.FocusNews> newFocusNewsList;
    private List<FocusNewsData.FocusNews> allFocusNewsList = new ArrayList<>();
    private FocusNewsAdapter focusNewsAdapter;
    private FocusNewsData focusNewsData;

    // 加载更多
    private int currentPage;
    private boolean isLoadData;
    private boolean isBottom;

    //
    private FocusHeaderData.FocusShuffling focusShuffling;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_focus, container, false);
        ButterKnife.bind(this, view);
        initView();
        setListener();
//        setSwipeRefreshInfo();
        return view;
    }

    private void initView() {
        lvFocus = (ListView) view.findViewById(R.id.lv_focus);
        headerView = UiUtils.inflate(R.layout.focus_listview_header_view);
        lvFocus.addHeaderView(headerView);
        horizontalListView  = (HorizontalListView) headerView.findViewById(R.id.hlv_overview);
        focusViewPager = (ViewPager) headerView.findViewById(R.id.vp_focus);
        tvTitle = (TextView) headerView.findViewById(R.id.tv_header_title);
        tvFocusModule = (TextView) headerView.findViewById(R.id.tv_focus_module_title);
        focusNewsAdapter = new FocusNewsAdapter(allFocusNewsList);
        //添加底部加载更多控件
        mFootView = UiUtils.inflate(R.layout.listview_footer_loading);
        mFootView.setVisibility(View.GONE);
        lvFocus.addFooterView(mFootView, null, false);
        lvFocus.setAdapter(focusNewsAdapter);
        initBanner();
    }

    //初始化图片轮播数据
    private void initBanner() {
        requestNewsFromSever(1);
        x.http().get(new RequestParams(API.FOCUS_SHUFFLING_URL), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                FocusHeaderData focusHeaderData = gson.fromJson(result,FocusHeaderData.class);
                focusShufflingList = focusHeaderData.list;
                if (focusShufflingList != null) {
                    focusViewPagerAdapter = new FocusViewPagerAdapter(focusShufflingList);
                    focusViewPager.setAdapter(focusViewPagerAdapter);
                    FocusHorizontalAdapter customArrayAdapter = new FocusHorizontalAdapter( focusShufflingList);
                    horizontalListView.setAdapter(customArrayAdapter);
                    tvTitle.setText(focusShufflingList.get(0).title);

                }
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

    //设置监听事件
    private void setListener() {

        //listview的条目事件点击
        lvFocus.setOnItemClickListener(this);
        //listview的滑动监听
        lvFocus.setOnScrollListener(this);
        horizontalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                focusShuffling = focusShufflingList.get(position);
                tvTitle.setText(focusShuffling.create_time + "  " + focusShuffling.title);
                focusViewPager.setCurrentItem(position);
            }
        });
    }

    private void requestNewsFromSever(final int page) {

//        multiplestatusview.showLoading();
        //使用xutils请求网络数据
        String newsUrl = String.format(API.FOCUS_NEWS_URL,page);
        x.http().get(new RequestParams(newsUrl), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
//                multiplestatusview.showContent();
                focusNewsData = JsonUtils.parseFocusNewsData(result);
                newFocusNewsList = focusNewsData.news;
                if (page == 1) {
                    allFocusNewsList.clear();
                }
                allFocusNewsList.addAll(newFocusNewsList);
                tvFocusModule.setText(focusNewsData.module_name);
//                ptrLayout.refreshComplete();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (NetUtils.isNetConnected(getActivity())){
//                    multiplestatusview.showError();
                }else {
//                    multiplestatusview.showNoNetwork();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {


                focusNewsAdapter.notifyDataSetChanged();
            }
        });
    }
    /**
     * 判断是否滑动到顶端
     * @return
     */
    private boolean canChildScrollUp() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (lvFocus instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) lvFocus;
                return absListView.getChildCount() > 0 &&
                        (absListView.getFirstVisiblePosition() > 0 ||
                                absListView.getChildAt(0).getTop() < absListView.getPaddingTop());

            } else {
                return ViewCompat.canScrollVertically(lvFocus, -1) || lvFocus.getScrollY() > 0;
            }

            } else {

                return ViewCompat.canScrollVertically(lvFocus, -1);

            }

    }
    //////////////////////listview的滑动事件监听/////////////////
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //isBottom是自定义的boolean变量，用于标记是否滑动到底部
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && isBottom && !isLoadData) {
            //如果加载到底部则加载下一页的数据显示到listview中
            loadMoreData();

        }
    }

    private void loadMoreData() {

        currentPage++;
        isLoadData = true;//将加载数据的状态设置为true
        final String newsUrl = String.format(API.FOCUS_NEWS_URL,currentPage);
        mFootView.setVisibility(View.VISIBLE);//设置进度条出现
        x.http().get(new RequestParams(newsUrl), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("--->",newsUrl);
                focusNewsData = JsonUtils.parseFocusNewsData(result);
                newFocusNewsList = focusNewsData.news;
                if(newFocusNewsList == null){
                    mFootView.setVisibility(View.GONE);//设置隐藏进度条
                }else{
                    if (newFocusNewsList.size() != 0) {
                        mFootView.setVisibility(View.GONE);//设置隐藏进度条
                        allFocusNewsList.addAll(newFocusNewsList);
                        focusNewsAdapter.notifyDataSetChanged();
                        //请求完数据之后将状态设为false
                        isLoadData = false;
                    }else if(newFocusNewsList.size() == 0) {
                        lvFocus.removeFooterView(mFootView);
                        mFootView = UiUtils.inflate(R.layout.listview_footer_no_more);
                        lvFocus.addFooterView(mFootView, null, false);
                        isLoadData = false;
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}

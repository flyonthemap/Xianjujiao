package xidian.xianjujiao.com.fragment.liveInnerFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.classic.common.MultipleStatusView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import xidian.xianjujiao.com.R;
import xidian.xianjujiao.com.activity.LiveVideoActivity;
import xidian.xianjujiao.com.activity.VideoNewsDetailActivity;
import xidian.xianjujiao.com.adapter.LiveNewsAdapter;
import xidian.xianjujiao.com.entity.LiveNewsData;
import xidian.xianjujiao.com.entity.NewsData;
import xidian.xianjujiao.com.utils.API;
import xidian.xianjujiao.com.utils.Constant;
import xidian.xianjujiao.com.utils.JsonUtils;
import xidian.xianjujiao.com.utils.NetUtils;
import xidian.xianjujiao.com.utils.ToastUtil;
import xidian.xianjujiao.com.utils.UiUtils;

/**
 * Created by flyonthemap on 2017/5/2.
 */

public class LiveModuleFragment extends Fragment implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener{
    private final static String TAG = "LiveModuleFragment";
    @Bind(R.id.live_module_msv)
    MultipleStatusView multipleStatusView;
    @Bind(R.id.live_module_srl_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.content_view)
    ListView lv_live_module;

    // 从LiveFragment传入的参数
    private String moduleId;
    private String secondName;

    // 直播条目数据集
    private List<LiveNewsData.LiveNewsItem> allLiveNewsDataList = new ArrayList<>();
    private List<LiveNewsData.LiveNewsItem> newLiveNewsDataList;
    // 直播条目适配器
    private LiveNewsAdapter adapter;


    // 加载更多
    public boolean isFirst = true;
    public boolean hasMore = true;
    private int currentPage = 1;
    private boolean isBottom ;
    private boolean isLoadData;
    private View mFootView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        moduleId = getArguments().getString("moduleId");
        secondName = getArguments().getString("secondName");
//        Log.e(TAG,secondName);
        View view = inflater.inflate(R.layout.fragment_live_module, container, false);
        ButterKnife.bind(this,view);
        initListView();
        setAdapter();
        setListener();
        setSwipeRefreshInfo();
        return view;
    }

    private void initListView() {
        View mHeaderView = UiUtils.inflate(R.layout.live_module_header);
        TextView tvModuleTitle = (TextView) mHeaderView.findViewById(R.id.live_module_title);
        tvModuleTitle.setText(secondName);
        lv_live_module.addHeaderView(mHeaderView);
        mFootView = UiUtils.inflate(R.layout.listview_footer_loading);
        lv_live_module.addFooterView(mFootView, null, false);
        mFootView.setVisibility(View.GONE);
    }

    //请求新闻数据
    private void requestNewsFromSever(final int page) {

        multipleStatusView.showLoading();
        //使用xutils请求网络数据
        String newsUrl = String.format(API.LIVE_MODULE_NEWS_LIST, moduleId,page);
        Log.e(TAG,newsUrl);
        x.http().get(new RequestParams(newsUrl), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG,result);
                multipleStatusView.showContent();
                newLiveNewsDataList = JsonUtils.parseLiveNewsData(result);
                if (page == 1) {
                    allLiveNewsDataList.clear();
                }
                if (newLiveNewsDataList.isEmpty()){
                    multipleStatusView.showEmpty();
                }
                allLiveNewsDataList.addAll(newLiveNewsDataList);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (NetUtils.isNetConnected(getActivity())){
                    multipleStatusView.showError();
                }else {
                    multipleStatusView.showNoNetwork();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

                if (page == 1) {
                    swipeRefreshLayout.setRefreshing(false);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }
    //设置适配器
    private void setAdapter() {
        adapter = new LiveNewsAdapter(allLiveNewsDataList);
        lv_live_module.setAdapter(adapter);
    }

    //设置监听事件
    private void setListener() {
        //listview的条目事件点击
        lv_live_module.setOnItemClickListener(this);
        //listview的滑动监听
        lv_live_module.setOnScrollListener(this);
        //点击重试
        multipleStatusView.setOnRetryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UiUtils.getContext(), "您点击了重试视图", Toast.LENGTH_SHORT).show();
                hasMore = true;
                isFirst = true;
                currentPage = 1;
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
        Intent intent = new Intent(getContext(), LiveVideoActivity.class);
        //将Url地址获取到
        Bundle bundle = new Bundle();

        int curPosition = position -1;
        LiveNewsData.LiveNewsItem curItem = allLiveNewsDataList.get(curPosition);
        bundle.putString("videoUrl",curItem.url);
        bundle.putString("imageUrl", curItem.thumb_image);
        bundle.putString("title",curItem.title);
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
        String newsUrl = String.format(API.LIVE_MODULE_NEWS_LIST, moduleId,currentPage);
        mFootView.setVisibility(View.VISIBLE);//设置进度条出现
        x.http().get(new RequestParams(newsUrl), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                newLiveNewsDataList = JsonUtils.parseLiveNewsData(result);
                if(newLiveNewsDataList == null){
                    mFootView.setVisibility(View.GONE);//设置隐藏进度条
                }else{
                    if (newLiveNewsDataList.size() != 0) {
                        mFootView.setVisibility(View.GONE);//设置隐藏进度条
                        allLiveNewsDataList.addAll(newLiveNewsDataList);
                        adapter.notifyDataSetChanged();
                        //请求完数据之后将状态设为false
                        isLoadData = false;
                    }else if(newLiveNewsDataList.size() == 0) {
                        lv_live_module.removeFooterView(mFootView);
                        mFootView = UiUtils.inflate(R.layout.listview_footer_no_more);
                        lv_live_module.addFooterView(mFootView, null, false);
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
        Log.e(Constant.DEBUG,"请求次数----->");

        swipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.green, R.color.orange);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                hasMore = true;
                requestNewsFromSever(1);
            }
        });

    }

}

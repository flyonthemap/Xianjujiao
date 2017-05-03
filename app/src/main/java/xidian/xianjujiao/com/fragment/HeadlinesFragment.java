package xidian.xianjujiao.com.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.classic.common.MultipleStatusView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import xidian.xianjujiao.com.R;
import xidian.xianjujiao.com.activity.ChannelActivity;
import xidian.xianjujiao.com.activity.SearchActivity;
import xidian.xianjujiao.com.adapter.LiveFragmentPagerAdapter;
import xidian.xianjujiao.com.entity.ChannelItem;
import xidian.xianjujiao.com.fragment.innerFragments.NewsFragment;
import xidian.xianjujiao.com.manager.ChannelManager;
import xidian.xianjujiao.com.utils.API;
import xidian.xianjujiao.com.utils.JsonUtils;
import xidian.xianjujiao.com.utils.NetUtils;
import xidian.xianjujiao.com.utils.UiUtils;

/**
 * 文章的Fragment
 */
public class HeadlinesFragment extends Fragment {

    @Bind(R.id.headline_msv)
    MultipleStatusView multiplestatusview;
    @Bind(R.id.headline_tabs)
    TabLayout tabLayout;
    public final static int CHANNELREQUEST = 1;


    private ViewPager vp_headline;
    private TextView tv_title;
    private ImageButton ibSearch;
    private ImageButton ibMoreChannel;
    // channelId的集合
    private List<ChannelItem> channelItemList;
    private LiveFragmentPagerAdapter pagerAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_headlines, container, false);
        ButterKnife.bind(this, view);
        initData();
        initView(view);
        setAdapter();
        setListener();
        return view;
    }

    //获取控件
    private void initView(View view) {
        Log.e("HeadlinesFragment","initView()调用了");
        ibSearch = (ImageButton) view.findViewById(R.id.ib_search);
        //获取到标题栏控件
        tv_title = (TextView) view.findViewById(R.id.title);
        tv_title.setText("西安聚焦");
        vp_headline = (ViewPager) view.findViewById(R.id.vp_headline);

        ibMoreChannel = (ImageButton) view.findViewById(R.id.btn_more_channel);

    }

    //初始化数据
    private void initData() {
        multiplestatusview.showLoading();
        x.http().get(new RequestParams(API.CHANNEL_LIST_URL), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                multiplestatusview.showContent();
                JsonUtils.parseChannelJson(result);

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
                channelItemList = ChannelManager.getChannelManager().getUserChannel();
                for (int i = 0; i < channelItemList.size(); i++) {
                    addPage(channelItemList.get(i));
                }

            }
        });

    }

    //设置适配器
    private void setAdapter() {
        pagerAdapter = new LiveFragmentPagerAdapter(getFragmentManager(),vp_headline,tabLayout);
        vp_headline.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(vp_headline);
        vp_headline.setOffscreenPageLimit(4);
    }

    //设置监听
    private void setListener() {

        ibMoreChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UiUtils.getContext(), ChannelActivity.class);
                getActivity().startActivityForResult(intent,CHANNELREQUEST);
            }
        });
        ibSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UiUtils.getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(vp_headline) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                vp_headline.setCurrentItem(tab.getPosition());
            }
        });
    }
    public void addPage(ChannelItem channelItem) {
        Bundle bundle = new Bundle();
        bundle.putString("typeid", channelItem.getId());
        NewsFragment fragmentChild = new NewsFragment();
        fragmentChild.setArguments(bundle);
        pagerAdapter.addFrag(fragmentChild, channelItem.getName());
        pagerAdapter.notifyDataSetChanged();
        if (pagerAdapter.getCount() > 0)
            tabLayout.setupWithViewPager(vp_headline);
        vp_headline.setCurrentItem(0);
        if(tabLayout.getTabCount()>6){
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }else {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }
    }

}

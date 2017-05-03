package xidian.xianjujiao.com.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.viewpagerindicator.TabPageIndicator;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import xidian.xianjujiao.com.MainActivity;
import xidian.xianjujiao.com.R;
import xidian.xianjujiao.com.activity.ChannelActivity;
import xidian.xianjujiao.com.activity.SearchActivity;
import xidian.xianjujiao.com.adapter.TabPageIndicatorAdapter;
import xidian.xianjujiao.com.db.SQLHelper;
import xidian.xianjujiao.com.entity.ChannelItem;
import xidian.xianjujiao.com.fragment.innerFragments.CommondFragment;
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

    public final static int CHANNELREQUEST = 1;
    /** 调整返回的RESULTCODE */
    private View view;
    private ViewPager article_viewpager;
    private TabPageIndicator indicator;
    private TabPageIndicatorAdapter adapter;
    //fragment的集合
    private List<Fragment> fragments = new ArrayList<>();
    private TextView tv_title;
    private ImageButton main_action_menu;
    private ImageButton ibSearch;
    private ImageButton ibMoreChannel;
    // channelId的集合
    private List<ChannelItem> channelItemList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_headlines, container, false);
        initData();
        initView();
        setAdapter();
        setListener();
        return view;
    }

    //获取控件
    private void initView() {
        //隐藏toolbar menu控件

        ibSearch = (ImageButton) view.findViewById(R.id.ib_search);
        //获取到标题栏控件
        tv_title = (TextView) view.findViewById(R.id.title);
        tv_title.setText("文章");
        article_viewpager = (ViewPager) view.findViewById(R.id.article_viewpager);
        //实例化TabPageIndicator然后设置ViewPager与之关联
        indicator = (TabPageIndicator) view.findViewById(R.id.article_indicator);

        ibMoreChannel = (ImageButton) view.findViewById(R.id.btn_more_channel);

    }

    //初始化数据
    private void initData() {

        x.http().get(new RequestParams(API.CHANNEL_LIST_URL), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                JsonUtils.parseChannelJson(result);
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
        channelItemList = ChannelManager.getChannelManager().getUserChannel();
        for (int i = 0; i < channelItemList.size(); i++) {
            NewsFragment fragment = new NewsFragment();//杂谈
            Bundle bundle = new Bundle();
            bundle.putString("typeid", channelItemList.get(i).getId());
            fragment.setArguments(bundle);
            fragments.add(fragment);
        }
    }

    //设置适配器
    private void setAdapter() {
        //实例化适配器
        adapter = new TabPageIndicatorAdapter(getFragmentManager(), fragments,ChannelManager.getChannelManager().getUserChannel());
        //设置适配器
        article_viewpager.setAdapter(adapter);
        indicator.setViewPager(article_viewpager, 0);
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
    }



}

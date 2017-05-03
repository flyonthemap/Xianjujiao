package xidian.xianjujiao.com.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import xidian.xianjujiao.com.R;
import xidian.xianjujiao.com.adapter.LiveFragmentPagerAdapter;
import xidian.xianjujiao.com.entity.LiveModuleName;
import xidian.xianjujiao.com.fragment.liveInnerFragments.LiveModuleFragment;
import xidian.xianjujiao.com.utils.API;
import xidian.xianjujiao.com.utils.JsonUtils;

/**
 * Created by flyonthemap on 2017/5/2.
 */

public class LiveFragment extends Fragment {
    private String TAG = "LiveFragment";
    //标题

    private ViewPager vp_live;
    private TabLayout tabLayout;
    //fragment的集合
    private TextView tv_title;
    private LiveFragmentPagerAdapter pagerAdapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    private List<LiveModuleName.ModuleData> moduleDataList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live, container, false);
        initData();
        initView(view);
        setAdapter();
        setListener();
        return view;
    }

    private void setListener() {

        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(vp_live) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                vp_live.setCurrentItem(tab.getPosition());
                int selectedTabPosition = vp_live.getCurrentItem();
                Log.d(TAG, "Selected " + tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                Log.d(TAG, "Unselected " + tab.getPosition());
            }
        });
    }

    //获取控件
    private void initView(View view) {
        //获取到标题栏控件
        tv_title = (TextView) view.findViewById(R.id.title);
        tv_title.setText("直播+");
        vp_live = (ViewPager) view.findViewById(R.id.live_viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.live_tabs);
    }

    //初始化数据
    private void initData() {
        x.http().get(new RequestParams(API.LIVE_MODULE_NAME_LIST), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                moduleDataList = JsonUtils.parseLiveModuleName(result);
                if(moduleDataList != null){
                    for (int i = 0; i < moduleDataList.size(); i++) {
                       addPage(moduleDataList.get(i).module_name);
                    }
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

    //设置适配器
    private void setAdapter() {
        pagerAdapter = new LiveFragmentPagerAdapter(getFragmentManager(),vp_live,tabLayout);
        vp_live.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(vp_live);


    }
    public void addPage(String pageName) {
        Bundle bundle = new Bundle();
        bundle.putString("data", pageName);
        LiveModuleFragment fragmentChild = new LiveModuleFragment();
        fragmentChild.setArguments(bundle);
        pagerAdapter.addFrag(fragmentChild, pageName);
        pagerAdapter.notifyDataSetChanged();
        if (pagerAdapter.getCount() > 0) tabLayout.setupWithViewPager(vp_live);

        vp_live.setCurrentItem(0);
        if(tabLayout.getTabCount()>6){
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }else {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }
    }



}

package xidian.xianjujiao.com.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import xidian.xianjujiao.com.R;
import xidian.xianjujiao.com.adapter.LiveFragmentPagerAdapter;
import xidian.xianjujiao.com.entity.LiveModuleName;
import xidian.xianjujiao.com.fragment.liveInnerFragments.LiveModuleFragment;
import xidian.xianjujiao.com.utils.API;
import xidian.xianjujiao.com.utils.Constant;
import xidian.xianjujiao.com.utils.JsonUtils;
import xidian.xianjujiao.com.utils.NetUtils;

/**
 * Created by flyonthemap on 2017/5/2.
 */

public class LiveFragment extends Fragment {
    private String TAG = "LiveFragment";
    //标题
    @Bind(R.id.live_msv)
    MultipleStatusView multiplestatusview;
    @Bind(R.id.live_viewpager)
    ViewPager vp_live;
    @Bind(R.id.live_tabs)
    TabLayout tabLayout;

    private LiveFragmentPagerAdapter pagerAdapter;


    private List<LiveModuleName.ModuleData> moduleDataList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live, container, false);

        ButterKnife.bind(this, view);
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
            }
        });
    }

    //获取控件
    private void initView(View view) {
        //获取到标题栏控件
        TextView tv_title = (TextView) view.findViewById(R.id.title);
        tv_title.setText("直播+");
        ImageButton ibSearch = (ImageButton) view.findViewById(R.id.ib_search);
        ibSearch.setVisibility(View.GONE);
    }

    //初始化数据
    private void initData() {
        multiplestatusview.showLoading();
        x.http().get(new RequestParams(API.LIVE_MODULE_NAME_LIST), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                multiplestatusview.showContent();
                moduleDataList = JsonUtils.parseLiveModuleName(result);
                if(moduleDataList != null){
                    for (int i = 0; i < moduleDataList.size(); i++) {
                        // 动态添加Tab标签
                        addPage(moduleDataList.get(i));
                    }
                }
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
            }

        });








    }

    //设置适配器
    private void setAdapter() {
        pagerAdapter = new LiveFragmentPagerAdapter(getFragmentManager(),vp_live,tabLayout);
        vp_live.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(vp_live);


    }
    public void addPage(LiveModuleName.ModuleData moduleData) {
        Bundle bundle = new Bundle();
        bundle.putString("moduleId", moduleData.module_id);
        bundle.putString("secondName",moduleData.secondName);
        LiveModuleFragment fragmentChild = new LiveModuleFragment();
        fragmentChild.setArguments(bundle);
        pagerAdapter.addFrag(fragmentChild, moduleData.module_name);
        pagerAdapter.notifyDataSetChanged();
        if (pagerAdapter.getCount() > 0)
            tabLayout.setupWithViewPager(vp_live);
        vp_live.setCurrentItem(0);
        if(tabLayout.getTabCount()>6){
            tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        }else {
            tabLayout.setTabMode(TabLayout.MODE_FIXED);
        }
        multiplestatusview.setOnRetryClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(Constant.DEBUG,"下载方法被调用了1");
                multiplestatusview.showLoading();
            }
        });
    }



}

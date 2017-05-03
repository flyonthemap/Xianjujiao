package xidian.xianjujiao.com.adapter;

/**
 * Created by flyonthemap on 2017/5/2.
 */


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.List;

import xidian.xianjujiao.com.entity.ChannelItem;
import xidian.xianjujiao.com.entity.LiveModuleName;
import xidian.xianjujiao.com.manager.ChannelManager;

/**
 * Created by flyonthemap on 2017/4/23.
 */

public class LiveTabPageIndicatorAdapter extends FragmentPagerAdapter {
    //fragments集合
    private List<Fragment> fragments;
    private List<LiveModuleName.ModuleData> moduleDataList;

    public LiveTabPageIndicatorAdapter(FragmentManager fm, List<Fragment> fragments, List<LiveModuleName.ModuleData> moduleDataList) {
        super(fm);
        this.fragments = fragments;
        this.moduleDataList = moduleDataList;

    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return moduleDataList.size();
    }

    //获取当前位置的标题
    @Override
    public CharSequence getPageTitle(int position) {
        return moduleDataList.get(position).module_name;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }
    public void setFragments(List<Fragment> list){
        fragments = list;
    }

}
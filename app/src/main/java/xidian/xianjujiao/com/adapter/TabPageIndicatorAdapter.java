package xidian.xianjujiao.com.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.List;

import xidian.xianjujiao.com.entity.ChannelItem;
import xidian.xianjujiao.com.manager.ChannelManager;

/**
 * Created by flyonthemap on 2017/4/23.
 */

public class TabPageIndicatorAdapter extends FragmentPagerAdapter {
    //fragments集合
    private List<Fragment> fragments;
    //标题
//    private String[] title;
    private List<ChannelItem> defaultChannel ;

    public TabPageIndicatorAdapter(FragmentManager fm, List<Fragment> fragments, List<ChannelItem> defaultChannel) {
        super(fm);
        this.fragments = fragments;
        if(defaultChannel != null)
        this.defaultChannel = defaultChannel;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return defaultChannel.size();
    }

    //获取当前位置的标题
    @Override
    public CharSequence getPageTitle(int position) {
        return defaultChannel.get(position % defaultChannel.size()).getName();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
//        super.destroyItem(container, position, object);
    }
}

package xidian.xianjujiao.com.adapter;

import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.xutils.x;

import java.util.List;

import xidian.xianjujiao.com.R;
import xidian.xianjujiao.com.entity.FocusHeaderData;
import xidian.xianjujiao.com.entity.ListHeaderData;
import xidian.xianjujiao.com.utils.PicassoUtils;
import xidian.xianjujiao.com.utils.UiUtils;

/**
 * Created by flyonthemap on 2017/4/26.
 */

public class FocusViewPagerAdapter extends PagerAdapter {

    private List<FocusHeaderData.FocusShuffling> shufflings;
    public FocusViewPagerAdapter(List<FocusHeaderData.FocusShuffling> shufflings) {
        this.shufflings = shufflings;
    }

    @Override
    public int getCount() {
        return shufflings.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView image = new ImageView(x.app());
        image.setScaleType(ImageView.ScaleType.FIT_XY);// 基于控件大小填充图片
        FocusHeaderData.FocusShuffling topNewsData = shufflings.get(position);
//            x.image().bind(image,topNewsData.thumb_image);
        PicassoUtils.loadImageWithHolder(topNewsData.thumb_image, R.drawable.default_image,image);
        container.addView(image);
        return image;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
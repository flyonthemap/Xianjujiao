package xidian.xianjujiao.com.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.xutils.x;

import java.util.List;

import xidian.xianjujiao.com.R;
import xidian.xianjujiao.com.activity.AudioNewsDetailActivity;
import xidian.xianjujiao.com.activity.NewVideoNewsDetail;
import xidian.xianjujiao.com.activity.WordNewsDetailActivity;
import xidian.xianjujiao.com.entity.ListHeaderData;
import xidian.xianjujiao.com.utils.PicassoUtils;
import xidian.xianjujiao.com.utils.UiUtils;

/**
 * Created by flyonthemap on 2017/4/26.
 */

public class HeadLinesHeaderAdapter extends PagerAdapter {
    // 新闻的类型
    private static final int WORD_NEWS = 1;
    private static final int AUDIO_NEWS = 2;
    private static final int VIDEO_NEWS = 3;
    private List<ListHeaderData.Shuffling> shufflings;
    public HeadLinesHeaderAdapter(List<ListHeaderData.Shuffling> shufflings) {
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
        final int curPosition = position;
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListHeaderData.Shuffling shufflingItem = shufflings.get(curPosition);
                Bundle bundle = new Bundle();
                bundle.putString("newsId", shufflingItem.news_id);
                Intent intent;
                switch (shufflingItem.type){
                    case  WORD_NEWS:
                        intent = new Intent(UiUtils.getContext(), WordNewsDetailActivity.class);
                        break;
                    case AUDIO_NEWS:
                        intent = new Intent(UiUtils.getContext(), AudioNewsDetailActivity.class);
                        break;
                    case VIDEO_NEWS:
                        intent = new Intent(UiUtils.getContext(), NewVideoNewsDetail.class);
                        break;
                    default:
                        intent = new Intent();
                        break;

                }
                intent.putExtras(bundle);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                UiUtils.getContext().startActivity(intent);
            }
        });
        ListHeaderData.Shuffling topNewsData = shufflings.get(position);
        PicassoUtils.loadImageWithHolder(topNewsData.thumb_image, R.drawable.default_image,image);
        container.addView(image);
        return image;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
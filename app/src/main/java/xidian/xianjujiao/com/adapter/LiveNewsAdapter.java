package xidian.xianjujiao.com.adapter;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import xidian.xianjujiao.com.R;
import xidian.xianjujiao.com.entity.FocusNewsData;
import xidian.xianjujiao.com.entity.LiveNewsData;
import xidian.xianjujiao.com.utils.PicassoUtils;
import xidian.xianjujiao.com.utils.UiUtils;

/**
 * Created by flyonthemap on 2017/5/4.
 * 直播条目
 */

public class LiveNewsAdapter extends BaseAdapter {
    private static final String IMAGE_URL = "http://vimg3.ws.126.net/image/snapshot/2016/11/C/T/VC628QHCT.jpg";
    private List<LiveNewsData.LiveNewsItem> liveNewsItemList;
    private LiveNewsData.LiveNewsItem liveNewsItem;

    public LiveNewsAdapter(List<LiveNewsData.LiveNewsItem> liveNewsItemList) {
        this.liveNewsItemList = liveNewsItemList;
    }

    @Override
    public int getCount() {
        return liveNewsItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return liveNewsItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = UiUtils.inflate(R.layout.live_news_item);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            //获取缓存布局
            viewHolder = (ViewHolder) convertView.getTag();
        }
        liveNewsItem = liveNewsItemList.get(position);
        viewHolder.tvLiveNewsTitle.setText(liveNewsItem.title);

        viewHolder.tvLiveTime.setText(liveNewsItem.live_time);
        viewHolder.tvLiveDesc.setText(liveNewsItem.jianjie);
        viewHolder.ivLiveNews.setImageResource(R.drawable.default_image);
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))//图片大小
                .setRadius(DensityUtil.dip2px(5))//ImageView圆角半径
                .setCrop(true)// 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.product_default)//加载中默认显示图片
                .setFailureDrawableId(R.drawable.product_default)//加载失败后默认显示图片
                .build();
        x.image().bind(viewHolder.ivLiveNews,liveNewsItem.thumb_image,imageOptions);

        PicassoUtils.loadImageWithHolder(liveNewsItem.thumb_image,R.drawable.product_default,viewHolder.ivLiveNews);
        return convertView;

    }

    //创建一个ViewHolder保存converview的布局
    static class ViewHolder {
        @Bind(R.id.live_news_title)
        TextView tvLiveNewsTitle;
        @Bind(R.id.live_image)
        ImageView ivLiveNews;//图片
        @Bind(R.id.live_news_desc)
        TextView tvLiveDesc;
        @Bind(R.id.live_time)
        TextView tvLiveTime;
        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
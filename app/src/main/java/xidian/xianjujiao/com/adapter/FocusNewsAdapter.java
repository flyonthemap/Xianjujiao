package xidian.xianjujiao.com.adapter;


import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import xidian.xianjujiao.com.R;
import xidian.xianjujiao.com.entity.FocusNewsData;
import xidian.xianjujiao.com.utils.PicassoUtils;
import xidian.xianjujiao.com.utils.UiUtils;

public class FocusNewsAdapter extends BaseAdapter {
    private List<FocusNewsData.FocusNews> focusNewsList;
    private FocusNewsData.FocusNews focusNewsItem;

    public FocusNewsAdapter(List<FocusNewsData.FocusNews> focusNewsList) {
        this.focusNewsList = focusNewsList;
    }

    @Override
    public int getCount() {
        return focusNewsList.size();
    }

    @Override
    public Object getItem(int position) {
        return focusNewsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
         ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = UiUtils.inflate(R.layout.focus_news_item);
            viewHolder = new FocusNewsAdapter.ViewHolder();
            viewHolder.tvFocusNewsTime = (TextView) convertView.findViewById(R.id.tv_news_time);
            viewHolder.tvFocusNewsTitle = (TextView) convertView.findViewById(R.id.tv_focus_news_title);
            viewHolder.ivFocusNews = (ImageView) convertView.findViewById(R.id.iv_focus_news);
            //设置tag
            convertView.setTag(viewHolder);
        } else {
            //获取缓存布局
            viewHolder = (ViewHolder) convertView.getTag();
        }
         focusNewsItem = focusNewsList.get(position);
        viewHolder.tvFocusNewsTitle.setText(focusNewsItem.title);

        viewHolder.tvFocusNewsTime.setText(focusNewsItem.create_time);//文章发布时间
        PicassoUtils.loadImageWithHolder(focusNewsItem.thumb_image,R.drawable.product_default,viewHolder.ivFocusNews);
        return convertView;
    }

    //创建一个ViewHolder保存converview的布局
    class ViewHolder {
        ImageView ivFocusNews;//图片
        TextView tvFocusNewsTitle, tvFocusNewsTime;
    }
}

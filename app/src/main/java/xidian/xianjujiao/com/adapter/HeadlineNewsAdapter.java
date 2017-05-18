package xidian.xianjujiao.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;



import org.xutils.x;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import xidian.xianjujiao.com.R;
import xidian.xianjujiao.com.entity.ChapterListItem;
import xidian.xianjujiao.com.entity.NewsData;
import xidian.xianjujiao.com.utils.API;
import xidian.xianjujiao.com.utils.DateUtils;
import xidian.xianjujiao.com.utils.PicassoUtils;
import xidian.xianjujiao.com.utils.UiUtils;

/**
 * Created by xhb on 2016/1/19.
 * Listview的自定义适配器
 */
public class HeadlineNewsAdapter extends BaseAdapter {
    private List<NewsData.NewsItem> chapterListItems;

    public HeadlineNewsAdapter( List<NewsData.NewsItem> chapterListItems) {
        this.chapterListItems = chapterListItems;

    }

    @Override
    public int getCount() {
        return chapterListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return chapterListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = UiUtils.inflate(R.layout.listview_item_layout);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            //获取缓存布局
            viewHolder = (ViewHolder) convertView.getTag();
        }
        NewsData.NewsItem chapterListItem = chapterListItems.get(position);
        viewHolder.title.setText(chapterListItem.title);
        PicassoUtils.loadImageWithHolder(chapterListItem.thumb_image,R.drawable.default_image,viewHolder.iv);
        if(chapterListItem.top == 0){
            viewHolder.btnStick.setVisibility(View.GONE);
        }
        switch (chapterListItem.type){
            case 1:
                viewHolder.btnNewsType.setText("文字新闻");
                break;
            case 2:
                viewHolder.btnNewsType.setText("听新闻");
                break;
            case 3:
                viewHolder.btnNewsType.setText("视频新闻");
                break;
        }

        return convertView;
    }

    //创建一个ViewHolder保存converview的布局
     static class ViewHolder {
        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
        @Bind(R.id.headline_iv)
        ImageView iv;//图片
        @Bind(R.id.stick)
        TextView btnStick;
        @Bind(R.id.headline_news_type)
        TextView btnNewsType;
        @Bind(R.id.headline_news_title)
        //标题、日期、评论数、文章id、分类id、文章地址
        TextView title;
    }
}

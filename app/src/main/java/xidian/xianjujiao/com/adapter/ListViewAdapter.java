package xidian.xianjujiao.com.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



import org.xutils.x;

import java.util.List;

import xidian.xianjujiao.com.R;
import xidian.xianjujiao.com.entity.ChapterListItem;
import xidian.xianjujiao.com.utils.API;
import xidian.xianjujiao.com.utils.DateUtils;

/**
 * Created by xhb on 2016/1/19.
 * Listview的自定义适配器
 */
public class ListViewAdapter extends BaseAdapter {
    private Context context;
    private List<ChapterListItem> chapterListItems;
    private LayoutInflater mLayoutInflater;

    public ListViewAdapter(Context context, List<ChapterListItem> chapterListItems) {
        this.context = context;
        this.chapterListItems = chapterListItems;
        if (mLayoutInflater == null) {
            mLayoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.search_item_layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.date = (TextView) convertView.findViewById(R.id.date);
            viewHolder.comment = (TextView) convertView.findViewById(R.id.comment);
            viewHolder.tv_id = (TextView) convertView.findViewById(R.id.tv_id);
            viewHolder.tv_typeId = (TextView) convertView.findViewById(R.id.tv_typeid);
            viewHolder.tv_url = (TextView) convertView.findViewById(R.id.tv_url);
            viewHolder.iv = (ImageView) convertView.findViewById(R.id.iv);
            //设置tag
            convertView.setTag(viewHolder);
        } else {
            //获取缓存布局
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ChapterListItem chapterListItem = chapterListItems.get(position);
        viewHolder.title.setText(chapterListItem.getTitle());
        //格式化时间
        String senddate = chapterListItem.getSenddate();
        String time = DateUtils.dateFormat(senddate);
        viewHolder.date.setText(time);//文章发布时间
        viewHolder.comment.setText(chapterListItem.getFeedback());//评论数
        viewHolder.tv_id.setText(chapterListItem.getId());//文章id
        viewHolder.tv_typeId.setText(chapterListItem.getTypeid());//文章分类id
        viewHolder.tv_url.setText(chapterListItem.getArcurl());//文章URl
        viewHolder.iv.setImageResource(R.drawable.product_default);//设置默认图片
        final ImageView iv = viewHolder.iv;
        //获取到图片地址
        String litpic = chapterListItem.getLitpic();
        //如果图片地址为空，则设置默认图片
        if (litpic == null) {
            iv.setImageResource(R.drawable.product_default);
        }
        //地址拼接
        String imageUrl = API.DMGEAME_URL + litpic;
        //下载图片，优先使用本地缓存图片
        x.image().bind(iv,imageUrl);
        return convertView;
    }

    //创建一个ViewHolder保存converview的布局
    class ViewHolder {
        ImageView iv;//图片
        //标题、日期、评论数、文章id、分类id、文章地址
        TextView title, date, comment, tv_id, tv_typeId, tv_url;
    }
}

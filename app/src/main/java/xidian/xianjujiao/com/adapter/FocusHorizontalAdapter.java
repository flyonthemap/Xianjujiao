package xidian.xianjujiao.com.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import xidian.xianjujiao.com.R;
import xidian.xianjujiao.com.entity.FocusHeaderData;
import xidian.xianjujiao.com.utils.PicassoUtils;
import xidian.xianjujiao.com.utils.UiUtils;

/**
 * Created by flyonthemap on 2017/4/30.
 * 水平缩略图的适配器
 */

public class FocusHorizontalAdapter extends BaseAdapter {
    private List<FocusHeaderData.FocusShuffling> focusList;
    private FocusHeaderData.FocusShuffling shufflingItem;
    public FocusHorizontalAdapter(List<FocusHeaderData.FocusShuffling> focusList){
        this.focusList = focusList;
    }
    @Override
    public int getCount() {
        return focusList.size();
    }

    @Override
    public Object getItem(int position) {
        return focusList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FocusHorizontalAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = UiUtils.inflate(R.layout.focus_hlistview_item);
            viewHolder = new FocusHorizontalAdapter.ViewHolder();
            viewHolder.smallTitle = (TextView) convertView.findViewById(R.id.tv_small_title);
            viewHolder.shortDesc = (TextView) convertView.findViewById(R.id.tv_short_desc);
            viewHolder.ivOverview = (ImageView) convertView.findViewById(R.id.iv_overview);
            //设置tag
            convertView.setTag(viewHolder);
        } else {
            //获取缓存布局
            viewHolder = (FocusHorizontalAdapter.ViewHolder) convertView.getTag();
        }
        shufflingItem = focusList.get(position);
        viewHolder.smallTitle.setText(shufflingItem.small_title);
        viewHolder.shortDesc.setText(shufflingItem.jianjie);

        PicassoUtils.loadImageWithHolder(shufflingItem.thumb_image, R.drawable.default_image,viewHolder.ivOverview);
        return convertView;
    }

    private static class ViewHolder {
        ImageView ivOverview;//图片
        TextView smallTitle, shortDesc;
    }
}

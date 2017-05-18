package xidian.xianjujiao.com.entity;

import java.util.ArrayList;

/**
 * Created by flyonthemap on 2017/5/4.
 */

public class LiveNewsData {
    public ArrayList<LiveNewsItem> list;
    public class LiveNewsItem{
        public String module_id;
        public String news_id;
        public String title;
        public String thumb_image;
        public String jianjie;
        public int top;
        public String url;
        public String live_time;
    }
}

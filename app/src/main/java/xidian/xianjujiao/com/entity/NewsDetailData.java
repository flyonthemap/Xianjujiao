package xidian.xianjujiao.com.entity;

import java.util.ArrayList;

/**
 * Created by flyonthemap on 2017/4/26.
 */

public class NewsDetailData {
    public DetailData data;
    public class DetailData{
        public String module_id;
        public String news_id;
        public String title;
        public String thumb_image;
        public int type = 1;
        public String audio;
        public String video;
        public String create_time;
        public String content;
        public String from;

        @Override
        public String toString() {
            return "NewItem{" +
                    ", title='" + title + '\'' +
                    ", create_time='" + create_time + '\'' +
                    '}';
        }
    }


}


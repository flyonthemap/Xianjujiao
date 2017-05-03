package xidian.xianjujiao.com.entity;

import java.util.ArrayList;

/**
 * Created by flyonthemap on 2017/4/26.
 */

public class NewsData {
    public ArrayList<NewItem> news;
    public class NewItem{
        public String module_id;
        public String news_id;
        public String title;
        public String thumb_image;
        public int type ;
        public String create_time;

        @Override
        public String toString() {
            return "NewItem{" +
                    ", title='" + title + '\'' +
                    ", create_time='" + create_time + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "NewsData{" +
                "news=" + news +
                '}';
    }
}

package xidian.xianjujiao.com.entity;

import java.util.ArrayList;

/**
 * Created by flyonthemap on 2017/4/30.
 */

public class FocusHeaderData {
    public ArrayList<FocusShuffling> list;
    public class FocusShuffling{
        public String create_time;
        public String thumb_image;
        public String small_image;
        public String title;
        public String small_title;
        public String jianjie;
        public String news_id;

        @Override
        public String toString() {
            return "FocusShuffling{" +
                    "create_time='" + create_time + '\'' +
                    ", title='" + title + '\'' +
                    ", news_id='" + news_id + '\'' +
                    '}';
        }
    }


}

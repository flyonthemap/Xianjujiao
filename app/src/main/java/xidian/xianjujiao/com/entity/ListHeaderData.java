package xidian.xianjujiao.com.entity;

/**
 * Created by flyonthemap on 2017/4/26.
 */

import java.util.ArrayList;

/**
 * 头条新闻
 *
 * @author Kevin
 *
 */
public class ListHeaderData {
    public ArrayList<Shuffling> lunbo;
    public class Shuffling{
        public String module_id;
        public String news_id;
        public String title;
        public String thumb_image;
        public int type;
        public String create_time;
        @Override
        public String toString() {
            return "ListHeaderData [title=" + title + "]";
        }
    }




}
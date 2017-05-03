package xidian.xianjujiao.com.entity;

import java.util.ArrayList;

/**
 * Created by flyonthemap on 2017/4/30.
 */

public class FocusNewsData {
    public ArrayList<FocusNews> news;
    public class FocusNews{
        public String module_id;
        public String news_id;
        public String title;
        public String thumb_image;
        public int type;
        public String create_time;
    }
    public String module_name;
}

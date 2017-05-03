package xidian.xianjujiao.com.utils;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import xidian.xianjujiao.com.entity.ChannelItem;
import xidian.xianjujiao.com.entity.ChapterListItem;
import xidian.xianjujiao.com.entity.FocusNewsData;
import xidian.xianjujiao.com.entity.GameListItem;
import xidian.xianjujiao.com.entity.LiveModuleName;
import xidian.xianjujiao.com.entity.NewsData;
import xidian.xianjujiao.com.manager.ChannelManager;

/**
 * Created by xhb on 2016/1/19
 * json解析工具类
 */
public class JsonUtils {
    private static Gson gson = new Gson();
    /**
     * json解析方法
     * 根据json数据格式来进行设计
     * <p/>
     * <p/>
     * "data": {
     * "0": {
     * "id": "3543719",
     * "typeid": "2",
     * "typeid2": "0",
     * "sortrank": "1453215017",
     * "flag": "",
     * "ismake": "1",
     * "channel": "1",
     * "arcrank": "0",
     * "click": "77",
     * "money": "0",
     * "title": "真实系赛车游戏《塞巴斯蒂安拉力赛》新截图放出",
     * "shorttitle": "塞巴斯蒂安拉力赛截图",
     * "color": "",
     * "writer": "landother",
     * "source": "landother",
     * "litpic": "/uploads/allimg/160119/174-160119225A30-L.jpg",
     * "pubdate": "1453215017",
     * "senddate": "1453215477",
     * "mid": "174",
     * "keywords": "Milestone,塞巴斯蒂安拉力赛,截图,职业模式",
     * "lastpost": "0",
     * "scores": "0",
     * "goodpost": "0",
     * "badpost": "0",
     * "voteid": "0",
     * "notpost": "0",
     * "description": "Milestone工作室日前放出了其开发的《塞巴斯蒂安拉力赛》全新截图。",
     * "filename": "",
     * "dutyadmin": "174",
     * "tackid": "0",
     * "mtype": "0",
     * "weight": "380983",
     * "fby_id": "0",
     * "game_id": "0",
     * "feedback": "0",
     * "typedir": "{cmspath}/a/news",
     * "typename": "游戏新闻",
     * "corank": "0",
     * "isdefault": "-1",
     * "defaultname": "index.html",
     * "namerule": "{typedir}/{Y}{M}/{aid}.html",
     * "namerule2": "{typedir}/list_{tid}_{page}.html",
     * "ispart": "0",
     * "moresite": "0",
     * "siteurl": "",
     * "sitepath": "{cmspath}/a/info",
     * "arcurl": "http://www.3dmgame.com/news/201601/3543719.html",
     * "typeurl": "http://www.3dmgame.com/news/",
     * "videolist": {
     * "0": {
     * "body": null
     * }
     * }
     * }
     * <p/>
     * 解析文章json数据的方法
     *
     * @param json 网络下载的json数据
     * @return
     */
    public static List<ChapterListItem> parseChapterJson(String json) {
        List<ChapterListItem> list = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(removeBOM(json));
            JSONObject data = object.optJSONObject("data");
            for (int index = 0; index < data.length(); index++) {
                //根据键值对来进行json解析
                JSONObject jsonObject = (JSONObject) data.get(index + "");
                String id = jsonObject.getString("id");
                String typeid = jsonObject.getString("typeid");
                String title = jsonObject.getString("title");
                String senddate = jsonObject.getString("senddate");
                String litpic = jsonObject.getString("litpic");
                String arcurl = jsonObject.getString("arcurl");
                String feedback = jsonObject.getString("feedback");
                ChapterListItem chapterListItem = new ChapterListItem(id, typeid, title, senddate, litpic, feedback, arcurl);
                list.add(chapterListItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 游戏列表json解析
     *
     * @param json
     * @return
     */
    public static List<GameListItem> parseGameJson(String json) {
        List<GameListItem> list = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(removeBOM(json));
            JSONObject data = (JSONObject) object.get("data");
            for (int index = 0; index < data.length(); index++) {
                //根据键值对来进行json解析
                JSONObject jsonObject = (JSONObject) data.get(index + "");
                String id = jsonObject.getString("id");
                String typeid = jsonObject.getString("typeid");
                String title = jsonObject.getString("title");
                String senddate = jsonObject.getString("senddate");
                String litpic = jsonObject.getString("litpic");
                String arcurl = jsonObject.getString("arcurl");
                String keywords = jsonObject.getString("keywords");
                String description = jsonObject.getString("description");
                String typename = jsonObject.getString("typename");
                String language = jsonObject.getString("language");
                GameListItem gameListItem = new GameListItem(id, typeid, title, litpic, senddate, keywords, description, typename, language, arcurl);
                list.add(gameListItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }


    public static void parseChannelJson(String json){
        ChannelManager channelManager = ChannelManager.getChannelManager();
        List<ChannelItem> defaultChannel = new ArrayList<>();
        List<ChannelItem> otherChannel = new ArrayList<>();
        // 使用hashSet来确定是否要更新数据库的数据
        Set<String> allChannelId = new HashSet<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray defaultJson = jsonObject.getJSONArray("default");
            JSONObject itemJson = null;
            int count = 0;
            for(int i =0;i<defaultJson.length();++i){
                itemJson = defaultJson.getJSONObject(i);
                ChannelItem channelItem = new ChannelItem();
                channelItem.setId(itemJson.getString("module_id"));
                channelItem.setName(itemJson.getString("module_name"));
                channelItem.setOrderId(++count);
                channelItem.setSelected(1);
                defaultChannel.add(channelItem);
                allChannelId.add(channelItem.getId());

            }
            JSONArray otherJson = jsonObject.getJSONArray("other");
            for(int i =0;i<otherJson.length();++i){
                itemJson = otherJson.getJSONObject(i);
                ChannelItem channelItem = new ChannelItem();
                channelItem.setId(itemJson.getString("module_id"));
                channelItem.setName(itemJson.getString("module_name"));
                channelItem.setOrderId(++count);
                channelItem.setSelected(0);
                otherChannel.add(channelItem);
                allChannelId.add(channelItem.getId());


            }
            // 如果模块发生变化，则更新本地数据库
            if(!allChannelId.equals(channelManager.getAllChannelId())){
                channelManager.setDefaultUserChannels(defaultChannel);
                channelManager.setDefaultOtherChannels(otherChannel);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /**
     * json串头部出现字符："\ufeff" 解决方法
     * @param data
     * @return
     */
    public static final String removeBOM(String data) {
        if (TextUtils.isEmpty(data)) {
            return data;
        }

        if (data.startsWith("\ufeff")) {
            return data.substring(1);
        } else {
            return data;
        }
    }

    public static List<NewsData.NewItem> parseNewsData(String result) {
        return gson.fromJson(result, NewsData.class).news;
    }
    public static FocusNewsData parseFocusNewsData(String result) {
        return gson.fromJson(result, FocusNewsData.class);
    }


    public static List<LiveModuleName.ModuleData> parseLiveModuleName(String result) {
        return gson.fromJson(result, LiveModuleName.class).data;
    }
}

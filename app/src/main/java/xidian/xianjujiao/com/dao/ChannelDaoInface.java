package xidian.xianjujiao.com.dao;

import android.content.ContentValues;


import java.util.List;
import java.util.Map;
import java.util.Set;

import xidian.xianjujiao.com.entity.ChannelItem;

public interface ChannelDaoInface {
	public boolean addCache(ChannelItem item);

	public boolean deleteCache(String whereClause, String[] whereArgs);

	public boolean updateCache(ContentValues values, String whereClause,
                               String[] whereArgs);

	public Map<String, String> viewCache(String selection,
                                         String[] selectionArgs);

	public List<Map<String, String>> listCache(String selection,
                                               String[] selectionArgs);
	public Set<String> getAllChannelId();

	public void clearFeedTable();
}

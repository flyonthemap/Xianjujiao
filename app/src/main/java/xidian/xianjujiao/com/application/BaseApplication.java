package xidian.xianjujiao.com.application;

import android.app.Application;
import android.content.Context;

import com.umeng.socialize.PlatformConfig;

import org.xutils.x;

import xidian.xianjujiao.com.db.SQLHelper;


/**
 * Created by xhb on 2016/1/19.
 * 程序主入口，当程序启动的时候，会调用这个方法
 */
public class BaseApplication extends Application {
    private static BaseApplication mAppApplication;
    private SQLHelper sqlHelper;
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化xutils的操作
        x.Ext.init(this);
        //设置是否输出日志
        x.Ext.setDebug(true);
//        //微信 appid appsecret
//        PlatformConfig.setWeixin("wx152334f54a39c3b0", "24949aef9a179c253fdd55f12a576632");
//        // QQ和Qzone appid appkey
//        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");
//        //极光推送初始化
//        JPushInterface.init(this);
//        JPushInterface.setDebugMode(true);//设置是否开启log日志，正式打包发布时建议关闭使用
        mAppApplication = this;
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
//        MultiDex.install(this);
    }
    /** 获取Application */
    public static BaseApplication getApp() {
        return mAppApplication;
    }

    /** 获取数据库Helper */
    public SQLHelper getSQLHelper() {
        if (sqlHelper == null)
            sqlHelper = new SQLHelper(mAppApplication);
        return sqlHelper;
    }

}

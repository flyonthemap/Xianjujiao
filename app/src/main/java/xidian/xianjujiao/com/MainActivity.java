package xidian.xianjujiao.com;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;



import java.util.ArrayList;
import java.util.List;

import xidian.xianjujiao.com.adapter.MainFragmentPageAdapter;
import xidian.xianjujiao.com.fragment.FocusFragment;
import xidian.xianjujiao.com.fragment.HeadlinesFragment;
import xidian.xianjujiao.com.fragment.ForumFragment;
import xidian.xianjujiao.com.fragment.GameFragment;
import xidian.xianjujiao.com.fragment.LiveFragment;
import xidian.xianjujiao.com.fragment.VideoFragment;
import xidian.xianjujiao.com.fragment.innerFragments.NewsFragment;
import xidian.xianjujiao.com.utils.SystemBarTintManager;
import xidian.xianjujiao.com.view.TipsToast;

public class MainActivity extends FragmentActivity {
    private ViewPager main_viewPager;
    private List<Fragment> fragments = new ArrayList<>();
    private MainFragmentPageAdapter adapter;
    private RadioGroup rgp;
    private TipsToast tipsToast;
    //退出时间
    private long exitTime = 0;
    //    private IFlytekUpdate mUpdManager;
    public final static int CHANNELREQUEST = 1;
    /** 调整返回的RESULTCODE */
    public final static int CHANNELRESULT = 2;
    HeadlinesFragment headlines_Fragments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        initWindow();
        initData();
        initView();

        setAdapter();
        setListener();
    }


    //初始化控件
    private void initView() {
        main_viewPager = (ViewPager) findViewById(R.id.main_viewpager);
        //设置viewpager的预加载页数，viewpager默认只会预加载左右两边的页面数据
        main_viewPager.setOffscreenPageLimit(1);
        rgp = (RadioGroup) findViewById(R.id.main_rgp);
        //设置默认第一个为选中状态
        RadioButton rb = (RadioButton) rgp.getChildAt(0);
        rb.setChecked(true);
    }

    //初始化数据
    private void initData() {
        headlines_Fragments = new HeadlinesFragment();
        ForumFragment forum_Fragment = new ForumFragment();
        VideoFragment video_Fragment = new VideoFragment();
        FocusFragment focusFragment = new FocusFragment();
        LiveFragment liveFragment = new LiveFragment();
        fragments.add(headlines_Fragments);// 头条
        fragments.add(focusFragment);//聚焦
        fragments.add(video_Fragment);//聚焦
        fragments.add(liveFragment);//视频
        fragments.add(forum_Fragment);//论坛
    }

    //设置适配器
    private void setAdapter() {
        //实例化适配器
        adapter = new MainFragmentPageAdapter(getSupportFragmentManager(), fragments);
        //设置适配器
        main_viewPager.setAdapter(adapter);
    }

    //设置监听
    private void setListener() {
        //viewPager的滑动监听
        main_viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //获取当前位置的RadioButton
                RadioButton rb = (RadioButton) rgp.getChildAt(position);
                //设置为true
                rb.setChecked(true);
            }
        });
        //RadioGroup的事件监听
        rgp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int index = 0; index < rgp.getChildCount(); index++) {
                    RadioButton rb = (RadioButton) rgp.getChildAt(index);
                    if (rb.isChecked()) {
                        main_viewPager.setCurrentItem(index, false);
                        break;
                    }
                }
            }
        });
    }

    /**
     * 按两次退出应用
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                showTips(R.drawable.tips_smile, "再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 自定义toast
     *
     * @param iconResId 图片
     * @param tips      提示文字
     */
    private void showTips(int iconResId, String tips) {
        if (tipsToast != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                tipsToast.cancel();
            }
        } else {
            tipsToast = TipsToast.makeText(MainActivity.this.getApplication()
                    .getBaseContext(), tips, TipsToast.LENGTH_SHORT);
        }
        tipsToast.show();
        tipsToast.setIcon(iconResId);
        tipsToast.setText(tips);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("debug","收到数据变化" +requestCode +" " +resultCode);
        switch (requestCode) {
            case CHANNELREQUEST:
                // 重新加载Activity实现频道更新！！！！！！！
                recreate();
                break;

            default:
                break;
        }
    }

}

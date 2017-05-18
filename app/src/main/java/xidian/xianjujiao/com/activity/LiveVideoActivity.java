package xidian.xianjujiao.com.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.dl7.player.media.IjkPlayerView;

import xidian.xianjujiao.com.R;
import xidian.xianjujiao.com.utils.PicassoUtils;
import xidian.xianjujiao.com.utils.UiUtils;

/**
 * Created by flyonthemap on 2017/5/4.
 */

public class LiveVideoActivity extends AppCompatActivity {

    private static final String VIDEO_URL = "http://flv2.bn.netease.com/videolib3/1611/28/nNTov5571/SD/nNTov5571-mobile.mp4";
    private static final String IMAGE_URL = "http://vimg3.ws.126.net/image/snapshot/2016/11/C/T/VC628QHCT.jpg";
    IjkPlayerView mPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlayerView = new IjkPlayerView(this);
        setContentView(mPlayerView);
        Bundle bundle = this.getIntent().getExtras();
        String imageUrl = bundle.getString("imageUrl");
        String videoUrl = bundle.getString("videoUrl");
        String title = bundle.getString("title");
        PicassoUtils.loadImageWithHolder(imageUrl, R.drawable.default_image,mPlayerView.mPlayerThumb);
        mPlayerView.init()
                .alwaysFullScreen()
                .enableOrientation()
                .setVideoPath(videoUrl)
                .enableDanmaku(false)
                .setTitle(title)
                .start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPlayerView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPlayerView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPlayerView.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mPlayerView.handleVolumeKey(keyCode)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
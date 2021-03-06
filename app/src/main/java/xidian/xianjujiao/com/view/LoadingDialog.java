package xidian.xianjujiao.com.view;

/**
 * Created by flyonthemap on 2017/5/11.
 */

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import xidian.xianjujiao.com.R;


/**
 * Created by flyonthemap on 2017/3/8.
 */

public class LoadingDialog extends Dialog {
    public static final int LOAD_SUCC = 0x001;
    public static final int LOAD_FAIL = 0x002;

    private ImageView iv_load_result;
    private TextView tv_load;
    private ProgressBar pb_loading;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case LOAD_SUCC:
                    dismiss();
                    break;
                case LOAD_FAIL:
                    dismiss();
                    break;
                default:
                    break;
            }
        };
    };
    public LoadingDialog(Context context) {
        super(context, R.style.loadingDialogTheme);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commom_loading_layout);
        iv_load_result = (ImageView) findViewById(R.id.iv_load_result);
        tv_load = (TextView) findViewById(R.id.tv_load);
        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);

    }
    // 设置加载成功应该给出的提示
    public void dismissSuc(String toast) {
        pb_loading.setVisibility(View.GONE);
        iv_load_result.setVisibility(View.VISIBLE);
        tv_load.setText(toast);
        iv_load_result.setImageResource(R.drawable.load_suc_icon);
        mHandler.sendEmptyMessageDelayed(LOAD_SUCC, 1500);
    }
    // 设置加载失败应该给出的提示
    public void dismissFail(String toast) {
        pb_loading.setVisibility(View.GONE);
        iv_load_result.setVisibility(View.VISIBLE);
        tv_load.setText(toast);
        iv_load_result.setImageResource(R.drawable.load_fail_icon);
        mHandler.sendEmptyMessageDelayed(LOAD_FAIL, 1500);
    }
}

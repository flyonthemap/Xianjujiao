package xidian.xianjujiao.com.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sohu.cyan.android.sdk.api.CallBack;
import com.sohu.cyan.android.sdk.api.CyanSdk;
import com.sohu.cyan.android.sdk.entity.AccountInfo;
import com.sohu.cyan.android.sdk.exception.CyanException;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xidian.xianjujiao.com.R;
import xidian.xianjujiao.com.entity.UserData;
import xidian.xianjujiao.com.utils.API;
import xidian.xianjujiao.com.utils.JsonUtils;
import xidian.xianjujiao.com.utils.MatchUtil;
import xidian.xianjujiao.com.utils.SPUtils;
import xidian.xianjujiao.com.utils.ToastUtil;
import xidian.xianjujiao.com.utils.UiUtils;
import xidian.xianjujiao.com.view.LoadingDialog;

/**
 * Created by flyonthemap on 2017/5/10.
 */

public class LoginActivity extends AppCompatActivity {
    @Bind(R.id.tv_get_code)
    Button tvGetCode;
    @Bind(R.id.ib_search)
    ImageButton ibSearch;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_phoneNum)
    EditText etPhoneNum;
    @Bind(R.id.et_confirm_code)
    EditText etConfirmCode;
    @Bind(R.id.et_invite)
    EditText etInvite;
    @Bind(R.id.iv_qq)
    ImageView ivQQ;
    @Bind(R.id.iv_wechat)
    ImageView ivWechat;
    @Bind(R.id.btn_login)
    Button btn_login;
    private String validateCode;

    private int validTime = 60;
    private Runnable timerRunnable;// 定时器
    private static final int TIME_CHANGE = 2000;
    private static final int TIMER_COMPLETE = 3000;
    public static final int GET_CODE_SUCCESS = 1005; //获取验证码成功
    private static final String TIME = "time";


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TIME_CHANGE:
                    Bundle data = msg.getData();
                    int time = data.getInt(TIME);
                    tvGetCode.setText(getString(R.string.get_code_remaining_time,time));
                    break;
                case TIMER_COMPLETE:
                    tvGetCode.setClickable(true);
                    tvGetCode.setText(R.string.get_verification_code);
                    handler.removeCallbacks(timerRunnable);
                    validTime = 60;
                    timerRunnable = null;
                    break;
                // 获取验证码成功
                case GET_CODE_SUCCESS:
                    //对话框提示
                    tvGetCode.setClickable(false);

                    // 定时器
                    if (timerRunnable != null) {
                        handler.removeCallbacks(timerRunnable);
                        timerRunnable = null;
                    }
                    timerRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (validTime > 0) {
                                validTime--;
                                Message msg = handler.obtainMessage();
                                msg.what = TIME_CHANGE;
                                Bundle bundle = new Bundle();
                                bundle.putInt("time", validTime);
                                msg.setData(bundle);
                                msg.sendToTarget();
                                handler.postDelayed(timerRunnable, 1000);
                            } else {
                                handler.sendEmptyMessage(TIMER_COMPLETE);
                            }
                        }
                    };
                    handler.post(timerRunnable);
                    break;

            }
        }
    };



    private LoadingDialog loadingDialog;
    private CyanSdk sdk;

    private String phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_login_layout);
        ButterKnife.bind(this);
        initToolbar();
        initLoadingDialog();
        sdk = CyanSdk.getInstance(this);
    }

    private void initLoadingDialog() {
        loadingDialog = new LoadingDialog(this);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        //获取到标题栏控件
        ibSearch.setVisibility(View.GONE);
        ab.setTitle("登录");
        //设置标题栏字体颜色
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorBlack));
    }
    @OnClick(R.id.tv_get_code)
    void getCheckCode(){

        phone = etPhoneNum.getText().toString();
        // 获取验证码并进行校验
        if (MatchUtil.isPhoneNum(phone)) {
            // 手机号合法的话在网络请求之前就将按钮设置为不可点击的状态
            tvGetCode.setClickable(false);
            RequestParams params = new RequestParams(API.GET_CODE_URL);
            params.addBodyParameter("mobile",phone);
            x.http().post(params, new Callback.CacheCallback<String>() {
                @Override
                public void onSuccess(String result) {

                    if(result != null){

                        handler.sendEmptyMessage(GET_CODE_SUCCESS);
                        try {
                            JSONObject object = new JSONObject(result);
                            validateCode = object.getString("validate_code");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.e("---->",result);
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                }

                @Override
                public void onCancelled(CancelledException cex) {

                }

                @Override
                public void onFinished() {

                }

                @Override
                public boolean onCache(String result) {
                    return false;
                }
            });

        }else {
            if ("".equals(phone)) {
                ToastUtil.showShort(UiUtils.getContext(),getString(R.string.null_phone));
            } else {
                ToastUtil.showShort(UiUtils.getContext(),getString(R.string.error_phone));
            }
        }
    }
    @OnClick(R.id.btn_login)
    void login(){
        // 获取手机号
        phone = etPhoneNum.getText().toString();
        if(MatchUtil.isPhoneNum(phone)){
            String etValidateCode = etConfirmCode.getText().toString();
            if(etValidateCode.equals(validateCode)){
                loadingDialog.show();
                RequestParams params = new RequestParams(API.LOGIN_URL);
                String inviteCode = etInvite.getText().toString();
                params.addBodyParameter("mobile",phone);
                params.addBodyParameter("code",inviteCode);
                x.http().post(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.e("---->",result);
                        UserData.User user = JsonUtils.parserLoginData(result);

                        SPUtils.put(UiUtils.getContext(),"isLogin",true);
                        SPUtils.put(UiUtils.getContext(),"memberId",user.member_id);
                        SPUtils.put(UiUtils.getContext(),"nickname",user.nickname);
                        SPUtils.put(UiUtils.getContext(),"mobile",user.mobile);
                        SPUtils.put(UiUtils.getContext(),"portrait",user.touxiang);
                        AccountInfo accountInfo = new AccountInfo();
                        accountInfo.isv_refer_id = user.member_id;

                        accountInfo.nickname = user.nickname;
                        sdk.setAccountInfo(accountInfo,new CallBack() {

                            @Override
                            public void success() {
                                // token
                                Set<String> set = CyanSdk.getCookie();
                                loadingDialog.dismissSuc("登录成功");
                            }
                            @Override
                            public void error(CyanException e){
                                Toast.makeText(LoginActivity.this, e.error_msg, Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {

                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                        finish();

                    }
                });
            }else {
                ToastUtil.showShort(UiUtils.getContext(),getString(R.string.input_verification_code));
            }
        }else {
            if ("".equals(phone)) {
                ToastUtil.showShort(UiUtils.getContext(),getString(R.string.null_phone));
            } else {
                ToastUtil.showShort(UiUtils.getContext(),getString(R.string.error_phone));
            }
        }


//        x.http().post()
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    protected void onDestroy() {
        super.onDestroy();
        if(loadingDialog != null)
            loadingDialog.dismiss();
    }


}

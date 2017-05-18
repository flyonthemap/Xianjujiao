package xidian.xianjujiao.com.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sohu.cyan.android.sdk.api.Config;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import xidian.xianjujiao.com.MainActivity;
import xidian.xianjujiao.com.R;
import xidian.xianjujiao.com.activity.LoginActivity;
import xidian.xianjujiao.com.utils.PicassoUtils;
import xidian.xianjujiao.com.utils.SPUtils;
import xidian.xianjujiao.com.utils.UiUtils;

/**
 * Created by flyonthemap on 2017/5/17.
 */

public class PersonInfoFragment extends Fragment {

    @Bind(R.id.title)
    TextView tvTitle;
    @Bind(R.id.ci_change_portrait)
    CircleImageView circleImageView;
    @Bind(R.id.tv_nickname)
    TextView tvNickname;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personinfo,container,false);
        ButterKnife.bind(this,view);
        initTitle();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        String portraitURL = (String) SPUtils.get(UiUtils.getContext(),"portrait","");
        String nickname = (String) SPUtils.get(UiUtils.getContext(),"nickname","");
        if(!portraitURL.equals("") && !nickname.equals("")){
            PicassoUtils.loadImageWithHolder(portraitURL,R.drawable.person_default,circleImageView);
            tvNickname.setText(nickname);
        }
    }

    private void initTitle() {
        tvTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("用户中心");
    }
    @OnClick(R.id.ll_login)
    void openLoginActivity(){
        boolean isLogin = (boolean) SPUtils.get(UiUtils.getContext(),"isLogin",false);
        if(!isLogin){
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }else{
            // 开始修改头像或者退出
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }

    }
}

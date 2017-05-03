package xidian.xianjujiao.com.fragment.liveInnerFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import xidian.xianjujiao.com.R;

/**
 * Created by flyonthemap on 2017/5/2.
 */

public class LiveModuleFragment extends Fragment {
    private View view;
    private TextView textView;
    private String title;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
//        title = getArguments().getString("title");
        view = inflater.inflate(R.layout.fragment_live_module, container, false);
        textView = (TextView) view.findViewById(R.id.tv_module);

        return view;
    }

    public CharSequence getTitle() {
        return "哈哈";
    }
}

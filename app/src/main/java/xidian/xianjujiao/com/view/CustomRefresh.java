package xidian.xianjujiao.com.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;

/**
 * Created by flyonthemap on 2017/5/3.
 */

public class CustomRefresh extends SwipeRefreshLayout {

    // 上一次触摸时的X坐标

    public CustomRefresh(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 触发移动事件的最短距离，如果小于这个距离就不触发移动控件
    }


    @Override
    public boolean canChildScrollUp() {
        View target = getChildAt(0);
        if (target instanceof AbsListView) {
            final AbsListView absListView = (AbsListView) target;
            return absListView.getChildCount() > 0
                    && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                    .getTop() < absListView.getPaddingTop());
        } else {
            return target.getScrollY() > 0;
        }
    }
}
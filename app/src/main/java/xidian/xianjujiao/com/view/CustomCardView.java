package xidian.xianjujiao.com.view;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

import xidian.xianjujiao.com.R;

/**
 * Created by flyonthemap on 2017/5/1.
 */

public class CustomCardView extends CardView {

    public CustomCardView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public CustomCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public CustomCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (isPressed()) {
            this.setCardBackgroundColor(getContext().getResources().getColor(R.color.card_view_pressed));
        } else {
            this.setCardBackgroundColor(getContext().getResources().getColor(R.color.cardview_light_background));
        }
    }
}
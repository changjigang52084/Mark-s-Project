package com.xunixianshi.vrshow.my.viewItem;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

import com.xunixianshi.vrshow.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by duan on 2016/9/21.
 */
public class UserLinearLabel {
    @Bind(R.id.user_linear_icon_iv)
    ImageView user_linear_icon_iv;
    @Bind(R.id.user_linear_text_tv)
    TextView user_linear_text_tv;
    @Bind(R.id.user_linear_num_tv)
    TextView user_linear_num_tv;
    public UserLinearLabel(View view, String tag, @DrawableRes int iconId, @StringRes int name, View.OnClickListener onClickListener) {
        ButterKnife.bind(this, view);
        view.setTag(tag);
        user_linear_icon_iv.setImageResource(iconId);
        user_linear_text_tv.setText(name);
        view.setOnClickListener(onClickListener);
    }

    public void setUserLinearNum(String formatString, int number) {
        if(number >= 0){
            user_linear_num_tv.setText(String.format(formatString, number));
        }
        else {
            user_linear_num_tv.setText("");
            user_linear_num_tv.setVisibility(View.INVISIBLE);
        }
    }
    public void setLinearNumVisibility(int visibility){
        user_linear_num_tv.setVisibility(visibility);
    }
}
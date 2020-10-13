package com.xunixianshi.vrshow.my.viewItem;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.xunixianshi.vrshow.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by duan on 2016/9/21.
 */
public class UserEqualLabel {
    @Bind(R.id.user_content_tv)
    TextView user_content_tv;

    @Bind(R.id.user_content_num_tv)
    TextView user_content_num_tv;

    public UserEqualLabel(View view, String tag, @StringRes int name, View.OnClickListener onClickListener){
        ButterKnife.bind(this, view);
        view.setTag(tag);
        view.setOnClickListener(onClickListener);
        user_content_tv.setText(name);
        user_content_num_tv.setVisibility(View.GONE);
    }
    public void setContentNum(int number){
        if(number<0){
            number = 0;
        }
        user_content_num_tv.setText(""+number);
        user_content_num_tv.setVisibility(View.VISIBLE);
    }
}

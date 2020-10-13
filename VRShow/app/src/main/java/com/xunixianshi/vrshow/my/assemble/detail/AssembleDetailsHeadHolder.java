package com.xunixianshi.vrshow.my.assemble.detail;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hch.viewlib.util.ScreenUtils;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.obj.assembleObj.AssembleDetailObj;
import com.zhy.http.okhttp.utils.PicassoUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by duan on 2016/10/14.
 */

public class AssembleDetailsHeadHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.assemble_content_cover_iv)
    ImageView assemble_content_cover_iv;

    @Bind(R.id.assemble_content_desc_tv)
    TextView assemble_content_desc_tv;

    @Bind(R.id.assemble_content_head_rl)
    RelativeLayout assemble_content_head_rl;

    private Context mContext;

    public AssembleDetailsHeadHolder(View itemView, Context context) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        setImageSize(context);
        this.mContext = context;
    }

    private void setImageSize(Context context) {
        int screenWith = ScreenUtils.getScreenWidth(context);
        ViewGroup.LayoutParams lp;
        lp = assemble_content_cover_iv.getLayoutParams();
        lp.width = screenWith;
        lp.height = (int) (screenWith * 0.56);
        assemble_content_cover_iv.setLayoutParams(lp);
    }

    public void setHeadHolderDate(AssembleDetailObj assembleDetailObj) {
        if (assembleDetailObj != null) {
            PicassoUtil.loadImage(mContext, assembleDetailObj.getCoverImgUrl() + "?imageView2/2/w/" + (100 + ScreenUtils.getScreenWidth(mContext) / 2), assemble_content_cover_iv);
            assemble_content_desc_tv.setText(assembleDetailObj.getCompilationIntro());
        }
    }
}
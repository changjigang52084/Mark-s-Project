package com.xunixianshi.vrshow.videodetail;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.ScreenUtils;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAda;
import com.xunixianshi.vrshow.customview.CircleImageView;
import com.xunixianshi.vrshow.my.homePage.OtherHomePageActivity;
import com.xunixianshi.vrshow.obj.CommentListObj;
import com.zhy.http.okhttp.utils.PicassoUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 视频详情页评论列表适配器
 *
 * @author HeChuang
 * @ClassName ClassifyVideoCommentAdapter
 * @time 2016/11/1 15:49
 */
public class ClassifyVideoCommentAdapter extends BaseAda<CommentListObj> {

    private boolean isFromDetail = false;

    public ClassifyVideoCommentAdapter(Context context, boolean isFromDetail) {
        super(context);
        this.mContext = context;
        this.isFromDetail = isFromDetail;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater
                    .inflate(R.layout.item_classify_comment_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        CommentListObj itemData = getItem(position);
        PicassoUtil.loadImage(mContext, itemData.getUserIcon() + "?imageView2/2/w/" + (ScreenUtils.dip2px(mContext, 60) + 120), viewHolder.comment_item_user_icon_civ);
        viewHolder.comment_item_user_name_tv.setText(itemData.getUserName());
        viewHolder.comment_item_comment_date_tv.setText(itemData.getCommentTime());
        viewHolder.comment_item_comment_content_tv.setText(itemData.getCommentContent());
        if (isFromDetail) {
            viewHolder.comment_item_comment_content_tv.setMaxLines(1);
            viewHolder.comment_item_comment_content_tv.setEllipsize(TextUtils.TruncateAt.END);
        } else {
            viewHolder.comment_item_comment_content_tv.setMaxLines(18);
            viewHolder.comment_item_comment_content_tv.setEllipsize(TextUtils.TruncateAt.END);
        }
        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.comment_item_user_icon_civ)
        CircleImageView comment_item_user_icon_civ;
        @Bind(R.id.comment_item_user_name_tv)
        TextView comment_item_user_name_tv;
        @Bind(R.id.comment_item_comment_date_tv)
        TextView comment_item_comment_date_tv;
        @Bind(R.id.comment_item_comment_content_tv)
        TextView comment_item_comment_content_tv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

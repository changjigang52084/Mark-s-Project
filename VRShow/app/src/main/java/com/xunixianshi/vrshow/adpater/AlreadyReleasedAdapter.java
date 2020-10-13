package com.xunixianshi.vrshow.adpater;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hch.utils.MLog;
import com.hch.viewlib.util.ScreenUtils;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAda;
import com.xunixianshi.vrshow.interfaces.ShareSendInterface;
import com.xunixianshi.vrshow.obj.ContentReviewedListObj;
import com.zhy.http.okhttp.utils.PicassoUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * TODO 内容已发布的适配器
 *
 * @author MarkChang
 * @ClassName AlreadyReleasedAdapter
 * @time 2016/11/1 16:01
 */

public class AlreadyReleasedAdapter extends BaseAda<ContentReviewedListObj> {

    private boolean type;
    private ShareSendInterface shareInterface;

    public AlreadyReleasedAdapter(Context context, boolean type) {
        super(context);
        this.type = type;
    }

    public void setShareInterface(ShareSendInterface shareInterface) {
        this.shareInterface = shareInterface;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.already_released_fragment_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final ContentReviewedListObj item = getItem(position);
        int screenWith = ScreenUtils.getScreenWidth(mContext);
        ViewGroup.LayoutParams lp;
        lp = viewHolder.already_released_video_item_icon_iv.getLayoutParams();
        lp.width = screenWith;
        lp.height = (int) (screenWith * 0.56);
        viewHolder.already_released_video_item_icon_iv.setLayoutParams(lp);
        viewHolder.public_video_item_more_operation_iv.setImageResource(R.drawable.classify_detail_share_icon);
        viewHolder.public_video_item_more_operation_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareInterface.share(item.getSmallIntroduce(), item.getCoverImgUrl(), item.getResourceName(), item.getResourceId());
            }
        });
//        viewHolder.public_video_item_more_operation_iv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (viewHolder.already_released_video_item_more_operation_ll.getVisibility() == View.GONE) {
//                    viewHolder.already_released_video_item_more_operation_ll.setVisibility(View.VISIBLE);
//                    viewHolder.public_video_item_more_operation_iv.setImageResource(R.drawable.arrow_up);
//                } else {
//                    viewHolder.already_released_video_item_more_operation_ll.setVisibility(View.GONE);
//                    viewHolder.public_video_item_more_operation_iv.setImageResource(R.drawable.arrow_down);
//                }
//            }
//        });
        MLog.d("type:" + type);
        if (type) {
            viewHolder.public_video_item_more_operation_iv.setVisibility(View.VISIBLE);
            viewHolder.already_released_video_item_bottom_text_two_tv.setText("播放次数：" + item.getCumulativeNum());
        } else {
            viewHolder.public_video_item_more_operation_iv.setVisibility(View.GONE);
            viewHolder.already_released_video_item_bottom_text_two_tv.setText("提交时间：" + item.getCreateTime());
        }
//        viewHolder.already_released_video_item_edit_ll.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent1 = new Intent(mContext, CreateContentActivity.class);
////                intent1.putExtra("videoPath", "");
////                intent1.putExtra("urlPath", item.getCoverImgUrl());
////                intent1.putExtra("videoName", item.getResourceName());
////                intent1.putExtra("videoIntroduce", item.getSmallIntroduce());
////                intent1.putExtra("isNative",false);
////                mContext.startActivity(intent1);
//            }
//        });
//        viewHolder.already_released_video_item_share_ll.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                shareInterface.share(item.getSmallIntroduce(),item.getCoverImgUrl(),item.getResourceName(),item.getResourceId());
//            }
//        });
        PicassoUtil.loadImage(mContext, item.getCoverImgUrl(), viewHolder.already_released_video_item_icon_iv);
        viewHolder.already_released_video_item_bottom_text_one_tv.setText(item.getResourceName());
        return convertView;
    }


    public class ViewHolder {
        @Bind(R.id.already_released_video_item_icon_iv)
        ImageView already_released_video_item_icon_iv;
        @Bind(R.id.already_released_video_item_bottom_text_one_tv)
        TextView already_released_video_item_bottom_text_one_tv;
        @Bind(R.id.already_released_video_item_bottom_text_two_tv)
        TextView already_released_video_item_bottom_text_two_tv;
        @Bind(R.id.public_video_item_more_operation_iv)
        ImageView public_video_item_more_operation_iv;
        @Bind(R.id.already_released_video_item_more_operation_ll)
        LinearLayout already_released_video_item_more_operation_ll;
        @Bind(R.id.already_released_video_item_edit_ll)
        LinearLayout already_released_video_item_edit_ll;
        @Bind(R.id.already_released_video_item_share_ll)
        LinearLayout already_released_video_item_share_ll;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

package com.xunixianshi.vrshow.classify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.MLog;
import com.hch.viewlib.util.ScreenUtils;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAda;
import com.xunixianshi.vrshow.interfaces.ShareSendInterface;
import com.xunixianshi.vrshow.my.LoginActivity;
import com.xunixianshi.vrshow.obj.HttpZanObj;
import com.xunixianshi.vrshow.obj.MainClassifyTypeListObj;
import com.xunixianshi.vrshow.obj.MainOtherTabListObj;
import com.xunixianshi.vrshow.obj.ResourcesCollect;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.StringUtil;
import com.xunixianshi.vrshow.videodetail.ClassifyVideoDetialActivity;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.zhy.http.okhttp.interfaces.HttpError;
import com.zhy.http.okhttp.interfaces.HttpSuccess;
import com.zhy.http.okhttp.utils.PicassoUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;


/**
 * 指定视频类型adapter
 *
 * @author HeChuang
 * @ClassName ClassifyVideoListAdapter
 * @time 2016/11/1 15:24
 */
public class ClassifyVideoListAdapter extends BaseAda<MainClassifyTypeListObj> {

    Animation btnAnim;
    Context mContext;
    ShareSendInterface shareInterface;

    public ClassifyVideoListAdapter(Context context) {
        super(context);
        this.mContext = context;
        btnAnim = AnimationUtils.loadAnimation(mContext, R.anim.zan_add_translate);
    }

    public void setShareInterface(ShareSendInterface shareInterface) {
        this.shareInterface = shareInterface;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_public_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final MainClassifyTypeListObj item = getItem(position);
        int screenWith = ScreenUtils.getScreenWidth(mContext);
        ViewGroup.LayoutParams lp;
        lp = viewHolder.public_video_item_icon_iv.getLayoutParams();
        lp.width = screenWith;
        lp.height = (int) (screenWith * 0.56);
        viewHolder.public_video_item_icon_iv.setLayoutParams(lp);
        viewHolder.public_video_item_more_operation_ll.setVisibility(View.GONE);
        viewHolder.public_video_item_more_operation_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewHolder.public_video_item_more_operation_ll.getVisibility() == View.GONE) {
                    viewHolder.public_video_item_more_operation_ll.setVisibility(View.VISIBLE);
                    viewHolder.public_video_item_more_operation_iv.setImageResource(R.drawable.arrow_up);
                } else {
                    viewHolder.public_video_item_more_operation_ll.setVisibility(View.GONE);
                    viewHolder.public_video_item_more_operation_iv.setImageResource(R.drawable.arrow_down);
                }
            }
        });
        viewHolder.public_video_item_more_operation_iv.setImageResource(R.drawable.arrow_down);
        viewHolder.public_video_item_collect_ll.setVisibility(View.VISIBLE);
        PicassoUtil.loadImage(mContext, item.getConverImgUrl() + "?imageView2/2/w/" + (screenWith + 120), viewHolder.public_video_item_icon_iv);
        viewHolder.public_video_item_more_operation_iv.setVisibility(View.VISIBLE);
        viewHolder.public_video_item_bottom_text_one_tv.setText(item.getResourceTitle());
        viewHolder.public_video_item_bottom_text_two_tv.setText("播放次数：" + item.getSumTotal());
        viewHolder.public_video_item_bottom_text_right_ll.setVisibility(View.GONE);
        viewHolder.public_video_item_collect_ib.setImageResource(R.drawable.classify_detail_collect_normal);

        attachZanView(viewHolder,item.getIsLike(),item.getResourceTotalLikes());
        attachCollectionView(viewHolder,item.getIsCollection());
        viewHolder.public_video_item_icon_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(mContext, ClassifyVideoDetialActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("videoTypeId",
                        item.getResourceId());
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
        viewHolder.public_video_item_zan_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MLog.d("AppContent.UID:" + AppContent.UID);
                if (AppContent.UID.equals("")) {
                    showToastMsg("用户没有登录，请先登录！");
                    mContext.startActivity(new Intent(mContext, LoginActivity.class));
                } else if (AppContent.LIMIT_LOGIN == 2) {
                    showToastMsg("当前登录状态未激活，请激活后重新登录。");
                    mContext.startActivity(new Intent(mContext, LoginActivity.class));
                } else {
                    zanVideo(viewHolder, item);
                }
            }
        });
        viewHolder.public_video_item_download_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareInterface.downLoad(item.getResourceId(), item.getConverImgUrl(), item.getResourceTitle());
            }
        });
        viewHolder.public_video_item_share_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareInterface.share(item.getSmallIntroduce(), item.getConverImgUrl(), item.getResourceTitle(), item.getResourceId());
            }
        });
        viewHolder.public_video_item_collect_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppContent.UID.equals("")) {
                    showToastMsg("用户没有登录，请先登录！");
                    mContext.startActivity(new Intent(mContext, LoginActivity.class));
                } else if (AppContent.LIMIT_LOGIN == 2) {
                    showToastMsg("当前登录状态未激活，请激活后重新登录。");
                    mContext.startActivity(new Intent(mContext, LoginActivity.class));
                } else {
                    if (item.getIsCollection() == 0) {
                        collectVideo(viewHolder, item);
                    } else {
                        cancelCollectVideo(viewHolder, item);
                    }
                }
            }
        });
        return convertView;
    }

    private void attachZanView(final ViewHolder viewHolder, int likesStatus, int likesTotal){
        if (likesStatus == 0) {
            viewHolder.public_video_item_zan_ib.setImageResource(R.drawable.classify_detail_zan_normal);
        } else if (likesStatus == 1) {
            viewHolder.public_video_item_zan_ib.setImageResource(R.drawable.classify_detail_zan_press);
        }
        viewHolder.public_video_item_zan_num_tv.setText("赞 " + likesTotal);
    }
    private void attachCollectionView(ViewHolder viewHolder, int collectionStatus){
        if(collectionStatus ==0){
            viewHolder.public_video_item_collect_ib.setImageResource(R.drawable.classify_detail_collect_normal);
        }else if(collectionStatus == 1){
            viewHolder.public_video_item_collect_ib.setImageResource(R.drawable.classify_detail_collect_press);
        }
    }

    static class ViewHolder {
        @Bind(R.id.public_video_item_icon_iv)
        ImageView public_video_item_icon_iv;
        @Bind(R.id.public_video_item_bottom_text_one_tv)
        TextView public_video_item_bottom_text_one_tv;
        @Bind(R.id.public_video_item_bottom_text_two_tv)
        TextView public_video_item_bottom_text_two_tv;
        @Bind(R.id.public_video_item_bottom_text_three_tv)
        TextView public_video_item_bottom_text_three_tv;
        @Bind(R.id.public_video_item_bottom_text_four_tv)
        TextView public_video_item_bottom_text_four_tv;
        @Bind(R.id.public_video_item_more_operation_iv)
        ImageView public_video_item_more_operation_iv;
        @Bind(R.id.public_video_item_bottom_lv_iv)
        ImageView public_video_item_bottom_lv_iv;
        @Bind(R.id.public_video_item_bottom_people_iv)
        ImageView public_video_item_bottom_people_iv;
        @Bind(R.id.public_video_item_more_operation_ll)
        LinearLayout public_video_item_more_operation_ll;
        @Bind(R.id.public_video_item_bottom_text_left_ll)
        LinearLayout public_video_item_bottom_text_left_ll;
        @Bind(R.id.public_video_item_bottom_text_right_ll)
        LinearLayout public_video_item_bottom_text_right_ll;
        @Bind(R.id.public_video_item_zan_ll)
        LinearLayout public_video_item_zan_ll;
        @Bind(R.id.public_video_item_download_ll)
        LinearLayout public_video_item_download_ll;
        @Bind(R.id.public_video_item_share_ll)
        LinearLayout public_video_item_share_ll;
        @Bind(R.id.public_video_item_collect_ll)
        LinearLayout public_video_item_collect_ll;
        @Bind(R.id.public_video_item_zan_ib)
        ImageButton public_video_item_zan_ib;
        @Bind(R.id.public_video_item_zan_num_tv)
        TextView public_video_item_zan_num_tv;
        @Bind(R.id.public_video_item_zan_add_one_tv)
        TextView public_video_item_zan_add_one_tv;
        @Bind(R.id.public_video_item_collect_ib)
        ImageButton public_video_item_collect_ib;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    /**
     * @Title: collectVideo
     * @Description: TODO 点赞接口
     * @author changjigang @param
     * 设定文件 @return void 返回类型 @throws
     */
    private void zanVideo(final ViewHolder viewHolder, final MainClassifyTypeListObj item) {
        // 加密用户ID
        String uid;
        if (AppContent.UID.equals("")) {
            uid = "-1";
        } else {
            uid = AppContent.UID;
        }
        String ras_uid = VR_RsaUtils.encodeByPublicKey(uid);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("resourceId", item.getResourceId());
        hashMap.put("userId", ras_uid);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData("/resource/like/service", jsonData, new GenericsCallback<HttpZanObj>() {
            @Override
            public void onError(Call call, Exception e, int id) {
//                showToastMsg("网络连接失败!请检查网络");
            }

            @Override
            public void onResponse(HttpZanObj result, int id) {
                if (result.getResCode().equals("0")) {
                    if (result.getLikesStatus() == 0) {
//                        showToastMsg("取消成功");

                        viewHolder.public_video_item_zan_ib.setImageResource(R.drawable.classify_detail_zan_normal);
                    } else if (result.getLikesStatus() == 1) {
//                        showToastMsg("点赞成功");
                        viewHolder.public_video_item_zan_ib.setImageResource(R.drawable.classify_detail_zan_press);
                        viewHolder.public_video_item_zan_add_one_tv.setVisibility(View.VISIBLE);
                        viewHolder.public_video_item_zan_add_one_tv.startAnimation(btnAnim);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                viewHolder.public_video_item_zan_add_one_tv.setVisibility(View.GONE);
                            }
                        }, 1000);
                    }
                    viewHolder.public_video_item_zan_num_tv.setText("赞 " + result.getLikesTotal());
                } else {
//                    showToastMsg(result.getResDesc());
                }
            }
        });
    }


    /**
     * @Title: collectVideo @Description: TODO 收藏视频接口 @author hechuang @param
     * 设定文件 @return void 返回类型 @throws
     */
    private void collectVideo(final ViewHolder viewHolder, final MainClassifyTypeListObj item) {
        // 加密用户ID
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("collectionType", 1);
        hashMap.put("sourceId", item.getResourceId());
        hashMap.put("userId", Ras_uid);
        // 转参数
        final String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData("/user/resources/collection/service", jsonData, new GenericsCallback<ResourcesCollect>() {
            @Override
            public void onResponse(ResourcesCollect result, int id) {
                if (result.getResCode().equals("0")) {
                    viewHolder.public_video_item_collect_ib.setImageResource(R.drawable.classify_detail_collect_press);
                    item.setCollectionId(result.getCollectId());
                } else {
                    if (result.getResCode().equals("-91")) {
                        showToastMsg("您已收藏");
                    }
                }
            }

            @Override
            public void onError(Call call, Exception e, int id) {
//                showToastMsg("网络连接失败!请检查网络");
            }
        });
    }

    private void cancelCollectVideo(final ViewHolder viewHolder, final MainClassifyTypeListObj item) {
        // 加密用户ID
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("collectIds", item.getCollectionId());
        hashMap.put("userId", Ras_uid);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postObjectData("/user/resources/collection/cancel/service", jsonData, new HttpSuccess<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result, int id) {
                try {
                    if (result.get("resCode").equals("0")) {
                        item.setCollectionId(-1);
                    }
                    viewHolder.public_video_item_collect_ib.setImageResource(R.drawable.classify_detail_collect_normal); //  后台接口不完善，暂时不判断成功与否
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new HttpError() {
            @Override
            public void onError(Call call, Exception e, int id) {
                viewHolder.public_video_item_collect_ib.setImageResource(R.drawable.classify_detail_collect_normal); //  后台接口不完善，暂时不判断成功与否
            }
        });
    }

}

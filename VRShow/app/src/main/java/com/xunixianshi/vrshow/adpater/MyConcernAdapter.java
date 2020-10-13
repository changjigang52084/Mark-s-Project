package com.xunixianshi.vrshow.adpater;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.MLog;
import com.hch.viewlib.util.ScreenUtils;
import com.hch.viewlib.widget.view.CircleImageView;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAda;
import com.xunixianshi.vrshow.interfaces.DeleteItemInterface;
import com.xunixianshi.vrshow.my.MyCustomerServiceActivity;
import com.xunixianshi.vrshow.obj.UserConcernResultList;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.api.OkHttpAPI;
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
 * TODO 我的关注适配器
 *
 * @author MarkChang
 * @ClassName MyConcernAdapter
 * @time 2016/11/1 16:02
 */

public class MyConcernAdapter extends BaseAda<UserConcernResultList> {

    private String followerUserId;
    boolean isMySelf;
    private DeleteItemInterface mDeleteItemInterface;

    public MyConcernAdapter(Context context) {
        super(context);
    }

    public void isMySelf(boolean isMySelf) {
        this.isMySelf = isMySelf;
    }

    public void setDeleteInterface(DeleteItemInterface deleteItemInterface) {
        this.mDeleteItemInterface = deleteItemInterface;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.my_concern_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final UserConcernResultList item = getItem(position);
        PicassoUtil.loadImage(mContext, item.getUserIcon() + "?imageView2/2/w/" + (ScreenUtils.dip2px(mContext, 60) + 120), viewHolder.my_concern_item_icon_civ);
        viewHolder.my_concern_item_name_tv.setText(item.getUserName());
        viewHolder.my_concern_item_content_tv.setText(item.getUpdateRemark());
        followerUserId = item.getUserId();
        if (isMySelf) {
            viewHolder.my_concern_item_cancel_tv.setVisibility(View.VISIBLE);
        } else {
            viewHolder.my_concern_item_cancel_tv.setVisibility(View.GONE);
        }
        viewHolder.my_concern_item_cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CancelConcern(item.getUserId(), position);
            }
        });
        return convertView;
    }

    public class ViewHolder {
        @Bind(R.id.my_concern_item_icon_civ)
        CircleImageView my_concern_item_icon_civ;
        @Bind(R.id.my_concern_item_name_tv)
        TextView my_concern_item_name_tv;
        @Bind(R.id.my_concern_item_content_tv)
        TextView my_concern_item_content_tv;
        @Bind(R.id.my_concern_item_cancel_tv)
        TextView my_concern_item_cancel_tv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    // 取消关注
    private void CancelConcern(String otherUid, final int position) {
        MLog.d("otherUid:" + otherUid);
        if(StringUtil.isEmpty(otherUid)){
            showToastMsg("该用户不存在");
            return;
        }
        String rsa_otherUid = VR_RsaUtils.encodeByPublicKey(otherUid);
        String rsa_UID = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("beFollowerUserId", rsa_otherUid);
        hashMap.put("followerUserId", rsa_UID);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        // 获取网络数据
        OkHttpAPI.postObjectData("/user/attention/cancel/service", jsonData, new HttpSuccess<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result, int id) {
                try {
                    if (result.getString("resCode").equals("0")) {
                        removeItem(position);
                        mDeleteItemInterface.showDeleteDialog(0, position);
//                        showToastMsg(result.getString("resDesc"));
                    } else if (result.getString("resCode").equals("-97")) {
//                        showToastMsg(result.getString("resDesc"));
                    } else {
//                        showToastMsg(result.getString("resDesc"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new HttpError() {
            @Override
            public void onError(Call call, Exception e, int id) {
//                showToastMsg("网络连接失败，请检查网络！");
            }
        });
    }
}

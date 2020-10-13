package com.xunixianshi.vrshow.adpater;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAda;
import com.xunixianshi.vrshow.my.fragment.CreateContentActivity;
import com.xunixianshi.vrshow.obj.AuditFailedResult;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * TODO 内容发布失败适配器
 *
 * @author MarkChang
 * @ClassName AuditFailedAdapter
 * @time 2016/11/1 16:01
 */
public class AuditFailedAdapter extends BaseAda<AuditFailedResult> {
    private Context mContext;

    public AuditFailedAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.audit_failed_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final AuditFailedResult item = getItem(position);

        viewHolder.audit_failed_content_tv.setText(item.getResourceName());
        viewHolder.audit_failed_reason_tv.setText("审核失败原因：" + item.getRemark());
        viewHolder.audit_failed_re_upload_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showToastMsg("正在努力开发中……");
                Intent intent1 = new Intent(mContext, CreateContentActivity.class);
                intent1.putExtra("videoPath", "");
                intent1.putExtra("videoName", item.getResourceName());
                intent1.putExtra("videoIntroduce", item.getSmallIntroduce());
                intent1.putExtra("isNative", false);
                mContext.startActivity(intent1);
            }
        });

        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.audit_failed_content_tv)
        TextView audit_failed_content_tv;
        @Bind(R.id.audit_failed_re_upload_tv)
        TextView audit_failed_re_upload_tv;
        @Bind(R.id.audit_failed_reason_tv)
        TextView audit_failed_reason_tv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

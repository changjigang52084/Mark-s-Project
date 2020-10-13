package com.xunixianshi.vrshow.customview;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hch.viewlib.util.ScreenUtils;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.interfaces.EditDialogTextInterface;
import com.zhy.http.okhttp.utils.PicassoUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by xnxs-ptzx04 on 2016/12/28.
 */

public class EditTextDialog extends Dialog {


    @Bind(R.id.edit_dialog_title_text_tv)
    TextView edit_dialog_title_text_tv;
    @Bind(R.id.edit_dialog_image_iv)
    ImageView edit_dialog_image_iv;
    @Bind(R.id.edit_dialog_text_et)
    EditText edit_dialog_text_et;
//    @Bind(R.id.edit_dialog_cancel_bt)
//    Button edit_dialog_cancel_bt;
    @Bind(R.id.edit_dialog_ok_bt)
    Button edit_dialog_ok_bt;

    private Context mContext;

    EditDialogTextInterface okClickListener;
    View.OnClickListener cancelClickListener;
    private String titleText;
    private String editText;
    private String imageUrl;
    private String okText;
    private String cancelText;

    public EditTextDialog(Context context) {
        super(context, R.style.custom_layout_dialog);
        this.mContext = context;
        // 点击对话框外是否让dialog消失
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.edittext_dialog);
        ButterKnife.bind(this);
        ViewGroup.LayoutParams alpha_lp;
        int screenWith = ScreenUtils.getScreenWidth(mContext);
        ViewGroup.LayoutParams lp;
        lp = edit_dialog_image_iv.getLayoutParams();
        lp.width = screenWith;
        lp.height = (int) (screenWith * 0.4);
        edit_dialog_image_iv.setLayoutParams(lp);

    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setContextText(String contextText) {
        this.editText = contextText;
    }


    public void setOkButText(String ok){
        this.okText  = ok;
    }

    public void setCancelText(String cancel){
        this.cancelText  = cancel;
    }


    public void setOkClickListaner(EditDialogTextInterface okClickListener){
        this.okClickListener = okClickListener;
    }
//    public void setCancelClickListener(View.OnClickListener onCancelClickListener){
//        this.cancelClickListener = onCancelClickListener;
//    }

    @Override
    public void show() {
        PicassoUtil.loadImage(mContext,imageUrl,edit_dialog_image_iv);
        //dialog 头部信息
//        if(titleText != null &&titleText.length() > 0){
//            edit_dialog_title_text_tv.setText(titleText);
//        }else{
//            edit_dialog_title_text_tv.setVisibility(View.GONE);
//        }
        //dialog 文案信息
        if(editText != null &&editText.length() > 0){
            edit_dialog_text_et.setText(editText);
        }else{
//            edit_dialog_text_et.setVisibility(View.GONE);
        }

        //确定按钮监听
        if(okClickListener != null){
            edit_dialog_ok_bt.setText(okText);
            edit_dialog_ok_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editText = edit_dialog_text_et.getText().toString().trim();
                    okClickListener.okButtonCarryText(editText);
                }
            });
        }else{
            edit_dialog_ok_bt.setVisibility(View.GONE);
        }
//        //取消按钮监听
//        if(cancelClickListener != null){
//            edit_dialog_cancel_bt.setText(cancelText);
//            edit_dialog_cancel_bt.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    cancelClickListener.onClick(v);
//                }
//            });
//        }else{
//            edit_dialog_cancel_bt.setVisibility(View.GONE);
//        }

        super.show();
    }
}

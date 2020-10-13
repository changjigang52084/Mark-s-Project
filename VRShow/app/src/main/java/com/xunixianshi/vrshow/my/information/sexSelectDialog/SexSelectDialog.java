package com.xunixianshi.vrshow.my.information.sexSelectDialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xunixianshi.vrshow.R;

/**
 * TODO 选择性别自定义dialog
 *
 * @author MarkChang
 * @ClassName SexSelectDialog
 * @time 2016/11/1 15:42
 */

public class SexSelectDialog extends Dialog implements View.OnClickListener {
    RelativeLayout nanLayout, nvLayout;
    ImageView nanImageView, nvImgageView;
    OnSexSelectLister sexSelectLister;
    Context context;
    AlertDialog dialog;

    public SexSelectDialog(Context context) {
        super(context);
        this.context = context;
        dialog = new android.app.AlertDialog.Builder(context).create();
        dialog.show();
        Window window = dialog.getWindow();
        window.setContentView(R.layout.sex_select_dialog);
        nanLayout = (RelativeLayout) window.findViewById(R.id.sex_select_nan_button);
        nanLayout.setOnClickListener(this);
        nvLayout = (RelativeLayout) window.findViewById(R.id.sex_select_nv_button);
        nvLayout.setOnClickListener(this);

        nanImageView = (ImageView) window.findViewById(R.id.sex_dialog_nanimg);
        nvImgageView = (ImageView) window.findViewById(R.id.sex_dialog_nvimg);

    }

    public void setSex(String sexString) {
        if ("男".equals(sexString)) {
            nanImageView.setImageResource(R.drawable.check_box_select_on);
            nvImgageView.setImageResource(R.drawable.check_box_select);
        } else {
            nvImgageView.setImageResource(R.drawable.check_box_select_on);
            nanImageView.setImageResource(R.drawable.check_box_select);
        }
    }

    public void setOnSexClickLister(OnSexSelectLister sexSelectLister) {
        this.sexSelectLister = sexSelectLister;
    }

    @Override
    public void onClick(View view) {
        dialog.dismiss();
        switch (view.getId()) {
            case R.id.sex_select_nan_button:
                if (sexSelectLister != null) {
                    sexSelectLister.sexSelectClick("男");
                    setSex("男");
                }
                ;

                break;
            case R.id.sex_select_nv_button:
                if (sexSelectLister != null) {
                    sexSelectLister.sexSelectClick("女");
                    setSex("女");
                }
                break;
            default:
        }

    }

    public interface OnSexSelectLister {
        void sexSelectClick(String sex);
    }
}

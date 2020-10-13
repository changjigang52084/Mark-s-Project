/**
 * ShectDialog.java V1.0 2014-9-15 下午8:10:33
 *
 * Copyright Talkweb Information System Co. ,Ltd. All rights reserved.
 *
 * Modification history(By Time Reason):
 *
 * Description:
 */

package com.xunixianshi.vrshow.customview;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xunixianshi.vrshow.R;

/**
* @ClassName: SheetDialog
* @Description: TODO 底部弹框dialog.用于选择拍照 相册 底部单选等功能。
* @author hechuang
* @date 2015-11-30 下午2:09:10
*
*/
public class ShareDialog extends Dialog {

	View.OnClickListener wenxinClickListener;
	View.OnClickListener pengyouquanClickListener;
	View.OnClickListener sinaClickListener;
	View.OnClickListener qqClickListener;
	View.OnClickListener alipayClickListener;
	View.OnClickListener cancelClickListener;

	private LinearLayout dialog_share_wenxin_ll;
	private LinearLayout dialog_share_pengyouquan_ll;
	private LinearLayout dialog_share_sina_ll;
	private LinearLayout dialog_share_qq_ll;
//	private LinearLayout dialog_share_alipay_ll;
//	private TextView dialog_share_cancel_tv;

	public void setWenxinClickListener(View.OnClickListener onClickListener) {
		this.wenxinClickListener = onClickListener;
	}

	public void setPengyouquanClickListener(View.OnClickListener onClickListener) {
		this.pengyouquanClickListener = onClickListener;
	}

	public void setSinaClickListener(View.OnClickListener onClickListener) {
		this.sinaClickListener = onClickListener;
	}
	public void setQqClickListener(View.OnClickListener onClickListener) {
		this.qqClickListener = onClickListener;
	}
	public void setAlipayClickListener(View.OnClickListener onClickListener) {
		this.alipayClickListener = onClickListener;
	}

	public void setOnCancelClickListener(View.OnClickListener onClickListener) {
		this.cancelClickListener = onClickListener;
	}
	public ShareDialog(Context context) {
		super(context, R.style.transparentFrameWindowStyle);
		this.setCanceledOnTouchOutside(true);
		setContentView(R.layout.dialog_share_layout);
		Window window = getWindow();
		window.setWindowAnimations(R.style.main_menu_animstyle);
		WindowManager.LayoutParams wl = window.getAttributes();
		wl.x = 0;
		wl.y = getWindow().getWindowManager().getDefaultDisplay().getHeight();
		// 以下这两句是为了保证按钮可以水平满屏
		wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
		wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
		this.getWindow().setAttributes(wl);
	}

	@Override
	public void show() {
		dialog_share_wenxin_ll = (LinearLayout) findViewById(R.id.dialog_share_wenxin_ll);
		dialog_share_pengyouquan_ll = (LinearLayout) findViewById(R.id.dialog_share_pengyouquan_ll);
		dialog_share_sina_ll = (LinearLayout) findViewById(R.id.dialog_share_sina_ll);
		dialog_share_qq_ll = (LinearLayout) findViewById(R.id.dialog_share_qq_ll);
//		dialog_share_alipay_ll = (LinearLayout) findViewById(R.id.dialog_share_alipay_ll);
//		dialog_share_cancel_tv = (TextView) findViewById(R.id.dialog_share_cancel_tv);
		dialog_share_wenxin_ll.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				wenxinClickListener.onClick(v);

			}
		});
		dialog_share_pengyouquan_ll.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				pengyouquanClickListener.onClick(v);

			}
		});
		dialog_share_sina_ll.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				sinaClickListener.onClick(v);

			}
		});
		dialog_share_qq_ll.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				qqClickListener.onClick(v);

			}
		});
//		dialog_share_alipay_ll.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				dismiss();
//				alipayClickListener.onClick(v);
//
//			}
//		});
//		dialog_share_cancel_tv.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				dismiss();
//				cancelClickListener.onClick(v);
//
//			}
//		});
		super.show();

	}
}

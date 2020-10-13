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
import android.widget.Button;

import com.xunixianshi.vrshow.R;

/**
* @ClassName: SheetDialog
* @Description: TODO 底部弹框dialog.用于选择拍照 相册 底部单选等功能。
* @author hechuang
* @date 2015-11-30 下午2:09:10
*
*/
public class SheetDialog extends Dialog {

	View.OnClickListener openTopClickListener;
	View.OnClickListener openCenterClickListener;
	View.OnClickListener openBottomClickListener;
	View.OnClickListener cancelClickListener;

	private Button sheet_dialog_opentop;
	private Button sheet_dialog_opencenter;
	private Button sheet_dialog_openbottom;
	private Button sheet_dialog_cancel;

	private String topText;
	private String centerText;
	private String bottomText;
	private String cancelText;

	public void setOnTopClickListener(View.OnClickListener onClickListener) {
		this.openTopClickListener = onClickListener;
	}

	public void setOnCenterClickListener(View.OnClickListener onClickListener) {
		this.openCenterClickListener = onClickListener;
	}

	public void setOnBottomClickListener(View.OnClickListener onClickListener) {
		this.openBottomClickListener = onClickListener;
	}

	public void setOnCancelClickListener(View.OnClickListener onClickListener) {
		this.cancelClickListener = onClickListener;
	}

	public void setCancelButtonText(String cancelText) {
		this.cancelText = cancelText;
	}

	public void setTopButtonText(String topText) {
		this.topText = topText;
	}

	public void setCenterButtonText(String centerText) {
		this.centerText = centerText;
	}

	public void setBottomButtonText(String bottomText) {
		this.bottomText = bottomText;
	}

	public SheetDialog(Context context) {
		super(context, R.style.transparentFrameWindowStyle);
		this.setCanceledOnTouchOutside(true);
		setContentView(R.layout.dialog_sheet_select);
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

		sheet_dialog_opentop = (Button) findViewById(R.id.sheet_dialog_opentop);
		sheet_dialog_opencenter = (Button) findViewById(R.id.sheet_dialog_opencenter);
		sheet_dialog_openbottom = (Button) findViewById(R.id.sheet_dialog_openbottom);
		sheet_dialog_cancel = (Button) findViewById(R.id.sheet_dialog_cancel);

		if (openTopClickListener != null) {
			if (topText != null) {
				sheet_dialog_opentop.setText(topText);
			}

			sheet_dialog_opentop.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();
					openTopClickListener.onClick(v);

				}
			});
			sheet_dialog_opentop.setVisibility(View.VISIBLE);
		} else {
			sheet_dialog_opentop.setVisibility(View.GONE);
		}
		if (openCenterClickListener != null) {
			if (centerText != null) {
				sheet_dialog_opencenter.setText(centerText);
			}

			sheet_dialog_opencenter.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();
					openCenterClickListener.onClick(v);

				}
			});
			sheet_dialog_opencenter.setVisibility(View.VISIBLE);
		} else {
			sheet_dialog_opencenter.setVisibility(View.GONE);
		}

		if (openBottomClickListener != null) {
			if (bottomText != null) {
				sheet_dialog_openbottom.setText(bottomText);
			}
			sheet_dialog_openbottom.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();
					openBottomClickListener.onClick(v);

				}
			});
			sheet_dialog_openbottom.setVisibility(View.VISIBLE);
		} else {
			sheet_dialog_openbottom.setVisibility(View.GONE);
		}

		if (cancelClickListener != null) {
			if (cancelText != null) {
				sheet_dialog_cancel.setText(cancelText);
			}
			sheet_dialog_cancel.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					cancelClickListener.onClick(v);
					dismiss();
				}
			});
			sheet_dialog_cancel.setVisibility(View.VISIBLE);
		} else {
			sheet_dialog_cancel.setVisibility(View.GONE);
		}
		super.show();

	}

	/**
	* @Title: clearData
	* @Description: TODO 清除所有数据
	* @author hechuang
	* @date 2015-12-2
	* @param     设定文件
	* @return void    返回类型
	*/
	public void clearData(){
		topText = null;
		centerText = null;
		bottomText = null;
		cancelText = null;
		openTopClickListener = null;
		openCenterClickListener = null;
		openBottomClickListener = null;
		cancelClickListener = null;
	}

}

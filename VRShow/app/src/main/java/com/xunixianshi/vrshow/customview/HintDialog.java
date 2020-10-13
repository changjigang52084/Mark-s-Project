package com.xunixianshi.vrshow.customview;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xunixianshi.vrshow.R;


public class HintDialog extends Dialog {

	private TextView hint_dialog_title_text_tv;
	private TextView hint_dialog_context_text_tv;
	private Button hint_dialog_ok_bt;
	private Button hint_dialog_cancel_bt;

	private String titleText;
	private String contextText;
	private String okText;
	private String cancelText;
	View.OnClickListener okClickListener;
	View.OnClickListener cancelClickListener;
	private View hint_dialog_divide;
	boolean isHideDivide;

	public HintDialog(Context context) {
		super(context, R.style.custom_layout_dialog);
		// 点击对话框外是否让dialog消失
		setCanceledOnTouchOutside(false);
		setContentView(R.layout.hint_dialog);
		hint_dialog_title_text_tv = (TextView) findViewById(R.id.hint_dialog_title_text_tv);
		hint_dialog_context_text_tv = (TextView) findViewById(R.id.hint_dialog_context_text_tv);
		hint_dialog_divide = (View)findViewById(R.id.hint_dialog_view);
		hint_dialog_ok_bt = (Button) findViewById(R.id.hint_dialog_ok_bt);
		hint_dialog_cancel_bt = (Button) findViewById(R.id.hint_dialog_cancel_bt);
	}

	/**
	 *title 与内容之间的分界线， 默认显示， 设置成true 隐藏
	 * @param is
	 */

	public void setHideDivide(boolean is){
		this. isHideDivide  =  is;
	}


	public void setTitleText(String titleText) {
		this.titleText = titleText;
	}

	public void setContextText(String contextText) {
		this.contextText = contextText;
	}


	public void setOkButText(String ok){
		this.okText  = ok;
	}

	public void setCancelText(String cancel){
		this.cancelText  = cancel;
	}


	public void setOkClickListaner(View.OnClickListener okClickListener){
		this.okClickListener = okClickListener;
	}
	public void setCancelClickListener(View.OnClickListener onCancelClickListener){
		this.cancelClickListener = onCancelClickListener;
	}

	@Override
	public void show(){
		//dialog 头部信息
		if(titleText != null &&titleText.length() > 0){
			hint_dialog_title_text_tv.setText(titleText);
		}else{
			hint_dialog_title_text_tv.setVisibility(View.GONE);
		}
		//dialog 文案信息
		if(contextText != null &&contextText.length() > 0){
			hint_dialog_context_text_tv.setText(contextText);
		}else{
			hint_dialog_context_text_tv.setVisibility(View.GONE);
		}

		if(isHideDivide){
			hint_dialog_divide.setVisibility(View.GONE);
		}else{
			hint_dialog_divide.setVisibility(View.VISIBLE);
		}

		//确定按钮监听
		if(okClickListener != null){
			hint_dialog_ok_bt.setText(okText);
			hint_dialog_ok_bt.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					okClickListener.onClick(v);
				}
			});
		}else{
			hint_dialog_ok_bt.setVisibility(View.GONE);
		}
		//取消按钮监听
		if(cancelClickListener != null){
			hint_dialog_cancel_bt.setText(cancelText);
			hint_dialog_cancel_bt.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					cancelClickListener.onClick(v);
				}
			});
		}else{
			hint_dialog_cancel_bt.setVisibility(View.GONE);
		}
		super.show();
	}
}

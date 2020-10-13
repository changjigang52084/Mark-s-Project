package com.lzmr.bindtool.util;

import com.lzmr.bindtool.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 
* @ClassName: MyProgressDialog 
* @Description: TODO(自定义ProgressDialog) 
* @author ymchen 
* @date 2015年6月30日 下午5:56:31 
*
 */
public class MyProgressDialog extends Dialog {


    private static TextView mTitleTv;

    private static ProgressBar mProgressBar;



    public MyProgressDialog(Context context) {
        super(context, R.style.transparentFrameWindowStyle);
    }


    /**
     * 
    * @Title: show 
    * @Description: TODO(显示进度对话框) 
    * @param @param context
    * @param @param title
    * @param @param canCancle
    * @param @return    设定文件 
    * @return MyProgressDialog    返回类型 
    * @throws
     */
    public static MyProgressDialog show(Context context, String title, boolean canCancle) {
        // TODO Auto-generated method stub
        MyProgressDialog dialog = new MyProgressDialog(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View contentView = inflater.inflate(R.layout.my_progress_layout, null);
        mTitleTv = (TextView) contentView.findViewById(R.id.loading_text);
        mTitleTv.setText(title);
        mProgressBar = (ProgressBar) contentView.findViewById(R.id.loading_progressbar);
        dialog.setContentView(contentView);
        dialog.setCanceledOnTouchOutside(canCancle);
        dialog.show();
        return dialog;
    }


}

package com.xunixianshi.vrshow.util;

import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by duan on 2016/12/8.
 */

public class MaxLengthWatcher {
    private EditText content;//定义一个文本输入框
    private TextView hasNumTv;// 用来显示剩余字数
    private int mMaxLength;

    private OutOfRangeListener outOfRangeListener;

    public MaxLengthWatcher(EditText content, int maxLength){
        this.content = content;
        this.mMaxLength = maxLength;
    }

    public MaxLengthWatcher(EditText content, int maxLength,TextView textView){
        this.content = content;
        this.mMaxLength = maxLength;
        this.hasNumTv = textView;
    }

    public void setOutOfRangeListener(OutOfRangeListener outOfRangeListener){
        this.outOfRangeListener = outOfRangeListener;
    }

    public void addTextChangedListener(){
        content.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
        @Override
        public void afterTextChanged(Editable s) {
            Editable editable = content.getText();
            int len = editable.length();

            if(len > mMaxLength)
            {
                int selEndIndex = Selection.getSelectionEnd(editable);
                String str = editable.toString();
                //截取新字符串
                String newStr = str.substring(0,mMaxLength);
                content.setText(newStr);
                editable = content.getText();

                //新字符串的长度
                int newLen = editable.length();
                //旧光标位置超过字符串长度
                if(selEndIndex > newLen)
                {
                    selEndIndex = editable.length();
                }
                //设置新光标所在的位置
                Selection.setSelection(editable, selEndIndex);

                if(outOfRangeListener!=null){
                    outOfRangeListener.outOfRange(mMaxLength-len);
                }
            }
        }
    };

    public interface OutOfRangeListener{
        void outOfRange(int count);
    }
}

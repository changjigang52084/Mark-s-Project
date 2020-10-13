package com.unccr.zclh.dsdps.sip;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.play.PlayActivity;
import com.unccr.zclh.dsdps.util.CONS;
import com.unccr.zclh.dsdps.util.ConfigSettings;
import com.unccr.zclh.dsdps.util.InCallUtils;

import java.util.ArrayList;
import java.util.List;

import blue.all.BluetelEngine;
import blue.bluetelobserver.BluetelInterface;

public class SipEngine implements BluetelInterface {

    public static final String TAG = "SipEngine";
    private static SipEngine instance;
    public boolean onLine = false;
    private String name = "0";
    private String pw = "0";
    public String ip = "0";
    private int port = 0;
    public static List<InCallUtils> callPagesConfig = new ArrayList<InCallUtils>();//所有通话页面的集合
    private BluetelEngine myBluetelEngine;

    public static SipEngine getInstance() {
        if (instance == null) {
            instance = new SipEngine();
        }
        return instance;
    }

    //删除页面配置
    private void removeConfig(int callid) {
        //移除页面配置
        InCallUtils cursorPagesConfig = null;
        for (InCallUtils icu : callPagesConfig) {
            if (icu.getCallId() == callid) {
                cursorPagesConfig = icu;
            }
        }
        if (cursorPagesConfig != null) {
            callPagesConfig.remove(cursorPagesConfig);
        }
    }

    /**
     * 根据号码查通话的id
     *
     * @param callnumber
     * @return
     */
    public int getCallId(String callnumber) {
        int res = 0;
        for (InCallUtils icu : callPagesConfig) {
            if (icu.getCallNumber().equals(callnumber)) {
                res = icu.getCallId();
                break;
            }
        }
        return res;
    }

    /**
     * 退出
     */
    public void stop() {
        myBluetelEngine.deinit();
    }

    public String getip() {
        return ip;
    }

    public boolean isonLine() {
        return onLine;
    }

    /**
     * sip初始化（需要1秒）
     */
    public void init() {
        if (myBluetelEngine == null) {
            myBluetelEngine = new BluetelEngine(DsdpsApp.getDsdpsApp(), this);
        } else {
            myBluetelEngine.Stop();
            myBluetelEngine = new BluetelEngine(DsdpsApp.getDsdpsApp(), this);
        }
        Log.e(TAG, "服务启动");
    }

    /**
     * sip注册
     *
     * @param name 账号
     * @param pw   密码
     * @param ip   服务器地址
     * @param port 端口
     */
    public void Register(String name, String pw, String ip, int port) {
        this.name = name;
        this.pw = pw;
        this.ip = ip;
        this.port = port;
        myBluetelEngine.Register(name, pw, ip, port);
    }

    /**
     * sip注销
     */
    public void Unregister() {
        myBluetelEngine.Stop();
    }

    /****************************  回调接口 *************************************************/

    /**
     * 账号的注册状态
     * 1、注册或注销的时候会触发
     * 2、注册成功后，每隔60秒触发一次
     *
     * @param s 例如sip:604@192.168.0.241
     * @param b true或false
     */
    @Override
    public void AccountState(String s, boolean b) {
        Log.e(TAG, "AccountState: " + s + "<>" + b);
        if (b) {
            //CONS.SENDMESSAGETO(PlayActivity.handler_PlayActivity, CONS.LOGIN, null);
            ConfigSettings.SIP_IS_LOGIN = true;
        }else{
            ConfigSettings.SIP_IS_LOGIN = false;
        }
    }

    /**
     * 待机状态下来电
     *
     * @param s  来电号码
     * @param b  是否是视频来电
     * @param i  视频的远程端口
     * @param i1 视频的本地端口
     * @param b1 是否处于保持状态
     * @param i2 通话的id
     */
    @Override
    public void FirstIncoming(String s, boolean b, int i, int i1, boolean b1, int i2) {
        Log.e(TAG, "FirstIncoming: " + s + "<>" + b + "<>" + i + "<>" + i1 + "<>" + b1 + "<>" + i2);
        int callType = b == false ? 0 : 1;
        String state = b == true ? "视频来电" : "音频来电";
        callPagesConfig.add(new InCallUtils(s, b1, b, i, i1, s, state, false, 0, i2));
    }

    /**
     * 通话状态下来电
     *
     * @param s  来电号码
     * @param b  是否是视频来电
     * @param i  视频的远程端口
     * @param i1 视频的本地端口
     * @param b1 是否处于保持状态
     * @param i2 通话的id
     */
    @Override
    public void OtherIncoming(String s, boolean b, int i, int i1, boolean b1, int i2) {
        Log.e(TAG, "OtherIncoming: " + s + "<>" + b + "<>" + i + "<>" + i1 + "<>" + b1 + "<>" + i2);
        String state = b == true ? "视频来电" : "音频来电";
        callPagesConfig.add(new InCallUtils(s, b1, b, i, i1, s, state, false, 0, i2));
    }

    /**
     * 有通话断开与下面的CallState状态6一致
     *
     * @param i 通话的id
     * @param s 通话的远程号码
     */
    @Override
    public void StateCallDown(int i, String s) {
        Log.e(TAG, "StateCallDown: " + i + "<>" + s);
        removeConfig(i);
        CONS.SENDMESSAGETO(PlayActivity.handler_PlayActivity, CONS.CALLDOWN, i);
    }

    /**
     * 通话状态变更
     *
     * @param i  通话的id
     * @param i1 通话状态
     * @param s  远程号码
     */
    @Override
    public void CallState(int i, int i1, String s) {
        Log.d(TAG, "CallState i: " + i + " ,i1: " + i1 + " ,s: " + s);
        String stateValues = "未知状态";
        switch (i1) {
            case 1:
                stateValues = "呼叫中";//也可以在这里启动主动呼叫界面
                break;
            case 2:
                stateValues = "未知";
                break;
            case 3:
                stateValues = "振铃中";
                break;
            case 4:
                stateValues = "连接中";
                break;
            case 5:
                stateValues = "通话中";
                //一般在这里进行增益调节
                break;
            case 6:
                stateValues = "挂断";
                break;
        }
        Log.e(TAG, "CallState: " + i + "<>" + i1 + "<>" + s);
        CONS.SENDMESSAGETO(PlayActivity.handler_PlayActivity, CONS.CALLSTATE, stateValues);
    }

    /**
     * 通话音视频状态变更
     *
     * @param i  通话的id
     * @param i1 远程端口
     * @param i2 本地端口
     * @param i3 payload
     */
    @Override
    public void NotifyCallMediaState(int i, int i1, int i2, int i3) {
        Log.e(TAG, "NotifyCallMediaState: " + i + "<>" + i1 + "<>" + i2 + "<>" + i3);
        CONS.SENDMESSAGETO(PlayActivity.handler_PlayActivity, CONS.MEDIASTATE, new CallMediaUtils(i, i3, i1, i2));
    }

    /**
     * 错误日志
     *
     * @param s
     */
    @Override
    public void ErrorMessage(String s) {
        Log.e(TAG, "ErrorMessage: " + s);
    }

    /************************  方法  ***************************************************/

    /**
     * 打电话
     *
     * @param number  呼叫的号码
     * @param isVideo 是否是视频呼叫
     * @return
     */
    public int CallNumber(String number, boolean isVideo) {
        int callid = myBluetelEngine.CallNumber(number, ip, port, isVideo);
        int calltype = isVideo ? 0 : 1;
        Log.d(TAG, "CallNumber" + "\nnumber: " + number + "\nisVideo: " + isVideo + "\ncallid: " + callid + "\ncalltype: " + calltype);
        CallBean callBean = new CallBean();
        callBean.setCallid(callid);
        callBean.setCallnumber(number);
        callBean.setCallstate("呼叫中");
        callBean.setCalltype(calltype);
        CONS.SENDMESSAGETO(PlayActivity.handler_PlayActivity, CONS.CALLING, callBean);
//        GoToInCall(DsdpsApp.getDsdpsApp(), number, "呼叫中", calltype, callid);
        callPagesConfig.add(new InCallUtils(number, false, isVideo, 0, 0, number, "呼叫中", false, 0, callid));
        return callid;
    }

    /**
     * 挂断电话
     *
     * @param callid
     */
    public void hangup(int callid) {
        myBluetelEngine.hangup(callid);
        removeConfig(callid);//移除页面配置
    }

    /**
     * 接听电话
     *
     * @param callid
     */
    public void answer(int callid) {
        myBluetelEngine.answer(callid);
    }


    /**
     * 通话增益调节
     *
     * @param callId
     * @param tx
     * @param rx
     */
    public void setAdjustAudio(int callId, int tx, int rx) {
        myBluetelEngine.setAdjustAudio2(callId, tx, rx);
    }

    /**
     * 通话保持
     *
     * @param isHolder
     * @param callid
     * @return
     */
    public boolean Holder(boolean isHolder, int callid) {
        return myBluetelEngine.Holder(isHolder, callid);
    }
}

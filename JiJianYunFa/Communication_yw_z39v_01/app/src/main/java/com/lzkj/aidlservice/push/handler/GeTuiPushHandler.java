package com.lzkj.aidlservice.push.handler;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.igexin.sdk.PushConsts;
import com.lzkj.aidlservice.push.control.CommandControl;
import com.lzkj.aidlservice.util.SharedUtil;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2016年4月14日 下午4:57:47
 * @parameter 个推穿透消息处理类
 */
public class GeTuiPushHandler extends PushHandler {

    private static final String TAG = "GeTuiPushHandler";

    /**
     * payload key
     **/
    private static final String PAYLOAD_KEY = "payload";
    /**
     * clientid key
     **/
    private static final String CLIENTID_KEY = "clientid";
    /**
     * utf-8
     */
    private static final String UTF8_CODE = "UTF-8";
    /**
     * 命令控制类
     **/
    private CommandControl mCommandControl;

    public GeTuiPushHandler() {
        mCommandControl = new CommandControl();
    }

    /**
     * 处理穿透消息的方法
     *
     * @param msgIntent 穿透消息对象
     */
    @Override
    public void handlerPerspective(Intent msgIntent) {
        if (null != msgIntent) {
            Bundle msgBundle = msgIntent.getExtras();
            if (null != msgBundle) {
                int i = msgBundle.getInt(PushConsts.CMD_ACTION, -1);
                Log.d(TAG, "i: " + i);
                switch (i) {
                    case PushConsts.GET_MSG_DATA:
                        handlePlayLoadMsg(msgBundle);
                        break;
                    case PushConsts.GET_CLIENTID:
                        updateClientId(msgBundle);
                        break;
                    default:
                        Log.i(TAG, "perspective cmd action is error");
                        break;
                }
            }
        }
    }

    /**
     * 处理穿透消息
     *
     * @param msgBundle 穿透消息对象
     */
    private void handlePlayLoadMsg(Bundle msgBundle) {
        byte[] payload = msgBundle.getByteArray(PAYLOAD_KEY);// 获取透传数据
        if (null != payload && payload.length > 0) {
            String payloadMessage = new String(payload);
            Log.d(TAG, "payloadMessage: " + payloadMessage);
            mCommandControl.handlerPayloadMsg(payloadMessage);
        }
    }

    /**
     * 更新client Id
     * <p>
     * .0
     *
     * @param msgBundle 穿透消息对象
     */
    private void updateClientId(Bundle msgBundle) {
        // 获取ClientID(CID)
        String clientId = msgBundle.getString(CLIENTID_KEY);
        SharedUtil.newInstance().setString(SharedUtil.CLIENT_ID, clientId);
        Log.d(TAG, "clientId: " + clientId);
    }
}

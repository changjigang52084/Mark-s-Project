package com.lzkj.aidlservice.push;

import android.content.Intent;

import com.lzkj.aidlservice.push.handler.PushHandler;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2015年6月3日 上午11:06:13
 * @parameter 处理推送命令的类
 */
public class HandlerPushCommand {

    private HandlerPushCommand() {
    }

    public static HandlerPushCommand getInstance() {
        return HandlerPushCommandInstand.handlerPushCommand;
    }

    private static class HandlerPushCommandInstand {
        private static final HandlerPushCommand handlerPushCommand = new HandlerPushCommand();
    }

    /**
     * 处理穿透消息
     *
     * @param intent 包含的穿透数据的intent对象
     * @param cls    处理消息的类 GeTuiPusherHandler.class
     */
    public void handlerPayLoadMes(Intent intent, Class<? extends PushHandler> cls) {
        try {
            PushHandler pushHandler = (PushHandler) Class.forName(cls.getName()).newInstance();
            pushHandler.handlerPerspective(intent);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

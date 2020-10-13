package cn.bluetel.intercom;
import cn.bluetel.intercom.CallStateCallBack;

interface BluetelServer {
    void registerCallback(CallStateCallBack cb);
    void unRegisterCallback(CallStateCallBack cb);
    void hangUp();
}

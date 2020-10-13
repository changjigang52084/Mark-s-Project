package com.xunixianshi.vrshow.interfaces;
public interface ReceiverNetworkInterface {
    /**
    *@author duanchunlin
    *@time 2016/5/14 15:15
    *注释： 不能做持久操作。会阻塞线程，请用handler接收消息后处理
    */
    void NetworkChanged(int state);  //消息传递到这里，如果有UI相关操作，需要用Handler传递
}

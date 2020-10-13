// CallStateCallBack.aidl
package cn.bluetel.intercom;

// Declare any non-default types here with import statements

interface CallStateCallBack {
    void CallState(int callid,String remoteNumber,int state);
}

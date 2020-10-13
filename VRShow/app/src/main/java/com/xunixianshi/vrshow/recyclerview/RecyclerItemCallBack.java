package com.xunixianshi.vrshow.recyclerview;
/**
 * Created by duan on 2016/10/24.
 */
public interface RecyclerItemCallBack<T,V> {
    void onSuccess(T t);
    void onFail(V v);
}

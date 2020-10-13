package com.zhy.http.okhttp.callback;


import com.hch.utils.MLog;

import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/9/14.
 */
public abstract class JsonObjectCallback extends Callback<JSONObject> {

    @Override
    public JSONObject parseNetworkResponse(Response response, int id) throws Exception {
        String string = response.body().string();
        MLog.d("返回数据："+string);
        return new JSONObject(string);
    }
}

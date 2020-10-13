package com.android.mytest.utils;

import com.google.gson.Gson;

import java.util.Map;

public class StringUtil {

    /**
     * 将map转化为json
     * @param map
     * @return
     */
    public static String MapToJson(Map<? extends Object, ? extends Object> map){
        String string = "";
        if(null != map){
            Gson gson = new Gson();
            string = gson.toJson(map);
        }
        return string;
    }
}

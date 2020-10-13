package com.lzmr.bindtool.http;

import com.alibaba.fastjson.JSON;
import com.lzkj.baize_android.utils.StringUtils;
import com.lzmr.bindtool.bean.ResponseContent;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年5月10日 上午10:13:29 
 * @version 1.0 
 * @parameter  http 响应处理数据的工具类
 */
public class HttpResponseUtil {
	
	/**
	 * 根据响应的结果获取data值
	 * @param responseResult 响应的结果
	 * @return 返回data or null
	 */
	public static String getDataToResponseResult(String responseResult) throws Exception{
		if (StringUtils.isEmpty(responseResult)) {
			return responseResult;
		}
		String dataJsonStr = null;
		ResponseContent responseContent = JSON.parseObject(responseResult, ResponseContent.class);
		if (null != responseContent && responseContent.isSuccess()) {
			Object responseData = responseContent.getData();
			if (null != responseData) {
				dataJsonStr = responseData.toString();
			}
		}
		return dataJsonStr;
	}
}

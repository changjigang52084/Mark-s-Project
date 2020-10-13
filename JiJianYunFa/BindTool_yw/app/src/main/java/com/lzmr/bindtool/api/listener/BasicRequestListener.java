package com.lzmr.bindtool.api.listener;
/**   
*    
* 项目名称：BindTools   
* 类名称：BindResultListener   
* 类描述：  绑定、解绑监听结果的接口
* 创建人：lyhuang   
* 创建时间：2015-9-18 下午7:10:52    
* @version    
*    
*/ 
public interface BasicRequestListener {
	/**
	 * 通讯成功
	 * @param msg 提示消息
     */
	public void onSuccess(String msg);
	/**
	 * 通讯失败
	 * @param msg 提示消息
	 */
	public void onFailure(String msg);
}

package com.lzmr.bindtool.api.listener;


/**
 * 项目名称：VMTool
 * 类描述：需要通讯Session的请求监听器
 * 创建人：longyihuang
 * 创建时间：17/1/13 15:42
 * 邮箱：huanglongyi@17-tech.com
 */

public interface SessionRequestListener extends BasicRequestListener {
    /**
     * 会话session失效
     */
    public void onSessionInvalid();
}

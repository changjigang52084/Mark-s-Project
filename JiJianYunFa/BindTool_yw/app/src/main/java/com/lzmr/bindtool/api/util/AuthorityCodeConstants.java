package com.lzmr.bindtool.api.util;

/**
 * 项目名称：BindTool
 * 类描述：
 * 创建人：longyihuang
 * 创建时间：16/11/4 14:50
 * 邮箱：huanglongyi@17-tech.com
 */

public class AuthorityCodeConstants {
    //终端管理-终端列表
    public static final String AUTHORITY_CODE_DEVICE_LIST = "DMSS-v1/devices-GET";
    //终端管理-查看终端
    public static final String AUTHORITY_CODE_DEVICE = "DMSS-v1/devices/id-GET";
    //终端管理-设置解绑
    public static final String AUTHORITY_CODE_UNBIND = "DMSS-v1/devices/controls/unbind-POST";
    //终端管理-设置关机
    public static final String AUTHORITY_CODE_SHUTDOWN = "DMSS-v1/devices/controls/shutdown-POST";
    //终端管理-设置重启
    public static final String AUTHORITY_CODE_REBOOT = "DMSS-v1/devices/controls/reboot-POST";
    //终端管理-截屏
    public static final String AUTHORITY_CODE_SCREENSHOT = "DMSS-v1/devices/controls/screenshot-POST";
    //终端管理-查看截屏
    public static final String AUTHORITY_CODE_CHECK_SCREENSHOT = "DMSS-v1/devices/deviceId/screenshot-GET";
}

/**
 * Contant.java
 * com.hch.httpforjson.util
 * <p>
 * Function： TODO
 * <p>
 * ver     date      		author
 * ──────────────────────────────────
 * 2015年4月6日 		hch
 * <p>
 * Copyright (c) 2015, TNT All Rights Reserved.
 */

package com.hch.utils;

import android.os.Environment;


/**
 * ClassName:Contant
 * Function: TODO ADD FUNCTION
 * Reason:	 TODO ADD REASON
 *
 * @author hch
 * @Date 2015年4月6日        下午2:09:50
 * @since Ver 1.1
 */
public class OkhttpConstant {

    public final static String DB_NAME = "";
    public final static int DB_VERSION = 1;
    public final static int REALM_VERSION = 1;
    public final static String DB_TB_NAME = "";
    public static int VERSION_CODE = 1;
    public static String VERSION_CODE_STRING = "1";
    public static int VERSION_CODE_SERVER = 1;
    public static String IPADDRESS = "-1";

    public static String USER_PROTOCOL = "http://www.vrshow.com/userLicense";

    public static String DEVICE_NUMBER = "";
    public static boolean isDownloading = false; //  是否是正在下载状态
    /**
     * @Fields RSA_KEY : ＲＳＡ公钥
     */
    public final static String RSA_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDQ9mIWe63OiIDRh9invj8PL+xQoKIcn3zlu08GCqBzCwPxcMzSfDgzPrW22VCax30sqZnycMMWXesCZLHMW7gJYyVjfGo5/dsE0rKFJ7lExJfUNOBC7fkAzoz07qpkgDHHSKc11edT7cxCh/UrEEzjeQ4/6enixYzDoWfA8upL1QIDAQAB";
    /**
     * @Fields RSA_KEY : ＭＤ５鉴权秘钥
     */
    public final static String MD5_KEY = "92A864886F70D010101050101010500048202613082025D02010002818";

    public final static String TOKEN_KEY = "0XN122BX2m3rhr8327263fb783YZn67b67a3ex621378Y357";


    public final static String DOWN_LOAD_UNKNOWN_TYPE_NAME = "unknown";  //未知
    public final static String DOWN_LOAD_GAME_TYPE_NAME = "game";        //游戏
    public final static String DOWN_LOAD_NET_VIDEO_TYPE_NAME = "net_video";       //网络视频
    public final static String DOWN_LOAD_LOCAL_VIDEO_TYPE_NAME = "local_video"; //本地视频


    public final static String SQL_KEY_FILE_DOWN_LOADER_TYPE = "download_type";
    public final static String SQL_KEY_FILE_DOWN_LOADER_NAME = "download_name";
    public final static String SQL_KEY_FILE_DOWN_LOADER_ICON_URL = "download_iconUrl";
    public final static String SQL_KEY_FILE_DOWN_LOADER_MARK_ID = "mark_id"; //标记ID， 对于网络的resources id
    public final static String SQL_KEY_FILE_DOWN_LOADER_VIDEO_TYPE = "download_video_type";  //视频类型

    public final static String SQL_KEY_FILE_DOWN_LOADER_FILE_SIZE = "download_fileSize";
    public final static String SQL_KEY_FILE_DOWN_LOADER_APP_PACKAGE_NAME = "packageName";

    public final static String SQL_KEY_FILE_DOWN_LOADER_APP_VERSION_CODE = "versionCode";

    /* 测试地址 */
//	public final static String url = "http://192.168.200.148:8080/VR_Service/api";
    /* 罗杰调试地址 */
//	public final static String url = "http://192.168.200.219:8080/VR_Service/api";
    /* 正式地址*/
    public final static String url = "http://api.center.service.vrshow.com/VR_Service/api";
    /* 测试专员地址*/
//    public final static String url = "http://192.168.200.188:28080/VR_Service/api";
    /* 中创联合服务器地址*/
    public final static String zc_url = "http://119.23.249.140:18889";
    /* 正式图片七牛域名*/
    public final static String pic_url = "http://xnxs.img.vrshow.com/";
    /* 测试图片七牛域名*/
//    public final static String pic_url = "http://xnxs.test2.vrshow.com/";
    /* 正式视频七牛域名*/
    public final static String video_url = "http://xnxs.video.vrshow.com/";
    /* 测试视频七牛域名*/
//    public final static String video_url = "http://xnxs.test2.vrshow.com/";
}


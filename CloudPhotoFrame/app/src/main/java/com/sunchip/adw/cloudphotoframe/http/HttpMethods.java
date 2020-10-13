package com.sunchip.adw.cloudphotoframe.http;

public class HttpMethods {
    //测试服务器
//    public static String AUTH_URLHTTP = "https://vucatimes.sunchip-ad.com:10080";

    //正式服务器
    public static String AUTH_URLHTTP = "https://service.myvucatimes.com";
    //本地
//    public String AUTH_URLHTTP =  "http://10.10.13.68:10080";

    /*
     * 相框心跳接口  /v1/message/heartbest?mac={mac}&serial={sn}&type=heartbest&time={time}&sign={sign} post
     */

    public static String METHOD_FRAME_STATE = AUTH_URLHTTP + "/v1/message/heartbest";


    //   /playlists/photos  获取相框列表去播放列表的列表图片
    public static String METHOD_FRAME_PLAYLIST = AUTH_URLHTTP + "/v1/message/playlists/photos";

    /*
     * 相框后去播放列表  /v1/message/playlists?mac={mac}&serial={sn}&type=playlists&time={time}&sign={sign}
     */
    //  /playlists  获取相框去播放列表的列表名
    public static String METHOD_FRAME_TITLE = AUTH_URLHTTP + "/v1/message/playlists";

    /*
     * 解绑
     * */
    public static String METHOD_FRAME_RESYNE = AUTH_URLHTTP + "/v1/message/unpair";


}

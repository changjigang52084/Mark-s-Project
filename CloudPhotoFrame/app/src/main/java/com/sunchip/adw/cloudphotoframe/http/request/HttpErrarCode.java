package com.sunchip.adw.cloudphotoframe.http.request;

public class HttpErrarCode {

    public static int RESULT_CODE_SUCCESS = 200;//请求成功
    public static int RESULT_CODE_SERVER_ERR = 10000;//服务器错误
    public static int RESULT_CODE_PARAM_MISS = 10001;//参数缺失
    public static int RESULT_CODE_USER_PWD_ERR = 10002;//账号不存在或密码错误
    public static int RESULT_CODE_PLAYLIST_MISS = 10003;//播放列表不存在
    public static int RESULT_CODE_PARAM_ERR = 10004;//参数错误
    public static int RESULT_CODE_EMAIL_ERR = 10005;//邮箱不存在错误
    public static int RESULT_CODE_TOKEN_OVERDUE = 10006;//TOKEN过期
    public static int RESULT_CODE_FRAME_NO_EXIST = 10007;//相框不存在
    public static int RESULT_CODE_FRAME_BINDING = 10008;//相框已绑定
    public static int RESULT_CODE_ACCOUNT_PAIR_MAXIMUM = 10009;//账号已达匹配上限
    public static int RESULT_CODE_ACCOUNT_NO_AUTH = 10010;//无权操作
    public static int RESULT_CODE_ACCOUNT_MISS = 10011;//账号不存在
    public static int RESULT_CODE_NO_ADD_SELF = 10012;//不能添加自己
    public static int RESULT_CODE_ALREADY_FRIEND = 10013;//已经是好友
    public static int RESULT_CODE_FRAME_UNPAIR = 10014;//相框没配对，或已接除匹配
    public static int RESULT_CODE_ACCOUNT_LIMIT = 10015;//已达用户限制上限
    public static int RESULT_CODE_REGISTER_ERR_ACCOUNT = 10016;//用户名已经存在
    public static int RESULT_CODE_REGISTER_ERR_EMAIL = 10017;//用户邮箱已经存在

}

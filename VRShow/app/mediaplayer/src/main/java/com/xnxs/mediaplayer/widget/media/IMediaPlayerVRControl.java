package com.xnxs.mediaplayer.widget.media;

import android.app.Activity;

import com.asha.vrlib.MDVRLibrary;

/**
 * Created by duanchunlin on 2016/8/17.
 */
public interface IMediaPlayerVRControl {
    // 1表示普通视频，2表示左右格式3D，3表示单画面全景，4表示上下格式3D，5表示上下格式全景
    int VIDEO_TYPE_UNKNOWN = -1;
    int VIDEO_TYPE_NORMAL  = 1;
    int VIDEO_TYPE_L_R_3D  = 2;
    int VIDEO_TYPE_ONE_GV  = 3;
    int VIDEO_TYPE_U_D_3D  = 4;
    int VIDEO_TYPE_U_D_GV  = 5;

    int VIDEO_SPLIT_SCREEN_NO  = 0; //普通模式
    int VIDEO_SPLIT_LEFT_RIGHT = 1;//左右分屏
    int VIDEO_SPLIT_UP_DOWN = 2;//上下分屏
    int VIDEO_SPLIT_MAX = 2; //暂时没有上下分屏按钮

    boolean bindVRLibrary(Activity activity);
    boolean isBindVRLibrary();
    MDVRLibrary getMDVRLibrary();

    void onPause();
    void onResume();
    void onDestroy();
}

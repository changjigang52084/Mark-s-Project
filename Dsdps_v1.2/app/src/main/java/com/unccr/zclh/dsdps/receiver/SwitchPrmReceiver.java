package com.unccr.zclh.dsdps.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.unccr.zclh.dsdps.models.Programme;
import com.unccr.zclh.dsdps.play.ProgramPlayManager;
import com.unccr.zclh.dsdps.util.PrmFileHashUtil;
import com.unccr.zclh.dsdps.util.SharedUtil;

import java.util.ArrayList;
import java.util.List;

public class SwitchPrmReceiver extends BroadcastReceiver {

    private static final String TAG = "SwitchPrmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(null != intent){
            Log.d(TAG,"onReceive switchPrmReceiver: update program"+"1111111111");
//            if (SharedUtil.newInstance().getString(SharedUtil.PROGRAM_SOURCE_TYPE).equals("programme")) {
//                PrmFileHashUtil.get().deleteOldFiles();  //下载新素材成功删除之前的图片视频
//            }
            Programme programme = null;
            try {
                programme = JSON.parseObject(SharedUtil.newInstance().getString(SharedUtil.PROGRAMME), Programme.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ProgramPlayManager.getInstance().playProgrammeList("downloadSuccessed", programme,false);
            ProgramPlayManager.getInstance().updateProgramList();
        }
    }
}

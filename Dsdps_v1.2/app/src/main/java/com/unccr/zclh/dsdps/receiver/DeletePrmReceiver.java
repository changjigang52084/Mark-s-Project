package com.unccr.zclh.dsdps.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.manager.DelProgramManager;
import com.unccr.zclh.dsdps.models.Program;
import com.unccr.zclh.dsdps.play.ProgramPlayManager;
import com.unccr.zclh.dsdps.play.ProgramSchedule;
import com.unccr.zclh.dsdps.util.Constants;

import java.util.ArrayList;
import java.util.List;

public class DeletePrmReceiver extends BroadcastReceiver {

    private static final String TAG = "DeletePrmReceiver";

    /**
     * 删除节目的key
     */
    private static final String DELETE_PRM_KEY = "deletePrmKey";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != intent) {
            if (Constants.NOTFIY_DELETE_PRM_ACTION.equals(intent.getAction())) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    ArrayList<String> prmKeys = bundle.getStringArrayList("deletePrmKey");
                    if (prmKeys != null && prmKeys.size() > 0) {
                        Log.i(TAG, "onReceive DeletePrmReceiver: delete program: " + prmKeys.toString());
                        updateCurrentPrmList(prmKeys);
                        reportDeletePrmCommand(prmKeys);
                    }
                }
            } else if(Constants.REPORT_DELETE_PRM_ACTION.equals(intent.getAction())){
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    ArrayList<String> prmKeys = bundle.getStringArrayList(DELETE_PRM_KEY);
                    if (prmKeys != null && !prmKeys.isEmpty()) {
                        DelProgramManager.getInstance().delPrmResourceByPrmId(prmKeys);
                    }

                    Intent intent1 = new Intent();
                    intent1.setAction(Constants.SECOND_SCREEN_ACTION);
                    DsdpsApp.getDsdpsApp().sendBroadcast(intent1);

                }
            }
        }
    }

    /**
     * 更新当前的播放列表。把撤销节目移出播放列表，如果当前播放的节目为撤销节目，切换下一个节目播放。
     *
     * @param prmKeys 撤销节目的id
     * @return
     */
    private void updateCurrentPrmList(List<String> prmKeys) {
        ProgramSchedule programSchedule = ProgramPlayManager.getInstance().getProgramSchedule();
        if (programSchedule == null) {
            return;
        }
        //删除播放列表中的节目
        programSchedule.deleteProgramByKey(prmKeys);
        //如果当前播放的节目为撤销节目，切换下一个节目播放。
        Program currentProgram = programSchedule.getCurrentProgram();
        if (currentProgram != null) {
            if (prmKeys.contains(currentProgram.getKey())) {
                //切换下一个节目播放
                ProgramPlayManager.getInstance().loadNextProgram();
            }
        } else {
            ProgramPlayManager.getInstance().clearPreProgram();
        }
    }

    /**
     *
     * @param prmKeys 撤销节目的id
     */
    private void reportDeletePrmCommand(ArrayList<String> prmKeys) {
        Intent intent = new Intent();
        intent.setAction(Constants.REPORT_DELETE_PRM_ACTION);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("deletePrmKey", prmKeys);
        intent.putExtras(bundle);
        DsdpsApp.getDsdpsApp().sendBroadcast(intent);
    }

}

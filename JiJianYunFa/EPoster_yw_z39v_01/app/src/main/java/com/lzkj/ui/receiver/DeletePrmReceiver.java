package com.lzkj.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Program;
import com.lzkj.ui.app.EPosterApp;
import com.lzkj.ui.play.ProgramPlayManager;
import com.lzkj.ui.play.ProgramSchedule;
import com.lzkj.ui.util.Constants;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;

import java.util.ArrayList;
import java.util.List;

/**
 * 接收撤销节目的广播
 *
 * @author lyhuang
 * @date 2016-2-15 下午3:52:35
 */
public class DeletePrmReceiver extends BroadcastReceiver {
    private static final LogTag TAG = LogUtils.getLogTag(DeletePrmReceiver.class.getSimpleName(), true);

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != intent) {
            if (Constants.NOTFIY_DELETE_PRM_ACTION.equals(intent.getAction())) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    ArrayList<String> prmKeys = bundle.getStringArrayList("deletePrmKey");
                    if (prmKeys != null && prmKeys.size() > 0) {
                        LogUtils.i(TAG, "onReceive", "DeletePrmReceiver: delete program: " + prmKeys.toString());
                        updateCurrentPrmList(prmKeys);
                        reportDeletePrmCommand(prmKeys);
                    }
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
            LogUtils.w(TAG, "updateCurrentPrmList", "Program Schedule is null.");
            return;
        }
        //删除播放列表中的节目
        programSchedule.deleteProgramByKey(prmKeys);
        //如果当前播放的节目为撤销节目，切换下一个节目播放。
        Program currentProgram = programSchedule.getCurrentProgram();
        if (currentProgram != null) {
            if (prmKeys.contains(currentProgram.getKey())) {
                LogUtils.i(TAG, "updateCurrentPrmList", "Current program need to delete,play next program.");
                //切换下一个节目播放
                ProgramPlayManager.getInstance().loadNextProgram();
            }
        } else {
            ProgramPlayManager.getInstance().clearPreProgram();
        }
    }

    /**
     * 汇报到Communication已撤销节目
     *
     * @param prmKeys 撤销节目的id
     */
    private void reportDeletePrmCommand(ArrayList<String> prmKeys) {
        Intent intent = new Intent();
        intent.setAction(Constants.REPORT_DELETE_PRM_ACTION);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("deletePrmKey", prmKeys);
        intent.putExtras(bundle);
        EPosterApp.getApplication().sendBroadcast(intent);
    }

}

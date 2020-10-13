package com.lzkj.ui.play;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Area;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Material;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Program;
import com.lzkj.ui.PlayActivity;
import com.lzkj.ui.R;
import com.lzkj.ui.app.EPosterApp;
import com.lzkj.ui.log.LogManager;
import com.lzkj.ui.play.interfaces.ISwitchLayoutListener;
import com.lzkj.ui.receiver.UpdateVolumeReceive;
import com.lzkj.ui.util.AppUtil;
import com.lzkj.ui.util.Constants;
import com.lzkj.ui.util.DateTimeUtil;
import com.lzkj.ui.util.FileStore;
import com.lzkj.ui.util.Helper;
import com.lzkj.ui.util.LayoutUtil;
import com.lzkj.ui.util.ListUitl;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;
import com.lzkj.ui.util.ProgramParseTools;
import com.lzkj.ui.util.SharedUtil;
import com.lzkj.ui.util.StringUtil;
import com.lzkj.ui.util.VolumeUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

/**
 * program管理类
 *
 * @author changkai
 */
public class ProgramPlayManager {
    private static final LogTag TAG = LogUtils.getLogTag(ProgramPlayManager.class.getSimpleName(), true);
    private String programErroMsg;
    /**
     * 节目调度器
     **/
    private ProgramSchedule programSchedule;
    /**
     * 当前播放的节目
     **/
    private Program currentProgram;
    /**
     * 切换界面的接口
     **/
    private ISwitchLayoutListener switchLayoutImpl = null;

    private ProgramPlayManager() {
    }

    public static ProgramPlayManager getInstance() {
        return ProgramPlayHolder.programPlayManager;
    }

    private static class ProgramPlayHolder {
        private static final ProgramPlayManager programPlayManager = new ProgramPlayManager();
    }

    /**
     * 获取节目调度器
     *
     * @return programSchedule 节目调度器
     */
    public ProgramSchedule getProgramSchedule() {
        return programSchedule;
    }

    /**
     * 设置切换节目的接口
     *
     * @param layoutImpl
     */
    public void setSwitchLayoutImpl(ISwitchLayoutListener layoutImpl) {
        switchLayoutImpl = layoutImpl;
    }

    /**
     * 播放节目列表
     */
    public void playProgramList(String methodName) {
        LogUtils.d(TAG, "playProgramList", "Play program now methodName : " + methodName);
        stopProgramList();
        //获取当天要播放的节目列表
        List<Program> todayProgramList = ProgramParseTools.getToDayProgramList();

        programSchedule = new ProgramSchedule(todayProgramList);
        //无节目播放
        if (null == todayProgramList || todayProgramList.isEmpty()) {
            LogUtils.d(TAG, "playProgramList", "programList is null");
            switchDefaultLayout(null);
        } else {
            //延时500ms 加载节目，避免异步添加节目到当前节目列表的逻辑顺序错误。
            programSchedule.loadDelayedNextProgram(500);
            LogUtils.d(TAG, "updateProgramList", "todayProgramList.size" + todayProgramList.size());
        }
    }

    /**
     * 更新节目列表
     */
    public void updateProgramList() {
        //获取当天要可播放的节目列表
        List<Program> programList = ProgramParseTools.getToDayProgramList();
        if (programSchedule == null || null == programList || programList.isEmpty()) {
            LogUtils.w(TAG, "updateProgramList", "programSchedule is null");
            if (programSchedule != null) {
                programSchedule.stopSchedule();
            }
            programSchedule = new ProgramSchedule(new ArrayList<Program>());
            switchDefaultLayout(null); // 当天没有节目可播放，播放垫片
            return;
        }
        LogUtils.d(TAG, "updateProgramList", "programList is size: " + programList.size());
        //当前的正在播放的节目，如果被删除则进行切换节目
        if (checkCurrentProgramIsRemove(programList)) {
            playProgramList("updateProgramList");
        } else {
            programSchedule.updateProgramList(programList);
        }
    }

    /**
     * 判断当前播放的节目是否被移除，如果是则切换掉当前的节目
     *
     * @param programList
     * @return true表示是
     */
    private boolean checkCurrentProgramIsRemove(List<Program> programList) {
        boolean isRemove = true;
        if (null == getCurrentProgram()) {
            isRemove = false;
            return isRemove;
        }
        for (Program program : programList) {
            if (null != program) {
                if (program.getKey().equals(getCurrentProgram().getKey())) {
                    isRemove = false;
                    break;
                }
            }
        }
        return isRemove;
    }

    public void clearPreProgram() {
        switchLayoutImpl.clearPreProgram();
    }

    /**
     * 加载下一个即将播放的节目
     */
    public void loadNextProgram() {
        LogUtils.d(TAG, "ProgramPlayManager", "loadNextProgram 播放下一个节目.");
        // 救援页面是否正在运行
        boolean rescueIsRun = AppUtil.isTopActivity(AppUtil.getTopTask(), "com.wconnet.eerp", "com.wconnet.eerp.activity.RescueActivity");
        if (!rescueIsRun) {
            VolumeUtil.setDeviceVolume(SharedUtil.newInstance().getString(UpdateVolumeReceive.CACHE_VOLUME));
        }
        Program currentProgram = programSchedule.getNextProgram();
        if (currentProgram == null) {
            Program upCommingProgram = programSchedule.getUpcomingProgram();//当前时间没有可播放的节目，播放垫片
            if (null != upCommingProgram) {
                switchDefaultLayout(StringUtil.getString(R.string.next_play_time) + upCommingProgram.getTs());
                // 启动定时器，到点播放的节目
                QuartzScheduler.getInstance().addSwitchProgramTask(upCommingProgram, new SwitchProgramTask());
                LogUtils.i(TAG, "loadNextProgram", "Next program play time: " + upCommingProgram.getTs());
            } else {
                // 当天所有节目均过期，播放垫片
                switchDefaultLayout(null);
                LogUtils.w(TAG, "loadNextProgram", "All programs is out of play time today, switch default layout.");
            }
        } else {
            if (checkProgramValidity(currentProgram)) {//校验节目是否合法，不合法则调用将这个节目暂时移除节目列表中,等素材下载完以后再次更新节目
                programErroMsg = null;
                sendProgramErrorMsg(false);
                playProgram(currentProgram);
                //记录当前正在播放的节目id
            } else {
                LogUtils.w(TAG, "loadNextProgram", "key: " + currentProgram.getKey() + " ,name: " + currentProgram.getN());
                programSchedule.removeProgramFromCurrentProgram(currentProgram);
                if (ListUitl.isEmpty(programSchedule.getCurrentPrograms()) && null != programErroMsg) {//当前没有节目播放的时候出现提示信息
                    StringBuffer stringBuffer = new StringBuffer(8);
                    stringBuffer.append("Eposter (loadNextProgram) Program is not validity program key is :");
                    stringBuffer.append(currentProgram.getKey());
                    stringBuffer.append(", program name is :");
                    stringBuffer.append(currentProgram.getN());
                    stringBuffer.append(",programErroMsg: ");
                    stringBuffer.append(programErroMsg);
                    stringBuffer.append(",_");
                    stringBuffer.append(DateTimeUtil.getStringTimeToFormat("yyyy_MM_dd_HH_mm_ss"));
                    LogManager.get().insertOperationMessage(stringBuffer.toString());
                    //发生错误提示信息到屏幕显示
                    sendProgramErrorMsg(true);
                }
                loadNextProgram();
            }
        }
    }

    private void sendProgramErrorMsg(boolean isShow) {
        Intent tipMsgIntent = new Intent(PlayActivity.UPDATE_MSG_TIP_MSG_ACTION);
        if (isShow) {
            tipMsgIntent.putExtra(PlayActivity.TIP_MSG_KEY, programErroMsg);
        }
        tipMsgIntent.putExtra(PlayActivity.TIP_MSG_IS_SHOW_KEY, isShow);
        LocalBroadcastManager.getInstance(EPosterApp
                .getApplication()
                .getApplicationContext()).sendBroadcast(tipMsgIntent);
    }

    private boolean checkProgramValidity(Program program) {
        boolean isValidity = true;
        List<Area> areaList = program.getAs();
        if (ListUitl.isEmpty(areaList)) {
            programErroMsg = StringUtil.getString(R.string.tip_area_is_null);
            isValidity = false;
            return isValidity;
        }
        List<String> unDownloadList = new ArrayList<String>();
        for (Area area : areaList) {
            if (null != area) {
                Integer areaType = area.getT();
                if (null != areaType) {
                    if (Constants.PIC_FRAGMENT == areaType
                            || Constants.VIDEO_FRAGMENT == areaType) {
                        List<Material> materialList = area.getMas();
//						if (ListUitl.isEmpty(materialList)) {
//							programErroMsg = StringUtil.getString(R.string.tip_area_file_is_null);
//							isValidity = false;
//							return isValidity;
//						}
                        if (ListUitl.isNotEmpty(materialList)) {
                            for (Material material : materialList) {
                                if (null != material) {
                                    Integer integer = material.getT();
                                    if (null != integer) {
                                        switch (integer) {
                                            case Constants.PIC_FRAGMENT:
                                                String filePath = FileStore.getInstance()
                                                        .getImageFilePath(FileStore.getFileName(material.getU()));
                                                File file = new File(filePath);
                                                if (!file.exists()) {
                                                    unDownloadList.add(material.getU());
                                                    isValidity = false;
                                                    LogUtils.d(TAG, "checkProgramValidity", "filePath : " + filePath);
                                                }
                                                break;
                                            case Constants.VIDEO_FRAGMENT:
                                                String videoFilePath = FileStore.getInstance()
                                                        .getVideoFilePath(FileStore.getFileName(material.getU()));
                                                File fileVideo = new File(videoFilePath);
                                                if (!fileVideo.exists()) {
                                                    unDownloadList.add(material.getU());
                                                    isValidity = false;
                                                    LogUtils.d(TAG, "checkProgramValidity", "videoFilePath : " + videoFilePath);
                                                }
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                } else {
                                    isValidity = false;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (ListUitl.isNotEmpty(unDownloadList)) {
            LogUtils.d(TAG, "checkProgramValidity", "unDownloadList size: " + unDownloadList.size());
            Helper.addDownload(unDownloadList, Constants.DOWNLOAD_FILE, program.getKey());
        }

        return isValidity;
    }


    /**
     * 开始播放节目
     *
     * @param program 要播放的节目
     */
    private void playProgram(Program program) {
        if (null == program) {
            LogUtils.e(TAG, "playProgram", "Program is null.");
            return;
        }
        try {
            Integer progarmW = program.getW();
            Integer programH = program.getH();
            if (null != progarmW && null != programH) {
                int w = progarmW.intValue();
                int h = programH.intValue();
                LayoutUtil.getInstance().BASE_WIDTH = Math.max(w, h);
                LayoutUtil.getInstance().BASE_HEIGHT = Math.min(w, h);
            } else {
                LayoutUtil.getInstance().BASE_WIDTH = 1280;
                LayoutUtil.getInstance().BASE_HEIGHT = 720;
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        LogUtils.d(TAG, "playProgram", "currentProgram: " + program.getKey());
        long playTime = ProgramParseTools.getProgramPlayTime(program) * 1000;
        if (playTime <= 0) {
            programSchedule.removeProgramFromCurrentProgram(program);
            LogUtils.d(TAG, "playProgram", "Error program play time " + program.getKey());
            loadNextProgram();
            return;
        }
        LogUtils.d(TAG, "playProgram", "Switch program after " + playTime + " ms.");
        QuartzScheduler.getInstance().addTimerSwitchProgramTask(program, playTime, new SwitchProgramTask());
        preLoadNextProgram();
        switchProgramLaytout(program);
    }

    /**
     * 切换默认界面
     */
    public void switchDefaultLayout(String alerMsg) {
        if (null == switchLayoutImpl) {
            LogUtils.e(TAG, "switchLayout", "switchLayoutImpl is null");
            return;
        }
        switchLayoutImpl.switchDefaultLayout(alerMsg);
    }

    /**
     * 停止节目列表
     */
    private void stopProgramList() {
        if (null != programSchedule) {
            programSchedule.stopSchedule();
            if (null != switchLayoutImpl) {
                switchLayoutImpl.clearPreProgram();
            }
        }
        LogUtils.e(TAG, "stopProgramList", "stopProgramList");
        programSchedule = null;
    }

    /**
     * 定时切换节目的线程
     */
    class SwitchProgramTask extends TimerTask {
        @Override
        public void run() {
            LogUtils.i(TAG, "switchProgramRun", "Switch next program.");
            loadNextProgram();
        }
    }

    /**
     * 切换节目界面
     *
     * @param program 节目对象
     */
    private void switchProgramLaytout(Program program) {
        if (null == switchLayoutImpl) {
            LogUtils.w(TAG, "switchLayout", "switchLayoutImpl is null");
            return;
        }
        LogUtils.d(TAG, "switchProgramLaytout", "program.getKey(): " + program.getKey());
        saveCurrentPlayProgram(program.getKey());
        currentProgram = program;
        switchLayoutImpl.switchProgramLayout(program);
    }

    private void saveCurrentPlayProgram(String prmKey) {
        SharedUtil.newInstance().setString(SharedUtil.KEY_CURRENT_PLAY_PRM, prmKey);
    }

    /**
     * 预加载下一个节目
     */
    public void preLoadNextProgram() {
        if (null != switchLayoutImpl && null != programSchedule) {
            switchLayoutImpl.preLoadProgram(programSchedule.getPreLoadNextProgram());
        }
    }

    /**
     * 获取当前正在播放的节目
     *
     * @return
     */
    public Program getCurrentProgram() {
        return currentProgram;
    }

    /**加载节目线程*/
//	class LoadNextDayProgramTask extends TimerTask{
//		@Override
//		public void run() {
//			LogUtils.i(TAG, "LoadNextDayProgramTask", "Load next day program after 1 min.");
//			Handler handler = new Handler(EPosterApp.getApplication().getMainLooper());
//			handler.postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					playProgramList();
//				}
//			}, 60 * 1000);
//		}
//	}
}

package com.unccr.zclh.dsdps.play;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.unccr.zclh.dsdps.R;
import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.models.Area;
import com.unccr.zclh.dsdps.models.Material;
import com.unccr.zclh.dsdps.models.Program;
import com.unccr.zclh.dsdps.models.Programme;
import com.unccr.zclh.dsdps.models.Resource;
import com.unccr.zclh.dsdps.play.interfaces.ISwitchLayoutListener;
import com.unccr.zclh.dsdps.util.Constants;
import com.unccr.zclh.dsdps.util.DateTimeUtil;
import com.unccr.zclh.dsdps.util.FileStore;
import com.unccr.zclh.dsdps.util.FileUtil;
import com.unccr.zclh.dsdps.util.LayoutUtil;
import com.unccr.zclh.dsdps.util.ListUtil;
import com.unccr.zclh.dsdps.util.ProgramParseTools;
import com.unccr.zclh.dsdps.util.SharedUtil;
import com.unccr.zclh.dsdps.util.StringUtil;
import com.unccr.zclh.dsdps.util.ThreadPoolManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

/**
 * @version 1.0
 * @data 创建时间：2019年10月09日 上午10:37:19
 * @parameter ProgramPlayManager
 */
public class ProgramPlayManager {
    private static final String TAG = ProgramPlayManager.class.getSimpleName();
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
    private ArrayList<String> playList;
    private boolean isPicProgramme = false;

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
     * 播放节目列表（电梯）
     */
    public void playProgrammeList(String methodName, Programme programme, boolean isAnfu) {
        Log.d(TAG, "playProgrammeList1 methodName: " + methodName);
        Log.d(TAG, "playProgrammeList1 SharedUtil: " + SharedUtil.newInstance().getString(SharedUtil.PROGRAMME_PLAY_OVER));
        ArrayList<String> programmeNameList = new ArrayList<>();
        if(isAnfu){
            switchLayoutImpl.switchPlayLayout(null, false,isAnfu);
        }else{
            if (programme == null || "over".equals(SharedUtil.newInstance().getString(SharedUtil.PROGRAMME_PLAY_OVER))) {
                Log.d(TAG, "playProgramList1 firstProgramList is null.切换到默认界面.");
                switchDefaultLayout(null);
                return;
            }
            for (Resource resource : programme.getResources()) {
                programmeNameList.add(resource.getName());
                Log.d(TAG, "playProgrammeList1 resource: " + programmeNameList.size());
            }
            if (checkProgrameTimeIfDuring(programme)) {
                playList = ProgramParseTools.getProgrammeList(programmeNameList);
                Log.d(TAG, "playProgrammeList1 methodName:3 " + playList.toString());
                for (String programmePath : playList) {
                    String suffix = FileUtil.getSuffix(programmePath).trim().toLowerCase();
                    if (FileUtil.getInstance().suffixToProgrammeFolder.containsKey(suffix)) {
                        isPicProgramme = true;
                        break;
                    } else {
                        isPicProgramme = false;
                    }
                }

                new Handler().postDelayed(() -> {
                    if (null == switchLayoutImpl || playList.isEmpty()) {
                        return;
                    }
//                Log.d(TAG,"playProgramList1 canPlay: " + SharedUtil.newInstance().getBoolean("canPlay"));
                    switchLayoutImpl.switchPlayLayout(playList, isPicProgramme,isAnfu);
                    Log.d(TAG, "playProgramList1 switchLayoutImpl----------> ");
                /*if (SharedUtil.newInstance().getBoolean("canPlay")) {
                    SharedUtil.newInstance().setBoolean("canPlay", false);
                    switchLayoutImpl.switchPlayLayout(playList, isPicProgramme);
                    Log.d(TAG, "playProgramList1 switchLayoutImpl----------> ");
                }*/
                }, 500);
            }
        }
    }

    public static boolean checkProgrameTimeIfDuring(Programme programme) {
        if (null == programme) {
            return true;
        }
        long startMillis = DateTimeUtil.getMillisTimeFromStringTime("yyyy-MM-dd HH:mm:ss", programme.getStartTime());
        long endMillis = DateTimeUtil.getMillisTimeFromStringTime("yyyy-MM-dd HH:mm:ss", programme.getEndTime());
        long currentMillis = DateTimeUtil.getCurrentMillisTime("yyyy-MM-dd HH:mm:ss");
        if (currentMillis >= startMillis && currentMillis <= endMillis) {//大于开始时间小于结束时间
            return true;
        }
        return false;
    }

    /**
     * 播放节目列表
     */
    public void playProgramList(String methodName) {
//        获取当前需要播放的节目图片或视频
        Log.d(TAG, "playProgramList methodName: " + methodName);
        stopProgramList();
        //获取当天要播放的节目列表
        List<Program> firstProgramList = getFirstProgramList();
        programSchedule = new ProgramSchedule(firstProgramList, null);
        //无节目播放
        if (null == firstProgramList || firstProgramList.isEmpty()) {
            Log.d(TAG, "playProgramList firstProgramList is null.切换到默认界面.");
            switchDefaultLayout(null);
        } else {
            //延时500ms 加载节目，避免异步添加节目到当前节目列表的逻辑顺序错误。
            programSchedule.loadDelayedNextProgram(500);
            Log.d(TAG, "playProgramList firstProgramList size: " + firstProgramList.size());
        }

        ThreadPoolManager.get().addRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    sendProgramToSecond();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取主屏当天要播放的节目
     *
     * @return
     */
    private List<Program> getFirstProgramList() {
        Log.d(TAG, "getFirstProgramList.");
        List<Program> todayProgramList = ProgramParseTools.getToDayProgramList();
        List<Program> firstProgramList = new ArrayList<>();
        for (Program program : todayProgramList) {
            if (program.getArea() == 1) {
                firstProgramList.add(program);
            }
        }
        return firstProgramList;
    }

    /**
     * 获取副屏当天要播放的节目
     *
     * @return
     */
    public static List<Program> getSecondProgramList() {
        List<Program> todayProgramList = ProgramParseTools.getToDayProgramList();
        List<Program> secondProgramList = new ArrayList<>();
        for (Program program : todayProgramList) {
            Log.d(TAG, "getSecondProgramList getArea: " + program.getArea());
            if (program.getArea() == 2) {
                secondProgramList.add(program);
            }
        }
        return secondProgramList;
    }

    /**
     * 更新节目列表
     */
    public void updateProgramList() {
        //获取当天要可播放的节目列表
        List<Program> firstProgramList = getFirstProgramList();
        sendProgramToSecond();
        if (programSchedule == null || null == firstProgramList || firstProgramList.isEmpty()) {
            Log.d(TAG, "updateProgramList programSchedule is null.");
            if (programSchedule != null) {
                programSchedule.stopSchedule();
            }
            programSchedule = new ProgramSchedule(new ArrayList<Program>(), null);
            switchDefaultLayout(null); // 当天没有节目可播放，播放垫片
            return;
        }
        Log.d(TAG, "updateProgramList firstProgramList is size: " + firstProgramList.size());
        //当前的正在播放的节目，如果被删除则进行切换节目
        if (checkCurrentProgramIsRemove(firstProgramList)) {
            playProgramList("updateProgramList");
        } else {
            programSchedule.updateProgramList(firstProgramList);
        }
    }

    private void sendProgramToSecond() {
        Intent intent = new Intent();
        intent.setAction(Constants.SECOND_SCREEN_ACTION);
        DsdpsApp.getDsdpsApp().sendBroadcast(intent);
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
        Program currentProgram = programSchedule.getNextProgram();  //根据时间刷选出没过期可播放的节目
        if (currentProgram == null) {
            Log.d(TAG, "loadNextProgram currentProgram is null.");
            Program upCommingProgram = programSchedule.getUpcomingProgram();//当前时间没有可播放的节目，播放垫片
            if (null != upCommingProgram) {
                switchDefaultLayout(StringUtil.getString(R.string.next_play_time) + upCommingProgram.getTs());
                // 启动定时器，到点播放的节目
                QuartzScheduler.getInstance().addSwitchProgramTask(upCommingProgram, new SwitchProgramTask());
            } else {
                // 当天所有节目均过期，播放垫片
                Log.d(TAG, "loadNextProgram upCommingProgram is null.");
                switchDefaultLayout(null);
            }
        } else {
            if (checkProgramValidity(currentProgram)) {//校验节目是否合法，不合法则调用将这个节目暂时移除节目列表中,
                // 等素材下载完以后再次更新节目
                programErroMsg = null;
                sendProgramErrorMsg(false);
                playProgram(currentProgram);
                //记录当前正在播放的节目id
            } else {
                programSchedule.removeProgramFromCurrentProgram(currentProgram);
                if (ListUtil.isEmpty(programSchedule.getCurrentPrograms()) && null != programErroMsg) {//当前没有节目播放的时候出现提示信息
                    StringBuffer stringBuffer = new StringBuffer(8);
                    stringBuffer.append("Eposter (loadNextProgram) Program is not validity program key is :");
                    stringBuffer.append(currentProgram.getKey());
                    stringBuffer.append(", program name is :");
                    stringBuffer.append(currentProgram.getArea());
                    stringBuffer.append(",programErroMsg: ");
                    stringBuffer.append(programErroMsg);
                    stringBuffer.append(",_");
                    stringBuffer.append(DateTimeUtil.getStringTimeToFormat("yyyy_MM_dd_HH_mm_ss"));
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
        LocalBroadcastManager.getInstance(DsdpsApp
                .getDsdpsApp()
                .getApplicationContext()).sendBroadcast(tipMsgIntent);
    }

    private boolean checkProgramValidity(Program program) {
        boolean isValidity = true;
        List<Area> areaList = program.getAs();
        if (ListUtil.isEmpty(areaList)) {
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
                        if (ListUtil.isNotEmpty(materialList)) {
                            for (Material material : materialList) {
                                if (null != material) {
                                    Integer integer = material.getT();
                                    if (null != integer) {
                                        switch (integer) {
                                            case Constants.PIC_FRAGMENT:
                                                String filePath = FileStore.getInstance()
                                                        .getImageFilePath(FileStore.getFileName(material.getU()));
                                                Log.d(TAG, "checkProgramValidity picFilePath: " + filePath);
                                                File file = new File(filePath);
                                                if (!file.exists()) {
                                                    Log.d(TAG, "checkProgramValidity pic file is not exists.");
                                                    unDownloadList.add(material.getU());
                                                    isValidity = false;
                                                }
                                                break;
                                            case Constants.VIDEO_FRAGMENT:
                                                String videoFilePath = FileStore.getInstance()
                                                        .getVideoFilePath(FileStore.getFileName(material.getU()));
                                                Log.d(TAG, "checkProgramValidity videoFilePath: " + videoFilePath);
                                                File fileVideo = new File(videoFilePath);
                                                Log.d(TAG, "fileVideo: " + fileVideo.getAbsolutePath());
//                                                if (!fileVideo.exists()) {
//                                                    Log.d(TAG, "checkProgramValidity video file is not exists.");
//                                                    unDownloadList.add(material.getU());
//                                                    isValidity = false;
//                                                }
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

        if (ListUtil.isNotEmpty(unDownloadList)) {
//            Helper.addDownload(unDownloadList, Constants.DOWNLOAD_FILE, program.getKey());
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
        int type = program.getAs().get(0).getT();
        long playTime;
        if (type == 4) {
            playTime = 1000 * 60 * 2;
        } else {
            playTime = ProgramParseTools.getProgramPlayTime(program) * 1000;
        }
        Log.d(TAG, "playProgram playTime: " + playTime);
        if (playTime <= 0) {
            programSchedule.removeProgramFromCurrentProgram(program);
            loadNextProgram();
            return;
        }
        QuartzScheduler.getInstance().addTimerSwitchProgramTask(program, playTime, new SwitchProgramTask());  //切换节目与节目
        preLoadNextProgram();
        switchProgramLaytout(program);  //切换节目内多图片和多视频
    }

    /**
     * 切换默认界面
     */
    public void switchDefaultLayout(String alerMsg) {
        if (null == switchLayoutImpl) {
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
        programSchedule = null;
    }

    /**
     * 定时切换节目的线程
     */
    class SwitchProgramTask extends TimerTask {
        @Override
        public void run() {
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
            return;
        }
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

}

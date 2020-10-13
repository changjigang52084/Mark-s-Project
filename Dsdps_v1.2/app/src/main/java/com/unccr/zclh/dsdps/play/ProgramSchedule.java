package com.unccr.zclh.dsdps.play;

import android.os.Handler;
import android.util.Log;

import com.unccr.zclh.dsdps.models.Program;
import com.unccr.zclh.dsdps.models.Programme;
import com.unccr.zclh.dsdps.util.DateTimeUtil;
import com.unccr.zclh.dsdps.util.ListUtil;
import com.unccr.zclh.dsdps.util.ProgramParseTools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @version 1.0
 * @data 创建时间：2019年10月09日 上午10:57:12
 * @parameter ProgramSchedule
 */
public class ProgramSchedule {
    private static final String TAG = "ProgramSchedule";

    private int programIndex = -1;

    private int intercutProgramIndex = -1;

    /**
     * *当天可播节目列表数量
     */
    private int programListSize = 0;
    /**
     * 当天可播放的节目列表
     */
    private List<Program> todayProgramList = null;

    /**
     * 当前播放的节目列表
     */
    private List<Program> currentProgramList = null;

    /**
     * 插播的节目列表
     */
    private List<Program> intercutProgramList = null;
    /**
     * handler 对象
     */
    private Handler mHandler = null;
    /**
     * 当前播放的节目
     */
    private Program currentProgram;

    public ProgramSchedule(List<Program> programList,List<String> playList) {
            mHandler = new Handler();
            todayProgramList = programList;
            currentProgramList = new ArrayList<Program>();
            intercutProgramList = new ArrayList<Program>();
            initSchedule();
    }

    /**
     * 初始化调度器
     */
    private void initSchedule() {
        if (null == todayProgramList || todayProgramList.isEmpty()) {
            Log.e(TAG, "initSchedule todayProgramList is null.");
            return;
        }
        programListSize = todayProgramList.size();
        Log.d(TAG, "initSchedule programListSize: " + programListSize);
        //调度器分配各个节目的调度任务，在指定的时间点把节目放入或者移出currentProgramList
        schedulePrmTask(todayProgramList);
    }

    /**
     * 调度节目任务
     *
     * @param programList 播放列表
     */
    private void schedulePrmTask(List<Program> programList) {
        if (null == programList || programList.size() == 0) {
            Log.e(TAG, "schedulePrmTask programList is null.");
            return;
        }
        for (Program program : programList) {
            if (null == program) {
                Log.e(TAG, "schedulePrmTask program is null.");
                continue;
            }
            //调度节目开始
            QuartzScheduler.getInstance().addStartPlayProgramTask(program, this);
            //调度节目结束
            QuartzScheduler.getInstance().addDelProgramTask(program, this);
            Log.d(TAG, "schedulePrmTask add new program key: " + program.getKey());
        }
    }

    /**
     * 调度节目任务(电梯)
     *
     * @param programList 播放列表
     */
    private void scheduleProgrammeTask(List<Program> programList) {
        if (null == programList || programList.size() == 0) {
            Log.e(TAG, "schedulePrmTask programList is null.");
            return;
        }
        for (Program program : programList) {
            if (null == program) {
                Log.e(TAG, "schedulePrmTask program is null.");
                continue;
            }
            //调度节目开始
            QuartzScheduler.getInstance().addStartPlayProgramTask(program, this);
            //调度节目结束
            QuartzScheduler.getInstance().addDelProgramTask(program, this);
            Log.d(TAG, "schedulePrmTask add new program key: " + program.getKey());
        }
    }


    /**
     * 更新正在播放的节目列表
     *
     * @param newPrmList 当天可播放的节目列表
     */
    public synchronized void updateProgramList(List<Program> newPrmList) {
        //TODO 判断空列表
        if (null == newPrmList || newPrmList.isEmpty()) {
            Log.e(TAG, "updateProgramList newPrmList is null.");
            return;
        }
        if (null == todayProgramList || todayProgramList.isEmpty()) {
            Log.e(TAG, "updateProgramList oldPrmList is null");
            scheduleTodayProgram(newPrmList);
            return;
        }
        Log.d(TAG, "updateProgramList newPrmList program size: " + newPrmList.size());
        List<Program> todayNewProgramList = new ArrayList<Program>(newPrmList);//保存当天最新的节目列表
        removeInvaildProgramList(screenInvaildProgramList(newPrmList));//筛选出无效的节目列表进行删除
        addNewProgramListToScheduleTask(newPrmList);//添加新的节目列表到调度任务列表中
        updateTodayProgramList(todayNewProgramList);//更新当天的节目列表
    }

    /**
     * 调度当天的节目列表到调度器
     *
     * @param newPrmList 当天最新的节目列表
     */
    private void scheduleTodayProgram(List<Program> newPrmList) {
        todayProgramList = newPrmList;
        schedulePrmTask(todayProgramList);
        //延时500ms 加载节目，避免异步添加节目到当前节目列表的逻辑顺序错误。
        loadDelayedNextProgram(500);
    }

    /**
     * 筛选无效的节目列表,并且将筛选出newPrmList中最新添加的节目列表
     *
     * @param newPrmList 新的节目列表
     * @return 返回无效的播放列表
     */
    private ArrayList<Program> screenInvaildProgramList(List<Program> newPrmList) {
        ArrayList<Program> tempCurrentPrmList = new ArrayList<Program>(todayProgramList);//临时保存新节目列表
        //查找相同的节目
        ArrayList<Program> repeatProgramList = new ArrayList<Program>();//重复的节目
        ArrayList<Program> repeatOldProgramList = new ArrayList<Program>();//重复的老节目
        for (Program newProgram : newPrmList) {//遍历新节目列表
            for (Program currentProgram : tempCurrentPrmList) {//当前节目列表
                if (newProgram.getKey().equals(currentProgram.getKey())) {
                    Log.d(TAG, "screenInvaildProgramList current program key: " + currentProgram.getKey());
                    repeatProgramList.add(newProgram);//保存重复的节目
                    repeatOldProgramList.add(currentProgram);//保存重复的节目
                }
            }
        }
        //删除新节目列表中和当前节目列表重复的节目
        newPrmList.removeAll(repeatProgramList);//移除掉与老节目重复的节目，剩余的就是新的节目
        tempCurrentPrmList.removeAll(repeatOldProgramList);
        return tempCurrentPrmList;
    }

    /**
     * 从当前的节目列表中移除无效的节目列表
     *
     * @param invaildProgramList 无效的节目列表
     */
    private void removeInvaildProgramList(ArrayList<Program> invaildProgramList) {
        //移除掉老节目中重复的节目，剩余的就是要删除的节目
        //已经筛选出新的节目列表 就是newPrmList.现在要得到旧节目列表中要删除的节目，其实就是重复节目列表之外的节目
        for (Program delProgram : invaildProgramList) {
            Log.d(TAG, "removeInvaildProgramList del program key: " + delProgram.getKey());
            cancelProgramTask(delProgram.getKey());
        }
        currentProgramList.removeAll(invaildProgramList);//当前的节目列表中移除
        intercutProgramList.removeAll(invaildProgramList);
    }

    /**
     * 添加新的节目列表到调度任务列表中
     *
     * @param newProgramList 新的节目列表
     */
    private void addNewProgramListToScheduleTask(List<Program> newProgramList) {
        //为新加进来的节目添加调度任务
        if (!newProgramList.isEmpty()) {
            schedulePrmTask(newProgramList);
        }
    }

    /**
     * 更新当天的节目列表
     *
     * @param todayNewProgramList 当天最新的节目列表
     */
    private void updateTodayProgramList(List<Program> todayNewProgramList) {
        //新节目列表覆盖旧节目列表
        todayProgramList = todayNewProgramList;
        //如果当前没有节目播放，提示即将播放
        if (getCurrentProgram() == null) {
            ProgramPlayManager.getInstance().loadNextProgram();
        }
    }


    /**
     * 取消节目定时任务
     *
     * @param prmKey 节目id
     */
    private void cancelProgramTask(String prmKey) {
        QuartzScheduler.getInstance().cancelAllTaskToTaskId(prmKey);
    }

    /**
     * 根据节目id,将节目从当前列表中移除。
     *
     * @param prmKeys 节目id
     */
    public void deleteProgramByKey(List<String> prmKeys) {
        delProgramToKeyAndList(prmKeys, intercutProgramList);
        delProgramToKeyAndList(prmKeys, currentProgramList);
        delProgramToKeyAndList(prmKeys, todayProgramList);

    }

    /**
     * 删除节目
     *
     * @param prmKeys
     * @param programList
     */
    private void delProgramToKeyAndList(List<String> prmKeys, List<Program> programList) {
        //移除programList中被移除的节目
        Iterator<Program> prmIterator = programList.iterator();
        while (prmIterator.hasNext()) {
            if (prmKeys.contains(prmIterator.next().getKey())) {
                prmIterator.remove();
            }
        }
        //移除定时器任务
        for (String delKey : prmKeys) {
            cancelProgramTask(delKey);
        }
    }

    /**
     * 停止调度器
     */
    public void stopSchedule() {
        QuartzScheduler.getInstance().stopTimerTask();
        clearAll();
    }


    /**
     * 获取下一个可播放的节目（在播放时间段内）
     */
    public Program getNextProgram() {
        if (currentProgramList == null || currentProgramList.isEmpty()) {
            Log.e(TAG, "getNextProgram current program list is null, switch default layout.");
            return null;
        }
        currentProgram = filterEffectiveProgram();
        return currentProgram;
    }

    /**
     * 筛选出当前需要播放的节目
     *
     * @return 当前需要播放的节目
     */
    private Program filterEffectiveProgram() {
        if (ListUtil.isEmpty(currentProgramList)) {
            programIndex = 0;
            return null;
        }
        //当前播放列表中有插播节目，优先播放。
        if (getIntercutProgramSize() > 0) {
            nextIntercutProgramIndex();
            Program intercutProgram = intercutProgramList.get(intercutProgramIndex);
            int retry = 0;
            while (ProgramParseTools.checkProgrameTimeIfOverdue(intercutProgram)) {
                retry++;
                if (retry >= getIntercutProgramSize()) {
                    intercutProgramIndex = -1;
                    intercutProgram = null;
                    break;
                }
                nextIntercutProgramIndex();
                intercutProgram = intercutProgramList.get(intercutProgramIndex);
            }
            if (null != intercutProgram) {
                return intercutProgram;
            }
        }
        //没有插播节目，播放普通节目
        nextProgramIndex();
        Program newProgram = currentProgramList.get(programIndex);
        //核对节目是否在播放时间段内newProgram
        int retry = 0;
        while (ProgramParseTools.checkProgrameTimeIfOverdue(newProgram)) {
            retry++;
            if (retry >= programListSize) {
                currentProgram = null;
                Log.d(TAG, "filterEffectiveProgram all programs is out of play time now, switch default layout.");
                return currentProgram;
            }
            nextProgramIndex();
            newProgram = currentProgramList.get(programIndex);
            Log.d(TAG, "filterEffectiveProgram program key: " + newProgram.getKey() + " ,todayProgramList size: " + todayProgramList.size());
        }

        return newProgram;
    }

    /**
     * 切换到下一个节目索引
     */
    private void nextProgramIndex() {
        programIndex++;
        if (getCurrentProgramSize() <= programIndex) {
            programIndex = 0;
        }
        Log.d(TAG, "nextProgramIndex programIndex: " + programIndex);
    }

    /**
     * 切换到下一个节目索引
     */
    private void nextIntercutProgramIndex() {
        intercutProgramIndex++;
        if (getIntercutProgramSize() <= intercutProgramIndex) {
            intercutProgramIndex = 0;
        }
        Log.d(TAG, "nextIntercutProgramIndex intercutProgramIndex: " + intercutProgramIndex);
    }

    /**
     * 获取即将播放的节目
     *
     * @return 节目对象 or null
     */
    public Program getUpcomingProgram() {
        if (null == todayProgramList || todayProgramList.isEmpty()) {
            Log.e(TAG,"getUpcomingProgram programList is empty.");
            return null;
        }
        long currentMillis = DateTimeUtil.getCurrentMillisTime(null);
        Log.d(TAG,"getUpcomingProgram: currentMillis: " + currentMillis);
        for (Program program : todayProgramList) {
            Log.d(TAG,"getUpcomingProgram ts: " + program.getTs() + " ,te: " + program.getTe());
            long startMillis = DateTimeUtil.getMillisTimeFromStringTime(null, program.getTs());
            Log.d(TAG,"getUpcomingProgram startMillis: " + startMillis);
            if (currentMillis <= startMillis) {
                Log.d(TAG,"getUpcomingProgram 到点了开始播放节目.");
                return program;
            }
        }
        return null;
    }

    /**
     * 获取预加载的下一个节目
     *
     * @return 返回下一个要播放的节目
     */
    public Program getPreLoadNextProgram() {
        int nextProgramIndex = programIndex + 1;
        if (getCurrentProgramSize() <= nextProgramIndex) {
            nextProgramIndex = 0;
        }
        if (null == currentProgramList || currentProgramList.isEmpty()) {
            return null;
        }
        return currentProgramList.get(nextProgramIndex);
    }

    /**
     * 获取当前正在播放的节目
     *
     * @return 返回正在播放的节目
     */
    public Program getCurrentProgram() {
        return currentProgram;
    }

    /**
     * 向当前在播放的节目列表中加入节目
     *
     * @param program
     * @return
     */
    public boolean addProgramToCurrentProgram(Program program) {
        if (null != currentProgramList && null != program) {
            if (program.getP() != null && program.getP() > 0) {
                intercutProgramList.add(program);
            }
            return currentProgramList.add(program);
        }
        return false;
    }


    /**
     * 向当前在播放的节目列表中移除节目
     *
     * @param program
     * @return
     */
    public boolean removeProgramFromCurrentProgram(Program program) {
        if (null != currentProgramList && null != program) {
            if (program.getP() != null && program.getP() > 0) {
                intercutProgramList.remove(program);
            }
            Log.e(TAG, "removeProgramFromCurrentProgram currentProgramList is remove currentProgram.");
            return currentProgramList.remove(program);
        }
        return false;
    }

    /**
     * 获取当前可播节目列表大小
     *
     * @return 返回当前可播节目列表的大小
     */
    public int getCurrentProgramSize() {
        return currentProgramList.size();
    }

    /**
     * 获取插播播节目列表大小
     *
     * @return 返回插播节目列表的大小
     */
    private int getIntercutProgramSize() {
        return intercutProgramList.size();
    }

    /**
     * 获取当前可播的节目列表
     *
     * @return 返回当前可播的节目列表
     */
    public List<Program> getCurrentPrograms() {
        return currentProgramList;
    }

    /**
     * 延时加载加下一个节目
     *
     * @param delayMillis 延时时间
     */
    public void loadDelayedNextProgram(int delayMillis) {
        delayedExecuteTask(false, delayMillis);
    }

    /**
     * 延时播放节目
     *
     * @param delayMillis 延时时间
     */
    public void loadDelayedPlayProgram(int delayMillis) {
        delayedExecuteTask(true, delayMillis);
    }

    /**
     * 延迟执行任务
     *
     * @param isPlayProgramTask true是播放任务,false是加载下一个节目任务
     * @param delayMillis       延迟时间(毫秒)
     */
    private void delayedExecuteTask(final boolean isPlayProgramTask, int delayMillis) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isPlayProgramTask) {
                    ProgramPlayManager.getInstance().playProgramList("delayedExecuteTask");
                } else {
                    ProgramPlayManager.getInstance().loadNextProgram();
                }
            }
        }, delayMillis);
    }

    /**
     * 清除所有任务
     */
    private void clearAll() {
        if (null != todayProgramList) {
            todayProgramList.clear();
        }

        if (null != currentProgramList) {
            currentProgramList.clear();
        }
        currentProgram = null;
        programIndex = -1;
        programListSize = 0;
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}

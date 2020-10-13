package com.unccr.zclh.dsdps.play;

import android.util.Log;

import com.unccr.zclh.dsdps.models.MessageEvent;
import com.unccr.zclh.dsdps.models.Program;
import com.unccr.zclh.dsdps.models.Programme;
import com.unccr.zclh.dsdps.util.DateTimeUtil;
import com.unccr.zclh.dsdps.util.ProgramParseTools;
import com.unccr.zclh.dsdps.util.SharedUtil;
import com.unccr.zclh.dsdps.util.StringUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @version 1.0
 * @data 创建时间：2019年10月09日 上午10:57:12
 * @parameter QuartzScheduler 定时任务调度类
 */
public class QuartzScheduler {

    private static final String TAG = "QuartzScheduler";

    /**
     * 定时任务对象
     **/
    private static volatile QuartzScheduler querScheduler = null;
    /**
     * 调度器集合
     */
    private HashMap<String, Timer> taskMap;

    private QuartzScheduler() {
        taskMap = null;
        taskMap = new HashMap<String, Timer>();
    }

    /**
     * 单例，获取QuartzScheduler对象
     *
     * @return QuartzScheduler.
     */
    public static QuartzScheduler getInstance() {
        if (null == querScheduler) {
            synchronized (QuartzScheduler.class) {
                if (null == querScheduler) {
                    querScheduler = new QuartzScheduler();
                }
            }
        }

        return querScheduler;
    }

    /**
     * 根据节目添加开始定时任务
     *
     * @param program         节目对象
     * @param programSchedule 节目调度器
     */
    public synchronized void addStartPlayProgramTask(Program program, ProgramSchedule programSchedule) {
        addJob(getStartTimerTaskId(program.getKey()), ProgramParseTools.getProgramStartTime(program), new AddPrmToPlaybill(program, programSchedule));
    }

    /**
     * 根据节目添加结束定时任务
     *
     * @param program         节目对象
     * @param programSchedule 节目调度器
     */
    public synchronized void addDelProgramTask(Program program, ProgramSchedule programSchedule) {
        addJob(getEndTimerTaskId(program.getKey()),
                ProgramParseTools.getProgramEndTime(program),
                new DelPrmToPlaybill(program, programSchedule));
    }

    /**
     * 根据节目添加切换节目定时任务 (定时节目，到点播放)
     *
     * @param program 节目对象
     * @param task    定时任务
     */
    public synchronized void addSwitchProgramTask(Program program, TimerTask task) {
        addJob(getSwitchTimerTaskId(program.getKey()), ProgramParseTools.getProgramStartTime(program), task);
    }

    /**
     * 根据节目添加定时切换节目任务 （切换节目与节目）
     *
     * @param program 节目对象
     * @param time    倒计时(毫秒)
     * @param task    定时任务
     */
    public synchronized void addTimerSwitchProgramTask(Program program, long time, TimerTask task) {
        addJobTimeLong(getCountdownTimerTaskId(program.getKey()), time, task);
    }


    /**
     * 添加一个定时任务
     *
     * @param taskId 定时器任务Id
     * @param time   时间设置，指定时间运行（format:2015-6-11 11:10:10）
     * @param task   定时执行的任务
     */
    private void addJob(String taskId, String time, TimerTask task) {
        if (null == taskMap || null == taskId || null == time || null == task) {
            return;
        }
        if (isDateOverdue(time)) {
            clearRepeatTaskToTaskId(taskId);//根据task id 去除重复的任务
            return;
        }
        addTimerTask(taskId, time, 0, task);
    }

    /**
     * 添加一个定时任务
     *
     * @param taskId  定时器任务Id
     * @param delayed 延迟多少毫秒
     * @param task    定时执行的任务
     */
    private void addJobTimeLong(String taskId, long delayed, TimerTask task) {
        if (null == taskMap || delayed < 0 || null == task || null == taskId) {
            return;
        }
        addTimerTask(taskId, null, delayed, task);
    }

    /**
     * 添加定时任务
     *
     * @param taskId  任务id
     * @param date    日期
     * @param delayed 延迟执行时间(毫秒)
     * @param task    定时任务
     */
    private void addTimerTask(String taskId, String date, long delayed, TimerTask task) {
        if (null == taskId || null == task) {
            return;
        }
        try {
            clearRepeatTaskToTaskId(taskId);//根据task id 去除重复的任务
            Timer timer = new Timer();
            if (null == date) {
                timer.schedule(task, delayed);
            } else {
                Log.d(TAG,"DATE: " + DateTimeUtil.getDateToStrDate(date));
                timer.schedule(task, DateTimeUtil.getDateToStrDate(date));
            }
            taskMap.put(taskId, timer);
        } catch (Exception e) {
        }
    }

    /**
     * 根据传入的日期判断是否过期
     *
     * @param date 日期
     * @return true表示过期, false表示未过期
     */
    private boolean isDateOverdue(String date) {
        long timeMillis = DateTimeUtil.getDateToStrDate(date).getTime();
        if (DateTimeUtil.uptimeMillis() > timeMillis) {
            return true;
        }
        return false;
    }

    /**
     * 根据task id 去除重复的任务
     *
     * @param taskId 任务id
     */
    private void clearRepeatTaskToTaskId(String taskId) {
        //防止重复添加任务
        if (taskMap.containsKey(taskId)) {
            cancelTaskById(taskId);
        }
    }

    /**
     * 停止所有关于task id 的定时任务
     *
     * @param taskId 任务id
     */
    public synchronized void cancelAllTaskToTaskId(String taskId) {
        cancelTaskById(getStartTimerTaskId(taskId));
        cancelTaskById(getEndTimerTaskId(taskId));
        cancelTaskById(getCountdownTimerTaskId(taskId));
        cancelTaskById(getSwitchTimerTaskId(taskId));
    }

    /**
     * 停止定时器任务
     *
     * @param taskId 任务id
     */
    private synchronized void cancelTaskById(String taskId) {
        if (StringUtil.isNullStr(taskId) || taskMap == null) {
            return;
        }
        cancelTimer(taskMap.get(taskId));
        taskMap.remove(taskId);
    }

    /**
     * 关闭调度器任务
     */
    public synchronized void stopTimerTask() {
        if (taskMap != null) {
            for (Map.Entry<String, Timer> taskEntry : taskMap.entrySet()) {
                cancelTimer(taskEntry.getValue());
            }
            taskMap.clear();
            querScheduler = null;
        }
    }

    /**
     * 取消定时器
     */
    private void cancelTimer(Timer timer) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * 获取节目开始定时器任务id
     *
     * @return
     */
    private String getStartTimerTaskId(String prmKey) {
        Log.d(TAG,"start_"+prmKey);
        return "start_" + prmKey;
    }

    /**
     * 获取节目结束定时器任务id
     *
     * @return
     */
    private String getEndTimerTaskId(String prmKey) {
        return "end_" + prmKey;
    }

    /**
     * 获取节目倒计时定时器任务id
     *
     * @return
     */
    private String getCountdownTimerTaskId(String prmKey) {
        return "countdown_" + prmKey;
    }

    /**
     * 获取节目切换定时器任务id
     *
     * @return
     */
    private String getSwitchTimerTaskId(String prmKey) {
        return "switch_" + prmKey;
    }

    /**
     * 添加节目到当前播放列表
     */
    class AddPrmToPlaybill extends TimerTask {
        private Program addPrm;
        private ProgramSchedule mProgramSchedule;

        public AddPrmToPlaybill(Program program, ProgramSchedule programSchedule) {
            addPrm = program;
            mProgramSchedule = programSchedule;
        }
        @Override
        public void run() {
            mProgramSchedule.addProgramToCurrentProgram(addPrm);
        }
    }
    /**
     * 从当前播放列表删除指定节目
     */
    class DelPrmToPlaybill extends TimerTask {
        private Program delPrm;
        private ProgramSchedule mProgramSchedule;

        public DelPrmToPlaybill(Program program, ProgramSchedule programSchedule) {
            delPrm = program;
            mProgramSchedule = programSchedule;
        }

        @Override
        public void run() {
            mProgramSchedule.removeProgramFromCurrentProgram(delPrm);
        }
    }


    /**
     * 根据节目添加开始定时任务（电梯）
     *
     * @param programme         节目对象
     */
    public synchronized void addStartPlayProgrammeTask(Programme programme) {
        addJob(getStartTimerTaskId(programme.getActionNum()), programme.getStartTime(),
                new AddPrpgrammeToPlaybill());
    }
    /**
     * 根据节目添加结束定时任务（电梯）
     *
     * @param programme         节目对象
     */
    public synchronized void addDelProgrammeTask(Programme programme) {
        addJob(getEndTimerTaskId(programme.getActionNum()),
                programme.getEndTime(),
                new DelProgrammeToPlaybill());
    }

    /**
     * 添加节目到当前播放列表（电梯）
     */
    class AddPrpgrammeToPlaybill extends TimerTask {
        @Override
        public void run() {
            EventBus.getDefault().post(new MessageEvent("节目开始"));
        }
    }

    /**
     * 把节目清空（电梯）
     */
    class DelProgrammeToPlaybill extends TimerTask {
        @Override
        public void run() {
            SharedUtil.newInstance().setString(SharedUtil.PROGRAMME_PLAY_OVER, "over");
            EventBus.getDefault().post(new MessageEvent("节目结束"));
        }
    }

}

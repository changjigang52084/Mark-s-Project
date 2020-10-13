package com.lzkj.aidlservice.util;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.WorkTimeInfo;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.WorkTimesInfo;
import com.lzkj.aidlservice.adapter.board.gw.GWPowerOnOffManager;
import com.lzkj.aidlservice.adapter.board.ly.LYPowerOnOffManager;
import com.lzkj.aidlservice.adapter.board.smdt.SMDTPowerOnOffManager;
import com.lzkj.aidlservice.adapter.board.sx.SXPowerOnOffGroup;
import com.lzkj.aidlservice.adapter.board.sx.SXPowerOnOffManager;
import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.manager.ReportDeviceStatusManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2015年7月3日 下午7:24:26
 * @parameter 同步工作时间
 */
public class WorkTimeUtil {

    private static final String TAG = "WorkTimeUtil";

    private static final int OPEN_WORKTIME = 1;
    private static final int CLOSE_WORKTIME = 0;
    private static volatile WorkTimeUtil ins;
    private boolean isWorkTime = false;
    private GWPowerOnOffManager gwPowerOnOffManager;

    private WorkTimeUtil() {
        gwPowerOnOffManager = new GWPowerOnOffManager();
    }

    public static WorkTimeUtil get() {
        if (null == ins) {
            synchronized (WorkTimeUtil.class) {
                if (null == ins) {
                    ins = new WorkTimeUtil();
                }
            }
        }
        return ins;
    }

    public void startLocalWorkTime() {
        String workTime = SharedUtil.newInstance().getString(SharedUtil.WROK_TIMER);
        if (!StringUtil.isNullStr(workTime)) {
            WorkTimesInfo workTimesInfo = JSON.parseObject(workTime, WorkTimesInfo.class);
            if (null != workTimesInfo) {
                checkWorkTimeSwitch(workTimesInfo);
            }
        }
    }

    /**
     * @return void    返回类型
     * @Description: TODO(更新一周的开关机时间到本地)
     * @Param @param adpressDataPackage    设定文件
     */
    public void updateWorkTime(WorkTimesInfo workTimesInfo) {
        try {
            Integer sts = workTimesInfo.getSts();
            String workTime = JSON.toJSONString(workTimesInfo);
            Log.d(TAG, "workTime: " + workTime);
            String cacheWorkTime = SharedUtil.newInstance().getString(SharedUtil.WROK_TIMER);
            if (StringUtil.isNullStr(cacheWorkTime) || !cacheWorkTime.equals(workTime)) {
                SharedUtil.newInstance().setString(SharedUtil.WROK_TIMER, workTime);
                checkWorkTimeSwitch(workTimesInfo);
            } else {
                if (!isWorkTime) {
                    checkWorkTimeSwitch(workTimesInfo);
                } else {
                    Log.d(TAG, "Already set work time.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 检测工作时间开关
     */
    private void checkWorkTimeSwitch(WorkTimesInfo workTimesInfo) {
        Integer sts = workTimesInfo.getSts();
        if (OPEN_WORKTIME == sts) {
            isWorkTime = true;
            setWorkTime();
        } else {
            //关闭开关机
            isWorkTime = false;
            Command.sendCmdAction(Constant.CLOSE_WORK_TIME, null);
            Log.d(TAG, "close workTime.");
        }
    }

    /**
     * 校验当前时间是否在休眠时间段内
     *
     * @return
     */
    public boolean checkCurrentTimeIsSleepTimeSlot() {
        boolean isSleepTimeSlot = false;
        //获取当天的周日期
        int week = Util.getWeek();
        WorkTimeInfo todayWorkTime = getWorkTimeByWeek(week);
        if (todayWorkTime == null) {
            Log.w(TAG, "checkCurrentTimeIsSleepTimeSlot todayWorkTime is null.");
            return isSleepTimeSlot;
        }
        switch (getStateOfWorkTime(todayWorkTime)) {
            case 0://在工作时间段内
                return isSleepTimeSlot;
            case 1://不在工作时间段内，还未到开机时间
                isSleepTimeSlot = true;
                return isSleepTimeSlot;
            case 2://不再工作时间段内，已过当天关机时间
                isSleepTimeSlot = true;
                return isSleepTimeSlot;
        }
        return isSleepTimeSlot;
    }

    /**
     * @return void    返回类型
     * @Description: TODO(设置开关机)
     * @Param @param workTime    设定文件
     */
    public void setWorkTime() {
        //获取当天的周日期
        int week = Util.getWeek();
        WorkTimeInfo todayWorkTime = getWorkTimeByWeek(week);
        if (todayWorkTime == null) {
            Log.w(TAG, "todayWorkTime is null");
            return;
        }
        todayWorkTime.setStart(getDefaultTime(todayWorkTime.getStart(), true));
        todayWorkTime.setEnd(getDefaultTime(todayWorkTime.getEnd(), false));
        cancelPowerOnOff(todayWorkTime.getStart(), todayWorkTime.getEnd());
        Log.d(TAG, "Today workTime [day: " + todayWorkTime.getDay() + " ,start: " + todayWorkTime.getStart() + " ,end：" + todayWorkTime.getEnd() + "]");
        int off = -1;
        int on = -1;
        //获取明天的开关机信息
        week++;
        WorkTimeInfo tomorrowWorkTime = getWorkTimeByWeek(week);
        if (tomorrowWorkTime == null) {
            Log.w(TAG, "tomorrowWorkTime is null");
            return;
        }
        String startTime = tomorrowWorkTime.getStart();
        String endTime = tomorrowWorkTime.getEnd();
        tomorrowWorkTime.setStart(getDefaultTime(startTime, true));
        tomorrowWorkTime.setEnd(getDefaultTime(endTime, false));
        //判断当前时间是否在工作时间段内，既是该开机，还是关机。
        switch (getStateOfWorkTime(todayWorkTime)) {
            case 0://在工作时间段内
                //获取明天的开机时间和今天的关机时间
                off = ((getTime(todayWorkTime.getEnd(), 0) / 1000) / 60);
                on = ((getTime(tomorrowWorkTime.getStart(), 1) / 1000) / 60) - off;
                setSxPowerOnOff(tomorrowWorkTime.getStart(), todayWorkTime.getEnd());
                break;
            case 1://不在工作时间段内，还未到开机时间
                //设置今天的开机时间，并关机
                on = ((getTime(todayWorkTime.getStart(), 0) / 1000) / 60);
                off = 0;
                break;
            case 2://不再工作时间段内，已过当天关机时间
                //设置明天的开机时间，并关机
                on = ((getTime(tomorrowWorkTime.getStart(), 1) / 1000) / 60);
                off = 0;
                break;
            default:
                break;
        }
        if (-1 != on && -1 != off) {
            Log.d(TAG, "device will on after " + on + " ,mins off: " + off + " mins.");
            if (off < 3) {
                String[] endHourMin = DateTimeUtil.getStringTimeToFormat("HH:mm").split(":");
                int hour = Integer.parseInt(endHourMin[0]);
                int min = Integer.parseInt(endHourMin[1]);
                int newOffMin = min + 6;
                Log.d(TAG, "newOffMin: " + newOffMin);
                if (newOffMin >= 60) {
                    hour = hour + 1;
                    min = newOffMin - 60;
                } else {
                    min = newOffMin;
                }
                String offHourMin = String.valueOf(hour) + ":" + String.valueOf(min);
                todayWorkTime.setEnd(offHourMin);
                Log.d(TAG, "offHourMin: " + offHourMin);
                setSxPowerOnOff(tomorrowWorkTime.getStart(), todayWorkTime.getEnd());
                off = 3;
            } else {
                //汇报状态为开机状态
                //发送给服务器待机状态
                ReportDeviceStatusManager.get().reportDeviceState(ReportDeviceStatusManager.DEVICE_STATE_STARTUP);
                //点亮屏幕
                if (LYPowerOnOffManager.get().isSeelp) {//如果是休眠状态下 则重启机器
                    Util.reboot();
                }
                Log.d(TAG, "state startup screen");
            }
            gwPowerOnOffManager.setOnOff(off, on);
        }
    }

    /**
     * 获取默认的开机和关机时间
     *
     * @param startTime
     * @param isStartTime 是否为开机时间
     * @return
     */
    private String getDefaultTime(String startTime, boolean isStartTime) {
        if (StringUtil.isNullStr(startTime)) {
            WorkTimeInfo workTimeInfo = getWorkTimeByWeek(0);
            if (null != workTimeInfo) {
                String defaultStartTime = isStartTime ? workTimeInfo.getStart() : workTimeInfo.getEnd();
                if (StringUtil.isNullStr(defaultStartTime)) {
                    startTime = isStartTime ? "00:00" : "23:59";
                } else {
                    startTime = defaultStartTime;
                }
            } else {
                startTime = isStartTime ? "00:00" : "23:59";
            }
        }
        return startTime;
    }

    /**
     * 设置国威的定时开关机
     *
     * @param off 单位分钟
     * @param on  单位分钟
     */
    private boolean setSMTPowerOnOf(int off, int on) {
        byte hour = 60;
        int offH = off / hour;
        int offM = off - (offH * hour);

        int onH = (on + off) / hour;
        int onM = (on + off) - (onH * hour);
        byte enable = 3;
        SharedUtil.newInstance().setInt(SharedUtil.ON_HOUR_KEY, onH);
        SharedUtil.newInstance().setInt(SharedUtil.ON_MINUTE_KEY, onM);

        SharedUtil.newInstance().setInt(SharedUtil.OFF_HOUR_KEY, offH);
        SharedUtil.newInstance().setInt(SharedUtil.OFF_MINUTE_KEY, offM);

        return SMDTPowerOnOffManager.setPowerOnOff((byte) offH, (byte) offM,
                (byte) onH, (byte) onM, enable);
    }

    /**
     * @return void    返回类型
     * @Title: getWorkTimeByWeek
     * @Description: TODO(获取指定周日期的开关机属性)
     * @Param @param week    指定周日期
     */
    private WorkTimeInfo getWorkTimeByWeek(int week) {
        Log.d(TAG, "Week day: " + week);
        if (week >= 8) {
            //星期天的明天是星期一
            week = 1;
        }
        //获取本地一周的开关机时间
        List<WorkTimeInfo> workTimes = getLocalWorkTime();
        if (workTimes == null || workTimes.size() == 0) {
            Log.w(TAG, "workTimeJson is null.");
            return null;
        }
        //（实行开关机周内日，0表示每天都按这个日期执行，6表示周六。）
        //遍历一周，获取当天的指定开关机时间
        for (WorkTimeInfo workTime : workTimes) {
            Log.d(TAG, "getDay: " + workTime.getDay());
            if (workTime.getDay() == week) {
                return workTime;
            }
        }
        //没有查找到指定日工作时间，即当天为默认工作时间
        for (WorkTimeInfo workTime : workTimes) {
            if (workTime.getDay() == 0) {
                return workTime;
            }
        }
        return null;
    }


    /**
     * @return void    返回类型
     * @Title: isWorkTime
     * @Description: TODO(判断当前时间是否在工作时间段内, 0:在工作时间段内 1：不在工作时间段内，还未到开机时间， 2：不再工作时间段内，已过当天关机时间)
     * @Param workTime 开关机对象
     */
    private int getStateOfWorkTime(WorkTimeInfo workTime) {
        int startTime = getIntToStringTime(workTime.getStart());//开机时间
        int endTime = getIntToStringTime(workTime.getEnd());//关机时间
        int currentTime = getIntToStringTime(getCurrentTimeHM());//当前的时间
        Log.d(TAG, "startTime: " + startTime + " ,endTime: " + endTime + " ,currentTime: " + currentTime);
        if (startTime >= endTime) {
            Log.e(TAG, "Error format: " + workTime.toString());
            return -1;
        }
        if (currentTime >= startTime && endTime >= currentTime) {
            Log.d(TAG, "in work time");
            return 0;
        } else {
            if (currentTime < startTime) {
                Log.d(TAG, "out of work time,1");
                return 1;
            } else {
                Log.d(TAG, "out of work time,2");
                return 2;
            }
        }
    }

    /**
     * 获取保存在本地的一周工作时间
     *
     * @return 返回null表示本地没有保存过工作时间
     */
    private static List<WorkTimeInfo> getLocalWorkTime() {
        String workTimeJson = SharedUtil.newInstance().getString(SharedUtil.WROK_TIMER);
        if (!StringUtil.isNullStr(workTimeJson)) {
            WorkTimesInfo workTimesInfo = JSON.parseObject(workTimeJson, WorkTimesInfo.class);
            List<WorkTimeInfo> workTimeInfo = workTimesInfo.getWts();
            return workTimeInfo;
        }
        return null;
    }

    /**
     * 时间转换成int类型
     *
     * @param time 例如:"22:00"，则返回2200
     * @return
     */
    private static int getIntToStringTime(String time) {
        int timeInt = Integer.parseInt(time.replaceAll(":", ""));
        return timeInt;
    }

    /**
     * 获取当前时间离关机时间,开机时间还剩多少毫秒
     *
     * @param time 关机时间,开机时间
     * @param day  关机为当天0,开机第二天1
     * @return 剩余多少毫秒开机, 关机
     */
    private static int getTime(String time, int day) {
        long shutdownLong = getMillisFromStringTime(time, day);
        return (int) (shutdownLong - System.currentTimeMillis());
    }

    /**
     * 获取当前的时分
     *
     * @return
     */
    public static String getCurrentTimeHM() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(new Date());
    }

    private static long getMillisFromStringTime(String time, int day) {
        Calendar cal = Calendar.getInstance();
        String[] timeArray = time.split(":"); // HH:MM
        int length = timeArray.length;
        if (length == 2) {
            if (!StringUtil.isNullStr(timeArray[0]) && !StringUtil.isNullStr(timeArray[1])) {
                int hour = Integer.parseInt(timeArray[0]);
                int minute = Integer.parseInt(timeArray[1]);
                cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH) + day, hour, minute, 0);
                return (long) (cal.getTimeInMillis() * 0.001) * 1000;
            }
        }
        return -1L;
    }

    /**
     * 设置视新定时开关机
     *
     * @param on  开机时间 HH:mm
     * @param off 关机时间 HH:mm
     */
    private void setSxPowerOnOff(String on, String off) {
        Log.d(TAG, "off: " + off);
        SXPowerOnOffGroup sXPowerOnOffGroup = new SXPowerOnOffGroup();

        String[] poweron = on.split(":");

        String[] poweroff = off.split(":");

        sXPowerOnOffGroup.mEnable = true;
        sXPowerOnOffGroup.mStartHour = Integer.parseInt(poweron[0]);
        sXPowerOnOffGroup.mStartMin = Integer.parseInt(poweron[1]);
        sXPowerOnOffGroup.mEndHour = Integer.parseInt(poweroff[0]);
        sXPowerOnOffGroup.mEndMin = Integer.parseInt(poweroff[1]);

        int[] timeonArray = new int[]{sXPowerOnOffGroup.mStartHour, sXPowerOnOffGroup.mStartMin};
        int[] timeoffArray = new int[]{sXPowerOnOffGroup.mEndHour, sXPowerOnOffGroup.mEndMin};

        Log.d(TAG, "RXWPowerOnOffManager");
        boolean isSuccess = SXPowerOnOffManager.setPowerTimePref(0, 0, sXPowerOnOffGroup, CommunicationApp.get());
        Log.d(TAG, "SXPowerOnOffManager");
        if (isSuccess) {
            Log.d(TAG, "power on off success");
        } else {
            Log.d(TAG, "power on off failed");
        }
    }

    private void cancelPowerOnOff(String on, String off) {
        String zero = "00:00";
        SXPowerOnOffGroup sXPowerOnOffGroup = new SXPowerOnOffGroup();
        if (zero.equals(on) && zero.equals(off)) {
            sXPowerOnOffGroup.mEnable = false;

            SMDTPowerOnOffManager.cancelPowerOnOff();
            SharedUtil.newInstance().removeKey(SharedUtil.WROK_TIMER);//清除保存在本地的开关机
            Log.d(TAG, "cancel power on off");
        }
        boolean isSuccess = SXPowerOnOffManager.setPowerTimePref(0, 0, sXPowerOnOffGroup, CommunicationApp.get());
        if (isSuccess) {
            Log.d(TAG, "cancel power on off success");
        } else {
            Log.d(TAG, "cancel power on off failed");
        }
    }

}

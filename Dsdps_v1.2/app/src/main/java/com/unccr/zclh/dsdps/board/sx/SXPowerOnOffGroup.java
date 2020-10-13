package com.unccr.zclh.dsdps.board.sx;

public class SXPowerOnOffGroup {
    public static final int MAX_GROUP_NUM = 5;
    public static final int DEFAULT_GROUP = 0;
    public static final int SUN_GROUP = 1;
    public static final int MON_GROUP = 2;
    public static final int TUE_GROUP = 3;
    public static final int WED_GROUP = 4;
    public static final int THU_GROUP = 5;
    public static final int FRI_GROUP = 6;
    public static final int SAT_GROUP = 7;
    /**是否启动开关机 true启动，false不启动*/
    public boolean mEnable;
    /**开机时间 （小时）**/
    public int mStartHour;
    /**开机时间 （分钟）**/
    public int mStartMin;
    /**关机时间 （小时）**/
    public int mEndHour;
    /**关机时间 （分钟）**/
    public int mEndMin;
}

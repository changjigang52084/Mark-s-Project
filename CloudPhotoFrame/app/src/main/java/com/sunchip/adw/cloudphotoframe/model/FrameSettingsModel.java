package com.sunchip.adw.cloudphotoframe.model;

public class FrameSettingsModel {
    private String name;
    private String location;
    /*显示相片数量*/
    private int alwaysShowRecent;
    private int sleepInterval;
    private String sleepIntervalStartTime;
    private String sleepIntervalEndTime;
    /*启动人体感应*/
    private int motion;
    private int sleepWhenNoMotionDetected;
    private int mostRecentTimeSpan;
    /*休眠显示时钟*/
    private int sleepClock;
    /*时区*/
    private int timezone;
    /*音量*/
    private int volume;
    /*亮度*/
    private int brightness;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getAlwaysShowRecent() {
        return alwaysShowRecent;
    }

    public void setAlwaysShowRecent(int alwaysShowRecent) {
        this.alwaysShowRecent = alwaysShowRecent;
    }

    public int getSleepInterval() {
        return sleepInterval;
    }

    public void setSleepInterval(int sleepInterval) {
        this.sleepInterval = sleepInterval;
    }

    public String getSleepIntervalStartTime() {
        return sleepIntervalStartTime;
    }

    public void setSleepIntervalStartTime(String sleepIntervalStartTime) {
        this.sleepIntervalStartTime = sleepIntervalStartTime;
    }

    public String getSleepIntervalEndTime() {
        return sleepIntervalEndTime;
    }

    public void setSleepIntervalEndTime(String sleepIntervalEndTime) {
        this.sleepIntervalEndTime = sleepIntervalEndTime;
    }

    public int getMotion() {
        return motion;
    }

    public void setMotion(int motion) {
        this.motion = motion;
    }

    public int getSleepWhenNoMotionDetected() {
        return sleepWhenNoMotionDetected;
    }

    public void setSleepWhenNoMotionDetected(int sleepWhenNoMotionDetected) {
        this.sleepWhenNoMotionDetected = sleepWhenNoMotionDetected;
    }

    public int getMostRecentTimeSpan() {
        return mostRecentTimeSpan;
    }

    public void setMostRecentTimeSpan(int mostRecentTimeSpan) {
        this.mostRecentTimeSpan = mostRecentTimeSpan;
    }

    public int getSleepClock() {
        return sleepClock;
    }

    public void setSleepClock(int sleepClock) {
        this.sleepClock = sleepClock;
    }

    public int getTimezone() {
        return timezone;
    }

    public void setTimezone(int timezone) {
        this.timezone = timezone;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public void setDefaultData(){
        this.alwaysShowRecent=1;
        this.sleepInterval=0;
        this.sleepIntervalStartTime="00:00";
        this.sleepIntervalEndTime="00:00";
        this.sleepWhenNoMotionDetected=1;
        this.motion=0;
        this.mostRecentTimeSpan=1;
        this.sleepClock=1;
        this.timezone=1;
        this.volume=0;
        this.brightness=30;
    }
}

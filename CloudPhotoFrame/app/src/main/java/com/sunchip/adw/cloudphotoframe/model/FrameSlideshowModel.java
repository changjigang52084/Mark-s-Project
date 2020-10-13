package com.sunchip.adw.cloudphotoframe.model;

public class FrameSlideshowModel {
    /*随机播放相片*/
    private int shuffle;
    /*时间显示格式*/
    private int clockSlideshow;
    /*显示描述*/
    private int captions;
    private int photoFit;
    /*切换特效*/
    private int transitionType;
    /*相片播放时间*/
    private int displayDuration;
    private int showAlert;

    public int getShuffle() {
        return shuffle;
    }

    public void setShuffle(int shuffle) {
        this.shuffle = shuffle;
    }

    public int getClockSlideshow() {
        return clockSlideshow;
    }

    public void setClockSlideshow(int clockSlideshow) {
        this.clockSlideshow = clockSlideshow;
    }

    public int getCaptions() {
        return captions;
    }

    public void setCaptions(int captions) {
        this.captions = captions;
    }

    public int getPhotoFit() {
        return photoFit;
    }

    public void setPhotoFit(int photoFit) {
        this.photoFit = photoFit;
    }

    public int getTransitionType() {
        return transitionType;
    }

    public void setTransitionType(int transitionType) {
        this.transitionType = transitionType;
    }

    public int getDisplayDuration() {
        return displayDuration;
    }

    public void setDisplayDuration(int displayDuration) {
        this.displayDuration = displayDuration;
    }

    public int getShowAlert() {
        return showAlert;
    }

    public void setShowAlert(int showAlert) {
        this.showAlert = showAlert;
    }

    public void setDefaultData(){
        this.shuffle=0;
        this.clockSlideshow=1;
        this.captions=0;
        this.photoFit=1;
        this.transitionType=1;
        this.displayDuration=1;
        this.showAlert=0;
    }
}

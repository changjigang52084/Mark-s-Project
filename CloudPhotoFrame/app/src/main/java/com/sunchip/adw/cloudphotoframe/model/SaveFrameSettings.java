package com.sunchip.adw.cloudphotoframe.model;

/**
 * Created by yingmuliang on 2020/1/8.
 */
public class SaveFrameSettings {


    private int frameId = 2;

    public int getFrameId() {
        return frameId;
    }

    public void setFrameId(int frameId) {
        this.frameId = frameId;
    }

    private FrameSettingsModel frameSettings;
    private FrameSlideshowModel frameSlideshow;

    public FrameSettingsModel getFrameSettings() {
        return frameSettings;
    }

    public void setFrameSettings(FrameSettingsModel frameSettings) {
        this.frameSettings = frameSettings;
    }

    public FrameSlideshowModel getFrameSlideshow() {
        return frameSlideshow;
    }

    public void setFrameSlideshow(FrameSlideshowModel frameSlideshow) {
        this.frameSlideshow = frameSlideshow;
    }
}

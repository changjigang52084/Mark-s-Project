package com.xunixianshi.vrshow.obj;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by markIron on 2016/9/23.
 */

public class PhoneVideoObj implements Serializable {

    private static final long serialVersionUID = 1L;
    public String videoId;
    public String videoPath;
    private Bitmap videoBitmap;
    public String videoTitle;
    public int videoSize;

    public PhoneVideoObj(String videoId, String videoPath, Bitmap videoBitmap, String videoTitle, int videoSize) {
        this.videoId = videoId;
        this.videoPath = videoPath;
        this.videoBitmap = videoBitmap;
        this.videoTitle = videoTitle;
        this.videoSize = videoSize;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public Bitmap getVideoBitmap() {
        return videoBitmap;
    }

    public void setVideoBitmap(Bitmap videoBitmap) {
        this.videoBitmap = videoBitmap;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public int getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(int videoSize) {
        this.videoSize = videoSize;
    }
}

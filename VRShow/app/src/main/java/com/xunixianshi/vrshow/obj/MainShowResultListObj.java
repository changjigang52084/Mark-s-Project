package com.xunixianshi.vrshow.obj;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/28.
 */

public class MainShowResultListObj implements Serializable {
    String showName;
    String showId;
    String showLevel;
    String showCoverImg;
    int resourceTotal;
    int resourceTotalDownload;
    int resourceTotalPlayer;
    int resourceTotalLikes;
    int resourceTotalComment;

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public String getShowLevel() {
        return showLevel;
    }

    public void setShowLevel(String showLevel) {
        this.showLevel = showLevel;
    }

    public String getShowCoverImg() {
        return showCoverImg;
    }

    public void setShowCoverImg(String showCoverImg) {
        this.showCoverImg = showCoverImg;
    }

    public int getResourceTotal() {
        return resourceTotal;
    }

    public void setResourceTotal(int resourceTotal) {
        this.resourceTotal = resourceTotal;
    }

    public int getResourceTotalDownload() {
        return resourceTotalDownload;
    }

    public void setResourceTotalDownload(int resourceTotalDownload) {
        this.resourceTotalDownload = resourceTotalDownload;
    }

    public int getResourceTotalPlayer() {
        return resourceTotalPlayer;
    }

    public void setResourceTotalPlayer(int resourceTotalPlayer) {
        this.resourceTotalPlayer = resourceTotalPlayer;
    }

    public int getResourceTotalLikes() {
        return resourceTotalLikes;
    }

    public void setResourceTotalLikes(int resourceTotalLikes) {
        this.resourceTotalLikes = resourceTotalLikes;
    }

    public int getResourceTotalComment() {
        return resourceTotalComment;
    }

    public void setResourceTotalComment(int resourceTotalComment) {
        this.resourceTotalComment = resourceTotalComment;
    }
}

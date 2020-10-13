package com.xunixianshi.vrshow.obj;

/**
 * Created by markIron on 2016/9/26.
 */

public class LeaveMessageResultList {

    private int commenterId;
    private String commenterName;
    private String commenterIcon;
    private String messageContent;
    private String createTime;
    private String messageIp;

    public int getCommenterId() {
        return commenterId;
    }

    public void setCommenterId(int commenterId) {
        this.commenterId = commenterId;
    }

    public String getCommenterName() {
        return commenterName;
    }

    public void setCommenterName(String commenterName) {
        this.commenterName = commenterName;
    }

    public String getCommenterIcon() {
        return commenterIcon;
    }

    public void setCommenterIcon(String commenterIcon) {
        this.commenterIcon = commenterIcon;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getMessageIp() {
        return messageIp;
    }

    public void setMessageIp(String messageIp) {
        this.messageIp = messageIp;
    }
}

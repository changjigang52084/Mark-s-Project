package com.unccr.zclh.dsdps.download.bean;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class DownloadBo implements Parcelable {
    private ArrayList<String> httpUrls;
    private String saveLocalFoldePath;
    private String prmId;
    private int type;
    public static final Creator<DownloadBo> CREATOR = new Creator() {
        public DownloadBo[] newArray(int size) {
            return new DownloadBo[size];
        }

        public DownloadBo createFromParcel(Parcel in) {
            return new DownloadBo(in);
        }
    };

    public DownloadBo() {
    }

    public ArrayList<String> getHttpUrls() {
        return this.httpUrls;
    }

    public void setHttpUrls(ArrayList<String> httpUrls) {
        this.httpUrls = httpUrls;
    }

    public String getSaveLocalFoldePath() {
        return this.saveLocalFoldePath;
    }

    public void setSaveLocalFoldePath(String saveLocalFoldePath) {
        this.saveLocalFoldePath = saveLocalFoldePath;
    }

    public String getPrmId() {
        return this.prmId;
    }

    public void setPrmId(String prmId) {
        this.prmId = prmId;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.saveLocalFoldePath);
        out.writeStringList(this.httpUrls);
        out.writeInt(this.type);
        out.writeString(this.prmId);
    }

    public DownloadBo(Parcel in) {
        this.saveLocalFoldePath = in.readString();
        this.httpUrls = new ArrayList();
        in.readStringList(this.httpUrls);
        this.type = in.readInt();
        this.prmId = in.readString();
    }
}

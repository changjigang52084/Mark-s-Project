package com.unccr.zclh.dsdps.download.bean;


import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;
/**
 * 恢复下载的bean
 * @author changjigang
 *
 */
public class RecoveryDownloadBean implements Parcelable{
    private int type;
    private ArrayList<String> list;
    private String saveFoldePath;
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public ArrayList<String> getList() {
        return list;
    }
    public void setList(ArrayList<String> list) {
        this.list = list;
    }
    public String getSaveFoldePath() {
        return saveFoldePath;
    }
    public void setSaveFoldePath(String saveFoldePath) {
        this.saveFoldePath = saveFoldePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(type);
        out.writeString(saveFoldePath);
        out.writeStringList(list);
    }

    public static final Creator<RecoveryDownloadBean> CREATOR = new Creator<RecoveryDownloadBean>() {
        @Override
        public RecoveryDownloadBean[] newArray(int size) {
            return new RecoveryDownloadBean[size];
        }

        @Override
        public RecoveryDownloadBean createFromParcel(Parcel in) {
            return new RecoveryDownloadBean(in);
        }
    };
    public RecoveryDownloadBean() {}
    public RecoveryDownloadBean(Parcel in) {
        type = in.readInt();
        saveFoldePath = in.readString();
        list = new ArrayList<String>();
        in.readStringList(list);
    }

}

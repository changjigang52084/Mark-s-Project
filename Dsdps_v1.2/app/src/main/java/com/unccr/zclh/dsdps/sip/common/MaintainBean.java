package com.unccr.zclh.dsdps.sip.common;

import java.util.List;

public class MaintainBean {


    /**
     * code : 200
     * message : 操作成功
     * data : {"data":["879ab0688c7144b7a9922af6e3e17668.mp4"],"deviceID":"YC000000000000F"}
     */

    private String code;
    private String message;
    private DataBean data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * data : ["879ab0688c7144b7a9922af6e3e17668.mp4"]
         * deviceID : YC000000000000F
         */

        private String deviceID;
        private List<String> data;

        public String getDeviceID() {
            return deviceID;
        }

        public void setDeviceID(String deviceID) {
            this.deviceID = deviceID;
        }

        public List<String> getData() {
            return data;
        }

        public void setData(List<String> data) {
            this.data = data;
        }
    }
}

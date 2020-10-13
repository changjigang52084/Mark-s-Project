package com.sunchip.adw.cloudphotoframe.http.request.Bean.Event;

/**
 * Created by yingmuliang on 2020/1/8.
 */
public class frameInfoEvent {
    //      "insert_id":null,
//              "id":3004,
//              "name":"测",
//              "location":"china",
//              "serial":1000000000000001,
//              "mac":"20:18:0E:17:DE:90",
//              "ip":"192.168.43.221",
//              "model":null,
//              "userId":8,
//              "spaceTotal":4955549696,
//              "spaceFree":4166283264,
//              "softwareVersion":"1.0",
//              "hardwareVersion":null,
//              "active_time":"2020-01-08T01:17:06.000+0000",
//              "paired_time":"2020-01-08T02:20:01.000+0000"
    private int id = 0;
    private String name;
    private String location;
    private long serial;
    private String mac;
    private String ip;
    private int userId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public long getSerial() {
        return serial;
    }

    public void setSerial(long serial) {
        this.serial = serial;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}

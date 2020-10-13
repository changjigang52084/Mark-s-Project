package com.unccr.zclh.dsdps.models;

public class ReportDeviceInfo {
    private String device_id;
    private String device_ip;
    private String screen;
    private Long memory;
    private Long disk_space_total;
    private Long disk_space_use;
    private String firmware_version;
    private String app_version;

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getDevice_ip() {
        return device_ip;
    }

    public void setDevice_ip(String device_ip) {
        this.device_ip = device_ip;
    }

    public String getScreen() {
        return screen;
    }

    public void setScreen(String screen) {
        this.screen = screen;
    }

    public Long getMemory() {
        return memory;
    }

    public void setMemory(Long memory) {
        this.memory = memory;
    }

    public Long getDisk_space_total() {
        return disk_space_total;
    }

    public void setDisk_space_total(Long disk_space_total) {
        this.disk_space_total = disk_space_total;
    }

    public Long getDisk_space_use() {
        return disk_space_use;
    }

    public void setDisk_space_use(Long disk_space_use) {
        this.disk_space_use = disk_space_use;
    }

    public String getFirmware_version() {
        return firmware_version;
    }

    public void setFirmware_version(String firmware_version) {
        this.firmware_version = firmware_version;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

}


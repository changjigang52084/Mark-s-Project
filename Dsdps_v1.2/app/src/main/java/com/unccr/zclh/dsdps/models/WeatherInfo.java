package com.unccr.zclh.dsdps.models;

public class WeatherInfo {

    private String temperature;
    private String weatherInfo;
    private String wid;

    public WeatherInfo(String temperature, String weatherInfo, String wid) {
        this.temperature = temperature;
        this.weatherInfo = weatherInfo;
        this.wid = wid;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWeatherInfo() {
        return weatherInfo;
    }

    public void setWeatherInfo(String weatherInfo) {
        this.weatherInfo = weatherInfo;
    }

    public String getWid() {
        return wid;
    }

    public void setWid(String wid) {
        this.wid = wid;
    }
}

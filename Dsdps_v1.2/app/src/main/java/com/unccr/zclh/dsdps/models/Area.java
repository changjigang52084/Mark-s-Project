package com.unccr.zclh.dsdps.models;

import java.util.List;

public class Area {

    private Long key;
    private Integer t;
    private Integer x;
    private Integer y;
    private Integer z;
    private Integer w;
    private Integer h;
    private String u;
    private Marquee m;
    private List<Material> mas;

    public Long getKey() {
        return key;
    }

    public void setKey(Long key) {
        this.key = key;
    }

    public Integer getT() {
        return t;
    }

    public void setT(Integer t) {
        this.t = t;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getZ() {
        return z;
    }

    public void setZ(Integer z) {
        this.z = z;
    }

    public Integer getW() {
        return w;
    }

    public void setW(Integer w) {
        this.w = w;
    }

    public Integer getH() {
        return h;
    }

    public void setH(Integer h) {
        this.h = h;
    }

    public String getU() {
        return u;
    }

    public void setU(String u) {
        this.u = u;
    }

    public Marquee getM() {
        return m;
    }

    public void setM(Marquee m) {
        this.m = m;
    }

    public List<Material> getMas() {
        return mas;
    }

    public void setMas(List<Material> mas) {
        this.mas = mas;
    }

    @Override
    public String toString() {
        return "Area{" +
                "key=" + key +
                ", t=" + t +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", w=" + w +
                ", h=" + h +
                ", u='" + u + '\'' +
                ", m=" + m +
                ", mas=" + mas +
                '}';
    }
}

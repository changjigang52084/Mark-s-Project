package com.unccr.zclh.dsdps.models;

import java.util.List;

/**
 * @author  jigangchang Email:changjigang@sunchip.com
 * @version 1.0
 * @data 创建时间：2019年10月08日 下午5:45:31
 * @parameter Program
 */
public class Program {

    private String key;
    private int area;
    private Integer p;
    private Integer t;
    private Integer d;
    private Integer w;
    private Integer h;
    private Long ds;
    private Long de;
    private String ts;
    private String te;
    private Integer i;
    private String bc;
    private Material bi;
    private Material bgm;
    private List<Area> as;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public Integer getP() {
        return p;
    }

    public void setP(Integer p) {
        this.p = p;
    }

    public Integer getT() {
        return t;
    }

    public void setT(Integer t) {
        this.t = t;
    }

    public Integer getD() {
        return d;
    }

    public void setD(Integer d) {
        this.d = d;
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

    public Long getDs() {
        return ds;
    }

    public void setDs(Long ds) {
        this.ds = ds;
    }

    public Long getDe() {
        return de;
    }

    public void setDe(Long de) {
        this.de = de;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    public String getTe() {
        return te;
    }

    public void setTe(String te) {
        this.te = te;
    }

    public Integer getI() {
        return i;
    }

    public void setI(Integer i) {
        this.i = i;
    }

    public String getBc() {
        return bc;
    }

    public void setBc(String bc) {
        this.bc = bc;
    }

    public Material getBi() {
        return bi;
    }

    public void setBi(Material bi) {
        this.bi = bi;
    }

    public Material getBgm() {
        return bgm;
    }

    public void setBgm(Material bgm) {
        this.bgm = bgm;
    }

    public List<Area> getAs() {
        return as;
    }

    public void setAs(List<Area> as) {
        this.as = as;
    }
}

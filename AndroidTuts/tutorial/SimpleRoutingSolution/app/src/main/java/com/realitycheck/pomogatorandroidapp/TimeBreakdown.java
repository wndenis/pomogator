package com.realitycheck.pomogatorandroidapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TimeBreakdown {

    @SerializedName("driving")
    @Expose
    private Integer driving;
    @SerializedName("rest")
    @Expose
    private Integer rest;
    @SerializedName("service")
    @Expose
    private Integer service;
    @SerializedName("waiting")
    @Expose
    private Integer waiting;

    public Integer getDriving() {
        return driving;
    }

    public void setDriving(Integer driving) {
        this.driving = driving;
    }

    public Integer getRest() {
        return rest;
    }

    public void setRest(Integer rest) {
        this.rest = rest;
    }

    public Integer getService() {
        return service;
    }

    public void setService(Integer service) {
        this.service = service;
    }

    public Integer getWaiting() {
        return waiting;
    }

    public void setWaiting(Integer waiting) {
        this.waiting = waiting;
    }

}
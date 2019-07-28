package com.realitycheck.pomogatorandroidapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Interconnection {

    @SerializedName("distance")
    @Expose
    private Integer distance;
    @SerializedName("fromWaypoint")
    @Expose
    private String fromWaypoint;
    @SerializedName("rest")
    @Expose
    private Integer rest;
    @SerializedName("time")
    @Expose
    private Integer time;
    @SerializedName("toWaypoint")
    @Expose
    private String toWaypoint;
    @SerializedName("waiting")
    @Expose
    private Integer waiting;

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public String getFromWaypoint() {
        return fromWaypoint;
    }

    public void setFromWaypoint(String fromWaypoint) {
        this.fromWaypoint = fromWaypoint;
    }

    public Integer getRest() {
        return rest;
    }

    public void setRest(Integer rest) {
        this.rest = rest;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getToWaypoint() {
        return toWaypoint;
    }

    public void setToWaypoint(String toWaypoint) {
        this.toWaypoint = toWaypoint;
    }

    public Integer getWaiting() {
        return waiting;
    }

    public void setWaiting(Integer waiting) {
        this.waiting = waiting;
    }

}
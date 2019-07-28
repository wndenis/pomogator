package com.realitycheck.pomogatorandroidapp;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {

    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("interconnections")
    @Expose
    private List<Interconnection> interconnections = null;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("timeBreakdown")
    @Expose
    private TimeBreakdown timeBreakdown;
    @SerializedName("waypoints")
    @Expose
    private List<Waypoint> waypoints = null;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public List<Interconnection> getInterconnections() {
        return interconnections;
    }

    public void setInterconnections(List<Interconnection> interconnections) {
        this.interconnections = interconnections;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public TimeBreakdown getTimeBreakdown() {
        return timeBreakdown;
    }

    public void setTimeBreakdown(TimeBreakdown timeBreakdown) {
        this.timeBreakdown = timeBreakdown;
    }

    public List<Waypoint> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(List<Waypoint> waypoints) {
        this.waypoints = waypoints;
    }

}
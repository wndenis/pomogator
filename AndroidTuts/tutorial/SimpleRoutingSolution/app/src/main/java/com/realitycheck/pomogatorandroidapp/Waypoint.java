package com.realitycheck.pomogatorandroidapp;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Waypoint {

    @SerializedName("estimatedArrival")
    @Expose
    private String estimatedArrival;
    @SerializedName("estimatedDeparture")
    @Expose
    private Object estimatedDeparture;
    @SerializedName("fulfilledConstraints")
    @Expose
    private List<Object> fulfilledConstraints = null;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lng")
    @Expose
    private Double lng;
    @SerializedName("sequence")
    @Expose
    private Integer sequence;

    public String getEstimatedArrival() {
        return estimatedArrival;
    }

    public void setEstimatedArrival(String estimatedArrival) {
        this.estimatedArrival = estimatedArrival;
    }

    public Object getEstimatedDeparture() {
        return estimatedDeparture;
    }

    public void setEstimatedDeparture(Object estimatedDeparture) {
        this.estimatedDeparture = estimatedDeparture;
    }

    public List<Object> getFulfilledConstraints() {
        return fulfilledConstraints;
    }

    public void setFulfilledConstraints(List<Object> fulfilledConstraints) {
        this.fulfilledConstraints = fulfilledConstraints;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Integer getSequence() {
        return sequence;
    }

    public void setSequence(Integer sequence) {
        this.sequence = sequence;
    }

}
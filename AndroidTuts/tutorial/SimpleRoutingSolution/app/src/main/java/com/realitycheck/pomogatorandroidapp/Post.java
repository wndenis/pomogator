package com.realitycheck.pomogatorandroidapp;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Post {

    @SerializedName("errors")
    @Expose
    private List<Object> errors = null;
    @SerializedName("processingTimeDesc")
    @Expose
    private String processingTimeDesc;
    @SerializedName("requestId")
    @Expose
    private Object requestId;
    @SerializedName("responseCode")
    @Expose
    private String responseCode;
    @SerializedName("results")
    @Expose
    private List<Result> results = null;
    @SerializedName("warnings")
    @Expose
    private Object warnings;

    public List<Object> getErrors() {
        return errors;
    }

    public void setErrors(List<Object> errors) {
        this.errors = errors;
    }

    public String getProcessingTimeDesc() {
        return processingTimeDesc;
    }

    public void setProcessingTimeDesc(String processingTimeDesc) {
        this.processingTimeDesc = processingTimeDesc;
    }

    public Object getRequestId() {
        return requestId;
    }

    public void setRequestId(Object requestId) {
        this.requestId = requestId;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public Object getWarnings() {
        return warnings;
    }

    public void setWarnings(Object warnings) {
        this.warnings = warnings;
    }

}
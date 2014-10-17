package com.erigir.chirp.exception;

/**
 * Created by chrweiss on 7/6/14.
 */
public class ChirpErrorData {
    private int httpStatusCode;
    private int detailCode;
    private String message;
    private String developerMessage;
    private String moreInfoUrl;
    private Object details;

    public ChirpErrorData() {
    }

    public ChirpErrorData(int httpStatusCode, int detailCode, String message, String developerMessage, String moreInfoUrl, Object details) {
        this.httpStatusCode = httpStatusCode;
        this.detailCode = detailCode;
        this.message = message;
        this.developerMessage = developerMessage;
        this.moreInfoUrl = moreInfoUrl;
        this.details = details;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public int getDetailCode() {
        return detailCode;
    }

    public void setDetailCode(int detailCode) {
        this.detailCode = detailCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = developerMessage;
    }

    public String getMoreInfoUrl() {
        return moreInfoUrl;
    }

    public void setMoreInfoUrl(String moreInfoUrl) {
        this.moreInfoUrl = moreInfoUrl;
    }

    public Object getDetails() {
        return details;
    }

    public void setDetails(Object details) {
        this.details = details;
    }
}

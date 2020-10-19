package com.expedia.util.error;

public class UnsupportedOperationException extends Exception {
    private String httpRequestType;

    public UnsupportedOperationException(String requestType) {
        this.httpRequestType = requestType;
    }

    public String getHttpRequestType() {
        return this.httpRequestType;
    }
}

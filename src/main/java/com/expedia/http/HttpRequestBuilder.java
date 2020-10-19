package com.expedia.http;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.message.BasicHeader;

import java.util.List;

public class HttpRequestBuilder {
    private HttpEntityEnclosingRequestBase request;

    public HttpRequestBuilder() {
    }

    public HttpRequestBuilder initialize(HttpEntityEnclosingRequestBase request) {
        this.request = request;
        return this;
    }

    public HttpRequestBuilder withHeaders(List<BasicHeader> headers) {
        headers.forEach((header) -> this.request.setHeader(header));
        return this;
    }

    public HttpRequestBuilder withEntity(HttpEntity entity) {
        this.request.setEntity(entity);
        return this;
    }

    public HttpEntityEnclosingRequestBase build() {
        return this.request;
    }

}

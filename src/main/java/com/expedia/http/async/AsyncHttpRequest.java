package com.expedia.http.async;

import com.expedia.http.HttpRequestBuilder;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AsyncHttpRequest {

    public AsyncHttpRequest() {
    }

    private static String combineUrlAndParameters(String url, Map<String, String> params) {
        String urlWithParams = url + "?";
        List<String> urlList = new ArrayList<String>();
        final StringBuilder builder = new StringBuilder();

        params.forEach((param1, param2) -> builder.append("&").append(param1).append("=").append(param2));
        return builder.toString();
    }

    public static HttpRequestBuilder build(String httpRequestType, String url, Map<String, String> params) {
        HttpRequestBuilder request = new HttpRequestBuilder();

        switch (httpRequestType) {
            case "post":
                request.initialize(new HttpPost(AsyncHttpRequest.combineUrlAndParameters(url, params)));
                return request;
            case "put":
                request.initialize(new HttpPut(AsyncHttpRequest.combineUrlAndParameters(url, params)));
                return request;
            default:
                throw new UnsupportedOperationException(httpRequestType);
        }
    }

    public static HttpRequestBuilder build(String httpRequestType, String url) {
        HttpRequestBuilder request = new HttpRequestBuilder();

        switch (httpRequestType) {
            case "post":
                request.initialize(new HttpPost(url));
                return request;
            case "put":
                request.initialize(new HttpPut(url));
                return request;
            default:
                throw new UnsupportedOperationException(httpRequestType);
        }
    }

}

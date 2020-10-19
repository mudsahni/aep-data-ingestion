package com.expedia.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpResponseException;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;

public class Helper {
    public static String parseResponse(HttpResponse response, int status, String key) throws IOException {
        if (response.getStatusLine().getStatusCode() == status) {
            return new JSONObject(EntityUtils.toString(response.getEntity())).getString(key);
        } else {
            throw new HttpResponseException(response.getStatusLine().getStatusCode(),
                    response.getStatusLine().getReasonPhrase());
        }

    }

    public static void parseResponse(HttpResponse response, int status) throws HttpResponseException {
        if (response.getStatusLine().getStatusCode() == status) {

            System.out.println(response.getStatusLine().getStatusCode());

        } else {
            throw new HttpResponseException(response.getStatusLine().getStatusCode(),
                    response.getStatusLine().getReasonPhrase());
        }

    }

    public static boolean needsPrecedingZero(int x) {
        return x <= 9;
    }

}

package com.expedia.aep;

import com.expedia.http.async.AsyncHttpRequest;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class AEPDIBase {

    private String clientId;
    private String orgId;
    private final String DATA_INGESTION_URL = "https://platform.adobe.io/data/foundation/import/batches";
    private String accessToken = "";

    public AEPDIBase(String clientId, String orgId, String accessToken) {
        this.clientId = clientId;
        this.orgId = orgId;
        this.accessToken = accessToken;
    }

    private List<BasicHeader> getAEPDefaultHeaders() {
        /*
            Function to create a list of default headers required for every AEP API transaction.
         */
        List<BasicHeader> defaultHeaders = new ArrayList<BasicHeader>();
        defaultHeaders.add(new BasicHeader("x-api-key", this.clientId));
        defaultHeaders.add(new BasicHeader("x-gw-ims-org-id", this.orgId));
        defaultHeaders.add(new BasicHeader("Authorization", "Bearer " + this.accessToken));
        return defaultHeaders;
    }

    public HttpEntityEnclosingRequestBase createBatchRequest(String datasetId, String format)
            throws UnsupportedEncodingException {
        /*
            Function to return a `createBatch` async http request for AEP data ingestion api.
         */
        // create a new json object
        JSONObject body = new JSONObject();
        // put datasetid as a parameter in the body
        body.put("datasetId", datasetId);
        // put inputformat as a parameter in the body
        body.put("inputFormat", new JSONObject().put("format", format));
        // generate default headers
        List<BasicHeader> headers = this.getAEPDefaultHeaders();
        // add custom headers for create batch request to headers object
        headers.add(new BasicHeader("Content-Type", "application/json"));
        headers.add(new BasicHeader("Accept", "application/json"));

        return AsyncHttpRequest.build("post", this.DATA_INGESTION_URL)
                .withHeaders(headers).withEntity(new StringEntity(body.toString())).build();
    }

    public HttpEntityEnclosingRequestBase uploadDataRequest(String datasetId, String batchId,
                                                            String fileName, InputStream inputStream) {
        /*
            Function to return an `uploadData` async http request for AEP API.
         */

        // generate default headers
        List<BasicHeader> headers = this.getAEPDefaultHeaders();
        // add custom headers for upload data request
        headers.add(new BasicHeader("Content-Type", "application/octet-stream"));

        return AsyncHttpRequest.build("put",
                this.DATA_INGESTION_URL + "/" + batchId + "/datasets/" + datasetId + "/files/" + fileName)
                .withHeaders(headers).withEntity(new InputStreamEntity(inputStream)).build();
    }

    public HttpEntityEnclosingRequestBase completeBatchRequest(String batchId) {
        /*
            Function to return a `completeBatch` async http request for AEP API.
         */
        return AsyncHttpRequest.build("post",
                this.DATA_INGESTION_URL + "/" + batchId + "?action=COMPLETE")
                .withHeaders(this.getAEPDefaultHeaders()).build();
    }
}



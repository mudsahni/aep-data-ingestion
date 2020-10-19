package com.expedia.aep;

import com.expedia.http.async.AsyncHttpClient;
import com.expedia.http.async.AsyncHttpRequest;
import com.expedia.util.Helper;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.nio.reactor.IOReactorException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class Auth {
    private String clientId;
    private String clientSecret;
    private String privateKeyString;
    private String ims;
    private String orgId;
    private String technicalAccountId;

    private long jwtExpirationTime = 0;
    private String jwt;
    private ArrayList<String> metascopes = new ArrayList<String>();
    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private final Logger logger = Logger.getLogger("AEP");
    private CloseableHttpAsyncClient client;

    public Auth(String clientId, String clientSecret,
                String privateKeyString, String ims,
                String orgId, String technicalAccountId) throws IOReactorException {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.privateKeyString = privateKeyString;
        this.ims = ims;
        this.orgId = orgId;
        this.technicalAccountId = technicalAccountId;
        metascopes.add("/s/ent_dataservices_sdk");
        this.client = this.asyncHttpClient.build();
    }

    public String generateJWT() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        /*
          Function to generate a new jsonwebtoken (JWT).
          Creates an instance of the JWT class
        */

        final JWT jsonwebtoken = new JWT(this.clientId, this.ims, this.orgId,
                this.technicalAccountId, this.privateKeyString, this.metascopes);
        this.jwt = jsonwebtoken.buildJWT();
        this.jwtExpirationTime = jsonwebtoken.getExpirationTime();
        return this.jwt;
    }

    public String getJWT() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
    /*
      Checks whether jsonwebtoken (JWT) already exists,
      or needs to be regenerated.
    */

        if (this.jwt == null || this.jwt.isEmpty()) {
            return this.generateJWT();
        } else {
            long currentTime = System.currentTimeMillis() / 1000;
            if (this.jwtExpirationTime >= currentTime) {
                return this.generateJWT();
            } else {
                return this.jwt;
            }
        }
    }

    public List<NameValuePair> getPayLoad() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        /*
            Function to generate a payload for fetching an access token from AEP API.
         */
        List<NameValuePair> payload = new ArrayList<NameValuePair>();
        payload.add(new BasicNameValuePair("client_id", this.clientId));
        payload.add(new BasicNameValuePair("client_secret", clientSecret));
        payload.add(new BasicNameValuePair("jwt_token", this.getJWT()));
        return payload;

    }

    public List<NameValuePair> getPayLoad(String jwt) {
        /*
            Function to generate a payload for fetching an access token from AEPI API.

            Arguments:
                1. jwt <String> - Jsonwebtoken
         */
        List<NameValuePair> payload = new ArrayList<NameValuePair>();
        payload.add(new BasicNameValuePair("client_id", this.clientId));
        payload.add(new BasicNameValuePair("client_secret", clientSecret));
        payload.add(new BasicNameValuePair("jwt_token", jwt));
        return payload;

    }

    public String getAccessToken() throws InvalidKeySpecException, NoSuchAlgorithmException,
            IOException, ExecutionException, InterruptedException {
        /*
            Function to get access token from AEP API.
         */

        // start async http client
        this.client.start();
        // make http request to fetch access token
        HttpResponse response = client.execute(
                AsyncHttpRequest.build("post", "https://" + ims + "/ims/exchange/jwt")
                        // add payload
                        .withEntity(new UrlEncodedFormEntity(this.getPayLoad())).build(), null)
                // get response from promise
                .get();

        // parse http response and get access token
        // will throw an error if status code != 200
        String access_token = Helper.parseResponse(response, HttpStatus.SC_OK, "access_token");

        // stop async http client
        this.client.close();

        // return access_token
        return access_token;

    }

}

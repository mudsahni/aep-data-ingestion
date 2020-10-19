package com.expedia.aws;

import org.json.JSONObject;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

public class Secrets {

    public static JSONObject getSecret(String arn, String region) {
        SecretsManagerClient client = SecretsManagerClient
                .builder()
                .httpClient(ApacheHttpClient.builder().build())
                .region(Region.of(region))
                .build();

        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder().secretId(arn).build();
        GetSecretValueResponse getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
        String secretString = getSecretValueResponse.secretString();
        return new JSONObject(secretString);
    }
}

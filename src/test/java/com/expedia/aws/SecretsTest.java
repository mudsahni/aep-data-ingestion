package com.expedia.aws;

import org.json.JSONObject;
import org.junit.Ignore;
import org.junit.Test;

public class SecretsTest {

    @Ignore("No DECAF connection")
    @Test
    public void getSecrets() {
        String arn = "arn:aws:secretsmanager:us-east-1:040764703330:secret:eg-clickstream-aep-secret-dev-gJanDy";
        String region = "us-east-1";
        String name = "eg-clickstream-aep-secret-dev";

        JSONObject secrets = Secrets.getSecret(arn, region);
        System.out.println(secrets);
    }
}

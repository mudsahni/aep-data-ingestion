package com.expedia.util.job;

public class AEPManager {
    private String clientId;
    private String clientSecret;
    private String ims;
    private String technicalAccountId;
    private String orgId;
    private String privateKey;
    private int maxRetryCounter;

    public String getClientId() {
        return this.clientId;
    }

    public String getIms() {
        return this.ims;
    }

    public String getTechnicalAccountId() {
        return this.technicalAccountId;
    }

    public String getOrgId() {
        return this.orgId;
    }

    public String getPrivateKey() {
        return this.privateKey;
    }

    public String getClientSecret() {
        return this.clientSecret;
    }

    public int getMaxRetryCounter() {
        return this.maxRetryCounter;
    }
}

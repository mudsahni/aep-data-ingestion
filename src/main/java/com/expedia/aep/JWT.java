package com.expedia.aep;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;

class JWT {

    private String clientId;
    private String ims;
    private String orgId;
    private String technicalAccountId;
    private String privateKeyString;
    private Long ONE_HOURS_IN_SECONDS = 3600L;
    private Long expirationTime;
    private ArrayList<String> metascopes;
    private PrivateKey privateKey;

    JWT(String clientId,
        String ims, String orgId,
        String technicalAccountId,
        String privateKey,
        ArrayList<String> metascopes) {
        this.clientId = clientId;
        this.ims = ims;
        this.orgId = orgId;
        this.technicalAccountId = technicalAccountId;
        this.privateKeyString = privateKey;
        this.expirationTime = System.currentTimeMillis() / 1000 + this.ONE_HOURS_IN_SECONDS;
        this.metascopes = metascopes;
    }

    private static String loadPrivateKey(String privateKey) throws IOException {
        StringBuilder pkcs8Lines = new StringBuilder();
        BufferedReader rdr = new BufferedReader(new StringReader(privateKey));
        String line;
        while ((line = rdr.readLine()) != null) {
            pkcs8Lines.append(line);
        }
        return pkcs8Lines.toString();
    }

    private void formatPrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
//        this.privateKeyString = loadPrivateKey("src/main/resources/private.key");
        String formattedPrivateKeyString = this.privateKeyString
                .replaceAll("\\n", "")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        System.out.println(formattedPrivateKeyString);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] keyBytes = Base64.getMimeDecoder().decode(formattedPrivateKeyString);
        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(keyBytes);
        this.privateKey = keyFactory.generatePrivate(ks);


    }

    private HashMap<String, Object> getJWTClaims() {
        HashMap<String, Object> jwtClaims = new HashMap<String, Object>();
        jwtClaims.put("iss", orgId);
        jwtClaims.put("sub", technicalAccountId);
        jwtClaims.put("exp", expirationTime);
        jwtClaims.put("aud", "https://" + this.ims + "/c/" + this.clientId);
        this.metascopes.forEach((x) -> jwtClaims.put("https://" + this.ims + x, true));
        return jwtClaims;
    }

    String buildJWT() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        this.formatPrivateKey();

        HashMap<String, Object> jwtClaims = this.getJWTClaims();

        return Jwts.builder().setClaims(jwtClaims).signWith(
                SignatureAlgorithm.RS256, this.privateKey)
                .compact();

    }

    Long getExpirationTime() {
        return this.expirationTime;
    }


}

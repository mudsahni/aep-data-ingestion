package com.expedia.aep;

import com.expedia.util.Config;
import com.expedia.util.error.ReadingConfigFileException;
import com.expedia.util.job.AEPManager;
import com.expedia.util.job.Job;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.ExecutionException;

public class AuthTest {

    @Ignore("JWT Test is ignored.")
    @Test
    public void getJWTTest() throws IOException, ReadingConfigFileException,
            InvalidKeySpecException, NoSuchAlgorithmException {
        Job job = Config.getJobConfiguration("src/test/resources/test");
        AEPManager aep = job.getAepManager();

        Auth auth = new Auth(aep.getClientId(), aep.getClientSecret(), aep.getPrivateKey(),
                aep.getIms(), aep.getOrgId(), aep.getTechnicalAccountId());
        String jwt = auth.getJWT();
        System.out.println(jwt);

    }

    public static String getAccessTokenTest() throws IOException, ReadingConfigFileException, NoSuchAlgorithmException, ExecutionException, InvalidKeySpecException, InterruptedException {
        Job job = Config.getJobConfiguration("src/test/resources/test");
        AEPManager aep = job.getAepManager();

        Auth auth = new Auth(aep.getClientId(), aep.getClientSecret(), aep.getPrivateKey(),
                aep.getIms(), aep.getOrgId(), aep.getTechnicalAccountId());

        return auth.getAccessToken();
    }
}

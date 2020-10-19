package com.expedia;

import com.expedia.aep.AuthTest;
import com.expedia.util.AppMetrics;
import com.expedia.util.Config;
import com.expedia.util.error.AEPMaxRetriesExceededException;
import com.expedia.util.error.ReadingConfigFileException;
import com.expedia.util.job.Job;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.ExecutionException;

/**
 * Unit test for simple App.
 */
public class AppTest {
    /**
     * Rigorous Test :-)
     */

    public static Job readConfigFile() throws IOException, ReadingConfigFileException {
        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        return Config.getJobConfiguration("src/test/resources/test");
    }


    @Ignore("Ignore main test.")
    @Test
    public void sendToAEP() throws IOException, InvalidKeySpecException, ExecutionException,
            NoSuchAlgorithmException, InterruptedException,
            ReadingConfigFileException, AEPMaxRetriesExceededException {
        String accessToken = AuthTest.getAccessTokenTest();
        byte[] fileContent = Files.readAllBytes(new File("src/test/resources/test.json").toPath());
        ByteArrayInputStream data = new ByteArrayInputStream(fileContent);
        System.out.println(data);

        AppMetrics appMetrics = new AppMetrics("3434", "2020-01-02");
        System.out.println("t1");
        String batchId = SendToAEP.sendFileToAEP(readConfigFile(), accessToken, data, appMetrics,
                "5f8468c33f219f194ca1ac47", "test123", "json");
        System.out.println(batchId);
    }


}

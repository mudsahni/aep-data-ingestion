package com.expedia;

import com.expedia.aep.AEPDI;
import com.expedia.util.AppMetrics;
import com.expedia.util.error.AEPMaxRetriesExceededException;
import com.expedia.util.job.AEPManager;
import com.expedia.util.job.Job;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class SendToAEP {
    private static Logger logger = Logger.getLogger("SendToAEP");

    public static String sendFileToAEP(Job credentials, String accessToken,
                                       ByteArrayInputStream data, AppMetrics appMetrics,
                                       String datasetId, String fileName, String dataFormat)
            throws IOException, InterruptedException, ExecutionException, AEPMaxRetriesExceededException {

        /*
            Function to send data to adobe experience platform.

            Arguments:
                1. credentials <Job> - adobe credentials to connect with the api
                2. accessToken <String> - access token generated from adobe
                3. data <ByteArrayInputStream> - parquet/json format data to be sent to adobe
                4. appMetrics <AppMetrics> - app metrics class to store performance metrics
                5. datasetId <String> - dataset ID to send the data to in Adobe Experience Platform
                6. fileName <String> - custom name for the file to be sent to AEP
                7. dataFormat <String> - json or parquet
            Returns:
                batchId <String> from adobe experience platform
         */

        // get aep credentials from job class
        AEPManager aep = credentials.getAepManager();
        // create an instance of the aep data ingestion class
        AEPDI aepDI = new AEPDI(aep.getClientId(), aep.getOrgId(), accessToken,
                aep.getMaxRetryCounter(), appMetrics, datasetId, dataFormat);

        // complete data upload and get status
        int status = aepDI.start().createBatch().uploadData(fileName, data).completeBatch();
        // close api http connection
        aepDI.close();
        SendToAEP.logger.info("Status of batch " + aepDI.getBatchId() + " completion: " + status);
        SendToAEP.logger.info("Status of batch completion: " + status);
        // assert if status is OK
        assert (status < 300);
        // return batchId
        return aepDI.getBatchId();


    }
}

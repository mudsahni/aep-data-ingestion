package com.expedia.aep;

import com.expedia.http.async.AsyncHttpClient;
import com.expedia.util.AppMetrics;
import com.expedia.util.Helper;
import com.expedia.util.error.AEPMaxRetriesExceededException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.nio.reactor.IOReactorException;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class AEPDI extends AEPDIBase {

    private String datasetId;
    private String batchId;
    private String fileName;
    private String dataFormat = "parquet";
    private int maxRetryCounter;
    private AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    private HttpEntityEnclosingRequestBase CrBR;
    private HttpEntityEnclosingRequestBase UDR;
    private HttpEntityEnclosingRequestBase CoBR;
    private CloseableHttpAsyncClient client;
    private AppMetrics appMetrics;
    private Logger logger = Logger.getLogger("AEPDI");

    public AEPDI(String clientId, String orgId,
                 String accessToken, int maxRetryCounter,
                 AppMetrics appMetrics, String datasetId, String dataFormat) throws IOReactorException {
        super(clientId, orgId, accessToken);
        this.maxRetryCounter = maxRetryCounter;
        this.client = this.asyncHttpClient.build();
        this.appMetrics = appMetrics;
        this.datasetId = datasetId;
        this.dataFormat = dataFormat;
    }

    public AEPDI start() throws IOReactorException {
        logger.info("Starting async http connection...");
        // start async http client
        this.client.start();
        return this;
    }

    public String getBatchId() {
        return this.batchId;
    }

    public HttpResponse createBatchRetry(int retryCounter) throws AEPMaxRetriesExceededException,
            ExecutionException, InterruptedException {
        if (retryCounter > this.maxRetryCounter) {
            logger.warning("Retry counter has exceeded the max limit." +
                    "could not create a new batch.");
            this.appMetrics.batchCreationFailure += 1;
            throw new AEPMaxRetriesExceededException("Could not create a new batch. " +
                    "Max retry count exceeded.");
        } else {
            logger.info("Batch creation attempt:" + retryCounter);
            HttpResponse response = this.client.execute(this.CrBR, null).get();
            // check if response is 200
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
                // if response was not 200, log error
                logger.severe("Batch creation request was not successful." +
                        "Error code: " + response.getStatusLine().getStatusCode() +
                        "Error cause: " + response.getStatusLine().getReasonPhrase());
                return this.createBatchRetry(retryCounter + 1);

            } else {
                logger.info("Batch creation was completed. " + response.getStatusLine().getReasonPhrase());
                // return http response
                return response;

            }
        }
    }

    public AEPDI createBatch() throws IOException, InterruptedException,
            ExecutionException, AEPMaxRetriesExceededException {
        /*
          Function to create a batch in AEP.
        */
        logger.info("Creating new batch...");
        // form create batch request
        this.CrBR = this.createBatchRequest(this.datasetId, this.dataFormat);
        // execute http request and fetch batchId
        // TODO: add exception handling
        this.batchId = Helper.parseResponse(this.createBatchRetry(0), HttpStatus.SC_CREATED, "id");
        // close async http client
        //    this.AEP.asyncHttpClient.close()
        logger.info("Successfully created new batch " + this.batchId + " for dataset " + this.datasetId + ".");
        this.appMetrics.batchCreationSuccess += 1;
        // return object instance
        return this;
    }

    public HttpResponse uploadDataRetry(int retryCounter, String fileName)
            throws AEPMaxRetriesExceededException, ExecutionException, InterruptedException {
        /*
          Function to attempt uploading a file with multiple tries.

          Arguments:
            retryCounter - count of file upload attempts.
          Output:
            return HttpResponse of upload file request.
        */

        // check retry counter with max possible retries
        if (retryCounter > this.maxRetryCounter) {
            logger.warning("Retry counter has exceeded the max limit." +
                    "Forgoing file ${fileName}.");
            this.appMetrics.fileUploadFailure += 1;
            throw new AEPMaxRetriesExceededException("Could not complete file upload for  " + fileName + ". " +
                    "Max retry count exceeded.");
        } else {
            logger.info("File " + fileName + "completion attempt: " + retryCounter + ".");
            HttpResponse response = this.client.execute(this.UDR, null).get();
            // check if response is 200
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                // if response was not 200, log error
                logger.severe("File " + fileName + "completion request was not successful." +
                        "Error code: " + response.getStatusLine().getStatusCode() +
                        "Error cause: " + response.getStatusLine().getReasonPhrase());
                return this.uploadDataRetry(retryCounter + 1, fileName);

            } else {
                logger.info("File " + fileName + "was completed. " + response.getStatusLine().getReasonPhrase());
                // return http response
                return response;

            }
        }
    }

    public AEPDI uploadData(String fileName, InputStream inputStream)
            throws IOException, InterruptedException, ExecutionException, AEPMaxRetriesExceededException {
        /*
          Function to upload a file to a batch.

          Arguments:
            fileName - name of the file to identify it within the batch/dataset.
            inputStream - ByteInputStream to attach with AEP request.
          Output:
            AEPDI object
        */
        logger.info("Starting file upload...");
        // saving the filename
        this.fileName = fileName;
        this.UDR = this.uploadDataRequest(this.datasetId, this.batchId, fileName, inputStream);
        HttpResponse response = this.uploadDataRetry(0, fileName);
        //    this.AEP.asyncHttpClient.close()
        logger.info("File upload completed.");
        this.appMetrics.fileUploadSuccess += 1;
        inputStream.close();
        return this;
    }

    public int completeBatch() throws InterruptedException, ExecutionException, AEPMaxRetriesExceededException {
        /*
          Function to complete batch. Creates complete batch request,
          sends request to function to complete batch with retries.

          Output:
            HttpStatusCode [Int]
        */
        logger.info("Completing batch: " + this.batchId);
        this.CoBR = this.completeBatchRequest(this.batchId);
        //    this.AEP.asyncHttpClient.start()
        HttpResponse response = this.completeBatchRetry(0);
        logger.info("Batch " + this.batchId + "successfully completed.");
        this.appMetrics.batchCompletionSuccess += 1;

        // return status code - should be 200
        return response.getStatusLine().getStatusCode();
    }

    public HttpResponse completeBatchRetry(int retryCounter)
            throws AEPMaxRetriesExceededException, ExecutionException, InterruptedException {
        /*
          Function to attempt completing a batch with multiple tries.

          Arguments:
            retryCounter - count of batch completion attempts.
          Output:
            return HttpResponse of complete batch request.
        */

        // check retry counter with max possible retries

        if (retryCounter > this.maxRetryCounter) {
            logger.warning("Retry counter has exceeded the max limit. Forgoing batch " + batchId + ".");
            this.appMetrics.batchCompletionFailure += 1;
            // send backlog
            throw new AEPMaxRetriesExceededException("Could not complete batch "
                    + batchId + ". Max retry count exceeded.");

        } else {
            logger.info("Batch " + batchId + " completion attempt: " + retryCounter + ".");
            HttpResponse response = this.client.execute(this.CoBR, null).get();
            // check if response is 200
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                // if response was not 200, log error
                logger.severe("Batch " + batchId + " completion request was not successful." +
                        "Error code: " + response.getStatusLine().getStatusCode() +
                        "Error cause: " + response.getStatusLine().getReasonPhrase());
                return this.completeBatchRetry(retryCounter + 1);
            } else {
                logger.info("Batch " + batchId + " was completed. " + response.getStatusLine().getReasonPhrase());
                // return http response
                return response;

            }

        }
    }

    public void close() throws IOException {
        /*
            Function to close async http connection.
         */
        logger.info("Closing async http connection...");
        this.client.close();

    }
}



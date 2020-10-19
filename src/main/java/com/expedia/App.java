package com.expedia;

import com.expedia.aep.Auth;
import com.expedia.util.AppMetrics;
import com.expedia.util.Config;
import com.expedia.util.error.AEPMaxRetriesExceededException;
import com.expedia.util.error.CommandLineParameterMissingException;
import com.expedia.util.error.ReadingConfigFileException;
import com.expedia.util.job.AEPManager;
import com.expedia.util.job.Job;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

/**
 * Hello world!
 */
public class App {
    private static Logger logger = Logger.getLogger("App");

    private static Map<String, String> parseCommandLineArgs(String[] args) {
        List<String> listArgs = Arrays.asList(args);
        Map<String, String> argsMap = new HashMap<String, String>();
        listArgs.stream().filter(arg -> arg.startsWith("-")).forEach(arg -> {
            String[] strings = arg.replaceFirst("^-", "").split("=");
            argsMap.put(strings[0], strings[1]);
        });
        return argsMap;
    }

    private static void checkIfParameterExists(Map<String, String> commandLineArgs, String parameter)
            throws CommandLineParameterMissingException {
        if (!commandLineArgs.containsKey(parameter)) {
            throw new CommandLineParameterMissingException("Parameter " + parameter
                    + " was not found in command line arguments.");
        }
    }

    private static void parseCommandLineArgs(Map<String, String> commandLineArgs) throws CommandLineParameterMissingException {

        App.checkIfParameterExists(commandLineArgs, "datasetId");
        App.checkIfParameterExists(commandLineArgs, "dataFormat");
        App.checkIfParameterExists(commandLineArgs, "privateKey");
        App.checkIfParameterExists(commandLineArgs, "clientSecret");
        App.checkIfParameterExists(commandLineArgs, "filePath");
        App.checkIfParameterExists(commandLineArgs, "fileName");

    }

    public static void main(String[] args) throws IOException, ReadingConfigFileException,
            NoSuchAlgorithmException, ExecutionException, InvalidKeySpecException,
            InterruptedException, AEPMaxRetriesExceededException, CommandLineParameterMissingException {
        // get config parameters
        Map<String, String> commandLineArgs = App.parseCommandLineArgs(args);
        parseCommandLineArgs(commandLineArgs);
        Job job = Config.getJobConfiguration("src/test/resources/test");
        AEPManager aep = job.getAepManager();
        String date = java.time.LocalDateTime.now().toString();
        String id = "SendToAEP_" + date;
        App.logger.info("Configuration parameters have been loaded in.");

        // get adobe access token
//        Auth auth = new Auth(aep.getClientId(), commandLineArgs.get("clientSecret"),
//                commandLineArgs.get("privateKey"), aep.getIms(),
//                aep.getOrgId(), aep.getTechnicalAccountId());
        Auth auth = new Auth(aep.getClientId(), aep.getClientSecret(),
                aep.getPrivateKey(), aep.getIms(),
                aep.getOrgId(), aep.getTechnicalAccountId());

        String accessToken = auth.getAccessToken();
        App.logger.info("Access token has been generated.");

        // read in data
        byte[] fileContent = Files.readAllBytes(new File(commandLineArgs.get("filePath")).toPath());
        ByteArrayInputStream data = new ByteArrayInputStream(fileContent);

        // load app metrics
        AppMetrics appMetrics = new AppMetrics(id, date);

        // send file to adobe experience platform
        String batchId = SendToAEP.sendFileToAEP(job, accessToken, data, appMetrics,
                commandLineArgs.get("datasetId"),
                commandLineArgs.get("fileName"),
                commandLineArgs.get("dataFormat"));


    }
}

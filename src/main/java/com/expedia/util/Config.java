package com.expedia.util;

import com.expedia.util.error.ReadingConfigFileException;
import com.expedia.util.job.Job;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.io.IOException;

public class Config {

    public static Job getJobConfiguration(String env) throws IOException, ReadingConfigFileException {
        ObjectMapper m2 = new ObjectMapper(new YAMLFactory());

        try {
            return m2.readValue(new File(env + ".yaml"), Job.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ReadingConfigFileException("Config file " + env + ".yaml could not be read.");
        }
    }
}

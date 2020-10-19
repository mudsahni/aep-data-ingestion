package com.expedia.util.error;

public class ReadingConfigFileException extends Exception {
    private String message;

    public ReadingConfigFileException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

}

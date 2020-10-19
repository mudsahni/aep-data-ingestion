package com.expedia.util.error;

public class CommandLineParameterMissingException extends Exception {
    private String message;

    public CommandLineParameterMissingException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

}

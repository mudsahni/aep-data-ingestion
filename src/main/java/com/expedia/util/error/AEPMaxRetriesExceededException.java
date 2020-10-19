package com.expedia.util.error;

public class AEPMaxRetriesExceededException extends Exception {
    private String message;

    public AEPMaxRetriesExceededException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

}

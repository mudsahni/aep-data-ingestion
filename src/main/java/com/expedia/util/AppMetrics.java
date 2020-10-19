package com.expedia.util;

public class AppMetrics {
    private long startTime = 0;
    private long endTime = 0;
    private long totalTime = 0;
    public int batchCreationFailure = 0;
    public int batchCreationSuccess = 0;
    public int fileUploadFailure = 0;
    public int fileUploadSuccess = 0;
    public int batchCompletionFailure = 0;
    public int batchCompletionSuccess = 0;
    private String id;
    private String date;

    public AppMetrics(String id, String date) {
        this.id = id;
        this.date = date;
    }

    public void startTimer() {
        this.startTime = System.nanoTime();
    }

    public void endTimer() {
        this.endTime = System.nanoTime();
    }

    public double calculateTime() {
        this.totalTime = this.endTime - this.startTime;
        return this.totalTime / 1000000000.0;
    }

    public String getId() {
        return this.id;
    }

    public String getDate() {
        return this.date;
    }
}

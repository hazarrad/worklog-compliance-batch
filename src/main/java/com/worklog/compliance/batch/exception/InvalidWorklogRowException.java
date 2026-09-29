package com.worklog.compliance.batch.exception;

public class InvalidWorklogRowException extends RuntimeException {

    private final int rowNumber;
    private final String rawData;

    public InvalidWorklogRowException(int rowNumber, String rawData, String message) {
        super(message);
        this.rowNumber = rowNumber;
        this.rawData = rawData;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public String getRawData() {
        return rawData;
    }
}
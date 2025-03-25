package com.hinam_list.hinam_list_fetch.service.scraper.exception;

public class APIResponseException extends Exception {
    private final int statusCode;
    public APIResponseException(int statusCode) {
        super(String.format("Unexpected API Response - Actual response code is: %d", statusCode));
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}

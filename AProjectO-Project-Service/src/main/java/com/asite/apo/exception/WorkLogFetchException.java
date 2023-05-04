package com.asite.apo.exception;

public class WorkLogFetchException extends Exception{
    public WorkLogFetchException(String msg)
    {
        super("Error in Task Fetching Exception: " + msg);
    }
}

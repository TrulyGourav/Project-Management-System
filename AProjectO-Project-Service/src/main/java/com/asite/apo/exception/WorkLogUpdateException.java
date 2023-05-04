package com.asite.apo.exception;

public class WorkLogUpdateException extends Exception{
    public WorkLogUpdateException(String msg)
    {
        super("Error in Task Updating Exception: " + msg);
    }
}

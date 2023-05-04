package com.asite.apo.exception;

public class WorkLogCreateException extends Exception{
    public WorkLogCreateException(String msg)
    {
        super("Error in Task Creation Exception: " + msg);
    }
}

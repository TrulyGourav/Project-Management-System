package com.asite.apo.exception;

public class WorkLogDeleteException extends Exception{
    public WorkLogDeleteException(String msg)
    {
        super("Error in Task Deleting Exception: " + msg);
    }
}

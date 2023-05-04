package com.asite.apo.exception;

public class ResourceNotFoundException extends Exception{

    public ResourceNotFoundException(String msg)
    {
        super("Resource not found Exception: " + msg);
    }
}

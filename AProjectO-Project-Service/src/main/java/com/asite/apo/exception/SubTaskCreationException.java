package com.asite.apo.exception;

public class SubTaskCreationException extends Exception{
    public SubTaskCreationException(String message){
        super("Error While Creating Task : "+message);
    }
}

package com.asite.aprojecto.authentication.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
public class ErrorDetails {
    private Date Timestamp;
    private String message;
    private String details;
    public ErrorDetails(Date timestamp, String message, String details) {
        Timestamp = timestamp;
        this.message = message;
        this.details = details;
    }
}

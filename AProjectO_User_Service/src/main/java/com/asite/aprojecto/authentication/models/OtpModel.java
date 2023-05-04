package com.asite.aprojecto.authentication.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

@Getter
@Setter
@AllArgsConstructor
public class OtpModel {
    private final String otp;
    private final long expiry;
    private final String email;
}
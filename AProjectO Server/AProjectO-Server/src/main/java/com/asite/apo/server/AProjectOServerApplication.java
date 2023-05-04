package com.asite.apo.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class AProjectOServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(AProjectOServerApplication.class, args);
	}
}
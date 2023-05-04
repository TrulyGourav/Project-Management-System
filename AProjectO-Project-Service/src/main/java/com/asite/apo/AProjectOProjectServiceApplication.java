package com.asite.apo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
//@ComponentScan(basePackages = "com.asite.apo")
//@ComponentScan(basePackages = "com.asite.apo")
//@EnableJpaRepositories(basePackages = "com.asite.apo.Repository")
//@EntityScan(basePackages = "com.asite.apo.model")
//@EnableEurekaClient
@EnableCaching
public class AProjectOProjectServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(AProjectOProjectServiceApplication.class, args);
	}
}

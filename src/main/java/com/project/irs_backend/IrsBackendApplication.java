package com.project.irs_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IrsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(IrsBackendApplication.class, args);
	}
}

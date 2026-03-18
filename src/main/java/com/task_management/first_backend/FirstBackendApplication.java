package com.task_management.first_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FirstBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FirstBackendApplication.class, args);
	}

}

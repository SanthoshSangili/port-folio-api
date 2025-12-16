package com.portfolioapi.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PortFolioApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PortFolioApiApplication.class, args);
	}

}

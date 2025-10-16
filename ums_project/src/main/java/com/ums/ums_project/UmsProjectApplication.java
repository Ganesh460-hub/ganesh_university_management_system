package com.ums.ums_project;

import jakarta.persistence.Entity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class UmsProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(UmsProjectApplication.class, args);
	}



}

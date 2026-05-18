package com.example.experiencias;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ExperienciasApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExperienciasApplication.class, args);
	}

}

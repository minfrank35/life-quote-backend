package com.minfrank.dailyharu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DailyharuApplication {

	public static void main(String[] args) {
		SpringApplication.run(DailyharuApplication.class, args);
	}

}

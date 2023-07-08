package com.shameless.bookingtech.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication(scanBasePackages = "com.shameless.bookingtech")
@EntityScan(basePackages= {"com.shameless.bookingtech.domain.entity"})
@EnableJpaRepositories("com.shameless.bookingtech.domain.repository")
@EnableJpaAuditing
public class BookingtechApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+1"));
		SpringApplication.run(BookingtechApplication.class, args);
	}

}

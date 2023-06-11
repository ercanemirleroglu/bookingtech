package com.shameless.bookingtech.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.shameless.bookingtech")
@EntityScan(basePackages= {"com.shameless.bookingtech.domain.entity"})
@EnableJpaRepositories("com.shameless.bookingtech.domain.repository")
@EnableScheduling
public class BookingtechApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookingtechApplication.class, args);
	}

}

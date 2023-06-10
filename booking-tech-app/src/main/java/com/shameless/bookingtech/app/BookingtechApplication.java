package com.shameless.bookingtech.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(scanBasePackages = "com.shameless.bookingtech")
public class BookingtechApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookingtechApplication.class, args);
	}

}

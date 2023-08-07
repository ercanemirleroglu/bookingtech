package com.shameless.bookingtech.app.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;


@Configuration
@EnableScheduling
@EnableConfigurationProperties(RsaKeyProperties.class)
public class AppConfig {
}

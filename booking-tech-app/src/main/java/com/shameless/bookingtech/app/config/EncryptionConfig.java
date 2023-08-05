package com.shameless.bookingtech.app.config;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@EnableEncryptableProperties
public class EncryptionConfig {

	@Value("${application.security.encryption.passwordFilePath}")
	private String passwordFilePath;

	@Bean("jasyptStringEncryptor")
	public StringEncryptor stringEncryptor() throws IOException {
		PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
		SimpleStringPBEConfig config = new SimpleStringPBEConfig();
		config.setPassword(this.readPemFile(passwordFilePath));
		config.setAlgorithm("PBEWithMD5AndDES");
		config.setKeyObtentionIterations("1000");
		config.setPoolSize("1");
		config.setProviderName("SunJCE");
		config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
		config.setIvGeneratorClassName("org.jasypt.salt.NoOpIVGenerator");
		config.setStringOutputType("base64");
		encryptor.setConfig(config);
		return encryptor;
	}

	private String readPemFile(String p) throws IOException {
		Path path = Paths.get(p);
		if (!Files.exists(path)) {
			throw new IOException("File not found: " + path);
		}
		byte[] bytes = Files.readAllBytes(path);
		return new String(bytes);
	}
}

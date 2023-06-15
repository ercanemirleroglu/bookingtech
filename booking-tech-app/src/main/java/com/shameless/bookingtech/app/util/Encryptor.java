package com.shameless.bookingtech.app.util;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class Encryptor {
    private final StringEncryptor stringEncryptor;

    public Encryptor(@Qualifier("jasyptStringEncryptor") StringEncryptor stringEncryptor) {
        this.stringEncryptor = stringEncryptor;
    }

    public String encrypt(String password) {
        return stringEncryptor.encrypt(password);
    }

}

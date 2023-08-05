package com.shameless.bookingtech.domain.util;

import com.shameless.bookingtech.app.BookingtechApplication;
import com.shameless.bookingtech.app.config.EncryptionConfig;
import com.shameless.bookingtech.app.service.ProcessService;
import com.shameless.bookingtech.app.util.Decryptor;
import com.shameless.bookingtech.app.util.Encryptor;
import com.shameless.bookingtech.integration.automation.infra.AppDriverFactory;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {BookingtechApplication.class})
@MockBean(classes = {ProcessService.class, AppDriverFactory.class})
@Slf4j
class EncryptorTest {

    @Autowired
    private Encryptor encryptor;

    @Autowired
    private Decryptor decryptor;

    @Test
    public void enc_test(){
        String password = "12345678";
        String passwordEnc = encryptor.encrypt(password);
        log.info(passwordEnc);
        String passwordDec = decryptor.decrypt(passwordEnc);
        Assertions.assertEquals(password, passwordDec);
    }
}

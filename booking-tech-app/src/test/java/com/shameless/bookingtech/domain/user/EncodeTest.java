package com.shameless.bookingtech.domain.user;

import com.shameless.bookingtech.app.BookingtechApplication;
import com.shameless.bookingtech.app.service.ProcessService;
import com.shameless.bookingtech.app.user.PasswordEncoder;
import com.shameless.bookingtech.integration.automation.infra.AppDriverFactory;
import org.junit.Ignore;
import org.junit.Test;
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
public class EncodeTest {
    @Autowired
    private PasswordEncoder underTest;

    @Test
    @Ignore
    public void encode(){
        String password = "zzz";
        String encode = underTest.encode(password);
        System.out.println(encode);
    }
}

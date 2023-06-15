package com.shameless.bookingtech.app.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender javaMailSender, TemplateEngine templateEngine) {
        this.javaMailSender = javaMailSender;
        this.templateEngine = templateEngine;
    }

    public void sendMail(Object model, String emailTemplate) throws MessagingException {
        Context context = new Context();
        context.setVariable("model", model);

        String htmlContent = templateEngine.process(emailTemplate, context);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String to = "emirleroglu.ercan@gmail.com";
        String[] toList = to.split(";");
        helper.setTo(toList);
        helper.setSubject("Hotel Price Comparison");
        helper.setText(htmlContent, true);
        log.info("Sending E-mail...");
        try{
            javaMailSender.send(message);
        }catch (MailAuthenticationException mae) {
            log.warn("Mail Auth Error");
        }catch (MailException me){
            log.warn("Mail Error!");
            me.printStackTrace();
        }

    }
}

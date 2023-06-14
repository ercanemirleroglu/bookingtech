package com.shameless.bookingtech.app.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
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

        helper.setTo("emirleroglu.ercan@gmail.com;ercan.emirleroglu@pinsoft.ist");
        helper.setSubject("Hotel Price Comparison");
        helper.setText(htmlContent, true);

        javaMailSender.send(message);
    }
}

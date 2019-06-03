package com.donat.donchess.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String MESSAGE_FROM;
    private JavaMailSender javaMailSender;


    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendMessage(String mail) {
        SimpleMailMessage message;

        try {
            message = new SimpleMailMessage();
            message.setFrom(MESSAGE_FROM);
            message.setTo(mail);
            message.setSubject("Udvozlo uzenet");
            message.setText("Hello! Ez itt az üzenet, \n amit neked küldünk");
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}

package com.donat.donchess.service;

import com.donat.donchess.domain.User;
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


    public void sendAuthenticatonMail(User newUser) {
        SimpleMailMessage message;

        try {
            message = new SimpleMailMessage();
            message.setFrom(MESSAGE_FROM);
            message.setTo(newUser.getEmail());
            message.setSubject("DonChess registration - authentication");
            message.setText("Please click here: \n http://localhost:8080/api/user/authenticaton?token=" + newUser.getAuthenticationToken());
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

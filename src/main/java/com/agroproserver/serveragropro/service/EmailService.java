package com.agroproserver.serveragropro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendSimpleMailMessage(String receiver, String subject, String text){
        
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("agroproserver@gmail.com");
        message.setTo(receiver);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}

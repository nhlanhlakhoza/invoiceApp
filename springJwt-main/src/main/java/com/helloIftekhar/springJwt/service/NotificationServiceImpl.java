package com.helloIftekhar.springJwt.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl extends NotificationService {
    @Autowired
    private JavaMailSender jmSender;


    public void sendNotification(String email, String message) {

            try {
                MimeMessage mimeMessage = jmSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

                helper.setTo(email);
                helper.setSubject("Notification");
                helper.setText(message);

                jmSender.send(mimeMessage);
            } catch (MessagingException e) {
                e.printStackTrace();
                // Handle messaging exception
            }
        }
    }


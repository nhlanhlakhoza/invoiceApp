package com.helloIftekhar.springJwt.service;

import com.helloIftekhar.springJwt.model.Invoice;
import com.helloIftekhar.springJwt.model.Notification;
import com.helloIftekhar.springJwt.model.Quote;
import com.helloIftekhar.springJwt.repository.NotificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
public class PaidNotificationServiceImpl implements PaidNotificationService{
    @Autowired
    private JavaMailSender jmSender; // Inject the JavaMailSender bean

    @Autowired
    private NotificationRepository notificationRepository; // Inject the NotificationRepository bean


    public void sendNotification(String email, String message, Invoice invoice, Quote quote) {
        try {
            MimeMessage mimeMessage = jmSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(email);
            helper.setFrom("nhlanhlakhoza05@gmail.com"); // Change this to your sender email
            helper.setSubject("Notification"); // You can set the subject as needed
            helper.setText(message, false);

            jmSender.send(mimeMessage);

            // Save notification to the database
            Notification notification = new Notification();
            notification.setMessage(message);
            notification.setRecipient(email);
            notification.setSentAt(LocalDateTime.now());

            // Set the invoice or quote based on what is provided
            if (invoice != null) {
                notification.setInvoice(invoice);
            } else if (quote != null) {
                notification.setQuote(quote);
            }

            notificationRepository.save(notification);
        } catch (MessagingException e) {
            e.printStackTrace(); // Handle the exception appropriately, like logging it
        }
    }
}

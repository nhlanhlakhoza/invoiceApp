package com.helloIftekhar.springJwt.service;

import com.helloIftekhar.springJwt.model.Notification;
import com.helloIftekhar.springJwt.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public abstract class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;
    abstract void sendNotification(String email, String message);
    public List<Notification> getNotificationsByEmail(String email) {
        return notificationRepository.findByRecipient(email);
    }
}

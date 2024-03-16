package com.helloIftekhar.springJwt.repository;

import com.helloIftekhar.springJwt.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipient(String email);
}

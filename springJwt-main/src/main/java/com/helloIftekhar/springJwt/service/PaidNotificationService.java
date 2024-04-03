package com.helloIftekhar.springJwt.service;

import com.helloIftekhar.springJwt.model.Invoice;
import com.helloIftekhar.springJwt.model.Quote;
import org.springframework.stereotype.Service;


@Service
public interface PaidNotificationService {
    void sendNotification(String email, String message, Invoice invoice, Quote quote);
}

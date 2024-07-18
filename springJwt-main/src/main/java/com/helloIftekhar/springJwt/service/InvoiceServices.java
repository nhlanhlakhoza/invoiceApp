package com.helloIftekhar.springJwt.service;

import com.helloIftekhar.springJwt.model.Invoice;
import com.helloIftekhar.springJwt.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class InvoiceServices {

    private final InvoiceRepository invoiceRepository;

    @Autowired
    public InvoiceServices(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public List<Invoice> getInvoicesByUserEmailAndPaymentStatus(String email, String paymentStatus) {
        return invoiceRepository.findAllByUserEmailAndPaymentStatus(email, paymentStatus);
    }
}

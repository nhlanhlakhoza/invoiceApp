package com.helloIftekhar.springJwt.service;

import com.helloIftekhar.springJwt.model.Invoice;
import com.helloIftekhar.springJwt.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InvoiceServiceImpl implements InvoiceService{

    @Autowired
    private InvoiceRepository invoiceRepository; // Update with your repository class

    @Override
    public Invoice getInvoiceByNo(int invoiceNo) {
        return invoiceRepository.findByInvoiceNo(invoiceNo);
    }
}

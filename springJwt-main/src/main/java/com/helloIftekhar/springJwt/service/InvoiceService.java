package com.helloIftekhar.springJwt.service;

import com.helloIftekhar.springJwt.model.Invoice;
import org.springframework.stereotype.Service;

@Service
public interface InvoiceService {
    Invoice getInvoiceByNo(int invoiceNo);
}

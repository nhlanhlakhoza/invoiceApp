package com.helloIftekhar.springJwt.service;

import com.helloIftekhar.springJwt.model.ClientAddressInvoiceQuoteItems;
import com.helloIftekhar.springJwt.model.Quote;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;

@Service
public interface QuoteService {
    Quote getQuoteByNo(int quoteNo);

}

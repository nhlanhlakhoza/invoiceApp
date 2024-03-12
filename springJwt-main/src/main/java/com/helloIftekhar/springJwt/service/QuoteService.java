package com.helloIftekhar.springJwt.service;

import com.helloIftekhar.springJwt.model.Quote;
import org.springframework.stereotype.Service;

@Service
public interface QuoteService {
    Quote getQuoteByNo(int quoteNo);
}

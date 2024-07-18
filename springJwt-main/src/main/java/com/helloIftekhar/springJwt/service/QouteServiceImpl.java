package com.helloIftekhar.springJwt.service;

import com.helloIftekhar.springJwt.model.Quote;
import com.helloIftekhar.springJwt.repository.QuoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QouteServiceImpl implements QuoteService{

    @Autowired
    private QuoteRepository quoteRepository;


    @Override
    public Quote getQuoteByNo(int quoteNo) {
        return quoteRepository.findByQuoteNo(quoteNo);
    }
}

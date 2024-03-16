package com.helloIftekhar.springJwt.model;

public class QuoteResponse {
    private Long id;
    private int quoteNo;

    private String clientName;

    public QuoteResponse(Quote quote, String clientName) {

        this.quoteNo = quote.getQuoteNo();

        this.clientName = clientName;
    }
}

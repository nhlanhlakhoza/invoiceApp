package com.helloIftekhar.springJwt.service;


import com.helloIftekhar.springJwt.model.ClientAddressInvoiceQuoteItems;
import com.helloIftekhar.springJwt.model.Invoice;
import com.helloIftekhar.springJwt.model.Quote;
import com.helloIftekhar.springJwt.model.User;

import java.util.List;

public interface Interface {


    public boolean deleteInvoice(int id, String email);
    public Invoice searchInvoice(int id, String email);
    public List<Invoice> homeTop5Invoice(String email);
    public List<Quote> homeTop5Quote(String email);
    public boolean createInvoiceOrQuote(String email, ClientAddressInvoiceQuoteItems caiqi);
    public List<Invoice> getAllInvoices(String email);

    public List<Quote> getAllQuote(String email);

}

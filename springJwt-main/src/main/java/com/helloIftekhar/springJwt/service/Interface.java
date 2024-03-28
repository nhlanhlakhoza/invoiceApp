package com.helloIftekhar.springJwt.service;


import com.helloIftekhar.springJwt.model.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface Interface {


    public boolean deleteInvoice(int id, String email);
    public Invoice searchInvoice(int id, String email);
    public List<Invoice> getTop5UnpaidInvoicesByEmail(String email, String paymentStatus);
    public List<Invoice> homeTop1Invoice(String email);
    public List<Quote> homeTop5Quote(String email);
    public boolean createInvoiceOrQuote(String email, ClientAddressInvoiceQuoteItems caiqi) throws IOException;
    public List<Invoice> getAllInvoices(String email);
    public void changeStatus(String email, int invoiceNo,double amount);
    public List<Quote> getAllQuote(String email);
    public BusinessInfo getAllBusinessInfo(String email);

    public double invoiceTotalAmt(String email);
    public void sendDoc(String to, String from,String path, Client client, String type, String link);
    boolean updateQuote(String email, ClientAddressInvoiceQuoteItems caiqi, Long quoteNo) throws IOException;
    public double getBalance(String email);
}

package com.helloIftekhar.springJwt.service;

import com.helloIftekhar.springJwt.generatePdf.generatePdf;
import com.helloIftekhar.springJwt.model.*;

import com.helloIftekhar.springJwt.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class Impl implements Interface {


    //Initialize/Declare variables/repository
    //repo
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private BusinessInfoRepository businessRepo;
    @Autowired
    private InvoiceRepository invoiceRepo;
    @Autowired
    private ItemsRepository itemRepo;
    @Autowired
    private QuoteRepository quoteRepo;
    @Autowired
    private ClientRepository clientRepo;
    @Autowired
    private ClientAddressRepository clientAddressRepo;
    @Autowired
    private MailSender mailSender;

    //generateAndSendPdf gPdf = new generateAndSendPdf();

    generatePdf gPdf = new generatePdf();











    @Override
    public boolean deleteInvoice(int id, String email) {

        if(invoiceRepo.existsById(id))
        {
            invoiceRepo.deleteById(id);
            return true;
        }
        return false;
    }


    @Override
    public List<Invoice> homeTop5Invoice(String email) {
        Optional<User> userOptional = userRepo.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Check if there are any invoices
            if (invoiceRepo.countByUser(user) == 0) {
                return Collections.emptyList();
            }

            // Return top 5 invoices by user ordered by date descending
            return invoiceRepo.findTop5ByUserOrderByDateDesc(user);
        } else {
            return Collections.emptyList();
        }
    }
    @Override
    public List<Quote> homeTop5Quote(String email) {
        // Check if there are any quotes
        if (quoteRepo.count() == 0) {
            return Collections.emptyList();
        }

        Optional<User> userOptional = userRepo.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Return top 5 quotes by user ordered by date descending
            return quoteRepo.findTop5ByUserOrderByDateDesc(user);
        } else {
            return Collections.emptyList();
        }
    }


    @Override
    @Transactional
    public boolean createInvoiceOrQuote(String email, ClientAddressInvoiceQuoteItems caiqi) {
        Optional<User> userOptional = userRepo.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            double total = 0;

            Client client = caiqi.getClient();
            client.setUser(user);

            ClientAddress clientAddress = caiqi.getClientAddress();
            clientAddressRepo.save(clientAddress);

            client.setClientAddress(clientAddress);
            clientRepo.save(client);

            if ("Invoice".equals(caiqi.getType())) {
                Invoice invoice = caiqi.getInvoice();
                invoice.setUser(user);
                invoice.setDate(LocalDate.now());

                List<Items> items = caiqi.getItems();
                for (Items item : items) {
                    item.setInvoice(invoice);
                    total += item.getPrice() * item.getQty();
                }

                invoice.setTotalAmount(total);
                invoiceRepo.save(invoice);

                for (Items item : items) {
                    item.setInvoice(invoice);
                    itemRepo.save(item);
                }
                // You may want to uncomment the PDF generation code
                try {
                    gPdf.generateEmailPdf(caiqi.getType(),invoice.getDate(),user,invoice.getTotalAmount(),items,client,clientAddress);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                return true;
            } else if ("Quote".equals(caiqi.getType())) {
                Quote quote = caiqi.getQuote();
                quote.setUser(user);
                quote.setDate(LocalDate.now());

                List<Items> items = caiqi.getItems();
                for (Items item : items) {
                    item.setQuote(quote);
                    total += item.getPrice() * item.getQty();
                }

                quote.setTotalAmount(total);
                quoteRepo.save(quote);

                for (Items item : items) {
                    item.setQuote(quote);
                    itemRepo.save(item);
                }
                // You may want to uncomment the PDF generation code
                // gPdf.generatePdf(caiqi.getType(), quote.getDate(), user, quote, clientAddress);
                return true;
            }
        }
        return false;
    }


    @Override
    public List<Invoice> getAllInvoices(String email) {

        try
        {
            return invoiceRepo.findByUserEmail(email);
        }
        catch (Exception e)
        {
            return Collections.emptyList();
        }
    }



    public List<Quote> getAllQuote(String email)
    {

        try
        {

            return quoteRepo.findByUserEmail(email);
        }
        catch (Exception e)
        {
            return Collections.emptyList();
        }
    }


    @Override
    public Invoice searchInvoice(int id, String email) {
        // Find the user by email
        Optional<User> user = userRepo.findByEmail(email);

        // Check if the user is present
        if(user.isPresent()) {
            // Retrieve the user object from the optional
            User foundUser = user.get();

            // Search for the invoice by ID and associated user
            return invoiceRepo.findByInvoiceIdAndUser(id, foundUser);
        } else {
            // If the user is not found, return null
            return null;
        }
    }




}

package com.helloIftekhar.springJwt.controller;

import com.helloIftekhar.springJwt.model.*;
import com.helloIftekhar.springJwt.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
@RestController
@CrossOrigin(origins = "http://localhost:4200") // Remove trailing slash
@RequestMapping("/user")
public class Control {
    @Autowired
    private Interface appService;
    @Autowired
    private Impl impl;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private QuoteService quoteService;

    @Autowired
    private ClientService clientService;
    @PostMapping("/createInvoiceOrQuote")
    public ResponseEntity<Boolean> createInvoiceOrQuote(@RequestParam String email, @RequestBody ClientAddressInvoiceQuoteItems caiqi) throws FileNotFoundException {
        boolean check = appService.createInvoiceOrQuote(email, caiqi);
        if (check) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }


    @DeleteMapping("/searchInvoice")
    public ResponseEntity<Invoice> searchInvoice(@RequestParam int id, @RequestParam String email) {
        Invoice invoice = appService.searchInvoice(id, email);

        if (invoice != null) {
            return ResponseEntity.ok(invoice);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/homeQuotes")
    public ResponseEntity<List<Quote>> display5QuoteOnHome(@RequestParam String email) {
        List<Quote> quotes = appService.homeTop5Quote(email);

        if (quotes != null) {
            return ResponseEntity.ok(quotes);
        } else {
            return ResponseEntity.ok(null);
        }
    }

    @GetMapping("/homeInvoices")
    public ResponseEntity<List<Invoice>> display5InvoicesHome(@RequestParam String email) {
        List<Invoice> invoices = appService.homeTop5Invoice(email);

        if (invoices != null) {
            return ResponseEntity.ok(invoices);
        } else {
            return ResponseEntity.ok(null);
        }
    }

    @GetMapping("/noti")
    public ResponseEntity<List<Invoice>> display1InvoicesHome(@RequestParam String email) {
        List<Invoice> invoices = appService.homeTop1Invoice(email);

        if (invoices != null) {
            return ResponseEntity.ok(invoices);
        } else {
            return ResponseEntity.ok(null);
        }
    }

    @GetMapping("/displayAllInvoices")
    public ResponseEntity<List<Invoice>> getAllInvoice(@RequestParam String email) {
        List<Invoice> allInvoice = appService.getAllInvoices(email);

        if (allInvoice != null) {
            return ResponseEntity.ok(allInvoice);
        } else {
            return ResponseEntity.ok(null);
        }
    }

    @GetMapping("/getTotalUnpaid")
    public double totalAmountofInvoices(@RequestParam String email) {
        return appService.invoiceTotalAmt(email);
    }

    @GetMapping("/displayAllQuote")
    public ResponseEntity<List<Quote>> getAllQuote(@RequestParam String email) {
        List<Quote> allQuote = appService.getAllQuote(email);
        if (allQuote != null && !allQuote.isEmpty()) {
            return ResponseEntity.ok(allQuote);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getNotificationsByEmail(@RequestParam String email) {
        try {
            // Retrieve notifications based on the provided email
            List<Notification> notifications = notificationService.getNotificationsByEmail(email);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

    @GetMapping("/invoice/{invoiceNo}")
    public ResponseEntity<Invoice> getInvoiceDetails(@PathVariable int invoiceNo) {
        Invoice invoice = invoiceService.getInvoiceByNo(invoiceNo);
        if (invoice != null) {
            return new ResponseEntity<>(invoice, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @GetMapping("/quote/{quoteNo}")
    public ResponseEntity<Quote> getQuoteDetails(@PathVariable int quoteNo) {
        Quote quote = quoteService.getQuoteByNo(quoteNo);
        if (quote != null) {

            return new ResponseEntity<>(quote, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PostMapping("/quotes/{quoteNo}/update")
    public ResponseEntity<?> updateQuote(
            @PathVariable("quoteNo") Long quoteId,
            @RequestParam("email") String email,
            @RequestBody ClientAddressInvoiceQuoteItems caiqi) {

        try {
            boolean updated = appService.updateQuote(email, caiqi, quoteId);
            if (updated) {
                return ResponseEntity.ok().body("{\"message\": \"Quote updated successfully\"}");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"error\": \"Quote not found or user not authorized\"}");
            }
        } catch (FileNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Error updating quote: " + e.getMessage() + "\"}");
        }
    }
    }




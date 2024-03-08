package com.helloIftekhar.springJwt.controller;

import com.helloIftekhar.springJwt.model.ClientAddressInvoiceQuoteItems;
import com.helloIftekhar.springJwt.model.Invoice;
import com.helloIftekhar.springJwt.model.Quote;
import com.helloIftekhar.springJwt.service.Interface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;
@RestController
@CrossOrigin(origins = "http://localhost:4200") // Remove trailing slash
@RequestMapping("/user")
public class Control {
    @Autowired
    private Interface appService;

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
}
    // Other methods remain the same, just add @RequestParam String email where needed.



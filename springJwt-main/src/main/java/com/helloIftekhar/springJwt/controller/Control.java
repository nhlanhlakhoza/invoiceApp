package com.helloIftekhar.springJwt.controller;



import com.helloIftekhar.springJwt.model.ClientAddressInvoiceQuoteItems;
import com.helloIftekhar.springJwt.model.Invoice;
import com.helloIftekhar.springJwt.model.Quote;
import com.helloIftekhar.springJwt.model.User;
import com.helloIftekhar.springJwt.service.Interface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200") // Remove trailing slash
@RequestMapping("/user")
public class Control implements ErrorController {

    @Autowired
    private Interface appService;

    private String email="nhlanhlakhoza05@gmail.com";

    @PostMapping("/createInvoiceOrQuote")
    public ResponseEntity<Boolean> createInvoiceOrQuote(@RequestBody ClientAddressInvoiceQuoteItems caiqi)
    {
        boolean check = appService.createInvoiceOrQuote(email,caiqi);
        if(check)
        {
            return ResponseEntity.ok(true);
        }
        else {
            return ResponseEntity.badRequest().body(false);
        }

    }
    @DeleteMapping("/searchInvoice")
    public ResponseEntity<Invoice> searchInvoice(@RequestParam int id)
    {
        Invoice invoice = appService.searchInvoice(id,email);

                if(invoice!=null)
                {
                    return ResponseEntity.ok(invoice);
                }
                else
                {
                    return ResponseEntity.notFound().build();
                }
    }

    @GetMapping("/homeQuotes")
    public ResponseEntity<List<Quote>> display5QuoteOnHome()
    {
        List<Quote> quotes = appService.homeTop5Quote(email);

        if(quotes!=null)
        {
            return ResponseEntity.ok(quotes);
        }
        else
        {
            return ResponseEntity.ok(null);
        }
    }

    @GetMapping("/homeInvoices")
    public ResponseEntity<List<Invoice>> display5InvoicesHome()
    {
        List<Invoice> invoices = appService.homeTop5Invoice(email);

        if(invoices!=null)
        {
            return ResponseEntity.ok(invoices);
        }
        else
        {
            return ResponseEntity.ok(null);
        }
    }



    @GetMapping("/displayAllInvoices")
    public ResponseEntity<List<Invoice>> getAllInvoice()
    {
        List<Invoice> allInvoice = appService.getAllInvoices(email);

        if(allInvoice!=null)
        {
            return ResponseEntity.ok(allInvoice);
        }
        else {
            return ResponseEntity.ok(null);
        }
    }
}

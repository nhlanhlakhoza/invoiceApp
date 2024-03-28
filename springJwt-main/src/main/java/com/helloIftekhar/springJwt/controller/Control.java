package com.helloIftekhar.springJwt.controller;

import com.helloIftekhar.springJwt.model.*;
import com.helloIftekhar.springJwt.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
    private PaidNotificationService paidNotificationService; // Inject the PaidNotificationService bean
    @Autowired
    private ClientService clientService;

    public Control(InvoiceServices invoiceServices) {
        this.invoiceServices = invoiceServices;
    }

    @PostMapping("/createInvoiceOrQuote")
    public ResponseEntity<Boolean> createInvoiceOrQuote(@RequestParam String email, @RequestBody ClientAddressInvoiceQuoteItems caiqi) throws IOException {
        System.out.print("Email "+ SecurityContextHolder.getContext().getAuthentication().getName());

        boolean check = appService.createInvoiceOrQuote(email, caiqi);
        if (check) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.badRequest().body(false);
        }
    }



    @GetMapping("/{email}/{invoiceNo}/{amount}")
    public ResponseEntity<byte[]> changeStatusAndSendNotification(@PathVariable("email") String email, @PathVariable("invoiceNo") int invoiceNo,@PathVariable("amount") double amount) {
        try {
            // Apply change status operation
            appService.changeStatus(email, invoiceNo,amount);

            // Send notification using PaidNotificationService
            paidNotificationService.sendNotification(email, "Invoice number #" + invoiceNo + " has been successfully Paid.", null, null);

            // Return response
            InputStream inputStream = getClass().getResourceAsStream("/images/images.png");
            byte[] imageBytes = StreamUtils.copyToByteArray(inputStream);
            String successMessage = "<div style='display: flex; justify-content: center; align-items: center; height: 100vh; flex-direction: column;'><img src='data:image/png;base64," + java.util.Base64.getEncoder().encodeToString(imageBytes) + "' alt='Success Icon' style='width: 100px; height: 100px; margin-bottom: 10px;'><p style='text-align: center; margin-top: 0; font-size: 24px;'><strong>Payment successful!</strong><br><span style='font-size: 18px;'>Your payment has been completed</span></p><button onclick='closeBrowser()' style='margin-top: 4px; background-color: green; color: white; border: none; padding: 10px 20px; cursor: pointer;'>Finish</button></div><script>function closeBrowser() { window.close(); }</script>";
            return ResponseEntity.ok().body(successMessage.getBytes());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to change status".getBytes());
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
        String paymentStatus = "unpaid"; // Assuming "unpaid" is the payment status for unpaid invoices
        List<Invoice> top5UnpaidInvoices = appService.getTop5UnpaidInvoicesByEmail(email, paymentStatus);

        if (top5UnpaidInvoices.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(top5UnpaidInvoices);
    }



    @GetMapping("/email/{email}")
    public ResponseEntity<BusinessInfo> getAllBusinessInfoForUser(@PathVariable("email") String email) {
        BusinessInfo AllbusinessInfo = appService.getAllBusinessInfo(email);
        if (AllbusinessInfo != null) {
            return ResponseEntity.ok(AllbusinessInfo);
        }else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/getBalance")
    public ResponseEntity<Double> getBalance(@RequestParam String email)
    {
        return ResponseEntity.ok(appService.getBalance(email));
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
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"Error updating quote: " + e.getMessage() + "\"}");
        }
    }

    private final InvoiceServices invoiceServices;

    @Autowired
    public Control(InvoiceService invoiceService, InvoiceServices invoiceServices) {
        this.invoiceService = invoiceService;
        this.invoiceServices = invoiceServices;
    }

    @GetMapping("/{userEmail}/payment-status/{paymentStatus}")
    public List<Invoice> getInvoicesByUserEmailAndPaymentStatus(@PathVariable String userEmail, @PathVariable String paymentStatus) {
        return invoiceServices.getInvoicesByUserEmailAndPaymentStatus(userEmail, paymentStatus);
    }
    }




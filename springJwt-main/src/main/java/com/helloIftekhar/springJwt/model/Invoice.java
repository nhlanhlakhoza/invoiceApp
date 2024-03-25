package com.helloIftekhar.springJwt.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Invoice implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int invoiceId;
    private double totalAmount;
    @JsonFormat(pattern = "M/d/yyyy HH:mm")
    private LocalDateTime date;
    private int invoiceNo;
    private String paymentStatus;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<com.helloIftekhar.springJwt.model.Items> Items;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;
    @ManyToOne
    @JoinColumn(name = "client_id")

    private Client client;

    @ManyToOne
    @JoinColumn(name = "client_address_id")
    private ClientAddress clientAddress;
    public Invoice() {
        this.paymentStatus = "unpaid"; // Setting default payment status
    }

    public Invoice(int invoiceId, double totalAmount, LocalDateTime date, int invoiceNo, String paymentStatus, List<com.helloIftekhar.springJwt.model.Items> items, User user,Client client,ClientAddress clientAddress) {
        this.invoiceId = invoiceId;
        this.totalAmount = totalAmount;
        this.date = date;
        this.invoiceNo = invoiceNo;
        this.paymentStatus =paymentStatus;
        Items = items;
        this.user = user;
        this.client=client;
        this.clientAddress=clientAddress;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public String isPaymentStatus() {
        return paymentStatus;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ClientAddress getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(ClientAddress clientAddress) {
        this.clientAddress = clientAddress;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<com.helloIftekhar.springJwt.model.Items> getItems() {
        return Items;
    }

    public void setItems(List<com.helloIftekhar.springJwt.model.Items> items) {
        Items = items;
    }

    public int getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(int invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

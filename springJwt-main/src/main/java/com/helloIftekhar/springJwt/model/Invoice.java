package com.helloIftekhar.springJwt.model;


import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Invoice implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int invoiceId;
    private double totalAmount;
    @JsonFormat(pattern = "M/d/yyyy")

    private LocalDate date;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Items> Items;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    public Invoice() {
    }

    public Invoice(int invoiceId, double totalAmount, LocalDate date, List<com.helloIftekhar.springJwt.model.Items> items, User user) {
        this.invoiceId = invoiceId;
        this.totalAmount = totalAmount;
        this.date = date;
        Items = items;
        this.user = user;
    }

    public int getInvoiceId() {
        return invoiceId;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }


    public List<com.helloIftekhar.springJwt.model.Items> getItems() {
        return Items;
    }

    public void setItems(List<com.helloIftekhar.springJwt.model.Items> items) {
        Items = items;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

package com.helloIftekhar.springJwt.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Quote implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private double totalAmount;
    @JsonFormat(pattern = "M/d/yyyy HH:mm")
    private LocalDateTime date;
    private int quoteNo;

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL)
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
    public Quote() {
    }

    public Quote(int id, double totalAmount, LocalDateTime date, int quoteNo,List<com.helloIftekhar.springJwt.model.Items> items, User user,Client client,ClientAddress clientAddress) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.date = date;
        this.quoteNo = quoteNo;
        this.Items = items;
        this.user = user;
        this.client=client;
        this.clientAddress=clientAddress;
    }

    public Client getClient() {
        return client;
    }

    public ClientAddress getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(ClientAddress clientAddress) {
        this.clientAddress = clientAddress;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public List<Items> getItems() {
        return Items;
    }

    public void setItems(List<Items> items) {
        this.Items = items;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getQuoteNo() {
        return quoteNo;
    }

    public void setQuoteNo(int quoteNo) {
        this.quoteNo = quoteNo;
    }


}
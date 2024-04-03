package com.helloIftekhar.springJwt.model;

import java.io.Serializable;

public class RegisterUserAndCompany implements Serializable {
    private User user;
    private  BusinessInfo businessInfo;

    public RegisterUserAndCompany(User user, BusinessInfo businessInfo) {
        this.user = user;
        this.businessInfo = businessInfo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BusinessInfo getBusinessInfo() {
        return businessInfo;
    }

    public void setBusinessInfo(BusinessInfo businessInfo) {
        this.businessInfo = businessInfo;
    }
}

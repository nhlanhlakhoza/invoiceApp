package com.helloIftekhar.springJwt.repository;


import com.helloIftekhar.springJwt.model.BusinessInfo;
import com.helloIftekhar.springJwt.model.Invoice;
import com.helloIftekhar.springJwt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BusinessInfoRepository extends JpaRepository<BusinessInfo,Integer> {

    BusinessInfo   findByUser(User user);
    BusinessInfo   findByUserEmail(String userEmail);
}

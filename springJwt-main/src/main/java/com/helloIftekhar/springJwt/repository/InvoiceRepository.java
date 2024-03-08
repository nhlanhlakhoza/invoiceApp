package com.helloIftekhar.springJwt.repository;


import com.helloIftekhar.springJwt.model.Invoice;
import com.helloIftekhar.springJwt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {

    long countByUser(User user);

    List<Invoice> findByUserEmail(String userEmail);

    List<Invoice> findTop5ByUserOrderByDateDesc(User user);

    Invoice findByInvoiceIdAndUser(int invoiceId, User user);


}

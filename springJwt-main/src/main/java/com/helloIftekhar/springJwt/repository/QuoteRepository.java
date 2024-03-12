package com.helloIftekhar.springJwt.repository;


import com.helloIftekhar.springJwt.model.Quote;
import com.helloIftekhar.springJwt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuoteRepository extends JpaRepository<Quote, Integer> {

    List<Quote> findByUserEmail(String userEmail);

    List<Quote> findTop5ByUserOrderByIdDesc(User user);

    boolean existsByQuoteNo(int quoteNo);

    Quote findByQuoteNoAndUser(int quoteNo, User user);
    Quote findByQuoteNo(int quoteNo);

}

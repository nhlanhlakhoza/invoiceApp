package com.helloIftekhar.springJwt.repository;


import com.helloIftekhar.springJwt.model.Items;
import com.helloIftekhar.springJwt.model.Quote;
import com.helloIftekhar.springJwt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuoteRepository extends JpaRepository<Quote, Integer> {

    List<Quote> findByUserEmail(String userEmail);

    List<Quote> findTop5ByUserOrderByIdDesc(User user);

    boolean existsByQuoteNo(int quoteNo);

    Quote findByQuoteNoAndUser(int quoteNo, User user);

    Quote findByQuoteNo(int quoteNo);
    Optional<Quote> findByQuoteNo(Long quoteNo);
    Optional<Quote> findById(Long id);
}

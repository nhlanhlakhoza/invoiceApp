package com.helloIftekhar.springJwt.repository;


import com.helloIftekhar.springJwt.model.Items;
import com.helloIftekhar.springJwt.model.Quote;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ItemsRepository extends JpaRepository<Items, Integer> {


    void deleteAllByQuote(Quote quote);
    @Transactional
    @Modifying
    @Query("DELETE FROM Items i WHERE i.quote.quoteNo = :quoteNo")
    void deleteByQuoteNo(@Param("quoteNo") Long quoteNo);
}

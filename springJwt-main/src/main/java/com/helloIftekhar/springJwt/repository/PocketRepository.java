package com.helloIftekhar.springJwt.repository;

import com.helloIftekhar.springJwt.model.Items;
import com.helloIftekhar.springJwt.model.Pocket;
import com.helloIftekhar.springJwt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PocketRepository extends JpaRepository<Pocket, Integer> {

    @Query("SELECT p.balance FROM Pocket p WHERE p.user.email = :email")
    Double findBalanceByUserEmail(@Param("email") String email);

    Pocket findByUser(User user);
}

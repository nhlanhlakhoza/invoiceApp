package com.helloIftekhar.springJwt.repository;


import com.helloIftekhar.springJwt.model.Items;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemsRepository extends JpaRepository<Items, Integer> {


}

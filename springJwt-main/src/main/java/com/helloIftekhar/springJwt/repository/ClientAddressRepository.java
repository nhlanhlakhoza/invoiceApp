package com.helloIftekhar.springJwt.repository;


import com.helloIftekhar.springJwt.model.ClientAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientAddressRepository extends JpaRepository<ClientAddress, Integer> {
}

package com.helloIftekhar.springJwt.repository;


import com.helloIftekhar.springJwt.model.BusinessInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessInfoRepository extends JpaRepository<BusinessInfo,Integer> {


}

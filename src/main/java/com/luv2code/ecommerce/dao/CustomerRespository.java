package com.luv2code.ecommerce.dao;

import com.luv2code.ecommerce.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRespository extends JpaRepository<Customer,Long> {
}

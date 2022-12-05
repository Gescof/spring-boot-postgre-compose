package com.gescof.springbootpostgrecompose.persistence.repos;

import com.gescof.springbootpostgrecompose.persistence.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomersRepository extends JpaRepository<Customer, Long> {
}

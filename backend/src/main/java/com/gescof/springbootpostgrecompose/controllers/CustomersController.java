package com.gescof.springbootpostgrecompose.controllers;

import com.gescof.springbootpostgrecompose.models.CustomerRequest;
import com.gescof.springbootpostgrecompose.models.CustomerResponse;
import com.gescof.springbootpostgrecompose.services.CustomersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Log4j2
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/customers/")
public class CustomersController {
    private final CustomersService customersService;

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getCustomers() {
        log.info("GET /customers");
        return ResponseEntity.ok(customersService.getCustomers());
    }

    @PostMapping
    public ResponseEntity<Long> createCustomer(@RequestBody CustomerRequest customerRequest) {
        log.info("POST /customers");
        return ResponseEntity.ok(customersService.createCustomer(customerRequest));
    }

    @PutMapping("{customerId}")
    public ResponseEntity<Long> updateCustomer(@PathVariable Long customerId,
                                               @RequestBody CustomerRequest customerRequest) {
        log.info("PUT /customers/{}", customerId);
        return ResponseEntity.ok(customersService.updateCustomer(customerId, customerRequest));
    }

    @DeleteMapping("{customerId}")
    public ResponseEntity<Boolean> deleteCustomer(@PathVariable Long customerId) {
        log.info("DELETE /customers/{}", customerId);
        return ResponseEntity.ok(customersService.deleteCustomer(customerId));
    }
}

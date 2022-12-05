package com.gescof.springbootpostgrecompose.services;

import com.gescof.springbootpostgrecompose.exceptions.CustomersNotFoundException;
import com.gescof.springbootpostgrecompose.models.CustomerRequest;
import com.gescof.springbootpostgrecompose.models.CustomerResponse;
import com.gescof.springbootpostgrecompose.persistence.entities.Customer;
import com.gescof.springbootpostgrecompose.persistence.repos.CustomersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
public class CustomersService {
    private final CustomersRepository customersRepository;

    public List<CustomerResponse> getCustomers() {
        log.debug("Starting getCustomers service");
        var customersList = customersRepository.findAll();
        if (customersList.isEmpty()) {
            throw new CustomersNotFoundException();
        }
        var customersResponseList = new ArrayList<CustomerResponse>();
        customersList.forEach(customer -> {
            var customerResponse = mapCustomerEntityToResponse(customer);
            customersResponseList.add(customerResponse);
        });
        return customersResponseList;
    }

    public Long createCustomer(CustomerRequest customerRequest) {
        log.debug("Starting createCustomer service");
        var newCustomer = new Customer();
        BeanUtils.copyProperties(customerRequest, newCustomer);
        return customersRepository.save(newCustomer).getId();
    }

    public Long updateCustomer(Long customerId, CustomerRequest customerRequest) {
        log.debug("Starting updateCustomer service");
        var foundCustomer = customersRepository.findById(customerId).orElseThrow(CustomersNotFoundException::new);
        BeanUtils.copyProperties(customerRequest, foundCustomer);
        return customersRepository.save(foundCustomer).getId();
    }

    public Boolean deleteCustomer(Long customerId) {
        log.debug("Starting deleteCustomer service");
        try {
            customersRepository.deleteById(customerId);
        } catch (EmptyResultDataAccessException emptyResultDataAccessException) {
            throw new CustomersNotFoundException();
        }
        return true;
    }

    private static CustomerResponse mapCustomerEntityToResponse(Customer customer) {
        var customerResponse = CustomerResponse.builder().build();
        BeanUtils.copyProperties(customer, customerResponse);
        return customerResponse;
    }
}

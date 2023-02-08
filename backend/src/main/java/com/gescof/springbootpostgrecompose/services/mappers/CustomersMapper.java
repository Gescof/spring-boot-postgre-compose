package com.gescof.springbootpostgrecompose.services.mappers;

import com.gescof.springbootpostgrecompose.models.CustomerRequest;
import com.gescof.springbootpostgrecompose.models.CustomerResponse;
import com.gescof.springbootpostgrecompose.persistence.entities.Customer;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface CustomersMapper {
    Customer customerRequestToCustomerEntity(CustomerRequest customerRequest);

    List<CustomerResponse> customerEntityListToCustomerResponseList(List<Customer> customer);
}

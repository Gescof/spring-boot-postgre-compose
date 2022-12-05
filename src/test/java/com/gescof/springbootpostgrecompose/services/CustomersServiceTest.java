package com.gescof.springbootpostgrecompose.services;

import com.gescof.springbootpostgrecompose.exceptions.CustomersNotFoundException;
import com.gescof.springbootpostgrecompose.models.CustomerRequest;
import com.gescof.springbootpostgrecompose.models.CustomerResponse;
import com.gescof.springbootpostgrecompose.persistence.entities.Customer;
import com.gescof.springbootpostgrecompose.persistence.repos.CustomersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class CustomersServiceTest {
    @InjectMocks
    private CustomersService customersService;

    @Mock
    private CustomersRepository customersRepositoryMock;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCustomers_existing_shouldReturnList() {
        final Customer mockedCustomer = Customer.builder()
                .id(1L).name("Name").email("email@test.com").age(27).build();
        final List<Customer> mockedCustomerResponseList = List.of(mockedCustomer);
        doReturn(mockedCustomerResponseList).when(customersRepositoryMock).findAll();

        final List<CustomerResponse> result = customersService.getCustomers();

        verify(customersRepositoryMock, times(1)).findAll();
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Name", result.get(0).getName());
        assertEquals("email@test.com", result.get(0).getEmail());
        assertEquals(27, result.get(0).getAge());
    }

    @Test
    void getCustomers_nonExisting_shouldThrowCustomersNotFoundException() {
        doReturn(new ArrayList<>()).when(customersRepositoryMock).findAll();

        assertThrows(CustomersNotFoundException.class, () -> customersService.getCustomers());

        verify(customersRepositoryMock, times(1)).findAll();
    }

    @Test
    void createCustomer_shouldReturnNewId() {
        final CustomerRequest mockedCustomerRequest = CustomerRequest.builder()
                .name("Name").email("email@test.com").age(27).build();
        final Customer mockedCustomer = Customer.builder()
                .id(1L).name("Name").email("email@test.com").age(27)
                .creationDate(LocalDateTime.now(Clock.fixed(Instant.parse("2022-12-03T10:15:30.00Z"), ZoneId.systemDefault())))
                .lastModificationDate(LocalDateTime.now(Clock.fixed(Instant.parse("2022-12-03T10:15:30.00Z"), ZoneId.systemDefault())))
                .build();
        doReturn(mockedCustomer).when(customersRepositoryMock).save(any(Customer.class));

        final Long result = customersService.createCustomer(mockedCustomerRequest);

        verify(customersRepositoryMock, times(1)).save(any(Customer.class));
        assertEquals(1, result);
    }

    @Test
    void updateCustomer_existing_shouldReturnExistingId() {
        final CustomerRequest mockedCustomerRequest = CustomerRequest.builder()
                .name("Name").email("email-mod@test.com").age(28).build();
        final Optional<Customer> mockedCustomerToUpdate = Optional.ofNullable(
                Customer.builder()
                        .id(1L).name("Name").email("email@test.com").age(27)
                        .creationDate(LocalDateTime.now(Clock.fixed(Instant.parse("2022-12-03T10:15:30.00Z"), ZoneId.systemDefault())))
                        .lastModificationDate(LocalDateTime.now(Clock.fixed(Instant.parse("2022-12-03T10:15:30.00Z"), ZoneId.systemDefault())))
                        .build());
        final Customer mockedCustomerUpdated = Customer.builder()
                .id(1L).name("Name").email("email-mod@test.com").age(28)
                .creationDate(LocalDateTime.now(Clock.fixed(Instant.parse("2022-12-03T10:15:30.00Z"), ZoneId.systemDefault())))
                .lastModificationDate(LocalDateTime.now(Clock.fixed(Instant.parse("2023-01-17T08:58:01.00Z"), ZoneId.systemDefault())))
                .build();
        doReturn(mockedCustomerToUpdate).when(customersRepositoryMock).findById(1L);
        doReturn(mockedCustomerUpdated).when(customersRepositoryMock).save(any(Customer.class));

        final Long result = customersService.updateCustomer(1L, mockedCustomerRequest);

        verify(customersRepositoryMock, times(1)).findById(1L);
        verify(customersRepositoryMock, times(1)).save(any(Customer.class));
        assertEquals(1, result);
    }

    @Test
    void updateCustomer_nonExisting_shouldThrowCustomerNotFoundException() {
        final CustomerRequest mockedCustomerRequest = CustomerRequest.builder()
                .name("Name").email("email-mod@test.com").age(28).build();
        final Optional<Customer> mockedCustomerEmpty = Optional.empty();
        doReturn(mockedCustomerEmpty).when(customersRepositoryMock).findById(1L);

        assertThrows(CustomersNotFoundException.class, () -> customersService.updateCustomer(1L, mockedCustomerRequest));

        verify(customersRepositoryMock, times(1)).findById(1L);
        verify(customersRepositoryMock, times(0)).save(any(Customer.class));
    }

    @Test
    void deleteCustomer_existing_shouldReturnTrue() {
        doNothing().when(customersRepositoryMock).deleteById(1L);

        final Boolean result = customersService.deleteCustomer(1L);

        verify(customersRepositoryMock, times(1)).deleteById(1L);
        assertEquals(true, result);
    }

    @Test
    void deleteCustomer_nonExisting_shouldCatchEmptyResultDataAccessException_andThrowCustomerNotFoundException() {
        doThrow(EmptyResultDataAccessException.class).when(customersRepositoryMock).deleteById(1L);

        assertThrows(CustomersNotFoundException.class, () -> customersService.deleteCustomer(1L));

        verify(customersRepositoryMock, times(1)).deleteById(1L);
    }
}

package com.gescof.springbootpostgrecompose.services;

import com.gescof.springbootpostgrecompose.exceptions.CustomersNotFoundException;
import com.gescof.springbootpostgrecompose.models.CustomerRequest;
import com.gescof.springbootpostgrecompose.models.CustomerResponse;
import com.gescof.springbootpostgrecompose.persistence.entities.Customer;
import com.gescof.springbootpostgrecompose.persistence.repos.CustomersRepository;
import com.gescof.springbootpostgrecompose.services.mappers.CustomersMapper;
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

import static org.junit.jupiter.api.Assertions.assertAll;
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
    private CustomersMapper customersMapper;
    @Mock
    private CustomersRepository customersRepositoryMock;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCustomers_existing_shouldReturnList() {
        // Given
        final Customer mockedCustomerEntity = Customer.builder()
                .id(1L).name("Name").email("email@test.com").age(27).build();
        final CustomerResponse mockedCustomerResponse = CustomerResponse.builder()
                .id(1L).name("Name").email("email@test.com").age(27).build();
        final List<Customer> mockedCustomerEntityList = List.of(mockedCustomerEntity);
        final List<CustomerResponse> mockedCustomerResponseList = List.of(mockedCustomerResponse);

        // When
        doReturn(mockedCustomerEntityList).when(customersRepositoryMock).findAll();
        doReturn(mockedCustomerResponseList).when(customersMapper).customerEntityListToCustomerResponseList(mockedCustomerEntityList);

        // Then
        final List<CustomerResponse> result = customersService.getCustomers();

        // Assert
        verify(customersRepositoryMock, times(1)).findAll();
        verify(customersMapper, times(1)).customerEntityListToCustomerResponseList(mockedCustomerEntityList);
        assertAll("Customer response list should contain valid properties",
                () -> assertEquals(mockedCustomerResponseList.size(), result.size()),
                () -> assertEquals(mockedCustomerResponseList.get(0).getId(), result.get(0).getId()),
                () -> assertEquals(mockedCustomerResponseList.get(0).getName(), result.get(0).getName()),
                () -> assertEquals(mockedCustomerResponseList.get(0).getEmail(), result.get(0).getEmail()),
                () -> assertEquals(mockedCustomerResponseList.get(0).getAge(), result.get(0).getAge())
        );
    }

    @Test
    void getCustomers_nonExisting_shouldThrowCustomersNotFoundException() {
        // When
        doReturn(new ArrayList<>()).when(customersRepositoryMock).findAll();

        // Then
        assertThrows(CustomersNotFoundException.class, () -> customersService.getCustomers());

        // Assert
        verify(customersRepositoryMock, times(1)).findAll();
    }

    @Test
    void createCustomer_shouldReturnNewId() {
        // Given
        final CustomerRequest mockedCustomerRequest = CustomerRequest.builder()
                .name("Name").email("email@test.com").age(27).build();
        final Customer mockedCustomer = Customer.builder()
                .id(1L).name("Name").email("email@test.com").age(27)
                .creationDate(LocalDateTime.now(Clock.fixed(Instant.parse("2022-12-03T10:15:30.00Z"), ZoneId.systemDefault())))
                .lastModificationDate(LocalDateTime.now(Clock.fixed(Instant.parse("2022-12-03T10:15:30.00Z"), ZoneId.systemDefault())))
                .build();

        // When
        doReturn(mockedCustomer).when(customersMapper).customerRequestToCustomerEntity(mockedCustomerRequest);
        doReturn(mockedCustomer).when(customersRepositoryMock).save(mockedCustomer);

        // Then
        final Long result = customersService.createCustomer(mockedCustomerRequest);

        // Assert
        verify(customersMapper, times(1)).customerRequestToCustomerEntity(mockedCustomerRequest);
        verify(customersRepositoryMock, times(1)).save(mockedCustomer);
        assertEquals(1, result);
    }

    @Test
    void updateCustomer_existing_shouldReturnExistingId() {
        // Given
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

        // When
        doReturn(mockedCustomerToUpdate).when(customersRepositoryMock).findById(1L);
        doReturn(mockedCustomerUpdated).when(customersRepositoryMock).save(any(Customer.class));

        // Then
        final Long result = customersService.updateCustomer(1L, mockedCustomerRequest);

        // Assert
        verify(customersRepositoryMock, times(1)).findById(1L);
        verify(customersRepositoryMock, times(1)).save(any(Customer.class));
        assertEquals(1, result);
    }

    @Test
    void updateCustomer_nonExisting_shouldThrowCustomerNotFoundException() {
        // Given
        final CustomerRequest mockedCustomerRequest = CustomerRequest.builder()
                .name("Name").email("email-mod@test.com").age(28).build();
        final Optional<Customer> mockedCustomerEmpty = Optional.empty();

        // When
        doReturn(mockedCustomerEmpty).when(customersRepositoryMock).findById(1L);

        // Then
        assertThrows(CustomersNotFoundException.class, () -> customersService.updateCustomer(1L, mockedCustomerRequest));

        // Assert
        verify(customersRepositoryMock, times(1)).findById(1L);
        verify(customersRepositoryMock, times(0)).save(any(Customer.class));
    }

    @Test
    void deleteCustomer_existing_shouldReturnTrue() {
        // When
        doNothing().when(customersRepositoryMock).deleteById(1L);

        // Then
        final Boolean result = customersService.deleteCustomer(1L);

        // Assert
        verify(customersRepositoryMock, times(1)).deleteById(1L);
        assertEquals(true, result);
    }

    @Test
    void deleteCustomer_nonExisting_shouldCatchEmptyResultDataAccessException_andThrowCustomerNotFoundException() {
        // When
        doThrow(EmptyResultDataAccessException.class).when(customersRepositoryMock).deleteById(1L);

        // Then
        assertThrows(CustomersNotFoundException.class, () -> customersService.deleteCustomer(1L));

        // Assert
        verify(customersRepositoryMock, times(1)).deleteById(1L);
    }
}

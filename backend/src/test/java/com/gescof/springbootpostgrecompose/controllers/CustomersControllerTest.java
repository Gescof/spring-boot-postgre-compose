package com.gescof.springbootpostgrecompose.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gescof.springbootpostgrecompose.advisors.CustomersControllerAdvisor;
import com.gescof.springbootpostgrecompose.exceptions.CustomersNotFoundException;
import com.gescof.springbootpostgrecompose.models.CustomerRequest;
import com.gescof.springbootpostgrecompose.models.CustomerResponse;
import com.gescof.springbootpostgrecompose.services.CustomersService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MimeTypeUtils;

import java.time.Clock;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        CustomersController.class
})
@ActiveProfiles("test")
public class CustomersControllerTest {
    @Autowired
    @InjectMocks
    private CustomersController customersController;

    @MockBean
    private CustomersService customersServiceMock;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(customersController)
                .setControllerAdvice(new CustomersControllerAdvisor(Clock.systemDefaultZone()))
                .build();
    }

    @Test
    public void getCustomers_one_shouldReturnOk() throws Exception {
        // Given
        final CustomerResponse mockedCustomerResponse = CustomerResponse.builder()
                .id(1L).name("Name").email("email@test.com").age(27).build();
        final List<CustomerResponse> mockedCustomerResponseList = List.of(mockedCustomerResponse);

        // When
        doReturn(mockedCustomerResponseList).when(customersServiceMock).getCustomers();

        // Assert
        mockMvc.perform(get("/api/v1/customers/")
                        .accept(MimeTypeUtils.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(mockedCustomerResponseList.size()))
                .andExpect(jsonPath("$[?(@.id === 1)]").exists())
                .andExpect(jsonPath("$[?(@.name === 'Name')]").exists())
                .andExpect(jsonPath("$[?(@.email === 'email@test.com')]").exists())
                .andExpect(jsonPath("$[?(@.age === 27)]").exists());
    }

    @Test
    public void getCustomers_empty_shouldReturnNotFound() throws Exception {
        // When
        doThrow(CustomersNotFoundException.class).when(customersServiceMock).getCustomers();

        // Assert
        mockMvc.perform(get("/api/v1/customers/")
                        .accept(MimeTypeUtils.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomersNotFoundException));
    }

    @Test
    public void createCustomer_shouldReturnOk() throws Exception {
        // Given
        final CustomerRequest mockedCustomerRequest = CustomerRequest.builder()
                .name("Name").email("email@test.com").age(27).build();

        // When
        doReturn(1L).when(customersServiceMock).createCustomer(mockedCustomerRequest);

        // Assert
        mockMvc.perform(post("/api/v1/customers/")
                        .accept(MimeTypeUtils.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(mockedCustomerRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    public void updateCustomer_one_shouldReturnOk() throws Exception {
        // Given
        final CustomerRequest mockedCustomerRequest = CustomerRequest.builder()
                .name("Name").email("email@test.com").age(27).build();

        // When
        doReturn(1L).when(customersServiceMock).updateCustomer(1L, mockedCustomerRequest);

        // Assert
        mockMvc.perform(put("/api/v1/customers/{customerId}", 1)
                        .accept(MimeTypeUtils.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(mockedCustomerRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    public void updateCustomer_nonExisting_shouldReturnNotfound() throws Exception {
        // Given
        final CustomerRequest mockedCustomerRequest = CustomerRequest.builder()
                .name("Non Existing").email("non-existing@test.com").age(42).build();

        // When
        doThrow(CustomersNotFoundException.class).when(customersServiceMock).updateCustomer(1L, mockedCustomerRequest);

        // Assert
        mockMvc.perform(put("/api/v1/customers/{customerId}", 1)
                        .accept(MimeTypeUtils.APPLICATION_JSON_VALUE)
                        .content(new ObjectMapper().writeValueAsString(mockedCustomerRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomersNotFoundException));
    }

    @Test
    public void deleteCustomer_one_shouldReturnOk() throws Exception {
        // When
        doReturn(true).when(customersServiceMock).deleteCustomer(1L);

        // Assert
        mockMvc.perform(delete("/api/v1/customers/{customerId}", 1)
                        .accept(MimeTypeUtils.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    public void deleteCustomer_nonExisting_shouldReturnNotFound() throws Exception {
        // When
        doThrow(CustomersNotFoundException.class).when(customersServiceMock).deleteCustomer(1L);

        // Assert
        mockMvc.perform(delete("/api/v1/customers/{customerId}", 1)
                        .accept(MimeTypeUtils.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof CustomersNotFoundException));
    }
}

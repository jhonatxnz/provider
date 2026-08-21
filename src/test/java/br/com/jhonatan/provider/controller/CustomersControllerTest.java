package br.com.jhonatan.provider.controller;

import br.com.jhonatan.provider.dto.CustomerRequest;
import br.com.jhonatan.provider.dto.CustomerResponse;
import br.com.jhonatan.provider.dto.StatusResponse;
import br.com.jhonatan.provider.service.CustomersService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import br.com.jhonatan.provider.model.Users;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import util.CustomerCreator;
import util.CustomerRequestCreator;
import util.CustomerResponseCreator;


@ExtendWith(MockitoExtension.class)
class CustomersControllerTest {
    @InjectMocks
    CustomersController customersController;

    @Mock
    CustomersService customersServiceMock;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("getCustomer returns customer by username when successful")
    void get_ReturnsCustomerByUsername_WhenSuccessful() {
        Users expectedUser = CustomerCreator.createValidUser();

        CustomerResponse customerResponse = CustomerResponseCreator.createCustomerResponse(expectedUser);

        BDDMockito.when(customersServiceMock.getByUsername(expectedUser.getUsername()))
                .thenReturn(customerResponse);

        CustomerResponse result = customersController.getCustomer(expectedUser.getUsername());

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getName()).isEqualTo(expectedUser.getName());
    }


    @Test
    @DisplayName("createCustomer returns status response when successful")
    void create_ReturnsStatusResponse_WhenSuccessful() {
        Users expectedUser = CustomerCreator.createValidUser();

        CustomerRequest customerRequest = CustomerRequestCreator.createCustomerRequest(expectedUser);

        BDDMockito.when(customersServiceMock.create(customerRequest)).thenReturn(ResponseEntity.status(HttpStatus.CREATED)
                .body(StatusResponse
                        .builder()
                        .status("success")
                        .message("User saved successfully")
                        .statusCode("201")
                        .build()));

        StatusResponse response = customersController.createCustomer(customerRequest).getBody();

        Assertions.assertThat(response).isNotNull();

        Assertions.assertThat(response.getStatusCode()).isEqualTo("201");
    }

    @Test
    @DisplayName("updateCustomer returns status response when successful")
    void update_ReturnsStatusResponse_WhenSuccessful() {
        Users expectedUser = CustomerCreator.createValidUser();

        CustomerRequest customerRequest = CustomerRequestCreator.createCustomerRequest(expectedUser);

        BDDMockito.when(customersServiceMock.update(customerRequest)).thenReturn(ResponseEntity.status(HttpStatus.OK)
                .body(StatusResponse
                        .builder()
                        .status("success")
                        .message("user updated successfully")
                        .statusCode("200")
                        .build()));

        StatusResponse response = customersController.updateCustomer(customerRequest).getBody();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo("200");
    }

}
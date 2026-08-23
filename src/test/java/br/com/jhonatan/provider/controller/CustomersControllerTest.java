package br.com.jhonatan.provider.controller;

import br.com.jhonatan.provider.dto.CustomerRequest;
import br.com.jhonatan.provider.dto.CustomerResponse;
import br.com.jhonatan.provider.dto.CustomerUpdateRequest;
import br.com.jhonatan.provider.dto.StatusResponse;
import br.com.jhonatan.provider.exception.CustomerAlreadyExistsException;
import br.com.jhonatan.provider.exception.CustomerNotFoundException;
import br.com.jhonatan.provider.service.CustomersService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import br.com.jhonatan.provider.model.Customers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import util.CustomerCreator;
import util.CustomerRequestCreator;
import util.CustomerResponseCreator;
import util.CustomerUpdateRequestCreator;


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
        Customers expectedCustomer = CustomerCreator.createValidCustomer();

        CustomerResponse customerResponse = CustomerResponseCreator.createCustomerResponse(expectedCustomer);

        BDDMockito.when(customersServiceMock.getByUsername(expectedCustomer.getUsername()))
                .thenReturn(customerResponse);

        CustomerResponse result = customersController.getCustomer(expectedCustomer.getUsername());

        Assertions.assertThat(result).isNotNull();
        Assertions.assertThat(result.getName()).isEqualTo(expectedCustomer.getName());
    }


    @Test
    @DisplayName("createCustomer returns status 201 response when successful")
    void create_ReturnsStatus201Response_WhenSuccessful() {
        Customers expectedCustomer = CustomerCreator.createValidCustomer();

        CustomerRequest customerRequest = CustomerRequestCreator.createCustomerRequest(expectedCustomer);

        BDDMockito.when(customersServiceMock.create(customerRequest)).thenReturn(ResponseEntity.status(HttpStatus.CREATED)
                .body(StatusResponse
                        .builder()
                        .status("success")
                        .message("Customer saved successfully")
                        .statusCode("201")
                        .build()));

        StatusResponse response = customersController.createCustomer(customerRequest).getBody();

        Assertions.assertThat(response).isNotNull();

        Assertions.assertThat(response.getStatusCode()).isEqualTo("201");
    }

    @Test
    @DisplayName("updateCustomer returns status 200 response when successful")
    void update_ReturnsStatus200Response_WhenSuccessful() {
        Customers expectedCustomer = CustomerCreator.createValidCustomer();

        CustomerUpdateRequest customerUpdateRequest = CustomerUpdateRequestCreator.createCustomerUpdateRequest(expectedCustomer);

        BDDMockito.when(customersServiceMock.update(expectedCustomer.getUsername(), customerUpdateRequest)).thenReturn(ResponseEntity.status(HttpStatus.OK)
                .body(StatusResponse
                        .builder()
                        .status("success")
                        .message("customer updated successfully")
                        .statusCode("200")
                        .build()));

        StatusResponse response = customersController.updateCustomer(expectedCustomer.getUsername(), customerUpdateRequest).getBody();

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo("200");
    }

    @Test
    @DisplayName("get propagates CustomerNotFoundException when customer does not exist")
    void get_ThrowsCustomerNotFoundException_WhenCustomerDoesNotExist() {
        String nonExistentUsername = "nonexistentcustomer";

        BDDMockito.when(customersServiceMock.getByUsername(nonExistentUsername))
                .thenThrow(new CustomerNotFoundException());

        Assertions.assertThatThrownBy(() -> customersController.getCustomer(nonExistentUsername))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    @DisplayName("create propagates CustomerAlreadyExistsException when username is already registered")
    void create_ThrowsCustomerAlreadyExistsException_WhenUsernameAlreadyExists() {
        Customers expectedCustomer = CustomerCreator.createValidCustomer();

        CustomerRequest customerRequest = CustomerRequestCreator.createCustomerRequest(expectedCustomer);

        BDDMockito.when(customersServiceMock.create(customerRequest))
                .thenThrow(new CustomerAlreadyExistsException());

        Assertions.assertThatThrownBy(() -> customersController.createCustomer(customerRequest))
                .isInstanceOf(CustomerAlreadyExistsException.class);
    }

    @Test
    @DisplayName("update propagates CustomerNotFoundException when customer does not exist")
    void update_ThrowsCustomerNotFoundException_WhenCustomerDoesNotExist() {
        Customers expectedCustomer = CustomerCreator.createValidCustomer();

        CustomerUpdateRequest customerUpdateRequest = CustomerUpdateRequestCreator.createCustomerUpdateRequest(expectedCustomer);

        String nonExistentUsername = "nonexistentcustomer";

        BDDMockito.when(customersServiceMock.update(nonExistentUsername, customerUpdateRequest))
                .thenThrow(new CustomerNotFoundException());

        Assertions.assertThatThrownBy(() -> customersController.updateCustomer(nonExistentUsername, customerUpdateRequest))
                .isInstanceOf(CustomerNotFoundException.class);
    }

}
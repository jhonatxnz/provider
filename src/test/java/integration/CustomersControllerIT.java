package integration;

import br.com.jhonatan.provider.ProviderApplication;
import br.com.jhonatan.provider.controller.RestControllerUrlBase;
import br.com.jhonatan.provider.dto.CustomerRequest;
import br.com.jhonatan.provider.dto.CustomerResponse;
import br.com.jhonatan.provider.dto.CustomerUpdateRequest;
import br.com.jhonatan.provider.dto.StatusResponse;
import br.com.jhonatan.provider.model.Customers;
import br.com.jhonatan.provider.repository.CustomersRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import util.CustomerCreator;
import util.CustomerRequestCreator;
import util.CustomerUpdateRequestCreator;

import java.util.Map;

@SpringBootTest(classes = ProviderApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@AutoConfigureTestDatabase
@ActiveProfiles("test")
class CustomersControllerIT {

    private static final String TEST_CLIENT_ID = "consumer-api";
    private static final String TEST_CLIENT_SECRET = "test-secret-1234";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private CustomersRepository customersRepository;

    private String token;

    @BeforeEach
    void authenticate() {
        Map<String, String> tokenRequest = Map.of(
                "clientId", TEST_CLIENT_ID,
                "clientSecret", TEST_CLIENT_SECRET
        );

        ResponseEntity<Map> response = testRestTemplate.postForEntity(
                RestControllerUrlBase.BASE_URL + "/auth/token",
                tokenRequest,
                Map.class
        );

        token = (String) response.getBody().get("accessToken");
    }

    @AfterEach
    void tearDown() {
        customersRepository.deleteAll();
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return headers;
    }

    private <T> HttpEntity<T> entity(T body) {
        return new HttpEntity<>(body, authHeaders());
    }

    private HttpEntity<Void> entity() {
        return new HttpEntity<>(null, authHeaders());
    }

    @Test
    @DisplayName("getCustomerByUsername returns customer by username when successful")
    void getByUsername_ReturnsCustomerByUsername_WhenSuccessful() {
        Customers savedCustomer = customersRepository.save(CustomerCreator.createCustomerToBeSaved());

        ResponseEntity<CustomerResponse> response = testRestTemplate.exchange(
                RestControllerUrlBase.BASE_URL + "/customers/" + savedCustomer.getUsername(),
                HttpMethod.GET,
                entity(),
                new ParameterizedTypeReference<CustomerResponse>() {}
        );

        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getName()).isEqualTo(savedCustomer.getName());
    }

    @Test
    @DisplayName("getCustomerByDocument returns customer by document when successful")
    void getByDocument_ReturnsCustomerByDocument_WhenSuccessful() {
        Customers savedCustomer = customersRepository.save(CustomerCreator.createCustomerToBeSaved());

        ResponseEntity<CustomerResponse> response = testRestTemplate.exchange(
                RestControllerUrlBase.BASE_URL + "/customers/document/" + savedCustomer.getDocument(),
                HttpMethod.GET,
                entity(),
                new ParameterizedTypeReference<CustomerResponse>() {}
        );

        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getName()).isEqualTo(savedCustomer.getName());
    }

    @Test
    @DisplayName("createCustomer returns status 201 response when successful")
    void create_ReturnsStatus201Response_WhenSuccessful() {
        Customers expectedCustomer = CustomerCreator.createValidCustomer();

        CustomerRequest customerRequest = CustomerRequestCreator.createCustomerRequest(expectedCustomer);

        ResponseEntity<StatusResponse> response = testRestTemplate.exchange(
                RestControllerUrlBase.BASE_URL + "/customers",
                HttpMethod.POST,
                entity(customerRequest),
                new ParameterizedTypeReference<StatusResponse>() {}
        );

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getStatusCode()).isEqualTo("201");
    }

    @Test
    @DisplayName("updateCustomer returns status 200 response when successful")
    void update_ReturnsStatus200Response_WhenSuccessful() {
        Customers savedCustomer = customersRepository.save(CustomerCreator.createCustomerToBeSaved());

        CustomerUpdateRequest customerUpdateRequest = CustomerUpdateRequestCreator.createCustomerUpdateRequest(savedCustomer);

        ResponseEntity<StatusResponse> response = testRestTemplate.exchange(
                RestControllerUrlBase.BASE_URL + "/customers/" + savedCustomer.getDocument(),
                HttpMethod.PUT,
                entity(customerUpdateRequest),
                new ParameterizedTypeReference<StatusResponse>() {}
        );

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getStatusCode()).isEqualTo("200");
    }

    @Test
    @DisplayName("getByUsername returns 404 when customer does not exist")
    void getByUsername_ReturnsNotFound_WhenCustomerDoesNotExist() {
        ResponseEntity<StatusResponse> response = testRestTemplate.exchange(
                RestControllerUrlBase.BASE_URL + "/customers/nonexistentcustomer",
                HttpMethod.GET,
                entity(),
                new ParameterizedTypeReference<StatusResponse>() {}
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("getCustomerByDocument returns 404 when customer does not exist")
    void getByDocument_ReturnsNotFound_WhenCustomerDoesNotExist() {
        ResponseEntity<StatusResponse> response = testRestTemplate.exchange(
                RestControllerUrlBase.BASE_URL + "/customers/document/00000000000",
                HttpMethod.GET,
                entity(),
                new ParameterizedTypeReference<StatusResponse>() {}
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("create returns 409 when username is already registered")
    void create_ReturnsConflict_WhenUsernameAlreadyExists() {
        Customers existingCustomer = customersRepository.save(CustomerCreator.createCustomerToBeSaved());

        CustomerRequest customerRequest = CustomerRequestCreator.createCustomerRequest(existingCustomer);

        ResponseEntity<StatusResponse> response = testRestTemplate.exchange(
                RestControllerUrlBase.BASE_URL + "/customers",
                HttpMethod.POST,
                entity(customerRequest),
                new ParameterizedTypeReference<StatusResponse>() {}
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("update returns 404 when customer does not exist")
    void update_ReturnsNotFound_WhenCustomerDoesNotExist() {
        Customers nonPersistedCustomer = CustomerCreator.createValidCustomer();

        CustomerUpdateRequest customerUpdateRequest = CustomerUpdateRequestCreator.createCustomerUpdateRequest(nonPersistedCustomer);

        ResponseEntity<StatusResponse> response = testRestTemplate.exchange(
                RestControllerUrlBase.BASE_URL + "/customers/24629118301",
                HttpMethod.PUT,
                entity(customerUpdateRequest),
                new ParameterizedTypeReference<StatusResponse>() {}
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
package integration;

import br.com.jhonatan.provider.ProviderApplication;
import br.com.jhonatan.provider.controller.RestControllerUrlBase;
import br.com.jhonatan.provider.dto.StatusResponse;
import br.com.jhonatan.provider.dto.SubscriptionRequest;
import br.com.jhonatan.provider.dto.SubscriptionResponse;
import br.com.jhonatan.provider.model.CustomerSubscriptions;
import br.com.jhonatan.provider.model.Customers;
import br.com.jhonatan.provider.model.Subscriptions;
import br.com.jhonatan.provider.repository.CustomerSubscriptionsRepository;
import br.com.jhonatan.provider.repository.CustomersRepository;
import br.com.jhonatan.provider.repository.SubscriptionsRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import util.CustomerCreator;
import util.CustomerSubscriptionCreator;
import util.SubscriptionCreator;
import util.SubscriptionRequestCreator;

import java.util.List;

@SpringBootTest(classes = ProviderApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@AutoConfigureTestDatabase
class SubscriptionsControllerIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private SubscriptionsRepository subscriptionsRepository;

    @Autowired
    private CustomerSubscriptionsRepository customerSubscriptionsRepository;

    @AfterEach
    void tearDown() {
        customerSubscriptionsRepository.deleteAll();
        subscriptionsRepository.deleteAll();
        customersRepository.deleteAll();
    }

    @Test
    @DisplayName("listSubscriptions returns list of subscriptions by customer document when successful")
    void list_ReturnsListOfSubscriptionsByCustomerDocument_WhenSuccessful() {
        Customers savedCustomer = customersRepository.save(CustomerCreator.createCustomerToBeSaved());
        Subscriptions savedSubscription = subscriptionsRepository.save(SubscriptionCreator.createSubscriptionToBeSaved());

        customerSubscriptionsRepository.save(
                CustomerSubscriptionCreator.createCustomerSubscriptionToBeSaved(savedCustomer, savedSubscription)
        );

        ResponseEntity<List<SubscriptionResponse>> response = testRestTemplate.exchange(
                RestControllerUrlBase.BASE_URL + "/customers/" + savedCustomer.getDocument() + "/subscriptions",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<SubscriptionResponse>>() {}
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        Assertions.assertThat(response.getBody())
                .isNotNull()
                .hasSize(1);

        Assertions.assertThat(response.getBody().getFirst().getName()).isEqualTo(savedSubscription.getName());
    }

    @Test
    @DisplayName("listSubscriptions returns an empty list when the customer has no subscriptions")
    void list_ReturnsEmptyList_WhenCustomerHasNoSubscriptions() {
        Customers savedCustomer = customersRepository.save(CustomerCreator.createCustomerToBeSaved());

        ResponseEntity<List<SubscriptionResponse>> response = testRestTemplate.exchange(
                RestControllerUrlBase.BASE_URL + "/customers/" + savedCustomer.getDocument() + "/subscriptions",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<SubscriptionResponse>>() {}
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("listSubscriptions returns 404 when customer does not exist")
    void list_ReturnsNotFound_WhenCustomerDoesNotExist() {
        ResponseEntity<StatusResponse> response = testRestTemplate.exchange(
                RestControllerUrlBase.BASE_URL + "/customers/00000000000/subscriptions",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<StatusResponse>() {}
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("createSubscription returns status 201 response when successful")
    void create_ReturnsStatus201Response_WhenSuccessful() {
        Customers savedCustomer = customersRepository.save(CustomerCreator.createCustomerToBeSaved());
        Subscriptions savedSubscription = subscriptionsRepository.save(SubscriptionCreator.createSubscriptionToBeSaved());

        SubscriptionRequest subscriptionRequest = SubscriptionRequestCreator.createSubscriptionRequest(savedSubscription);

        ResponseEntity<StatusResponse> response = testRestTemplate.exchange(
                RestControllerUrlBase.BASE_URL + "/customers/" + savedCustomer.getDocument() + "/subscriptions",
                HttpMethod.POST,
                new HttpEntity<>(subscriptionRequest),
                new ParameterizedTypeReference<StatusResponse>() {}
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getStatusCode()).isEqualTo("201");
    }

    @Test
    @DisplayName("createSubscription returns 409 when customer already has the subscription")
    void create_ReturnsConflict_WhenCustomerAlreadyHasSubscription() {
        Customers savedCustomer = customersRepository.save(CustomerCreator.createCustomerToBeSaved());
        Subscriptions savedSubscription = subscriptionsRepository.save(SubscriptionCreator.createSubscriptionToBeSaved());

        customerSubscriptionsRepository.save(
                CustomerSubscriptionCreator.createCustomerSubscriptionToBeSaved(savedCustomer, savedSubscription)
        );

        SubscriptionRequest subscriptionRequest = SubscriptionRequestCreator.createSubscriptionRequest(savedSubscription);

        ResponseEntity<StatusResponse> response = testRestTemplate.exchange(
                RestControllerUrlBase.BASE_URL + "/customers/" + savedCustomer.getDocument() + "/subscriptions",
                HttpMethod.POST,
                new HttpEntity<>(subscriptionRequest),
                new ParameterizedTypeReference<StatusResponse>() {}
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("createSubscription returns 404 when subscription code does not exist")
    void create_ReturnsNotFound_WhenSubscriptionCodeDoesNotExist() {
        Customers savedCustomer = customersRepository.save(CustomerCreator.createCustomerToBeSaved());

        SubscriptionRequest subscriptionRequest = SubscriptionRequest.builder()
                .code("NONEXISTENT_CODE")
                .build();

        ResponseEntity<StatusResponse> response = testRestTemplate.exchange(
                RestControllerUrlBase.BASE_URL + "/customers/" + savedCustomer.getDocument() + "/subscriptions",
                HttpMethod.POST,
                new HttpEntity<>(subscriptionRequest),
                new ParameterizedTypeReference<StatusResponse>() {}
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("createSubscription returns 404 when customer does not exist")
    void create_ReturnsNotFound_WhenCustomerDoesNotExist() {
        Subscriptions savedSubscription = subscriptionsRepository.save(SubscriptionCreator.createSubscriptionToBeSaved());

        SubscriptionRequest subscriptionRequest = SubscriptionRequestCreator.createSubscriptionRequest(savedSubscription);

        ResponseEntity<StatusResponse> response = testRestTemplate.exchange(
                RestControllerUrlBase.BASE_URL + "/customers/00000000000/subscriptions",
                HttpMethod.POST,
                new HttpEntity<>(subscriptionRequest),
                new ParameterizedTypeReference<StatusResponse>() {}
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("deleteSubscription returns status 200 response when successful")
    void delete_ReturnsStatus200Response_WhenSuccessful() {
        Customers savedCustomer = customersRepository.save(CustomerCreator.createCustomerToBeSaved());
        Subscriptions savedSubscription = subscriptionsRepository.save(SubscriptionCreator.createSubscriptionToBeSaved());

        customerSubscriptionsRepository.save(
                CustomerSubscriptionCreator.createCustomerSubscriptionToBeSaved(savedCustomer, savedSubscription)
        );

        ResponseEntity<StatusResponse> response = testRestTemplate.exchange(
                RestControllerUrlBase.BASE_URL + "/customers/" + savedCustomer.getDocument()
                        + "/subscriptions/" + savedSubscription.getCode(),
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<StatusResponse>() {}
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isNotNull();
        Assertions.assertThat(response.getBody().getStatusCode()).isEqualTo("200");
    }

    @Test
    @DisplayName("deleteSubscription returns 409 when subscription is already canceled")
    void delete_ReturnsConflict_WhenSubscriptionAlreadyCanceled() {
        Customers savedCustomer = customersRepository.save(CustomerCreator.createCustomerToBeSaved());
        Subscriptions savedSubscription = subscriptionsRepository.save(SubscriptionCreator.createSubscriptionToBeSaved());

        CustomerSubscriptions canceledSubscription =
                CustomerSubscriptionCreator.createCustomerSubscriptionToBeSaved(savedCustomer, savedSubscription);
        canceledSubscription.setStatus("0"); // '0' - inactive/already canceled

        customerSubscriptionsRepository.save(canceledSubscription);

        ResponseEntity<StatusResponse> response = testRestTemplate.exchange(
                RestControllerUrlBase.BASE_URL + "/customers/" + savedCustomer.getDocument()
                        + "/subscriptions/" + savedSubscription.getCode(),
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<StatusResponse>() {}
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("deleteSubscription returns 404 when subscription does not exist for the customer")
    void delete_ReturnsNotFound_WhenSubscriptionDoesNotExistForCustomer() {
        Customers savedCustomer = customersRepository.save(CustomerCreator.createCustomerToBeSaved());
        Subscriptions savedSubscription = subscriptionsRepository.save(SubscriptionCreator.createSubscriptionToBeSaved());

        ResponseEntity<StatusResponse> response = testRestTemplate.exchange(
                RestControllerUrlBase.BASE_URL + "/customers/" + savedCustomer.getDocument()
                        + "/subscriptions/" + savedSubscription.getCode(),
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<StatusResponse>() {}
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("deleteSubscription returns 404 when customer does not exist")
    void delete_ReturnsNotFound_WhenCustomerDoesNotExist() {
        Subscriptions savedSubscription = subscriptionsRepository.save(SubscriptionCreator.createSubscriptionToBeSaved());

        ResponseEntity<StatusResponse> response = testRestTemplate.exchange(
                RestControllerUrlBase.BASE_URL + "/customers/00000000000/subscriptions/" + savedSubscription.getCode(),
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<StatusResponse>() {}
        );

        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
package br.com.jhonatan.provider.controller;

import br.com.jhonatan.provider.dto.CustomerRequest;
import br.com.jhonatan.provider.dto.StatusResponse;
import br.com.jhonatan.provider.dto.SubscriptionRequest;
import br.com.jhonatan.provider.dto.SubscriptionResponse;
import br.com.jhonatan.provider.model.Subscriptions;
import br.com.jhonatan.provider.model.Customers;
import br.com.jhonatan.provider.service.SubscriptionsService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import util.*;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class SubscriptionsControllerTest {

    @InjectMocks
    SubscriptionsController subscriptionsController;

    @Mock
    SubscriptionsService subscriptionsServiceMock;

    @Test
    @DisplayName("listSubscriptions returns list of subscriptions by customer username when successful")
    void list_ReturnsListOfSubscriptionsByCustomerUsername_WhenSuccessful() {
        Customers expectedCustomer = CustomerCreator.createValidCustomer();

        Subscriptions subscription = SubscriptionCreator.createValidSubscription();

        SubscriptionResponse expectedResponse = SubscriptionResponseCreator.createSubscriptionResponse(subscription);

        BDDMockito.when(subscriptionsServiceMock.list(expectedCustomer.getUsername()))
                .thenReturn(List.of(expectedResponse));

        List<SubscriptionResponse> subscriptionResponse = subscriptionsController.listSubscriptions(expectedCustomer.getUsername());

        Assertions.assertThat(subscriptionResponse)
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(subscriptionResponse.getFirst().getName()).isEqualTo(subscription.getName());
    }

    @Test
    @DisplayName("createSubscription returns status response when successful")
    void create_ReturnsStatusResponse_WhenSuccessful(){
        Customers customer = CustomerCreator.createValidCustomer();

        Subscriptions subscription = SubscriptionCreator.createValidSubscription();

        SubscriptionRequest subscriptionRequest = SubscriptionRequestCreator.createSubscriptionRequest(subscription);

        BDDMockito.when(subscriptionsServiceMock.subscribe(customer.getUsername(), subscription.getCode())).thenReturn(ResponseEntity.status(HttpStatus.CREATED)
                .body(StatusResponse
                        .builder()
                        .status("success")
                        .message("Subscription created successfully")
                        .statusCode("201")
                        .build()));

        StatusResponse response = subscriptionsController.createSubscription(customer.getUsername(), subscriptionRequest).getBody();

        Assertions.assertThat(response).isNotNull();

        Assertions.assertThat(response.getStatusCode()).isEqualTo("201");
    }

    @Test
    @DisplayName("deleteSubscription returns status response when successful")
    void delete_ReturnsStatusResponse_WhenSuccessful(){
        Customers customer = CustomerCreator.createValidCustomer();

        Subscriptions subscription = SubscriptionCreator.createValidSubscription();

        BDDMockito.when(subscriptionsServiceMock.cancel(customer.getUsername(), subscription.getCode())).thenReturn(ResponseEntity.ok(
                StatusResponse.builder()
                        .status("Subscription canceled successfully")
                        .message("Subscription " + subscription.getName() + " canceled for customer " + customer.getUsername())
                        .statusCode("200")
                        .build()));

        StatusResponse response = subscriptionsController.deleteSubscription(customer.getUsername(), subscription.getCode()).getBody();

        Assertions.assertThat(response).isNotNull();

        Assertions.assertThat(response.getStatusCode()).isEqualTo("200");

    }

    @Test
    @DisplayName("TODO: test listSubscriptions returns an empty list when the customer has no subscriptions")
    void list_ReturnsEmptyList_WhenCustomerHasNoSubscriptions(){}

    @Test
    @DisplayName("TODO: test listSubscriptions propagates CustomerNotFoundException when customer does not exist")
    void list_ThrowsCustomerNotFoundException_WhenCustomerDoesNotExist(){}

    @Test
    @DisplayName("TODO: test createSubscription propagates CustomerAlreadyHasSubscription when customer already has the subscription")
    void create_ThrowsCustomerAlreadyHasSubscription_WhenCustomerAlreadyHasSubscription(){}

    @Test
    @DisplayName("TODO: test createSubscription propagates SubscriptionNotFound when subscription code does not exist")
    void create_ThrowsSubscriptionNotFound_WhenSubscriptionCodeDoesNotExist(){}

    @Test
    @DisplayName("TODO: test deleteSubscription propagates SubscriptionAlreadyCanceled when subscription is already canceled")
    void delete_ThrowsSubscriptionAlreadyCanceled_WhenSubscriptionAlreadyCanceled(){}

    @Test
    @DisplayName("TODO: test deleteSubscription propagates SubscriptionNotFound when subscription does not exist for the customer")
    void delete_ThrowsSubscriptionNotFound_WhenSubscriptionDoesNotExistForCustomer(){}
}
package br.com.jhonatan.provider.controller;

import br.com.jhonatan.provider.dto.StatusResponse;
import br.com.jhonatan.provider.dto.SubscriptionRequest;
import br.com.jhonatan.provider.dto.SubscriptionResponse;
import br.com.jhonatan.provider.exception.CustomerAlreadyHasSubscription;
import br.com.jhonatan.provider.exception.CustomerNotFoundException;
import br.com.jhonatan.provider.exception.SubscriptionAlreadyCanceled;
import br.com.jhonatan.provider.exception.SubscriptionNotFound;
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
    @DisplayName("listSubscriptions returns list of subscriptions by customer document when successful")
    void list_ReturnsListOfSubscriptionsByCustomerDocument_WhenSuccessful() {
        Customers expectedCustomer = CustomerCreator.createValidCustomer();

        Subscriptions subscription = SubscriptionCreator.createValidSubscription();

        SubscriptionResponse expectedResponse = SubscriptionResponseCreator.createSubscriptionResponse(subscription);

        BDDMockito.when(subscriptionsServiceMock.list(expectedCustomer.getDocument()))
                .thenReturn(List.of(expectedResponse));

        List<SubscriptionResponse> subscriptionResponse = subscriptionsController.listSubscriptions(expectedCustomer.getDocument());

        Assertions.assertThat(subscriptionResponse)
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(subscriptionResponse.getFirst().getName()).isEqualTo(subscription.getName());
    }

    @Test
    @DisplayName("createSubscription returns status 201 response when successful")
    void create_ReturnsStatus201Response_WhenSuccessful(){
        Customers customer = CustomerCreator.createValidCustomer();

        Subscriptions subscription = SubscriptionCreator.createValidSubscription();

        SubscriptionRequest subscriptionRequest = SubscriptionRequestCreator.createSubscriptionRequest(subscription);

        BDDMockito.when(subscriptionsServiceMock.subscribe(customer.getDocument(), subscription.getCode())).thenReturn(ResponseEntity.status(HttpStatus.CREATED)
                .body(StatusResponse
                        .builder()
                        .status("success")
                        .message("Subscription created successfully")
                        .statusCode("201")
                        .build()));

        StatusResponse response = subscriptionsController.createSubscription(customer.getDocument(), subscriptionRequest).getBody();

        Assertions.assertThat(response).isNotNull();

        Assertions.assertThat(response.getStatusCode()).isEqualTo("201");
    }

    @Test
    @DisplayName("deleteSubscription returns status 200 response when successful")
    void delete_ReturnsStatus200Response_WhenSuccessful(){
        Customers customer = CustomerCreator.createValidCustomer();

        Subscriptions subscription = SubscriptionCreator.createValidSubscription();

        BDDMockito.when(subscriptionsServiceMock.cancel(customer.getDocument(), subscription.getCode())).thenReturn(ResponseEntity.ok(
                StatusResponse.builder()
                        .status("Subscription canceled successfully")
                        .message("Subscription " + subscription.getName() + " canceled for customer " + customer.getDocument())
                        .statusCode("200")
                        .build()));

        StatusResponse response = subscriptionsController.deleteSubscription(customer.getDocument(), subscription.getCode()).getBody();

        Assertions.assertThat(response).isNotNull();

        Assertions.assertThat(response.getStatusCode()).isEqualTo("200");

    }

    @Test
    @DisplayName("list listSubscriptions returns an empty list when the customer has no subscriptions")
    void list_ReturnsEmptyList_WhenCustomerHasNoSubscriptions() {
        Customers expectedCustomer = CustomerCreator.createValidCustomer();

        BDDMockito.when(subscriptionsServiceMock.list(expectedCustomer.getDocument()))
                .thenReturn(List.of());

        List<SubscriptionResponse> subscriptionResponse = subscriptionsController.listSubscriptions(expectedCustomer.getDocument());

        Assertions.assertThat(subscriptionResponse).isEmpty();
    }

    @Test
    @DisplayName("list listSubscriptions propagates CustomerNotFoundException when customer does not exist")
    void list_ThrowsCustomerNotFoundException_WhenCustomerDoesNotExist(){

        String nonExistentDocument = "00000000000";

        BDDMockito.when(subscriptionsServiceMock.list(nonExistentDocument))
                .thenThrow(new CustomerNotFoundException());

        Assertions.assertThatThrownBy(() -> subscriptionsController.listSubscriptions(nonExistentDocument))
                .isInstanceOf(CustomerNotFoundException.class);
    }

    @Test
    @DisplayName("createSubscription propagates CustomerAlreadyHasSubscription when customer already has the subscription")
    void create_ThrowsCustomerAlreadyHasSubscription_WhenCustomerAlreadyHasSubscription(){
        Customers customer = CustomerCreator.createValidCustomer();

        Subscriptions subscription = SubscriptionCreator.createValidSubscription();

        SubscriptionRequest subscriptionRequest = SubscriptionRequestCreator.createSubscriptionRequest(subscription);

        BDDMockito.when(subscriptionsServiceMock.subscribe(customer.getDocument(), subscription.getCode())).thenThrow(new CustomerAlreadyHasSubscription());

        Assertions.assertThatThrownBy(() -> subscriptionsController.createSubscription(customer.getDocument(), subscriptionRequest))
                .isInstanceOf(CustomerAlreadyHasSubscription.class);
    }

    @Test
    @DisplayName("createSubscription propagates SubscriptionNotFound when subscription code does not exist")
    void create_ThrowsSubscriptionNotFound_WhenSubscriptionCodeDoesNotExist() {
        Customers customer = CustomerCreator.createValidCustomer();

        String nonExistentCode = "NONEXISTENT_CODE";

        SubscriptionRequest subscriptionRequest = SubscriptionRequest.builder()
                .code(nonExistentCode)
                .build();

        BDDMockito.when(subscriptionsServiceMock.subscribe(customer.getDocument(), nonExistentCode))
                .thenThrow(new SubscriptionNotFound());

        Assertions.assertThatThrownBy(() -> subscriptionsController.createSubscription(customer.getDocument(), subscriptionRequest))
                .isInstanceOf(SubscriptionNotFound.class);
    }

    @Test
    @DisplayName("deleteSubscription propagates SubscriptionAlreadyCanceled when subscription is already canceled")
    void delete_ThrowsSubscriptionAlreadyCanceled_WhenSubscriptionAlreadyCanceled() {

        Customers customer = CustomerCreator.createValidCustomer();

        Subscriptions subscription = SubscriptionCreator.createValidSubscription();

        BDDMockito.when(subscriptionsServiceMock.cancel(customer.getDocument(), subscription.getCode()))
                .thenThrow(new SubscriptionAlreadyCanceled());

        Assertions.assertThatThrownBy(() -> subscriptionsController.deleteSubscription(customer.getDocument(), subscription.getCode()))
                .isInstanceOf(SubscriptionAlreadyCanceled.class);
    }

    @Test
    @DisplayName("deleteSubscription propagates SubscriptionNotFound when subscription does not exist for the customer")
    void delete_ThrowsSubscriptionNotFound_WhenSubscriptionDoesNotExistForCustomer() {
        Customers customer = CustomerCreator.createValidCustomer();

        String nonExistentCode = "NONEXISTENT_CODE";

        BDDMockito.when(subscriptionsServiceMock.cancel(customer.getDocument(), nonExistentCode))
                .thenThrow(new SubscriptionNotFound());

        Assertions.assertThatThrownBy(() -> subscriptionsController.deleteSubscription(customer.getDocument(), nonExistentCode))
                .isInstanceOf(SubscriptionNotFound.class);
    }
}
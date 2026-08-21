package br.com.jhonatan.provider.controller;

import br.com.jhonatan.provider.dto.CustomerRequest;
import br.com.jhonatan.provider.dto.StatusResponse;
import br.com.jhonatan.provider.dto.SubscriptionRequest;
import br.com.jhonatan.provider.dto.SubscriptionResponse;
import br.com.jhonatan.provider.model.Subscriptions;
import br.com.jhonatan.provider.model.Users;
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
        Users expectedUser = CustomerCreator.createValidUser();

        Subscriptions subscription = SubscriptionCreator.createValidSubscription();

        SubscriptionResponse expectedResponse = SubscriptionResponseCreator.createSubscriptionResponse(subscription);

        BDDMockito.when(subscriptionsServiceMock.list(expectedUser.getUsername()))
                .thenReturn(List.of(expectedResponse));

        List<SubscriptionResponse> subscriptionResponse = subscriptionsController.listSubscriptions(expectedUser.getUsername());

        Assertions.assertThat(subscriptionResponse)
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(subscriptionResponse.getFirst().getName()).isEqualTo(subscription.getName());
    }

    @Test
    @DisplayName("createSubscription returns status response when successful")
    void create_ReturnsStatusResponse_WhenSuccessful(){
        Users user = CustomerCreator.createValidUser();

        Subscriptions subscription = SubscriptionCreator.createValidSubscription();

        SubscriptionRequest subscriptionRequest = SubscriptionRequestCreator.createSubscriptionRequest(subscription);

        BDDMockito.when(subscriptionsServiceMock.subscribe(user.getUsername(), subscription.getCode())).thenReturn(ResponseEntity.status(HttpStatus.CREATED)
                .body(StatusResponse
                        .builder()
                        .status("success")
                        .message("Subscription created successfully")
                        .statusCode("201")
                        .build()));

        StatusResponse response = subscriptionsController.createSubscription(user.getUsername(), subscriptionRequest).getBody();

        Assertions.assertThat(response).isNotNull();

        Assertions.assertThat(response.getStatusCode()).isEqualTo("201");
    }

    @Test
    @DisplayName("deleteSubscription returns status response when successful")
    void delete_ReturnsStatusResponse_WhenSuccessful(){
        Users user = CustomerCreator.createValidUser();

        Subscriptions subscription = SubscriptionCreator.createValidSubscription();

        BDDMockito.when(subscriptionsServiceMock.cancel(user.getUsername(), subscription.getCode())).thenReturn(ResponseEntity.ok(
                StatusResponse.builder()
                        .status("Subscription canceled successfully")
                        .message("Subscription " + subscription.getName() + " canceled for user " + user.getUsername())
                        .statusCode("200")
                        .build()));

        StatusResponse response = subscriptionsController.deleteSubscription(user.getUsername(), subscription.getCode()).getBody();

        Assertions.assertThat(response).isNotNull();

        Assertions.assertThat(response.getStatusCode()).isEqualTo("200");

    }
}
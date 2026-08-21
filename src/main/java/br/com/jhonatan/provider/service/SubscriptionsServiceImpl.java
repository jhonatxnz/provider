package br.com.jhonatan.provider.service;

import br.com.jhonatan.provider.dto.StatusResponse;
import br.com.jhonatan.provider.dto.SubscriptionResponse;
import br.com.jhonatan.provider.enums.SubscriptionStatus;
import br.com.jhonatan.provider.exception.*;
import br.com.jhonatan.provider.model.Subscriptions;
import br.com.jhonatan.provider.model.CustomerSubscriptions;
import br.com.jhonatan.provider.model.Customers;
import br.com.jhonatan.provider.repository.SubscriptionsRepository;
import br.com.jhonatan.provider.repository.CustomerSubscriptionsRepository;
import br.com.jhonatan.provider.repository.CustomersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionsServiceImpl implements SubscriptionsService {

    private final CustomerSubscriptionsRepository customerSubscriptionsRepository;
    private final CustomersRepository customersRepository;
    private final SubscriptionsRepository subscriptionsRepository;

    @Override
    public List<SubscriptionResponse> list(String username) {
        Customers customer = customersRepository.findByUsername(username).orElseThrow(CustomerNotFoundException::new);

        List<CustomerSubscriptions> subscriptions = customerSubscriptionsRepository.findByCustomerId(customer.getId());

        return subscriptions.stream()
                .map(subscription -> {
                    Subscriptions subscriptionDetails = subscriptionsRepository.findById(subscription.getSubscriptionId())
                            .orElseThrow(SubscriptionNotFound::new);

                    return SubscriptionResponse.builder()
                            .name(subscriptionDetails.getName())
                            .code(subscriptionDetails.getCode())
                            .createdAt(subscription.getCreatedAt())
                            .status(subscription.getStatus())
                            .build();
                })
                .toList();
    }

    @Override
    public ResponseEntity<StatusResponse> subscribe(String username, String code) {
        Customers customer = customersRepository.findByUsername(username).orElseThrow(CustomerNotFoundException::new);

        Subscriptions subscription = subscriptionsRepository.findByCode(code)
                .orElseThrow(SubscriptionNotFound::new);

        boolean subscriptionExists = customerSubscriptionsRepository.findByCustomerId(customer.getId())
                .stream()
                .anyMatch(customerSubscription -> customerSubscription.getSubscriptionId().equals(subscription.getId()));

        if (subscriptionExists) { //fix when status - 0
            throw new CustomerAlreadyHasSubscription();
        }  else {

            CustomerSubscriptions newSubscription = CustomerSubscriptions.builder()
                    .subscriptionId(subscription.getId())
                    .customerId(customer.getId())
                    .createdAt(java.time.LocalDateTime.now())
                    .canceledAt(null)
                    .status(SubscriptionStatus.ACTIVE.value())
                    .email(customer.getEmail())
                    .phone(customer.getPhone())
                    .build();

            customerSubscriptionsRepository.save(newSubscription);

            return ResponseEntity.status(201).body(
                    StatusResponse.builder()
                            .status("Subscription created successfully")
                            .message("Subscription " + code + " created for customer " + username)
                            .statusCode("201")
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<StatusResponse> cancel(String username, String code) {
        Customers customer = customersRepository.findByUsername(username).orElseThrow(CustomerNotFoundException::new);

        Subscriptions subscription = subscriptionsRepository.findByCode(code)
                .orElseThrow(SubscriptionNotFound::new);

        CustomerSubscriptions subscriptionToCancel = customerSubscriptionsRepository.findByCustomerId(customer.getId())
                .stream()
                .filter(customerSubscription -> customerSubscription.getSubscriptionId().equals(subscription.getId()))
                .findFirst()
                .orElseThrow(() -> new SubscriptionNotFound("Subscription not found for the customer"));

        if (subscriptionToCancel.getStatus().equals(SubscriptionStatus.INACTIVE.value())) {
            throw new SubscriptionAlreadyCanceled("Subscription already canceled for the customer");
        }

        subscriptionToCancel.setStatus(SubscriptionStatus.INACTIVE.value());
        subscriptionToCancel.setCanceledAt(java.time.LocalDateTime.now());
        subscriptionToCancel.setUpdatedAt(java.time.LocalDateTime.now());

        customerSubscriptionsRepository.save(subscriptionToCancel);

        return ResponseEntity.ok(
                StatusResponse.builder()
                        .status("Subscription canceled successfully")
                        .message("Subscription " + code + " canceled for customer " + username)
                        .statusCode("200")
                        .build()
        );
    }
}
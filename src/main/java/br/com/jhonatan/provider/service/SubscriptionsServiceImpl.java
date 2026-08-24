package br.com.jhonatan.provider.service;

import br.com.jhonatan.provider.dto.StatusResponse;
import br.com.jhonatan.provider.dto.SubscriptionResponse;
import br.com.jhonatan.provider.enums.SubscriptionStatus;
import br.com.jhonatan.provider.event.SubscriptionCreatedEvent;
import br.com.jhonatan.provider.event.SubscriptionCanceledEvent;
import br.com.jhonatan.provider.event.SubscriptionReactivatedEvent;
import br.com.jhonatan.provider.exception.*;
import br.com.jhonatan.provider.kafka.producer.SubscriptionEventProducer;
import br.com.jhonatan.provider.model.Subscriptions;
import br.com.jhonatan.provider.model.CustomerSubscriptions;
import br.com.jhonatan.provider.model.Customers;
import br.com.jhonatan.provider.repository.SubscriptionsRepository;
import br.com.jhonatan.provider.repository.CustomerSubscriptionsRepository;
import br.com.jhonatan.provider.repository.CustomersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionsServiceImpl implements SubscriptionsService {

    private final SubscriptionEventProducer subscriptionEventProducer;

    private final CustomerSubscriptionsRepository customerSubscriptionsRepository;
    private final CustomersRepository customersRepository;
    private final SubscriptionsRepository subscriptionsRepository;

    @Override
    public List<SubscriptionResponse> list(String document) {

        Customers customer = customersRepository.findByDocument(document).orElseThrow(CustomerNotFoundException::new);

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
    public ResponseEntity<StatusResponse> subscribe(String document, String code) {

        log.info("Starting subscribe process for customer {}, subscription {}", document, code);

        Customers customer = customersRepository.findByDocument(document).orElseThrow(CustomerNotFoundException::new);

        Subscriptions subscription = subscriptionsRepository.findByCode(code)
                .orElseThrow(SubscriptionNotFound::new);

        List<CustomerSubscriptions> customerSubscriptions = customerSubscriptionsRepository.findByCustomerId(customer.getId())
                .stream()
                .filter(customerSubscription -> customerSubscription.getSubscriptionId().equals(subscription.getId()))
                .toList();

        if (!customerSubscriptions.isEmpty() && Objects.equals(customerSubscriptions.getFirst().getStatus(), SubscriptionStatus.ACTIVE.value())) {
            log.info("Customer {} already has {} subscription", document, code);
            throw new CustomerAlreadyHasSubscription();

        } else if (!customerSubscriptions.isEmpty() && Objects.equals(customerSubscriptions.getFirst().getStatus(), SubscriptionStatus.INACTIVE.value())) {

            log.info("Reactivating {} subscription for customer {}", code, document);

            CustomerSubscriptions existingSubscription = customerSubscriptions.getFirst();

            existingSubscription.setStatus(SubscriptionStatus.ACTIVE.value());
            existingSubscription.setUpdatedAt(java.time.LocalDateTime.now());
            existingSubscription.setCanceledAt(null);
            existingSubscription.setEmail(customer.getEmail());
            existingSubscription.setPhone(customer.getPhone());

            customerSubscriptionsRepository.save(existingSubscription);

            subscriptionEventProducer.publishSubscriptionReactivated(
                    SubscriptionReactivatedEvent.builder()
                            .customerEmail(customer.getEmail())
                            .customerName(customer.getName())
                            .subscriptionCode(code)
                            .build()
            );

            log.info("Reactivated {} subscription for customer {}", code, document);

            return ResponseEntity.status(201).body(
                    StatusResponse.builder()
                            .status("Subscription reactivated successfully")
                            .message("Subscription " + code + " reactivated for customer " + document)
                            .statusCode("201")
                            .build()
            );

        } else {

            log.info("Creating {} subscription for customer {}", code, document);

            CustomerSubscriptions newSubscription = CustomerSubscriptions.builder()
                    .subscriptionId(subscription.getId())
                    .customerId(customer.getId())
                    .createdAt(java.time.LocalDateTime.now())
                    .updatedAt(java.time.LocalDateTime.now())
                    .canceledAt(null)
                    .status(SubscriptionStatus.ACTIVE.value())
                    .email(customer.getEmail())
                    .phone(customer.getPhone())
                    .build();

            customerSubscriptionsRepository.save(newSubscription);

            subscriptionEventProducer.publishSubscriptionCreated(
                    SubscriptionCreatedEvent.builder()
                            .customerEmail(customer.getEmail())
                            .customerName(customer.getName())
                            .subscriptionCode(code)
                            .build()
            );

            log.info("Created {} subscription for customer {}", code, document);

            return ResponseEntity.status(201).body(
                    StatusResponse.builder()
                            .status("Subscription created successfully")
                            .message("Subscription " + code + " created for customer " + document)
                            .statusCode("201")
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<StatusResponse> cancel(String document, String code) {

        log.info("Starting cancel process for customer {}, subscription {}", document, code);

        Customers customer = customersRepository.findByDocument(document).orElseThrow(CustomerNotFoundException::new);

        Subscriptions subscription = subscriptionsRepository.findByCode(code)
                .orElseThrow(SubscriptionNotFound::new);

        CustomerSubscriptions subscriptionToCancel = customerSubscriptionsRepository.findByCustomerId(customer.getId())
                .stream()
                .filter(customerSubscription -> customerSubscription.getSubscriptionId().equals(subscription.getId()))
                .findFirst()
                .orElseThrow(() -> new SubscriptionNotFound("Subscription not found for the customer"));

        if (subscriptionToCancel.getStatus().equals(SubscriptionStatus.INACTIVE.value())) {
            log.info("Subscription {} already canceled for the customer {}", code, document);
            throw new SubscriptionAlreadyCanceled("Subscription already canceled for the customer");
        }

        subscriptionToCancel.setStatus(SubscriptionStatus.INACTIVE.value());
        subscriptionToCancel.setCanceledAt(java.time.LocalDateTime.now());
        subscriptionToCancel.setUpdatedAt(java.time.LocalDateTime.now());

        customerSubscriptionsRepository.save(subscriptionToCancel);

        subscriptionEventProducer.publishSubscriptionDeleted(
                SubscriptionCanceledEvent.builder()
                        .customerEmail(customer.getEmail())
                        .customerName(customer.getName())
                        .subscriptionCode(code)
                        .build()
        );

        log.info("Canceled {} subscription for customer {}", code, document);

        return ResponseEntity.ok(
                StatusResponse.builder()
                        .status("Subscription canceled successfully")
                        .message("Subscription " + code + " canceled for customer " + document)
                        .statusCode("200")
                        .build()
        );
    }
}
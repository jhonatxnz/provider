package util;

import br.com.jhonatan.provider.model.Subscriptions;
import br.com.jhonatan.provider.model.CustomerSubscriptions;
import br.com.jhonatan.provider.model.Customers;

public class CustomerSubscriptionCreator {

    public static CustomerSubscriptions createCustomerSubscriptionToBeSaved(Customers customer, Subscriptions subscription) {
        return CustomerSubscriptions.builder()
                .customerId(customer.getId())
                .subscriptionId(subscription.getId())
                .status("1")
                .createdAt(java.time.LocalDateTime.now())
                .build();
    }

    public static CustomerSubscriptions createValidCustomerSubscription(Customers customer, Subscriptions subscription) {
        return CustomerSubscriptions.builder()
                .id(1L)
                .customerId(customer.getId())
                .subscriptionId(subscription.getId())
                .status("1")
                .createdAt(java.time.LocalDateTime.now())
                .build();
    }

    public static CustomerSubscriptions createValidUpdatedCustomerSubscription(Customers customer, Subscriptions subscription) {
        return CustomerSubscriptions.builder()
                .id(1L)
                .customerId(customer.getId())
                .subscriptionId(subscription.getId())
                .status("2")
                .createdAt(java.time.LocalDateTime.now())
                .build();
    }
}

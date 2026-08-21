package br.com.jhonatan.provider.repository;

import br.com.jhonatan.provider.model.Subscriptions;
import br.com.jhonatan.provider.model.CustomerSubscriptions;
import br.com.jhonatan.provider.model.Customers;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import util.CustomerCreator;
import util.SubscriptionCreator;
import util.CustomerSubscriptionCreator;

import java.math.BigDecimal;
import java.util.Optional;


@DataJpaTest
@DisplayName("Tests for CustomerSubscriptionsRepository")
class CustomerSubscriptionsRepositoryTest {

    @Autowired
    CustomerSubscriptionsRepository customerSubscriptionsRepository;

    @Test
    @DisplayName("Insert association when successful")
    void insert_Association_WhenSuccessful() {
        Customers customer = CustomerCreator.createValidCustomer();

        Subscriptions subscription = SubscriptionCreator.createValidSubscription();

        CustomerSubscriptions customerSubscriptionToBeSaved = CustomerSubscriptionCreator.createCustomerSubscriptionToBeSaved(customer, subscription);

        CustomerSubscriptions savedCustomerSubscriptions = this.customerSubscriptionsRepository.save(customerSubscriptionToBeSaved);

        Assertions.assertThat(savedCustomerSubscriptions).isNotNull();

        Assertions.assertThat(savedCustomerSubscriptions.getId()).isNotNull();

        Assertions.assertThat(savedCustomerSubscriptions.getCustomerId()).isEqualTo(customer.getId());

        Assertions.assertThat(savedCustomerSubscriptions.getSubscriptionId()).isEqualTo(subscription.getId());
    }

    @Test
    @DisplayName("Save throws DataIntegrityViolationException when customer is null")
    void save_ThrowsDataIntegrityViolationException_WhenCustomerIsNull() {
        Subscriptions subscription = SubscriptionCreator.createSubscriptionToBeSaved();

        CustomerSubscriptions customerSubscription = CustomerSubscriptions.builder()
                .customerId(null)
                .subscriptionId(subscription.getId())
                .status("1")
                .build();

        Assertions.assertThatThrownBy(() -> {
            customerSubscriptionsRepository.save(customerSubscription);
        }).isInstanceOf(DataIntegrityViolationException.class);

    }

    @Test
    @DisplayName("Save throws DataIntegrityViolationException when subscription is null")
    void save_ThrowsDataIntegrityViolationException_WhenSubscriptionIsNull() {
        Customers customer = CustomerCreator.createCustomerToBeSaved();

        CustomerSubscriptions customerSubscription = CustomerSubscriptions.builder()
                .customerId(customer.getId())
                .subscriptionId(null)
                .status("1")
                .build();

        Assertions.assertThatThrownBy(() -> {
            customerSubscriptionsRepository.save(customerSubscription);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Save throws ConstraintViolationException when customer not exists")
    void save_ThrowsConstraintViolationException_WhenCustomerNotExists() {
        Subscriptions subscription = SubscriptionCreator.createSubscriptionToBeSaved();

        CustomerSubscriptions customerSubscription = CustomerSubscriptions.builder()
                .customerId(2L)
                .subscriptionId(subscription.getId())
                .status("1")
                .build();

        Assertions.assertThatThrownBy(() -> {
            customerSubscriptionsRepository.save(customerSubscription);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Save throws ConstraintViolationException when subscription not exists")
    void save_ThrowsConstraintViolationException_WhenSubscriptionNotExists() {
        Customers customer = CustomerCreator.createCustomerToBeSaved();

        CustomerSubscriptions customerSubscription = CustomerSubscriptions.builder()
                .customerId(customer.getId())
                .subscriptionId(2L)
                .status("1")
                .build();

        Assertions.assertThatThrownBy(() -> {
            customerSubscriptionsRepository.save(customerSubscription);
        }).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("Update association when successful")
    void update_Association_WhenSuccessful() {
        Customers customer = CustomerCreator.createValidCustomer();

        Subscriptions subscription = SubscriptionCreator.createValidSubscription();

        CustomerSubscriptions customerSubscriptionToBeSaved = CustomerSubscriptionCreator.createCustomerSubscriptionToBeSaved(customer, subscription);

        CustomerSubscriptions savedCustomerSubscriptions = this.customerSubscriptionsRepository.save(customerSubscriptionToBeSaved);

        savedCustomerSubscriptions.setStatus("0");

        CustomerSubscriptions CustomerSubscriptions = this.customerSubscriptionsRepository.save(savedCustomerSubscriptions);

        Assertions.assertThat(CustomerSubscriptions).isNotNull();

        Assertions.assertThat(CustomerSubscriptions.getId()).isNotNull();

        Assertions.assertThat(CustomerSubscriptions.getStatus()).isEqualTo(savedCustomerSubscriptions.getStatus());

    }

    @Test
    @DisplayName("Delete removes association when successful")
    void delete_RemovesAssociation_WhenSuccessful() {
        Customers customer = CustomerCreator.createValidCustomer();

        Subscriptions subscription = SubscriptionCreator.createValidSubscription();

        CustomerSubscriptions customerSubscriptionToBeSaved = CustomerSubscriptionCreator.createCustomerSubscriptionToBeSaved(customer, subscription);

        CustomerSubscriptions savedCustomerSubscriptions = this.customerSubscriptionsRepository.save(customerSubscriptionToBeSaved);

        this.customerSubscriptionsRepository.delete(savedCustomerSubscriptions);

        Optional<CustomerSubscriptions> customerSubscriptionOptional = this.customerSubscriptionsRepository.findById(savedCustomerSubscriptions.getId());

        Assertions.assertThat(customerSubscriptionOptional).isEmpty();
    }

    @Test
    @DisplayName("Find by customer id when successful")
    void findByCustomerId_WhenSuccessful() {
        Customers customer = CustomerCreator.createValidCustomer();

        Subscriptions subscription = SubscriptionCreator.createValidSubscription();

        CustomerSubscriptions customerSubscriptionToBeSaved = CustomerSubscriptionCreator.createCustomerSubscriptionToBeSaved(customer, subscription);

        this.customerSubscriptionsRepository.save(customerSubscriptionToBeSaved);

        Assertions.assertThat(this.customerSubscriptionsRepository.findByCustomerId(customer.getId())).isNotEmpty();
    }

    

}
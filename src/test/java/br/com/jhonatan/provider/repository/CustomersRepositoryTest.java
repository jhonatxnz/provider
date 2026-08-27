package br.com.jhonatan.provider.repository;
import br.com.jhonatan.provider.model.Customers;
import jakarta.validation.ConstraintViolationException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import util.CustomerCreator;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("Tests for CustomersRepository")
class CustomersRepositoryTest {
    @Autowired
    private CustomersRepository customersRepository;

    @Test
    @DisplayName("Save creates customer when successful")
    void save_PersistCustomer_WhenSuccessful() {
        Customers customerToBeSaved = CustomerCreator.createCustomerToBeSaved();

        Customers savedCustomer = this.customersRepository.save(customerToBeSaved);

        Assertions.assertThat(savedCustomer).isNotNull();

        Assertions.assertThat(savedCustomer.getId()).isNotNull();

        Assertions.assertThat(savedCustomer.getUsername()).isEqualTo(customerToBeSaved.getUsername());
    }

    @Test
    @DisplayName("Save updates customer when successful")
    void save_UpdatesCustomer_WhenSuccessful() {

        Customers customerToBeSaved = CustomerCreator.createCustomerToBeSaved();

        Customers savedCustomer = this.customersRepository.save(customerToBeSaved);

        savedCustomer.setName("Updated customer");

        Customers updatedCustomer = this.customersRepository.save(savedCustomer);

        Assertions.assertThat(updatedCustomer).isNotNull();

        Assertions.assertThat(updatedCustomer.getId()).isNotNull();

        Assertions.assertThat(updatedCustomer.getName()).isEqualTo(updatedCustomer.getName());
    }

    @Test
    @DisplayName("Delete removes customer when successful")
    void delete_RemovesCustomer_WhenSuccessful() {

        Customers customerToBeSaved = CustomerCreator.createCustomerToBeSaved();

        Customers savedCustomer = this.customersRepository.save(customerToBeSaved);

        this.customersRepository.delete(savedCustomer);

        Optional<Customers> customerOptional = this.customersRepository.findById(savedCustomer.getId());

        Assertions.assertThat(customerOptional).isEmpty();
    }

    @Test
    @DisplayName("Find by username returns customer when successful")
    void findByUsername_ReturnsCustomer_WhenSuccessful() {
        Customers customerToBeSaved = CustomerCreator.createCustomerToBeSaved();

        Customers savedCustomer = this.customersRepository.save(customerToBeSaved);

        String username = savedCustomer.getUsername();

        Optional<Customers> customer = this.customersRepository.findByUsername(username);

        Assertions.assertThat(customer).isPresent();

        Assertions.assertThat(customer).contains(savedCustomer);
    }

    @Test
    @DisplayName("Find by username returns empty when no subscription is found")
    void findByUsername_ReturnsEmpty_WhenSuccessful() {
        Optional<Customers> customer = this.customersRepository.findByUsername("NotExistingUsername");

        Assertions.assertThat(customer).isEmpty();
    }

    @Test
    @DisplayName("Find by document returns customer when successful")
    void findByDocument_ReturnsCustomer_WhenSuccessful() {
        Customers customerToBeSaved = CustomerCreator.createCustomerToBeSaved();

        Customers savedCustomer = this.customersRepository.save(customerToBeSaved);

        String document = savedCustomer.getDocument();

        Optional<Customers> customer = this.customersRepository.findByDocument(document);

        Assertions.assertThat(customer).isPresent();

        Assertions.assertThat(customer).contains(savedCustomer);
    }

    @Test
    @DisplayName("Find by document returns empty when no customer is found")
    void findByDocument_ReturnsEmpty_WhenSuccessful() {
        Optional<Customers> customer = this.customersRepository.findByDocument("00000000000");

        Assertions.assertThat(customer).isEmpty();
    }

    @Test
    @DisplayName("Save throw ConstraintViolationException when name is empty")
    void save_ThrowDataIntegrityViolationException_WhenNameIsEmpty() {
        Customers customer = new Customers();

        Assertions.assertThatThrownBy(() -> this.customersRepository.save(customer))
                .isInstanceOf(ConstraintViolationException.class);
    }
}
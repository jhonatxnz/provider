package br.com.jhonatan.provider.service;

import br.com.jhonatan.provider.dto.CustomerRequest;
import br.com.jhonatan.provider.dto.CustomerResponse;
import br.com.jhonatan.provider.dto.CustomerUpdateRequest;
import br.com.jhonatan.provider.dto.StatusResponse;
import br.com.jhonatan.provider.enums.SubscriptionStatus;
import br.com.jhonatan.provider.exception.CustomerAlreadyExistsException;
import br.com.jhonatan.provider.exception.CustomerNotFoundException;
import br.com.jhonatan.provider.exception.InvalidEmailException;
import br.com.jhonatan.provider.exception.InvalidNameException;
import br.com.jhonatan.provider.model.CustomerSubscriptions;
import br.com.jhonatan.provider.model.Customers;
import br.com.jhonatan.provider.repository.CustomerSubscriptionsRepository;
import br.com.jhonatan.provider.repository.CustomersRepository;
import br.com.jhonatan.provider.utils.DocumentUtils;
import br.com.jhonatan.provider.utils.EmailUtils;
import br.com.jhonatan.provider.utils.NameUtils;
import br.com.jhonatan.provider.utils.PhoneUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class CustomersServiceImpl implements CustomersService {

    private final CustomersRepository customersRepository;
    private final CustomerSubscriptionsRepository customerSubscriptionsRepository;

    @Override
    public CustomerResponse getByUsername(String username) {

        Customers customer = customersRepository.findByUsername(username)
                .orElseThrow(CustomerNotFoundException::new);

        return CustomerResponse.builder()
                .username(customer.getUsername())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .name(customer.getName())
                .document(customer.getDocument())
                .build();
    }

    @Override
    public CustomerResponse getByDocument(String document) {

        Customers customer = customersRepository.findByDocument(document)
                .orElseThrow(CustomerNotFoundException::new);

        return CustomerResponse.builder()
                .username(customer.getUsername())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .name(customer.getName())
                .document(customer.getDocument())
                .build();
    }

    @Override
    public ResponseEntity<StatusResponse> create(CustomerRequest customerRequest) {

        log.info("Starting customer creation - {}", customerRequest.getDocument());

        String customerDocument = DocumentUtils.cleanDocument(customerRequest.getDocument());

        Optional<Customers> customer = customersRepository.findByDocument(customerDocument);

        if (customer.isPresent())
            throw new CustomerAlreadyExistsException();

        if (!NameUtils.isValidName(customerRequest.getName()))
            throw new InvalidNameException();

        if (!EmailUtils.isValidEmail(customerRequest.getEmail()))
            throw new InvalidEmailException();

        String customerUsername = generateUniqueUsername(customerRequest.getName());

        String customerPhoneNumber = PhoneUtils.normalizePhoneNumber(customerRequest.getPhone());

        Customers newCustomer = Customers.builder()
                .username(customerUsername)
                .name(customerRequest.getName())
                .email(customerRequest.getEmail())
                .phone(customerPhoneNumber)
                .document(customerDocument)
                .createdAt(java.time.LocalDateTime.now())
                .build();

        customersRepository.save(newCustomer);

        log.info("Finished customer creation, username created {}", customerUsername);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                StatusResponse.builder()
                        .status("success")
                        .message("customer saved successfully")
                        .statusCode("201")
                        .build()
        );
    }

    @Override
    public ResponseEntity<StatusResponse> update(String document, CustomerUpdateRequest customerUpdateRequest) {

        log.info("Updating customer with document {}", document);

        Customers customer = customersRepository.findByDocument(document)
                .orElseThrow(CustomerNotFoundException::new);

        if (!NameUtils.isValidName(customerUpdateRequest.getName()))
            throw new InvalidNameException();

        if (!EmailUtils.isValidEmail(customerUpdateRequest.getEmail()))
            throw new InvalidEmailException();

        String customerPhoneNumber = PhoneUtils.normalizePhoneNumber(customerUpdateRequest.getPhone());

        customer.setName(customerUpdateRequest.getName());
        customer.setEmail(customerUpdateRequest.getEmail());
        customer.setPhone(customerPhoneNumber);

        customersRepository.save(customer);

        List<CustomerSubscriptions> customerSubscriptions = customerSubscriptionsRepository.findByCustomerIdAndStatus(customer.getId(), SubscriptionStatus.ACTIVE.value());

        for (CustomerSubscriptions activeSubscription  : customerSubscriptions){
            activeSubscription.setEmail(customerUpdateRequest.getEmail());
            activeSubscription.setPhone(customerPhoneNumber);
            activeSubscription.setUpdatedAt(java.time.LocalDateTime.now());

            customerSubscriptionsRepository.save(activeSubscription);
        }

        log.info("Finished customer update");

        return ResponseEntity.status(HttpStatus.OK).body(
                StatusResponse.builder()
                        .status("success")
                        .message("customer and subscriptions updated successfully")
                        .statusCode("200")
                        .build()
        );
    }

    public String generateUniqueUsername(String name) {

        log.info("Generating new username for customer {}", name);

        String[] parts = name.trim().split(" ");

        String firstName = NameUtils.sanitize(parts[0]);
        String lastName = NameUtils.sanitize(parts[parts.length - 1]);

        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;

        int suffix = 1;
        while (customersRepository.findByUsername(username).isPresent()) {
            username = baseUsername + suffix;
            suffix++;
        }

        log.info("Generated username {}", username);

        return username;
    }
}

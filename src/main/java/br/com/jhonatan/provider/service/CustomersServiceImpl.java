package br.com.jhonatan.provider.service;

import br.com.jhonatan.provider.dto.CustomerRequest;
import br.com.jhonatan.provider.dto.CustomerResponse;
import br.com.jhonatan.provider.dto.StatusResponse;
import br.com.jhonatan.provider.exception.CustomerAlreadyExistsException;
import br.com.jhonatan.provider.exception.CustomerNotFoundException;
import br.com.jhonatan.provider.model.Customers;
import br.com.jhonatan.provider.repository.CustomersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomersServiceImpl implements CustomersService {

    private final CustomersRepository customersRepository;

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
    public ResponseEntity<StatusResponse> create(CustomerRequest customerRequest) {
        Optional<Customers> customer = customersRepository.findByUsername(customerRequest.getUsername());

        if (customer.isPresent())
            throw new CustomerAlreadyExistsException();

        Customers newCustomer = Customers.builder()
                .username(customerRequest.getUsername())
                .name(customerRequest.getName())
                .email(customerRequest.getEmail())
                .phone(customerRequest.getPhone())
                .document(customerRequest.getDocument())
                .createdAt(java.time.LocalDateTime.now())
                .build();

        customersRepository.save(newCustomer);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                StatusResponse.builder()
                        .status("success")
                        .message("customer saved successfully")
                        .statusCode("201")
                        .build()
        );
    }

    @Override
    public ResponseEntity<StatusResponse> update(CustomerRequest customerRequest) {
        Customers customer = customersRepository.findByUsername(customerRequest.getUsername())
                .orElseThrow(CustomerNotFoundException::new);

        customer.setName(customerRequest.getName());
        customer.setEmail(customerRequest.getEmail());
        customer.setPhone(customerRequest.getPhone());
        customer.setDocument(customerRequest.getDocument());

        customersRepository.save(customer);

        return ResponseEntity.status(HttpStatus.OK).body(
                StatusResponse.builder()
                        .status("success")
                        .message("customer updated successfully")
                        .statusCode("200")
                        .build()
        );
    }
}

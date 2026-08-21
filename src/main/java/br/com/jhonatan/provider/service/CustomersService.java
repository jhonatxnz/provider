package br.com.jhonatan.provider.service;

import br.com.jhonatan.provider.dto.CustomerRequest;
import br.com.jhonatan.provider.dto.CustomerResponse;
import br.com.jhonatan.provider.dto.StatusResponse;
import org.springframework.http.ResponseEntity;

public interface CustomersService {

    CustomerResponse getByUsername(String username);

    ResponseEntity<StatusResponse> create(CustomerRequest customerRequest);

    ResponseEntity<StatusResponse> update(CustomerRequest customerRequest);
}

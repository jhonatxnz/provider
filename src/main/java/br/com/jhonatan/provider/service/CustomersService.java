package br.com.jhonatan.provider.service;

import br.com.jhonatan.provider.dto.CustomerRequest;
import br.com.jhonatan.provider.dto.CustomerResponse;
import br.com.jhonatan.provider.dto.StatusResponse;

public interface CustomersService {

    CustomerResponse getByUsername(String username);

    StatusResponse create(CustomerRequest request);

    StatusResponse update(CustomerRequest request);
}

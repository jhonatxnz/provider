package br.com.jhonatan.provider.service;

import br.com.jhonatan.provider.dto.CustomerRequest;
import br.com.jhonatan.provider.dto.CustomerResponse;
import br.com.jhonatan.provider.dto.StatusResponse;
import br.com.jhonatan.provider.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomersServiceImpl implements CustomersService {

    private final UsersRepository usersRepository;

    @Override
    public CustomerResponse getByUsername(String username) {
        // TODO: fetch customer + subscriptions by username
        throw new UnsupportedOperationException("TODO: CustomersServiceImpl.getByUsername");
    }

    @Override
    public StatusResponse create(CustomerRequest request) {
        // TODO: create customer
        throw new UnsupportedOperationException("TODO: CustomersServiceImpl.create");
    }

    @Override
    public StatusResponse update(CustomerRequest request) {
        // TODO: update customer
        throw new UnsupportedOperationException("TODO: CustomersServiceImpl.update");
    }
}

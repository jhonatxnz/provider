package util;

import br.com.jhonatan.provider.dto.CustomerResponse;
import br.com.jhonatan.provider.model.Customers;

public class CustomerResponseCreator {

    public static CustomerResponse createCustomerResponse(Customers customer) {

        return CustomerResponse.builder()
                .username(customer.getUsername())
                .document(customer.getDocument())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .build();
    }

}

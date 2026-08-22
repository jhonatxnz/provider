package util;

import br.com.jhonatan.provider.dto.CustomerRequest;
import br.com.jhonatan.provider.model.Customers;

public class CustomerRequestCreator {

    public static CustomerRequest createCustomerRequest(Customers customer) {

        return CustomerRequest.builder()
                .document(customer.getDocument())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .build();
    }
}

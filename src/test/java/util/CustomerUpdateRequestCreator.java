package util;

import br.com.jhonatan.provider.dto.CustomerRequest;
import br.com.jhonatan.provider.dto.CustomerUpdateRequest;
import br.com.jhonatan.provider.model.Customers;

public class CustomerUpdateRequestCreator {
    public static CustomerUpdateRequest createCustomerUpdateRequest(Customers customer) {

        return CustomerUpdateRequest.builder()
                .document(customer.getDocument())
                .name(customer.getName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .build();
    }
}

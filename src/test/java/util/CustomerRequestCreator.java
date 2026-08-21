package util;

import br.com.jhonatan.provider.dto.CustomerRequest;
import br.com.jhonatan.provider.model.Users;

public class CustomerRequestCreator {

    public static CustomerRequest createCustomerRequest(Users user) {

        return CustomerRequest.builder()
                .username(user.getUsername())
                .document(user.getDocument())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }
}

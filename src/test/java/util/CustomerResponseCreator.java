package util;

import br.com.jhonatan.provider.dto.CustomerResponse;
import br.com.jhonatan.provider.model.Users;

public class CustomerResponseCreator {

    public static CustomerResponse createCustomerResponse(Users user) {

        return CustomerResponse.builder()
                .username(user.getUsername())
                .document(user.getDocument())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .build();
    }

}

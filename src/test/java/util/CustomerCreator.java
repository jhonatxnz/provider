package util;

import br.com.jhonatan.provider.model.Users;

public class CustomerCreator {

    public static Users createUserToBeSaved() {
        return Users.builder()
                .name("Test User")
                .username("testuser")
                .createdAt(java.time.LocalDateTime.now())
                .build();
    }

    public static Users createValidUser() {
        return Users.builder()
                .id(1L)
                .name("Test User")
                .username("testuser")
                .createdAt(java.time.LocalDateTime.now())
                .document("12345678900")
                .email("test.example.com.br")
                .phone("19991912323")
                .build();
    }

    public static Users createValidUpdatedUser() {
        return Users.builder()
                .id(1L)
                .name("Test User updated")
                .username("testuser")
                .createdAt(java.time.LocalDateTime.now())
                .build();
    }
}

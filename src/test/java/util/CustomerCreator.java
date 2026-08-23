package util;

import br.com.jhonatan.provider.model.Customers;

public class CustomerCreator {

    public static Customers createCustomerToBeSaved() {
        return Customers.builder()
                .name("Test Customer")
                .username("test.customer")
                .createdAt(java.time.LocalDateTime.now())
                .document("12345678900")
                .email("jw.jhonatan@gmail.com")
                .phone("19991912323")
                .build();
    }

    public static Customers createValidCustomer() {
        return Customers.builder()
                .id(1L)
                .name("Test Customer")
                .username("test.customer")
                .createdAt(java.time.LocalDateTime.now())
                .document("12345678900")
                .email("jw.jhonatan@gmail.com")
                .phone("19991912323")
                .build();
    }

    public static Customers createInvalidCustomer() {
        return Customers.builder()
                .id(1L)
                .name("Test Customer")
                .username("test.customer")
                .createdAt(java.time.LocalDateTime.now())
                .document("12345678900")
                .email("test.example.com.br")
                .phone("19991912323")
                .build();
    }

    public static Customers createValidUpdatedCustomer() {
        return Customers.builder()
                .id(1L)
                .name("Test Customer updated")
                .username("test.customer")
                .createdAt(java.time.LocalDateTime.now())
                .build();
    }
}

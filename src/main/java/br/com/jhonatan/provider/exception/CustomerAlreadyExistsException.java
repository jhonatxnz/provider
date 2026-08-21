package br.com.jhonatan.provider.exception;

public class CustomerAlreadyExistsException extends RuntimeException {

    public CustomerAlreadyExistsException() {
        super("Customer already exists");
    }

    public CustomerAlreadyExistsException(String message) {
        super(message);
    }
}

package br.com.jhonatan.provider.exception;

public class CustomerAlreadyHasSubscription extends RuntimeException{

    public CustomerAlreadyHasSubscription() {
        super("Customer already has subscription");
    }

    public CustomerAlreadyHasSubscription(String message) {
        super(message);
    }
}

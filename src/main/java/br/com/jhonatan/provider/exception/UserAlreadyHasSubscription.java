package br.com.jhonatan.provider.exception;

public class UserAlreadyHasSubscription extends RuntimeException{

    public UserAlreadyHasSubscription() {
        super("User already has subscription");
    }

    public UserAlreadyHasSubscription(String message) {
        super(message);
    }
}

package br.com.jhonatan.provider.exception;

public class SubscriptionNotFound extends RuntimeException{

    public SubscriptionNotFound() {
        super("Subscription not found");
    }

    public SubscriptionNotFound(String message) {
        super(message);
    }
}

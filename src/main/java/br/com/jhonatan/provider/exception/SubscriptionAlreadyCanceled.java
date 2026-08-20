package br.com.jhonatan.provider.exception;

public class SubscriptionAlreadyCanceled extends RuntimeException{
    public SubscriptionAlreadyCanceled() {
        super("Subscription already canceled");
    }

    public SubscriptionAlreadyCanceled(String message) {
        super(message);
    }
}

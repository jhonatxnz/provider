package br.com.jhonatan.provider.service;

public interface EmailService {
    void sendSubscriptionConfirmation(String toEmail, String customerName, String subscriptionCode);
    void sendSubscriptionCancellation(String toEmail, String customerName, String subscriptionCode);
    void sendSubscriptionReactivation(String toEmail, String customerName, String subscriptionCode);
}
package br.com.jhonatan.provider.kafka.consumer;

import br.com.jhonatan.provider.event.SubscriptionCreatedEvent;
import br.com.jhonatan.provider.event.SubscriptionCanceledEvent;
import br.com.jhonatan.provider.event.SubscriptionReactivatedEvent;
import br.com.jhonatan.provider.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class SubscriptionEventListener {

    private final EmailService emailService;

    //Read and process message, listening in topic subscription.created, then send email
    @KafkaListener(topics = "subscription.created", groupId = "provider-group")
    public void handleSubscriptionCreated(SubscriptionCreatedEvent event) {
        log.info("Sending confirmation email to {}", event.getCustomerEmail());

//        https://myaccount.google.com/apppasswords configure
//        emailService.sendSubscriptionConfirmation(
//                event.getCustomerEmail(),
//                event.getCustomerName(),
//                event.getSubscriptionCode()
//        );
    }

    //Read and process message, listening in topic subscription.canceled, then send email
    @KafkaListener(topics = "subscription.canceled", groupId = "provider-group")
    public void handleSubscriptionCanceled(SubscriptionCanceledEvent event) {
        log.info("Sending warning cancellation email to {}", event.getCustomerEmail());

//        https://myaccount.google.com/apppasswords configure
//        emailService.sendSubscriptionCancellation(
//                event.getCustomerEmail(),
//                event.getCustomerName(),
//                event.getSubscriptionCode()
//        );
    }

    //Read and process message, listening in topic subscription.reactivated, then send email
    @KafkaListener(topics = "subscription.reactivated", groupId = "provider-group")
    public void handleSubscriptionReactivated(SubscriptionReactivatedEvent event) {
        log.info("Sending warning reactivation email to {}", event.getCustomerEmail());

//        https://myaccount.google.com/apppasswords configure
//        emailService.sendSubscriptionReactivation(
//                event.getCustomerEmail(),
//                event.getCustomerName(),
//                event.getSubscriptionCode()
//        );
    }
}
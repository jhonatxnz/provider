package br.com.jhonatan.provider.kafka.consumer;

import br.com.jhonatan.provider.event.SubscriptionCreatedEvent;
import br.com.jhonatan.provider.event.SubscriptionCanceledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class SubscriptionEventListener {

    //Read and process message, listening in topic subscription.created, then send email
    @KafkaListener(topics = "subscription.created", groupId = "provider-group")
    public void handleSubscriptionCreated(SubscriptionCreatedEvent event) {
        log.info("Sending confirmation email to {}", event.getCustomerEmail());
        // emailService.sendSubscriptionConfirmation(event);
    }

    //Read and process message, listening in topic subscription.canceled, then send email
    @KafkaListener(topics = "subscription.canceled", groupId = "provider-group")
    public void handleSubscriptionCanceled(SubscriptionCanceledEvent event) {
        log.info("Sending warning email to {}", event.getCustomerEmail());
        // emailService.sendSubscriptionConfirmation(event);
    }
}
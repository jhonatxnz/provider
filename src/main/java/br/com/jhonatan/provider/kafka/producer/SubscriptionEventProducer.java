package br.com.jhonatan.provider.kafka.producer;

import br.com.jhonatan.provider.event.SubscriptionCreatedEvent;
import br.com.jhonatan.provider.event.SubscriptionCanceledEvent;
import br.com.jhonatan.provider.event.SubscriptionReactivatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class SubscriptionEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC_CREATED = "subscription.created";
    private static final String TOPIC_CANCELED = "subscription.canceled";
    private static final String TOPIC_REACTIVATED = "subscription.reactivated";

    //Publish the message to topic "subscription.created"
    public void publishSubscriptionCreated(SubscriptionCreatedEvent event) {
        log.info("Publishing event to topic {}: {}", TOPIC_CREATED, event);
        kafkaTemplate.send(TOPIC_CREATED, event);
    }

    //Publish the message to topic "subscription.canceled"
    public void publishSubscriptionCanceled(SubscriptionCanceledEvent event) {
        log.info("Publishing event to topic {}: {}", TOPIC_CANCELED, event);
        kafkaTemplate.send(TOPIC_CANCELED, event);
    }

    //Publish the message to topic "subscription.reactivated"
    public void publishSubscriptionReactivated(SubscriptionReactivatedEvent event) {
        log.info("Publishing event to topic {}: {}", TOPIC_REACTIVATED, event);
        kafkaTemplate.send(TOPIC_REACTIVATED, event);
    }
}
package br.com.jhonatan.provider.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    //Automatically creates topic when application ups, independent on demand Kafka creation waiting for  his first message
    @Bean
    public NewTopic subscriptionCreatedTopic() {
        return new NewTopic("subscription.created", 1, (short) 1);
    }

    @Bean
    public NewTopic subscriptionDeletedTopic() {
        return new NewTopic("subscription.canceled", 1, (short) 1);
    }

    @Bean
    public NewTopic subscriptionReactivatedTopic() {
        return new NewTopic("subscription.reactivated", 1, (short) 1);
    }
}
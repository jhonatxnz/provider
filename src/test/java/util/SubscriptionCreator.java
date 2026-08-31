package util;

import br.com.jhonatan.provider.model.Subscriptions;

import java.math.BigDecimal;

public class SubscriptionCreator {

    public static Subscriptions createSubscriptionToBeSaved() {

        return Subscriptions.builder()
                .name("Subscription Test")
                .code("SUBSCRIPTION_TEST")
                .category("Test category")
                .description("Description Test")
                .price(BigDecimal.valueOf(9.9))
                .status("1")
                .build();
    }
    public static Subscriptions createValidSubscription() {

        return Subscriptions.builder()
                .id(1L)
                .name("Subscription Test")
                .code("SUBSCRIPTION_TEST")
                .status("1")
                .build();
    }
    public static Subscriptions createValidUpdatedSubscription() {

        return Subscriptions.builder()
                .id(1L)
                .name("Subscription Test Updated")
                .code("SUBSCRIPTION_TEST")
                .status("1")
                .build();
    }
}

package util;

import br.com.jhonatan.provider.dto.CustomerRequest;
import br.com.jhonatan.provider.dto.SubscriptionRequest;
import br.com.jhonatan.provider.model.Subscriptions;
import br.com.jhonatan.provider.model.Customers;

public class SubscriptionRequestCreator{

public static SubscriptionRequest createSubscriptionRequest(Subscriptions subscription) {

    return SubscriptionRequest.builder()
            .name(subscription.getName())
            .code(subscription.getCode())
            .build();
    }
}
package util;

import br.com.jhonatan.provider.dto.SubscriptionResponse;
import br.com.jhonatan.provider.model.Subscriptions;

public class SubscriptionResponseCreator {
    public static SubscriptionResponse createSubscriptionResponse(Subscriptions subscription) {

        return SubscriptionResponse.builder()
                .name(subscription.getName())
                .code(subscription.getCode())
                .status(subscription.getStatus())
                .build();
    }
}

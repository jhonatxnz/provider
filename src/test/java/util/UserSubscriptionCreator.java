package util;

import br.com.jhonatan.provider.model.Subscriptions;
import br.com.jhonatan.provider.model.UserSubscriptions;
import br.com.jhonatan.provider.model.Users;

public class UserSubscriptionCreator {

    public static UserSubscriptions createUserSubscriptionToBeSaved(Users user, Subscriptions subscription) {
        return UserSubscriptions.builder()
                .userId(user.getId())
                .subscriptionId(subscription.getId())
                .status("1")
                .createdAt(java.time.LocalDateTime.now())
                .build();
    }

    public static UserSubscriptions createValidUserSubscription(Users user, Subscriptions subscription) {
        return UserSubscriptions.builder()
                .id(1L)
                .userId(user.getId())
                .subscriptionId(subscription.getId())
                .status("1")
                .createdAt(java.time.LocalDateTime.now())
                .build();
    }

    public static UserSubscriptions createValidUpdatedUserSubscription(Users user, Subscriptions subscription) {
        return UserSubscriptions.builder()
                .id(1L)
                .userId(user.getId())
                .subscriptionId(subscription.getId())
                .status("2")
                .createdAt(java.time.LocalDateTime.now())
                .build();
    }
}

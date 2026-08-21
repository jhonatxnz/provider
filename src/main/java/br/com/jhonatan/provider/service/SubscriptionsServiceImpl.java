package br.com.jhonatan.provider.service;

import br.com.jhonatan.provider.dto.StatusResponse;
import br.com.jhonatan.provider.dto.SubscriptionResponse;
import br.com.jhonatan.provider.enums.SubscriptionStatus;
import br.com.jhonatan.provider.exception.*;
import br.com.jhonatan.provider.model.Subscriptions;
import br.com.jhonatan.provider.model.UserSubscriptions;
import br.com.jhonatan.provider.model.Users;
import br.com.jhonatan.provider.repository.SubscriptionsRepository;
import br.com.jhonatan.provider.repository.UserSubscriptionsRepository;
import br.com.jhonatan.provider.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionsServiceImpl implements SubscriptionsService {

    private final UserSubscriptionsRepository userSubscriptionsRepository;
    private final UsersRepository usersRepository;
    private final SubscriptionsRepository subscriptionsRepository;

    @Override
    public List<SubscriptionResponse> list(String username) {
        Users user = usersRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        List<UserSubscriptions> subscriptions = userSubscriptionsRepository.findByUserId(user.getId());

        return subscriptions.stream()
                .map(subscription -> {
                    Subscriptions subscriptionDetails = subscriptionsRepository.findById(subscription.getSubscriptionId())
                            .orElseThrow(SubscriptionNotFound::new);

                    return SubscriptionResponse.builder()
                            .name(subscriptionDetails.getName())
                            .code(subscriptionDetails.getCode())
                            .createdAt(subscription.getCreatedAt())
                            .status(subscription.getStatus())
                            .build();
                })
                .toList();
    }

    @Override
    public ResponseEntity<StatusResponse> subscribe(String username, String code) {
        Users user = usersRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        Subscriptions subscription = subscriptionsRepository.findByCode(code)
                .orElseThrow(SubscriptionNotFound::new);

        boolean subscriptionExists = userSubscriptionsRepository.findByUserId(user.getId())
                .stream()
                .anyMatch(userSubscription -> userSubscription.getSubscriptionId().equals(subscription.getId()));

        if (subscriptionExists) {
            throw new UserAlreadyHasSubscription();
        }  else {

            UserSubscriptions newSubscription = UserSubscriptions.builder()
                    .subscriptionId(subscription.getId())
                    .userId(user.getId())
                    .createdAt(java.time.LocalDateTime.now())
                    .status(SubscriptionStatus.ACTIVE.value())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .build();

            userSubscriptionsRepository.save(newSubscription);

            return ResponseEntity.status(201).body(
                    StatusResponse.builder()
                            .status("Subscription created successfully")
                            .message("Subscription " + code + " created for user " + username)
                            .statusCode("201")
                            .build()
            );
        }
    }

    @Override
    public ResponseEntity<StatusResponse> cancel(String username, String subscriptionCode) {
        Users user = usersRepository.findByUsername(username).orElseThrow(UserNotFoundException::new);

        List<UserSubscriptions> subscriptionExists = userSubscriptionsRepository.findByUserId(user.getId());

        if(subscriptionExists.isEmpty()) {
            throw new SubscriptionNotFound("Subscription not found for the user");
        }

        if(subscriptionExists.getFirst().getStatus().equals(SubscriptionStatus.ACTIVE.value())){
            throw new SubscriptionAlreadyCanceled("Subscription already canceled for the user");
        }

        UserSubscriptions subscriptionToCancel = subscriptionExists.getFirst();
        subscriptionToCancel.setStatus(SubscriptionStatus.INACTIVE.value());
        subscriptionToCancel.setUpdatedAt(java.time.LocalDateTime.now());
        userSubscriptionsRepository.save(subscriptionToCancel);

        return ResponseEntity.ok(
                StatusResponse.builder()
                        .status("Subscription canceled successfully")
                        .message("Subscription " + subscriptionCode + " canceled for user " + username)
                        .status("200")
                        .build()
        );
    }
}
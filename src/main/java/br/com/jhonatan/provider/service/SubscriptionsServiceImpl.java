package br.com.jhonatan.provider.service;

import br.com.jhonatan.provider.dto.StatusResponse;
import br.com.jhonatan.provider.dto.SubscriptionRequest;
import br.com.jhonatan.provider.dto.SubscriptionSummary;
import br.com.jhonatan.provider.model.UserSubscriptions;
import br.com.jhonatan.provider.model.Users;
import br.com.jhonatan.provider.repository.UserSubscriptionsRepository;
import br.com.jhonatan.provider.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionsServiceImpl implements SubscriptionsService {

    private final UserSubscriptionsRepository userSubscriptionsRepository;
    private final UsersRepository usersRepository;

    @Override
    public List<SubscriptionSummary> list(String username) {
        Users user = usersRepository.findByUsername(username).orElseThrow(() -> new UnsupportedOperationException("User not found"));

        return userSubscriptionsRepository.findByUserId(user.getId())
                .stream()
                .map(subscription -> SubscriptionSummary.builder()
                        .subscription(subscription.getSubscriptionId().toString()) //change later
                        .status(subscription.getStatus())
                        .build())
                .toList();

    }

    @Override
    public StatusResponse subscribe(String username, SubscriptionRequest request) {
        Users user = usersRepository.findByUsername(username).orElseThrow(() -> new UnsupportedOperationException("User not found"));

        boolean subscriptionExists = userSubscriptionsRepository.findByUserId(user.getId())
                .stream()
                .anyMatch(subscription -> subscription.getSubscriptionId().toString().equals(request.getSubscription()));

        if (subscriptionExists) {
            throw new UnsupportedOperationException("Subscription already exists for the user");
        }  else {

            userSubscriptionsRepository.save(
                    br.com.jhonatan.provider.model.UserSubscriptions.builder() //change later
                            .subscriptionId(Long.parseLong(request.getSubscription()))
                            .userId(user.getId())
                            .createdAt(java.time.LocalDateTime.now())
                            .status("1") //active
                            .email(user.getEmail())
                            .phone(user.getPhone())
                            .build()
            );

            return StatusResponse.builder()
                    .status("Subscription created successfully")
                    .build();
        }
    }

    @Override
    public StatusResponse cancel(String username, String subscription) {
        Users user = usersRepository.findByUsername(username).orElseThrow(() -> new UnsupportedOperationException("User not found"));

        List<UserSubscriptions> subscriptionExists = userSubscriptionsRepository.findByUserId(user.getId());

        if(subscriptionExists.isEmpty()) {
            throw new UnsupportedOperationException("Subscription not found for the user");
        }

        if(!subscriptionExists.isEmpty() && subscriptionExists.getFirst().getStatus().equals("0")){
            throw new UnsupportedOperationException("Subscription already canceled for the user");
        }


        userSubscriptionsRepository.findByUserId(user.getId())
                .stream()
                .filter(sub -> sub.getSubscriptionId().toString().equals(subscription))
                .findFirst()
                .ifPresent(sub -> {
                    sub.setStatus("0"); //inactive
                    sub.setCanceledAt(java.time.LocalDateTime.now());
                    userSubscriptionsRepository.save(sub);
                });

        return StatusResponse.builder()
                .status("Subscription canceled successfully")
                .build();

    }
}

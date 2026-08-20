package br.com.jhonatan.provider.service;

import br.com.jhonatan.provider.dto.StatusResponse;
import br.com.jhonatan.provider.dto.SubscriptionRequest;
import br.com.jhonatan.provider.dto.SubscriptionSummary;
import br.com.jhonatan.provider.repository.UserSubscriptionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionsServiceImpl implements SubscriptionsService {

    private final UserSubscriptionsRepository userSubscriptionsRepository;

    @Override
    public List<SubscriptionSummary> list(String username) {
        // TODO: list the customer's subscriptions
        throw new UnsupportedOperationException("TODO: SubscriptionsServiceImpl.list");
    }

    @Override
    public StatusResponse subscribe(String username, SubscriptionRequest request) {
        // TODO: create a subscription for the customer
        throw new UnsupportedOperationException("TODO: SubscriptionsServiceImpl.subscribe");
    }

    @Override
    public StatusResponse cancel(String username, String subscription) {
        // TODO: cancel/remove the customer's subscription
        throw new UnsupportedOperationException("TODO: SubscriptionsServiceImpl.cancel");
    }
}

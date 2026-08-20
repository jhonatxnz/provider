package br.com.jhonatan.provider.service;

import br.com.jhonatan.provider.dto.StatusResponse;
import br.com.jhonatan.provider.dto.SubscriptionRequest;
import br.com.jhonatan.provider.dto.SubscriptionSummary;

import java.util.List;

public interface SubscriptionsService {

    List<SubscriptionSummary> list(String username);

    StatusResponse subscribe(String username, SubscriptionRequest request);

    StatusResponse cancel(String username, String subscription);
}

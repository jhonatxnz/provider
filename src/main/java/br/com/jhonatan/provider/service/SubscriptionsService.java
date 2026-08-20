package br.com.jhonatan.provider.service;

import br.com.jhonatan.provider.dto.StatusResponse;
import br.com.jhonatan.provider.dto.SubscriptionRequest;
import br.com.jhonatan.provider.dto.SubscriptionSummary;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SubscriptionsService {

    List<SubscriptionSummary> list(String username);

    ResponseEntity<StatusResponse> subscribe(String username, String code);

    ResponseEntity<StatusResponse> cancel(String username, String subscription);
}

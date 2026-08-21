package br.com.jhonatan.provider.service;

import br.com.jhonatan.provider.dto.StatusResponse;
import br.com.jhonatan.provider.dto.SubscriptionResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SubscriptionsService {

    List<SubscriptionResponse> list(String username);

    ResponseEntity<StatusResponse> subscribe(String username, String code);

    ResponseEntity<StatusResponse> cancel(String username, String code);
}

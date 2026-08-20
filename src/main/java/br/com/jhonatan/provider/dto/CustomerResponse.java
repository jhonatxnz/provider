package br.com.jhonatan.provider.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerResponse {

    private String username;
    private String document;
    private String pass;
    private String name;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
    private List<SubscriptionSummary> subscriptions;

    // The original spec mentioned an extra "response" field without explaining what it is.
    // Left it out until you decide what it should represent.
}

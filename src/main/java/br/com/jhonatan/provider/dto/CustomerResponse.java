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
    private String name;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
    private List<SubscriptionSummary> subscriptions;

}

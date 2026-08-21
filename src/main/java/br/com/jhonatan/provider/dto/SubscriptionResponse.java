package br.com.jhonatan.provider.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionResponse {

    private String name;

    private String code;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String status;
}

package br.com.jhonatan.provider.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRequest {

    @NotBlank
    private String clientId;

    @NotBlank
    private String clientSecret;
}
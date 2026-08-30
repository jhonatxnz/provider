package br.com.jhonatan.provider.infra.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "security")
public record SecurityProperties(Jwt jwt, List<Client> clients) {

    public record Jwt(String secret, long expirationMs) {}

    public record Client(String clientId, String clientSecret) {}
}
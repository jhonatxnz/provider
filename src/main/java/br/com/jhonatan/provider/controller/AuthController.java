package br.com.jhonatan.provider.controller;

import br.com.jhonatan.provider.dto.TokenRequest;
import br.com.jhonatan.provider.dto.TokenResponse;
import br.com.jhonatan.provider.exception.InvalidClientCredentialsException;
import br.com.jhonatan.provider.infra.security.JwtService;
import br.com.jhonatan.provider.infra.security.SecurityProperties;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RestControllerUrlBase.BASE_URL + "/auth")
@RequiredArgsConstructor

@Tag(name = "Authorization", description = "Generate token to authenticate")
public class AuthController {

    private final SecurityProperties securityProperties;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/token")
    public TokenResponse issueToken(@Valid @RequestBody TokenRequest request) {

        SecurityProperties.Client client = securityProperties.clients().stream()
                .filter(c -> c.clientId().equals(request.getClientId()))
                .findFirst()
                .orElseThrow(InvalidClientCredentialsException::new);

        if (!passwordEncoder.matches(request.getClientSecret(), client.clientSecret())) {
            throw new InvalidClientCredentialsException();
        }

        String token = jwtService.generateToken(client.clientId());

        return TokenResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(securityProperties.jwt().expirationMs() / 1000)
                .build();
    }
}
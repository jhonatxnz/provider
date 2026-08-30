package br.com.jhonatan.provider.security;

import br.com.jhonatan.provider.controller.AuthController;
import br.com.jhonatan.provider.dto.TokenRequest;
import br.com.jhonatan.provider.dto.TokenResponse;
import br.com.jhonatan.provider.exception.InvalidClientCredentialsException;
import br.com.jhonatan.provider.infra.security.JwtService;
import br.com.jhonatan.provider.infra.security.SecurityProperties;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks
    AuthController authController;

    @Mock
    JwtService jwtService;

    @Mock
    PasswordEncoder passwordEncoder;

    SecurityProperties securityProperties;

    @BeforeEach
    void setUp() {
        securityProperties = new SecurityProperties(
                new SecurityProperties.Jwt("secret", 3_600_000),
                List.of(new SecurityProperties.Client("consumer-api", "hashed-secret"))
        );

        authController = new AuthController(securityProperties, jwtService, passwordEncoder);
    }

    @Test
    @DisplayName("issueToken returns a token when credentials are valid")
    void issueToken_ReturnsToken_WhenCredentialsAreValid() {
        TokenRequest request = new TokenRequest();
        request.setClientId("consumer-api");
        request.setClientSecret("plain-secret");

        BDDMockito.when(passwordEncoder.matches("plain-secret", "hashed-secret")).thenReturn(true);
        BDDMockito.when(jwtService.generateToken("consumer-api")).thenReturn("generated-token");

        TokenResponse response = authController.issueToken(request);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getAccessToken()).isEqualTo("generated-token");
        Assertions.assertThat(response.getTokenType()).isEqualTo("Bearer");
        Assertions.assertThat(response.getExpiresIn()).isEqualTo(3_600);
    }

    @Test
    @DisplayName("issueToken throws InvalidClientCredentialsException when clientId does not exist")
    void issueToken_ThrowsInvalidClientCredentialsException_WhenClientIdDoesNotExist() {
        TokenRequest request = new TokenRequest();
        request.setClientId("nonexistent-client");
        request.setClientSecret("whatever");

        Assertions.assertThatThrownBy(() -> authController.issueToken(request))
                .isInstanceOf(InvalidClientCredentialsException.class);

        BDDMockito.then(jwtService).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("issueToken throws InvalidClientCredentialsException when secret is wrong")
    void issueToken_ThrowsInvalidClientCredentialsException_WhenSecretIsWrong() {
        TokenRequest request = new TokenRequest();
        request.setClientId("consumer-api");
        request.setClientSecret("wrong-secret");

        BDDMockito.when(passwordEncoder.matches("wrong-secret", "hashed-secret")).thenReturn(false);

        Assertions.assertThatThrownBy(() -> authController.issueToken(request))
                .isInstanceOf(InvalidClientCredentialsException.class);

        BDDMockito.then(jwtService).shouldHaveNoInteractions();
    }
}
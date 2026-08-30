package br.com.jhonatan.provider.security;

import br.com.jhonatan.provider.infra.security.JwtService;
import br.com.jhonatan.provider.infra.security.SecurityProperties;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private static final String SECRET = "MyTestKeyWithThirtyAndTwoBytesOrMore!!";

    private JwtService newService(long expirationMs) {
        SecurityProperties properties = new SecurityProperties(
                new SecurityProperties.Jwt(SECRET, expirationMs),
                java.util.List.of()
        );
        return new JwtService(properties);
    }

    @Test
    @DisplayName("generateToken creates a token that isTokenValid accepts")
    void generateToken_CreatesValidToken() {
        JwtService jwtService = newService(60_000);

        String token = jwtService.generateToken("consumer-api");

        Assertions.assertThat(token).isNotBlank();
        Assertions.assertThat(jwtService.isTokenValid(token)).isTrue();
    }

    @Test
    @DisplayName("extractClientId returns the same clientId used to generate the token")
    void extractClientId_ReturnsOriginalClientId() {
        JwtService jwtService = newService(60_000);

        String token = jwtService.generateToken("consumer-api");

        Assertions.assertThat(jwtService.extractClientId(token)).isEqualTo("consumer-api");
    }

    @Test
    @DisplayName("isTokenValid returns false for a malformed token")
    void isTokenValid_ReturnsFalse_WhenTokenIsMalformed() {
        JwtService jwtService = newService(60_000);

        Assertions.assertThat(jwtService.isTokenValid("this.is.not.a.jwt")).isFalse();
    }

    @Test
    @DisplayName("isTokenValid returns false for an expired token")
    void isTokenValid_ReturnsFalse_WhenTokenIsExpired() throws InterruptedException {
        JwtService jwtService = newService(1);

        String token = jwtService.generateToken("consumer-api");

        Thread.sleep(50);

        Assertions.assertThat(jwtService.isTokenValid(token)).isFalse();
    }

    @Test
    @DisplayName("isTokenValid returns false for a token signed with a different secret")
    void isTokenValid_ReturnsFalse_WhenSignedWithDifferentSecret() {
        JwtService jwtServiceA = newService(60_000);

        SecurityProperties propertiesB = new SecurityProperties(
                new SecurityProperties.Jwt("AnotherCompletelyDifferentKeyWithThirtyTwoBytes", 60_000),
                java.util.List.of()
        );
        JwtService jwtServiceB = new JwtService(propertiesB);

        String tokenFromA = jwtServiceA.generateToken("consumer-api");

        Assertions.assertThat(jwtServiceB.isTokenValid(tokenFromA)).isFalse();
    }
}
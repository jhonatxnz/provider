package br.com.jhonatan.provider.infra;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RestExceptionResponse {
    @JsonProperty("status")
    @Valid
    private String status;

    @JsonProperty("message")
    private String message;

    @JsonProperty("statusCode")
    private String statusCode;

}

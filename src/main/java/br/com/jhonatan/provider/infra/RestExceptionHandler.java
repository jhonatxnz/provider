package br.com.jhonatan.provider.infra;

import br.com.jhonatan.provider.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    protected ResponseEntity<RestExceptionResponse> userNotFoundHandler(UserNotFoundException e) {
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new RestExceptionResponse(
                        "error",
                        e.getMessage(),
                        "404"
                )
        );
    }

    @ExceptionHandler
    protected ResponseEntity<RestExceptionResponse> userAlreadyExistsHandler(UserAlreadyExistsException e) {
        return  ResponseEntity.status(HttpStatus.CONFLICT).body(
                new RestExceptionResponse(
                        "error",
                        e.getMessage(),
                        "409"
                )
        );
    }

    @ExceptionHandler
    protected ResponseEntity<RestExceptionResponse> subscriptionsNotFoundHandler(SubscriptionNotFound e) {
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new RestExceptionResponse(
                        "error",
                        e.getMessage(),
                        "404"
                )
        );
    }

    @ExceptionHandler
    protected ResponseEntity<RestExceptionResponse> userAlreadyHasSubscriptionHandler(UserAlreadyHasSubscription e) {
        return  ResponseEntity.status(HttpStatus.CONFLICT).body(
                new RestExceptionResponse(
                        "error",
                        e.getMessage(),
                        "409"
                )
        );
    }

    @ExceptionHandler
    protected ResponseEntity<RestExceptionResponse> subscriptionAlreadyCanceledHandler(SubscriptionAlreadyCanceled e) {
        return  ResponseEntity.status(HttpStatus.CONFLICT).body(
                new RestExceptionResponse(
                        "error",
                        e.getMessage(),
                        "409"
                )
        );
    }

}

package br.com.jhonatan.provider.infra.exceptions;

import br.com.jhonatan.provider.exception.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    protected ResponseEntity<RestExceptionResponse> customerNotFoundHandler(CustomerNotFoundException e) {
        return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                new RestExceptionResponse(
                        "error",
                        e.getMessage(),
                        "404"
                )
        );
    }

    @ExceptionHandler
    protected ResponseEntity<RestExceptionResponse> customerAlreadyExistsHandler(CustomerAlreadyExistsException e) {
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
    protected ResponseEntity<RestExceptionResponse> customerAlreadyHasSubscriptionHandler(CustomerAlreadyHasSubscription e) {
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

    @ExceptionHandler
    protected ResponseEntity<RestExceptionResponse> invalidNameException(InvalidNameException e) {
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new RestExceptionResponse(
                        "error",
                        e.getMessage(),
                        "400"
                )
        );
    }

    @ExceptionHandler
    protected ResponseEntity<RestExceptionResponse> invalidEmailException(InvalidEmailException e) {
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new RestExceptionResponse(
                        "error",
                        e.getMessage(),
                        "400"
                )
        );
    }

    @ExceptionHandler
    protected ResponseEntity<RestExceptionResponse> invalidPhoneException(InvalidPhoneException e) {
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new RestExceptionResponse(
                        "error",
                        e.getMessage(),
                        "400"
                )
        );
    }

    @ExceptionHandler
    protected ResponseEntity<RestExceptionResponse> invalidDocumentException(InvalidDocumentException e) {
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new RestExceptionResponse(
                        "error",
                        e.getMessage(),
                        "400"
                )
        );
    }

    @ExceptionHandler
    protected ResponseEntity<RestExceptionResponse> outlierException(OutLierException e) {
        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new RestExceptionResponse(
                        "error",
                        e.getMessage(),
                        "500"
                )
        );
    }

    @ExceptionHandler
    protected ResponseEntity<RestExceptionResponse> genericExceptionHandler(Exception e) {
        return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new RestExceptionResponse(
                        "error",
                        e.getMessage(),
                        "500"
                )
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining("; "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new RestExceptionResponse(
                        "error",
                        message,
                        "400"
                )
        );
    }

}

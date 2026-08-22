package br.com.jhonatan.provider.exception;

public class InvalidPhoneException extends RuntimeException {

    public InvalidPhoneException() {
        super("Invalid phone");
    }

    public InvalidPhoneException(String message) {
        super(message);
    }
}

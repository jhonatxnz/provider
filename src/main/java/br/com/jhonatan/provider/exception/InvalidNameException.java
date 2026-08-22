package br.com.jhonatan.provider.exception;

public class InvalidNameException extends RuntimeException {

    public InvalidNameException() {
        super("Invalid name");
    }

    public InvalidNameException(String message) {
        super(message);
    }

}

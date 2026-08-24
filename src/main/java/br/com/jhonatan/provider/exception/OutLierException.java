package br.com.jhonatan.provider.exception;

public class OutLierException extends RuntimeException {

    public OutLierException() {
        super("Unable to conclude requisition");
    }

    public OutLierException(String message) {
        super(message);
    }
}

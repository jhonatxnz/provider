package br.com.jhonatan.provider.exception;

public class InvalidDocumentException extends RuntimeException {

    public InvalidDocumentException() {
      super("Invalid document");
    }

    public InvalidDocumentException(String message) {
        super(message);
    }
}

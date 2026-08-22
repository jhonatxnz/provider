package br.com.jhonatan.provider.exception;

public class InvalidPhoneException extends RuntimeException {
  public InvalidPhoneException(String message) {
    super(message);
  }
}

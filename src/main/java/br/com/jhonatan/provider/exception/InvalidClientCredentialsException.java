package br.com.jhonatan.provider.exception;

public class InvalidClientCredentialsException extends RuntimeException {
  public InvalidClientCredentialsException() {
    super("Invalid client credentials");
  }
}
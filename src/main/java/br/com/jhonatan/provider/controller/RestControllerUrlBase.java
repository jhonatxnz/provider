package br.com.jhonatan.provider.controller;

/**
 * Shared path constants used by the controllers.
 * Not a @RestController itself: just holds the "/api" base prefix used by the other controllers.
 */
public final class RestControllerUrlBase {

    public static final String BASE_URL = "/api";

    private RestControllerUrlBase() {
    }
}

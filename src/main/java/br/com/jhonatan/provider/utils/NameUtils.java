package br.com.jhonatan.provider.utils;

import java.util.regex.Pattern;

public class NameUtils {

    private static final Pattern VALID_NAME_PATTERN =
            Pattern.compile("^[\\p{L}]{2,}(?: [\\p{L}]{2,})+$");

    public static Boolean isValidName(String name) {
        if (name == null) {
            return false;
        }
        return VALID_NAME_PATTERN.matcher(name).matches();
    }
}
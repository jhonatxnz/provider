package br.com.jhonatan.provider.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class NameUtils {

    private static final Pattern VALID_NAME_PATTERN =
            Pattern.compile("^[\\p{L}]{2,}(?: [\\p{L}]{2,})+$");

    private static final Pattern NON_LETTER_PATTERN =
            Pattern.compile("[^a-z]");


    public static Boolean isValidName(String name) {
        if (name == null) {
            return false;
        }
        return VALID_NAME_PATTERN.matcher(name).matches();
    }

    public static String sanitize(String value) {
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD);
        String withoutAccents = normalized.replaceAll("\\p{M}", "");
        return NON_LETTER_PATTERN.matcher(withoutAccents.toLowerCase()).replaceAll("");
    }
}
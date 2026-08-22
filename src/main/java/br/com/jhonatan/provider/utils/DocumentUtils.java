package br.com.jhonatan.provider.utils;

import br.com.jhonatan.provider.exception.InvalidDocumentException;

public class DocumentUtils {

    private static final String CPF_REGEX = "(\\d{3})(\\d{3})(\\d{3})(\\d{2})";
    private static final String CPF_FORMAT = "$1.$2.$3-$4";

    private static final String CNPJ_REGEX = "([A-Z0-9]{2})([A-Z0-9]{3})([A-Z0-9]{3})([A-Z0-9]{4})(\\d{2})";
    private static final String CNPJ_FORMAT = "$1.$2.$3/$4-$5";


    public static String cleanDocument(String document) {
        if (document == null || document.isEmpty()) {
            throw new InvalidDocumentException("Invalid document: must not be null or empty.");
        }

        String cleaned = document.toUpperCase().replaceAll("[^A-Z0-9]", "");

        if (cleaned.isEmpty()) {
            throw new InvalidDocumentException("Invalid document: no valid characters.");
        }

        if (cleaned.length() == 11 && !cleaned.matches("\\d{11}")) {
            throw new InvalidDocumentException("Invalid document: CPF must contain only digits.");
        }

        if (cleaned.length() == 14 && !cleaned.matches("[A-Z0-9]{12}\\d{2}")) {
            throw new InvalidDocumentException(
                    "Invalid document: CNPJ check digits must be numeric.");
        }

        return cleaned;
    }


    public static String formatDocument(String document) {
        String cleaned = cleanDocument(document);

        // Suporte ao caso legado de 15 caracteres (descarta o primeiro).
        if (cleaned.length() == 15) {
            cleaned = cleaned.substring(1);
        }

        if (cleaned.length() == 11) {
            return cleaned.replaceFirst(CPF_REGEX, CPF_FORMAT);
        } else if (cleaned.length() == 14) {
            return cleaned.replaceFirst(CNPJ_REGEX, CNPJ_FORMAT);
        } else {
            throw new InvalidDocumentException("Invalid document: incorrect character count.");
        }
    }

}

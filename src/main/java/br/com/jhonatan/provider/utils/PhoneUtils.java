package br.com.jhonatan.provider.utils;

import br.com.jhonatan.provider.exception.InvalidPhoneException;

public class PhoneUtils {

    public static String formatPhoneNumber(String phone) {
        String cleanedPhone = phone.replaceAll("[^0-9]", "");

        if (cleanedPhone.length() == 10) {
            cleanedPhone = cleanedPhone.substring(0, 2) + "9" + cleanedPhone.substring(2);
        } else if (cleanedPhone.length() != 11) {
            throw new InvalidPhoneException("The phone number provided is invalid. It must contain 10 or 11 digits, including the area code.");
        }

        return "+55" + cleanedPhone;
    }

    public static String normalizePhoneNumber(String phone) {
        if (phone == null || phone.isBlank()) {
            throw new InvalidPhoneException("Provide a telephone number.");
        }

        String cleanPhone = phone.replaceAll("[^0-9]", "");

        if (cleanPhone.matches("^55\\d{2}9\\d{8}$")) {
            return cleanPhone;
        }

        if (cleanPhone.matches("^\\d{2}9\\d{8}$")) {
            return "55" + cleanPhone;
        }

        throw new InvalidPhoneException("The phone number provided is invalid.");
    }
}

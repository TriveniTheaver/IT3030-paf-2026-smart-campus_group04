package com.smartcampus.incidents.validation;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Validates and normalizes preferred contact as either a structured phone number or an email.
 * Phones are stored in E.164-style (+&lt;country&gt;&lt;national&gt;).
 */
public final class PreferredContactValidator {

    private static final Pattern EMAIL = Pattern.compile(
            "^[a-zA-Z0-9_+&.-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );

    private PreferredContactValidator() {
    }

    /**
     * @param raw user input; blank or null returns null (optional field)
     * @return normalized email (lowercased) or normalized phone; never blank
     */
    public static String normalizeAndValidate(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String input = raw.trim();
        if (input.contains("@")) {
            if (!EMAIL.matcher(input).matches()) {
                throw new IllegalArgumentException("Invalid email format for preferred contact.");
            }
            return input.toLowerCase(Locale.ROOT);
        }
        return normalizePhone(input);
    }

    private static String normalizePhone(String input) {
        String compact = input.replaceAll("[\\s().-]", "");
        if (compact.startsWith("+")) {
            String digits = compact.substring(1).replaceAll("\\D", "");
            if (digits.length() < 8 || digits.length() > 15) {
                throw new IllegalArgumentException(
                        "Phone number must have 8–15 digits after the country code (E.164).");
            }
            if (!digits.matches("[1-9]\\d*")) {
                throw new IllegalArgumentException("Invalid phone number.");
            }
            return "+" + digits;
        }
        String digits = compact.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            throw new IllegalArgumentException("Enter a phone number or an email address.");
        }
        // Sri Lanka mobile: 077 XXX XXXX → +9477XXXXXXX
        if (digits.length() == 10 && digits.startsWith("07")) {
            return "+94" + digits.substring(1);
        }
        if (digits.length() == 11 && digits.startsWith("94") && digits.charAt(2) == '7') {
            return "+" + digits;
        }
        if (digits.length() == 9 && digits.startsWith("7")) {
            return "+94" + digits;
        }
        if (digits.length() >= 8 && digits.length() <= 15 && !digits.startsWith("0")) {
            return "+" + digits;
        }
        throw new IllegalArgumentException(
                "Invalid phone number. Use Sri Lanka mobile (e.g. 077 123 4567), international (+94 77 123 4567), "
                        + "or a valid email.");
    }
}

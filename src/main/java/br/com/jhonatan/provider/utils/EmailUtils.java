package br.com.jhonatan.provider.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class EmailUtils {

    private static final Set<String> VALID_DOMAINS = new HashSet<>(Arrays.asList(
            "gmail.com", "googlemail.com", "hotmail.com", "outlook.com",
            "live.com", "yahoo.com", "uol.com.br", "bol.com.br", "terra.com.br"
    ));

    private static final Set<String> VALID_TLDS = new HashSet<>(Arrays.asList(
            "com", "net", "org", "edu", "gov", "mil", "int",
            "com.br", "net.br", "org.br", "gov.br", "edu.br",
            "br", "us", "uk", "ca", "au", "de", "fr", "io", "dev", "tech", "app"
    ));

    private static final Set<String> DOMAIN_TYPOS = Set.of(
            "@gnail.", "@gmai.", "@gmial.", "@gmali.", "@gmaul.", "@gmil.", "@gmal.",
            "@gmaill.", "@gmeil.", "@gmsil.", "@gmaiil.",
            "@gimail.", "@gamail.", "@gamil.", "@gmaio.", "@fmail.",
            "@vmail.", "@tmail.", "@gmzil.", "@gmqil.", "@gmnail.", "@gfmail.",
            "@gmailc.", "@gmaik.", "@gmail.com.br",
            "@hotmai.", "@hotmial.", "@hotmal.", "@hotmali.", "@hotmeil.",
            "@hotmil.", "@htomail.", "@hotmaik.", "@hotmaiil.", "@hotmqil.",
            "@hotnail.", "@hotmaill.", "@hotmaio.", "@hotmsil.",
            "@hotmzil.", "@gotmail.", "@homtail.", "@hoymail.", "@hormail.",
            "@hotmailc.", "@htmail.", "@homail.", "@hotma.",
            "@outloo.", "@outlool.", "@outloook.", "@outlookc.",
            "@outtlook.", "@outllook.", "@outlok.",
            "@yaho.", "@yahooo.", "@yahool.", "@yhoo.", "@yhaoo.", "@yaoo."
    );

    private static final Set<String> TLD_TYPOS = Set.of(
            ".comm", ".con", ".coom", ".cm", ".ocm", ".nt", ".met", ".og", ".ogr",
            ".combr", ".com.r", ".com.b"
    );

    private static final Pattern BASIC_EMAIL_PATTERN = Pattern.compile("^[\\w\\.-]+@[\\w\\.-]+\\.\\w{2,}$");

    public static boolean isValidEmail(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }

        String emailLower = email.toLowerCase().trim();

        if (emailLower.length() < 6 || !BASIC_EMAIL_PATTERN.matcher(emailLower).matches()) {
            return false;
        }

        if (!isValidFormat(emailLower)) {
            return false;
        }

        if (containsCommonTypo(emailLower)) {
            return false;
        }

        int atIndex = emailLower.indexOf('@');
        String domain = emailLower.substring(atIndex + 1);

        return isValidDomain(domain);
    }

    private static boolean isValidFormat(String email) {
        if (email.contains(" ") || email.contains("..")) {
            return false;
        }

        if (email.indexOf('@') != email.lastIndexOf('@')) {
            return false;
        }

        int atIndex = email.indexOf('@');
        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex + 1);

        if (localPart.startsWith(".") || localPart.endsWith(".") || localPart.startsWith("-") || localPart.endsWith("-")) {
            return false;
        }

        if (domain.startsWith(".") || domain.endsWith(".") || domain.startsWith("-") || domain.endsWith("-")) {
            return false;
        }

        return true;
    }

    private static boolean containsCommonTypo(String email) {
        for (String typo : TLD_TYPOS) {
            if (email.endsWith(typo)) {
                return true;
            }
        }

        int atIndex = email.indexOf('@');
        String domainPart = email.substring(atIndex);

        for (String typo : DOMAIN_TYPOS) {
            if (domainPart.contains(typo)) {
                return true;
            }
        }

        return false;
    }


    private static boolean isValidDomain(String domain) {
        if (domain.length() < 3) {
            return false;
        }

        if (VALID_DOMAINS.contains(domain)) {
            return true;
        }

        String[] parts = domain.split("\\.");
        if (parts.length < 2) {
            return false;
        }

        String tld = parts[parts.length - 1];
        if (tld.matches("^\\d+$")) {
            return false;
        }

        return hasValidTLD(domain);
    }

    private static boolean hasValidTLD(String domain) {
        String[] parts = domain.split("\\.");

        if (parts.length < 2) {
            return false;
        }

        if (parts.length >= 3) {
            String compoundTld = parts[parts.length - 2] + "." + parts[parts.length - 1];
            if (VALID_TLDS.contains(compoundTld)) {
                return true;
            }
        }

        String simpleTld = parts[parts.length - 1];

        if (VALID_TLDS.contains(simpleTld)) {
            return true;
        }

        if (simpleTld.length() >= 2 && simpleTld.length() <= 10 && simpleTld.matches("^[a-z]+$")) {
            return true;
        }

        return false;
    }
}
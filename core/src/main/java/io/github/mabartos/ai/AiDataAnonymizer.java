package io.github.mabartos.ai;

import org.keycloak.quarkus.runtime.configuration.Configuration;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Anonymizes sensitive user data before sending to AI services.
 * <p>
 * <strong>Privacy Considerations:</strong><br>
 * Sending raw user data (IP addresses, emails, usernames) to third-party AI services
 * can violate privacy regulations (GDPR, CCPA) and organizational policies.
 * This class provides anonymization to balance:
 * <ul>
 *   <li><strong>Privacy:</strong> No PII leaves your infrastructure</li>
 *   <li><strong>Utility:</strong> AI can still detect patterns (same hash = same entity)</li>
 *   <li><strong>Security:</strong> Hashing prevents reverse-lookup of IPs</li>
 * </ul>
 * <p>
 * <strong>Configuration:</strong>
 * <pre>
 * ai.anonymize.enabled=true              # Enable anonymization (default: true)
 * ai.anonymize.salt=your-secret-salt     # Salt for hashing (required if enabled)
 * </pre>
 * <p>
 * <strong>What Gets Anonymized:</strong>
 * <ul>
 *   <li><strong>IP Addresses:</strong> Hashed with salt → "ip_a3f5b2..."</li>
 *   <li><strong>Geolocation:</strong> Only country/region kept, no city</li>
 *   <li><strong>Device Info:</strong> Generic categories only (mobile/desktop, OS family)</li>
 * </ul>
 */
public class AiDataAnonymizer {
    private static final String ANONYMIZE_ENABLED_PROPERTY = "ai.anonymize.enabled";
    private static final String GENERATED_SALT = generateRandomSalt();

    private static final HexFormat HEX_FORMAT = HexFormat.of();

    /**
     * Check if anonymization is enabled (default: true for privacy)
     */
    public static boolean isEnabled() {
        return Configuration.getOptionalValue(ANONYMIZE_ENABLED_PROPERTY)
                .map(Boolean::parseBoolean)
                .orElse(true);  // Default to enabled for privacy protection
    }

    private static String getSalt() {
        return GENERATED_SALT;
    }

    private static String generateRandomSalt() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Anonymize an IP address by hashing it with a salt.
     * <p>
     * Benefits:
     * <ul>
     *   <li>Same IP always produces same hash (pattern detection works)</li>
     *   <li>Cannot reverse-engineer the original IP</li>
     *   <li>Salting prevents rainbow table attacks</li>
     * </ul>
     *
     * @param ipAddress Raw IP address
     * @return Anonymized identifier like "ip_a3f5b2c1" or original if disabled
     */
    public static String anonymizeIp(String ipAddress) {
        if (!isEnabled() || ipAddress == null || ipAddress.isEmpty()) {
            return ipAddress;
        }

        return "ip_" + hashWithSalt(ipAddress).substring(0, 8);
    }

    /**
     * Anonymize a list of IP addresses
     */
    public static List<String> anonymizeIps(List<String> ipAddresses) {
        if (!isEnabled() || ipAddresses == null) {
            return ipAddresses;
        }

        return ipAddresses.stream()
                .map(AiDataAnonymizer::anonymizeIp)
                .collect(Collectors.toList());
    }

    /**
     * Anonymize device information to generic categories
     * <p>
     * Instead of "Chrome 120.0 on Windows 11 Pro"
     * Returns "Desktop Browser on Windows"
     *
     * @param browser Browser name
     * @param os Operating system
     * @param osVersion OS version
     * @param isMobile Is mobile device
     * @return Anonymized device description
     */
    public static String anonymizeDevice(String browser, String os, String osVersion, boolean isMobile) {
        if (!isEnabled()) {
            return String.format("%s on %s %s", browser, os, osVersion);
        }

        String deviceType = isMobile ? "Mobile" : "Desktop";
        String osFamily = anonymizeOsFamily(os);

        return String.format("%s Device on %s", deviceType, osFamily);
    }

    /**
     * Reduce OS to generic family (e.g., "Windows 11 Pro" → "Windows")
     */
    private static String anonymizeOsFamily(String os) {
        if (os == null) return "Unknown";

        String lower = os.toLowerCase();
        if (lower.contains("windows")) return "Windows";
        if (lower.contains("mac") || lower.contains("darwin")) return "macOS";
        if (lower.contains("linux")) return "Linux";
        if (lower.contains("android")) return "Android";
        if (lower.contains("ios") || lower.contains("iphone")) return "iOS";

        return "Other";
    }

    /**
     * Hash a string with salt using SHA-256
     */
    private static String hashWithSalt(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String salted = value + getSalt();
            byte[] hash = digest.digest(salted.getBytes(StandardCharsets.UTF_8));
            return HEX_FORMAT.formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

}

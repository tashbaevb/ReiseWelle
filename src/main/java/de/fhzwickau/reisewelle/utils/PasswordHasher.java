package de.fhzwickau.reisewelle.utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public class PasswordHasher {

    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 100_000;
    private static final int KEY_LENGTH = 256;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";

    public record HashResult(String hashBase64, String saltBase64) {
    }

    public static HashResult hashPassword(String password) throws Exception {
        byte[] salt = new byte[SALT_LENGTH];
        SecureRandom.getInstanceStrong().nextBytes(salt);

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] hash = factory.generateSecret(spec).getEncoded();

        return new HashResult(
                Base64.getEncoder().encodeToString(hash),
                Base64.getEncoder().encodeToString(salt)
        );
    }

    public static boolean verifyPassword(String inputPassword, String storedHashBase64, String storedSaltBase64) throws Exception {
        byte[] salt = Base64.getDecoder().decode(storedSaltBase64);
        byte[] expectedHash = Base64.getDecoder().decode(storedHashBase64);

        KeySpec spec = new PBEKeySpec(inputPassword.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
        byte[] inputHash = factory.generateSecret(spec).getEncoded();

        return java.security.MessageDigest.isEqual(inputHash, expectedHash);
    }
}

package mx.uv.lis.professionalpracticesystem.logic.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.HASH_ALGORITHM;
import static mx.uv.lis.professionalpracticesystem.logic.utils.SystemConstants.PASSWORD_COMPLEXITY_REGEX;

/**
 *
 * @author cinth
 * @author andre
 */
public class PasswordManager {

    private static final Logger LOGGER = Logger.getLogger(PasswordManager.class.getName());

    private PasswordManager() {
    }

    public static String hashPassword(String password) {
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException exception) {
            LOGGER.log(Level.SEVERE, "Error initializing hashing algorithm", exception);
        }
        return hexString.toString();
    }

    public static boolean checkPassword(String plainPassword, String storedHash) {
        String hashedInput = hashPassword(plainPassword);
        return hashedInput.equals(storedHash);
    }

    public static boolean isStrongPassword(String password) {
        return password != null && password.matches(PASSWORD_COMPLEXITY_REGEX);
    }
}

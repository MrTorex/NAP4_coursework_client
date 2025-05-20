package by.mrtorex.businessshark.server.utils;

import org.mindrot.jbcrypt.BCrypt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Утилита для хеширования паролей.
 */
public class PasswordHasher {
    private static final Logger logger = LogManager.getLogger(PasswordHasher.class);

    /**
     * Хеширует пароль с использованием BCrypt.
     *
     * @param password пароль для хеширования
     * @return хешированный пароль
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, generateSalt(password));
    }

    /**
     * Генерирует соль для хеширования пароля.
     *
     * @param password пароль, для которого генерируется соль
     * @return сгенерированная соль
     */
    private static String generateSalt(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedPassword = digest.digest(password.getBytes());

            StringBuilder salt = new StringBuilder();
            for (byte b : hashedPassword) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) salt.append('0');
                salt.append(hex);
            }

            return "$2a$10$" + salt.substring(0, 22);
        } catch (NoSuchAlgorithmException e) {
            logger.error("Ошибка при генерации соли: {}", e.getMessage());
            throw new RuntimeException("Не удалось сгенерировать соль", e);
        }
    }
}

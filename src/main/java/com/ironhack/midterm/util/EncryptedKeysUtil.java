package com.ironhack.midterm.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EncryptedKeysUtil {

  public static String encryptedKey(String key) {
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
    return passwordEncoder.encode(key);
  }

  public static String generateSecretKey() throws NoSuchAlgorithmException {
    KeyGenerator keyGen = KeyGenerator.getInstance("AES");
    keyGen.init(192);
    SecretKey secretKey = keyGen.generateKey();
    byte[] encoded = secretKey.getEncoded();
    return Base64.getEncoder().encodeToString(encoded);
  }


}

package com.ironhack.midterm.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.NoSuchAlgorithmException;

import static com.ironhack.midterm.util.EncryptedKeysUtil.encryptedKey;
import static com.ironhack.midterm.util.EncryptedKeysUtil.generateSecretKey;
import static org.junit.jupiter.api.Assertions.*;

class EncryptedKeysUtilTest {

  // ==================== Test Encrypted Key ==========
  @Test
  void testEncryptedKey_correctWord_recognizes() {
    PasswordEncoder encrypt = new BCryptPasswordEncoder();

    var encryptedWord = encryptedKey("password123");
    assertTrue(encrypt.matches("password123", encryptedWord));
  }

  @Test
  void testEncryptedKey_incorrectWord_doesNotRecognize() {
    PasswordEncoder encrypt = new BCryptPasswordEncoder();

    var encryptedWord = encryptedKey("password123");
    assertFalse(encrypt.matches("otherPassword", encryptedWord));
  }

  // ==================== Test Generate Secret Key==========
  @Test
  void testGenerateSecretKey() throws NoSuchAlgorithmException {
    assertEquals(32, generateSecretKey().length());
    System.out.println(generateSecretKey());

  }

}
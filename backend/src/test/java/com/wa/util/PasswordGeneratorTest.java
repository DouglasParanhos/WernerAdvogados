package com.wa.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorTest {

    @Test
    void testGeneratePassword_ReturnsPasswordWithCorrectLength() {
        // Act
        String password = PasswordGenerator.generatePassword();

        // Assert
        assertNotNull(password);
        assertEquals(8, password.length());
    }

    @Test
    void testGeneratePassword_ContainsAlphanumericAndSpecialCharacters() {
        // Act
        String password = PasswordGenerator.generatePassword();

        // Assert
        assertNotNull(password);
        assertTrue(password.matches(".*[a-z].*"), "Password should contain lowercase letters");
        assertTrue(password.matches(".*[A-Z].*"), "Password should contain uppercase letters");
        assertTrue(password.matches(".*[0-9].*"), "Password should contain numbers");
        assertTrue(password.matches(".*[!@#$%^&*].*"), "Password should contain special characters");
    }

    @Test
    void testGeneratePassword_GeneratesDifferentPasswords() {
        // Act
        String password1 = PasswordGenerator.generatePassword();
        String password2 = PasswordGenerator.generatePassword();
        String password3 = PasswordGenerator.generatePassword();

        // Assert
        assertNotEquals(password1, password2);
        assertNotEquals(password2, password3);
        assertNotEquals(password1, password3);
    }

    @Test
    void testGeneratePassword_OnlyUsesAllowedCharacters() {
        // Act
        String password = PasswordGenerator.generatePassword();

        // Assert
        assertTrue(password.matches("[a-zA-Z0-9!@#$%^&*]{8}"), 
                "Password should only contain allowed characters");
    }

    @Test
    void testGeneratePassword_MultipleCalls_AllValid() {
        // Act & Assert
        for (int i = 0; i < 100; i++) {
            String password = PasswordGenerator.generatePassword();
            assertNotNull(password);
            assertEquals(8, password.length());
            assertTrue(password.matches("[a-zA-Z0-9!@#$%^&*]{8}"));
        }
    }
}


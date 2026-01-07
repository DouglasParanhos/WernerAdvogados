package com.wa.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CpfValidatorTest {

    private CpfValidator validator;

    @Mock
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        validator = new CpfValidator();
        validator.initialize(null);
    }

    @Test
    void testValidCpf_WithFormatting_ReturnsTrue() {
        // CPFs válidos com formatação
        assertTrue(validator.isValid("123.456.789-09", context));
        assertTrue(validator.isValid("111.444.777-35", context));
    }

    @Test
    void testValidCpf_WithoutFormatting_ReturnsTrue() {
        // CPFs válidos sem formatação
        assertTrue(validator.isValid("12345678909", context));
        assertTrue(validator.isValid("11144477735", context));
    }

    @Test
    void testInvalidCpf_AllSameDigits_ReturnsFalse() {
        // CPFs com todos os dígitos iguais são inválidos
        assertFalse(validator.isValid("11111111111", context));
        assertFalse(validator.isValid("22222222222", context));
        assertFalse(validator.isValid("00000000000", context));
    }

    @Test
    void testInvalidCpf_WrongLength_ReturnsFalse() {
        // CPFs com tamanho incorreto
        assertFalse(validator.isValid("123456789", context)); // 9 dígitos
        assertFalse(validator.isValid("123456789012", context)); // 12 dígitos
        assertFalse(validator.isValid("123", context)); // 3 dígitos
    }

    @Test
    void testInvalidCpf_WrongCheckDigits_ReturnsFalse() {
        // CPFs com dígitos verificadores incorretos
        assertFalse(validator.isValid("12345678900", context));
        assertFalse(validator.isValid("11144477700", context));
    }

    @Test
    void testNullCpf_ReturnsTrue() {
        // Null deve retornar true (validação de @NotNull deve ser feita separadamente)
        assertTrue(validator.isValid(null, context));
    }

    @Test
    void testEmptyCpf_ReturnsTrue() {
        // String vazia deve retornar true (validação de @NotBlank deve ser feita separadamente)
        assertTrue(validator.isValid("", context));
        assertTrue(validator.isValid("   ", context));
    }

    @Test
    void testCpf_WithNonNumericCharacters_RemovesAndValidates() {
        // Deve remover caracteres não numéricos e validar
        assertTrue(validator.isValid("123.456.789-09", context));
        assertTrue(validator.isValid("123 456 789 09", context));
        assertTrue(validator.isValid("123-456-789-09", context));
    }

    @Test
    void testCpf_WithSpecialCharacters_RemovesAndValidates() {
        // Deve remover caracteres especiais
        assertTrue(validator.isValid("123.456.789-09", context));
        assertFalse(validator.isValid("abc.def.ghi-jk", context)); // Não tem números suficientes
    }

    @Test
    void testKnownValidCpfs() {
        // CPFs conhecidos como válidos
        assertTrue(validator.isValid("11144477735", context));
        assertTrue(validator.isValid("12345678909", context));
    }

    @Test
    void testKnownInvalidCpfs() {
        // CPFs conhecidos como inválidos
        assertFalse(validator.isValid("12345678900", context));
        assertFalse(validator.isValid("00000000000", context));
    }
}


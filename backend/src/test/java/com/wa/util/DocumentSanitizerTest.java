package com.wa.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DocumentSanitizerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testSanitizeText_RemovesDangerousCharacters() {
        // Arrange
        String text = "Hello<script>alert('xss')</script>World";
        
        // Act
        String result = DocumentSanitizer.sanitizeText(text);
        
        // Assert
        assertNotNull(result);
        assertFalse(result.contains("<script>"));
        assertFalse(result.contains("</script>"));
    }

    @Test
    void testSanitizeText_EscapesHtmlCharacters() {
        // Arrange
        String text = "Test < > & \" '";
        
        // Act
        String result = DocumentSanitizer.sanitizeText(text);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.contains("&lt;"));
        assertTrue(result.contains("&gt;"));
        assertTrue(result.contains("&amp;"));
    }

    @Test
    void testSanitizeText_HandlesNull() {
        // Act
        String result = DocumentSanitizer.sanitizeText(null);
        
        // Assert
        assertEquals("", result);
    }

    @Test
    void testSanitizeText_TruncatesLongText() {
        // Arrange
        String longText = "a".repeat(2000000); // 2MB
        
        // Act
        String result = DocumentSanitizer.sanitizeText(longText);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.length() <= 1000000); // MAX_TEXT_LENGTH
    }

    @Test
    void testValidateTemplateName_ValidName_ReturnsTrue() {
        // Arrange
        String validName = "template.docx";
        
        // Act
        boolean result = DocumentSanitizer.validateTemplateName(validName);
        
        // Assert
        assertTrue(result);
    }

    @Test
    void testValidateTemplateName_WithPathTraversal_ReturnsFalse() {
        // Arrange
        String invalidName = "../../etc/passwd";
        
        // Act
        boolean result = DocumentSanitizer.validateTemplateName(invalidName);
        
        // Assert
        assertFalse(result);
    }

    @Test
    void testValidateTemplateName_WithAbsolutePath_ReturnsFalse() {
        // Arrange
        String invalidName = "/etc/passwd";
        
        // Act
        boolean result = DocumentSanitizer.validateTemplateName(invalidName);
        
        // Assert
        assertFalse(result);
    }

    @Test
    void testValidateTemplateName_WithSpecialCharacters_ReturnsFalse() {
        // Arrange
        String invalidName = "template<script>.docx";
        
        // Act
        boolean result = DocumentSanitizer.validateTemplateName(invalidName);
        
        // Assert
        assertFalse(result);
    }

    @Test
    void testValidateQuillDelta_ValidDelta_ReturnsTrue() throws Exception {
        // Arrange
        String deltaJson = """
                {
                    "ops": [
                        {"insert": "Hello "},
                        {"insert": "World", "attributes": {"bold": true}},
                        {"insert": "\\n"}
                    ]
                }
                """;
        JsonNode delta = objectMapper.readTree(deltaJson);
        
        // Act
        boolean result = DocumentSanitizer.validateQuillDelta(delta);
        
        // Assert
        assertTrue(result);
    }

    @Test
    void testValidateQuillDelta_InvalidStructure_ReturnsFalse() {
        // Arrange
        String invalidJson = "{\"notOps\": []}";
        
        // Act
        boolean result = DocumentSanitizer.validateQuillDelta(invalidJson);
        
        // Assert
        assertFalse(result);
    }

    @Test
    void testValidateQuillDelta_TooManyOps_ReturnsFalse() throws Exception {
        // Arrange
        StringBuilder ops = new StringBuilder("[");
        for (int i = 0; i < 10001; i++) {
            if (i > 0) ops.append(",");
            ops.append("{\"insert\": \"text\"}");
        }
        ops.append("]");
        String deltaJson = "{\"ops\": " + ops + "}";
        JsonNode delta = objectMapper.readTree(deltaJson);
        
        // Act
        boolean result = DocumentSanitizer.validateQuillDelta(delta);
        
        // Assert
        assertFalse(result);
    }

    @Test
    void testValidateQuillDelta_WithInvalidAttribute_ReturnsFalse() throws Exception {
        // Arrange
        String deltaJson = """
                {
                    "ops": [
                        {"insert": "Test", "attributes": {"onclick": "alert('xss')"}}
                    ]
                }
                """;
        JsonNode delta = objectMapper.readTree(deltaJson);
        
        // Act
        boolean result = DocumentSanitizer.validateQuillDelta(delta);
        
        // Assert
        assertFalse(result);
    }

    @Test
    void testSanitizeQuillDelta_ValidDelta_ReturnsSanitized() throws Exception {
        // Arrange
        String deltaJson = """
                {
                    "ops": [
                        {"insert": "Hello "},
                        {"insert": "World", "attributes": {"bold": true}}
                    ]
                }
                """;
        JsonNode delta = objectMapper.readTree(deltaJson);
        
        // Act
        JsonNode result = DocumentSanitizer.sanitizeQuillDelta(delta);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.has("ops"));
        assertTrue(result.get("ops").isArray());
    }

    @Test
    void testEscapeForWord_RemovesControlCharacters() {
        // Arrange
        String text = "Hello\u0000World\u0001Test";
        
        // Act
        String result = DocumentSanitizer.escapeForWord(text);
        
        // Assert
        assertNotNull(result);
        assertFalse(result.contains("\u0000"));
        assertFalse(result.contains("\u0001"));
    }

    @Test
    void testEscapeForWord_HandlesNull() {
        // Act
        String result = DocumentSanitizer.escapeForWord(null);
        
        // Assert
        assertEquals("", result);
    }
}


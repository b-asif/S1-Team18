package com.myapp.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ResetTokenUtilTest {

    @Test
    void generatePlainToken_returnsNonEmptyUrlSafeToken() {
        String token = ResetTokenUtil.generatePlainToken();

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertFalse(token.contains("+"));
        assertFalse(token.contains("/"));
        assertFalse(token.contains("="));
    }

    @Test
    void sha256_isDeterministicAndDistinct() {
        String one = ResetTokenUtil.sha256("same-input");
        String two = ResetTokenUtil.sha256("same-input");
        String three = ResetTokenUtil.sha256("different-input");

        assertEqualsNonBlank(one);
        assertEqualsNonBlank(two);
        assertNotEquals(one, three);
        assertTrue(one.equals(two));
    }

    private void assertEqualsNonBlank(String value) {
        assertNotNull(value);
        assertFalse(value.isBlank());
    }
}

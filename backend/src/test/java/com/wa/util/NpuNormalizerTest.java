package com.wa.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NpuNormalizerTest {

    @Test
    void normalize_twentyDigits_formatsCnj() {
        assertThat(NpuNormalizer.normalizeNpu("00591977520238190000"))
                .isEqualTo("0059197-75.2023.8.19.0000");
    }

    @Test
    void normalize_alreadyFormatted_unchanged() {
        assertThat(NpuNormalizer.normalizeNpu("0059197-75.2023.8.19.0000"))
                .isEqualTo("0059197-75.2023.8.19.0000");
    }

    @Test
    void normalize_short_returnsTrimmed() {
        assertThat(NpuNormalizer.normalizeNpu("  abc  ")).isEqualTo("abc");
    }
}

package com.example.boobybank.controlTests;

import com.example.boobybank.control.shared.HashUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HashUtilTest {

    private final HashUtil hashUtil = new HashUtil();

    @Test
    void sha256_sameInput_producesSameHash() {
        assertThat(hashUtil.sha256("password")).isEqualTo(hashUtil.sha256("password"));
    }

    @Test
    void sha256_differentInput_producesDifferentHash() {
        assertThat(hashUtil.sha256("password1")).isNotEqualTo(hashUtil.sha256("password2"));
    }

    @Test
    void sha256_emptyString_doesNotThrow() {
        assertThat(hashUtil.sha256("")).isNotBlank();
    }

    @Test
    void sha256_output_isLowercaseHex64Chars() {
        String result = hashUtil.sha256("anything");

        assertThat(result).hasSize(64).matches("[0-9a-f]+");
    }
}

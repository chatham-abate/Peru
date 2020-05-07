package com.github.chathamabate.peru.preset.base;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class TestBaseOperator {

    // Base Operator Cast Error testing.
    // Successful Tests are found in TestBaseOperatorSet.
    @Test
    void testErrors() {
        assertThrows(ClassCastException.class, () -> {
           BaseOperatorUtil.PLUS_SEQUENCE.apply(BaseValue.ofInt(1), BaseValue.ofInt(2));
        });

        assertThrows(ClassCastException.class, () -> {
            BaseOperatorUtil.PLUS_INT.apply(BaseValue.ofDouble(1.0), BaseValue.ofDouble(2.0));
        });
    }

}

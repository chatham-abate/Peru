package org.perudevteam.peru.base;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import static org.perudevteam.peru.base.BaseValue.*;
import static org.perudevteam.peru.base.BaseOperatorUtil.*;

public class TestBaseOperator {

    // Base Operator Cast Error testing.
    // Successful Tests are found in TestBaseOperatorSet.
    @Test
    void testErrors() {
        assertThrows(ClassCastException.class, () -> {
           PLUS_SEQUENCE.apply(ofInt(1), ofInt(2));
        });

        assertThrows(ClassCastException.class, () -> {
            PLUS_INT.apply(ofDouble(1.0), ofDouble(2.0));
        });
    }

}

package org.perudevteam.type.base;

import static org.junit.jupiter.api.Assertions.*;

import io.vavr.collection.List;
import io.vavr.collection.Seq;
import org.junit.jupiter.api.Test;

import org.perudevteam.type.base.BaseValue;
import static org.perudevteam.type.base.BaseValue.*;
import static org.perudevteam.type.base.BaseOperatorUtil.*;

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

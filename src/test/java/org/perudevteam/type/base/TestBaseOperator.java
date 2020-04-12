package org.perudevteam.type.base;

import static org.junit.jupiter.api.Assertions.*;

import io.vavr.collection.List;
import org.junit.jupiter.api.Test;

import org.perudevteam.type.base.BaseValue;
import static org.perudevteam.type.base.BaseValue.*;
import static org.perudevteam.type.base.BaseOperatorUtil.*;

public class TestBaseOperator {

    @Test
    void testBasics() {
        BaseValue i1 = ofInt(10);
        BaseValue i2 = ofDouble(20);

        assertEquals(BaseType.DOUBLE, PLUS_DOUBLE.tryApply(i1, i2).get().getTag());
        assertEquals(200.0, TIMES_DOUBLE.tryApply(i1, i2).get().toDouble());
        assertTrue(LT_DOUBLE.tryApply(i1, i2).get().toBoolean());

        BaseValue s1 = ofString("BA");
        BaseValue s2 = ofString("AB");

        assertEquals("BAAB", PLUS_STRING.tryApply(s1, s2).get().toString());
        assertTrue(GT_STRING.tryApply(s1, s2).get().toBoolean());

        BaseValue seq1 = ofSequence(List.of(ofInt(1)));
    }

}

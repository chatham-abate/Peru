package org.perudevteam.type.base;

import static org.junit.jupiter.api.Assertions.*;

import io.vavr.collection.List;
import io.vavr.collection.Seq;
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

        BaseValue i3 = ofByte((byte)10);
        assertEquals(BaseType.FLOAT, OVER_FLOAT.tryApply(i1, i3).get().getTag());

        BaseValue s1 = ofString("BA");
        BaseValue s2 = ofString("AB");

        assertEquals("BAAB", PLUS_STRING.tryApply(s1, s2).get().toString());
        assertTrue(GT_STRING.tryApply(s1, s2).get().toBoolean());

        BaseValue seq1 = ofSequence(List.of(ofInt(1)));
        BaseValue seq2 = seq1.mapSeq(s -> s.append(ofInt(2)));

        Seq<BaseValue> expected = List.of(ofInt(1), ofInt(1), ofInt(2));
        assertEquals(expected, PLUS_SEQUENCE.tryApply(seq1, seq2).get().toSequence());

        BaseValue f1 = ofFunction(args -> args.get(1));
        BaseValue f2 = ofFunction(args -> args.get(0).mapInt(i -> i * 2));

        BaseValue f3 = AND_THEN_FUNCTION.tryApply(f1, f2).get();
        assertEquals(10, f3.toFunction().apply(List.of(ofInt(1), ofInt(5))).toInt());
    }

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

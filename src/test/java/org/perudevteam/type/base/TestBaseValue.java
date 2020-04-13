package org.perudevteam.type.base;

import static org.perudevteam.type.base.BaseValue.*;
import static org.junit.jupiter.api.Assertions.*;

import io.vavr.Function1;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import org.junit.jupiter.api.Test;
import org.perudevteam.type.base.BaseValue;

public class TestBaseValue {

    /*
     * Basic Usage Tests.
     */
    @Test
    void testBasics() {
        BaseValue integer1 = ofInt(10);
        BaseValue integer2 = ofInt(20);

        assertEquals(BaseType.INT, integer1.getTag());

        assertEquals(30, integer1.toInt() + integer2.toInt());
        assertEquals(30.0, integer1.toDouble() + integer2.toDouble());

        BaseValue string = ofString("Hello");
        string = string.mapString(s -> s + " World");
        assertEquals("Hello World", string.toString());

        BaseValue func = ofFunction(args -> args.get(0).mapInt(i -> i + 1));
        assertEquals(2, func.toFunction().apply(List.of(ofInt(1))).toInt());

        func = func.mapFunction(f -> (args -> f.apply(args).mapInt(i -> i * 2)));
        assertEquals(4, func.toFunction().apply(List.of(ofInt(1))).toInt());

        BaseValue seq = ofSequence(List.of(integer1, integer2));
        seq = seq.mapSeq(s -> s.map(e -> e.mapInt(i -> i * 2)));
        assertEquals(List.of(20, 40), seq.toSequence().map(BaseValue::toInt));
    }

    /*
     * Basic Errors.
     */
    @Test
    void testErrors() {
        assertThrows(ClassCastException.class, () -> ofInt(1).toByte());
        assertThrows(ClassCastException.class, () -> ofBoolean(true).toSequence());
        assertThrows(ClassCastException.class, () -> ofInt(2).mapSeq(s -> s));

        assertThrows(NullPointerException.class, () -> ofInt(2).mapInt(i -> null));

        assertNotEquals(ofInt(1), ofLong(1));
    }
}
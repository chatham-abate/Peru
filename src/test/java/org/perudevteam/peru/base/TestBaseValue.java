package org.perudevteam.peru.base;

import static org.perudevteam.peru.base.BaseValue.*;
import static org.junit.jupiter.api.Assertions.*;

import io.vavr.collection.List;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.perudevteam.peru.base.BaseType;
import org.perudevteam.peru.base.BaseValue;

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

        BaseValue func = ofFunction(args -> Try.success(args.get(0).mapInt(i -> i + 1)));
        assertEquals(2, func.toFunction().apply(List.of(ofInt(1))).get().toInt());

        func = func.mapFunction(f -> (args -> Try.success(f.apply(args).get().mapInt(i -> i * 2))));
        assertEquals(4, func.toFunction().apply(List.of(ofInt(1))).get().toInt());

        BaseValue seq = ofSequence(List.of(integer1, integer2));
        seq = seq.mapSequence(s -> s.map(e -> e.mapInt(i -> i * 2)));
        assertEquals(List.of(20, 40), seq.toSequence().map(BaseValue::toInt));
    }

    /*
     * Basic Errors.
     */
    @Test
    void testErrors() {
        assertThrows(ClassCastException.class, () -> ofInt(1).toByte());
        assertThrows(ClassCastException.class, () -> ofBoolean(true).toSequence());
        assertThrows(ClassCastException.class, () -> ofInt(2).mapSequence(s -> s));

        assertThrows(NullPointerException.class, () -> ofInt(2).mapInt(i -> null));

        assertNotEquals(ofInt(1), ofLong(1));
    }
}

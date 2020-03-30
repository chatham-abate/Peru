package org.perudevteam.dynamic;

import io.vavr.control.Try;
import org.junit.jupiter.api.*;
import io.vavr.collection.Set;
import io.vavr.collection.HashSet;
import io.vavr.collection.Seq;
import io.vavr.collection.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.perudevteam.dynamic.Dynamic.*;

class TestDynamic {
    @Test
    void testNull() {
        Try integerTry = Try.of(() -> ofNull().asInt());
        Try doubleTry = Try.of(() -> ofNull().asDouble());

        assertTrue(integerTry.isFailure());
        assertTrue(doubleTry.isFailure());

        assertTrue(ofNull().isNull());
    }

    @Test
    void testValues() {
        Try<Dynamic> integerTry = Try.of(() -> ofInt(10));
        Try<Dynamic> booleanTry = Try.of(Dynamic::ofTrue);

        Object ref = new Object();
        Try<Dynamic> refTry = Try.of(() -> Dynamic.ofReference(ref));

        assertTrue(integerTry.isSuccess());
        assertTrue(booleanTry.isSuccess());
        assertTrue(refTry.isSuccess());

        assertEquals(10, integerTry.get().asInt());
        assertTrue(booleanTry.get().asBoolean());
        assertEquals(ref, refTry.get().asReference());
    }

    @Test
    void testReferences() {
        Set<Integer> set = HashSet.of(1, 2 ,3);
        Set<Integer> otherSet = HashSet.of(1, 2, 3);

        Dynamic ref = ofReference(set);
        Dynamic otherRef = ofReference(otherSet);

        assertEquals(ref, otherRef);
    }

    @Test
    void testMaps() {
        Dynamic intVal = ofInt(10);
        intVal = intVal.mapInt(i -> i * 10);
        assertEquals(100, intVal.asInt());

        Dynamic seqVal = ofSequence(List.of(ofInt(1), ofInt(2), ofInt(3)));
        seqVal = seqVal.mapSequence(s -> s.map(d -> d.mapInt(i -> i + 1)));

        assertEquals(ofSequence(List.of(ofInt(2), ofInt(3), ofInt(4))), seqVal);
    }
}

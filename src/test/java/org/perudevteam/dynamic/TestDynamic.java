package org.perudevteam.dynamic;

import com.apple.laf.AquaButtonBorder;
import io.vavr.control.Try;
import org.junit.jupiter.api.*;
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
}

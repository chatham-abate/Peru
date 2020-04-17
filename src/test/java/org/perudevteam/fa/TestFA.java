package org.perudevteam.fa;

import io.vavr.Function0;
import io.vavr.collection.*;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.perudevteam.fa.DFA.*;

public class TestFA {

    enum InputClass {
        A, B, C, D, E, F
    }

    enum OutputClass {
        Thing1, Thing2
    }

    private static final Set<InputClass> INPUT_ALPHA = HashSet.of(InputClass.A, InputClass.B, InputClass.C);

    private static final Seq<Function0<DFA<Character, InputClass, OutputClass>>> NULL_ERRORS = Array.of(
            () -> dfa(null, INPUT_ALPHA, Array.of(HashMap.empty()), a -> InputClass.A),
            () -> dfa(HashMap.empty(), null, Array.of(HashMap.empty()), a -> InputClass.A),
            () -> dfa(HashMap.empty(), INPUT_ALPHA, null, a -> InputClass.A),
            () -> dfa(HashMap.empty(), INPUT_ALPHA, Array.of(HashMap.empty()), null),
            () -> dfa(HashMap.of(null, null), INPUT_ALPHA, Array.empty(), a -> InputClass.A),
            () -> dfa(HashMap.empty(), HashSet.of(null), Array.empty(), a -> InputClass.A)
    );

    @TestFactory
    Seq<DynamicTest> testDFANullConstructionErrors() {
        Seq<DynamicTest> tests = List.empty();
        for (int i = 0; i < NULL_ERRORS.length(); i++) {
            final int testNo = i;
            tests = tests.append(DynamicTest.dynamicTest(testNo + "", () -> {
                assertThrows(NullPointerException.class, () -> NULL_ERRORS.get(testNo).apply());
            }));
        }

        return tests;
    }



}

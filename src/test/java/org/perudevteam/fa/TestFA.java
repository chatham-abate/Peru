package org.perudevteam.fa;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.collection.*;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.function.Executable;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.perudevteam.fa.DFA.*;

import static org.perudevteam.TestingUtil.*;

/**
 * This class if for testing all Finite Automata Classes.
 * FA, DFA, and NFA.
 */
public class TestFA {

    enum InputClass {
        A, B, C, OTHER
    }

    enum OutputClass {
        Thing1
    }

    private static final Set<InputClass> INPUT_ALPHA = HashSet.of(InputClass.A, InputClass.B, InputClass.C);

    private static final Seq<Tuple2<String, Executable>> NULL_ERRORS = List.of(
            Tuple.of("Null Accepting",
                    () -> dfa(null, INPUT_ALPHA, Array.of(HashMap.empty()), a -> InputClass.A)),
            Tuple.of("Null Alphabet",
                    () -> dfa(HashMap.empty(), null, Array.of(HashMap.empty()), a -> InputClass.A)),
            Tuple.of("Null Transition Table",
                    () -> dfa(HashMap.empty(), INPUT_ALPHA, null, a -> InputClass.A)),
            Tuple.of("Null Input Function",
                    () -> dfa(HashMap.empty(), INPUT_ALPHA, Array.of(HashMap.empty()), null)),
            Tuple.of("Null Accepting State",
                    () -> dfa(HashMap.of(null, OutputClass.Thing1), INPUT_ALPHA, Array.empty(), a -> InputClass.A)),
            Tuple.of("Null Output",
                    () -> dfa(HashMap.of(5, null), INPUT_ALPHA, Array.empty(), a -> InputClass.A))
    );

    @TestFactory
    Seq<DynamicTest> testDFANullConstructionErrors() {
        return buildThrowTests(NullPointerException.class, NULL_ERRORS);
    }

    private static final Seq<Tuple2<String, Executable>> ILLEGAL_ARG_ERRORS = List.of(
            Tuple.of("Empty Input Alphabet",
                    () -> dfa(HashMap.empty(), HashSet.empty(), Array.of(HashMap.empty()), a -> InputClass.A)),
            Tuple.of("Empty Transition Table",
                    () -> dfa(HashMap.empty(), INPUT_ALPHA, Array.empty(), a -> InputClass.A)),
            Tuple.of("Bad Input Class",
                    () -> dfa(
                            HashMap.empty(),
                            INPUT_ALPHA,
                            Array.of(HashMap.of(InputClass.OTHER, 0)),
                            a -> InputClass.A
                    ))
    );

    @TestFactory
    Seq<DynamicTest> testDFAIllegalArgErrors() {
        return buildThrowTests(IllegalArgumentException.class, ILLEGAL_ARG_ERRORS);
    }

    private static final Seq<Tuple2<String, Executable>> OUT_OF_B_ERRORS = List.of(
            Tuple.of("Bad Accepting State",
                    () -> dfa(
                            HashMap.of(2, OutputClass.Thing1),
                            INPUT_ALPHA,
                            Array.of(HashMap.empty()),
                            a -> InputClass.A
                    )),
            Tuple.of("Bad Transition State",
                    () -> dfa(
                            HashMap.empty(),
                            INPUT_ALPHA,
                            Array.of(HashMap.of(InputClass.A, 3)),
                            a -> InputClass.A
                    ))
    );

    @TestFactory
    Seq<DynamicTest> testDFAOutOfBoundsErrors() {
        return buildThrowTests(IndexOutOfBoundsException.class, OUT_OF_B_ERRORS);
    }

    private static final DFA<Character, InputClass, OutputClass> TEST_DFA =
            DFA.<Character,InputClass,OutputClass>dfa(3, INPUT_ALPHA, (input) -> {
                if (input == 'A') return InputClass.A;
                if (input == 'B') return InputClass.B;
                if (input == 'C') return InputClass.C;
                return InputClass.OTHER; })
                    .withSingleTransition(0,1, InputClass.A)
                    .withSingleTransition(1, 1, InputClass.A)
                    .withSingleTransition(1, 2, InputClass.C)
                    .withSingleTransition(0, 2, InputClass.B)
                    .withAcceptingState(2, OutputClass.Thing1);

    private static final Seq<Tuple3<Integer, InputClass, Integer>> DFA_TEST_TRANSITIONS = List.of(
            Tuple.of(0, InputClass.A, 1),
            Tuple.of(1, InputClass.A, 1),
            Tuple.of(1, InputClass.C, 2),
            Tuple.of(0, InputClass.B, 2)
    );

    @TestFactory
    Seq<DynamicTest> testDFACorrectTransitions() {
        return DFA_TEST_TRANSITIONS.map(tuple -> DynamicTest.dynamicTest(tuple.toString(),
                () -> assertEquals(tuple._3, TEST_DFA.getTransitionFromClass(tuple._1, tuple._2))));
    }

    private static final Seq<Tuple2<Integer, InputClass>> DFA_ERROR_TRANSITIONS = List.of(
            Tuple.of(4, InputClass.A),
            Tuple.of(1, InputClass.OTHER),
            Tuple.of(-1, InputClass.B)
    );

    @TestFactory
    Seq<DynamicTest> testDFAErrorTransitions() {
        return DFA_ERROR_TRANSITIONS.map(tuple -> DynamicTest.dynamicTest(
                tuple.toString(),
                () -> assertThrows(Exception.class, () -> TEST_DFA.getTransitionFromClass(tuple._1, tuple._2))
        ));
    }

    @Test
    void testDFAAccepting() {
        assertTrue(TEST_DFA.isAccepting(2));
        assertEquals(OutputClass.Thing1, TEST_DFA.getOutput(2));

        assertThrows(NoSuchElementException.class, () -> TEST_DFA.getOutput(1));
    }
}

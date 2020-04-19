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
import static org.perudevteam.fa.DFAutomaton.*;
import static org.perudevteam.fa.NFAutomaton.*;

import static org.perudevteam.TestingUtil.*;

/**
 * This class if for testing all Finite Automata Classes.
 * FA, DFA, and NFA.
 */
public class TestFAutomaton {

    enum InputClass {
        A, B, C, OTHER
    }

    enum OutputClass {
        Thing1
    }

    private static final Set<InputClass> INPUT_ALPHA = HashSet.of(InputClass.A, InputClass.B, InputClass.C);

    private static final Seq<Tuple2<String, Executable>> DFA_NULL_ERRORS = List.of(
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
        return buildThrowTests(NullPointerException.class, DFA_NULL_ERRORS);
    }

    private static final Seq<Tuple2<String, Executable>> DFA_ILLEGAL_ARG_ERRORS = List.of(
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
        return buildThrowTests(IllegalArgumentException.class, DFA_ILLEGAL_ARG_ERRORS);
    }

    private static final Seq<Tuple2<String, Executable>> DFA_OUT_OF_B_ERRORS = List.of(
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
        return buildThrowTests(IndexOutOfBoundsException.class, DFA_OUT_OF_B_ERRORS);
    }

    private static final DFAutomaton<Character, InputClass, OutputClass> TEST_DFA =
            DFAutomaton.<Character,InputClass,OutputClass>dfa(3, INPUT_ALPHA, (input) -> {
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

    private static final Seq<Tuple2<String, Executable>> NFA_NULL_ERRORS = List.of(
            Tuple.of("Null Transition Table",
                    () -> nfa(HashMap.empty(), INPUT_ALPHA, null, Array.empty(), a -> InputClass.A)),
            Tuple.of("Null Transition Table Row",
                    () -> nfa(
                            HashMap.empty(),
                            INPUT_ALPHA,
                            Array.of(HashMap.empty(), null),
                            Array.empty(), a -> InputClass.A
                    )),
            Tuple.of("Null Transition Input Class",
                    () -> nfa(
                            HashMap.empty(),
                            INPUT_ALPHA,
                            Array.of(HashMap.of(null, HashSet.empty())),
                            Array.empty(), a -> InputClass.A
                    )),
            Tuple.of("Null Transition",
                    () -> nfa(
                            HashMap.empty(),
                            INPUT_ALPHA,
                            Array.of(HashMap.of(0, null)),
                            Array.empty(), a -> InputClass.A
                    )),
            Tuple.of("Null Epsilon Table",
                    () -> nfa(
                            HashMap.empty(),
                            INPUT_ALPHA,
                            Array.of(HashMap.empty()),
                            null, a -> InputClass.A
                    )),
            Tuple.of("Null Epsilon Table Row",
                    () -> nfa(
                            HashMap.empty(),
                            INPUT_ALPHA,
                            Array.of(HashMap.empty(), HashMap.empty()),
                            Array.of(null, HashSet.empty()), a -> InputClass.A
                    )),
            Tuple.of("Null Epsilon Transition",
                    () -> nfa(
                            HashMap.empty(),
                            INPUT_ALPHA,
                            Array.of(HashMap.empty()),
                            Array.of(HashSet.of(1, null)), a -> InputClass.A
                    ))
    );

    @TestFactory
    Seq<DynamicTest> testNFANullErrors() {
        return buildThrowTests(NullPointerException.class, NFA_NULL_ERRORS);
    }

    private static final Seq<Tuple2<String, Executable>> NFA_ILLEGAL_ARG_ERRORS = List.of(
            Tuple.of("Empty Transition Table",
                    () -> nfa(HashMap.empty(),
                            INPUT_ALPHA,
                            Array.empty(),
                            Array.of(HashSet.empty()), a -> InputClass.A
                    )),
            Tuple.of("Empty Transition Set",
                    () -> nfa(HashMap.empty(),
                            INPUT_ALPHA,
                            Array.of(HashMap.of(InputClass.A, HashSet.empty())),
                            Array.of(HashSet.empty()), a -> InputClass.A
                    )),
            Tuple.of("Illegal Input Class",
                    () -> nfa(
                            HashMap.empty(),
                            INPUT_ALPHA,
                            Array.of(HashMap.of(InputClass.OTHER, HashSet.of(0))),
                            Array.of(HashSet.empty()), a -> InputClass.A
                    )),
            Tuple.of("Inconsistent Table Sizes.",
                    () -> nfa(
                            HashMap.empty(),
                            INPUT_ALPHA,
                            Array.of(HashMap.of(InputClass.A, HashSet.of(0))),
                            Array.of(), a -> InputClass.A
                    ))
    );

    @TestFactory
    Seq<DynamicTest> testNFAIllegalArgumentErrors() {
        return buildThrowTests(IllegalArgumentException.class, NFA_ILLEGAL_ARG_ERRORS);
    }

    private static final Seq<Tuple2<String, Executable>> NFA_OUT_OF_B_ERRORS = List.of(
            Tuple.of("Bad Transition",
                    () -> nfa(HashMap.empty(),
                            INPUT_ALPHA,
                            Array.of(HashMap.of(InputClass.A, HashSet.of(12))),
                            Array.of(HashSet.empty()), a -> InputClass.A
                    )),
            Tuple.of("Bad Epsilon",
                    () -> nfa(HashMap.empty(),
                            INPUT_ALPHA,
                            Array.of(HashMap.of(InputClass.A, HashSet.of(0))),
                            Array.of(HashSet.of(12)), a -> InputClass.A
                    ))
    );

    @TestFactory
    Seq<DynamicTest> testNFAOutOfBoundsErrors() {
        return buildThrowTests(IndexOutOfBoundsException.class, NFA_OUT_OF_B_ERRORS);
    }

    private static final NFAutomaton<Character, InputClass, OutputClass> TEST_NFA =
            NFAutomaton.<Character, InputClass, OutputClass>nfa(4, INPUT_ALPHA, (input) -> {
                if (input == 'A') return InputClass.A;
                if (input == 'B') return InputClass.B;
                return InputClass.B;
            })
                    .withEpsilonTransition(0, 1)
                    .withSingleTransition(0, 2, InputClass.B)
                    .withSingleTransition(1, 2, InputClass.A)
                    .withSingleTransition(1, 3, InputClass.A)
                    .withAcceptingState(3, OutputClass.Thing1);

    public static final Seq<Tuple3<Integer, InputClass, Set<Integer>>> NFA_TRANSITIONS = List.of(
            Tuple.of(0, InputClass.B, HashSet.of(2)),
            Tuple.of(1, InputClass.A, HashSet.of(2, 3))
    );

    @TestFactory
    Seq<DynamicTest> testNFATransitions() {
        return NFA_TRANSITIONS.map(tuple -> DynamicTest.dynamicTest(tuple.toString(),
                () -> assertEquals(tuple._3, TEST_NFA.getTransitionsFromClass(tuple._1, tuple._2))));
    }

    public static final Seq<Tuple2<Integer, InputClass>> NFA_ERROR_TRANSITIONS = List.of(
            Tuple.of(0, InputClass.A),
            Tuple.of(1, InputClass.B),
            Tuple.of(-1, InputClass.C),
            Tuple.of(10, InputClass.B),
            Tuple.of(1, InputClass.OTHER)
    );

    @TestFactory
    Seq<DynamicTest> testNFATransitionErrors() {
        return NFA_ERROR_TRANSITIONS.map(tuple -> DynamicTest.dynamicTest(tuple.toString(),
                () -> assertThrows(Exception.class, () -> TEST_NFA.getTransitionsFromClass(tuple._1, tuple._2))));
    }
}

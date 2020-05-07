package com.github.chathamabate.fa;

import com.github.chathamabate.lexer.charlexer.CharSimpleContext;
import com.github.chathamabate.lexer.charlexer.CharSimpleDLexer;
import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import com.github.chathamabate.charpos.EnumCharPos;

import static org.junit.jupiter.api.Assertions.*;
import static com.github.chathamabate.charpos.EnumCharPos.*;

public class TestFAutomatonUtil {

    private static final Array<Set<Integer>> TEST_GRAPH = Array.of(
            HashSet.of(1),
            HashSet.of(2),
            HashSet.of(1),
            HashSet.empty(),
            HashSet.of(2, 3)
    );

    private static final Array<Set<Integer>> EXPECTED_REACHABLES = Array.of(
            HashSet.of(0, 1, 2),
            HashSet.of(1, 2),
            HashSet.of(1, 2),
            HashSet.of(3),
            HashSet.of(1, 2, 3, 4)
    );

    @Test
    void testReachableSets() {
        Assertions.assertEquals(EXPECTED_REACHABLES, FAutomatonUtil.reachableSets(TEST_GRAPH));
    }

    /*
     * NFA to DFA tests.
     *
     * Must test by lexing some input.
     */

    enum InputClass {
        A,
        B,
        C
    }

    private static InputClass getInputClass(Character c) {
        if (c == 'a') return InputClass.A;
        if (c == 'b') return InputClass.B;
        if (c == 'c') return InputClass.C;

        throw new IllegalArgumentException("Bad Input Value Given.");
    }

    enum OutputClass {
        THING1,
        THING2
    }

    private static final Function1<CharSimpleContext, EnumCharPos<OutputClass>>
        OUTPUT_1 = c -> charPosEnum(c, OutputClass.THING1),
        OUTPUT_2 = c -> charPosEnum(c, OutputClass.THING2);

    private static final NFAutomaton<Character, InputClass, OutputClass> AMBIGUOUS_NFA =
            new NFAutomaton<Character, InputClass, OutputClass>(
            5, HashSet.of(InputClass.values()), TestFAutomatonUtil::getInputClass
    )
                    .withSingleTransition(0,1, InputClass.A)
                    .withEpsilonTransition(1, 2)
                    .withAcceptingState(2, OutputClass.THING1)
                    .withEpsilonTransition(0, 3)
                    .withSingleTransition(3, 4, InputClass.A)
                    .withAcceptingState(4, OutputClass.THING2);

    @Test
    void testAmbiguousNFA() {
        assertThrows(Exception.class, AMBIGUOUS_NFA::toDFA);
    }

    private static final NFAutomaton<Character, InputClass, Function1<CharSimpleContext, EnumCharPos<OutputClass>>> NFA1 =
            new NFAutomaton<Character, InputClass, Function1<CharSimpleContext, EnumCharPos<OutputClass>>>(
                    9, HashSet.of(InputClass.values()), TestFAutomatonUtil::getInputClass
    )
                    .withEpsilonTransition(0, 1)
                    .withSingleTransition(1, 2, InputClass.A)
                    .withEpsilonTransition(2, 3)
                    .withSingleTransition(3, 4, InputClass.B)
                    .withAcceptingState(4, OUTPUT_1)

                    .withSingleTransition(0, 5, InputClass.A)
                    .withEpsilonTransition(5, 6)
                    .withEpsilonTransition(5, 8)
                    .withSingleTransition(6, 7, InputClass.C)
                    .withEpsilonTransition(7, 8)
                    .withEpsilonTransition(8, 6)
                    .withAcceptingState(8, OUTPUT_2);

    private static final DFAutomaton<Character, InputClass, Function1<CharSimpleContext, EnumCharPos<OutputClass>>>
            DFA1 = NFA1.tryToDFA().get();

    private static final CharSimpleDLexer<OutputClass> LEXER1 = new CharSimpleDLexer<>(DFA1);

    private static final Seq<Tuple2<String, OutputClass>> EXPECTED1 = List.of(
            Tuple.of("ab", OutputClass.THING1),
            Tuple.of("a", OutputClass.THING2),
            Tuple.of("ac", OutputClass.THING2),
            Tuple.of("acc", OutputClass.THING2),
            Tuple.of("accccc", OutputClass.THING2)
    );

    @TestFactory
    Seq<DynamicTest> testNFAtoDFALexer() {
        return EXPECTED1.map(tuple -> DynamicTest.dynamicTest("String : " + tuple._1, () -> {
            Seq<Character> inputSequence = List.ofAll(tuple._1.toCharArray());

            Seq<Tuple2<String, EnumCharPos<OutputClass>>> outputs =
                    LEXER1.buildOnlySuccessfulTokenStream(inputSequence, CharSimpleContext.INIT_SIMPLE_CONTEXT);

            assertEquals(1, outputs.length());
            Tuple2<String, EnumCharPos<OutputClass>> result = outputs.head();

            assertEquals(tuple._1, result._1);
            assertEquals(tuple._2, result._2.getTokenType());
        }));
    }

    private static final Seq<String> FAILURES1 = List.of(
            "ba",
            "caa",
            "ccc",
            "bbbb"
    );

    @TestFactory
    Seq<DynamicTest> testExpectedErrors() {
        return FAILURES1.map(failure -> DynamicTest.dynamicTest("Failure : " + failure, () ->
            assertTrue(LEXER1.build(List.ofAll(failure.toCharArray()), CharSimpleContext.INIT_SIMPLE_CONTEXT)
                    ._1._2.isFailure())));
    }

    /*
     * Precedence Tests.
     */

    private static final NFAutomaton<Character, InputClass, OutputClass>
            NFA2 = new NFAutomaton<Character, InputClass, OutputClass>(
            3, HashSet.of(InputClass.values()), TestFAutomatonUtil::getInputClass
    )
            .withSingleTransition(0, 1, InputClass.A)
            .withAcceptingState(1, OutputClass.THING1)

            .withSingleTransition(0, 2, InputClass.A)
            .withAcceptingState(2, OutputClass.THING1);

    @Test
    void checkSimplePrecedence() {


        assertTrue(NFA2.tryToDFA().isSuccess());

        // Precedence conflict.
        assertThrows(Exception.class,
                () -> NFA2.withAcceptingState(2, OutputClass.THING2).toDFA());

        assertTrue(NFA2.withAcceptingState(2, OutputClass.THING2).tryToDFA(
                List.of(HashSet.of(OutputClass.THING1), HashSet.of(OutputClass.THING2))
        ).isSuccess());
    }
}

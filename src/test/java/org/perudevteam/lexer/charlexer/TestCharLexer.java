package org.perudevteam.lexer.charlexer;

import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.perudevteam.misc.Builder;
import org.perudevteam.statemachine.DFStateMachine;
import org.perudevteam.statemachine.DStateMachine;

import org.perudevteam.misc.SeqHelpers;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class TestCharLexer {

    /*
     * Types and Lexers for testing.
     */

    /*
     * Language 1.
     */

    private enum CharType1 {
        SPACE,
        NUMBER,
        DOT,
        OTHER
    }

    private enum TokenType1 {
        WHITESPACE,
        INT,
        DOUBLE
    }

    /*
     * DSFM's for language 1.
     */

    private static final DStateMachine<CharType1, Function1<CharSimpleContext, CharData<TokenType1>>>
            DFSM_SIMPLE1 = DFStateMachine.<CharType1, Function1<CharSimpleContext, CharData<TokenType1>>>emptyDFSM(5)
            .withEdge(0, 1, CharType1.SPACE)
            .withEdge(1, 1, CharType1.SPACE)
            .withEdge(0, 2, CharType1.NUMBER)
            .withEdge(2,2, CharType1.NUMBER)
            .withEdge(2, 3, CharType1.DOT)
            .withEdge(3, 4, CharType1.NUMBER)
            .withEdge(4, 4, CharType1.NUMBER)
            .withAcceptingState(1, c -> new CharData<>(TokenType1.WHITESPACE, c.getStartingLine()))
            .withAcceptingState(2, c -> new CharData<>(TokenType1.INT, c.getStartingLine()))
            .withAcceptingState(4, c -> new CharData<>(TokenType1.DOUBLE, c.getStartingLine()));

    /*
     * Lexers for Language 1.
     */

    private static final CharSimpleDLexer<CharType1> LEXER_SIMPLE1 = new CharSimpleDLexer<CharType1>(DFSM_SIMPLE1) {
        @Override
        protected CharType1 inputClass(Character input) {
            if (Character.isWhitespace(input)) {
                return CharType1.SPACE;
            } else if ('0' <= input && input <= '9') {
                return CharType1.NUMBER;
            } else if (input == '.') {
                return CharType1.DOT;
            }

            return CharType1.OTHER;
        }
    };

    private static final CharLinearDLexer<CharType1> LEXER_LINEAR1 = new CharLinearDLexer<CharType1>(DFSM_SIMPLE1) {
        @Override
        protected CharType1 inputClass(Character input) {
            if (Character.isWhitespace(input)) {
                return CharType1.SPACE;
            } else if ('0' <= input && input <= '9') {
                return CharType1.NUMBER;
            } else if (input == '.') {
                return CharType1.DOT;
            }

            return CharType1.OTHER;
        }
    };

    /*
     * Language 2.
     */

    private enum CharType2 {
        A,
        B,
        C,
        OTHER
    }

    private enum TokenType2 {
        LONG,
        SHORT
    }

    /*
     * Language 2 Simple DSFM.
     */

    private static final DFStateMachine<CharType2, Function1<CharSimpleContext, CharData<TokenType2>>>
            DSFM_SIMPLE2 = DFStateMachine.<CharType2, Function1<CharSimpleContext, CharData<TokenType2>>>emptyDFSM(6)
            .withEdge(0, 1, CharType2.A)
            .withEdge(1, 2, CharType2.B)
            .withEdge(2, 3, CharType2.A)
            .withEdge(3, 4, CharType2.B)
            .withEdge(4, 5, CharType2.C)
            .withEdge(2, 5, CharType2.C)
            .withEdge(4, 3, CharType2.A)
            .withAcceptingState(2, c -> new CharData<>(TokenType2.SHORT, c.getStartingLine()))
            .withAcceptingState(5, c -> new CharData<>(TokenType2.LONG, c.getStartingLine()));

    /*
     * Language 2 Lexers.
     */

    private static final Map<Character, CharType2> CHAR_MAP2 = HashMap.of(
            'a', CharType2.A,
            'b', CharType2.B,
            'c', CharType2.C
    );

    // Simple Lexer on DSFM2.
    private static final CharSimpleDLexer<CharType2> LEXER_SIMPLE2 = new CharSimpleDLexer<CharType2>(DSFM_SIMPLE2) {
        @Override
        protected CharType2 inputClass(Character input) {
            return CHAR_MAP2.getOrElse(input, CharType2.OTHER);
        }
    };

    // Linear Lexer on DSFM2
    private static final CharLinearDLexer<CharType2> LEXER_LINEAR2 = new CharLinearDLexer<CharType2>(DSFM_SIMPLE2) {
        @Override
        protected CharType2 inputClass(Character input) {
            return CHAR_MAP2.getOrElse(input, CharType2.OTHER);
        }
    };

    /*
     * Finally Tests!
     */

    private static final Seq<Character> INPUT1 = List.ofAll("123 \n 456 12.34\n".toCharArray());

    private static final Seq<Tuple2<String, CharData>> EXPECTED1 = List.of(
            Tuple.of("123", new CharData<>(TokenType1.INT, 1)),
            Tuple.of(" \n ", new CharData<>(TokenType1.WHITESPACE, 1)),
            Tuple.of("456", new CharData<>(TokenType1.INT, 2)),
            Tuple.of(" ", new CharData<>(TokenType1.WHITESPACE, 2)),
            Tuple.of("12.34", new CharData<>(TokenType1.DOUBLE, 2)),
            Tuple.of("\n", new CharData<>(TokenType1.WHITESPACE, 2))
    );

    @Test
    void testSimpleLexer() {
        assertEquals(EXPECTED1,
                LEXER_SIMPLE1.buildStreamUnchecked(INPUT1, CharSimpleContext.INIT_SIMPLE_CONTEXT));
    }


    private static final Seq<Tuple2<String, CharData>> EXPECTED2 = List.of(
            Tuple.of("ababc", new CharData<>(TokenType2.LONG, 1)),
            Tuple.of("ab", new CharData<>(TokenType2.SHORT, 1)),
            Tuple.of("ab", new CharData<>(TokenType2.SHORT, 1)),
            Tuple.of("ab", new CharData<>(TokenType2.SHORT, 1)),
            Tuple.of("ab", new CharData<>(TokenType2.SHORT, 1))
    );

    @Test
    void testLinearLexer() {
        Seq<Character> input = List.ofAll("ababcabababab".toCharArray());

        Stream<Tuple2<String, CharData>> stream =
                LEXER_LINEAR2.buildStreamUnchecked(input, CharLinearContext.INIT_LINEAR_CONTEXT);

        assertEquals(EXPECTED2, stream);
    }

    @Test
    void testLinearLexerSpeed() {
        Seq<Character> input = SeqHelpers.fileUnchecked("src/test/testcases/TestCaseLinearDLexer.txt");

        double linearStart = System.nanoTime();
        LEXER_LINEAR2.buildStreamUnchecked(input, CharLinearContext.INIT_LINEAR_CONTEXT).toArray().length();
        double linearEnd = System.nanoTime();

        double simpleStart = System.nanoTime();
        LEXER_SIMPLE2.buildStreamUnchecked(input, CharLinearContext.INIT_LINEAR_CONTEXT).toArray().length();
        double simpleEnd = System.nanoTime();

        assertTrue((linearStart - linearEnd) < (simpleStart - simpleEnd));
    }


    private static final Seq<Character> VERBOSE_INPUT =
            SeqHelpers.fileUnchecked("src/test/testcases/TestCaseVerboseDLexer.txt");

    @Test
    void testVerboseNumberOfTokens() {
        assertEquals(23,
                LEXER_SIMPLE1.buildStreamUnchecked(VERBOSE_INPUT, CharSimpleContext.INIT_SIMPLE_CONTEXT).length());

        assertEquals(23,
                LEXER_LINEAR1.buildStreamUnchecked(VERBOSE_INPUT, CharLinearContext.INIT_LINEAR_CONTEXT).length());
    }

    /*
     * Failure Tests.
     */

    private static Seq<Seq<Character>> FAILURES1 = List.of(
            "123 + ",
            "",
            " \n *",
            "123a456",
            ".123",
            "0..123",
            ".",
            "0."
    ).map(s -> List.ofAll(s.toCharArray()));

    @Test
    void testFailures() {
        expectAllFailures(LEXER_SIMPLE1, CharSimpleContext.INIT_SIMPLE_CONTEXT, FAILURES1);
        expectAllFailures(LEXER_LINEAR1, CharLinearContext.INIT_LINEAR_CONTEXT, FAILURES1);
    }

    static <I, C, O> void expectAllFailures(Builder<I, C, O> lexer, C context, Seq<? extends Seq<I>> inputs) {
        // First create the output for each input.
        // Then filter the output to just the failures of each stream.
        // Then get only those streams which are empty... had no failures.
        Seq<Stream<Try<O>>> outputsWithNoFailures =
                inputs.map(i -> lexer.buildStream(i, context).filter(Try::isFailure))
                .filter(Seq::isEmpty);

        assertTrue(outputsWithNoFailures.isEmpty());
    }
}

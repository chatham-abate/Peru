package org.perudevteam.lexer.charlexer;

import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.perudevteam.charpos.CharPos;
import org.perudevteam.charpos.CharPosEnum;
import org.perudevteam.fa.DFAutomaton;
import org.perudevteam.lexer.DLexer;

import org.perudevteam.misc.MiscHelpers;

import static org.junit.jupiter.api.Assertions.*;
import static org.perudevteam.charpos.CharPosEnum.*;

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
     * Simple DFA for language 1.
     */

    private static final DFAutomaton<Character, CharType1, Function1<CharSimpleContext, CharPosEnum<TokenType1>>>
            DFA_SIMPLE1 = new DFAutomaton<Character, CharType1, Function1<CharSimpleContext, CharPosEnum<TokenType1>>>
            (5, HashSet.of(CharType1.values()), (input) -> {
        if ('0' <= input && input <= '9') {
            return CharType1.NUMBER;
        }

        if (input == '.') {
            return CharType1.DOT;
        }

        if (Character.isWhitespace(input)) {
            return CharType1.SPACE;
        }

        return CharType1.OTHER;
    })
            .withSingleTransition(0, 1, CharType1.SPACE)
            .withSingleTransition(1, 1, CharType1.SPACE)
            .withSingleTransition(0, 2, CharType1.NUMBER)
            .withSingleTransition(2,2, CharType1.NUMBER)
            .withSingleTransition(2, 3, CharType1.DOT)
            .withSingleTransition(3, 4, CharType1.NUMBER)
            .withSingleTransition(4, 4, CharType1.NUMBER)
            .withAcceptingState(1, c -> charPosEnum(c, TokenType1.WHITESPACE))
            .withAcceptingState(2, c -> charPosEnum(c, TokenType1.INT))
            .withAcceptingState(4, c -> charPosEnum(c, TokenType1.DOUBLE));


    /*
     * Lexers for Language 1.
     */

    private static final CharSimpleDLexer<TokenType1> LEXER_SIMPLE1 = new CharSimpleDLexer<>(DFA_SIMPLE1);
    private static final CharLinearDLexer<TokenType1> LEXER_LINEAR1 = new CharLinearDLexer<>(DFA_SIMPLE1);

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

    private static final Map<Character, CharType2> CHAR_MAP2 = HashMap.of(
            'a', CharType2.A,
            'b', CharType2.B,
            'c', CharType2.C
    );

    /*
     * Language 2 DFA.
     */

    private static final DFAutomaton<Character, CharType2, Function1<CharSimpleContext, CharPosEnum<TokenType2>>>
            DFA_SIMPLE2 = new DFAutomaton<Character, CharType2, Function1<CharSimpleContext, CharPosEnum<TokenType2>>>
            (6, HashSet.of(CharType2.values()), (input) -> CHAR_MAP2.get(input).get())
            .withSingleTransition(0, 1, CharType2.A)
            .withSingleTransition(1, 2, CharType2.B)
            .withSingleTransition(2, 3, CharType2.A)
            .withSingleTransition(3, 4, CharType2.B)
            .withSingleTransition(4, 5, CharType2.C)
            .withSingleTransition(2, 5, CharType2.C)
            .withSingleTransition(4, 3, CharType2.A)
            .withAcceptingState(2, c -> charPosEnum(c, TokenType2.SHORT))
            .withAcceptingState(5, c -> charPosEnum(c, TokenType2.LONG));

    /*
     * Language 2 Lexers.
     */


    // Simple Lexer on DFA2.
    private static final CharSimpleDLexer<TokenType2> LEXER_SIMPLE2 = new CharSimpleDLexer<>(DFA_SIMPLE2);

    // Linear Lexer on DFA2.
    private static final CharLinearDLexer<TokenType2> LEXER_LINEAR2 = new CharLinearDLexer<>(DFA_SIMPLE2);

    /*
     * Finally Tests!
     */

    private static final Seq<Character> INPUT1 = List.ofAll("123 \n 456 12.34\n".toCharArray());

    private static final Seq<Tuple2<String, CharPosEnum<TokenType1>>> EXPECTED1 = List.of(
            Tuple.of("123", charPosEnum(0, 0, TokenType1.INT)),
            Tuple.of(" \n ", charPosEnum(0, 3, TokenType1.WHITESPACE)),
            Tuple.of("456", charPosEnum(1, 1, TokenType1.INT)),
            Tuple.of(" ", charPosEnum(1, 4, TokenType1.WHITESPACE)),
            Tuple.of("12.34", charPosEnum(1, 5, TokenType1.DOUBLE)),
            Tuple.of("\n", charPosEnum(1, 10, TokenType1.WHITESPACE))
    );

    @Test
    void testSimpleLexer() {
        assertEquals(EXPECTED1,
                LEXER_SIMPLE1.buildSuccessfulTokenStream(INPUT1, CharSimpleContext.INIT_SIMPLE_CONTEXT));
    }

    private static final Seq<Character> INPUT2 = List.ofAll("ababcabababab".toCharArray());


    private static final Seq<Tuple2<String, CharPosEnum<TokenType2>>> EXPECTED2 = List.of(
            Tuple.of("ababc", charPosEnum(0, 0, TokenType2.LONG)),
            Tuple.of("ab", charPosEnum(0, 5, TokenType2.SHORT)),
            Tuple.of("ab", charPosEnum(0, 7, TokenType2.SHORT)),
            Tuple.of("ab", charPosEnum(0, 9, TokenType2.SHORT)),
            Tuple.of("ab", charPosEnum(0, 11, TokenType2.SHORT))
    );

    @Test
    void testLinearLexer() {
        Stream<Tuple2<String, CharPosEnum<TokenType2>>> stream =
                LEXER_LINEAR2.buildSuccessfulTokenStream(INPUT2, CharLinearContext.INIT_LINEAR_CONTEXT);

        assertEquals(EXPECTED2, stream);
    }

    @Test
    void testLinearLexerSpeed() {
        Seq<Character> input = MiscHelpers.fileUnchecked("src/test/testcases/TestCaseLinearDLexer.txt");

        double linearStart = System.nanoTime();
        LEXER_LINEAR2.buildSuccessfulTokenStream(input, CharLinearContext.INIT_LINEAR_CONTEXT).toArray().length();
        double linearEnd = System.nanoTime();

        double simpleStart = System.nanoTime();
        LEXER_SIMPLE2.buildSuccessfulTokenStream(input, CharLinearContext.INIT_LINEAR_CONTEXT).toArray().length();
        double simpleEnd = System.nanoTime();

        assertTrue((linearStart - linearEnd) < (simpleStart - simpleEnd));
    }


    private static final Seq<Character> VERBOSE_INPUT =
            MiscHelpers.fileUnchecked("src/test/testcases/TestCaseVerboseDLexer.txt");

    @Test
    void testVerboseNumberOfTokens() {
        assertEquals(23,
                LEXER_SIMPLE1.buildSuccessfulTokenStream(
                        VERBOSE_INPUT, CharSimpleContext.INIT_SIMPLE_CONTEXT).length());

        assertEquals(23,
                LEXER_LINEAR1.buildSuccessfulTokenStream(
                        VERBOSE_INPUT, CharLinearContext.INIT_LINEAR_CONTEXT).length());
    }

    /*
     * Failure Tests.
     */

    private static final Seq<Tuple2<Seq<Character>, Integer>> FAILURES1 = List.of(
            Tuple.of("123 + ", 1),
            Tuple.of("++", 2),
            Tuple.of(" \n *", 1),
            Tuple.of("123a456", 1),
            Tuple.of(".123", 1),
            Tuple.of("0..123", 2),
            Tuple.of(".", 1),
            Tuple.of("0.", 1)
    ).map(tuple -> tuple.map1(s -> List.ofAll(s.toCharArray())));

    @Test
    void testFailures() {
        expectFailures(LEXER_SIMPLE1, CharSimpleContext.INIT_SIMPLE_CONTEXT, FAILURES1);
        expectFailures(LEXER_LINEAR1, CharLinearContext.INIT_LINEAR_CONTEXT, FAILURES1);
    }

    static <I, L, D, C> void expectFailures(DLexer<I, L, D, C> lexer, C context,
         Seq<? extends Tuple2<? extends Seq<I>, ? extends Integer>> inputs) {

        inputs.forEach(tuple -> {
            Seq<Tuple2<L, Try<D>>> tokens = lexer.buildStream(tuple._1, context);

            assertEquals(tuple._2, tokens.filter(t -> t._2.isFailure()).length());
        });
    }

    private static final Seq<Character> ERROR_INPUT = List.ofAll("acabcacababc".toCharArray());

    private static final Seq<String> EXPECTED_LEXEMES = List.of(
            "ac",
            "abc",
            "ac",
            "ababc"
    );

    @Test
    void testLanguage2Recovery() {
        Seq<String> simpleLexemes = LEXER_SIMPLE2.buildStream(ERROR_INPUT, CharSimpleContext.INIT_SIMPLE_CONTEXT)
                .map(tuple -> tuple._1);

        Seq<String> linearLexemes = LEXER_LINEAR2.buildStream(ERROR_INPUT, CharLinearContext.INIT_LINEAR_CONTEXT)
                .map(tuple -> tuple._1);

        assertEquals(EXPECTED_LEXEMES, simpleLexemes);
        assertEquals(EXPECTED_LEXEMES, linearLexemes);
    }
}

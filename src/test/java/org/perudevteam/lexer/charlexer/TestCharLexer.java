package org.perudevteam.lexer.charlexer;

import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.collection.*;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.perudevteam.statemachine.DFStateMachine;
import org.perudevteam.statemachine.DStateMachine;

import org.perudevteam.misc.SeqHelpers;

import static org.junit.jupiter.api.Assertions.*;

public class TestCharLexer {
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

    private static final DStateMachine<CharType1, Function1<CharSimpleContext, CharData>>
            DFSM1 = DFStateMachine.<CharType1, Function1<CharSimpleContext, CharData>>emptyDFSM(5)
            .withEdge(0, 1, CharType1.SPACE)
            .withEdge(1, 1, CharType1.SPACE)
            .withEdge(0, 2, CharType1.NUMBER)
            .withEdge(2,2, CharType1.NUMBER)
            .withEdge(2, 3, CharType1.DOT)
            .withEdge(3, 4, CharType1.NUMBER)
            .withEdge(4, 4, CharType1.NUMBER)
            .withAcceptingState(1, c -> new CharData(TokenType1.WHITESPACE, c.getStartingLine()))
            .withAcceptingState(2, c -> new CharData(TokenType1.INT, c.getStartingLine()))
            .withAcceptingState(4, c -> new CharData(TokenType1.DOUBLE, c.getStartingLine()));

    private static final CharSimpleDLexer<CharType1> LEXER1 = new CharSimpleDLexer<CharType1>(DFSM1) {
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

    private static final Seq<Character> INPUT1 = List.ofAll("123 \n 456 12.34\n".toCharArray());

    private static final Seq<Tuple2<String, CharData>> EXPECTED1 = List.of(
            Tuple.of("123", new CharData(TokenType1.INT, 1)),
            Tuple.of(" \n ", new CharData(TokenType1.WHITESPACE, 1)),
            Tuple.of("456", new CharData(TokenType1.INT, 2)),
            Tuple.of(" ", new CharData(TokenType1.WHITESPACE, 2)),
            Tuple.of("12.34", new CharData(TokenType1.DOUBLE, 2)),
            Tuple.of("\n", new CharData(TokenType1.WHITESPACE, 2))
    );

    @Test
    void testSimpleLexer() {
        assertEquals(EXPECTED1,
                LEXER1.buildStreamUnchecked(INPUT1, CharSimpleContext.INIT_SIMPLE_CONTEXT));
    }

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

    private static final DFStateMachine<CharType2, Function1<CharSimpleContext, CharData>>
            DSFM2 = DFStateMachine.<CharType2, Function1<CharSimpleContext, CharData>>emptyDFSM(6)
            .withEdge(0, 1, CharType2.A)
            .withEdge(1, 2, CharType2.B)
            .withEdge(2, 3, CharType2.A)
            .withEdge(3, 4, CharType2.B)
            .withEdge(4, 5, CharType2.C)
            .withEdge(2, 5, CharType2.C)
            .withEdge(4, 3, CharType2.A)
            .withAcceptingState(2, c -> new CharData(TokenType2.SHORT, c.getStartingLine()))
            .withAcceptingState(5, c -> new CharData(TokenType2.LONG, c.getStartingLine()));

    private static final Map<Character, CharType2> CHAR_MAP2 = HashMap.of(
            'a', CharType2.A,
            'b', CharType2.B,
            'c', CharType2.C
    );

    private static final CharLinearDLexer<CharType2> LEXER2 = new CharLinearDLexer<CharType2>(DSFM2) {
        @Override
        protected CharType2 inputClass(Character input) {
            return CHAR_MAP2.getOrElse(input, CharType2.OTHER);
        }
    };

    private static final Seq<Tuple2<String, CharData>> EXPECTED2 = List.of(
            Tuple.of("ababc", new CharData(TokenType2.LONG, 1)),
            Tuple.of("ab", new CharData(TokenType2.SHORT, 1)),
            Tuple.of("ab", new CharData(TokenType2.SHORT, 1))
    );

    @Test
    void testLinearLexer() {
        Seq<Character> input = List.ofAll("ababcabab".toCharArray());

        Stream<Tuple2<String, CharData>> stream =
                LEXER2.buildStreamUnchecked(input, CharLinearContext.INIT_LINEAR_CONTEXT);

        assertEquals(EXPECTED2, stream);
    }

    private static final CharSimpleDLexer<CharType2> LEXER3 = new CharSimpleDLexer<CharType2>(DSFM2) {
        @Override
        protected CharType2 inputClass(Character input) {
            return CHAR_MAP2.getOrElse(input, CharType2.OTHER);
        }
    };

    @Test
    void testLinearLexerSpeed() {
        Seq<Character> input = SeqHelpers.fileUnchecked("src/test/testcases/TestCaseLinearDLexer.txt");

        double linearStart = System.nanoTime();
        LEXER2.buildStreamUnchecked(input, CharLinearContext.INIT_LINEAR_CONTEXT).toArray().length();
        double linearEnd = System.nanoTime();

        double simpleStart = System.nanoTime();
        LEXER3.buildStreamUnchecked(input, CharLinearContext.INIT_LINEAR_CONTEXT).toArray().length();
        double simpleEnd = System.nanoTime();

        assertTrue((linearStart - linearEnd) < (simpleStart - simpleEnd));
    }

}

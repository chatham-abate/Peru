package org.perudevteam.lexer.charlexer;

import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.perudevteam.statemachine.DFStateMachine;
import org.perudevteam.statemachine.DStateMachine;

import static org.junit.jupiter.api.Assertions.*;

public class TestCharLexer {
    enum CharType1 {
        SPACE,
        NUMBER,
        DOT,
        OTHER
    }

    enum TokenType1 {
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
                LEXER1.buildStream(INPUT1, CharSimpleContext.INITIAL_CONTEXT).map(Try::get));
    }
}

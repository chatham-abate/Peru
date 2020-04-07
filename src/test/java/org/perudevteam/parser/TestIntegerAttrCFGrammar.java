package org.perudevteam.parser;

import io.vavr.Function1;
import io.vavr.Function2;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import org.junit.jupiter.api.Test;
import org.perudevteam.lexer.charlexer.CharData;
import org.perudevteam.lexer.charlexer.CharSimpleContext;
import org.perudevteam.lexer.charlexer.CharSimpleDLexer;
import org.perudevteam.misc.Builder;
import org.perudevteam.misc.LineException;
import org.perudevteam.misc.SeqHelpers;
import org.perudevteam.parser.grammar.AttrCFGrammar;
import org.perudevteam.parser.grammar.AttrProduction;
import org.perudevteam.parser.lrone.LROneParser;
import org.perudevteam.statemachine.DFStateMachine;
import org.perudevteam.statemachine.DStateMachine;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static io.vavr.control.Either.*;

/**
 * Tests for parsing a simple language.
 * The language is for simply adding and subtracting natural numbers, returning an integer.
 */
public class TestIntegerAttrCFGrammar {

    enum CL {
        DIGIT,
        ADDOP,
        OTHER
    }

    enum T {
        NUMBER,
        ADDOP
    }

    // DSFM
    private static final DStateMachine<CL, Function1<CharSimpleContext, CharData<T>>>
            DFSM = DFStateMachine.<CL, Function1<CharSimpleContext, CharData<T>>>emptyDFSM(3)
            .withEdge(0, 1, CL.DIGIT)
            .withEdge(1, 1, CL.DIGIT)
            .withEdge(0, 2, CL.ADDOP)
            .withAcceptingState(1, c -> new CharData<>(T.NUMBER, c))
            .withAcceptingState(2, c -> new CharData<>(T.ADDOP, c));

    // Lexer
    private static final CharSimpleDLexer<CL, T> LEXER = new CharSimpleDLexer<CL, T>(DFSM) {
        @Override
        protected CL inputClass(Character input) {
            if ('0' <= input && input <= '9') {
                return CL.DIGIT;
            }

            if (input == '+' || input == '-') {
                return CL.ADDOP;
            }

            return CL.OTHER;
        }
    };

    // Manual Lexer Test.
    @Test
    void testLexer() {
        String input = "1+";

        LEXER.buildStreamUnchecked(List.ofAll(input.toCharArray()), CharSimpleContext.INIT_SIMPLE_CONTEXT)
                .forEach(System.out::println);
    }

    enum NT {
        GOAL,
        SUM
    }

    private static final AttrProduction<NT, T, Integer>
    GOAL_PROD = new AttrProduction<NT, T, Integer>(NT.GOAL, List.of(left(NT.SUM))) {
        @Override
        protected Integer buildResultUnsafe(Seq<? extends Integer> children) {
            return children.get(0);
        }
    },
    SUM_PROD1 = new AttrProduction<NT, T, Integer>(NT.SUM, List.of(left(NT.SUM), right(T.ADDOP), right(T.NUMBER))) {
        @Override
        protected Integer buildResultUnsafe(Seq<? extends Integer> children) {
            return children.get(1) == '+'
                    ? children.get(0) + children.get(2)
                    : children.get(0) - children.get(2);
        }
    },
    SUM_PROD2 = new AttrProduction<NT, T, Integer>(NT.SUM, List.of(right(T.NUMBER))) {
        @Override
        protected Integer buildResultUnsafe(Seq<? extends Integer> children) {
            return children.get(0);
        }
    };

    private static final Map<T, Function2<String, CharData<T>, Integer>> TERM_GENERATORS = HashMap.of(
            T.NUMBER, (l, d) -> Integer.parseInt(l),
            T.ADDOP, (l, d) -> 0
    );

    private static final AttrCFGrammar<NT, T, AttrProduction<NT, T, Integer>, String, CharData<T>, Integer>
            G = new AttrCFGrammar<>(NT.GOAL, TERM_GENERATORS, List.of(GOAL_PROD, SUM_PROD1, SUM_PROD2));

    private static final LROneParser<NT, T, String, CharData<T>, Integer>
            PARSER = new LROneParser<NT, T, String, CharData<T>, Integer>(G) {
        @Override
        protected Exception onError(Tuple2<String, CharData<T>> lookAhead) {
            return new LineException(lookAhead._2, "Unexpected Token : " + lookAhead._2.getType().name());
        }

        @Override
        protected Exception onError() {
            return new IllegalArgumentException("Unexpected EOF.");
        }
    };

    private static final Seq<String> INPUTS = List.of(
            "1+2",
            "1-2",
            "1-2+1",
            "2-1+3-3"
    );

    Seq<Integer> RESULTS = List.of(
            3,
            -1,
            0,
            1
    );

    @Test
    void testSuccessfulResults() {
        for (int i = 0; i < INPUTS.length(); i++) {
            Seq<Character> input = List.ofAll(INPUTS.get(i).toCharArray());
            Seq<Tuple2<String, CharData<T>>> tokens =
                    LEXER.buildStreamUnchecked(input, CharSimpleContext.INIT_SIMPLE_CONTEXT);

            try {
                assertEquals(RESULTS.get(i), PARSER.parse(tokens));
            } catch (Throwable e) {

            }
        }
    }

    private static final Seq<String> ERRORS = List.of(
            "+",
            "++",
            "2+",
            "+2",
            "2-1++4",
            "12+32---"
    );

    @Test
    void testErrors() {
        for (String errorStr: ERRORS) {
            Seq<Character> errorInput = List.ofAll(errorStr.toCharArray());
            Seq<Tuple2<String, CharData<T>>> errorTokens =
                    LEXER.buildStreamUnchecked(errorInput, CharSimpleContext.INIT_SIMPLE_CONTEXT);

            assertThrows(Exception.class, () -> {
                PARSER.parse(errorTokens);
            });
        }
    }
}





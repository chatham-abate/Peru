package com.github.chathamabate.peru.parser;

import com.github.chathamabate.peru.charpos.EnumCharPos;
import com.github.chathamabate.peru.lexer.charlexer.CharSimpleContext;
import com.github.chathamabate.peru.lexer.charlexer.CharSimpleDLexer;
import com.github.chathamabate.peru.misc.LineException;
import com.github.chathamabate.peru.parser.grammar.SemanticCFGrammar;
import com.github.chathamabate.peru.parser.grammar.SemanticProduction;
import com.github.chathamabate.peru.parser.lrone.LROneParser;
import io.vavr.CheckedFunction2;
import io.vavr.Function1;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import io.vavr.control.Try;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.github.chathamabate.peru.fa.DFAutomaton;

import static org.junit.jupiter.api.Assertions.*;
import static io.vavr.control.Either.*;

/**
 * Tests for parsing a simple language.
 * The language is for simply adding and subtracting natural numbers, returning an integer.
 */
public class TestIntegerSemanticCFGrammar {

    enum CL {
        DIGIT,
        ADDOP,
        OTHER
    }

    enum T {
        NUMBER,
        ADDOP
    }


    private static final DFAutomaton<Character, CL, Function1<CharSimpleContext, EnumCharPos<T>>>
            DFA_1 = new DFAutomaton<Character, CL, Function1<CharSimpleContext, EnumCharPos<T>>>(
                    3, HashSet.of(CL.values()), (input) -> {
        if ('0' <= input && input <= '9') {
            return CL.DIGIT;
        }

        if (input == '+' || input == '-') {
            return CL.ADDOP;
        }

        return CL.OTHER;
    })
            .withSingleTransition(0, 1, CL.DIGIT)
            .withSingleTransition(1, 1, CL.DIGIT)
            .withSingleTransition(0, 2, CL.ADDOP)
            .withAcceptingState(1, c -> EnumCharPos.charPosEnum(c, T.NUMBER))
            .withAcceptingState(2, c -> EnumCharPos.charPosEnum(c, T.ADDOP));

    // Lexer
    private static final CharSimpleDLexer<T> LEXER = new CharSimpleDLexer<>(DFA_1);


    // Manual Lexer Test.
    // @Test
    void testLexer() {
        String input = "1+.2123\n23";

        LEXER.buildStream(List.ofAll(input.toCharArray()), CharSimpleContext.INIT_SIMPLE_CONTEXT)
                .forEach(System.out::println);
    }

    enum NT {
        GOAL,
        SUM
    }

    private static final SemanticProduction<NT, T, Integer>
    GOAL_PROD = new SemanticProduction<NT, T, Integer>(NT.GOAL, List.of(left(NT.SUM))) {
        @Override
        protected Integer buildResultUnchecked(Seq<Integer> children) {
            return children.get(0);
        }
    },
    SUM_PROD1 = new SemanticProduction<NT, T, Integer>(NT.SUM, List.of(left(NT.SUM), right(T.ADDOP), right(T.NUMBER))) {
        @Override
        protected Integer buildResultUnchecked(Seq<Integer> children) {
            return children.get(0) + (children.get(1) * children.get(2));
        }
    },
    SUM_PROD2 = new SemanticProduction<NT, T, Integer>(NT.SUM, List.of(right(T.NUMBER))) {
        @Override
        protected Integer buildResultUnchecked(Seq<Integer> children) {
            return children.get(0);
        }
    };

    private static final Map<T, CheckedFunction2<String, EnumCharPos<T>, Integer>> TERM_GENERATORS = HashMap.of(
            T.NUMBER, (l, d) -> Integer.parseInt(l),
            T.ADDOP, (l, d) -> l.equals("+") ? 1 : -1
    );

    private static final SemanticCFGrammar<NT, T, SemanticProduction<NT, T, Integer>, String, EnumCharPos<T>, Integer>
            G = new SemanticCFGrammar<>(NT.GOAL, TERM_GENERATORS, List.of(GOAL_PROD, SUM_PROD1, SUM_PROD2));

    private static final LROneParser<NT, T, String, EnumCharPos<T>, Integer>
            PARSER = new LROneParser<NT, T, String, EnumCharPos<T>, Integer>(G) {
        @Override
        protected Exception onError(Tuple2<String, EnumCharPos<T>> lookAhead) {
            return LineException.lineEx(lookAhead._2, "Unexpected Token : " + lookAhead._2.getTokenType().name());
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
            Seq<Tuple2<String, EnumCharPos<T>>> tokens =
                    LEXER.buildSuccessfulTokenStream(input, CharSimpleContext.INIT_SIMPLE_CONTEXT);

            Try<Integer> result = PARSER.tryParse(tokens);
            assertTrue(result.isSuccess());
            assertEquals(RESULTS.get(i), result.get());
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
            Seq<Tuple2<String, EnumCharPos<T>>> errorTokens =
                    LEXER.buildSuccessfulTokenStream(errorInput, CharSimpleContext.INIT_SIMPLE_CONTEXT);

            Assertions.assertTrue(PARSER.tryParse(errorTokens).isFailure());
        }
    }
}





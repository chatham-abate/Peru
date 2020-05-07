package com.github.chathamabate.peru.regex;

import com.github.chathamabate.charpos.EnumCharPos;
import com.github.chathamabate.fa.DFAutomaton;
import com.github.chathamabate.lexer.DLexer;
import com.github.chathamabate.lexer.charlexer.CharSimpleContext;
import com.github.chathamabate.lexer.charlexer.CharSimpleDLexer;
import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.collection.*;
import io.vavr.control.Try;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import static org.junit.jupiter.api.Assertions.*;

public class TestPeruRegex {

    private static final Seq<String> BAD_REGEX = List.of(
            "]",
            "",
            "{",
            "\\",
            "\\1",
            "a{a}",
            "[]",
            "[^]",
            "[\\s]",
            "+",
            "*"
    );

    @TestFactory
    Seq<DynamicTest> testBadRegex() {
        return BAD_REGEX.map(bad -> DynamicTest.dynamicTest("Bad Regex " + bad,
                () -> Assertions.assertTrue(PeruRegex.tryBuildNFA(bad).isFailure())));
    }

    private enum Terminal1 {
        INTEGER,
        DOUBLE,
        ID,
        IF,
        THEN,
        A,
        B,
        C,
        OR,
        WHITESPACE
    }

    private static final Seq<Tuple3<String, Boolean, Function1<CharSimpleContext, EnumCharPos<Terminal1>>>>
            PATTERNS1 = Array.of(
            Tuple.of("\\d+", false, EnumCharPos.enumBuilder(Terminal1.INTEGER)),
            Tuple.of("[0-9]+\\.[0-9]+", false, EnumCharPos.enumBuilder(Terminal1.DOUBLE)),
            Tuple.of("[a-zA-Z_][a-zA-Z_0-9]*", false, EnumCharPos.enumBuilder(Terminal1.ID)),
            Tuple.of("if", true, EnumCharPos.enumBuilder(Terminal1.IF)),
            Tuple.of("then", true, EnumCharPos.enumBuilder(Terminal1.THEN)),
            Tuple.of("(\\.|\\*)a{3}", false, EnumCharPos.enumBuilder(Terminal1.A)),
            Tuple.of("(\\+$?)b{2,}", false, EnumCharPos.enumBuilder(Terminal1.B)),
            Tuple.of("@c{3,5}", false, EnumCharPos.enumBuilder(Terminal1.C)),
            Tuple.of("(or\\-){0,3}else", true, EnumCharPos.enumBuilder(Terminal1.OR)),
            Tuple.of("\\s+", false, EnumCharPos.enumBuilder(Terminal1.WHITESPACE))
    ).map(tuple -> tuple.map3(Function1::narrow));

    private static final DFAutomaton<Character, Character, Function1<CharSimpleContext, EnumCharPos<Terminal1>>>
            DFA1 = PeruRegex.tryBuildMultiResultDFA(PATTERNS1).get();

    private static final CharSimpleDLexer<Terminal1> LEXER1 = new CharSimpleDLexer<>(DFA1);

    private static final Seq<Tuple2<String, Terminal1>> LEXER1_SUCCESSES = List.of(
            Tuple.of("123", Terminal1.INTEGER),
            Tuple.of("1.1", Terminal1.DOUBLE),
            Tuple.of("asdfAS_123", Terminal1.ID),
            Tuple.of("if", Terminal1.IF),
            Tuple.of("then", Terminal1.THEN),
            Tuple.of("123.34953945", Terminal1.DOUBLE),
            Tuple.of(" ", Terminal1.WHITESPACE),
            Tuple.of("\n", Terminal1.WHITESPACE),
            Tuple.of("*aaa", Terminal1.A),
            Tuple.of(".aaa", Terminal1.A),
            Tuple.of("+$bb", Terminal1.B),
            Tuple.of("+$bbb", Terminal1.B),
            Tuple.of("+bbbbbbbb", Terminal1.B),
            Tuple.of("@ccc", Terminal1.C),
            Tuple.of("@cccc", Terminal1.C),
            Tuple.of("@ccccc", Terminal1.C),
            Tuple.of("else", Terminal1.OR),
            Tuple.of("or-else", Terminal1.OR),
            Tuple.of("or-or-else", Terminal1.OR)
    );

    @TestFactory
    Seq<DynamicTest> testLexer1Successes() {
        return charLexerSuccessTests(LEXER1, CharSimpleContext.INIT_SIMPLE_CONTEXT, LEXER1_SUCCESSES);
    }

    private static final Seq<String> LEXER1_FAILURES = List.of(
            "*",
            ".234",
            "*aa",
            "+b",
            "@cc"
    );

    @TestFactory
    Seq<DynamicTest> testLexer1Failures() {
        return charLexerFailureTests(LEXER1, CharSimpleContext.INIT_SIMPLE_CONTEXT, LEXER1_FAILURES);
    }

    static <T extends Enum<T>, C> Seq<DynamicTest> charLexerFailureTests(
            DLexer<Character, String, EnumCharPos<T>, C> lexer,
            C context, Seq<? extends String> cases) {
        return cases.map(failure -> DynamicTest.dynamicTest("Can't Lex " + failure, () -> {
            Tuple3<Tuple2<String, Try<EnumCharPos<T>>>, C, Seq<Character>> result =
                    lexer.build(List.ofAll(failure.toCharArray()), context);

            assertTrue(result._1._2.isFailure());
        }));
    }


    static <T extends Enum<T>, C> Seq<DynamicTest> charLexerSuccessTests(
            DLexer<Character, String, EnumCharPos<T>, C> lexer,
            C context, Seq<? extends Tuple2<? extends String, T>> cases) {
        return cases.map(tuple -> DynamicTest.dynamicTest("Can Lex " + tuple._1, () -> {
            Tuple3<Tuple2<String, Try<EnumCharPos<T>>>, C, Seq<Character>> result =
                    lexer.build(List.ofAll(tuple._1.toCharArray()), context);

            assertEquals(result._1._1, tuple._1);
            assertTrue(result._1._2.isSuccess());
            assertTrue(result._3.isEmpty());

            assertEquals(tuple._2, result._1._2.get().getTokenType());
        }));
    }
}

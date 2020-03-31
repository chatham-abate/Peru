package org.perudevteam.lexeralpha;

import io.vavr.*;
import io.vavr.collection.*;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.perudevteam.dynamic.Builder;
import org.perudevteam.dynamic.Dynamic;

import static org.junit.jupiter.api.Assertions.*;
import static org.perudevteam.dynamic.Dynamic.*;

public class TestLexer {
    enum CharClasses {
        DIGIT,          // 0
        DOT,            // 1
        WHTIESPACE      // 2
    }

    static final Function1<Character, Option<Integer>> CLASSES = c -> {
        if ('0' <= c && c <= '9') {
            return Option.of(CharClasses.DIGIT.ordinal());
        } else if (c == '.') {
            return Option.of(CharClasses.DOT.ordinal());
        } else if (Character.isWhitespace(c)) {
            return Option.of(CharClasses.WHTIESPACE.ordinal());
        }

        return Option.none();
    };

    enum TokenTypes {
        INT,
        DECIMAL,
        SPACE
    }

    private static final Array<Option<Function2<Dynamic, Dynamic, Dynamic>>> ACCEPTS = Array.of(
            Option.none(), // State 0
            Option.some((lexeme, context) -> ofMap(HashMap.of( // State 1. (WhiteSpace)
                    "Data", lexeme,
                    "Type", ofEnum(TokenTypes.SPACE),
                    "Position", context.asMap().get("InitialLine").get()
            ))),
            Option.some((lexeme, context) -> ofMap(HashMap.of( // State 2. (Int)
                    "Data", lexeme,
                    "Type", ofEnum(TokenTypes.INT),
                    "Position", context.asMap().get("InitialLine").get()
            ))),
            Option.none(), // State 3.
            Option.some((lexeme, context) -> ofMap(HashMap.of( // State 4 (Decimal)
                    "Data", lexeme,
                    "Type", ofEnum(TokenTypes.DECIMAL),
                    "Position", context.asMap().get("InitialLine").get()
            )))
    );

    private static final Array<Array<Option<Integer>>> TRANS = Array.of(
            Array.of(Option.of(2), Option.none(), Option.of(1)),
            Array.of(Option.none(), Option.none(), Option.of(1)),
            Array.of(Option.of(2), Option.of(3), Option.none()),
            Array.of(Option.of(4), Option.none(), Option.none()),
            Array.of(Option.of(4), Option.none(), Option.none())
    );

    private static final TableDFA<Character, Function2<Dynamic, Dynamic, Dynamic>> DFA = TableDFA.tableDFA(
       CLASSES, ACCEPTS, TRANS
    );

    private static final Builder<Character, Dynamic> LEXER = Lexer.classicTableLexer(DFA);

    private static final Dynamic START_CONTEXT = ofMap(HashMap.of(
            "InitialLine", ofInt(0),
            "LineSinceLastToken", ofInt(0),
            "CurrentLine", ofInt(0)
    ));

    @Test
    void testBasicLanguage() {
        Seq<Character> input = List.ofAll("123 456.1".toCharArray());

        Try<Tuple3<Dynamic, Dynamic, Seq<Character>>> outputTry = LEXER.tryBuild(input, START_CONTEXT);

        assertTrue(outputTry.isSuccess());
        Tuple3<Dynamic, Dynamic, Seq<Character>> output = outputTry.get();
        Dynamic token = output._1;
        Seq<Character> rest = output._3;

        assertEquals("123", token.asMap().get("Data").get().asString());
        assertEquals(TokenTypes.INT, token.asMap().get("Type").get().asEnum());
        assertEquals(List.ofAll(" 456.1".toCharArray()), rest);
    }

    private static final String TEST_STRING = "123.3\n 7852.12 \n 23";

    private static final Seq<Dynamic> EXPECTED_TOKENS = List.of(
            ofMap(HashMap.of(
                    "Data", ofString("123.3"),
                    "Type", ofEnum(TokenTypes.DECIMAL),
                    "Position", ofInt(0)
            )),
            ofMap(HashMap.of(
                    "Data", ofString("\n "),
                    "Type", ofEnum(TokenTypes.SPACE),
                    "Position", ofInt(0)
            )),
            ofMap(HashMap.of(
                    "Data", ofString("7852.12"),
                    "Type", ofEnum(TokenTypes.DECIMAL),
                    "Position", ofInt(1)
            )),
            ofMap(HashMap.of(
                    "Data", ofString(" \n "),
                    "Type", ofEnum(TokenTypes.SPACE),
                    "Position", ofInt(1)
            )),
            ofMap(HashMap.of(
                    "Data", ofString("23"),
                    "Type", ofEnum(TokenTypes.INT),
                    "Position", ofInt(2)
            ))
    );

    @Test
    void testBasicLanguageVerbose() {
        assertTokenMatch(EXPECTED_TOKENS, LEXER, START_CONTEXT, TEST_STRING);
    }

    static void assertTokenMatch(Seq<Dynamic> expected, Builder<Character, Dynamic> lexer,
                                   Dynamic startContext, String input) {
        Dynamic context = startContext;
        Seq<Character> rest = List.ofAll(input.toCharArray());

        Seq<Dynamic> tokens = List.empty();

        while (!rest.isEmpty()) {
            Try<Tuple3<Dynamic, Dynamic, Seq<Character>>> outputTry = lexer.tryBuild(rest, context);

            assertTrue(outputTry.isSuccess());
            Tuple3<Dynamic, Dynamic, Seq<Character>> output = outputTry.get();

            tokens = tokens.append(output._1);
            context = output._2;
            rest = output._3;
        }

        assertEquals(expected, tokens);
    }

    /*
     * Now for Table Lexer Tests....
     * The Table
     */


}
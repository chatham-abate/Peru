package org.perudevteam.peru.regex;

import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.control.Either;
import static io.vavr.control.Either.*;
import static org.perudevteam.peru.regex.RegexTerminalUtil.*;

public final class RegexNonTerminalUtil {
    enum RegexNonTerminal {
        ESCAPE,
        LITERAL,
        CLASS_PRESET,
        CLASS_ATOM,
        CLASS_INNER,
        CLASS,
        VALUE,
        NUMBER,
        QUANTIFIER,
        CONCAT,
        EXPRESSION,
        REGEX_GOAL
    }

    // Static Either Wrappers.

    static final Either<RegexNonTerminal, RegexTerminal>
    NT_ESCAPE = left(RegexNonTerminal.ESCAPE),
    NT_LITERAL = left(RegexNonTerminal.LITERAL),
    NT_CLASS_PRESET = left(RegexNonTerminal.CLASS_PRESET),
    NT_CLASS_ATOM = left(RegexNonTerminal.CLASS_ATOM),
    NT_CLASS_INNER = left(RegexNonTerminal.CLASS_INNER),
    NT_CLASS = left(RegexNonTerminal.CLASS),
    NT_VALUE = left(RegexNonTerminal.VALUE),
    NT_NUMBER = left(RegexNonTerminal.NUMBER),
    NT_QUANTIFIER = left(RegexNonTerminal.QUANTIFIER),
    NT_CONCAT = left(RegexNonTerminal.CONCAT),
    NT_EXPRESSION = left(RegexNonTerminal.EXPRESSION);

    // Rules.
    static final Seq<Either<RegexNonTerminal, RegexTerminal>>

    // Escape Rules.
    ESCAPE_R1 = List.of(T_BACKSLASH, T_STAR),
    ESCAPE_R2 = List.of(T_BACKSLASH, T_PLUS),
    ESCAPE_R3 = List.of(T_BACKSLASH, T_Q_MARK),
    ESCAPE_R4 = List.of(T_BACKSLASH, T_LEFT_B),
    ESCAPE_R5 = List.of(T_BACKSLASH, T_RIGHT_B),
    ESCAPE_R6 = List.of(T_BACKSLASH, T_LEFT_SQ),
    ESCAPE_R7 = List.of(T_BACKSLASH, T_RIGHT_SQ),
    ESCAPE_R8 = List.of(T_BACKSLASH, T_LEFT_P),
    ESCAPE_R9 = List.of(T_BACKSLASH, T_RIGHT_P),
    ESCAPE_R10 = List.of(T_BACKSLASH, T_COMMA),
    ESCAPE_R11 = List.of(T_BACKSLASH, T_PIPE),
    ESCAPE_R12 = List.of(T_BACKSLASH, T_DOT),
    ESCAPE_R13 = List.of(T_BACKSLASH, T_DASH),
    ESCAPE_R14 = List.of(T_BACKSLASH, T_CARROT),
    ESCAPE_R15 = List.of(T_BACKSLASH, T_BACKSLASH),

    // Literal Rules.
    LITERAL_R1 = List.of(NT_ESCAPE),
    LITERAL_R2 = List.of(T_DIGIT),
    LITERAL_R3 = List.of(T_NON_SPECIAL),

    // Class Preset Rule.
    CLASS_PRESET_R1 = List.of(T_BACKSLASH, T_NON_SPECIAL),
    CLASS_PRESET_R2 = List.of(T_BACKSLASH, T_DIGIT),

    // Atomic Class Rules.
    CLASS_ATOM_R1 = List.of(NT_LITERAL),
    CLASS_ATOM_R2 = List.of(NT_LITERAL, T_DASH, NT_LITERAL),

    // Class Inner Rules.
    CLASS_INNER_R1 = List.of(NT_CLASS_INNER, NT_CLASS_ATOM),
    CLASS_INNER_R2 = List.of(NT_CLASS_ATOM),

    // Character Class Rules.
    CLASS_R1 = List.of(T_LEFT_SQ, NT_CLASS_INNER, T_RIGHT_SQ),
    CLASS_R2 = List.of(T_LEFT_SQ, T_CARROT, NT_CLASS_INNER, T_RIGHT_SQ),

    // Value Rules.
    VALUE_R1 = List.of(NT_LITERAL),
    VALUE_R2 = List.of(NT_CLASS),
    VALUE_R3 = List.of(NT_CLASS_PRESET),
    VALUE_R4 = List.of(T_LEFT_P, NT_EXPRESSION, T_RIGHT_P),

    // Number Rules.
    NUMBER_R1 = List.of(NT_NUMBER, T_DIGIT),
    NUMBER_R2 = List.of(T_DIGIT),

    // Quantifier Rules.
    QUANTIFIER_R1 = List.of(NT_VALUE, T_PLUS),
    QUANTIFIER_R2 = List.of(NT_VALUE, T_STAR),
    QUANTIFIER_R3 = List.of(NT_VALUE, T_Q_MARK),
    QUANTIFIER_R4 = List.of(NT_VALUE, T_LEFT_B, NT_NUMBER, T_RIGHT_B),
    QUANTIFIER_R5 = List.of(NT_VALUE, T_LEFT_B, NT_NUMBER, T_COMMA, T_RIGHT_B),
    QUANTIFIER_R6 = List.of(NT_VALUE, T_LEFT_B, NT_NUMBER, T_COMMA, NT_NUMBER, T_RIGHT_B),

    // Concat Rules.
    CONCAT_R1 = List.of(NT_CONCAT, NT_QUANTIFIER),
    CONCAT_R2 = List.of(NT_QUANTIFIER),

    // Expressions Rules.
    EXPRESSION_R1 = List.of(NT_EXPRESSION, T_PIPE, NT_CONCAT),
    EXPRESSION_R2 = List.of(NT_CONCAT),

    // Regex Goal Rule.
    REGEX_GOAL_R1 = List.of(NT_EXPRESSION);
}
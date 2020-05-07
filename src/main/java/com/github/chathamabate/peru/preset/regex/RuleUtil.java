package com.github.chathamabate.peru.preset.regex;

import io.vavr.collection.Array;
import io.vavr.collection.Seq;
import io.vavr.control.Either;
import static io.vavr.control.Either.*;

public final class RuleUtil {
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

    static final Either<RegexNonTerminal, LexerUtil.RegexTerminal>
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
    NT_EXPRESSION = left(RegexNonTerminal.EXPRESSION),
    
    T_CARROT = right(LexerUtil.RegexTerminal.CARROT),
    T_DOT = right(LexerUtil.RegexTerminal.DOT),
    T_LEFT_SQ = right(LexerUtil.RegexTerminal.LEFT_SQ),
    T_RIGHT_SQ = right(LexerUtil.RegexTerminal.RIGHT_SQ),
    T_LEFT_P = right(LexerUtil.RegexTerminal.LEFT_P),
    T_RIGHT_P = right(LexerUtil.RegexTerminal.RIGHT_P),
    T_LEFT_B = right(LexerUtil.RegexTerminal.LEFT_B),
    T_RIGHT_B = right(LexerUtil.RegexTerminal.RIGHT_B),
    T_STAR = right(LexerUtil.RegexTerminal.STAR),
    T_COMMA = right(LexerUtil.RegexTerminal.COMMA),
    T_Q_MARK = right(LexerUtil.RegexTerminal.Q_MARK),
    T_PLUS = right(LexerUtil.RegexTerminal.PLUS),
    T_PIPE = right(LexerUtil.RegexTerminal.PIPE),
    T_BACKSLASH = right(LexerUtil.RegexTerminal.BACKSLASH),
    T_DASH = right(LexerUtil.RegexTerminal.DASH),
    T_DIGIT = right(LexerUtil.RegexTerminal.DIGIT),
    T_NON_SPECIAL = right(LexerUtil.RegexTerminal.NON_SPECIAL);

    // Rules.
    static final Seq<Either<RegexNonTerminal, LexerUtil.RegexTerminal>>

    // Escape Rules.
    ESCAPE_R1 = Array.of(T_BACKSLASH, T_STAR),
    ESCAPE_R2 = Array.of(T_BACKSLASH, T_PLUS),
    ESCAPE_R3 = Array.of(T_BACKSLASH, T_Q_MARK),
    ESCAPE_R4 = Array.of(T_BACKSLASH, T_LEFT_B),
    ESCAPE_R5 = Array.of(T_BACKSLASH, T_RIGHT_B),
    ESCAPE_R6 = Array.of(T_BACKSLASH, T_LEFT_SQ),
    ESCAPE_R7 = Array.of(T_BACKSLASH, T_RIGHT_SQ),
    ESCAPE_R8 = Array.of(T_BACKSLASH, T_LEFT_P),
    ESCAPE_R9 = Array.of(T_BACKSLASH, T_RIGHT_P),
    ESCAPE_R10 = Array.of(T_BACKSLASH, T_COMMA),
    ESCAPE_R11 = Array.of(T_BACKSLASH, T_PIPE),
    ESCAPE_R12 = Array.of(T_BACKSLASH, T_DOT),
    ESCAPE_R13 = Array.of(T_BACKSLASH, T_DASH),
    ESCAPE_R14 = Array.of(T_BACKSLASH, T_CARROT),
    ESCAPE_R15 = Array.of(T_BACKSLASH, T_BACKSLASH),

    // eral Rules.
    LITERAL_R1 = Array.of(NT_ESCAPE),
    LITERAL_R2 = Array.of(T_DIGIT),
    LITERAL_R3 = Array.of(T_NON_SPECIAL),

    // Class Preset Rule.
    CLASS_PRESET_R1 = Array.of(T_BACKSLASH, T_NON_SPECIAL),
    CLASS_PRESET_R2 = Array.of(T_BACKSLASH, T_DIGIT),
    CLASS_PRESET_R3 = Array.of(T_DOT),

    // Atomic Class Rules.
    CLASS_ATOM_R1 = Array.of(NT_LITERAL),
    CLASS_ATOM_R2 = Array.of(NT_LITERAL, T_DASH, NT_LITERAL),

    // Class Inner Rules.
    CLASS_INNER_R1 = Array.of(NT_CLASS_INNER, NT_CLASS_ATOM),
    CLASS_INNER_R2 = Array.of(NT_CLASS_ATOM),

    // Character Class Rules.
    CLASS_R1 = Array.of(T_LEFT_SQ, NT_CLASS_INNER, T_RIGHT_SQ),
    CLASS_R2 = Array.of(T_LEFT_SQ, T_CARROT, NT_CLASS_INNER, T_RIGHT_SQ),

    // Value Rules.
    VALUE_R1 = Array.of(NT_LITERAL),
    VALUE_R2 = Array.of(NT_CLASS),
    VALUE_R3 = Array.of(NT_CLASS_PRESET),
    VALUE_R4 = Array.of(T_LEFT_P, NT_EXPRESSION, T_RIGHT_P),

    // Number Rules.
    NUMBER_R1 = Array.of(NT_NUMBER, T_DIGIT),
    NUMBER_R2 = Array.of(T_DIGIT),

    // Quantifier Rules.
    QUANTIFIER_R1 = Array.of(NT_VALUE, T_PLUS),
    QUANTIFIER_R2 = Array.of(NT_VALUE, T_STAR),
    QUANTIFIER_R3 = Array.of(NT_VALUE, T_Q_MARK),
    QUANTIFIER_R4 = Array.of(NT_VALUE, T_LEFT_B, NT_NUMBER, T_RIGHT_B),
    QUANTIFIER_R5 = Array.of(NT_VALUE, T_LEFT_B, NT_NUMBER, T_COMMA, T_RIGHT_B),
    QUANTIFIER_R6 = Array.of(NT_VALUE, T_LEFT_B, NT_NUMBER, T_COMMA, NT_NUMBER, T_RIGHT_B),
    QUANTIFIER_R7 = Array.of(NT_VALUE),

    // Concat Rules.
    CONCAT_R1 = Array.of(NT_CONCAT, NT_QUANTIFIER),
    CONCAT_R2 = Array.of(NT_QUANTIFIER),

    // Expressions Rules.
    EXPRESSION_R1 = Array.of(NT_EXPRESSION, T_PIPE, NT_CONCAT),
    EXPRESSION_R2 = Array.of(NT_CONCAT),

    // Regex Goal Rule.
    REGEX_GOAL_R1 = Array.of(NT_EXPRESSION);
}

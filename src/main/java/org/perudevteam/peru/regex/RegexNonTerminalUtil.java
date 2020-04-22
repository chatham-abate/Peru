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
        EXPRESSION
    }

    // Static Either Wrappers.

    private static final Either<RegexNonTerminal, RegexTerminal>
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
    private static final Seq<Either<RegexNonTerminal, RegexTerminal>>

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
    CLASS_PRESET_R1 = List.of(T_BACKSLASH, T_NON_SPECIAL)
}

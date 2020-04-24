package org.perudevteam.peru.regex;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import org.perudevteam.parser.Tokenized;
import io.vavr.control.Either;
import org.perudevteam.peru.regex.RegexNonTerminalUtil.RegexNonTerminal;

import static io.vavr.control.Either.*;

public final class RegexTerminalUtil {
    private RegexTerminalUtil() {
        // Should never be initialized.
    }

    static final Set<Character> ASCII_SET = buildASCIISet();

    private static Set<Character> buildASCIISet() {
        Set<Character> asciiSet = HashSet.empty();

        for (char c = 0; c < 128; c++) {
            asciiSet = asciiSet.add(c);
        }

        return asciiSet;
    }

    enum RegexTerminal {
        CARROT,
        DOT,
        LEFT_SQ,
        RIGHT_SQ,
        LEFT_P,
        RIGHT_P,
        LEFT_B,
        RIGHT_B,
        STAR,
        COMMA,
        Q_MARK,
        PLUS,
        PIPE,
        BACKSLASH,
        DASH,
        DIGIT,
        NON_SPECIAL
    }

    // Either Wrappers.
    static final Either<RegexNonTerminal, RegexTerminal>
    T_CARROT = right(RegexTerminal.CARROT),
    T_DOT = right(RegexTerminal.DOT),
    T_LEFT_SQ = right(RegexTerminal.LEFT_SQ),
    T_RIGHT_SQ = right(RegexTerminal.RIGHT_SQ),
    T_LEFT_P = right(RegexTerminal.LEFT_P),
    T_RIGHT_P = right(RegexTerminal.RIGHT_P),
    T_LEFT_B = right(RegexTerminal.LEFT_B),
    T_RIGHT_B = right(RegexTerminal.RIGHT_B),
    T_STAR = right(RegexTerminal.STAR),
    T_COMMA = right(RegexTerminal.COMMA),
    T_Q_MARK = right(RegexTerminal.Q_MARK),
    T_PLUS = right(RegexTerminal.PLUS),
    T_PIPE = right(RegexTerminal.PIPE),
    T_BACKSLASH = right(RegexTerminal.BACKSLASH),
    T_DASH = right(RegexTerminal.DASH),
    T_DIGIT = right(RegexTerminal.DIGIT),
    T_NON_SPECIAL = right(RegexTerminal.NON_SPECIAL);

    static final Map<Character, RegexTerminal> CMDS =
            HashMap.<Character, RegexTerminal>empty()
                    .put('^', RegexTerminal.CARROT)
                    .put('.', RegexTerminal.DOT)
                    .put('[', RegexTerminal.LEFT_SQ)
                    .put(']', RegexTerminal.RIGHT_SQ)
                    .put('(', RegexTerminal.LEFT_P)
                    .put(')', RegexTerminal.RIGHT_P)
                    .put('{', RegexTerminal.LEFT_B)
                    .put('}', RegexTerminal.RIGHT_B)
                    .put('*', RegexTerminal.STAR)
                    .put(',', RegexTerminal.COMMA)
                    .put('?', RegexTerminal.Q_MARK)
                    .put('+', RegexTerminal.PLUS)
                    .put('|', RegexTerminal.PIPE)
                    .put('\\', RegexTerminal.BACKSLASH)
                    .put('-', RegexTerminal.DASH);

    static RegexTerminal getRegexCharType(Character input) {
        if (input > 127) {
            throw new IllegalArgumentException("Given character not basic ASCII.");
        }

        if ('0' <= input && input <= '9') {
            return RegexTerminal.DIGIT;
        }

        return CMDS.getOrElse(input, RegexTerminal.NON_SPECIAL);
    }

    static Tuple2<Character, Tokenized<RegexTerminal>> asRegexToken(Character input) {
        return Tuple.of(input, Tokenized.token(getRegexCharType(input)));
    }

    static Stream<Tuple2<Character, Tokenized<RegexTerminal>>> asRegexTokenStream(String regex) {
        return Stream.ofAll(regex.toCharArray()).map(RegexTerminalUtil::asRegexToken);
    }
}

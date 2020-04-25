package org.perudevteam.peru.regex;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import org.perudevteam.parser.Tokenized;
import io.vavr.control.Either;
import org.perudevteam.peru.regex.RuleUtil.RegexNonTerminal;

import static io.vavr.control.Either.*;

public final class LexerUtil {
    private LexerUtil() {
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
        return Stream.ofAll(regex.toCharArray()).map(LexerUtil::asRegexToken);
    }
}

package org.perudevteam.peru.regex;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import org.intellij.lang.annotations.Language;
import org.perudevteam.parser.Tokenized;

import java.util.Scanner;
import java.util.regex.Pattern;

public class RegexCharUtil {
    enum RegexCharType {
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
        OTHER
    }

    private static Map<Character, RegexCharType> CMDS =
            HashMap.<Character, RegexCharType>empty()
                    .put('^', RegexCharType.CARROT)
                    .put('.', RegexCharType.DOT)
                    .put('[', RegexCharType.LEFT_SQ)
                    .put(']', RegexCharType.RIGHT_SQ)
                    .put('(', RegexCharType.LEFT_P)
                    .put(')', RegexCharType.RIGHT_P)
                    .put('{', RegexCharType.LEFT_B)
                    .put('}', RegexCharType.RIGHT_B)
                    .put('*', RegexCharType.STAR)
                    .put(',', RegexCharType.COMMA)
                    .put('?', RegexCharType.Q_MARK)
                    .put('+', RegexCharType.PLUS)
                    .put('|', RegexCharType.PIPE)
                    .put('\\', RegexCharType.BACKSLASH)
                    .put('-', RegexCharType.DASH);

    public static RegexCharType getRegexCharType(Character input) {
        if (input > 127) {
            throw new IllegalArgumentException("Given character not basic ASCII.");
        }

        if ('0' <= input && input <= '9') {
            return RegexCharType.DIGIT;
        }

        return CMDS.getOrElse(input, RegexCharType.OTHER);
    }

    public static Tuple2<Character, Tokenized<RegexCharType>> asRegexToken(Character input) {
        return Tuple.of(input, Tokenized.token(getRegexCharType(input)));
    }


    public static void main(String[] args) {
        String test = "b";
        System.out.println(test.matches(
                "a{1,}"
        ));
    }


}

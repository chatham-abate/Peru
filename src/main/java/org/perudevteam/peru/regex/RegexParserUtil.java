package org.perudevteam.peru.regex;

import io.vavr.CheckedFunction2;
import io.vavr.Tuple;
import io.vavr.collection.*;
import org.perudevteam.fa.NFAutomaton;
import org.perudevteam.parser.grammar.SemanticProduction;
import org.perudevteam.peru.regex.RegexParse;
import org.perudevteam.type.Tagged;

import static org.perudevteam.peru.regex.RegexTerminalUtil.*;
import static org.perudevteam.peru.regex.RegexNonTerminalUtil.*;
import static org.perudevteam.peru.regex.RegexParse.*;
import static org.perudevteam.parser.grammar.SemanticProduction.*;


public final class RegexParserUtil {
    private RegexParserUtil() {
        // Never to be initialized.
    }

    static final Map<Character, Set<Character>> CLASS_PRESETS = HashMap.empty();

    static RegexParse getClassPreset(RegexParse key) {
        char k = key.asCharacter();

        if (!CLASS_PRESETS.containsKey(k)) {
            throw new IllegalArgumentException("Preset Class does not exist " + k + ".");
        }

        return ofCharacterSet(CLASS_PRESETS.get(k).get());
    }

    static final NFAutomaton<Character, Character, ?> TWO_STATE_NFA =
            new NFAutomaton<>(2, ASCII_SET, c -> c);

    static final Map<RegexTerminal, CheckedFunction2<Character, Tagged<RegexTerminal>, RegexParse>>
    REGEX_TERMINAL_RES_GENS = HashMap.ofEntries(
            List.of(RegexTerminal.values()).map(
                    r -> Tuple.of(r, (l, d) -> ofCharacter(l))
            )
    );

    static final SemanticProduction<RegexNonTerminal, RegexTerminal, RegexParse>
    // Escape Productions. (Returns Character)
    ESCAPE_P1 = semanticProduction(RegexNonTerminal.ESCAPE, ESCAPE_R1, c -> c.get(1)),
    ESCAPE_P2 = semanticProduction(RegexNonTerminal.ESCAPE, ESCAPE_R2, c -> c.get(1)),
    ESCAPE_P3 = semanticProduction(RegexNonTerminal.ESCAPE, ESCAPE_R3, c -> c.get(1)),
    ESCAPE_P4 = semanticProduction(RegexNonTerminal.ESCAPE, ESCAPE_R4, c -> c.get(1)),
    ESCAPE_P5 = semanticProduction(RegexNonTerminal.ESCAPE, ESCAPE_R5, c -> c.get(1)),
    ESCAPE_P6 = semanticProduction(RegexNonTerminal.ESCAPE, ESCAPE_R6, c -> c.get(1)),
    ESCAPE_P7 = semanticProduction(RegexNonTerminal.ESCAPE, ESCAPE_R7, c -> c.get(1)),
    ESCAPE_P8 = semanticProduction(RegexNonTerminal.ESCAPE, ESCAPE_R8, c -> c.get(1)),
    ESCAPE_P9 = semanticProduction(RegexNonTerminal.ESCAPE, ESCAPE_R9, c -> c.get(1)),
    ESCAPE_P10 = semanticProduction(RegexNonTerminal.ESCAPE, ESCAPE_R10, c -> c.get(1)),
    ESCAPE_P11 = semanticProduction(RegexNonTerminal.ESCAPE, ESCAPE_R11, c -> c.get(1)),
    ESCAPE_P12 = semanticProduction(RegexNonTerminal.ESCAPE, ESCAPE_R12, c -> c.get(1)),
    ESCAPE_P13 = semanticProduction(RegexNonTerminal.ESCAPE, ESCAPE_R13, c -> c.get(1)),
    ESCAPE_P14 = semanticProduction(RegexNonTerminal.ESCAPE, ESCAPE_R14, c -> c.get(1)),
    ESCAPE_P15 = semanticProduction(RegexNonTerminal.ESCAPE, ESCAPE_R15, c -> c.get(1)),

    // Literal Productions. (Returns Character)
    LITERAL_P1 = semanticProduction(RegexNonTerminal.LITERAL, LITERAL_R1, c -> c.get(0)),
    LITERAL_P2 = semanticProduction(RegexNonTerminal.LITERAL, LITERAL_R2, c -> c.get(0)),
    LITERAL_P3 = semanticProduction(RegexNonTerminal.LITERAL, LITERAL_R3, c -> c.get(0)),

    // Class Preset Productions. (Returns Char Set)
    CLASS_PRESET_P1 = semanticProduction(RegexNonTerminal.CLASS_PRESET, CLASS_PRESET_R1,
            c -> getClassPreset(c.get(1))),
    CLASS_PRESET_P2 = semanticProduction(RegexNonTerminal.CLASS_PRESET, CLASS_PRESET_R2,
            c -> getClassPreset(c.get(1))),

    // Class Atomic Productions. (Returns Char Set)
    CLASS_ATOM_P1 = semanticProduction(RegexNonTerminal.CLASS_ATOM, CLASS_ATOM_R1,
            c -> ofCharacterSet(HashSet.of(c.get(0).asCharacter()))),
    CLASS_ATOM_P2 = semanticProduction(RegexNonTerminal.CLASS_ATOM, CLASS_ATOM_R2, c -> {
        char start = c.get(0).asCharacter();
        char end = c.get(2).asCharacter();

        // We must make a set starting at start and ending at end of characters.
        if (start > end) {
            throw new IllegalArgumentException("Illegal Range " + start + "-" + end + ".");
        }

        Set<Character> range = HashSet.empty();

        for (char character = start; character <= end; character++) {
            range = range.add(character);
        }

        return ofCharacterSet(range);
    }),

    // Class Inner Productions. (Returns a Character Set)
    CLASS_INNER_P1 = semanticProduction(RegexNonTerminal.CLASS_INNER, CLASS_INNER_R1,
            c -> ofCharacterSet(c.get(0).asCharacterSet().addAll(c.get(1).asCharacterSet()))),
    CLASS_INNER_P2 = semanticProduction(RegexNonTerminal.CLASS_INNER, CLASS_INNER_R2, c -> c.get(0)),

    // Class Productions. (Returns a Character Set)
    CLASS_P1 = semanticProduction(RegexNonTerminal.CLASS, CLASS_R1, c -> c.get(1)),
    CLASS_P2 = semanticProduction(RegexNonTerminal.CLASS, CLASS_R2,
            c -> ofCharacterSet(ASCII_SET.filter(character -> !c.get(2).asCharacterSet().contains(character)))),

    // Value Productions. (Returns an NFA)
    VALUE_P1 = semanticProduction(RegexNonTerminal.VALUE, VALUE_R1,
            c -> ofNFAutomaton(TWO_STATE_NFA.withSingleTransition(0, 1, c.get(0).asCharacter()))),
    VALUE_P2 = semanticProduction(RegexNonTerminal.VALUE, VALUE_R2,
            c -> ofNFAutomaton(TWO_STATE_NFA.withSingleTransitions(
                    HashSet.of(0), HashSet.of(1), c.get(0).asCharacterSet()))),
    VALUE_P3 = semanticProduction(RegexNonTerminal.VALUE, VALUE_R3,
            c -> ofNFAutomaton(TWO_STATE_NFA.withSingleTransitions(
                    HashSet.of(0), HashSet.of(1), c.get(0).asCharacterSet()))),
    VALUE_P4 = semanticProduction(RegexNonTerminal.VALUE, VALUE_R4, c -> c.get(1)),

    // Number Productions. (Returns a String)
    NUMBER_P1 = semanticProduction(RegexNonTerminal.NUMBER, NUMBER_R1,
            c -> ofString(c.get(0).asString() + c.get(1).asCharacter())),
    NUMBER_P2 = semanticProduction(RegexNonTerminal.NUMBER, NUMBER_R2,
            c -> ofString("" + c.get(0).asCharacter()));






}

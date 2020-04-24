package org.perudevteam.peru.regex;

import io.vavr.CheckedFunction2;
import io.vavr.Tuple;
import io.vavr.Tuple3;
import io.vavr.collection.*;
import org.perudevteam.fa.NFAutomaton;
import org.perudevteam.parser.Parser;
import org.perudevteam.parser.Tokenized;
import org.perudevteam.parser.grammar.SemanticCFGrammar;
import org.perudevteam.parser.grammar.SemanticProduction;
import org.perudevteam.parser.lrone.LROneParser;
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

    static final Set<Character> DIGITS = ASCII_SET.filter(Character::isDigit);
    static final Set<Character> WHITESPACE = ASCII_SET.filter(Character::isWhitespace);

    static final Map<Character, Set<Character>> CLASS_PRESETS = HashMap.of(
            's', WHITESPACE,
            'd', DIGITS
    );

    static RegexParse getClassPreset(RegexParse key) {
        char k = key.asCharacter();

        if (!CLASS_PRESETS.containsKey(k)) {
            throw new IllegalArgumentException("Preset Class does not exist " + k + ".");
        }

        return ofCharacterSet(CLASS_PRESETS.get(k).get());
    }

    static int extractNaturalNum(RegexParse parse) {
        int integer = Integer.parseInt(parse.asString());
        if (integer < 0) {
            throw new IllegalArgumentException("Expected Natural Number, found Negative.");
        }

        return integer;
    }

    static final NFAutomaton<Character, Character, Object> ONE_STATE_NFA =
            new NFAutomaton<>(1, ASCII_SET, c -> c);

    static final NFAutomaton<Character, Character, Object> TWO_STATE_NFA =
            new NFAutomaton<>(2, ASCII_SET, c -> c);

    static final Map<RegexTerminal, CheckedFunction2<Character, Tokenized<RegexTerminal>, RegexParse>>
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
    CLASS_PRESET_P3 = semanticProduction(RegexNonTerminal.CLASS_PRESET, CLASS_PRESET_R3,
            c -> ofCharacterSet(ASCII_SET)),


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
            c -> ofString("" + c.get(0).asCharacter())),

    // Quantifier Productions. (Returns an NFA)
    QUANTIFIER_P1 = semanticProduction(RegexNonTerminal.QUANTIFIER, QUANTIFIER_R1,
            c -> c.get(0).mapNFAutomaton(nfa -> nfa.withEpsilonTransition(nfa.getNumberOfStates() - 1, 0))),
    QUANTIFIER_P2 = semanticProduction(RegexNonTerminal.QUANTIFIER, QUANTIFIER_R2,
            c -> c.get(0).mapNFAutomaton(nfa -> nfa.withEpsilonTransition(0, nfa.getNumberOfStates() - 1)
                    .withEpsilonTransition(nfa.getNumberOfStates() - 1, 0))),
    QUANTIFIER_P3 = semanticProduction(RegexNonTerminal.QUANTIFIER, QUANTIFIER_R3,
            c -> c.get(0).mapNFAutomaton(nfa -> nfa.withEpsilonTransition(0, nfa.getNumberOfStates() - 1))),
    QUANTIFIER_P4 = semanticProduction(RegexNonTerminal.QUANTIFIER, QUANTIFIER_R4,
            c -> c. get(0).mapNFAutomaton(nfa -> nfa.repeat(nfa.getNumberOfStates() - 1,
                    extractNaturalNum(c.get(2))))),
    QUANTIFIER_P5 = semanticProduction(RegexNonTerminal.QUANTIFIER, QUANTIFIER_R5,
            c -> c.get(0).mapNFAutomaton(nfa -> {
                int minimum = extractNaturalNum(c.get(2));
                NFAutomaton<Character, Character, Object> prefix = nfa
                        .repeat(nfa.getNumberOfStates() - 1, minimum);
                NFAutomaton<Character, Character, Object> suffix = nfa
                        .withEpsilonTransition(0, nfa.getNumberOfStates() - 1)
                        .withEpsilonTransition(nfa.getNumberOfStates() - 1, 0);

                return prefix.combineWithEpsilonConnection(prefix.getNumberOfStates() - 1, suffix);
            })),
    QUANTIFIER_P6 = semanticProduction(RegexNonTerminal.QUANTIFIER, QUANTIFIER_R6,
            c -> c.get(0).mapNFAutomaton(nfa -> {
                // We need to find the range.
                int min = extractNaturalNum(c.get(2));
                int max = extractNaturalNum(c.get(4));
                // Validate the range.
                if (max < min) {
                    throw new IllegalArgumentException("Invalid Range Given [" + min + ", " + max + "].");
                }

                // Build the NFA.
                NFAutomaton<Character, Character, Object> productNFA = nfa
                        .repeat(nfa.getNumberOfStates() - 1, max);

                for (int i = min; i < max; i++) {
                    productNFA = productNFA.withEpsilonTransition(
                            i * nfa.getNumberOfStates(),
                            productNFA.getNumberOfStates() - 1
                    );
                }

                return productNFA;
            })),
    QUANTIFIER_P7 = semanticProduction(RegexNonTerminal.QUANTIFIER, QUANTIFIER_R7,
            c -> c.get(0)),

    // Concat Productions. (Returns NFA)
    CONCAT_P1 = semanticProduction(RegexNonTerminal.CONCAT, CONCAT_R1,
            c -> c.get(0).mapNFAutomaton(nfa -> nfa.combineWithEpsilonConnection(
                    nfa.getNumberOfStates() - 1, c.get(1).asNFAutomaton()))),
    CONCAT_P2 = semanticProduction(RegexNonTerminal.CONCAT, CONCAT_R2, c -> c.get(0)),

    //  Expression Productions. (Returns NFA)
    EXPRESSION_P1 = semanticProduction(RegexNonTerminal.EXPRESSION, EXPRESSION_R1,
            c -> c.get(0).mapNFAutomaton(nfa -> {
                NFAutomaton<Character, Character, Object> otherNFA = c.get(2).asNFAutomaton();
                NFAutomaton<Character, Character, Object> fullNFA = nfa
                        .combineWithEpsilonConnection(0, otherNFA).appendStates(1);

                return fullNFA
                        .withEpsilonTransition(
                                nfa.getNumberOfStates() - 1,
                                fullNFA.getNumberOfStates() - 1
                        )
                        .withEpsilonTransition(
                                fullNFA.getNumberOfStates() - 2,
                                fullNFA.getNumberOfStates() - 1
                        );
            })),
    EXPRESSION_P2 = semanticProduction(RegexNonTerminal.EXPRESSION, EXPRESSION_R2, c -> c.get(0)),

    // Regex Goal Productions. (Returns NFA)
    REGEX_GOAL_P1 = semanticProduction(RegexNonTerminal.REGEX_GOAL, REGEX_GOAL_R1, c -> c.get(0));

    // Semantic Grammar
    static final SemanticCFGrammar<RegexNonTerminal, RegexTerminal,
            SemanticProduction<RegexNonTerminal, RegexTerminal, RegexParse>,
            Character, Tokenized<RegexTerminal>, RegexParse> REGEX_CFG = new SemanticCFGrammar<>(
            RegexNonTerminal.REGEX_GOAL,
            REGEX_TERMINAL_RES_GENS,
            Array.of(
                    ESCAPE_P1,
                    ESCAPE_P2,
                    ESCAPE_P3,
                    ESCAPE_P4,
                    ESCAPE_P5,
                    ESCAPE_P6,
                    ESCAPE_P7,
                    ESCAPE_P8,
                    ESCAPE_P9,
                    ESCAPE_P10,
                    ESCAPE_P11,
                    ESCAPE_P12,
                    ESCAPE_P13,
                    ESCAPE_P14,
                    ESCAPE_P15,

                    LITERAL_P1,
                    LITERAL_P2,
                    LITERAL_P3,

                    CLASS_PRESET_P1,
                    CLASS_PRESET_P2,
                    CLASS_PRESET_P3,

                    CLASS_ATOM_P1,
                    CLASS_ATOM_P2,

                    CLASS_INNER_P1,
                    CLASS_INNER_P2,

                    CLASS_P1,
                    CLASS_P2,

                    VALUE_P1,
                    VALUE_P2,
                    VALUE_P3,
                    VALUE_P4,

                    NUMBER_P1,
                    NUMBER_P2,

                    QUANTIFIER_P1,
                    QUANTIFIER_P2,
                    QUANTIFIER_P3,
                    QUANTIFIER_P4,
                    QUANTIFIER_P5,
                    QUANTIFIER_P6,
                    QUANTIFIER_P7,

                    CONCAT_P1,
                    CONCAT_P2,

                    EXPRESSION_P1,
                    EXPRESSION_P2,

                    REGEX_GOAL_P1
            )
    );

    static final Parser<RegexTerminal, Character, Tokenized<RegexTerminal>, RegexParse>
    REGEX_PARSER = LROneParser.lrOneParser(REGEX_CFG,
            t -> new Exception("Cannot parse token " + t._1 + "."),
            () -> new Exception("End of Expression not expected."));
}

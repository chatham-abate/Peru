package org.perudevteam.peru.regex;

import io.vavr.Tuple2;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.Seq;
import io.vavr.control.Try;
import org.perudevteam.fa.NFAutomaton;
import org.perudevteam.parser.Tokenized;

import java.util.Objects;

import static org.perudevteam.peru.regex.RegexTerminalUtil.*;
import static org.perudevteam.peru.regex.RegexNonTerminalUtil.*;
import static org.perudevteam.peru.regex.RegexParserUtil.*;

public final class PeruRegex {
    private PeruRegex() {
        // Should never be initialized.
    }

    public static NFAutomaton<Character, Character, Object> buildNFA(String regex) throws Throwable {
        Objects.requireNonNull(regex);
        Seq<Tuple2<Character, Tokenized<RegexTerminal>>> tokens = asRegexTokenStream(regex);
        return REGEX_PARSER.parseUnchecked(tokens).asNFAutomaton();
    }

    public static Try<NFAutomaton<Character, Character, Object>> tryBuildNFA(String regex)  {
        return Try.of(() -> buildNFA(regex));
    }

    public static <O> NFAutomaton<Character, Character, O> buildNFAWithResult(String regex, O output)
            throws Throwable {
        NFAutomaton<Character, Character, Object> nfa = buildNFA(regex);
        HashMap<Integer, O> newAccepting = HashMap.of(nfa.getNumberOfStates() - 1, output);
        return nfa.withAcceptingStates(newAccepting);
    }

    public static <O> Try<NFAutomaton<Character, Character, O>> tryBuildNFAWithResult(String regex, O output) {
        return Try.of(() -> buildNFAWithResult(regex, output));
    }

    public static <O> NFAutomaton<Character, Character, O> buildMultiResultNFA(
            Seq<? extends Tuple2<? extends String, ? extends O>> patterns) throws Throwable {
        Objects.requireNonNull(patterns);
        patterns.forEach(Objects::requireNonNull);

        NFAutomaton<Character, Character, O> nfa = ONE_STATE_NFA.withAcceptingStates(HashMap.empty());

        for (Tuple2<? extends String, ? extends O> pattern: patterns) {
            nfa = nfa.combineWithEpsilonConnection(
                    0,
                    buildNFAWithResult(pattern._1, pattern._2)
            );
        }

        return nfa;
    }

    public static <O> Try<NFAutomaton<Character, Character, O>> tryBuildMultiResultNFA(
            Seq<? extends Tuple2<? extends String, ? extends O>> patterns) {
        return Try.of(() -> buildMultiResultNFA(patterns));
    }
}

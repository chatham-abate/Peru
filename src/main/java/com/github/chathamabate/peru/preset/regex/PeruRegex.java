package com.github.chathamabate.peru.preset.regex;

import com.github.chathamabate.peru.fa.DFAutomaton;
import com.github.chathamabate.peru.fa.NFAutomaton;
import com.github.chathamabate.peru.parser.Tokenized;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.collection.HashMap;
import io.vavr.collection.HashSet;
import io.vavr.collection.Seq;
import io.vavr.collection.Set;
import io.vavr.control.Try;

import java.util.Objects;

import static com.github.chathamabate.peru.preset.regex.ParserUtil.*;

public final class PeruRegex {
    private PeruRegex() {
        // Should never be initialized.
    }

    public static NFAutomaton<Character, Character, Object> buildNFA(String regex) throws Throwable {
        Objects.requireNonNull(regex);
        Seq<Tuple2<Character, Tokenized<LexerUtil.RegexTerminal>>> tokens = LexerUtil.asRegexTokenStream(regex);
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

    public static <O> DFAutomaton<Character, Character, O> buildMultiResultDFA(
            Seq<? extends Tuple3<? extends String, ? extends Boolean, ? extends O>> patternSignals
    ) throws Throwable {
        Seq<Tuple2<String, O>> patterns = patternSignals.map(tuple -> Tuple.of(tuple._1, tuple._3));
        Set<O> strongSignals = HashSet.ofAll(patternSignals.filter(Tuple3::_2).map(Tuple3::_3));

        return buildMultiResultNFA(patterns).toDFA(strongSignals);
    }

    public static <O> Try<DFAutomaton<Character, Character, O>> tryBuildMultiResultDFA(
            Seq<? extends Tuple3<? extends String, ? extends Boolean, ? extends O>> patternSignals
    ) {
        return Try.of(() -> buildMultiResultDFA(patternSignals));
    }
}

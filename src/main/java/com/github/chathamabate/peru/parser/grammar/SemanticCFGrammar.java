package com.github.chathamabate.peru.parser.grammar;

import io.vavr.CheckedFunction2;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.collection.Set;
import io.vavr.control.Either;
import io.vavr.control.Try;
import com.github.chathamabate.peru.parser.Tokenized;

import java.util.Objects;


/**
 * This class represents a context free grammar whose productions have the ability to
 * build some result given the results corresponding to each symbol on that
 * productions rule.
 * <br>
 * Besides the productions, this grammar requires a way of turning terminal symbols
 * into results. Thus, it contains a map of functions for turning a token of
 * any terminal type into a result.
 * @see SemanticProduction
 * @param <NT> The non-terminal <b>Enum</b> type of the grammar.
 * @param <T> The terminal <b>Enum</b> type of the grammar.
 * @param <P> The type of <b>SemanticProduction</b> used by the grammar.
 * @param <L> The lexeme type of the tokens to be parsed by this grammar.
 * @param <D> The data type of the tokens to be parsed by this grammar.
 * @param <R> The result type produced by this grammar.
 */
public class SemanticCFGrammar<NT extends Enum<NT>, T extends Enum<T>,
        P extends SemanticProduction<NT, T, R>, L, D extends Tokenized<T>, R> extends CFGrammar<NT, T, P> {

    /**
     * A map of terminal <b>Enum</b> types to <b>CheckedFunction</b>s.
     * Each function in this map will translate the lexeme and data of a token
     * into some result of type <b>R</b>. This function is allowed to throw
     * an error during this translation step.
     */
    private final Map<T, CheckedFunction2<L, D, R>> terminalResGenerators;

    /**
     * Constructor.
     *
     * @param start The start symbol of the grammar.
     * @param termResGens The map of terminal result generating functions.
     * @param prods The sequence of production rule for the grammar.
     */
    public SemanticCFGrammar(NT start, Map<? extends T, ? extends CheckedFunction2<L, D, ? extends R>> termResGens,
                             Seq<P> prods) {
        super(start, prods);

        // None of the generators can be null.
        Objects.requireNonNull(termResGens);
        termResGens.values().forEach(Objects::requireNonNull);

        Map<T, CheckedFunction2<L, D, R>> narrowTermResGens =
                Map.narrow(termResGens.mapValues(CheckedFunction2::narrow));

        for (T terminal: getTerminalsUsed()) {
            if (!narrowTermResGens.containsKey(terminal)) {
                throw new IllegalArgumentException("All terminal types need result generators.");
            }
        }

        terminalResGenerators = narrowTermResGens;
    }

    /**
     * Direct Constructor with no checks.
     * <br>
     * meant for interior use only.
     *
     * @param start The start symbol of the grammar.
     * @param prodMap The productions of the grammar.
     * @param termsUsed The terminals used by the grammar.
     * @param termResGens The terminal result generating functions.
     */
    protected SemanticCFGrammar(NT start, Map<NT, Set<P>> prodMap, Set<T> termsUsed,
                                Map<T, CheckedFunction2<L, D, R>> termResGens) {
        super(start, prodMap, termsUsed);
        terminalResGenerators = termResGens;
    }

    /**
     * Given a lexeme of type <b>L</b> and data of type <b>D</b> attempt
     * to build a result of type <b>R</b>. (No checks)
     *
     * @param lexeme The lexeme.
     * @param data The data.
     * @return The generated result.
     * @throws Throwable When there is an error building the result.
     */
    public R buildTerminalResultUnchecked(L lexeme, D data) throws Throwable {
        return terminalResGenerators.get(data.getTokenType()).get().apply(lexeme, data);
    }

    /**
     * Given a lexeme of type <b>L</b> and data of type <b>D</b> attempt
     * to build a result of type <b>R</b>.
     *
     * @param lexeme The lexeme.
     * @param data The data.
     * @return The generated result.
     * @throws Throwable When there is an error building the result.
     */
    public R buildTerminalResult(L lexeme, D data) throws Throwable {
        Objects.requireNonNull(lexeme);
        Objects.requireNonNull(data);

        T terminal = data.getTokenType();

        if (!getTerminalsUsed().contains(terminal)) {
            throw new IllegalArgumentException("Given terminal not used in this grammar.");
        }

        return terminalResGenerators.get(terminal).get().apply(lexeme, data);
    }

    /**
     * Given a lexeme of type <b>L</b> and data of type <b>D</b> attempt to
     * build a result of type <b>R</b>. If there is an error, return a <b>Failure</b>.
     * Otherwise return a <b>Success</b> containing the constructed result.
     *
     * @param lexeme The lexeme.
     * @param data The data.
     * @return A <b>Try</b> which may contain the built result.
     */
    public Try<R> tryBuildTerminalResult(L lexeme, D data) {
        return Try.of(() -> buildTerminalResult(lexeme, data));
    }

    @Override
    public SemanticCFGrammar<NT, T, P, L, D, R> withProduction(P p) {
        // Same as CFGrammar, except all terminals used by this production must
        // have entries in the terminal result generators map.

        Objects.requireNonNull(p);

        // Create new Production map.
        Map<NT, Set<P>> newProdMap = newProductionMap(p);

        Seq<T> rightSymbols = p.getRule().filter(Either::isRight).map(Either::get);

        // All terminals in P must be keys of the generator map.
        Seq<T> notMapped = rightSymbols.filter(t -> !terminalResGenerators.containsKey(t));

        if (!notMapped.isEmpty()) {
            throw new IllegalArgumentException("Given rule has terminals without result generators.");
        }

        return new SemanticCFGrammar<>(getStartSymbol(), newProdMap,
                getTerminalsUsed().addAll(rightSymbols), terminalResGenerators);
    }
}

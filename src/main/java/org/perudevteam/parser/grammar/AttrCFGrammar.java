package org.perudevteam.parser.grammar;

import io.vavr.Function2;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.collection.Set;
import io.vavr.control.Either;
import org.perudevteam.misc.Typed;

import java.util.Objects;


/**
 * Attributed Grammar built for producing some result of type R, from some sequence
 * of typed tokens.
 *
 * @param <NT> Non terminal enum type of the grammar.
 * @param <T> terminal Enum type of the grammar.
 * @param <P> Production Type. (Must extend Attribute Production)
 * @param <L> The lexeme type of the tokens.
 * @param <D> The Data type of the tokens.
 * @param <R> Result Type.
 */
public class AttrCFGrammar<NT extends Enum<NT>, T extends Enum<T>,
        P extends AttrProduction<NT, T, R>, L, D extends Typed<T>, R> extends CFGrammar<NT, T, P> {

    private Map<? super T, Function2<L, D, R>> terminalResGenerators;

    public AttrCFGrammar(NT start, Map<? super T, ? extends Function2<L, D, ? extends R>> termResGens, Seq<P> prods) {
        super(start, prods);

        // None of the generators can be null.
        Objects.requireNonNull(termResGens);
        termResGens.values().forEach(Objects::requireNonNull);

        for (T terminal: getTerminalsUsed()) {
            if (!termResGens.containsKey(terminal)) {
                throw new IllegalArgumentException("All terminal types need result generators.");
            }
        }

        terminalResGenerators = Map.narrow(termResGens.mapValues(Function2::narrow));
    }

    // Direct Constructor with no checks... only used by methods of this class.
    protected AttrCFGrammar(NT start, Map<NT, Set<P>> prodMap, Set<T> termsUsed,
                            Map<? super T, Function2<L, D, R>> termResGens) {
        super(start, prodMap, termsUsed);
        terminalResGenerators = termResGens;
    }

    public R buildTerminalResult(L lexeme, D data) {
        T terminal = data.getType();

        if (!getTerminalsUsed().contains(terminal)) {
            throw new IllegalArgumentException("Given terminal not used in this grammar.");
        }

        return terminalResGenerators.get(terminal).get().apply(lexeme, data);
    }

    @Override
    public AttrCFGrammar<NT, T, P, L, D, R> withProduction(P p) {
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

        return new AttrCFGrammar<>(getStartSymbol(), newProdMap,
                getTerminalsUsed().addAll(rightSymbols), terminalResGenerators);
    }
}

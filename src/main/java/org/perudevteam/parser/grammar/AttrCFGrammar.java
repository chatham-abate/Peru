package org.perudevteam.parser.grammar;

import io.vavr.Function2;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import org.perudevteam.misc.Typed;

import java.util.Objects;


/**
 * Attributed Grammar built for producing some result of type R, from some sequence
 * of typed tokens.
 *
 * @param <NT> Non terminal enum type of the grammar.
 * @param <T> terminal Enum type of the grammar.
 * @param <P> Production Type.
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
}

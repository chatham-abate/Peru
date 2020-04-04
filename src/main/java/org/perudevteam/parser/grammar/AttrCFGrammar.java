package org.perudevteam.parser.grammar;

import io.vavr.Function1;
import io.vavr.collection.HashMap;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.collection.Set;
import io.vavr.control.Either;
import org.perudevteam.dynamic.Dynamic;

import java.util.HashSet;
import java.util.Objects;

/**
 * @param <NT> Non Terminal Type.
 * @param <T> Terminal Type.
 * @param <P> Production Type.
 * @param <TV> Terminal Value Type.
 */
public class AttrCFGrammar<NT extends Enum<NT>, T extends Enum<T>, P extends AttrProduction<NT, T>, TV>
        extends CFGrammar<NT, T, P> {

    private Map<T, Function1<TV, Function1<? super Dynamic, ? extends Dynamic>>> terminalASTBuilders;

    public AttrCFGrammar(NT start,
                         Map<T, ? extends Function1<TV,
                                 ? extends Function1<? super Dynamic, ? extends Dynamic>>> termASTBs,
                         Seq<P> productions) {
        super(start, productions);

        Objects.requireNonNull(termASTBs);

        for (T terminal: getTerminalsUsed()) {
            if (!termASTBs.containsKey(terminal)) {
                throw new IllegalArgumentException("Terminal AST Builder Map must contain an entry for every terminal" +
                        "used in the grammar.");
            } else {
                // No AST generator functions can be null them selves.
                Objects.requireNonNull(termASTBs.get(terminal).get());
            }
        }

        terminalASTBuilders = termASTBs.mapValues(Function1::narrow);
    }

    protected AttrCFGrammar(NT start, Map<NT, Set<P>> prodMap, Set<T> termsUsed,
                            Map<T, Function1<TV, Function1<? super Dynamic, ? extends Dynamic>>> termASTBs) {
        super(start, prodMap, termsUsed);
        terminalASTBuilders = termASTBs;
    }

    public Function1<Dynamic, Dynamic> buildTerminalAST(T terminal, TV terminalValue) {
        if (!getTerminalsUsed().contains(terminal)) {
            throw new IllegalArgumentException("Given terminal is not used in this grammar.");
        }

        return Function1.narrow(terminalASTBuilders.get(terminal).get().apply(terminalValue));
    }

    @Override
    public AttrCFGrammar<NT, T, P, TV> withProduction(P p) {
        Objects.requireNonNull(p);

        // If any terminals are new, they need to have AST generators.
        Seq<T> newTerminals = p.getRule().filter(Either::isRight).map(Either::get);
        for (T t: newTerminals) {
            if (!terminalASTBuilders.containsKey(t)) {
                throw new IllegalArgumentException("Given rule has terminals without AST generators.");
            }
        }

        return new AttrCFGrammar<>(getStartSymbol(), newProductionMap(p), getTerminalsUsed().addAll(newTerminals),
                terminalASTBuilders);
    }
}

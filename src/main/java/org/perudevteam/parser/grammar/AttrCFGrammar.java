package org.perudevteam.parser.grammar;

import io.vavr.Function1;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import org.perudevteam.dynamic.Dynamic;

import java.util.Objects;

/**
 * @param <NT> Non Terminal Type.
 * @param <T> Terminal Type.
 * @param <P> Production Type.
 * @param <TV> Terminal Value Type.
 */
public class AttrCFGrammar<NT extends Enum<NT>, T extends Enum<T>, P extends AttrProduction<NT, T>, TV>
        extends CFGrammar<NT, T, P> {

    private Map<T, Function1<TV, Function1<? super Dynamic, Dynamic>>> terminalASTBuilders;

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
            }
        }

    }

    // Change with Production code.
}

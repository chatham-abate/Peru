package org.perudevteam.parser.lrone;

import io.vavr.Function1;
import io.vavr.Tuple2;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.collection.Set;
import io.vavr.control.Either;
import org.perudevteam.dynamic.Dynamic;
import org.perudevteam.misc.Typed;
import org.perudevteam.parser.ASTParser;
import org.perudevteam.parser.grammar.AttrCFGrammar;
import org.perudevteam.parser.grammar.AttrProduction;

import static io.vavr.control.Either.*;

import java.util.Objects;

public class LROneASTParser<NT extends Enum<NT>, T extends Enum<T>, L, D extends Typed<T>>
        implements ASTParser<L, D> {

    public LROneASTParser(AttrCFGrammar<NT, T, ? extends AttrProduction<NT, T>, Tuple2<L, D>> grammar) {
        Objects.requireNonNull(grammar);

        // Put grammar details into local fields.
        NT startSymbol = grammar.getStartSymbol();
        Map<NT, Set<AttrProduction<NT, T>>> prodMap = grammar.getProductionMap().mapValues(Set::narrow);
        Set<T> terminals = grammar.getTerminalsUsed();
        Set<NT> nonTerminals = grammar.getNonTerminalsUsed();

        // Start symbol can not be on the rhs of any rules.
        Either<NT, T> startSymbolEither = left(startSymbol);
        for (NT nt: nonTerminals) {
            for (AttrProduction<NT, T> ap: prodMap.get(nt).get()) {
                if (ap.getRule().contains(startSymbolEither)) {
                    throw new IllegalArgumentException("Given start symbol appears on rhs of a production.");
                }
            }
        }

        // can we build table using the grammar... then simply drop the grammar....

        // Build tables here, perform
    }

    @Override
    public Function1<Dynamic, Dynamic> buildASTUnsafe(Seq<Tuple2<L, D>> tokens) throws Throwable {
        return null;
    }
}

package org.perudevteam.parser;

import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import org.perudevteam.misc.Typed;

import java.util.Objects;

/**
 * Generic Parser interface.
 *
 * @param <T> Type Enum of tokens passed in.
 * @param <L> Lexeme type of tokens passed in.
 * @param <D> Data type of tokens passed in.
 * @param <R> What is parsed by the parser... (Could be an AST, or a parse tree... etc)
 */
@FunctionalInterface
public interface Parser<T extends Enum<T>, L, D extends Typed<T>, R> {
    R parseUnchecked(Seq<Tuple2<L, D>> tokens) throws Throwable;

    default R parse(Seq<? extends Tuple2<L, D>> tokens) throws Throwable {
        Objects.requireNonNull(tokens);
        Seq<Tuple2<L, D>> narrowTokens = Seq.narrow(tokens);
        narrowTokens.forEach(Objects::requireNonNull);
        return parseUnchecked(narrowTokens);
    }
}

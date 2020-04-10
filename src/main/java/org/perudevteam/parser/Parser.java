package org.perudevteam.parser;

import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import io.vavr.control.Try;

import java.util.Objects;

/**
 * Generic Parser interface.
 *
 * A parser reads in a sequence of lexeme data pairs and attempts to build some result R.
 * Here the given pairs are required to be "tokenized" ... meaning they must have some token type.
 *
 * A parser obviously can fail while reading a sequence of tokens.
 * Thus, a Try is returned.
 *
 * @param <T> Type Enum of tokens passed in.
 * @param <L> Lexeme type of tokens passed in.
 * @param <D> Data type of tokens passed in.
 * @param <R> What is parsed by the parser... (Could be an AST, or a parse tree... etc)
 */
@FunctionalInterface
public interface Parser<T extends Enum<T>, L, D extends Tokenized<T>, R> {
    Try<R> parseUnchecked(Seq<Tuple2<L, D>> tokens);

    default Try<R> parse(Seq<? extends Tuple2<L, D>> tokens) {
        Objects.requireNonNull(tokens);
        Seq<Tuple2<L, D>> narrowTokens = Seq.narrow(tokens);
        narrowTokens.forEach(Objects::requireNonNull);

        return parseUnchecked(narrowTokens);
    }
}

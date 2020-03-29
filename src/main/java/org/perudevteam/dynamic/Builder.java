package org.perudevteam.dynamic;

import io.vavr.Tuple3;
import io.vavr.collection.Seq;
import io.vavr.control.Option;
import io.vavr.control.Try;

/**
 * A Builder is a function interface which attempts to build some form of output from
 * a sequence of inputs.
 *
 * The builder takes a sequence of inputs and a Dynamic value representing the context.
 * Think lexing characters. The input would be a sequence of characters to be lexed.
 * The context might be a Dynamic containing the last line number used.
 * The builder would then build the first valid token it comes across.
 * It returns the token, the rest of the characters yet to be used, and the new context
 * with some new line number (Maybe a new line character had been read).
 */
@FunctionalInterface
public interface Builder<I, O> {
    Tuple3<O, Seq<I>, Dynamic> build(Seq<I> input, Dynamic context) throws Throwable;

    default Try<Tuple3<O, Seq<I>, Dynamic>> tryBuild(Seq<I> input, Dynamic context) {
        return Try.of(() -> build(input, context));
    }

    default Option<Tuple3<O, Seq<I>, Dynamic>> optionBuild(Seq<I> input, Dynamic context) {
        return tryBuild(input, context).toOption();
    }
}

package org.perudevteam.misc;

import io.vavr.Tuple3;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import io.vavr.control.Try;

import java.util.Objects;

/**
 * A Builder takes in a sequence of Inputs of type I, as well as a context of type C.
 * It constructs some output from the Seq of inputs, it returns the output, the rest of the sequence
 * which is yet to be used, and a new Context.
 *
 * Nothing should ever be constructable from an empty input sequence.
 */
@FunctionalInterface
public interface Builder<I, C, O> {
    Tuple3<O, C, Seq<I>> buildUnchecked(Seq<I> input, C context);

    default Tuple3<O, C, Seq<I>> build(Seq<I> input, C context) {
        Objects.requireNonNull(input);
        Objects.requireNonNull(context);

        if (input.isEmpty()) {
            throw new IllegalArgumentException("Cannot construct anything from empty input.");
        }

        return buildUnchecked(input, context);
    }

    default Stream<O> buildStream(Seq<I> input, C context) {
        Objects.requireNonNull(input);

        if (input.isEmpty()) {
            return Stream.empty();
        }

        Tuple3<O, C, Seq<I>> output = build(input, context);

        O construct = output._1;
        C newContext = output._2;
        Seq<I> rest = output._3;

        return Stream.cons(construct, () -> buildStream(rest, newContext));
    }
}

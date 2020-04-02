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
 */
@FunctionalInterface
public interface Builder<I, C, O> {
    Tuple3<O, C, Seq<I>> build(Seq<I> input, C context) throws Throwable;

    default Tuple3<O, C, Seq<I>> buildWithNullChecks(Seq<I> input, C context) throws Throwable {
        Objects.requireNonNull(input);
        Objects.requireNonNull(context);

        return build(input, context);
    }

    default Stream<Try<O>> buildStream(Seq<I> input, C context) {
        try {
            Tuple3<O, C, Seq<I>> output = buildWithNullChecks(input, context);

            O construct = output._1;
            C newContext = output._2;
            Seq<I> rest = output._3;

            return rest.isEmpty()
                    ? Stream.of(Try.success(construct))
                    : Stream.cons(Try.success(construct), () -> buildStream(rest, newContext));
        } catch (Throwable t) {
            return Stream.of(Try.failure(t));
        }
    }

    default Stream<O> buildStreamUnchecked(Seq<I> input, C context) {
        return buildStream(input, context).map(Try::get);
    }
}

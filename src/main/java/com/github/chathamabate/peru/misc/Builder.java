package com.github.chathamabate.peru.misc;

import io.vavr.Tuple3;
import io.vavr.collection.Seq;
import io.vavr.collection.Stream;
import io.vavr.control.Try;

import java.util.Objects;

/**
 * A <b>Builder</b> is a function which takes in a sequence of inputs of type <b>I</b>
 * and a context of type <b>C</b>. Using the inputs and the context, the function builds some output
 * of type <b>O</b> then returns a <b>Tuple3</b> containing the output, a new context, and
 * a sequence of the inputs which were not used.
 *
 *
 * @param <I> The input type.
 * @param <C> They context type.
 * @param <O> The output type.
 */
@FunctionalInterface
public interface Builder<I, C, O> {

    /**
     * The builder function with no checks on the given input and context.
     *
     * @param input The input sequence.
     * @param context The input context.
     * @return A <b>Tuple3</b> containing the output, the new context, and a sequence of
     * the inputs not used.
     */
    Tuple3<O, C, Seq<I>> buildUnchecked(Seq<? extends I> input, C context);

    /**
     * The builder function with null checks on the input sequence and context.
     * If the input sequence is empty, an error is thrown. <b>Builder</b> assumes nothing can be built
     * from an empty input sequence.
     *
     * @param input The input sequence.
     * @param context The input context.
     * @return A <b>Tuple3</b> containing the output, the new context, and a sequence of
     * the inputs not used.
     */
    default Tuple3<O, C, Seq<I>> build(Seq<? extends I> input, C context) {
        Objects.requireNonNull(input);
        input.forEach(Objects::requireNonNull);
        Objects.requireNonNull(context);

        if (input.isEmpty()) {
            throw new IllegalArgumentException("Cannot construct anything from empty input.");
        }

        return buildUnchecked(input, context);
    }

    /**
     * This function lazily builds outputs repeatedly until the given input sequence is empty.
     * Unlike the normal build function, no error is thrown if an empty input sequence is given.
     * An empty <b>Stream</b> is returned in that case.
     *
     * @param input The input sequence.
     * @param context The initial context.
     * @return A <b>Stream</b> of the outputs created.
     */
    default Stream<O> buildStream(Seq<? extends I> input, C context) {
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

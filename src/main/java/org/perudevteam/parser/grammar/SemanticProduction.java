package org.perudevteam.parser.grammar;

import io.vavr.CheckedFunction1;
import io.vavr.Function1;
import io.vavr.collection.Seq;
import io.vavr.control.Either;
import io.vavr.control.Try;

import java.util.Objects;

/**
 * This class represents a production which can be used to build some result.
 * Results are built in a bottom up manor.
 *
 * @param <NT> The non terminal enum type of the production.
 * @param <T> the terminal enum type of the production.
 * @param <R> The result which can be generated using this production.
 */
public abstract class SemanticProduction<NT extends Enum<NT>, T extends Enum<T>, R> extends Production<NT, T> {

    /**
     * Static Helper function for building Semantic Productions.
     */
    public static <NT extends Enum<NT>, T extends Enum<T>, R> SemanticProduction<NT, T, R>
    semanticProduction(NT s, Seq<? extends Either<NT, T>> r,
                       CheckedFunction1<? super Seq<R>, ? extends R> resultBuilder) {
        return new SemanticProduction<NT, T, R>(s, r) {
            @Override
            protected R buildResultUnchecked(Seq<R> children) throws Throwable {
                return resultBuilder.apply(children);
            }
        };
    }


    public SemanticProduction(NT s, Seq<? extends Either<NT, T>> r) {
        super(s, r);
    }

    /**
     * This production holds a rule with a certain number of symbols.
     * Each one of those symbols should be able to produce some result R.
     * These ordered results are then passed into this function to create
     * the result for this production.
     */
    protected abstract R buildResultUnchecked(Seq<R> children) throws Throwable;

    public R buildResult(Seq<? extends R> children) throws Throwable {
        Objects.requireNonNull(children);
        children.forEach(Objects::requireNonNull);

        if (children.length() != getRule().length()) {
            throw new IllegalArgumentException("This rule requires " + getRule().length() + " tokens.");
        }

        Seq<R> childrenNarrow = Seq.narrow(children);

        return buildResultUnchecked(childrenNarrow);
    }

    public Try<R> tryBuildResult(Seq<? extends R> children) {
        return Try.of(() -> buildResult(children));
    }
}

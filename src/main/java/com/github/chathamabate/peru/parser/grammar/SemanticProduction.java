package com.github.chathamabate.peru.parser.grammar;

import io.vavr.CheckedFunction1;
import io.vavr.collection.Seq;
import io.vavr.control.Either;
import io.vavr.control.Try;

import java.util.Objects;

/**
 * This class represents a production which can build some result of type <b>R</b> given the
 * results corresponding to each of its rule's symbols.
 *
 * @param <NT> The non-terminal <b>Enum</b> type.
 * @param <T> The terminal <b>Enum</b> type.
 * @param <R> The result type built by the production.
 */
public abstract class SemanticProduction<NT extends Enum<NT>, T extends Enum<T>, R> extends Production<NT, T> {

    /**
     * Static helper for building a <b>SemanticProduction</b> without using an
     * abstract inner class.
     *
     * @param s The start symbol of the production.
     * @param r The rule of the production.
     * @param resultBuilder The result building function of the production.
     * @param <NT> The non-terminal <b>Enum</b> type.
     * @param <T> The terminal <b>Enum</b> type.
     * @param <R> The result type built by the production.
     * @return The constructed <b>SemanticProduction</b>.
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

    /**
     * Construct a <b>SemanticProduction</b>.
     *
     * @param s The source symbol of the production.
     * @param r The rule of the production.
     */
    public SemanticProduction(NT s, Seq<? extends Either<NT, T>> r) {
        super(s, r);
    }

    /**
     * This function is what defines a <b>SemanticProduction</b>. It
     * translates the results corresponding to each symbol of its rule into
     * one new result. It is possible for this translation to throw an error.
     *
     * @param children A result for each symbol in this production's rule.
     * @return A new result produced by this production.
     * @throws Throwable When there is an error building the result.
     */
    protected abstract R buildResultUnchecked(Seq<R> children) throws Throwable;

    /**
     * Same as {@link SemanticProduction#buildResultUnchecked(Seq)} except checks
     * are preformed on the results passed into the production before any
     * new result is attempted to be created.
     *
     * @param children A result for each symbol in this production's rule.
     * @return A new result produced by this production.
     * @throws Throwable When there is an error building the result or the given inputs
     * are malformed.
     */
    public R buildResult(Seq<? extends R> children) throws Throwable {
        Objects.requireNonNull(children);
        children.forEach(Objects::requireNonNull);

        if (children.length() != getRule().length()) {
            throw new IllegalArgumentException("This rule requires " + getRule().length() + " tokens.");
        }

        Seq<R> childrenNarrow = Seq.narrow(children);

        return buildResultUnchecked(childrenNarrow);
    }

    /**
     * Same as {@link SemanticProduction#buildResult(Seq)} just wrapped in a <b>Try</b>.
     *
     * @param children A result for each symbol in this production's rule.
     * @return A <b>Try</b> which may contain a produced result.
     */
    public Try<R> tryBuildResult(Seq<? extends R> children) {
        return Try.of(() -> buildResult(children));
    }
}

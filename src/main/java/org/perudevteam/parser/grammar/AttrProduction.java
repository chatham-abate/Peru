package org.perudevteam.parser.grammar;

import io.vavr.Function1;
import io.vavr.collection.Seq;
import io.vavr.control.Either;

import java.util.Objects;

/**
 * This class represents a production which can be used to build some result.
 * Results are built in a bottom up manor.
 *
 * @param <NT> The non terminal enum type of the production.
 * @param <T> the terminal enum type of the production.
 * @param <R> The result which can be generated using this production.
 */
public abstract class AttrProduction<NT extends Enum<NT>, T extends Enum<T>, R> extends Production<NT, T> {

    public AttrProduction(NT s, Seq<? extends Either<NT, T>> r) {
        super(s, r);
    }

    /**
     * This production holds a rule with a certain number of symbols.
     * Each one of those symbols should be able to produce some result R.
     * These ordered results are then passed into this function to create
     * the result for this production.
     */
    protected abstract R buildResultUnsafe(Seq<? extends R> children);

    public R buildResult(Seq<? extends R> children) {
        Objects.requireNonNull(children);
        children.forEach(Objects::requireNonNull);

        if (children.length() != getRule().length()) {
            throw new IllegalArgumentException("This rule requires " + getRule().length() + " tokens.");
        }

        return buildResultUnsafe(children);
    }
}

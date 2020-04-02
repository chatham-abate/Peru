package org.perudevteam.parser.production;


import io.vavr.collection.Array;
import io.vavr.collection.Seq;
import io.vavr.control.Either;

import java.util.Objects;

// Functional Production Representation. (Uses Eithers instead of plain Enums).
public class Production<NT extends Enum<NT>, T extends Enum<T>> {
    private NT source;

    // Each member of the rule sequence will either be a terminal, or
    // a non-terminal.
    private Seq<Either<NT, T>> rule;

    public Production(NT s, Seq<Either<NT, T>> r) {
        Objects.requireNonNull(s);
        Objects.requireNonNull(r);

        source = s;
        rule = r;
    }

    public NT getSource() {
        return source;
    }

    public Production<NT, T> withSource(NT s) {
        return new Production<>(s, rule);
    }

    public Seq<Either<NT, T>> getRule() {
        return rule;
    }

    public <P extends Enum<P>> Production<NT, P> withRule(Seq<Either<NT, P>> r) {
        return new Production<>(source, r);
    }

    @Override
    public String toString() {
        return source.name() + " ->" + rule.map(
                e -> e.isLeft() ? e.getLeft().name() : e.get().name()
        );
    }
}

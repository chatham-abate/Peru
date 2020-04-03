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

        // Make sure no either's hold a null value.
        r.forEach(e -> {
            Objects.requireNonNull(e);

            if (e.isLeft()) {
                Objects.requireNonNull(e.getLeft());
            } else {
                Objects.requireNonNull(e.get());
            }
        });

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
        // Mutation for string builder only here.
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(source.name());
        strBuilder.append(" -> ");

        for (int i = 0; i < rule.length(); i++) {
            Either<NT, T> sym = rule.get(i);
            strBuilder.append(sym.isRight()
                    ? sym.get() : sym.getLeft());

            if (i < rule.length() - 1) {
                strBuilder.append(" ");
            }
        }

        return strBuilder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Production<?, ?> that = (Production<?, ?>) o;
        return Objects.equals(source, that.source) &&
                Objects.equals(rule, that.rule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, rule);
    }
}

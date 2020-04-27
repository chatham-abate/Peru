package org.perudevteam.parser.grammar;

import io.vavr.collection.Seq;
import io.vavr.control.Either;

import java.util.Objects;

/**
 * A class representing a production of a context free grammar.
 * <br>
 * A <b>Production</b> is simply some non-terminal symbol which can be substituted for
 * a sequence of other non-terminal and terminal symbols.
 * <br>
 * Here non-terminals and terminal <b>Enum</b>s are able to be stored in one sequence of
 * <b>Either</b>s representing the rule of this <b>Production</b>.
 * A non-terminal is represented by a left <b>Either</b> and a
 * terminal is represented by a right <b>Either</b>.
 *
 * @param <NT> The <b>Enum</b> non-terminal type.
 * @param <T> The <b>Enum</b> terminal type.
 */
public class Production<NT extends Enum<NT>, T extends Enum<T>> {

    /**
     * The non-terminal source of the <b>Production</b>.
     */
    private final NT source;

    /**
     * The sequence of non-terminals and terminals representing the rule of this
     * <b>Production</b>.
     */
    private final Seq<Either<NT, T>> rule;

    /**
     * Create a <b>Production</b> given a non-terminal and its rule.
     *
     * @param s The non-terminal source symbol.
     * @param r Its corresponding rule.
     */
    public Production(NT s, Seq<? extends Either<NT, T>> r) {
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
        rule = Seq.narrow(r);
    }

    /**
     * Get the source symbol <b>Enum</b> of this <b>Production</b>.
     *
     * @return The source symbol.
     */
    public NT getSource() {
        return source;
    }

    /**
     * Update the source non-terminal symbol of this <b>Production</b>.
     *
     * @param s The new source symbol.
     * @return The new <b>Production</b>.
     */
    public Production<NT, T> withSource(NT s) {
        return new Production<>(s, rule);
    }

    /**
     * Get this <b>Production</b>'s rule.
     *
     * @return The sequence of non-terminals and terminals.
     */
    public Seq<Either<NT, T>> getRule() {
        return rule;
    }

    /**
     * Update the rule of this <b>Production</b>.
     *
     * @param r The new rule.
     * @param <TP> The terminal type used in the new rule.
     * @return The new <b>Production</b>.
     */
    public <TP extends Enum<TP>> Production<NT, TP> withRule(Seq<Either<NT, TP>> r) {
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

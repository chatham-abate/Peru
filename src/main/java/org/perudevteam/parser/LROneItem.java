package org.perudevteam.parser;

import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.collection.Set;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.perudevteam.parser.production.Production;

import java.util.Objects;

public class LROneItem <NT extends Enum<NT>, T extends Enum<T>, P extends Production<NT, T>> {

    /*
     * LR(1) Item static Helpers.
     */

    public <NT extends Enum<NT>, T extends Enum<T>, P extends Production<NT, T>> Set<LROneItem<NT, T, P>>
    closure(CFGrammar<NT, T, P> g, Set<LROneItem<NT, T, P>> set0) {
        List<LROneItem<NT, T, P>> workStack = List.ofAll(set0);
        Set<LROneItem<NT, T, P>> closure = HashSet.empty();

        while (!workStack.isEmpty()) {
            // Pop an item off the work stack.
            LROneItem<NT, T, P> item = workStack.peek();
            workStack = workStack.tail();

            closure = closure.add(item);    // Add item to closure.

            if (item.getCursor() == 0 && !item.isEmpty() && item.getProduction().getRule().get(0).isLeft()) {
                // We have found a production in the form A -> *Bd  where B is a NT.


            }
        }

        return null;
    }

    /*
     * LR(1) Item Class.
     */

    private int cursor;
    private P production;
    private Option<T> suffix;

    public LROneItem(int c, P p, T s) {
        this(c, p, Option.some(s));
    }

    public LROneItem(int c, P p) {
        this(c, p, Option.none());
    }

    private LROneItem(int c, P p, Option<T> os) {
        Objects.requireNonNull(p);
        Objects.requireNonNull(os);

        if (c > p.getRule().length() || c < 0) {
            throw new IllegalArgumentException("Cursor must be in range [0, |rule|].");
        }

        cursor = c;
        production = p;
        suffix = os;
    }

    public int getCursor() {
        return cursor;
    }

    public P getProduction() {
        return production;
    }

    public boolean hasSuffix() {
        return !suffix.isEmpty();
    }

    public T getSuffix() {
        if (suffix.isEmpty()) {
            throw new NullPointerException("This LROneItem has no suffix.");
        }

        return suffix.get();
    }

    /**
     * Whether or not the LR(1) item holds the empty rule.
     */
    public boolean isEmpty() {
        return production.getRule().isEmpty();
    }

    public LROneItem<NT, T, P> withoutSuffix() {
        return suffix.isEmpty() ? this : new LROneItem<>(cursor, production);
    }

    public LROneItem<NT, T, P> withSuffix(T s) {
        return new LROneItem<>(cursor, production, s);
    }

    public LROneItem<NT, T, P> shiftCursor() {
        return new LROneItem<>(cursor + 1, production, suffix);
    }

    public LROneItem<NT, T, P> withProduction(P p) {
        return new LROneItem<>(cursor, p, suffix);
    }

    /*
     * Overridden methods.
     */

    public String toString() {
        StringBuilder strBuilder = new StringBuilder();

        strBuilder.append("[")
                .append(production.getSource().name())
                .append(" -> ");

        Seq<Either<NT, T>> rule = production.getRule();

        for (int i = 0; i <= rule.length(); i++) {
            if (i == cursor) {
                strBuilder.append("●");
            } else {
                int symInd = i < cursor ? i : i - 1;
                Either<NT, T> sym = rule.get(symInd);

                strBuilder.append(sym.isLeft()
                        ? sym.getLeft().name()
                        : sym.get().name());

            }

            if (i < rule.length()) {
                strBuilder.append(" ");
            }
        }

        strBuilder.append(", ")
                .append(suffix.isEmpty() ? "$" : suffix.get().name())
                .append("]");

        return strBuilder.toString();
    }

    /*
     * Generated HashCode and Equals.
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LROneItem<?, ?, ?> lrOneItem = (LROneItem<?, ?, ?>) o;
        return cursor == lrOneItem.cursor &&
                production.equals(lrOneItem.production) &&
                suffix.equals(lrOneItem.suffix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cursor, production, suffix);
    }
}

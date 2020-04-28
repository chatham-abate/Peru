package org.perudevteam.parser.lrone;

import io.vavr.Tuple2;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Seq;
import io.vavr.collection.Set;
import io.vavr.control.Either;
import io.vavr.control.Option;
import org.perudevteam.parser.grammar.CFGrammar;
import org.perudevteam.parser.grammar.Production;

import java.util.Objects;

/**
 * This class represents an LR(1) Item to be used by and LR(1) parser generator to generate an
 * <i>ACTION</i> and <i>GOTO</i> table.
 *
 * @param <NT> The non-terminal <b>Enum</b> type of the rule held inside the <b>LROneItem</b>.
 * @param <T> The terminal <b>Enum</b> type of the rule held inside the <b>LROneItem</b>.
 * @param <P> The type of the <b>Production</b> held inside the <b>LROneItem</b>.
 */
public class LROneItem <NT extends Enum<NT>, T extends Enum<T>, P extends Production<NT, T>> {

    /*
     * LR(1) Item static Helpers.
     */

    public static <NT extends Enum<NT>, T extends Enum<T>, P extends Production<NT, T>> Set<LROneItem<NT, T, P>>
    closureSet(CFGrammar<NT, T, P> g, FirstSets<NT, T> firstSets, Set<LROneItem<NT, T, P>> set0) {
        // Perform Null Checks.
        Objects.requireNonNull(g);
        Objects.requireNonNull(firstSets);
        Objects.requireNonNull(set0);
        set0.forEach(Objects::requireNonNull);

        List<LROneItem<NT, T, P>> workStack = List.ofAll(set0);
        Set<LROneItem<NT, T, P>> closure = HashSet.empty();

        while (!workStack.isEmpty()) {
            // Pop an item off the work stack.
            LROneItem<NT, T, P> item = workStack.peek();
            workStack = workStack.tail();

            // Only process items yet to be processed.
            if (!closure.contains(item)) {
                closure = closure.add(item);    // Add item to closure.

                Seq<Either<NT, T>> rule = item.getProduction().getRule();
                int cursor = item.getCursor();

                if (cursor < rule.length() && rule.get(cursor).isLeft()) {
                    // Generate ending of the rule.
                    Seq<Either<NT, T>> ruleEnding = cursor == rule.length() - 1
                            ? List.empty()
                            : rule.subSequence(cursor + 1);

                    if (item.hasSuffix()) {
                        ruleEnding = ruleEnding.append(item.getSuffixAsEither());
                    }

                    // Calculate endings first tuple.
                    Tuple2<Boolean, Set<T>> firstSetTuple = firstSets.getFirstSet(ruleEnding);

                    NT source = rule.get(cursor).getLeft();
                    Set<P> productions = g.getProductions(source);

                    for (T t: firstSetTuple._2) {
                        for (P production: productions) {
                            workStack = workStack.prepend(new LROneItem<>(0, production, t));
                        }
                    }

                    // If the given rule may derive to completely empty.
                    if (firstSetTuple._1) {
                        for (P production: productions) {
                            workStack = workStack.prepend(new LROneItem<>(0, production));
                        }
                    }
                }
            }
        }

        return closure;
    }

    /*
     * LR(1) Item Class.
     */

    private final int cursor;
    private final P production;
    private final Option<T> suffix;

    public LROneItem(int c, P p, T s) {
        this(c, p, Option.some(s));
    }

    public LROneItem(int c, P p) {
        this(c, p, Option.none());
    }

    private LROneItem(int c, P p, Option<T> os) {
        Objects.requireNonNull(p);
        Objects.requireNonNull(os);
        if (!os.isEmpty()) Objects.requireNonNull(os.get());

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

    private void validateSuffix() {
        if (suffix.isEmpty()) {
            throw new NullPointerException("This LROneItem has no suffix.");
        }
    }

    public T getSuffix() {
        validateSuffix();
        return suffix.get();
    }

    public Either<NT, T> getSuffixAsEither() {
        validateSuffix();
        return Either.right(suffix.get());
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


    @Override
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

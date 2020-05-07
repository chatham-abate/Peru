package com.github.chathamabate.peru.parser.lrone;

import com.github.chathamabate.peru.parser.grammar.CFGrammar;
import com.github.chathamabate.peru.parser.grammar.Production;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import io.vavr.control.Either;

import java.util.Objects;

/**
 * This class holds a <b>FIRST</b> set for each of the non-terminal <b>Enum</b>s
 * of a <b>CFGrammar</b>.
 * @see <a href="https://www3.cs.stonybrook.edu/~warren/xsbbook/node29.html"><b>FIRST</b> sets</a>
 * @param <NT> The non-terminal <b>Enum</b> type of the <b>CFGrammar</b> given.
 * @param <T> The terminal <b>Enum</b> type of the <b>CFGrammar</b> given.
 */
public class FirstSets<NT extends Enum<NT>, T extends Enum<T>> {

    /**
     * Generate the firsts set of an entire rule.
     *
     * @param rule The rule itself.
     * @param nonTerminalFirsts The <b>FIRST</b> set for each non-terminal in grammar.
     * @param mayBeEmpty The set of non-terminals in the grammar which may derive to epsilon.
     * @param <NT> The non-terminal <b>Enum</b> type of the grammar.
     * @param <T> The terminal <b>Enum</b> type of the grammar.
     * @return The set of terminal <b>Enum</b>s which may start something derived from this rule, and
     * whether or not the rule can derive to epsilon.
     */
    private static <NT extends Enum<NT>, T extends Enum<T>> Tuple2<Boolean, Set<T>>
    genRuleFirstSet(Seq<? extends Either<NT, T>> rule,
                    Map<NT, ? extends Set<T>> nonTerminalFirsts, Set<NT> mayBeEmpty) {
        if (rule.isEmpty()) {
            return Tuple.of(true, HashSet.empty());
        } else {
            Either<NT, T> firstSymbol = rule.get(0);

            Set<T> ruleFirsts = firstSymbol.isLeft()
                    ? nonTerminalFirsts.get(firstSymbol.getLeft()).get()
                    : HashSet.of(firstSymbol.get());

            int i;
            for (i = 1; i < rule.length() && rule.get(i - 1).isLeft() &&
                    mayBeEmpty.contains(rule.get(i - 1).getLeft()); i++) {
                Either<NT, T> midEither = rule.get(i);

                ruleFirsts = midEither.isLeft()
                        ? ruleFirsts.addAll(nonTerminalFirsts.get(midEither.getLeft()).get())
                        : ruleFirsts.add(midEither.get());
            }

            if (i == rule.length() && rule.get(i - 1).isLeft()
                    && mayBeEmpty.contains(rule.get(i - 1).getLeft())) {
                return Tuple.of(true, ruleFirsts);
            }

            return Tuple.of(false, ruleFirsts);
        }
    }

    /**
     * A map from non-terminal symbols to <b>FIRST</b> sets.
     */
    private final Map<NT, Set<T>> nonTerminalFirsts;

    /**
     * The set of non-terminals symbols in the grammar which can derive to epsilon.
     */
    private final Set<NT> mayBeEmpty;

    /**
     * Construct the <b>FIRST</b> sets of a <b>CFGrammar</b>.
     *
     * @param g the <b>CFGrammar</b>.
     */
    public FirstSets(CFGrammar<NT, T, ? extends Production<NT, T>> g) {
        Objects.requireNonNull(g);  // Grammar cannot be null.

        Map<NT, Set<Production<NT, T>>> productions = g.getProductionMap().mapValues(Set::narrow);
        Set<NT> nonTerminals = productions.keySet();

        Set<NT> mayBeEmptyTemp = HashSet.empty();

        // Fill all Non Terminals with empty Sets here.
        Map<NT, Set<T>> nonTerminalFirstsTemp =
                HashMap.ofEntries(nonTerminals.map(nt -> Tuple.of(nt, HashSet.empty())));

        int oldSize = -1;
        int newSize = 0;

        while (newSize - oldSize > 0) {

            // Iterate over every production p.
            for (NT nonTerminal: nonTerminals) {
                for (Production<NT, T> p: productions.get(nonTerminal).get()) {
                    NT source = p.getSource();
                    Seq<Either<NT, T>> rule = p.getRule();
                    Tuple2<Boolean, Set<T>> ruleFirstsTuple =
                            genRuleFirstSet(rule, nonTerminalFirstsTemp, mayBeEmptyTemp);

                    nonTerminalFirstsTemp = nonTerminalFirstsTemp.put(source,
                            nonTerminalFirstsTemp.get(source).get().addAll(ruleFirstsTuple._2));

                    if (ruleFirstsTuple._1) {
                        mayBeEmptyTemp = mayBeEmptyTemp.add(source);
                    }
                }
            }

            oldSize = newSize;
            newSize = 0;

            for (NT nonTerminal: nonTerminals) {
                newSize += nonTerminalFirstsTemp.get(nonTerminal).get().length();
            }

            newSize += mayBeEmptyTemp.length();
        }

        mayBeEmpty = mayBeEmptyTemp;
        nonTerminalFirsts = nonTerminalFirstsTemp;
    }

    /**
     * Get the <b>FIRST</b> set of every non-terminal in the grammar.
     *
     * @return The <b>FIRST</b> sets.
     */
    public Map<NT, Set<T>> getNonTerminalFirsts() {
        return nonTerminalFirsts;
    }

    /**
     * Throw an error if a non-terminal is not defined in this group of <b>FIRST</b> sets.
     *
     * @param nt The non-terminal <b>Enum</b> to validate.
     */
    private void validateNonTerminal(NT nt) {
        Objects.requireNonNull(nt);
        if (!nonTerminalFirsts.containsKey(nt)) {
            throw new IllegalArgumentException("Given non-terminal is not in this firsts set.");
        }
    }

    /**
     * Get the <b>FIRST</b> set of a non-terminal. Throw an error if the given non-terminal
     * is not defined in this group of <b>FIRST</b> sets.
     *
     * @param nt The non-terminal <b>Enum</b>.
     * @return The <b>FIRST</b> set of the given non-terminal.
     */
    public Tuple2<Boolean, Set<T>> getFirstSet(NT nt) {
        validateNonTerminal(nt);
        return Tuple.of(mayBeEmpty.contains(nt), nonTerminalFirsts.get(nt).get());
    }

    /**
     * Get the <b>FIRST</b> set of an entire rule.
     *
     * @param rule The rule.
     * @return The <b>FIRST</b> set of the rule, and whether or not the rule can
     * derive to epsilon.
     */
    public Tuple2<Boolean, Set<T>> getFirstSet(Seq<Either<NT, T>> rule) {
        Objects.requireNonNull(rule);
        return genRuleFirstSet(rule, nonTerminalFirsts, mayBeEmpty);
    }

    @Override
    public String toString() {
        return nonTerminalFirsts.toString() + "\n" + mayBeEmpty.toString();
    }
}


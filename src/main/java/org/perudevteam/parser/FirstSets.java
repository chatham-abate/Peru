package org.perudevteam.parser;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import io.vavr.control.Either;
import org.perudevteam.parser.production.Production;

import java.util.Objects;

class FirstSets<NT extends Enum<NT>, T extends Enum<T>> {

    /**
     * Generate the firsts set of a specific rule.
     *
     * @param <NT> The non-Terminal type of the rule.
     * @param <T> The terminal type of the rule.
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

    private Map<NT, Set<T>> nonTerminalFirsts;
    private Set<NT> mayBeEmpty;

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

    public Map<NT, Set<T>> getNonTerminalFirsts() {
        return nonTerminalFirsts;
    }

    private void validateNonTerminal(NT nt) {
        Objects.requireNonNull(nt);
        if (!nonTerminalFirsts.containsKey(nt)) {
            throw new IllegalArgumentException("Given non-terminal is not in this firsts set.");
        }
    }


    public Set<T> getFirstSet(NT nt) {
        validateNonTerminal(nt);
        return nonTerminalFirsts.get(nt).get();
    }

    public Tuple2<Boolean, Set<T>> getFirstSet(Seq<Either<NT, T>> rule) {
        Objects.requireNonNull(rule);
        return genRuleFirstSet(rule, nonTerminalFirsts, mayBeEmpty);
    }

    public boolean mayBeEmpty(NT nt) {
        validateNonTerminal(nt);
        return mayBeEmpty.contains(nt);
    }

    @Override
    public String toString() {
        return nonTerminalFirsts.toString() + "\n" + mayBeEmpty.toString();
    }
}


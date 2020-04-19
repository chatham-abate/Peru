package org.perudevteam.parser.grammar;

import io.vavr.collection.*;
import io.vavr.control.Either;

import java.util.Objects;

/**
 * Context free grammar class
 *
 * @param <NT> Non terminal Type
 * @param <T> Terminal Type
 * @param <P> Production Type
 */
public class CFGrammar<NT extends Enum<NT>, T extends Enum<T>, P extends Production<NT, T>> {

    private final NT startSymbol;
    private final Map<NT, Set<P>> productionMap;

    private final Set<T> terminalsUsed;
    // Non-Terminals Used = productionMap.KeySet();

    /**
     * Constructor for generating the production.. with checks.
     * All non-terminals appearing in rules must have their own rule.
     * The start symbol must have its own rule.
     *
     * @param start The start symbol.
     * @param productions Sequence of productions.
     */
    public CFGrammar(NT start, Seq<P> productions) {
        // Neither productions nor start can be null.
        Objects.requireNonNull(start);
        Objects.requireNonNull(productions);

        // Store all non-terminals with rules temporarily.
        Set<NT> nonTerminalsUsed = HashSet.ofAll(productions.map(Production::getSource));

        if (!nonTerminalsUsed.contains(start)) {
            throw new IllegalArgumentException("Start Symbol must have its own rule in the grammar.");
        }

        startSymbol = start;

        Set<T> termsUsedTemp = HashSet.empty();

        // Fill A temporary hash map with empty sets.
        Map<NT, Set<P>> prodMapTemp = HashMap.empty();
        for (NT nonTermUsed: nonTerminalsUsed) prodMapTemp = prodMapTemp.put(nonTermUsed, HashSet.empty());

        for (P p: productions) {
            // Add all terminals in the rule to the terminals used temp set.
            termsUsedTemp = termsUsedTemp.addAll(p.getRule().filter(Either::isRight).map(Either::get));

            // Make sure all non-terminals in the rule have their own rules.
            if (!p.getRule().filter(Either::isLeft)      // Get Left Eithers.
                    .map(Either::getLeft)               // Map to their values.
                    .filter(nt -> !nonTerminalsUsed.contains(nt)).isEmpty()) { // All values without rules.
                throw new IllegalArgumentException("All non-terminals must have their own rules in the grammar.");
            }

            // Add production to production map under its source.
            NT source = p.getSource();
            prodMapTemp = prodMapTemp.put(source, prodMapTemp.get(source).get().add(p));
        }

        terminalsUsed = termsUsedTemp;
        productionMap = prodMapTemp;
    }

    // Direct Constructor with no checks... only used by methods of this class.
    protected CFGrammar(NT start, Map<NT, Set<P>> prodMap, Set<T> termsUsed) {
        startSymbol = start;
        productionMap = prodMap;
        terminalsUsed = termsUsed;
    }

    public NT getStartSymbol() {
        return startSymbol;
    }

    public Set<NT> getNonTerminalsUsed() {
        return productionMap.keySet();
    }

    public Set<T> getTerminalsUsed() {
        return terminalsUsed;
    }

    public Set<P> getProductions(NT nonTerminal) {
        if (productionMap.containsKey(nonTerminal)) {
            return productionMap.get(nonTerminal).get();
        }

        throw new IllegalArgumentException("Given Non-Terminal (while valid) is not used in this grammar.");
    }

    public Map<NT, Set<P>> getProductionMap() {
        return productionMap;
    }

    protected Map<NT, Set<P>> newProductionMap(P p) {
        NT source = p.getSource();
        Map<NT, Set<P>> newProdMap = productionMap.containsKey(source)
                ? productionMap.put(source, productionMap.get(source).get().add(p))
                : productionMap.put(source, HashSet.of(p));

        // All nonterminals in the rule which are do not have rules...
        Seq<NT> notRuled = p.getRule()
                .filter(Either::isLeft)
                .map(Either::getLeft)
                .filter(nt -> !newProdMap.containsKey(nt));

        if (!notRuled.isEmpty()) {
            throw new IllegalArgumentException("Given production has non-terminals which are not defined.");
        }

        return newProdMap;
    }


    public CFGrammar<NT, T, P> withProduction(P p) {
        Objects.requireNonNull(p);  // p cannot be null.

        Set<T> newTerminals = terminalsUsed.addAll(p.getRule().filter(Either::isRight).map(Either::get));

        return new CFGrammar<>(startSymbol, newProductionMap(p), newTerminals);
    }

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();

        for (NT nonTerminal: productionMap.keySet()) {
            for (Production<NT, T> p: productionMap.get(nonTerminal).get()) {
                strBuilder.append(p.toString());
                strBuilder.append('\n');
            }
        }

        String str = strBuilder.toString();

        return str.substring(0, str.length() - 1);  // Exclude final newline Character.
    }
}



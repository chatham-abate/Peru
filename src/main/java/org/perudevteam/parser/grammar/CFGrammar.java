package org.perudevteam.parser.grammar;

import io.vavr.collection.*;
import io.vavr.control.Either;

import java.util.Objects;

/**
 * This class represents a Context Free Grammar.
 * <br>
 * It is composed of a set of productions.
 * @see <a href="https://en.wikipedia.org/wiki/Context-free_grammar#Formal_definitions">Context Free Grammar</a>
 *
 * @param <NT> The non-terminal <b>Enum</b> type.
 * @param <T> The terminal <b>Enum</b> type.
 * @param <P> The <b>Production</b> type.
 */
public class CFGrammar<NT extends Enum<NT>, T extends Enum<T>, P extends Production<NT, T>> {

    /**
     * The start symbol of the grammar.
     */
    private final NT startSymbol;

    /**
     * The production map of the grammar.
     * <br>
     * This maps each non terminal to its corresponding set of <b>Production</b>s.
     */
    private final Map<NT, Set<P>> productionMap;

    /**
     * Set of terminal symbols used by the grammar.
     */
    private final Set<T> terminalsUsed;

    /**
     * Construct a new <b>CFGrammar</b> from some start symbol as sequence of <b>Production</b>s.
     * <br>
     * All non terminal symbols found in this grammar must have their own <b>Production</b>s also in this
     * grammar.
     *
     * @param start The start symbol of the grammar.
     * @param productions The sequence of <b>Production</b>s of this grammar.
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

    /**
     * Directly build a <b>CFGrammar</b> with no checks on the given parameters.
     *
     * @param start The start symbol of the grammar.
     * @param prodMap A map from non terminal symbols to their corresponding sets of <b>Production</b>s.
     * @param termsUsed The set of Terminals used by this grammar.
     */
    protected CFGrammar(NT start, Map<NT, Set<P>> prodMap, Set<T> termsUsed) {
        startSymbol = start;
        productionMap = prodMap;
        terminalsUsed = termsUsed;
    }

    /**
     * Get this grammar's start symbol.
     *
     * @return The start symbol.
     */
    public NT getStartSymbol() {
        return startSymbol;
    }

    /**
     * Get this grammar's set of non-terminal symbols.
     *
     * @return The set of non-terminals.
     */
    public Set<NT> getNonTerminalsUsed() {
        return productionMap.keySet();
    }

    /**
     * Get this grammar's set of terminal symbols.
     *
     * @return The set of terminal symbols.
     */
    public Set<T> getTerminalsUsed() {
        return terminalsUsed;
    }

    /**
     * Get a given non-terminal's set of <b>Production</b>s in this grammar.
     * <br>
     * If this grammar does not contain the given non-terminal, throw an error.
     *
     * @param nonTerminal The non-terminal.
     * @return The set of <b>Productions</b>.
     */
    public Set<P> getProductions(NT nonTerminal) {
        if (productionMap.containsKey(nonTerminal)) {
            return productionMap.get(nonTerminal).get();
        }

        throw new IllegalArgumentException("Given Non-Terminal (while valid) is not used in this grammar.");
    }

    /**
     * Get this grammar's <b>Production</b> map.
     *
     * @return The <b>Production</b> map.
     */
    public Map<NT, Set<P>> getProductionMap() {
        return productionMap;
    }

    /**
     * Add a new <b>Production</b> to this grammar's production map.
     * <br>
     * If the given <b>Production</b> introduces non-terminals with no entries in the new map,
     * an error is thrown.
     *
     * @param p The <b>Production</b> to be added.
     * @return The new map.
     */
    protected Map<NT, Set<P>> newProductionMap(P p) {
        NT source = p.getSource();
        Map<NT, Set<P>> newProdMap = productionMap.containsKey(source)
                ? productionMap.put(source, productionMap.get(source).get().add(p))
                : productionMap.put(source, HashSet.of(p));

        // All non-terminals in the rule which are do not have rules...
        Seq<NT> notRuled = p.getRule()
                .filter(Either::isLeft)
                .map(Either::getLeft)
                .filter(nt -> !newProdMap.containsKey(nt));

        if (!notRuled.isEmpty()) {
            throw new IllegalArgumentException("Given production has non-terminals which are not defined.");
        }

        return newProdMap;
    }

    /**
     * Add a <b>Production</b> to this <b>CFGrammar</b>.
     *
     * @param p The <b>Production</b> to add.
     * @return The new <b>CFGrammar</b>.
     */
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



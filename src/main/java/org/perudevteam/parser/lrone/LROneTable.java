package org.perudevteam.parser.lrone;

import io.vavr.collection.*;
import io.vavr.control.Either;
import org.perudevteam.parser.grammar.CFGrammar;
import org.perudevteam.parser.grammar.Production;
import static io.vavr.control.Either.*;

import java.util.Objects;

/**
 * LR(1) table for an LR(1) parser of any type.
 * @param <NT> The non-terminal enum type.
 * @param <T> The Terminal Enum Type.
 * @param <P> The type of production being stored in the table's reduce states.
 */
public class LROneTable<NT extends Enum<NT>, T extends Enum<T>, P extends Production<NT, T>> {
    private Array<Array<Integer>> gotoTable;
    private Array<Array<Either<Integer, P>>> actionTable;

    private Map<NT, Integer> nonTerminalMap;
    private Map<T, Integer> terminalMap;

    public LROneTable(CFGrammar<NT, T, P> g) {
        Objects.requireNonNull(g);  // The given grammar cannot be null.

        // Store in grammar information in local fields.
        Set<T> terminals = g.getTerminalsUsed();
        Set<NT> nonTerminals = g.getNonTerminalsUsed();
        Map<NT, Set<P>> prodMap = g.getProductionMap();
        FirstSets<NT, T> firstSets = new FirstSets<>(g);

        NT goal = g.getStartSymbol();

        // First we must confirm that goal is not on the rhs of any productions.
        for (NT nt: nonTerminals) {
            for (P p: prodMap.get(nt).get()) {
                if (p.getRule().filter(Either::isLeft).map(Either::getLeft).contains(goal)) {
                    throw new IllegalArgumentException("Start Symbol can not appear on rhs of any productions.");
                }
            }
        }

        // First give all non-terminals and terminals indices for the tables.
        nonTerminalMap = HashMap.empty();
        terminalMap = HashMap.empty();

        int i = 1;  // The 0 column in the action table is saved for eof.
        for (T t: terminals) terminalMap = terminalMap.put(t, i++);
        i = 0;
        for (NT nt: nonTerminals) nonTerminalMap = nonTerminalMap.put(nt, i++);

        // Now initial CC setup.
        Set<LROneItem<NT, T, P>> cc0 = prodMap.get(goal).get().map(p -> new LROneItem<>(0, p));
        cc0 = LROneItem.closureSet(g, firstSets, cc0);  // Perform closure.

        Queue<Set<LROneItem<NT, T, P>>> cc = Queue.of(cc0);
        Queue<Set<LROneItem<NT, T, P>>> workQueue = Queue.of(cc0);

        // Finally initial Table set up.
        final Array<Either<Integer, P>> blankActionRow = Array.fill(terminals.length() + 1, left(0));
        final Array<Integer> blankGotoRow = Array.fill(nonTerminals.length(), 0);

        // Temporary action and goto table.
        Queue<Array<Either<Integer, P>>> tempActionTable = Queue.of(blankActionRow);
        Queue<Array<Integer>> tempGotoTable = Queue.of(blankGotoRow);

        // Now for work loop!
        int row = 0;
        while (!workQueue.isEmpty()) {
            Set<LROneItem<NT, T, P>> cci = workQueue.head();
            workQueue = workQueue.tail();

            for (LROneItem<NT, T, P> lri: cci) {
                Seq<Either<NT, T>> rule = lri.getProduction().getRule();
                int cursor = lri.getCursor();

                if (rule.length() == cursor) {
                    // A -> B*, C Reduce Action[i, C] <- reduce A.
                    final int col = lri.hasSuffix()
                            ? terminalMap.get(lri.getSuffix()).get()
                            : 0;

                    Either<Integer, P> placeHolder = tempActionTable.get(row).get(col);

                    if (placeHolder.isRight() || placeHolder.getLeft() != 0) {
                        throw new IllegalArgumentException("Given grammar not LR(1).");
                    }

                    // Otherwise update.
                    tempActionTable.update(row, r -> r.update(col, right(lri.getProduction())));
                } else {
                    // SHIFTTTTT!!!!
                    // Either a nonTerminal shift or a terminal shift.
                    // Terminal shifts result in changes in the action table.
                    // Nonterminal shifts result in changes to the goto table.
                }
            }

            row++;
        }


    }
}

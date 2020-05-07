package com.github.chathamabate.peru.parser.lrone;

import com.github.chathamabate.peru.parser.grammar.CFGrammar;
import com.github.chathamabate.peru.parser.grammar.Production;
import io.vavr.collection.*;
import io.vavr.control.Either;
import com.github.chathamabate.peru.misc.MiscHelpers;

import static io.vavr.control.Either.*;

import java.util.Objects;

/**
 * LR(1) table for an LR(1) parser of any type.
 * @param <NT> The non-terminal enum type.
 * @param <T> The Terminal Enum Type.
 * @param <P> The type of production being stored in the table's reduce states.
 */
public class LROneTable<NT extends Enum<NT>, T extends Enum<T>, P extends Production<NT, T>> {
    private final Array<Array<Integer>> gotoTable;
    private final Array<Array<Either<Integer, P>>> actionTable;

    private final Array<Set<LROneItem<NT, T, P>>> cc;

    private final Map<NT, Integer> nonTerminalMap;
    private final Map<T, Integer> terminalMap;

    public LROneTable(CFGrammar<NT, T, P> g) {
        Objects.requireNonNull(g);  // The given grammar cannot be null.

        // Store in grammar information in local fields.
        Set<T> terminals = g.getTerminalsUsed();
        Set<NT> nonTerminals = g.getNonTerminalsUsed();
        Map<NT, Set<P>> prodMap = g.getProductionMap();
        FirstSets<NT, T> firstSets = new FirstSets<>(g);

        // Goal is the start symbol of the grammar.
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
        Map<T, Integer> terminalMapTemp = HashMap.empty();
        Map<NT, Integer> nonTerminalMapTemp = HashMap.empty();

        int i = 1;  // The 0 column in the action table is saved for eof.
        for (T t: terminals) terminalMapTemp = terminalMapTemp.put(t, i++);
        i = 0;
        for (NT nt: nonTerminals) {
            if (nt != goal) {
                nonTerminalMapTemp = nonTerminalMapTemp.put(nt, i++);
            }
        }

        terminalMap = terminalMapTemp;
        nonTerminalMap = nonTerminalMapTemp;

        // Now initial CC setup.
        Set<LROneItem<NT, T, P>> cc0 = prodMap.get(goal).get().map(p -> new LROneItem<>(0, p));
        cc0 = LROneItem.closureSet(g, firstSets, cc0);  // Perform closure.

        Vector<Set<LROneItem<NT, T, P>>> tempCC = Vector.of(cc0);

        // Finally initial Table set up.
        final Array<Either<Integer, P>> blankActionRow = Array.fill(terminals.length() + 1, left(0));
        final Array<Integer> blankGotoRow = Array.fill(nonTerminals.length() - 1, 0);

        // Temporary action and goto table.
        Vector<Array<Either<Integer, P>>> tempActionTable = Vector.of(blankActionRow);
        Vector<Array<Integer>> tempGotoTable = Vector.of(blankGotoRow);

        // Now for work loop!
        int row = 0;
        while (row < tempCC.length()) {
            Set<LROneItem<NT, T, P>> cci = tempCC.get(row);

            Map<NT, Set<LROneItem<NT, T, P>>> ntShifts = HashMap.empty();
            Map<T, Set<LROneItem<NT, T, P>>> tShifts = HashMap.empty();

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
                    tempActionTable = tempActionTable.update(row, r -> r.update(col, right(lri.getProduction())));
                } else {
                    Either<NT, T> nextSymEither = rule.get(cursor);

                    if (nextSymEither.isLeft()) {
                        // Non Terminal Shift.
                        NT nt = nextSymEither.getLeft();
                        ntShifts = ntShifts.containsKey(nt)
                                ? ntShifts.put(nt, ntShifts.get(nt).get().add(lri.shiftCursor()))
                                : ntShifts.put(nt, HashSet.of(lri.shiftCursor()));
                    } else {
                        // Terminal Shift.
                        T t = nextSymEither.get();
                        tShifts = tShifts.containsKey(t)
                                ? tShifts.put(t, tShifts.get(t).get().add(lri.shiftCursor()))
                                : tShifts.put(t, HashSet.of(lri.shiftCursor()));
                    }
                }
            }

            // Reductions are done, now for dealing with the shift moves.
            ntShifts = ntShifts.mapValues(s -> LROneItem.closureSet(g, firstSets, s));
            tShifts = tShifts.mapValues(s -> LROneItem.closureSet(g, firstSets, s));

            // Terminal Shifts first.
            for (T tShift: tShifts.keySet()) {
                Set<LROneItem<NT, T, P>> ccj = tShifts.get(tShift).get();

                final int ccSize = tempCC.length();
                final int col = terminalMap.get(tShift).get();
                int shiftState = 0;

                for (int st = 0; st < ccSize; st++) {
                    if (tempCC.get(st).equals(ccj)) {
                        shiftState = st;
                        break;
                    }
                }

                if (shiftState == 0) {
                    // No equal state was found. Create a new state.
                    shiftState = ccSize;
                    tempCC = tempCC.append(ccj);

                    tempActionTable = tempActionTable.append(blankActionRow);
                    tempGotoTable = tempGotoTable.append(blankGotoRow);
                }

                // Get the cell which will be overwritten.
                Either<Integer, P> cell = tempActionTable.get(row).get(col);

                final int shiftStateFinal = shiftState;

                if (cell.isLeft() && cell.getLeft() == 0) {
                    tempActionTable = tempActionTable.update(row, r -> r.update(col, left(shiftStateFinal)));
                } else if (cell.isRight() || cell.getLeft() != shiftStateFinal) {
                    throw new IllegalArgumentException("Given Grammar is not LR(1).");
                }
            }

            for (NT ntShift: ntShifts.keySet()) {
                Set<LROneItem<NT, T, P>> ccj = ntShifts.get(ntShift).get();

                final int ccSize = tempCC.length();
                final int col = nonTerminalMap.get(ntShift).get();
                int shiftState = 0;

                for (int st = 0; st < ccSize; st++) {
                    if (tempCC.get(st).equals(ccj)) {
                        shiftState = st;
                        break;
                    }
                }

                if (shiftState == 0) {
                    shiftState = ccSize;
                    tempCC = tempCC.append(ccj);

                    tempActionTable = tempActionTable.append(blankActionRow);
                    tempGotoTable = tempGotoTable.append(blankGotoRow);
                }

                final int shiftStateFinal = shiftState;
                int cell = tempGotoTable.get(row).get(col);

                if (cell == 0) {
                    tempGotoTable = tempGotoTable.update(row, r -> r.update(col, shiftStateFinal));
                } else if (cell != shiftStateFinal) {
                    throw new IllegalArgumentException("Given Grammar is not LR(1).");
                }
            }

            row++;
        }

        gotoTable = Array.ofAll(tempGotoTable);
        actionTable = Array.ofAll(tempActionTable);
        cc = Array.ofAll(tempCC);
    }

    /**
     * Confirm a given state is valid.
     */
    protected void validateState(int state) {
        if (state < 0 || state >= cc.length()) {
            throw new IndexOutOfBoundsException("Given state is not valid.");
        }
    }


    public Array<Set<LROneItem<NT, T, P>>> getCC() {
        return cc;
    }

    public Array<Array<Integer>> getGotoTable() {
        return gotoTable;
    }

    /**
     * Get the shift for being at a certain state with a non-terminal lookahead.
     */
    public Integer gotoShift(int state, NT nonTerminal) {
        validateState(state);
        if (!nonTerminalMap.containsKey(nonTerminal)) {
            throw new IllegalArgumentException("Given Non Terminal not used in the table");
        }

        return gotoTable.get(state).get(nonTerminalMap.get(nonTerminal).get());
    }

    public Array<Array<Either<Integer, P>>> getActionTable() {
        return actionTable;
    }

    /**
     * Get the action for being at a certain state with a terminal lookahead.
     */
    public Either<Integer, P> actionMove(int state, T terminal) {
        validateState(state);
        if (!terminalMap.containsKey(terminal)) {
            throw new IllegalArgumentException("Given Terminal not used in the table");
        }

        return actionTable.get(state).get(terminalMap.get(terminal).get());
    }

    /**
     * Get the action for being at a certain state with an EOF lookahead.
     */
    public Either<Integer, P> actionMove(int state) {
        validateState(state);
        return actionTable.get(state).get(0);
    }

    public String actionTableString() {
        Seq<String> rowLabels = Array.range(0, cc.length()).map(i -> i + "");
        Seq<String> colLabels = Array.range(0, terminalMap.keySet().length() + 1).map(i ->
                i == 0
                        ? "$"
                        : terminalMap.keySet().filter(k -> terminalMap.get(k).get().equals(i)).head().name()
        );

        Seq<Seq<String>> grid = actionTable.map(r -> r.map(cell -> {
            if (cell.isLeft()) {
                return cell.getLeft() == 0 ? "" : "s" + cell.getLeft();
            }

            return cell.get().getSource().name();
        }));

        return MiscHelpers.gridString(rowLabels, colLabels, grid);
    }

    public String gotoTableString() {
        Seq<String> rowLabels = Array.range(0, cc.length()).map(i -> i + "");
        Seq<String> colLabels = Array.range(0, nonTerminalMap.keySet().length()).map(i ->
                nonTerminalMap.keySet().filter(nt -> nonTerminalMap.get(nt).get().equals(i)).head().name());

        Seq<Seq<String>> grid = gotoTable.map(r -> r.map(cell -> cell.equals(0) ? "" : cell + ""));

        return MiscHelpers.gridString(rowLabels, colLabels, grid);
    }
}

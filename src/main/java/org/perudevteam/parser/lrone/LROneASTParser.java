package org.perudevteam.parser.lrone;

import io.vavr.Function1;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import io.vavr.control.Either;
import org.perudevteam.dynamic.Dynamic;
import org.perudevteam.misc.Typed;
import org.perudevteam.parser.ASTParser;
import org.perudevteam.parser.grammar.AttrCFGrammar;
import org.perudevteam.parser.grammar.AttrProduction;

import static io.vavr.control.Either.*;

import java.util.Objects;

public class LROneASTParser<NT extends Enum<NT>, T extends Enum<T>, L, D extends Typed<T>>
        implements ASTParser<L, D> {

    private Map<NT, Integer> nonTerminalIndex;
    private Map<T, Integer> terminalIndex;

    @SuppressWarnings("unchecked")
    public LROneASTParser(AttrCFGrammar<NT, T, ? extends AttrProduction<NT, T>, Tuple2<L, D>> grammar) {
        Objects.requireNonNull(grammar);
        AttrCFGrammar<NT, T, AttrProduction<NT, T>, Tuple2<L, D>> g =
                (AttrCFGrammar<NT, T, AttrProduction<NT, T>, Tuple2<L, D>>) grammar;

        // Put grammar details into local fields.
        NT startSymbol = g.getStartSymbol();
        Map<NT, Set<AttrProduction<NT, T>>> prodMap = g.getProductionMap().mapValues(Set::narrow);
        Set<T> terminals = g.getTerminalsUsed();
        Set<NT> nonTerminals = g.getNonTerminalsUsed();
        FirstSets<NT, T> firstSets = new FirstSets<>(g);

        // Initialize Indices.
        nonTerminalIndex = HashMap.empty();
        terminalIndex = HashMap.empty();

        // Start symbol cannot be on the rhs of any rules.
        int ind = 0;    // Also fill non-terminal index.
        Either<NT, T> startSymbolEither = left(startSymbol);
        for (NT nt: nonTerminals) {
            for (AttrProduction<NT, T> ap: prodMap.get(nt).get()) {
                if (ap.getRule().contains(startSymbolEither)) {
                    throw new IllegalArgumentException("Given start symbol appears on rhs of a production.");
                }
            }

            nonTerminalIndex = nonTerminalIndex.put(nt, ind);
            ind++;
        }

        // Fill terminal index.
        ind = 1;
        for (T t: terminals) {
            terminalIndex = terminalIndex.put(t, ind);
            ind++;
        }

        // We must build the Canonical Collection set. (CC) and the table at the same time.
        Seq<Set<LROneItem<NT, T, AttrProduction<NT, T>>>> cc = Queue.empty();
        Seq<Set<LROneItem<NT, T, AttrProduction<NT, T>>>> workQueue = Queue.empty();

        // temporary goto and action tables.
        Seq<Array<Either<Integer, AttrProduction<NT, T>>>> tempActionTable = Queue.empty();
        Seq<Array<Integer>> tempGotoTable = Queue.empty();

        Array<Either<Integer, AttrProduction<NT, T>>> emptyActionRow = Array.fill(terminals.length() + 1, left(0));
        Array<Integer> emptyGotoRow = Array.fill(nonTerminals.length(), 0);

        // If a set is in the work stack -> that set is in the CC already, yet its specific row is yet to
        // be calculated and stored in the table.

        // cc0 will manually calculated.
        Set<LROneItem<NT, T, AttrProduction<NT, T>>> cc0 = prodMap.get(startSymbol).get().map(ap ->
            new LROneItem<>(0, ap));

        cc0 = LROneItem.closureSet(g, firstSets, cc0);

        // Add cc0 to work stack and CC.
        cc = cc.append(cc0);
        workQueue = workQueue.append(cc0);

        // Add state0 rows to the action and goto tables.
        tempActionTable = tempActionTable.append(emptyActionRow);
        tempGotoTable = tempGotoTable.append(emptyGotoRow);

        int row = 0; // Current row in the table being generated.

        while (!workQueue.isEmpty()) {
            // pop head off work queue.
            Set<LROneItem<NT, T, AttrProduction<NT, T>>> cci = workQueue.head();
            workQueue = workQueue.tail();

            // Categorize shifts by whether the shift is a terminal or a non terminal.
            Map<NT, Set<LROneItem<NT, T, AttrProduction<NT, T>>>> nonTerminalShifts = HashMap.empty();
            Map<T, Set<LROneItem<NT, T, AttrProduction<NT, T>>>> terminalShifts = HashMap.empty();

            for (LROneItem<NT, T, AttrProduction<NT, T>> lri: cci) {
                if (lri.getCursor() == lri.getProduction().getRule().length()) {
                    // If we are on a reduction, simply add it to the action table...
                    // reductions are always actions.
                    final int col = lri.hasSuffix() ? terminalIndex.get(lri.getSuffix()).get() : 0;

                    tempActionTable = tempActionTable.update(row, r -> {
                        Either<Integer, AttrProduction<NT, T>>  cell = r.get(col);
                        if (!cell.isLeft() || cell.getLeft() != 0) {
                            throw new IllegalArgumentException("Table conflict, Grammar is not LR(1).");
                        }

                        return r.update(col, right(lri.getProduction()));
                    });
                } else {
                    // Otherwise we are on a shift.
                    Either<NT, T> nextSymEither = lri.getProduction().getRule().get(lri.getCursor());
                    LROneItem<NT, T, AttrProduction<NT, T>> shiftedLri = lri.shiftCursor();

                    if (nextSymEither.isLeft()) {
                        // After the cursor comes a nonTerminal.
                        NT nextSym = nextSymEither.getLeft();

                        nonTerminalShifts = nonTerminalShifts.containsKey(nextSym)
                                ? nonTerminalShifts.put(nextSym, nonTerminalShifts.get(nextSym).get().add(shiftedLri))
                                : nonTerminalShifts.put(nextSym, HashSet.of(shiftedLri));
                    } else {
                        // After the cursor comes a terminal.
                        T nextSym = nextSymEither.get();

                        terminalShifts = terminalShifts.containsKey(nextSym)
                                ? terminalShifts.put(nextSym, terminalShifts.get(nextSym).get().add(shiftedLri))
                                : terminalShifts.put(nextSym, HashSet.of(shiftedLri));
                    }
                }
            }

            // Reductions have been dealt with, now we must deal with shifts.
            // First lets perform closures on all of the shift sets.
            nonTerminalShifts = nonTerminalShifts.mapValues(s -> LROneItem.closureSet(g, firstSets, s));
            terminalShifts = terminalShifts.mapValues(s -> LROneItem.closureSet(g, firstSets, s));

            // Now for filling in the table.
            for (T t: terminalShifts.keySet()) {

            }
        }
    }

    @Override
    public Function1<Dynamic, Dynamic> buildASTUnsafe(Seq<Tuple2<L, D>> tokens) throws Throwable {
        return null;
    }
}

package org.perudevteam.fa;

import io.vavr.collection.Array;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import org.perudevteam.misc.SeqHelpers;

import java.util.Objects;

public abstract class DFA<I, IC, O> extends FA<I, IC, O> {

    private final Array<Array<Integer>> transitionTable;

    private DFA(Map<? extends Integer, O> as, Set<IC> ics, Array<? extends Array<? extends Integer>> tt) {
        super(as, ics);

        SeqHelpers.validateTable(tt);

        if (tt.isEmpty()) {
            throw new IllegalArgumentException("DFA requires at least one state.");
        }

        for (Array<? extends Integer> row: tt) {
            for (Integer cell: row) {
                if (cell != -1) {
                    validateState(cell);
                }
            }
        }

        // Confirm correct number of columns.
        if (tt.get(0).length() != getNumberOfInputClasses()) {
            throw new IllegalArgumentException("Malformed transition table.");
        }

        transitionTable = Array.narrow(tt.map(Array::narrow));
    }

    // Unchecked.
    private DFA(Map<IC, Integer> ici, Map<Integer, O> as, Array<Array<Integer>> tt) {
        super(ici, as);
        transitionTable = tt;
    }

    @Override
    protected int getNumberOfStates() {
        return transitionTable.length();
    }

    @Override
    public DFA<I, IC, O> withSingleTransition(int from, int to, IC inputClass) {
        validateState(from);
        validateState(to);
        validateInputClass(inputClass);

        int col = getInputClassIndex().get(inputClass).get();
        final DFA<I, IC, O> thisDFA = this;

        return new DFA<I, IC, O>(getInputClassIndex(), getAcceptingStates(),
                transitionTable.update(from, row -> row.update(col, to))) {
            @Override
            protected IC getInputClassUnchecked(I input) {
                return thisDFA.getInputClassUnchecked(input);
            }
        };
    }

    @Override
    public DFA<I, IC, O> withAcceptingState(int state, O output) {
        validateState(state);
        Objects.requireNonNull(output);

        final DFA<I, IC, O> thisDFA = this;

        return new DFA<I, IC, O>(getInputClassIndex(), getAcceptingStates().put(state, output),
                transitionTable) {
            @Override
            protected IC getInputClassUnchecked(I input) {
                return thisDFA.getInputClassUnchecked(input);
            }
        };
    }
}

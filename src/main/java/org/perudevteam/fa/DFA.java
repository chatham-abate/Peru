package org.perudevteam.fa;

import io.vavr.Function1;
import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import org.perudevteam.misc.SeqHelpers;

import java.util.Objects;

public abstract class DFA<I, IC, O> extends FA<I, IC, O> {

    private final Array<Map<IC, Integer>> transitionTable;

    public DFA(Map<? extends Integer, O> as, Set<IC> ia,
               Array<? extends Map<IC, ? extends Integer>> tt) {
        this(as, ia, tt, true);
    }

    private DFA(Map<? extends Integer, O> as, Set<IC> ia,
                Array<? extends Map<IC, ? extends Integer>> tt, boolean withCheck) {
        super(as, ia, withCheck);

        if (withCheck) {
            Objects.requireNonNull(tt);
            if (tt.isEmpty()) {
                throw new IllegalArgumentException("Transition table needs at least 1 row.");
            }

            for (Map<IC, ? extends Integer> row: tt) {
                Objects.requireNonNull(row);

                for (Tuple2<IC, ? extends Integer> cell: row) {
                    IC inputClass = cell._1;
                    Integer transitionState = cell._2;

                    if (!getInputAlphabet().contains(inputClass)) {
                        throw new IllegalArgumentException("Unknown input class found in transition table" +
                                inputClass.toString() + ".");
                    }

                    validateState(transitionState);
                }
            }
        }

        transitionTable = Array.narrow(tt.map(Map::narrow));
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

        final DFA<I, IC, O> thisDFA = this;

        return new DFA<I, IC, O>(getAcceptingStates(), getInputAlphabet(),
                transitionTable.update(from, row -> row.put(inputClass, to)), false) {
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

        return new DFA<I, IC, O>(getAcceptingStates().put(state, output), getInputAlphabet(),
                transitionTable, false) {
            @Override
            protected IC getInputClassUnchecked(I input) {
                return thisDFA.getInputClassUnchecked(input);
            }
        };
    }
}

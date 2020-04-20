package org.perudevteam.fa;

import io.vavr.Function1;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import io.vavr.control.Option;
import java.util.Objects;

// DFA.
public class DFAutomaton<I, IC, O> extends FAutomaton<I, IC, O> {

    private final Array<Map<IC, Integer>> transitionTable;

    public DFAutomaton(int numberOfStates, Set<? extends IC> ia, Function1<? super I, ? extends IC> gic) {
        this(HashMap.empty(), ia, Array.fill(numberOfStates, HashMap.empty()), gic, true);
    }

    public DFAutomaton(Map<? extends Integer, ? extends O> as,
                       Set<? extends IC> ia,
                       Array<? extends Map<? extends IC, ? extends Integer>> tt,
                       Function1<? super I, ? extends IC> gic) {
        this(as, ia, tt, gic, true);
    }

    DFAutomaton(Map<? extends Integer, ? extends O> as,
                        Set<? extends IC> ia,
                        Array<? extends Map<? extends IC, ? extends Integer>> tt,
                        Function1<? super I, ? extends IC> gic, boolean withCheck) {
        super(as, ia, gic, withCheck);

        if (withCheck) {
            Objects.requireNonNull(tt);
            if (tt.isEmpty()) {
                throw new IllegalArgumentException("Transition table needs at least 1 row.");
            }

            for (Map<? extends IC, ? extends Integer> row: tt) {
                Objects.requireNonNull(row);

                for (Tuple2<? extends IC, ? extends Integer> cell: row) {
                    IC inputClass = cell._1;
                    Integer transitionState = cell._2;

                    Objects.requireNonNull(inputClass);
                    Objects.requireNonNull(transitionState);

                    validateInputClass(inputClass);

                    if (transitionState < 0 || tt.length() <= transitionState) {
                        throw new IndexOutOfBoundsException("Bad transition state found.");
                    }
                }
            }

            for (int acceptingState: getAcceptingStates().keySet()) {
                if (acceptingState < 0 || tt.length() <= acceptingState) {
                    throw new IndexOutOfBoundsException("Bad accepting state found.");
                }
            }
        }

        transitionTable = Array.narrow(tt.map(Map::narrow));
    }

    @Override
    protected int getNumberOfStates() {
        return transitionTable.length();
    }

    public Array<Map<IC, Integer>> getTransitionTable() {
        return transitionTable;
    }

    public boolean hasTransition(int from, I input) {
        IC inputClass = getInputClass(input);
        validateState(from);
        return transitionTable.get(from).containsKey(inputClass);
    }

    public boolean hasTransitionFromClass(int from, IC inputClass) {
        validateInputClass(inputClass);
        validateState(from);
        return transitionTable.get(from).containsKey(inputClass);
    }

    public int getTransition(int from, I input) {
        return getTransitionAsOption(from, input).get();
    }

    public Option<Integer> getTransitionAsOption(int from, I input) {
        IC inputClass = getInputClass(input);
        validateState(from);
        return transitionTable.get(from).get(inputClass);
    }

    public int getTransitionFromClass(int from, IC inputClass) {
        return getTransitionFromClassAsOption(from, inputClass).get();
    }

    public Option<Integer> getTransitionFromClassAsOption(int from, IC inputClass) {
        validateInputClass(inputClass);
        validateState(from);
        return transitionTable.get(from).get(inputClass);
    }

    @Override
    public DFAutomaton<I, IC, O> withSingleTransition(int from, int to, IC inputClass) {
        validateState(from);
        validateState(to);
        validateInputClass(inputClass);

        return new DFAutomaton<I, IC, O>(getAcceptingStates(), getInputAlphabet(),
                transitionTable.update(from, row -> row.put(inputClass, to)),
                getGetInputClassUnchecked(),false);
    }

    @Override
    public DFAutomaton<I, IC, O> withAcceptingState(int state, O output) {
        validateState(state);
        Objects.requireNonNull(output);

        return new DFAutomaton<I, IC, O>(getAcceptingStates().put(state, output), getInputAlphabet(),
                transitionTable, getGetInputClassUnchecked(),false);
    }

    @Override
    public DFAutomaton<I, IC, O> toDFA() {
        return this;
    }
}

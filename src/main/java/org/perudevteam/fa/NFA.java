package org.perudevteam.fa;

import io.vavr.Tuple2;
import io.vavr.collection.Array;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import io.vavr.control.Option;

import java.util.Objects;

public abstract class NFA<I, IC, O> extends FA<I, IC, O>  {
    // NFA has empty transitions...
    // And multi Transitions...
    // How can this be dealt with...
    // We need an empty transitions data structure and a normal transiitons data struture.
    // Then a to DFA algorithm....

    private final Array<Map<IC, Set<Integer>>> transitionTable;
    private final Array<Set<Integer>> epsilonTransitions;

    public NFA(Map<? extends Integer, O> as, Set<IC> ia,
               Array<? extends Map<IC, ? extends Set<? extends Integer>>> tt,
               Array<? extends Set<? extends Integer>> ets, boolean withCheck) {
        super(as, ia, withCheck);

        if (withCheck) {
            Objects.requireNonNull(tt);
            if (tt.isEmpty()) {
                throw new IllegalArgumentException("Transition table needs at least 1 row.");
            }

            // Let's iterate over all rows of the transition table.
            for (Map<IC, ? extends Set<? extends Integer>> row: tt) {
                Objects.requireNonNull(row);

                for (Tuple2<IC, ? extends Set<? extends Integer>> cell: row) {
                    IC inputClass = cell._1;
                    Set<? extends Integer> tSet = cell._2;

                    Objects.requireNonNull(inputClass);
                    Objects.requireNonNull(tSet);

                    validateInputClass(inputClass);

                    if (tSet.isEmpty()) {
                        throw new IllegalArgumentException("Transition Table contains an empty Set.");
                    }

                    // No Null.
                    for (Integer transition: tSet) {
                        Objects.requireNonNull(transition);
                        if (transition < 0 || tt.length() <= transition) {
                            throw new IndexOutOfBoundsException("Bad transition state found.");
                        }
                    }
                }
            }

            // Now for epsilon checks...
            for (Set<? extends Integer> epsilonTrans: ets) {
                Objects.requireNonNull(epsilonTrans);
                for (Integer transState: epsilonTrans) {
                    Objects.requireNonNull(transState);
                    if (transState < 0 || tt.length() <= transState) {
                        throw new IndexOutOfBoundsException("Bad epsilon transition found.");
                    }
                }
            }

            // Finally accepting state checks.
            for (int acceptingState: getAcceptingStates().keySet()) {
                if (acceptingState < 0 || tt.length() <= acceptingState) {
                    throw new IndexOutOfBoundsException("Bad accepting state found.");
                }
            }
        }

        transitionTable = Array.narrow(tt.map(row -> row.mapValues(Set::narrow)));
        epsilonTransitions = Array.narrow(ets.map(Set::narrow));
    }

    @Override
    protected int getNumberOfStates() {
        return transitionTable.length();
    }

    public boolean hasTransitions(int from, I input) {
        IC inputClass = getInputClass(input);
        validateState(from);
        return transitionTable.get(from).containsKey(inputClass);
    }

    public boolean hasTransitionsFromClass(int from, IC inputClass) {
        validateInputClass(inputClass);
        validateState(from);
        return transitionTable.get(from).containsKey(inputClass);
    }

    public Set<Integer> getTransitions(int from, I input) {
        return getTransitionsAsOption(from, input).get();
    }

    public Option<Set<Integer>> getTransitionsAsOption(int from, I input) {
        IC inputClass = getInputClass(input);
        validateState(from);
        return transitionTable.get(from).get(inputClass);
    }

    public Set<Integer> getTransitionsFromClass(int from, IC inputClass) {
        return getTransitionsFromClassAsOption(from, inputClass).get();
    }

    public Option<Set<Integer>> getTransitionsFromClassAsOption(int from, IC inputClass) {
        validateInputClass(inputClass);
        validateState(from);
        return transitionTable.get(from).get(inputClass);
    }

    public Set<Integer> getEpsilonTransitions(int from) {
        validateState(from);
        return epsilonTransitions.get(from);
    }
}

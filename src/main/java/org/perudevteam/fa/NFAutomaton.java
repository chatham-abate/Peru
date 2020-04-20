package org.perudevteam.fa;

import io.vavr.Function1;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import io.vavr.control.Option;

import java.util.Objects;

public class NFAutomaton<I, IC, O> extends FAutomaton<I, IC, O> {

    private final Array<Map<IC, Set<Integer>>> transitionTable;
    private final Array<Set<Integer>> epsilonTransitions;

    public NFAutomaton(int numberOfStates,
                       Set<? extends IC> ia,
                       Function1<? super I, ? extends IC> gic) {
        this(HashMap.empty(), ia,
                Array.fill(numberOfStates, HashMap.empty()),
                Array.fill(numberOfStates, HashSet.empty()), gic, true);
    }

    public NFAutomaton(Map<? extends Integer, ? extends O> as, Set<? extends IC> ia,
                       Array<? extends Map<? extends IC, ? extends Set<? extends Integer>>> tt,
                       Array<? extends Set<? extends Integer>> ets,
                       Function1<? super I, ? extends IC> gic) {
        this(as, ia, tt, ets, gic, true);
    }

    NFAutomaton(Map<? extends Integer, ? extends O> as, Set<? extends IC> ia,
                          Array<? extends Map<? extends IC, ? extends Set<? extends Integer>>> tt,
                          Array<? extends Set<? extends Integer>> ets,
                          Function1<? super I, ? extends IC> gic, boolean withCheck) {

        super(as, ia, gic, withCheck);

        if (withCheck) {
            Objects.requireNonNull(tt);
            if (tt.isEmpty()) {
                throw new IllegalArgumentException("Transition table needs at least 1 row.");
            }

            // Let's iterate over all rows of the transition table.
            for (Map<? extends IC, ? extends Set<? extends Integer>> row: tt) {
                Objects.requireNonNull(row);

                for (Tuple2<? extends IC, ? extends Set<? extends Integer>> cell: row) {
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

            Objects.requireNonNull(ets);

            if (ets.length() != tt.length()) {
                throw new IllegalArgumentException("Inconsistent Number of states.");
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

        transitionTable = Array.narrow(tt.map(row -> Map.narrow(row.mapValues(Set::narrow))));
        epsilonTransitions = Array.narrow(ets.map(Set::narrow));
    }

    @Override
    protected int getNumberOfStates() {
        return transitionTable.length();
    }

    public Array<Map<IC, Set<Integer>>> getTransitionTable() {
        return transitionTable;
    }

    public Array<Set<Integer>> getEpsilonTransitions() {
        return epsilonTransitions;
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

    public boolean hasEpsilonTransitions(int from) {
        validateState(from);
        return !epsilonTransitions.get(from).isEmpty();
    }

    public Set<Integer> getEpsilonTransitions(int from) {
        return getEpsilonTransitionsAsOption(from).get();
    }

    public Option<Set<Integer>> getEpsilonTransitionsAsOption(int from) {
        validateState(from);

        return epsilonTransitions.get(from).isEmpty()
                ? Option.none()
                : Option.some(epsilonTransitions.get(from));
    }

    // With moves...


    @Override
    public NFAutomaton<I, IC, O> withSingleTransition(int from, int to, IC inputClass) {
        validateState(from);
        validateState(to);
        validateInputClass(inputClass);

        Array<Map<IC, Set<Integer>>> newTT = transitionTable.update(from, row ->
                row.containsKey(inputClass)
                        ? row.put(inputClass, row.get(inputClass).get().add(to))
                        : row.put(inputClass, HashSet.of(to))
                );

        return new NFAutomaton<I, IC, O>(getAcceptingStates(), getInputAlphabet(),
                newTT, epsilonTransitions, getGetInputClassUnchecked(), false);
    }

    public NFAutomaton<I, IC, O> withEpsilonTransition(int from, int to) {
        validateState(from);
        validateState(to);

        Array<Set<Integer>> newEps = epsilonTransitions.update(from, set -> set.add(to));

        return new NFAutomaton<I, IC, O>(getAcceptingStates(), getInputAlphabet(),
                transitionTable, newEps, getGetInputClassUnchecked(), false);
    }

    @Override
    public NFAutomaton<I, IC, O> withAcceptingState(int state, O output) {
        validateState(state);
        return new NFAutomaton<I, IC, O>(getAcceptingStates().put(state, output), getInputAlphabet(),
                transitionTable, epsilonTransitions, getGetInputClassUnchecked(), false);
    }

    @Override
    public DFAutomaton<I, IC, O> toDFA() {
        return FAutomatonUtil.convertNFAToDFA(this);
    }
}

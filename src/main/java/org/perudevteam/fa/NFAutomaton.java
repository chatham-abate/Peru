package org.perudevteam.fa;

import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.collection.*;
import io.vavr.control.Option;
import org.perudevteam.misc.MiscHelpers;

import java.util.Objects;

public class NFAutomaton<I, IC, O> extends FAutomaton<I, IC, O> {

    public static <I, IC, O> NFAutomaton<I, IC, O> narrow(NFAutomaton<? super I, ? extends IC, ? extends O> nfa) {
        return new NFAutomaton<>(
                nfa.getAcceptingStates(),
                nfa.getInputAlphabet(),
                nfa.getTransitionTable(),
                nfa.getEpsilonTransitions(),
                nfa.getGetInputClassUnchecked(),
                false
        );
    }

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

    @Override
    public NFAutomaton<I, IC, O> prependStates(int states) {
        if (states <= 0) throw new IllegalArgumentException("States must be positive.");

        Tuple3<Map<Integer, O>, Array<Map<IC, Set<Integer>>>, Array<Set<Integer>>> shiftTuple = shift(states);

        return new NFAutomaton<>(shiftTuple._1, getInputAlphabet(),
                shiftTuple._2.prependAll(Array.fill(states, HashMap.empty())),
                shiftTuple._3.prependAll(Array.fill(states, HashSet.empty())),
                getGetInputClassUnchecked(), false);
    }

    @Override
    public NFAutomaton<I, IC, O> appendStates(int states) {
        if (states <= 0) throw new IllegalArgumentException("States must be positive.");

        return new NFAutomaton<>(getAcceptingStates(), getInputAlphabet(),
                transitionTable.appendAll(Array.fill(states, HashMap.empty())),
                epsilonTransitions.appendAll(Array.fill(states, HashSet.empty())),
                getGetInputClassUnchecked(), false);
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

    // Shift Helper Functions...

    public Tuple3<Map<Integer, O>, Array<Map<IC, Set<Integer>>>, Array<Set<Integer>>> shift(int shift) {
        return Tuple.of(
                getAcceptingStates().mapKeys(acceptingState -> acceptingState + shift),
                transitionTable.map(row -> row.mapValues(stateSet -> stateSet.map(state -> state + shift))),
                epsilonTransitions.map(stateSet -> stateSet.map(state -> state + shift))
        );
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

        return new NFAutomaton<>(getAcceptingStates(), getInputAlphabet(),
                newTT, epsilonTransitions, getGetInputClassUnchecked(), false);
    }

    @Override
    public NFAutomaton<I, IC, O> withSingleTransitions(Set<? extends Integer> froms,
                                                      Set<? extends Integer> tos,
                                                      Set<? extends IC> inputClasses) {
        Objects.requireNonNull(froms);
        Objects.requireNonNull(tos);
        Objects.requireNonNull(inputClasses);

        froms.forEach(this::validateState);
        tos.forEach(this::validateState);
        inputClasses.forEach(this::validateInputClass);

        Array<Map<IC, Set<Integer>>> tt = transitionTable;

        for (int from: froms) {
            for (int to: tos) {
                for (IC inputClass: inputClasses) {
                    tt = tt.update(from, row -> row.containsKey(inputClass)
                            ? row.put(inputClass, row.get(inputClass).get().add(to))
                            : row.put(inputClass, HashSet.of(to))
                    );
                }
            }
        }

        return new NFAutomaton<>(getAcceptingStates(), getInputAlphabet(),
                tt, epsilonTransitions, getGetInputClassUnchecked(), false);
    }

    public NFAutomaton<I, IC, O> withEpsilonTransition(int from, int to) {
        validateState(from);
        validateState(to);

        Array<Set<Integer>> newEps = epsilonTransitions.update(from, set -> set.add(to));

        return new NFAutomaton<>(getAcceptingStates(), getInputAlphabet(),
                transitionTable, newEps, getGetInputClassUnchecked(), false);
    }

    @Override
    public NFAutomaton<I, IC, O> withAcceptingState(int state, O output) {
        validateState(state);
        return new NFAutomaton<>(getAcceptingStates().put(state, output), getInputAlphabet(),
                transitionTable, epsilonTransitions, getGetInputClassUnchecked(), false);
    }

    @Override
    public <OP> NFAutomaton<I, IC, OP> withAcceptingStates(Map<? extends Integer, ? extends OP> newOutputs) {
        MiscHelpers.requireNonNullMap(newOutputs);
        newOutputs.keySet().forEach(this::validateState);
        return new NFAutomaton<>(newOutputs, getInputAlphabet(), transitionTable, epsilonTransitions,
                getGetInputClassUnchecked(), false);
    }

    public NFAutomaton<I, IC, O> merge(NFAutomaton<? super I, ? extends IC, ? extends O> nfa2) {
        return merge(nfa2, getGetInputClassUnchecked());    // Keep old get input class function.
    }

    public NFAutomaton<I, IC, O> merge(NFAutomaton<? super I, ? extends IC, ? extends O> nfa2,
                                       Function1<? super I, ? extends IC> newGIC) {
        Objects.requireNonNull(nfa2);
        Objects.requireNonNull(newGIC);

        // Union of Input Alphabets.
        Set<IC> iaUnion = getInputAlphabet().addAll(nfa2.getInputAlphabet());

        int shift = getNumberOfStates();

        Tuple3<Map<Integer, O>, Array<Map<IC, Set<Integer>>>, Array<Set<Integer>>>
                nfa2Shifts = nfa2.shift(shift)
                .<Map<Integer, O>>map1(Map::narrow)
                .map2(table -> table.map(Map::narrow));

        Map<Integer, O> newAcceptingStates = getAcceptingStates().merge(nfa2Shifts._1);
        Array<Map<IC, Set<Integer>>> newTransitionTable = transitionTable.appendAll(nfa2Shifts._2);
        Array<Set<Integer>> newEpsilonTransitions = epsilonTransitions.appendAll(nfa2Shifts._3);

        newEpsilonTransitions = newEpsilonTransitions.update(0, stateSet -> stateSet.add(shift));

        return new NFAutomaton<>(newAcceptingStates, iaUnion,
                newTransitionTable, newEpsilonTransitions, newGIC, false);
    }

    public DFAutomaton<I, IC, O> toDFA() {
        return FAutomatonUtil.convertNFAToDFA(this, List.empty());
    }

    public DFAutomaton<I, IC, O> toDFA(Set<? extends O> strongSignals) {
        return FAutomatonUtil.convertNFAToDFA(this, List.of(strongSignals));
    }

    public DFAutomaton<I, IC, O> toDFA(Seq<? extends Set<? extends O>> precSeq) {
        return FAutomatonUtil.convertNFAToDFA(this, precSeq);
    }
}

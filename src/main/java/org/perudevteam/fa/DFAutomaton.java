package org.perudevteam.fa;

import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import io.vavr.control.Option;
import org.perudevteam.misc.MiscHelpers;

import java.util.Objects;

// DFA.
public class DFAutomaton<I, IC, O> extends FAutomaton<I, IC, O> {

    public static <I, IC, O> DFAutomaton<I, IC, O> narrow(DFAutomaton<? super I, ? extends IC, ? extends O> dfa) {
        return new DFAutomaton<>(
                dfa.getAcceptingStates(),
                dfa.getInputAlphabet(),
                dfa.getTransitionTable(),
                dfa.getGetInputClassUnchecked(),
                false
        );
    }

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
    public int getNumberOfStates() {
        return transitionTable.length();
    }

    @Override
    public DFAutomaton<I, IC, O> prependStates(int states) {
        if (states <= 0) throw new IllegalArgumentException("States must be positive.");

        // states = the shift here.
        Tuple2<Map<Integer, O>, Array<Map<IC, Integer>>> shiftedTuple = shift(states);

        Array<Map<IC, Integer>> prefix = Array.fill(states, HashMap.empty());

        return new DFAutomaton<>(shiftedTuple._1, getInputAlphabet(),
                shiftedTuple._2.prependAll(prefix), getGetInputClassUnchecked(), false);
    }

    @Override
    public DFAutomaton<I, IC, O> appendStates(int states) {
        if (states <= 0) throw new IllegalArgumentException("States must be positive.");

        Array<Map<IC, Integer>> suffix = Array.fill(states, HashMap.empty());

        return new DFAutomaton<>(getAcceptingStates(), getInputAlphabet(),
                transitionTable.appendAll(suffix), getGetInputClassUnchecked(), false);
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

    public Tuple2<Map<Integer, O>, Array<Map<IC, Integer>>> shift(int shift) {
        return Tuple.of(
                getAcceptingStates().mapKeys(acceptingState -> acceptingState + shift),
                transitionTable.map(row -> row.mapValues(state -> state + shift))
        );
    }

    public DFAutomaton<I, IC, O> combine(DFAutomaton<? super I, ? extends IC, ? extends O> nfa) {
        return combine(nfa, getGetInputClassUnchecked());
    }

    public DFAutomaton<I, IC, O> combine(DFAutomaton<? super I, ? extends IC, ? extends O> nfa,
                                         Function1<? super I, ? extends IC> newGIC) {
        Objects.requireNonNull(nfa);
        Objects.requireNonNull(newGIC);

        int shift = getNumberOfStates();
        Tuple2<Map<Integer, O>, Array<Map<IC, Integer>>> shiftTuple = DFAutomaton.<I, IC, O>narrow(nfa).shift(shift);

        return new DFAutomaton<>(
                getAcceptingStates().merge(shiftTuple._1),
                getInputAlphabet().addAll(nfa.getInputAlphabet()),
                transitionTable.appendAll(shiftTuple._2),
                newGIC, false
        );
    }

    @Override
    public DFAutomaton<I, IC, O> withSingleTransition(int from, int to, IC inputClass) {
        validateState(from);
        validateState(to);
        validateInputClass(inputClass);

        return new DFAutomaton<>(getAcceptingStates(), getInputAlphabet(),
                transitionTable.update(from, row -> row.put(inputClass, to)),
                getGetInputClassUnchecked(),false);
    }

    @Override
    public DFAutomaton<I, IC, O> withSingleTransitions(Set<? extends Integer> froms,
                                                      Set<? extends Integer> tos,
                                                      Set<? extends IC> inputClasses) {
        Array<Map<IC, Integer>> tt = transitionTable;

        Objects.requireNonNull(froms);
        Objects.requireNonNull(tos);
        Objects.requireNonNull(inputClasses);

        froms.forEach(this::validateState);
        tos.forEach(this::validateState);
        inputClasses.forEach(this::validateInputClass);

        for (int from: froms) {
            for (int to: tos) {
                for (IC inputClass: inputClasses) {
                    tt.update(from, row -> row.put(inputClass, to));
                }
            }
        }

        return new DFAutomaton<>(getAcceptingStates(), getInputAlphabet(),
                tt, getGetInputClassUnchecked(), false);
    }

    @Override
    public DFAutomaton<I, IC, O> withAcceptingState(int state, O output) {
        validateState(state);
        Objects.requireNonNull(output);

        return new DFAutomaton<>(getAcceptingStates().put(state, output), getInputAlphabet(),
                transitionTable, getGetInputClassUnchecked(),false);
    }

    @Override
    public <OP> DFAutomaton<I, IC, OP> withAcceptingStates(Map<? extends Integer, ? extends OP> newOutputs) {
        MiscHelpers.requireNonNullMap(newOutputs);
        newOutputs.keySet().forEach(this::validateState);
        return new DFAutomaton<>(newOutputs, getInputAlphabet(), transitionTable,
                getGetInputClassUnchecked(), false);
    }
}

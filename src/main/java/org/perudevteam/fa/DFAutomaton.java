package org.perudevteam.fa;

import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.*;
import io.vavr.control.Option;
import org.perudevteam.misc.MiscHelpers;

import java.util.Objects;

/**
 * This class represents a deterministic finite automaton.
 * A deterministic finite automaton is different from a finite automaton in that for any given state
 * there can be at most one outgoing transition for each element of the input alphabet.
 * Additionally a deterministic finite automaton cannot have epsilon transitions.
 * @see <a href="https://en.wikipedia.org/wiki/Deterministic_finite_automaton">DFA</a>
 *
 * @param <I> The raw input type of the automaton.
 * @param <IC> The translated input type of the automaton.
 * @param <O> The output type of the automaton.
 */
public class DFAutomaton<I, IC, O> extends FAutomaton<I, IC, O> {

    /**
     * Cast a <b>DFAutomaton</b>.
     *
     * @param dfa The automaton to cast.
     * @param <I> The raw input type to cast to.
     * @param <IC> The translated input type to cast to.
     * @param <O> The output type to case to.
     * @return The casted <b>DFAutomaton</b>.
     */
    public static <I, IC, O> DFAutomaton<I, IC, O> narrow(DFAutomaton<? super I, ? extends IC, ? extends O> dfa) {
        return new DFAutomaton<>(
                dfa.getAcceptingStates(),
                dfa.getInputAlphabet(),
                dfa.getTransitionTable(),
                dfa.getGetInputClassUnchecked(),
                false
        );
    }

    /**
     * The automatons transition table.
     * Each state has exactly one row in this table.
     * Each row maps translated input types to other states. This represents the
     * outgoing transitions of a given state.
     */
    private final Array<Map<IC, Integer>> transitionTable;

    /**
     * Builds a <b>DFAutomaton</b> with no transitions.
     *
     * @param numberOfStates The number of states to initialize.
     * @param ia The input alphabet.
     * @param gic The translation function.
     */
    public DFAutomaton(int numberOfStates, Set<? extends IC> ia, Function1<? super I, ? extends IC> gic) {
        this(HashMap.empty(), ia, Array.fill(numberOfStates, HashMap.empty()), gic, true);
    }

    /**
     * Build a <b>DFAutomaton</b> with given transitions and states.
     *
     * @param as The accepting states of the automaton.
     * @param ia The input alphabet of the automaton.
     * @param tt The transition table of the automaton.
     * @param gic The translation function of the automaton.
     */
    public DFAutomaton(Map<? extends Integer, ? extends O> as,
                       Set<? extends IC> ia,
                       Array<? extends Map<? extends IC, ? extends Integer>> tt,
                       Function1<? super I, ? extends IC> gic) {
        this(as, ia, tt, gic, true);
    }

    /**
     * Build a <b>DFAutomaton</b> with given transitions and states.
     *
     * @param as The accepting states of the automaton.
     * @param ia The input alphabet of the automaton.
     * @param tt The transition table of the automaton.
     * @param gic The translation function of the automaton.
     * @param withCheck Whether or not the above parameters should be validated.
     *                  For all public constructors this flag is true.
     */
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

    /**
     * Get the states and transitions of this automaton.
     *
     * @return This automatons transition table.
     */
    public Array<Map<IC, Integer>> getTransitionTable() {
        return transitionTable;
    }

    /**
     * Determine whether a state has an outgoing transition for a given raw input.
     *
     * @param from The state.
     * @param input The raw input.
     * @return Whether or not there is a transition.
     */
    public boolean hasTransition(int from, I input) {
        IC inputClass = getInputClass(input);
        validateState(from);
        return transitionTable.get(from).containsKey(inputClass);
    }

    /**
     * Determine whether a state has an outgoing transition for a given translated input.
     *
     * @param from The state.
     * @param inputClass The translated input.
     * @return Whether or not there is a transition.
     */
    public boolean hasTransitionFromClass(int from, IC inputClass) {
        validateInputClass(inputClass);
        validateState(from);
        return transitionTable.get(from).containsKey(inputClass);
    }

    /**
     * Given a state and a translated input, traverse the state's corresponding outgoing edge
     * and return the ending state of that edge.
     *
     * @param from The starting state.
     * @param input The translated input to interpret.
     * @return The ending state.
     */
    public int getTransition(int from, I input) {
        return getTransitionAsOption(from, input).get();
    }

    /**
     * Same as {@link DFAutomaton#getTransition(int, Object)} except result it returned as an <b>Option</b>.
     * <b>None</b> is returned if there is no outgoing edge from the given state with said raw input.
     * FINISFHSDHFSDHFSDHFSDHFDHFHFHDSFHSD
     * @param from
     * @param input
     * @return
     */
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

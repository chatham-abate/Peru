package org.perudevteam.fa;

import io.vavr.Function1;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import io.vavr.collection.*;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.perudevteam.misc.MiscHelpers;
import java.util.Objects;

/**
 * This class represents a non-deterministic finite automaton.
 * <br>
 * This automaton may have epsilon transitions between states. Additionally, one state may have multiple
 * outgoing edges for the same translated input. This makes sense given the automaton is non-deterministic.
 * @see <a href="https://en.wikipedia.org/wiki/Nondeterministic_finite_automaton">DFA</a>
 *
 * @param <I> The raw input type.
 * @param <IC> The translated input type.
 * @param <O> The output type.
 */
public class NFAutomaton<I, IC, O> extends FAutomaton<I, IC, O> {

    /**
     * Cast an <b>NFAutomaton</b>.
     *
     * @param nfa The <b>NFAutomaton</b> to case.
     * @param <I> The raw input type to cast to.
     * @param <IC> The translated input type to cast to.
     * @param <O> The output type to cast to.
     * @return The casted automaton.
     */
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

    /**
     * Transition Table for the automaton.
     * <br>
     * Each row represents a single state's transitions. This is done with map from
     * translated inputs to sets of states. Sets are used here since one translated input
     * could lead to multiple other states.
     */
    private final Array<Map<IC, Set<Integer>>> transitionTable;

    /**
     * Epsilon Transitions for the automaton.
     * <br>
     * Each row in this table represents a single state's e transitions. Here no maps are needed
     * since there are no inputs associated with an epsilon transition.
     */
    private final Array<Set<Integer>> epsilonTransitions;

    /**
     * Construct an <b>NFAutomaton</b> with no accepting states or transitions.
     *
     * @param numberOfStates The number of states.
     * @param ia The input alphabet.
     * @param gic The translation function.
     */
    public NFAutomaton(int numberOfStates,
                       Set<? extends IC> ia,
                       Function1<? super I, ? extends IC> gic) {
        this(HashMap.empty(), ia,
                Array.fill(numberOfStates, HashMap.empty()),
                Array.fill(numberOfStates, HashSet.empty()), gic, true);
    }

    /**
     * Construct an <b>NFAutomaton</b> with accepting states and transitions.
     *
     * @param as The accepting sates map.
     * @param ia The input alphabet.
     * @param tt The transition table.
     * @param ets The epsilon transition table.
     * @param gic The translation function.
     */
    public NFAutomaton(Map<? extends Integer, ? extends O> as, Set<? extends IC> ia,
                       Array<? extends Map<? extends IC, ? extends Set<? extends Integer>>> tt,
                       Array<? extends Set<? extends Integer>> ets,
                       Function1<? super I, ? extends IC> gic) {
        this(as, ia, tt, ets, gic, true);
    }

    /**
     * Construct an <b>NFAutomaton</b> with accepting states and transitions.
     * Additionally, specify whether these attributes should be validated.
     *
     * @param as The accepting states map.
     * @param ia The input alphabet.
     * @param tt The transition table.
     * @param ets The epsilon transition table.
     * @param gic The translation function.
     * @param withCheck Whether or not this constructor's parameters should be validated.
     *                  For all public constructors this flag is true.
     */
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
    public int getNumberOfStates() {
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

    /**
     * Get this automaton's non epsilon transitions.
     *
     * @return The transition table.
     */
    public Array<Map<IC, Set<Integer>>> getTransitionTable() {
        return transitionTable;
    }

    /**
     * Get this automaton's epsilon transitions.
     *
     * @return The epsilon transition table.
     */
    public Array<Set<Integer>> getEpsilonTransitions() {
        return epsilonTransitions;
    }

    /**
     * Determine whether a state has at least one outgoing transition for a given raw input.
     *
     * @param from The state.
     * @param input The raw input.
     * @return Whether or not there is a transition.
     */
    public boolean hasTransitions(int from, I input) {
        IC inputClass = getInputClass(input);
        validateState(from);
        return transitionTable.get(from).containsKey(inputClass);
    }

    /**
     * Determine whether a state has at least one outgoing transition for a given translated input.
     *
     * @param from The state.
     * @param inputClass The translated input.
     * @return Whether or not there is a transition.
     */
    public boolean hasTransitionsFromClass(int from, IC inputClass) {
        validateInputClass(inputClass);
        validateState(from);
        return transitionTable.get(from).containsKey(inputClass);
    }

    /**
     * Get the transition set from a starting state given some raw input.
     *
     * @param from The starting state.
     * @param input The raw input.
     * @return The set of outgoing transitions.
     */
    public Set<Integer> getTransitions(int from, I input) {
        return getTransitionsAsOption(from, input).get();
    }

    /**
     * Same as {@link NFAutomaton#getTransitions(int, Object)} except an <b>Option</b> is returned.
     * If this automaton contains no transitions for the given state and raw input, <b>None</b> is returned.
     *
     * @param from The starting state.
     * @param input The raw input.
     * @return An <b>Option</b> of the set of outgoing transitions.
     */
    public Option<Set<Integer>> getTransitionsAsOption(int from, I input) {
        IC inputClass = getInputClass(input);
        validateState(from);
        return transitionTable.get(from).get(inputClass);
    }

    /**
     * Get the transition set from some starting state given a translated input.
     *
     * @param from The starting state.
     * @param inputClass The translated input.
     * @return The transition state.
     */
    public Set<Integer> getTransitionsFromClass(int from, IC inputClass) {
        return getTransitionsFromClassAsOption(from, inputClass).get();
    }

    /**
     * Same as {@link NFAutomaton#getTransitionsFromClass(int, Object)} except the transition set is returned
     * as an <b>Option</b>. If there is no transition set associated with the given starting state and
     * translated input, <b>None</b> is returned.
     *
     * @param from The starting state.
     * @param inputClass The translated input.
     * @return An <b>Option</b> of the transition set.
     */
    public Option<Set<Integer>> getTransitionsFromClassAsOption(int from, IC inputClass) {
        validateInputClass(inputClass);
        validateState(from);
        return transitionTable.get(from).get(inputClass);
    }

    /**
     * Determine whether or not a given state has any epsilon transitions.
     *
     * @param from The starting state.
     * @return Whether or not the state has any outgoing epsilon transitions.
     */
    public boolean hasEpsilonTransitions(int from) {
        validateState(from);
        return !epsilonTransitions.get(from).isEmpty();
    }

    /**
     * Get a state's epsilon transition set.
     *
     * @param from The starting state.
     * @return The starting state's epsilon transitions.
     */
    public Set<Integer> getEpsilonTransitions(int from) {
        return getEpsilonTransitionsAsOption(from).get();
    }

    /**
     * Same as {@link NFAutomaton#getEpsilonTransitions(int)} except the transition set is returned
     * as an <b>Option</b>. If the given state has no epsilon transitions, <b>None</b> is returned.
     *
     * @param from The starting state.
     * @return An <b>Option</b> of the epsilon transitions.
     */
    public Option<Set<Integer>> getEpsilonTransitionsAsOption(int from) {
        validateState(from);

        return epsilonTransitions.get(from).isEmpty()
                ? Option.none()
                : Option.some(epsilonTransitions.get(from));
    }

    /**
     * Similar to {@link DFAutomaton#shift(int)}. Shifts state numbers in all fields of this <b>DFAutomaton</b>.
     * Since the new fields will no longer represent a valid automaton, the incremented fields are returned
     * in a <b>Tuple</b>.
     *
     * @param shift The amount to increment each state by.
     * @return A <b>Tuple</b> of the shifted fields.
     */
    public Tuple3<Map<Integer, O>, Array<Map<IC, Set<Integer>>>, Array<Set<Integer>>> shift(int shift) {
        return Tuple.of(
                getAcceptingStates().mapKeys(acceptingState -> acceptingState + shift),
                transitionTable.map(row -> row.mapValues(stateSet -> stateSet.map(state -> state + shift))),
                epsilonTransitions.map(stateSet -> stateSet.map(state -> state + shift))
        );
    }

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

    /**
     * Add an epsilon transition into the automaton.
     *
     * @param from The starting state for the transition.
     * @param to The ending state for the transition.
     * @return The new automaton.
     */
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

    /**
     * Same as {@link DFAutomaton#combine(DFAutomaton)}. Place two automata into a single automaton.
     *
     * @param nfa2 The automaton to combine with.
     * @return The new combined automaton.
     */
    public NFAutomaton<I, IC, O> combine(NFAutomaton<? super I, ? extends IC, ? extends O> nfa2) {
        return combine(nfa2, getGetInputClassUnchecked());    // Keep old get input class function.
    }

    /**
     * Same as {@link DFAutomaton#combine(DFAutomaton, Function1)}. Place two automata into a single automaton.
     * Additionally, give the combined automaton a new translation function.
     *
     * @param nfa2 The automaton to combine with.
     * @param newGIC The new translation function.
     * @return The combined automaton.
     */
    public NFAutomaton<I, IC, O> combine(NFAutomaton<? super I, ? extends IC, ? extends O> nfa2,
                                         Function1<? super I, ? extends IC> newGIC) {
        Objects.requireNonNull(nfa2);
        Objects.requireNonNull(newGIC);

        int shift = getNumberOfStates();

        Tuple3<Map<Integer, O>, Array<Map<IC, Set<Integer>>>, Array<Set<Integer>>>
                nfa2Shifts = NFAutomaton.<I, IC, O>narrow(nfa2).shift(shift);

        return new NFAutomaton<>(
                getAcceptingStates().merge(nfa2Shifts._1),
                getInputAlphabet().addAll(nfa2.getInputAlphabet()),
                transitionTable.appendAll(nfa2Shifts._2),
                epsilonTransitions.appendAll(nfa2Shifts._3),
                newGIC, false
        );
    }

    /**
     * Similar to {@link NFAutomaton#combine(NFAutomaton)}. Combine this automaton with another.
     * Add an epsilon transition from the given state in this automaton to the <i>Oth</i> state of the
     * given automaton.
     *
     * @param from The state in this automaton which will be connected to the other automaton.
     * @param nfa2 The other automaton.
     * @return The combine automaton with the added epsilon transition.
     */
    public NFAutomaton<I, IC, O> combineWithEpsilonConnection(int from,
                                        NFAutomaton<? super I, ? extends IC, ? extends O> nfa2) {
        return combine(nfa2).withEpsilonTransition(from, getNumberOfStates());
    }

    /**
     * Same as {@link NFAutomaton#combineWithEpsilonConnection(int, NFAutomaton)}. Combine this automaton with
     * another with a connecting epsilon transition. Specify the new automaton's translation function.
     *
     * @param from The connecting state in this automaton.
     * @param nfa2 The other automaton.
     * @param newGIC The new translation function.
     * @return The new combined automaton.
     */
    public NFAutomaton<I, IC, O> combineWithEpsilonConnection(int from,
                                        NFAutomaton<? super I, ? extends IC, ? extends O> nfa2,
                                        Function1<? super I, ? extends IC> newGIC) {
        return combine(nfa2, newGIC).withEpsilonTransition(from, getNumberOfStates());
    }

    /**
     * Combine an <b>NFAutomaton</b> with itself a given number of times. This group of automata will be
     * chained together with epsilon transitions from the given state of each automaton to the <i>0th</i> state of
     * the next.
     *
     * @param from The given connecting state.
     * @param times The number of times to repeat this automaton.
     * @return The new repeated automaton.
     */
    public NFAutomaton<I, IC, O> repeat(int from, int times) {
        validateState(from);

        if (times < 0) {
            throw new IllegalArgumentException("Cannot repeat negative times.");
        }

        if (times == 0) {
            return new NFAutomaton<>(
                    HashMap.empty(),
                    getInputAlphabet(),
                    Array.of(HashMap.empty()),
                    Array.of(HashSet.empty()),
                    getGetInputClassUnchecked(), false
            );
        }

        return repeatHelper(from, times, this);
    }

    /**
     * Static helper for {@link NFAutomaton#repeat(int, int)}. Repeat an automaton a given number of times.
     * Chain the resulting automata together with epsilon transitions from the given state number.
     *
     * @param from The connecting state.
     * @param times The number of times to repeat.
     * @param nfa The automaton to repeat.
     * @param <I> The raw input type of the given automaton.
     * @param <IC> The translated input type of the given automaton.
     * @param <O> The output type of the given automaton.
     * @return The repeated automaton.
     */
    private static <I, IC, O> NFAutomaton<I, IC, O> repeatHelper(int from, int times, NFAutomaton<I, IC, O> nfa) {
        return times == 1
                ? nfa
                : nfa.combineWithEpsilonConnection(from, repeatHelper(from, times - 1, nfa));
    }

    /**
     * Turn this <b>NFAutomaton</b> into a <b>DFAutomaton</b>.
     *
     * @return The resulting <b>DFAutomaton</b>.
     * @throws Exception When output ambiguities are held in this <b>NFAutomaton</b>.
     */
    public DFAutomaton<I, IC, O> toDFA() throws Exception {
        return FAutomatonUtil.convertNFAToDFA(this, List.empty());
    }

    /**
     * Try to turn this <b>NFAutomaton</b> into a <b>DFAutomaton</b>.
     *
     * @return A Try which may contain the resulting <b>DFAutomaton</b>.
     */
    public Try<DFAutomaton<I, IC, O>> tryToDFA() {
        return Try.of(() -> FAutomatonUtil.convertNFAToDFA(this, List.empty()));
    }

    /**
     * Turn this <b>NFAutomaton</b> into a <b>DFAutomaton</b>.
     * <br>
     * This builder allows for a strong signals set. This is how ambiguities in outputs are dealt with.
     * For example, if when converting this <b>NFAutomaton</b> a state is created with two possible outputs,
     * the output which is contained in the strong signal set will be chosen. If a state holds multiple strong
     * signaled outputs, the <b>NFAutomaton</b> is considered ambiguous, an error is thrown.
     *
     * @param strongSignals The set of strong signaled outputs.
     * @return The resulting <b>DFAutomaton</b>.
     * @throws Exception When this <b>NFAutomaton</b> has output ambiguities.
     */
    public DFAutomaton<I, IC, O> toDFA(Set<? extends O> strongSignals) throws Exception {
        return FAutomatonUtil.convertNFAToDFA(this, List.of(strongSignals));
    }

    /**
     * Try to turn this <b>NFAutomaton</b> into a <b>DFAutomaton</b>.
     * Simply runs {@link NFAutomaton#toDFA(Set)} just as a <b>Try</b>.
     *
     * @param strongSignals The strong output signal set.
     * @return A <b>Try</b> which may contain the resulting <b>DFAutomaton</b>.
     */
    public Try<DFAutomaton<I, IC, O>> tryToDFA(Set<? extends O> strongSignals) {
        return Try.of(() -> FAutomatonUtil.convertNFAToDFA(this, List.of(strongSignals)));
    }

    /**
     * Similar to {@link NFAutomaton#toDFA(Set)} except with more rigorous output ambiguity resolution.
     * <br>
     * Here a list of signal sets is given. The <i>0th</i> set is the strongest, the <i>1st</i> is the second strongest
     * and so on...
     * @param precSeq The sequence of signal sets.
     * @return The resulting <b>DFAutomaton</b>.
     * @throws Exception When the <b>NFAutomaton</b> has output ambiguities.
     */
    public DFAutomaton<I, IC, O> toDFA(Seq<? extends Set<? extends O>> precSeq) throws Exception {
        return FAutomatonUtil.convertNFAToDFA(this, precSeq);
    }

    /**
     * Same as {@link NFAutomaton#tryToDFA(Seq)} just in a <b>Try</b>.
     *
     * @param precSeq The sequence of signal sets.
     * @return A <b>Try</b> which may contain a resulting <b>DFAutomaton</b>.
     */
    public Try<DFAutomaton<I, IC, O>> tryToDFA(Seq<? extends Set<? extends O>> precSeq) {
        return Try.of(() -> FAutomatonUtil.convertNFAToDFA(this, precSeq));
    }
}
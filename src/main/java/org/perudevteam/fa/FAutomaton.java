package org.perudevteam.fa;

import io.vavr.Function1;
import io.vavr.Tuple2;
import io.vavr.collection.*;

import java.util.Objects;

/**
 * An <b>FAutomaton</b> represents some finite automaton which reads translated inputs of type <b>IC</b>,
 * and contains accepting states with outputs of type <b>O</b>.
 * The automaton can only interpret translated inputs of type <b>IC</b>, however with a translation function,
 * the automaton has the ability to take in some raw type, translate it, then interpret it.
 * <br>
 * As a simple example imagine an automaton which accepts ascii characters.
 * Character will be the automaton's raw type <b>I</b>.
 * CharType will be the automaton's translated type <b>IC</b>. CharType could be some enum containing entries
 * like <i>DIGIT</i> or <i>LETTER</i>.
 * <br>
 * An <b>FAutomaton</b> contains <i>n</i> states numbered <i>1</i> through <i>n - 1</i>.
 *
 * @param <I> The raw input type of the automaton.
 * @param <IC> The type raw inputs are translated to. This is the type the automaton can actually process.
 * @param <O> The output type.
 */
abstract class FAutomaton<I, IC, O> {

    /**
     * A map of accepting states to there outputs. A state will only have an entry in this map if
     * it is an accepting state.
     */
    private final Map<Integer, O> acceptingStates;

    /**
     * The set of translated inputs this automaton can process.
     */
    private final Set<IC> inputAlphabet;

    /**
     * A function for translating a raw input of type <b>I</b> into a processable
     * input of type <b>IC</b>.
     */
    private final Function1<I, IC> getInputClassUnchecked;

    /**
     * Constructor.
     *
     * @param as Accepting states map.
     * @param ia Input alphabet set.
     * @param gic The input translation function.
     * @param withCheck Whether or not the given parameters should be validated.
     */
    public FAutomaton(Map<? extends Integer, ? extends O> as, Set<? extends IC> ia,
                      Function1<? super I, ? extends IC> gic, boolean withCheck) {
        if (withCheck) {
            Objects.requireNonNull(as);

            for (Tuple2<? extends Integer, ? extends O> keyValue: as) {
                Objects.requireNonNull(keyValue._2);    // No null values in the map.
                Objects.requireNonNull(keyValue._1);
            }

            Objects.requireNonNull(ia);
            if (ia.isEmpty()) {
                throw new IllegalArgumentException("FA requires at least one input class.");
            }
            ia.forEach(Objects::requireNonNull);

            Objects.requireNonNull(gic);
        }

        // **NOTE** The states inside the accepting states Map are yet to be checked.
        // We don't know if they are valid or not.
        // This is up to child classes to check based on how they specifically handle state validation.
        acceptingStates = Map.narrow(as);
        inputAlphabet = Set.narrow(ia);
        getInputClassUnchecked = Function1.narrow(gic);
    }

    /**
     * The number of states in this automaton.
     *
     * @return integer number of states.
     */
    public abstract int getNumberOfStates();

    /**
     * Throw an error if the given state is not contained in this automaton.
     *
     * @param state the state.
     */
    protected void validateState(int state) {
        if (state < 0 || state >= getNumberOfStates()) {
            throw new IndexOutOfBoundsException("Bad state given " + state + ".");
        }
    }

    /**
     * Throw an error if the translated input is not in the input
     * alphabet.
     *
     * @param inputClass The translated input.
     */
    protected void validateInputClass(IC inputClass) {
        if (!inputAlphabet.contains(inputClass)) {
            throw new IllegalArgumentException("Bad input class given " + inputClass + ".");
        }
    }

    /**
     * Translate a raw input of type <b>I</b> into a processable input of type <b>IC</b>.
     *
     * @param input The raw input.
     * @return The translate input.
     */
    protected IC getInputClass(I input) {
        IC inputClass = getInputClassUnchecked.apply(input);
        validateInputClass(inputClass);
        return inputClass;
    }

    /**
     * Get the input alphabet.
     *
     * @return The input alphabet.
     */
    public Set<IC> getInputAlphabet() {
        return inputAlphabet;
    }

    /**
     * Get the accepting states map.
     *
     * @return The accepting states.
     */
    protected Map<Integer, O> getAcceptingStates() {
        return acceptingStates;
    }

    /**
     * Get the input translation function.
     *
     * @return the function.
     */
    public Function1<I, IC> getGetInputClassUnchecked() {
        return getInputClassUnchecked;
    }

    /**
     * Determine whether a given state is accepting.
     * (Throws an error if given state is not contained in this automaton).
     *
     * @param state The state.
     * @return Whether or not the state is accepting.
     */
    public boolean isAccepting(int state) {
        validateState(state);
        return acceptingStates.containsKey(state);
    }

    /**
     * Get the output associated with an accepting state.
     * (Throws an error if the given state is not accepting)
     *
     * @param state The accepting state.
     * @return The output of type <b>O</b>.
     */
    public O getOutput(int state) {
        return acceptingStates.get(state).get();
    }

    /**
     * Prepend a given number of states to the automaton. All preexisting states and transitions between states
     * will shifted over by the number of new states prepended onto the beginning of the automaton.
     *
     * @param states The number of states to prepend.
     * @return The new <b>FAutomaton</b>.
     */
    public abstract FAutomaton<I, IC, O> prependStates(int states);

    /**
     * Append a given number of states onto the end of the automaton. In this case, no preexisting states
     * are affected, thus they do not need to be shifted.
     *
     * @param states The number of states to append.
     * @return The new <b>FAutomaton</b>.
     */
    public abstract FAutomaton<I, IC, O> appendStates(int states);

    /**
     * Add a transition between two states given a certain translated input is read.
     *
     * @param from The starting state.
     * @param to The ending state.
     * @param inputClass The input needing to be read to execute the transition.
     * @return The new <b>FAutomaton</b>.
     */
    public abstract FAutomaton<I, IC, O> withSingleTransition(int from, int to, IC inputClass);

    /**
     * Add multiple transitions to the automaton.
     * Given a set of starting states, a set of ending states, and a set of translated inputs,
     * adds a transition to the Automaton for every combination of elements one from each set.
     * Think cartesian product.
     *
     * @param froms The set of starting states.
     * @param tos The set of ending states.
     * @param inputClasses The set of translated inputs.
     * @return The new <b>FAutomaton</b>.
     */
    public abstract FAutomaton<I, IC, O> withSingleTransitions(Set<? extends Integer> froms,
                                                               Set<? extends Integer> tos,
                                                               Set<? extends IC> inputClasses);

    /**
     * Add an accepting state to the automaton.
     * If the given state is already an accepting state, its old output will be
     * replaced.
     *
     * @param state The accepting state.
     * @param output The output to be associated with the accepting state.
     * @return The new <b>FAutomaton</b>.
     */
    public abstract FAutomaton<I, IC, O> withAcceptingState(int state, O output);

    /**
     * Resets the automaton's accepting states map to the given map.
     *
     * @param newOutputs A map of states to outputs representing new accepting states.
     * @param <OP> The new output type of this automaton.
     * @return The new <b>FAutomaton</b>.
     */
    public abstract <OP> FAutomaton<I, IC, OP> withAcceptingStates(Map<? extends Integer, ? extends OP> newOutputs);
}

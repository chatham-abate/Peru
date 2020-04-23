package org.perudevteam.fa;

import io.vavr.Function1;
import io.vavr.Tuple2;
import io.vavr.collection.*;

import java.util.Objects;

// Finite Automaton Class.
abstract class FAutomaton<I, IC, O> {

    // We also need accepting states here.
    // Both NFA and DFA have a finite number of accepting states.
    private final Map<Integer, O> acceptingStates;
    private final Set<IC> inputAlphabet;
    private final Function1<I, IC> getInputClassUnchecked;

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

    public abstract int getNumberOfStates();

    protected void validateState(int state) {
        if (state < 0 || state >= getNumberOfStates()) {
            throw new IndexOutOfBoundsException("Bad state given " + state + ".");
        }
    }

    protected void validateInputClass(IC inputClass) {
        if (!inputAlphabet.contains(inputClass)) {
            throw new IllegalArgumentException("Bad input class given " + inputClass + ".");
        }
    }

    protected IC getInputClass(I input) {
        IC inputClass = getInputClassUnchecked.apply(input);
        validateInputClass(inputClass);
        return inputClass;
    }

    public Set<IC> getInputAlphabet() {
        return inputAlphabet;
    }

    protected Map<Integer, O> getAcceptingStates() {
        return acceptingStates;
    }

    public Function1<I, IC> getGetInputClassUnchecked() {
        return getInputClassUnchecked;
    }

    public boolean isAccepting(int state) {
        validateState(state);
        return acceptingStates.containsKey(state);
    }

    public O getOutput(int state) {
        return acceptingStates.get(state).get();
    }

    public abstract FAutomaton<I, IC, O> prependStates(int states);

    public abstract FAutomaton<I, IC, O> appendStates(int states);

    public abstract FAutomaton<I, IC, O> withSingleTransition(int from, int to, IC inputClass);

    public abstract FAutomaton<I, IC, O> withSingleTransitions(Set<? extends Integer> froms,
                                                               Set<? extends Integer> tos,
                                                               Set<? extends IC> inputClasses);

    public abstract FAutomaton<I, IC, O> withAcceptingState(int state, O output);

    public abstract <OP> FAutomaton<I, IC, OP> withAcceptingStates(Map<? extends Integer, ? extends OP> newOutputs);
}

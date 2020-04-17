package org.perudevteam.fa;

import io.vavr.Tuple2;
import io.vavr.collection.*;
import io.vavr.control.Option;

import java.util.Objects;

// Finite Automaton Class
abstract class FA<I, IC, O> {


    // We also need accepting states here.
    // Both NFA and DFA have a finite number of accepting states.
    private final Map<Integer, O> acceptingStates;
    private final Set<IC> inputAlphabet;

    // Unchecked constructor.
    public FA(Map<? extends Integer, O> as, Set<IC> ia) {
        this(as, ia, true);
    }

    public FA(Map<? extends Integer, O> as, Set<IC> ia, boolean withCheck) {
        if (withCheck) {
            Objects.requireNonNull(as);

            if (as.isEmpty()) {
                throw new IllegalArgumentException("FA requires at least one accepting state.");
            }

            for (Tuple2<? extends Integer, O> keyValue: as) {
                Objects.requireNonNull(keyValue._2);    // No null values in the map.
                Objects.requireNonNull(keyValue._1);
                validateState(keyValue._1);     // No out of bounds states.
            }

            // Validate input class set, and create input class index.
            Objects.requireNonNull(ia);
            if (ia.isEmpty()) {
                throw new IllegalArgumentException("FA requires at least one input class.");
            }
            ia.forEach(Objects::requireNonNull);
        }

        acceptingStates = Map.narrow(as);
        inputAlphabet = ia;
    }

    protected abstract int getNumberOfStates();

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

    protected abstract IC getInputClassUnchecked(I input);

    public Set<IC> getInputAlphabet() {
        return inputAlphabet;
    }

    protected Map<Integer, O> getAcceptingStates() {
        return acceptingStates;
    }

    public boolean isAccepting(int state) {
        validateState(state);
        return acceptingStates.containsKey(state);
    }

    public O getOutput(int state) {
        return acceptingStates.get(state).get();
    }

    public abstract FA<I, IC, O> withSingleTransition(int from, int to, IC inputClass);

    public abstract FA<I, IC, O> withAcceptingState(int state, O output);
}

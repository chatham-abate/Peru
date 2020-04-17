package org.perudevteam.fa;

import io.vavr.collection.*;
import io.vavr.control.Option;

import java.util.Objects;

// Finite Automaton Class
abstract class FA<I, IC, O> {
    private final Map<IC, Integer> inputClassIndex; // Contains all input classes.

    // We also need accepting states here.
    // Both NFA and DFA have a finite number of accepting states.
    private final Map<Integer, O> acceptingStates;

    public FA(Map<? extends Integer, O> as, Set<IC> ics) {
        // Validate Accepting states.
        Objects.requireNonNull(as);
        for (Integer acceptingState: as.keySet()) {
            Objects.requireNonNull(acceptingState);
            // Only valid states can be help in the accepting states keyset.
            validateState(acceptingState);
        }
        acceptingStates = Map.narrow(as);

        // Validate input class set, and create input class index.
        Objects.requireNonNull(ics);
        ics.forEach(Objects::requireNonNull);

        int count = 0;
        Map<IC, Integer> tempIndex = HashMap.empty();
        for (IC inputClass: ics) {
            tempIndex = tempIndex.put(inputClass, count);
            count++;
        }

        inputClassIndex = tempIndex;
    }

    protected abstract int getNumberOfStates();

    protected void validateState(int state) {
        if (state < 0 || state >= getNumberOfStates()) {
            throw new IndexOutOfBoundsException("Bad state given " + state + ".");
        }
    }

    protected abstract IC getInputClassUnchecked(I input);

    protected int getInputClassIndex(I input) {
        Objects.requireNonNull(input); // Null Check.
        IC inputClass = getInputClassUnchecked(input);
        if (!inputClassIndex.containsKey(inputClass)) {
            throw new IllegalArgumentException("Bad input class returned " + inputClass + ".");
        }
        return inputClassIndex.get(inputClass).get();
    }

    public boolean isAccepting(int state) {
        validateState(state);
        return acceptingStates.containsKey(state);
    }

    public O getOutput(int state) {
        return acceptingStates.get(state).get();
    }
}

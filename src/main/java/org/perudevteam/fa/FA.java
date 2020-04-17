package org.perudevteam.fa;

import io.vavr.Tuple2;
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

        if (as.isEmpty()) {
            throw new IllegalArgumentException("FA requires at least one accepting state.");
        }

        for (Tuple2<? extends Integer, O> keyValue: as) {
            Objects.requireNonNull(keyValue._2);    // No null values in the map.
            Objects.requireNonNull(keyValue._1);
            validateState(keyValue._1);     // No out of bounds states.
        }

        acceptingStates = Map.narrow(as);

        // Validate input class set, and create input class index.
        Objects.requireNonNull(ics);

        if (ics.isEmpty()) {
            throw new IllegalArgumentException("FA requires at least one input class.");
        }

        int count = 0;
        Map<IC, Integer> tempIndex = HashMap.empty();
        for (IC inputClass: ics) {
            Objects.requireNonNull(ics);
            tempIndex = tempIndex.put(inputClass, count);
            count++;
        }

        inputClassIndex = tempIndex;
    }

    // Unchecked constructor.
    public FA(Map<IC, Integer> ici, Map<Integer, O> as) {
        inputClassIndex = ici;
        acceptingStates = as;
    }

    protected abstract int getNumberOfStates();

    protected void validateState(int state) {
        if (state < 0 || state >= getNumberOfStates()) {
            throw new IndexOutOfBoundsException("Bad state given " + state + ".");
        }
    }

    protected void validateInputClass(IC inputClass) {
        if (!inputClassIndex.containsKey(inputClass)) {
            throw new IllegalArgumentException("Bad input class given " + inputClass + ".");
        }
    }

    protected abstract IC getInputClassUnchecked(I input);

    protected int getInputClassIndex(I input) {
        Objects.requireNonNull(input); // Null Check.
        IC inputClass = getInputClassUnchecked(input);
        validateInputClass(inputClass);
        return inputClassIndex.get(inputClass).get();
    }

    protected Map<IC, Integer> getInputClassIndex() {
        return inputClassIndex;
    }

    public int getNumberOfInputClasses() {
        return inputClassIndex.keySet().length();
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

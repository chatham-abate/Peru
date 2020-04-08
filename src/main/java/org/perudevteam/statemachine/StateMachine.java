package org.perudevteam.statemachine;

import io.vavr.control.Option;

import java.util.Objects;

public interface StateMachine<O> {
    default boolean validState(int st) {
        return st >= 0; // States should always be positive.
    }

    default void validateState(int st) {
        if (!validState(st)) {
            throw new IllegalArgumentException("State is not valid.");
        }
    }

    Option<O> getOutputUnsafe(int st);

    /**
     * If state is invalid, throw error.
     * If state is valid and accepting, return the output in an option.
     * Otherwise return an empty option.
     */
    default Option<O> getOutput(int st) {
        validateState(st);
        return getOutputUnsafe(st);
    }

    boolean isAcceptingUnsafe(int st);

    default boolean isAccepting(int st) {
        validateState(st);
        return isAcceptingUnsafe(st);
    }

    StateMachine<O> withAcceptingState(int st, O output);
}

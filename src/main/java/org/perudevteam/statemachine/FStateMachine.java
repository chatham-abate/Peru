package org.perudevteam.statemachine;

public interface FStateMachine<O> extends StateMachine<O> {
    int getNumberOfStates();

    @Override
    default boolean validState(int st) {
        // A finite state machine will have a finite number of consecutive states.
        return st >= 0 && st < getNumberOfStates();
    }
}

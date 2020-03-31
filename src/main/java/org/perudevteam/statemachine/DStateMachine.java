package org.perudevteam.statemachine;

import io.vavr.control.Option;

// Deterministic State Machine.
public interface DStateMachine<I, O> extends StateMachine<O> {

    /**
     * Since this state machine is deterministic.
     * Taking an input should always go to no states or one and only one state.
     */
    Option<Integer> getNextStateUnsafe(int st, I in);

    default Option<Integer> getNextState(int st, I in) {
        validateState(st);
        return getNextStateUnsafe(st, in);
    }

    DStateMachine<I, O> withEdge(int from, int to, I in);
}
